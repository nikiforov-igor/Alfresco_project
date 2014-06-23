package ru.it.lecm.base.processors;

import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SearchQueryProcessor;

import java.io.Serializable;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 10:20
 */
public class FavouritesProcessor extends SearchQueryProcessor {

    private PreferenceService preferenceService;

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        String favourites = "org.alfresco.share.documents.favourites";
        String currentUser = authService.getCurrentUserName();
        Map<String, Serializable> preferences = preferenceService.getPreferences(currentUser, favourites);
        if (preferences != null) {
            String favouriteDocs = preferences.get(favourites) != null ? preferences.get(favourites).toString() : null;
            if (favouriteDocs != null && favouriteDocs.length() > 0) {
                String[] docsRefs = favouriteDocs.split(",");
                for (String docsRef : docsRefs) {
                    if (NodeRef.isNodeRef(docsRef)) {
                        sbQuery.append("ID:").append(docsRef.replace(":", "\\:")).append(" OR ");
                    }
                }

            }
        }
        sbQuery.append("ID:\"NOT_REF\""); // выключать поиск, если документы не найдены
        return sbQuery.toString();
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }
}
