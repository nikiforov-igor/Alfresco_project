package ru.it.lecm.security.script;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.security.LecmPermissionService;

import java.util.List;

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

	public boolean hasReadAccess(ScriptNode node) {
        return lecmPermissionService.hasReadAccess(node.getNodeRef());
    }

	public boolean hasPermission(String nodeRef, String permission, String userLogin) {
        return lecmPermissionService.hasPermission(permission, new NodeRef(nodeRef), userLogin);
    }

    public void grantDynamicRole(String docRef, String employeeRef, String roleCode) {
        lecmPermissionService.grantDynamicRole(roleCode, new NodeRef(docRef), new NodeRef(employeeRef).getId(), LecmPermissionService.LecmPermissionGroup.PGROLE_Reader);
    }

	public void revokeDynamicRole(String docRef, String employeeRef, String roleCode) {
        lecmPermissionService.revokeDynamicRole(roleCode, new NodeRef(docRef), new NodeRef(employeeRef).getId());
    }

	public void revokeDynamicRole(ScriptNode document, ScriptNode employee, String roleCode) {
        lecmPermissionService.revokeDynamicRole(roleCode, document.getNodeRef(), employee.getNodeRef().getId());
    }

    public void grantDynamicRole(ScriptNode document, ScriptNode employee, String roleCode, String permission) {
        lecmPermissionService.grantDynamicRole(roleCode, document.getNodeRef(), employee.getNodeRef().getId(), lecmPermissionService.findPermissionGroup(permission));
    }

	public boolean isAdmin(String login) {
		return lecmPermissionService.isAdmin(login);
	}

    /**
     * Проверка наличия прав чтения у документа для сотрудника
     * @param document документ
     * @param employee сотрудник
     * @param roleName имя роли
     * @return
     */
    public boolean hasEmployeeDynamicRole(ScriptNode document, ScriptNode employee, String roleName) {
        return lecmPermissionService.hasEmployeeDynamicRole(document.getNodeRef(), employee.getNodeRef(), roleName);
    }

    /**
     * Список существующих прав для пользователя по отношению к документу
     * @param document документ
     * @param employee сотрудник
     * @return
     */
    public Scriptable getEmployeeRoles(ScriptNode document, ScriptNode employee) {
        List<String> results = lecmPermissionService.getEmployeeRoles(document.getNodeRef(), employee.getNodeRef());
        return Context.getCurrentContext().newArray(getScope(), results.toArray());
    }

}
