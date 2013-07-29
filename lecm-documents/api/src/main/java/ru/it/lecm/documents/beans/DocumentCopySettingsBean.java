package ru.it.lecm.documents.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 29.07.13
 * Time: 16:09
 */
public class DocumentCopySettingsBean {

    private static Map<String, DocumentCopySettings> settings = new HashMap<String, DocumentCopySettings>();

    public static Map<String, DocumentCopySettings> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, DocumentCopySettings> settings) {
        if (settings != null) {
            DocumentCopySettingsBean.settings.putAll(settings);
        }
    }

    public static DocumentCopySettings getSettingsForDocType(String type){
        return getSettings().get(type);
    }
}
