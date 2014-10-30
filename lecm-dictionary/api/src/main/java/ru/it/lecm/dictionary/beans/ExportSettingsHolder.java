package ru.it.lecm.dictionary.beans;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.dictionary.ExportSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 04.06.13
 * Time: 9:09
 */
public class ExportSettingsHolder implements ExportSettings {
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    private Map<String, List<String>> typeFields = new HashMap<String, List<String>>();

    @Override
    public List<String> getFieldsForType(String typeName) {
        List<String> fields = typeFields.get(typeName);
        if (fields == null) {
            fields = new ArrayList<>();
        }
        if (fields.isEmpty()) {

            Map<QName, PropertyDefinition> allProperties = dictionaryService.getPropertyDefs(QName.createQName(typeName, namespaceService));

            for (QName property : allProperties.keySet()) {
                String string = property.toPrefixString();
                if (!string.equals("cm:content")) {
                    fields.add(string);
                }
            }

            if (!fields.contains("cm:name")) {
                fields.add("cm:name");
            }
            if (!fields.contains("cm:title")) {
                fields.add("cm:title");
            }

            typeFields.put(typeName, fields);
        }
        return fields;
    }

    @Override
    public void addFieldsForType(String typeName, List<String> fields) {
        List<String> existingFields = typeFields.get(typeName);
        if (existingFields == null) {
            existingFields = new ArrayList<String>();
        }
        for (String field : fields) {
            if (!existingFields.contains(field)) {
                existingFields.add(field);
            }
        }
        typeFields.put(typeName, existingFields);
    }
}
