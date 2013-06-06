package ru.it.lecm.dictionary;

import java.util.List;

/**
 * User: AZinovin
 * Date: 04.06.13
 * Time: 9:05
 */
public interface ExportSettings {
    List<String> getFieldsForType(String typeName);
    void addFieldsForType(String typeName, List<String> fields);
}
