package ru.it.lecm.security.events;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Интерфейс сервиса для нарезки прав на папки и файлы. 
 * @author rabdullin
 */
public interface INodeACLBuilder {

	/**
	 * Предоставить Динамическую Роль на документ/папку указанному пользователю.
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param userId id Сотрудника
	 */
	void grantDynamicRole( String roleCode, NodeRef nodeRef, String userId);

	/**
	 * Отобрать у Сотрудника динамическую роль в документе/папке
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param userId id Сотрудника
	 */
	void revokeDynamicRole( String roleCode, NodeRef nodeRef, String userId);

	/**
	 * Перестроить ACL-список Динамических прав для указанного документа/папки
	 * согласно указанному статусу
	 * @param nodeRef документа или папки
	 * @param statusId
	 */
	void rebuildACL(NodeRef nodeRef, String statusId);
}
