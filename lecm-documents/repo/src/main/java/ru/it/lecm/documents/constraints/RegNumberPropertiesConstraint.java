package ru.it.lecm.documents.constraints;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * User: dbashmakov
 * Date: 31.07.13
 * Time: 9:05
 */
public class RegNumberPropertiesConstraint extends AbstractConstraint {

    private String regNumbersProperties;

    @Override
    public String getType() {
        return "LECM_REG_NUMBERS_PROPERTY";
    }

    @Override
    protected void evaluateSingleValue(Object o) {
    }

    public void setRegNumbersProperties(String regNumbersProperties) {
        this.regNumbersProperties = regNumbersProperties;
    }

    public  String[] getRegNumbersProps() {
        if (getRegNumbersProperties() != null && !getRegNumbersProperties().isEmpty()) {
            return getRegNumbersProperties().split(",");
        }
        return null;
    }

    public String getRegNumbersProperties() {
        return regNumbersProperties;
    }
}
