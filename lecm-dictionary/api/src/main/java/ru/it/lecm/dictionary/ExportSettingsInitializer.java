package ru.it.lecm.dictionary;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 04.06.13
 * Time: 9:41
 */
public abstract class ExportSettingsInitializer {
    protected ExportSettings exportSettings;
    private Map<String, List<String>> typeFields;

    public void setExportSettings(ExportSettings exportSettings) {
        this.exportSettings = exportSettings;
    }

    protected ExportSettingsInitializer(Map<String, List<String>> typeFields) {
        this.typeFields = typeFields;
    }

    public void init() {
        if (typeFields != null) {
            for (Map.Entry<String, List<String>> entry : typeFields.entrySet()) {
                exportSettings.addFieldsForType(entry.getKey(), entry.getValue());
            }
        }
    }
}
