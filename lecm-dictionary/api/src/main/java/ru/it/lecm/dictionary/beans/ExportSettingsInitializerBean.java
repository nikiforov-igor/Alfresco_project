package ru.it.lecm.dictionary.beans;

import ru.it.lecm.dictionary.ExportSettingsInitializer;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 04.06.13
 * Time: 9:42
 */
public class ExportSettingsInitializerBean extends ExportSettingsInitializer {
    public ExportSettingsInitializerBean(Map<String, List<String>> typeFields) {
        super(typeFields);
    }
}
