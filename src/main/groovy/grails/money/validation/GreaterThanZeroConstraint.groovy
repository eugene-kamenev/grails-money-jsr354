package grails.money.validation

import grails.validation.AbstractConstraint
import groovy.transform.CompileStatic
import org.javamoney.moneta.Money
import org.springframework.validation.Errors

import javax.money.MonetaryAmount

@CompileStatic
class GreaterThanZeroConstraint extends AbstractConstraint {

    private static final String DEFAULT_INVALID_MESSAGE_CODE = 'default.gtZero.invalid'
    static final String CONSTRAINT_NAME = 'gtZero'

    private boolean gtZero

    void setParameter(Object constraintParameter) {
        if (!(constraintParameter instanceof Boolean)) {
            throw new IllegalArgumentException("Parameter for constraint [${CONSTRAINT_NAME}]"
                + " of property [${constraintPropertyName}]"
                + " of class [${constraintOwningClass}] must be a boolean value")
        }
        gtZero = ((Boolean) constraintParameter).booleanValue()
        super.setParameter(constraintParameter)
    }

    protected void processValidate(Object target, Object propertyValue, Errors errors) {
        if (!validate(propertyValue)) {
            Object[] args = [constraintPropertyName, constraintOwningClass, propertyValue]
            rejectValue(target, errors, DEFAULT_INVALID_MESSAGE_CODE, "not.${CONSTRAINT_NAME}", args)
        }
    }

    boolean supports(Class type) {
        type != null && MonetaryAmount.isAssignableFrom(type)
    }

    String getName() {
        CONSTRAINT_NAME
    }

    boolean validate(value) {
        value instanceof MonetaryAmount && ((MonetaryAmount) value).isGreaterThan(Money.zero(value.currency))
    }
}
