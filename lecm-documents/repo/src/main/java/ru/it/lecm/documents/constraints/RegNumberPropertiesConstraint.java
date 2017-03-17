package ru.it.lecm.documents.constraints;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * User: dbashmakov
 * Date: 31.07.13
 * Time: 9:05
 * @deprecated this class not used in system.
 * Use ru.it.lecm.documents.beans.DocumentService#getRegNumbersValues(org.alfresco.service.cmr.repository.NodeRef) instead
 */
@Deprecated
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
    
    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        
        params.put("regNumbersProperties", this.regNumbersProperties);
        
        return params;
    }
}
