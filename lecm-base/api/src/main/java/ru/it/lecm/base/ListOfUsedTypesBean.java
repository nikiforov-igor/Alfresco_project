package ru.it.lecm.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, содержащий карту, используемых в системе типов (как стандартных Alfresco, так и LECM).
 * Каждый новый модуль может добавлять новые типы.
 * User: dbashmakov
 * Date: 15.08.13
 * Time: 12:17
 */
public class ListOfUsedTypesBean {

    protected static Map<String, String> types = new HashMap<String, String>();

    public static Map<String, String> getTypes() {
        return types;
    }

    public void setTypes(Map<String, String> types) {
        if (types != null) {
            ListOfUsedTypesBean.types.putAll(types);
        }
    }
}
