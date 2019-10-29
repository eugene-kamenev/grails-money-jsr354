package grail.money.test

import org.javamoney.moneta.Money
import spock.lang.Specification

import javax.money.Monetary
import java.math.RoundingMode

class MoneySpec extends Specification {

    void "test money basic arithmetic"() {
    given:
        def eur12 = Money.of(12, 'EUR')
        def eur20 = Money.of(20, 'EUR')
        def eur32 = Money.of(32.0G, 'EUR')

    expect:
        eur12 + eur20 == eur32
        eur32 - eur20 == eur12
        eur12 - eur32 == -eur20
        -eur20 - eur12 == -eur32
        eur20 + 12 == eur32
        eur32 - 12.0G == eur20
        eur12.add(eur20) == eur32
        eur32.subtract(eur20) == eur12
        eur12.subtract(eur32) == -eur20
        (-eur20).subtract(eur12) == -eur32
        eur20.add(12) == eur32
        eur32.subtract(12.0G) == eur20
    }

    void "test money compare"() {
        def eur12 = Money.of(12.0G, 'EUR')

    expect:
        eur12 != Money.of(12, 'USD')
        eur12 != Money.of(20, 'EUR')
        eur12 > Money.of(5, 'EUR')
        eur12 <= Money.of(12, 'EUR')
    }

    void "test currency mistmatch"() {
    given:
        def eur12 = Money.of(12, 'EUR')
        def usd20 = Money.of(20, 'USD')

    when:
        eur12 + usd20
    then:
        thrown(javax.money.MonetaryException)
    when:
        usd20 - eur12
    then:
        thrown(javax.money.MonetaryException)
    when:
        eur12.add(usd20)
    then:
        thrown(javax.money.MonetaryException)
    when:
        usd20.subtract(eur12)
    then:
        thrown(javax.money.MonetaryException)
    }


    void "test operations with values"() {
    given:
        def eur20 = Money.of(20, 'EUR')
        def eur40 = Money.of(40.0G, 'EUR')

    expect:
        eur40 == eur20 * 2
        eur20 == eur40 / 2.0G
    }

    void "test number extension"() {
    given:
        def eur20 = Money.of(20, 'EUR')

    expect:
        20 + eur20 == Money.of(40, 'EUR')
        40 - eur20 == Money.of(20, 'EUR')
        2 * eur20 == Money.of(40, 'EUR')
        eur20 == 20.toMoney(Monetary.getCurrency('EUR'))
    }

    void "test rounding moneys"() {
    given:
        def eur20 = Money.of(20.1G, 'EUR')

    expect:
        eur20.setScale(0, RoundingMode.CEILING) == Money.of(21, 'EUR')
        eur20.setScale(0, RoundingMode.HALF_DOWN) == Money.of(20, 'EUR')
    }

    def "test fromMinorUnits"() {
    given:
        def eur220cents = 220.fromMinorUnits('EUR')
        def eur200cents = 200.fromMinorUnits('EUR')
    expect:
        eur220cents == Money.of(2.20, 'EUR')
        eur200cents == Money.of(2, 'EUR')
    }

    def "test majorUnits"() {
    given:
        def eurCents = Money.of(2.35, 'EUR')
        def bdhCents = Money.of(-1.845, 'BHD')
        def noCents = Money.of(4, 'EUR')
    expect:
        eurCents.majorUnits() == 200
        bdhCents.majorUnits() == -1000
        noCents.majorUnits() == 400
    }

    def "test minorUnits"() {
    given:
        def eurCents = Money.of(2.35, 'EUR')
        def bdhCents = Money.of(-1.345, 'BHD')
        def noCents = Money.of(4, 'EUR')
    expect:
        eurCents.minorUnits() == 235
        bdhCents.minorUnits() == -1345
        noCents.minorUnits() == 400
    }

    def "test majorPart"() {
    given:
        def eurCents = Money.of(2.35, 'EUR')
        def bdhCents = Money.of(-1.345, 'BHD')
        def noCents = Money.of(4, 'EUR')
    expect:
        eurCents.majorPart() == 2
        bdhCents.majorPart() == -1
        noCents.majorPart() == 4
    }

    def "test minorPart"() {
    given:
        def eurCents = Money.of(2.35, 'EUR')
        def bdhCents = Money.of(-1.345, 'BHD')
        def noCents = Money.of(4, 'EUR')
    expect:
        eurCents.minorPart() == 35
        bdhCents.minorPart() == -345
        noCents.minorPart() == 0
    }
}
