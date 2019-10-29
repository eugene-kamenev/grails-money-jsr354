package grails.money

import grails.databinding.BindingHelper
import grails.databinding.DataBindingSource
import groovy.transform.CompileStatic
import org.javamoney.moneta.Money

import javax.money.Monetary

@CompileStatic
class MoneyDataBindingHelper implements BindingHelper<Money> {
    @Override
    Money getPropertyValue(Object obj, String propertyName, DataBindingSource source) {
        Object value = source.getPropertyValue(propertyName)
        if (value && value instanceof String) {
            String[] parts = value.split(/\s+/)
            if (parts.length == 2) {
                return Money.of(new BigDecimal(parts[0]), Monetary.getCurrency(parts[1]))
            }
        }
        return null
    }
}