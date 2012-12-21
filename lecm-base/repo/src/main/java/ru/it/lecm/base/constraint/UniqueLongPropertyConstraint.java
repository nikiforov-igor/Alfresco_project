package ru.it.lecm.base.constraint;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 20.12.12
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class UniqueLongPropertyConstraint extends AbstractConstraint {
    private static ServiceRegistry serviceRegistry;

    private String typeName;
    private String propertyName;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        UniqueLongPropertyConstraint.serviceRegistry = serviceRegistry;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        if (typeName == null) {
            throw new DictionaryException("typeName is null");
        }

        this.typeName = typeName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        if (propertyName == null) {
            throw new DictionaryException("propertyName is null");
        }

        this.propertyName = propertyName;
    }

    @Override
    protected void evaluateSingleValue(Object value) {
        // ensure that the value can be converted to a String
        Long checkValue = null;
        try {
            checkValue = DefaultTypeConverter.INSTANCE.convert(Long.class, value);
        } catch (TypeConversionException e) {
            throw new ConstraintException("Wrong value type!", value);
        }

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        searchParameters.setQuery("TYPE:\"" + typeName + "\" AND @" + propertyName.replace(":", "\\:") + ":\"" + checkValue + "\"");

        ResultSet results = null;
        try {
            results = serviceRegistry.getSearchService().query(searchParameters);
            if (results.length() > 0) {
                throw new ConstraintException("The value is not unique!", value);
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<String, Object>(2);

        params.put("typeName", this.typeName);
        params.put("propertyName", this.propertyName);

        return params;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append("UniqueLongPropertyConstraint")
                .append("[ typeName=").append(typeName)
                .append(", propertyName=").append(propertyName)
                .append("]");
        return sb.toString();
    }

}

