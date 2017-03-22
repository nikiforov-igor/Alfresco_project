package ru.it.lecm.documents.constraints;

import java.util.HashMap;
import java.util.Map;

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
    
    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        
        params.put("authorProperty", this.authorProperty);
        
        return params;
    }
}
