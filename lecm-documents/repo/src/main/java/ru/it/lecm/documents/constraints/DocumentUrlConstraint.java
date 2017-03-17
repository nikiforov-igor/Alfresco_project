package ru.it.lecm.documents.constraints;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * User: PMelnikov
 * Date: 18.05.15
 * Time: 11:45
 */
public class DocumentUrlConstraint extends AbstractConstraint {

    private String createUrl = null;
    private String viewUrl = null;
    private String editUrl = null;

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

    public String getEditUrl() {
        return editUrl;
    }

    public void setEditUrl(String editUrl) {
        this.editUrl = editUrl;
    }

    @Override
    public String getType() {
        return "LECM_DOCUMENT_URL";
    }

    @Override
    protected void evaluateSingleValue(Object value) {
    }
    
    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        
        params.put("viewUrl", this.viewUrl);
        params.put("createUrl", this.createUrl);
        
        return params;
    }
}
