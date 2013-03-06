package ru.it.lecm.documents.constraints;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * User: AIvkin
 * Date: 05.03.13
 * Time: 9:29
 */
public class PresentStringConstraint extends AbstractConstraint {

    private String presentString;

    public String getPresentString() {
        return presentString;
    }

    public void setPresentString(String presentString) {
        this.presentString = presentString;
    }

    @Override
    public String getType() {
        return "LECM_PRESENT_STRING";
    }

    @Override
    protected void evaluateSingleValue(Object value) {
    }
}
