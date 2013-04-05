package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

import java.util.Set;

/**
 * User: mshafeev
 * Date: 04.04.13
 * Time: 16:05
 */
public class DocumentCommentsServiceImpl extends BaseBean{

    public boolean isPermissionChangeComment (NodeRef nodeRef) {
        Set<String> admin = authService.getDefaultAdministratorUserNames();
        String person = authService.getCurrentUserName();
        if (admin.contains(person)){
            return true;
        } else {
            return isAuthorNode(nodeRef);
        }
    }
}
