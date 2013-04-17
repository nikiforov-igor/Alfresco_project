package ru.it.lecm.delegation;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Интерфейс для поддержки делегирований.
 * @author rabdullin
 */
public interface IDelegation {

	/**
	 * &lt;namespace uri="http://www.it.ru/logicECM/model/delegation/1.0" prefix="lecm-d8n"/&gt;
	 */
	String DELEGATION_NAMESPACE = "http://www.it.ru/logicECM/model/delegation/1.0";

	/**
	 * &lt;type name="lecm-d8n:delegation-opts-container"&gt;
	 */
	QName TYPE_DELEGATION_OPTS_CONTAINER = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-container");

	/**
	 * &lt;type name="lecm-d8n:procuracy"&gt;
	 */
	QName TYPE_PROCURACY = QName.createQName (DELEGATION_NAMESPACE, "procuracy");

	/**
	 * &lt;type name="lecm-d8n:delegation-opts"&gt;
	 */
	QName TYPE_DELEGATION_OPTS = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts");

	/**
	 * &lt;property name="lecm-d8n:procuracy-can-transfer-rights"&gt;
	 */
	QName PROP_PROCURACY_CAN_TRANSFER_RIGHTS = QName.createQName (DELEGATION_NAMESPACE, "procuracy-can-transfer-rights");

	/**
	 * &lt;property name="lecm-d8n:delegation-opts-can-transfer-rights"&gt;
	 */
	QName PROP_DELEGATION_OPTS_CAN_TRANSFER_RIGHTS = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-can-transfer-rights");

	/**
	 * &lt;property name="lecm-d8n:delegation-opts-can-delegate-all"&gt;
	 */
	QName PROP_DELEGATION_OPTS_CAN_DELEGATE_ALL = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-can-delegate-all");

	/**
	 * &lt;child-association name="lecm-d8n:container-delegation-opts-assoc"&gt;
	 */
	QName ASSOC_DELEGATION_OPTS_CONTAINER = QName.createQName (DELEGATION_NAMESPACE, "container-delegation-opts-assoc");

	/**
	 * &lt;association name="lecm-d8n:procuracy-business-role-assoc"&gt;
	 */
	QName ASSOC_PROCURACY_BUSINESS_ROLE = QName.createQName (DELEGATION_NAMESPACE, "procuracy-business-role-assoc");

	/**
	 * &lt;association name="lecm-d8n:procuracy-trustee-assoc"&gt;
	 */
	QName ASSOC_PROCURACY_TRUSTEE = QName.createQName (DELEGATION_NAMESPACE, "procuracy-trustee-assoc");

	/**
	 * &lt;association name="lecm-d8n:delegation-opts-owner-assoc"&gt;
	 */
	QName ASSOC_DELEGATION_OPTS_OWNER = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-owner-assoc");

	/**
	 * &lt;association name="lecm-d8n:delegation-opts-trustee-assoc"&gt;
	 */
	QName ASSOC_DELEGATION_OPTS_TRUSTEE = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-trustee-assoc");

	/**
	 * &lt;child-association name="lecm-d8n:delegation-opts-procuracy-assoc"&gt;
	 */
	QName ASSOC_DELEGATION_OPTS_PROCURACY = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-procuracy-assoc");

	/**
	 * получение ссылки на папку сервиса делегирования
	 * если папка отсутствует она будет создана
	 * @return NodeRef
	 */
	NodeRef getDelegationFolder ();

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
	 * получение параметров делегирования для объекта системы
	 * В качестве объекта системы можно передать cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @param nodeRef объект системы, можно передать следующие типы объектов cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @return NodeRef идентификатор параметров делегирования или null если ничего не нашел.
	 */
	NodeRef getDelegationOpts (final NodeRef nodeRef);

	/**
	 * Получение списка уникальных бизнес ролей для сотрудника
	 * @param employeeNodeRef идентификатор сотрудника
	 * @param onlyActive только активные
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getUniqueBusinessRolesByEmployee (final NodeRef employeeNodeRef, final boolean onlyActive);

	/**
	 * Получение списка уникальных бизнес ролей для пользователя системы
	 * @param personNodeRef идентификатор пользователя
	 * @param onlyActive только активные
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getUniqueBusinessRolesByPerson (final NodeRef personNodeRef, final boolean onlyActive);

	/**
	 * Получение списка уникальных бизнес ролей для параметров делегирования сотрудника
	 * @param delegationOptsNodeRef идентификатор параметров делегирования сотрудника
	 * @param onlyActive только активные
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getUniqueBusinessRolesByDelegationOpts (final NodeRef delegationOptsNodeRef, final boolean onlyActive);

	/**
	 * получение списка доверенностей для указанного объекта системы
	 * @param nodeRef объект системы, можно передать следующие типы объектов cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @param onlyActive только активные
	 * @return список идентификаторов бизнес ролей или пустой список
	 */
	List<NodeRef> getProcuracies (final NodeRef nodeRef, final boolean onlyActive);

