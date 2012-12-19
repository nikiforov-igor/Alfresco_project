package ru.it.lecm.delegation;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONObject;

/**
 * Интерфейс для поддержки делегирований.
 * @author rabdullin
 */
public interface IDelegation {

	JSONObject test(JSONObject args);

	/**
	 * получение начальной информации от модуля делегирования
	 * @return структура данных IDelegationDescriptor которая содержит в себе:
	 * <li>NodeRef папки где хранятся все параметры делегирования</li>
	 * <li>название типа данных элементов которые в ней хранятся</li>
	 */
	IDelegationDescriptor getDelegationDescriptor ();

	/**
	 * поиск или создание параметров делегирования (delegation-opts) для сотрудника (employee)
	 * @param employeeNodeRef идентификатор сотрудника
	 * @return идентификатор параметров делегирования
	 */
	NodeRef getOrCreateDelegationOpts (final NodeRef employeeNodeRef);

	/**
	 * получение параметров делегирования для пользователя системы
	 * @param personNodeRef идентификатор пользователя системы
	 * @return NodeRef идентификатор параметров делегирования или null если ничего не нашел
	 */
	NodeRef getDelegationOptsByPerson (final NodeRef personNodeRef);

	/**
	 * получение параметров делегирования для сотрудника
	 * @param employeeNodeRef идентификатор сотрудника
	 * @return NodeRef идентификатор параметров делегирования или null если ничего не нашел
	 */
	NodeRef getDelegationOptsByEmployee (final NodeRef employeeNodeRef);

	/**
	 * Получение списка уникальных бизнес ролей для сотрудника
	 * @param employeeNodeRef идентификатор сотрудника
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getUniqueBusinessRolesByEmployee (final NodeRef employeeNodeRef);

	/**
	 * Получение списка уникальных бизнес ролей для пользователя системы
	 * @param personNodeRef идентификатор пользователя
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getUniqueBusinessRolesByPerson (final NodeRef personNodeRef);

	/**
	 * Получение списка уникальных бизнес ролей для параметров делегирования сотрудника
	 * @param delegationOptsNodeRef идентификатор параметров делегирования сотрудника
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getUniqueBusinessRolesByDelegationOpts (final NodeRef delegationOptsNodeRef);
	/**
	 * получение списка доверенностей для сотрудника
	 * @param employeeNodeRef идентификатор сотрудника
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getProcuraciesByEmployee (final NodeRef employeeNodeRef);
}
