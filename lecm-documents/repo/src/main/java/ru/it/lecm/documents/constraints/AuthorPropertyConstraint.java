package ru.it.lecm.documents.constraints;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * User: DBashmakov
 * Date: 11.07.13
 * Time: 10:09
 */
public class AuthorPropertyConstraint extends AbstractConstraint {

    private String authorProperty;

    @Override
    public String getType() {
        return "LECM_AUTHOR_PROPERTY";
    }

    @Override
    protected void evaluateSingleValue(Object value) {
    }

    public String getAuthorProperty() {
        return authorProperty;
    }

    public void setAuthorProperty(String authorProperty) {
        this.authorProperty = authorProperty;
    }
}
