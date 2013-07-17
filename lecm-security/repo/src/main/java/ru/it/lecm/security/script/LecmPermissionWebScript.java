package ru.it.lecm.security.script;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import ru.it.lecm.security.LecmPermissionService;

/**
 * User: dbashmakov
 * Date: 28.03.13
 * Time: 16:37
 */
public class LecmPermissionWebScript extends BaseScopableProcessorExtension {

    private LecmPermissionService lecmPermissionService;
    private AuthenticationService authService;

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public boolean hasPermission(ScriptNode node, String permission) {
        return lecmPermissionService.hasPermission(permission, node.getNodeRef(), authService.getCurrentUserName());
    }

    public boolean hasPermission(String nodeRef, String permission) {
        return lecmPermissionService.hasPermission(permission, new NodeRef(nodeRef), authService.getCurrentUserName());
    }

    public void grantDynamicRole(String docRef, String employeeRef, String roleCode) {
        lecmPermissionService.grantDynamicRole(roleCode, new NodeRef(docRef), new NodeRef(employeeRef).getId(), LecmPermissionService.LecmPermissionGroup.PGROLE_Reader);
    }

	public void revokeDynamicRole(String docRef, String employeeRef, String roleCode) {
        lecmPermissionService.revokeDynamicRole(roleCode, new NodeRef(docRef), new NodeRef(employeeRef).getId());
    }

	public boolean isAdmin(String login) {
		return lecmPermissionService.isAdmin(login);
	}
}
