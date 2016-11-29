package ru.it.lecm.delegation;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
//	QName PROP_PROCURACY_CAN_TRANSFER_RIGHTS = QName.createQName (DELEGATION_NAMESPACE, "procuracy-can-transfer-rights");

	/**
	 * &lt;property name="lecm-d8n:delegation-opts-can-transfer-rights"&gt;
	 */
//	QName PROP_DELEGATION_OPTS_CAN_TRANSFER_RIGHTS = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-can-transfer-rights");

	/**
	 * &lt;property name="lecm-d8n:delegation-opts-can-delegate-all"&gt;
	 */
//	QName PROP_DELEGATION_OPTS_CAN_DELEGATE_ALL = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-can-delegate-all");

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
	 * &lt;property name="lecm-d8n:is-owner-employee-exists"&gt;
	 */
	QName PROP_IS_OWNER_EMPLOYEE_EXISTS = QName.createQName(DELEGATION_NAMESPACE, "is-owner-employee-exists");

	/**
	 * &lt;child-association name="lecm-d8n:delegation-opts-procuracy-assoc"&gt;
	 */
	QName ASSOC_DELEGATION_OPTS_PROCURACY = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-procuracy-assoc");

	/*
	 * <type name="lecm-d8n:task-delegation">
	 */
	QName TYPE_TASK_DELEGATION = QName.createQName(DELEGATION_NAMESPACE, "task-delegation");
	/*
	 * <property name="lecm-d8n:task-delegation-task-id">
	 */
	QName PROP_TASK_DELEGATION_TASK_ID = QName.createQName(DELEGATION_NAMESPACE, "task-delegation-task-id");
	/*
	 * <association name="lecm-d8n:task-delegation-assumed-executor-assoc">
	 */
	QName ASSOC_TASK_DELEGATION_ASSUMED_EXECUTOR = QName.createQName(DELEGATION_NAMESPACE, "task-delegation-assumed-executor-assoc");
	/*
	 * <association name="lecm-d8n:task-delegation-effective-executor-assoc">
	 */
	QName ASSOC_TASK_DELEGATION_EFFECTIVE_EXECUTOR = QName.createQName(DELEGATION_NAMESPACE, "task-delegation-effective-executor-assoc");
	/**
	 * Бизнес-роль "Другие назначения"
	 */
	String BUSINESS_ROLE_OTHER_DESIGNATIONS = "BR_OTHER_DESIGNATIONS";

	QName TYPE_DELEGATION_GLOBAL_SETTINGS = QName.createQName (DELEGATION_NAMESPACE, "global-settings");
	QName PROP_CREATE_DOCUMENT_DELEGATION_SETTING = QName.createQName(DELEGATION_NAMESPACE, "create-document-delegation-setting");
	String DELEGATION_SETTINGS_NODE_NAME = "Settings";


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
	//NodeRef getOrCreateDelegationOpts (final NodeRef employeeNodeRef);

	/**
	 * получение параметров делегирования для объекта системы
	 * В качестве объекта системы можно передать cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @param nodeRef объект системы, можно передать следующие типы объектов cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @return NodeRef идентификатор параметров делегирования или null если ничего не нашел.
	 */
	NodeRef getDelegationOpts (NodeRef nodeRef);

        NodeRef createDelegationOpts (NodeRef nodeRef);

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
	 * @param delegationOptsNodeRef идентификатор параметров делегирования которые мы сохраняем
	 * @param options JSON-объект с параметрами которые подлежат сохранению. Эти параметры приходят с формы
	 */
	String saveDelegationOpts (final NodeRef delegationOptsNodeRef, final JSONObject options);

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
	 * Получить из опции делегирования карту вида "NodeRef_на_бизнес-роль -
	 * NodeRef_на_делегата"
	 *
	 * @param delegationOpts NodeRef на опции делегирования
	 * @param activeOnly только активные делегирования
	 * @return "NodeRef_на_бизнес-роль - NodeRef_на_делегата"
	 */
	Map<NodeRef, NodeRef> getBusinessRoleToTrusteeByDelegationOpts(final NodeRef delegationOpts, final boolean activeOnly);

	/**
	 * Получить делегата (lecm-orgstr:employee) по экземляру доверенности
	 * (lecm-d8n:procuracy)
	 *
	 * @param procuracy NodeRef на доверенность
	 * @return NodeRef на сотрудника, которому делегированы полномочия
	 */
	NodeRef getTrusteeByProcuracy(final NodeRef procuracy);

	/**
	 * Получить делегированную бизнес-роль (lecm-orgstr:business-role) по
	 * экземляру доверенности (lecm-d8n:procuracy)
	 *
	 * @param procuracy NodeRef на доверенность
	 * @return NodeRef на делегированную бизнес-роль
	 */
	NodeRef getBusinessRoleByProcuracy(final NodeRef procuracy);

	/**
	 * Получить "реального исполнителя" бизнес-роли "Другие назначения" с учетом
	 * возможного делегирования.
	 *
	 * @param assumedExecutor "предполагаемый исполнитель"
	 * (lecm-orgstr:employee)
	 * @return "реальный исполнитель" (lecm-orgstr:employee); сотрудник,
	 * которому делегирована бизнес-роль "Другие назначения". Если делегирование
	 * отсутствует, то возвращается ссылка на "предполагаемого исполнителя"
	 */
	NodeRef getEffectiveExecutor(final NodeRef assumedExecutor);

	/**
	 * Получить "реального исполнителя" заданной бизнес-роли с учетом возможного
	 * делегирования.
	 *
	 * @param assumedExecutor "предполагаемый исполнитель"
	 * (lecm-orgstr:employee)
	 * @param businessRole идентификатор бизнес-роли, делегирование которой
	 * необходимо проверить
	 * @return "реальный исполнитель" (lecm-orgstr:employee); сотрудник,
	 * которому делегирована указанная бизнес-роль. Если делегирование
	 * отсутствует, то возвращается ссылка на "предполагаемого исполнителя"
	 */
	NodeRef getEffectiveExecutor(final NodeRef assumedExecutor, final String businessRole);

	/**
	 * (Пере)назначить задачу "реальному исполнителю" указанной бизнес-роли.
	 *
	 * @param assumedExecutor "предполагаемый исполнитель"
	 * (lecm-orgstr:employee)
	 * @param businessRole идентификатор бизнес-роли, на которую назначается
	 * задача.
	 * @param taskID ID задачи
	 * @return NodeRef сотрудника, на которого назначена задача. null при
	 * неудаче
	 */
	NodeRef assignTaskToEffectiveExecutor(final NodeRef assumedExecutor, final String businessRole, final String taskID);

	/**
	 * @return NodeRef на папку, в которой хранятся объекты типа
	 * lecm-d8n:task-delegation, описывающие, какие задачи кому были
	 * делегированы.
	 */
	NodeRef getTasksDelegationFolder();

	/**
	 * Получить список задач, которые должны быть назначены на указанного
	 * сотрудника, но были делегированы.
	 *
	 * @param assumedExecutor "предполагаемы исполнитель", сотрудник, на
	 * которого должна быть назначена задача
	 * @param activeOnly только активные задачи (находящиеся в статусах "Not Yet
	 * Started", "In Progress" или "On Hold")
	 * @return Список NodeRef'ов на объекты типа lecm-d8n:task-delegation
	 */
	List<NodeRef> getDelegatedTasksForAssumedExecutor(final NodeRef assumedExecutor, final boolean activeOnly);

	/**
	 * Переназначить указанную задачу обратно на "предполагаемого исполнителя"
	 *
	 * @param delegatedTask
	 * @return NodeRef сотрудника, на которого назначена задача. null при
	 * неудаче
	 */
	NodeRef reassignTaskBackToAssumedExecutor(final NodeRef delegatedTask);

	/**
	 * Получение глобальных настроек
	 * @return ссылка на объект настроек
	 */
	NodeRef getGlobalSettingsNode();

	/**
	 * Получение настройки "давать делегирующим права на документы, созданные делегатом на основе делегирования"
	 * @return если true, то нужно давать делегирующим права на документы, созданные делегатом на основе делегирования
	 */
	boolean getCreateDocumentDelegationSetting();

	/**
	 * Получение доверителей для сотрудника по бизнес роли (Сотрудников, которые делегировали указанную бизнес роль)
	 * @param employee сотрудник
	 * @param roles список ролей
	 * @return список сотрудников
	 */
	Set<NodeRef> getDeletionOwnerEmployees(NodeRef employee, Set<String> roles);

	/**
	 * получение актуального исполнителя по задаче, с учетом делегирования полномочий
	 * @param employeeRef предполагаемый сотрудник-исполнитель задачи
	 * @param workflowDynRole идентификатор динамической бизнес роли через которую осуществляется делегирование
	 * @return актуальный сотрудник или тотже самый сотрудник
	 */
	NodeRef getEffectiveEmployee(final NodeRef employeeRef, final String workflowDynRole);
}
