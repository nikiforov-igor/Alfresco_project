package ru.it.lecm.documents.resolvers;

import org.alfresco.web.resolver.doclib.DoclistActionGroupResolver;
import org.json.simple.JSONObject;

/**
 * User: dbashmakov
 * Date: 19.01.2017
 * Time: 9:26
 */
public class ActionsGroupResolver implements DoclistActionGroupResolver {

    @Override
    public String resolve(JSONObject jsonObject, String view) {
        JSONObject node = (JSONObject)jsonObject.get("node");
        boolean isContainer = (Boolean) node.get("isContainer");
        String actionGroupId;
        if(isContainer) {
            actionGroupId = "folder-";
        } else {
            actionGroupId = "document-";
        }

        boolean isLink = (Boolean) node.get("isLink");
        if(isLink) {
            actionGroupId = actionGroupId + "link-";
        }

        if (view == null || view.isEmpty()) {
            view = "browse";
        }

        actionGroupId = actionGroupId + view;

        return actionGroupId;
    }
}
