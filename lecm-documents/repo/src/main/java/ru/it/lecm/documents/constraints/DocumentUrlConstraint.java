package ru.it.lecm.documents.constraints;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * User: PMelnikov
 * Date: 18.05.15
 * Time: 11:45
 */
public class DocumentUrlConstraint extends AbstractConstraint {

    private String createUrl = null;
    private String viewUrl = null;

    public String getCreateUrl() {
        return createUrl;
    }

    public void setCreateUrl(String createUrl) {
        this.createUrl = createUrl;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    @Override
    public String getType() {
        return "LECM_DOCUMENT_URL";
    }

    @Override
    protected void evaluateSingleValue(Object value) {
    }
}
