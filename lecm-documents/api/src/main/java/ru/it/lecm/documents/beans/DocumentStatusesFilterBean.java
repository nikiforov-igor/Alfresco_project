package ru.it.lecm.documents.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 16.04.13
 * Time: 11:39
 */
public class DocumentStatusesFilterBean {

    protected static Map<String, Map> filters = new HashMap<String, Map>();

    protected static Map<String, String> defaultFilters = new HashMap<String, String>();

    public static Map<String, Map> getFilters() {
        return filters;
    }

    public static Map<String, String> getDefaultFilters() {
        return defaultFilters;
    }

    public void setDefaultFilters(Map<String, String> defaultFilters) {
        if(defaultFilters != null) {
            DocumentStatusesFilterBean.defaultFilters.putAll(defaultFilters);
        }
    }

    public void setFilters(Map<String, Map> filters) {
        if(filters != null) {
            DocumentStatusesFilterBean.filters.putAll(filters);
        }
    }

    public static Map<String, String> getFilterForType(String type){
        return getFilters().get(type);
    }

    public static String getDefaultFilter(String type){
        return getDefaultFilters().get(type);
    }
}
