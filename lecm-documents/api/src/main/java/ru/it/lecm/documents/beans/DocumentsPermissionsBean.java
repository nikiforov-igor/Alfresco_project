package ru.it.lecm.documents.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 05.06.13
 * Time: 17:15
 */
public class DocumentsPermissionsBean {
    protected static Map<String, List<String>> permissions = new HashMap<String, List<String>>();


    public static Map<String, List<String>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, List<String>> permissions) {
        if(permissions != null) {
            DocumentsPermissionsBean.permissions.putAll(permissions);
        }
    }

    public static List<String> getPermissionsByType(String type){
        return getPermissions().get(type);
    }
}
