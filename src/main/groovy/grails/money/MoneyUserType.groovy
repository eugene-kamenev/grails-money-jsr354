package grails.money

import groovy.transform.CompileStatic
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.ParameterizedType
import org.hibernate.usertype.UserType
import org.javamoney.moneta.Money

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Types

@CompileStatic
class MoneyUserType implements UserType, ParameterizedType {

    private final static String DEFAULT_CURRENCY_COLUMN = 'currency'
    private final static int[] SQL_TYPES = [Types.DOUBLE] as int[]

    Properties parameterValues

    Object assemble(Serializable cached, Object owner) {
        cached
    }

    Object deepCopy(Object o) {
        o.clone()
    }

    Serializable disassemble(Object value) {
        (Serializable) value
    }

    boolean equals(Object x, Object y) {
        x == y
    }

    int hashCode(Object o) {
        o.hashCode()
    }

    boolean isMutable() {
        false
    }
    
    Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) {
        String amountColumnName = names[0]
        Integer currencyColumnIdx = retrieveCurrencyColumnIdx(rs, amountColumnName)

        BigDecimal amount = rs.getBigDecimal(amountColumnName)
        String currency = currencyColumnIdx ? rs.getString(currencyColumnIdx) : null

        if (amount == null && currency == null)
            return null
        Money.of(amount, currency)
    }

    void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) {
        if (value == null) {
            st.setBigDecimal(index, null)
        } else {
            Money money = (Money) value
            st.setBigDecimal(index, money.numberStripped)
        }
    }

    Object replace(Object original, Object target, Object owner) {
        original
    }

    Class returnedClass() {
        Money.class
    }

    int[] sqlTypes() {
        SQL_TYPES
    }

    private Integer retrieveCurrencyColumnIdx(ResultSet rs, String amountColumnName) {
        ResultSetMetaData rsmd = rs.getMetaData()
        int columnCount = rsmd.getColumnCount()

        // The column count starts from 1
        Integer amountColumnIdx = (1..columnCount).find { i ->
            rsmd.getColumnLabel(i) == amountColumnName
        }

        if (!amountColumnIdx)
            return null

        String tableName = rsmd.getTableName(amountColumnIdx)
        String currencyColumn = parameterValues?.currencyColumn ?:
                                DEFAULT_CURRENCY_COLUMN

        Integer currencyColumnIdx = (1..columnCount).find { i ->
            currencyColumn == rsmd.getColumnName(i) &&
                (!tableName || tableName == rsmd.getTableName(i))
        }

        if (!currencyColumnIdx) {
            println "Unable to find currency column '${ currencyColumn }' in result set"
        }

        currencyColumnIdx
    }
}
