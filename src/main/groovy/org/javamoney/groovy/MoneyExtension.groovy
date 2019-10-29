package org.javamoney.groovy

import groovy.transform.CompileStatic
import org.javamoney.moneta.Money

import javax.money.CurrencyQueryBuilder
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.MonetaryAmount
import java.math.RoundingMode

@CompileStatic
final class MoneyExtension {

    static CurrencyUnit asCurrencyUnit(Currency currency) {
        return Monetary.getCurrency(currency.currencyCode)
    }

    static Currency asCurrency(CurrencyUnit currencyUnit) {
        return Currency.getInstance(currencyUnit.currencyCode)
    }

    static MonetaryAmount divide(MonetaryAmount _this, MonetaryAmount divisor) {
        return div(_this, divisor)
    }

    static MonetaryAmount div(MonetaryAmount _this, MonetaryAmount divisor) {
        return _this.divide(divisor.number.numberValueExact(BigDecimal))
    }

    static MonetaryAmount plus(Number n, MonetaryAmount money) {
        return money.add(Money.of(n, money.currency))
    }

    static MonetaryAmount plus(MonetaryAmount _this, MonetaryAmount other) {
        return _this.add(other)
    }

    static MonetaryAmount plus(MonetaryAmount _this, Number number) {
        return plus(number, _this)
    }

    static MonetaryAmount minus(Number n, MonetaryAmount money) {
        return money.negate().add(Money.of(n, money.currency))
    }

    static MonetaryAmount minus(MonetaryAmount _this, Number n) {
        return subtract(_this, n)
    }

    static MonetaryAmount minus(MonetaryAmount _this, MonetaryAmount another) {
        return _this.subtract(another)
    }

    static MonetaryAmount multiply(Number n, MonetaryAmount money) {
        return money * n
    }

    static MonetaryAmount multiply(MonetaryAmount _this, Number n) {
        return multiply(n, _this)
    }

    static Money toMoney(Number n, CurrencyUnit currency) {
        return Money.of(n, currency)
    }

    static Money fromMinorUnits(Number minorAmount, CurrencyUnit currency) {
        BigDecimal amount = ((BigDecimal)minorAmount).scaleByPowerOfTen(-currency.defaultFractionDigits)
        return Money.of(amount, currency)
    }

    static Money fromMinorUnits(Number minorAmount, String currencyCode) {
        return fromMinorUnits(minorAmount, Monetary.getCurrency(currencyCode))
    }

    static MonetaryAmount setScale(MonetaryAmount amount, int newScale, RoundingMode rounding) {
        Money.of(amount.number.numberValueExact(BigDecimal).setScale(newScale, rounding), amount.currency)
    }

    /**
     * Returns new Money with (-amount) value.
     */
    static MonetaryAmount negative(MonetaryAmount _this) {
        return _this.negate()
    }

    /**
     * Subtract a Number to this Money.
     * Currencies must match.
     */
    static MonetaryAmount subtract(MonetaryAmount _this, Number n) {
        return minus(_this, Money.of(n, _this.currency))
    }

    static MonetaryAmount add(MonetaryAmount _this, Number n) {
        return plus(n, _this)
    }

    /**
     * Divide this Money by an number divisor.
     *
     * The scale of the returned Money is equal to the scale of
     * 'this' Money.
     */
    static MonetaryAmount div(MonetaryAmount _this, Number n) {
        return _this.divide(n)
    }

    /**
     * Return true only if other Money has the same currency
     * as this Money.
     */
    static boolean isSameCurrencyAs(MonetaryAmount _this, MonetaryAmount other) {
        _this.currency == other?.currency
    }

    /**
     * Gets the amount in major units as a {@code long}.
     * <p>
     * This returns the Money amount in terms of the major units of the
     * currency, truncating the amount if necessary. For example, 'EUR 2.35'
     * will return 200, and 'BHD -1.845' will return -1000.
     * <p>
     *
     * @return the major units of the amount
     */
    static long majorUnits(MonetaryAmount _this) {
        _this.number.numberValue(BigInteger).longValue() * (10 ** (_this.currency.defaultFractionDigits))
    }

    /**
     * Gets the minor part as a {@code long}.
     * <p>
     * This returns the Money amount in terms of the minor units of the
     * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
     * will return 'EUR 235', and 'BHD -1.345' will return 'BHD -1345'.
     * <p>
     *
     * @return the minor units of the amount
     */
    static long minorUnits(MonetaryAmount _this) {
        toMinorUnits(_this.number.numberValue(BigDecimal), _this.currency)
    }

    private static long toMinorUnits(BigDecimal amount, CurrencyUnit currency) {
        amount.scaleByPowerOfTen(currency.defaultFractionDigits).longValue()
    }

    /**
     * Gets the amount major part as a {@code long}.
     * <p>
     * This returns the Money amount in terms of the major units of the currency, truncating the
     * amount if necessary. For example, 'EUR 2.35' will return 'EUR 2', and 'BHD -1.345' will
     * return 'BHD -1'.
     * <p>
     * @return the major units part of the amount
     */
    static long majorPart(MonetaryAmount _this) {
        _this.number.longValue()
    }

    /**
     * Extract the minor part as a {@code long}.
     * <p>
     * This returns the Money amount in terms of the minor units of the
     * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
     * will return 35, and 'BHD -1.345' will return -345.
     * <p>
     * @return the minor units part of the amount
     */
    static long minorPart(MonetaryAmount _this) {
        toMinorUnits(_this.number.numberValue(BigDecimal) - majorPart(_this), _this.currency)
    }
}
