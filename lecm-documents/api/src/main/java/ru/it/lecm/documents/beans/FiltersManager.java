package ru.it.lecm.documents.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * User: DBashmakov
 * Date: 12.07.13
 * Time: 11:25
 */
public class FiltersManager {
    private static Map<String, DocumentFilter> filters = new HashMap<String, DocumentFilter>();

    public static Map<String, DocumentFilter> getFilters() {
        return filters;
    }

    public static void registerFilter(DocumentFilter newFilter){
        String newFilterId = newFilter.getId();
        if (newFilterId != null && !newFilterId.isEmpty()){
            if (FiltersManager.getFilters().get(newFilterId) == null){
                FiltersManager.getFilters().put(newFilterId, newFilter);
            }
        }
    }

    public static DocumentFilter getFilterById(String filterId){
        return getFilters().get(filterId);
    }
}
