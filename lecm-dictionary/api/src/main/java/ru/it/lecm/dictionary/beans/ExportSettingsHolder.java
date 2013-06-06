package ru.it.lecm.dictionary.beans;

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
    private Map<String, List<String>> typeFields = new HashMap<String, List<String>>();

    @Override
    public List<String> getFieldsForType(String typeName) {
        List<String> fields = typeFields.get(typeName);
        if (fields == null) {
            fields = new ArrayList<String>();
        }
        if (fields.isEmpty()) {
            fields.add("cm:name");
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