	/**
	 * создать список пустых доверенностей для указанных бизнес ролей
	 * @param delegationOptsNodeRef для каких параметров делегирования создаем доверенности
	 * @param businessRoleNodeRefs список бизнес ролей для которых надо создать доверенность
	 * @return список доверенностей
	 */
	List<NodeRef> createEmptyProcuracies (final NodeRef delegationOptsNodeRef, final List<NodeRef> businessRoleNodeRefs);

	/**
	 * сохранение настроенных параметров делегирования
	 * @param delegationOptsRef идентификатор параметров делегирования которые мы сохраняем
	 * @param options JSON-объект с параметрами которые подлежат сохранению. Эти параметры приходят с формы
	 */
	String saveDelegationOpts (final NodeRef delegationOptsNodeRef, final JSONObject options);

	/**
	 * Актуализировать active true/false у доверенности в зависимости от наличия ассоциации на доверенное лицо
	 * @param procuracyNodeRef
	 */
	void actualizeProcuracyActivity (final NodeRef procuracyNodeRef);

	/**
	 * "удаление" доверенностей. На самом деле отрывается ассоциация на дловеренное лицо. И автоматом, с помощью policy active меняется на false
	 * @param nodeRefs json массив нодов для удаления
	 */
	void deleteProcuracies (final JSONArray nodeRefs);

	/**
	 * активировать делегирование для указанного делегирующего лица
	 * @param delegator логин пользователя системы
	 */
	void startDelegation (final String delegator);

	/**
	 * активировать делегирование для указанного делегирующего лица
	 * @param delegator nodeRef указывающая на employee, person или delegation-opts
	 */
	void startDelegation (final NodeRef delegator);

	/**
	 * деактивировать делегирование для указанного делегирующего лица
	 * @param delegator логин пользователя системы
	 */
	void stopDelegation (final String delegator);

	/**
	 * деактивировать делегирование для указанного делегирующего лица
	 * @param delegator nodeRef указывающая на employee, person или delegation-opts
	 */
	void stopDelegation (final NodeRef delegator);

	/**
	 * по указанным параметрам находит сотрудника и проверяет является ли он подчиненным
	 * @param nodeRef параметры потенциального подчиненного (employee, person или delegation-opts)
	 * @return true/false
	 */
	boolean hasSubordinate (final NodeRef nodeRef);

	/**
	 * проверить является ли делегирование активным
	 * @param delegationOptsNodeRef параметры делегирования которые проверяются на активность
	 * @return true - делегирование активно, false в противном случае
	 */
	boolean isDelegationActive (final NodeRef delegationOptsNodeRef);

	/**
	 * проверяет что объект является параметрами делегирования
	 * @param objectNodeRef объект проверяемый на параметры делегирования
	 * @return true является, false не является
	 */
	boolean isDelegationOpts (final NodeRef objectNodeRef);

	/**
	 * проверяет что объект является доверенностью
	 * @param objectNodeRef объект проверяемый на доверенность
	 * @return true является, false не является
	 */
	boolean isProcuracy (final NodeRef objectNodeRef);

	/**
	 * получение сотрудника для объекта системы
	 * В качестве объекта системы можно передать cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @param nodeRef объект системы, можно передать следующие типы объектов cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @return NodeRef идентификатор сотрудника или null если ничего не нашел.
	 */
	NodeRef getEmployee (final NodeRef nodeRef);

	/**
	 * установка флага "передавать права руководителя" для указанной доверенности в рамках параметров делегирования
	 * для всех остальных доверенностей этот флаг будет сброшен
	 * если nodeRef-ы не существуют, или не являются доверенностью и делегированием, то ничего не делать
	 * @param procuracyRef
	 * @param delegationOptsRef
	 * @return true если удалось установить флаг, false - неудалось
	 */
	boolean transferRights(final NodeRef procuracyRef, final NodeRef delegationOptsRef);
}
