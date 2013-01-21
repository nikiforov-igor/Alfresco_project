package ru.it.lecm.security;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.model.BusinessRole;
import ru.it.lecm.model.Employee;

/**
 * Сопровождение модели Бизнес-Ролей:
 * 1) заведение соот-щих теневых групп и пользователей для БР и орг-штатных должностей и сотрудников
 * 2) нарезка прав на папки согласно БР (прописать "теневым группам" соот-щий доступ к папкам)
 * 
 * @author rabdullin
 */
public interface IBusinessRoleManager {

	/**
	 * Проверить наличие соот-щего пользователя Alfresco,
	 * при отсутствии поднимается SecurityException
	 * @param employee
	 * @exception SecurityException
	 */
	// void checkEmployee(Employee employee) throws SecurityException;
	void checkEmployee(NodeRef employee) throws SecurityException;

//	/**
//	 * Создать системного пользователя для указанного Сотрудника
//	 * @param emplRef
//	 */
//	PersonService.PersonInfo createUserForEmployee(NodeRef emplRef);

	/**
	 * Создать нужные объекты и обеспечить вложенность для указанного узла, 
	 * относящегося к орг-структуре
	 * @param nodeRef
	 */
	void ensureOrgStructureUnit(NodeRef nodeRef);


	/**
	 * 1) Гарантировать наличие теневой sec-group для указанной бизнес-роли
	 * 2) Вложить в неё sec-groups, которые соот-ют должностным позициям 
	 * 3) Сформировать ACL список в указанных для этой бизнес-роли папках
	 * @param broleRef id узла с бизнес-ролью ("lecm-ba:businessRoles")
	 */
	void ensureBusinessRole(NodeRef broleRef);

//	/**
//	 * Сформировать ACL списки в папках, указанных для этой бизнес-роли
//	 * @param role бизнес-роль
//	 */
//	void prepareACLs(BusinessRole role);

	/**
	 * Внести/убрать пользователя в sec-группу, соот-щую бизнес роли.
	 * @param employee
	 * @param groupRole
	 * @param allow true включить в бизнес роль, false исключить.
	 */
	void regroupUser(Employee employee, BusinessRole groupRole, boolean allow);


	/**
	 * Выдать пользователю на документ права, относящиеся к указанной бизнес роли
	 * @param nodeRef документ
	 * @param user пользователь
	 * @param businessRole название бизнес-роли (динамической, т.к. для 
	 * статической ничего делать не потребуется)
	 */
	void grantBusinessRole(NodeRef nodeRef, String user, String businessRole);

	/**
	 * Отобрать у пользователя права на документ, относящиеся к некоторой бизнес-роли
	 * @param nodeRef
	 * @param user
	 * @param businessRole динамическая роль, статические роли игнорируются 
	 */
	void revokeBusinessRole(NodeRef nodeRef, String user, String businessRole);

	/**
	 * Выдать права динамических ролей, согласно текщему статусу документа.
	 * При этом, поднимаются все имеющиеся в документе на данный момент динамические
	 * роли и для них выполнятся "пересчёт". 
	 * @param nodeRef документ
	 */
	void renewBusinessRoles(NodeRef nodeRef);
	
	
}
