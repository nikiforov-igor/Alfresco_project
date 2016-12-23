package ru.it.lecm.security.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 28.03.13
 * Time: 16:37
 */
public class LecmPermissionWebScript extends BaseWebScript {
    final static protected Logger logger = LoggerFactory.getLogger(LecmPermissionWebScript.class);
    private LecmPermissionService lecmPermissionService;
    private OrgstructureBean orgstructureService;
    private AuthenticationService authService;
    private AuthorityService authorityService;

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    /**
     * Определение наличия конкретной привилегии у текущего Сотрудника относительно узла (документа, папки)
     * @param permission группа ("LECM_BASIC_PG_Initiator") или атомарная привилегия (например, "_lecmPerm_ViewTag")
     * @param node проверяемый узел
     * @return true, если указанная привилегия permission имеется у пользователя
     * userName для объекта node, иначе false.
     */
    public boolean hasPermission(ScriptNode node, String permission) {
        return lecmPermissionService.hasPermission(permission, node.getNodeRef(), authService.getCurrentUserName());
    }

    public boolean hasPermission(String nodeRef, String permission) {
        return lecmPermissionService.hasPermission(permission, new NodeRef(nodeRef), authService.getCurrentUserName());
    }

    /**
     * Проверяет элемент на доступность
     * @param node элемент
     */
    public boolean hasReadAccess(ScriptNode node) {
        return lecmPermissionService.hasReadAccess(node.getNodeRef());
    }
    public boolean hasReadAccess(NodeRef node) {
        return lecmPermissionService.hasReadAccess(node);
    }

	public boolean hasPermission(String nodeRef, String permission, String userLogin) {
        return lecmPermissionService.hasPermission(permission, new NodeRef(nodeRef), userLogin);
    }

    /**
     * Предоставить Динамическую Роль на документ/папку указанному пользователю.
     * @param roleCode id Динамической Роли
     * @param docRef документа или папки
     * @param employeeRef id Сотрудника
     * @param roleCode предоставляемый доступ, если null, то будет присвоено право по-умолчанию (конфигурируется бинами)
     */
    public void grantDynamicRole(String docRef, String employeeRef, String roleCode) {
        lecmPermissionService.grantDynamicRole(roleCode, new NodeRef(docRef), new NodeRef(employeeRef).getId(), LecmPermissionService.LecmPermissionGroup.PGROLE_Reader);
    }

    /**
     * Отобрать у Сотрудника динамическую роль в документе/папке
     * @param roleCode id Динамической Роли
     * @param docRef документа или папки
     * @param employeeRef id Сотрудника
     */

    public void revokeDynamicRole(String docRef, String employeeRef, String roleCode) {
        lecmPermissionService.revokeDynamicRole(roleCode, new NodeRef(docRef), new NodeRef(employeeRef).getId());
    }
    /**
     * Отобрать у Сотрудника динамическую роль в документе/папке
     * @param roleCode id Динамической Роли
     * @param document документа
     * @param employee Сотрудник
     */

    public void revokeDynamicRole(ScriptNode document, ScriptNode employee, String roleCode) {
        lecmPermissionService.revokeDynamicRole(roleCode, document.getNodeRef(), employee.getNodeRef().getId());
    }

	public void revokeDynamicRole(ScriptNode document, String roleCode) {
		lecmPermissionService.revokeDynamicRole(roleCode, document.getNodeRef());
    }

    public void grantDynamicRole(ScriptNode document, ScriptNode employee, String roleCode, String permission) {
        lecmPermissionService.grantDynamicRole(roleCode, document.getNodeRef(), employee.getNodeRef().getId(), lecmPermissionService.findPermissionGroup(permission));
    }

    /**
     * Проверка, что пользователь является админом
     * @param login логин пользователя
     */
    public boolean isAdmin(String login) {
		return lecmPermissionService.isAdmin(login);
	}

    /**
     * Проверка наличия прав чтения у документа для сотрудника
     * @param document документ
     * @param employee сотрудник
     * @param roleName имя роли
     */
    public boolean hasEmployeeDynamicRole(ScriptNode document, ScriptNode employee, String roleName) {
        return lecmPermissionService.hasEmployeeDynamicRole(document.getNodeRef(), orgstructureService.getEmployeeLogin(employee.getNodeRef()), roleName);
    }

    /**
     * Список существующих прав для пользователя по отношению к документу
     * @param document документ
     * @param employee сотрудник
     */
    public Scriptable getEmployeeRoles(ScriptNode document, ScriptNode employee) {
        List<String> results = lecmPermissionService.getEmployeeRoles(document.getNodeRef(), employee.getNodeRef());
        return Context.getCurrentContext().newArray(getScope(), results.toArray());
    }

	/**
	 * Получение всех сотрудников с динамической ролью в документе
	 * @param document документ
	 * @param roleCode динамическая роль
	 * @return список сотрудников
	 */
	public Scriptable getEmployeesByDynamicRole(ScriptNode document, String roleCode) {
		List<NodeRef> results = lecmPermissionService.getEmployeesByDynamicRole(document.getNodeRef(), roleCode);
		return createScriptable(results);
	}
    /**
     * Запускает выполнение от имени администратора
     */
    public void setRunAsUserSystem(){
        AuthenticationUtil.setRunAsUserSystem();
    }

    /**
     * Заталкивает в стек аунтификацию пользователя
     */
    public void pushAuthentication(){
        AuthenticationUtil.pushAuthentication();
    }

    /**
     * Выталкивает аунтификацию пользователя
     */
    public void popAuthentication(){
        AuthenticationUtil.popAuthentication();
    }
}
