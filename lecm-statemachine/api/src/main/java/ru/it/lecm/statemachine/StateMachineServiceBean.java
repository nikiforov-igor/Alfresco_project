package ru.it.lecm.statemachine;

import org.activiti.engine.task.Task;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StateMachineServiceBean {

	String REDIRECT_VARIABLE = "lecm_redirect_url";
	String ROLE_WITHOUT_PRIVELEGES = "LECM_BASIC_PG_None";

	/**
	 * Возвращает список категорий для документа с флагами "редактируемый/не редактируемый"
	 *
	 * @param document
	 * @return
	 */
	boolean isReadOnlyCategory(NodeRef document, String category);

	boolean hasActiveStatemachine(NodeRef document);

	String getCurrentTaskId(String executionId);

	/**
	 * Возвращает true, если документ находится в статусе Черновик
	 *
	 * @param document
	 * @return
	 */
	boolean isDraft(NodeRef document);

	List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef, boolean isActive);

	/**
	 * Возвращает может ли текущий сотрудник создавать документ определенного типа
	 *
	 * @param type - тип документа
	 * @return
	 */
	boolean isStarter(String type);

	/*
     * Используется в
     * 		- StatemachineWebScriptBean - getDocumentsTasks
     * 		- helper - getDocumentsWithActiveTasks, getDocumentsTasks
     */
	NodeRef getTaskDocument(WorkflowTask task, List<String> documentTypes);

	/*
         * Используется в
         * 		- ActionsScript
         * 		- StatemachineWebScriptBean - getTasks
         */
	List<WorkflowTask> getDocumentTasks(NodeRef documentRef, boolean activeTasks);

	/**
	 * Возвращает можно ли создавать документ определенного типа из АРМ-а
	 *
	 * @param type - тип документа
	 * @return true - если нельзя создавать из АРМ-а
	 */
	boolean isNotArmCreate(String type);

	/**
	 * Возвращает список возможных статусов для определенного типа документа
	 *
	 * @param documentType - тип документа
	 * @return
	 */
	List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal);

	/**
	 * Получение всех динамических ролей по документу
	 * @param document документ
	 * @return список динамических ролей
	 */
	List<String> getAllDynamicRoles(NodeRef document);

	boolean isFinal(NodeRef document);

	/**
	 * Выдача сотруднику динамической роли и привелегии согласно текущему статусу документа
	 *
	 * @param document документ
	 * @param employee сотрудник
	 * @param roleName имя роли
	 * @return
	 */
	boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName);

	/**
	 * @param document - документ
	 * @return Имя предыдущего статуса
	 */
	String getPreviousStatusName(NodeRef document);

	/**
	 * @param document - документ
	 * @return Имя предыдущего статуса для статусов в ожидании
	 */
	String getPreviousStatusNameOnTake(NodeRef document);

	List<String> getPreviousStatusesNames(NodeRef document);

	String getStatemachineId(NodeRef document);

	Map<String, Object> getVariables(String executionId);

	/**
	 * Возвращает бизнес-роли которые могут создавать документ определенного типа
	 *
	 * @param documentType - тип документа
	 * @return
	 */
	Set<String> getStarterRoles(String documentType);

	void checkReadOnlyCategory(NodeRef document, String category);

	boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority);
	boolean setTaskAssignee(NodeRef documentRef, String taskId, String beforeAuthority, String afterAuthority);
	boolean setWorkflowTaskProperty(NodeRef documentRef, String workflowTaskId, QName propertyName, Serializable propertyValue);

	/*
     * Используется в
     * 		- state.fields.get.js
     */
    //TODO По возможности "выпилить" или объединить с isEditableField
	StateFields getStateFields(NodeRef document);

	/*
         * Используется в
         * 		- /lecm/statemachine/api/field/editable - editable.field.get.js
         */
    //TODO Выпилить ?? или объединить с getStateFields
	boolean isEditableField(NodeRef document, String field);

	/*
     * Проверка наличия машины состояний у документа
     * @param document
     * @return
     *
     * Используется в
     * 		- evaluator.lib.js
     * 		- permission-utils.js -> has.statemachine.get.js-(/lecm/documents/hasStatemachine)
     */
	boolean hasStatemachine(NodeRef document);

	/*
        * @param document
        * @return Версия машины состояний для документа
        *
        * Используется в
        * 		- service.information.post.json.js
        */
	String getStatemachineVersion(NodeRef document);

	/*
             * Используется в
             * 		- групповых операциях
             */
	void executeTransitionAction(NodeRef document, String actionName);

	/*
         * Используется в
         * 		- групповых операциях и мобильном клиенте
         */
	void executeTransitionAction(NodeRef document, String actionName, String persistedResponse);

	/*
         * Используется в
         * 		- StatemachineWebScriptBean - getTasks, getDocumentsTasks
         */
	List<WorkflowTask> filterTasksByAssignees(List<WorkflowTask> tasks, List<NodeRef> assigneesEmployees);

	/*
	 * Используется в
	 * 		- documentsTasks.get.js
	 * 		- StatemachineWebScriptBean - getDocumentsTasks
	 */
	List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser);

	/**
	 * Останавливает процесс по его Id
	 *
	 * @param processId
	 */
	void terminateProcess(String processId);

	void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value);

	List<NodeRef> getDocumentsWithActiveTasks(String employeeLogin, Set<String> tasksNames, Integer remainingDays);
	List<NodeRef> getDocumentsWithFinishedTasks(List<NodeRef> documents, String employeeLogin, Set<String> tasksNames);

	void sendSignal(String executionId);

	boolean isServiceWorkflow(WorkflowInstance workflow);

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Аналог grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName)
     * но получает инфу о таске из 4-го параметра
     *
     * Используется в
     * 		- машинах состояний (Исходящие, ОРД, НД, Внутренний, Входящий - теперь используется альтернативный метод)
     */
	boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName, Task task);

	/*
     * Используется в
     * 		- additional.docs.byType.get.js
     * 		- contracts.settings.get.js
     * 		- document-type.settings.get.js
     * 		- documents.summary.get.js
     */
	Set<String> getArchiveFolders(String documentType);

	void resetStateMachene();

	/**
	 * прицепить запущеный процесс к машине состояний, которая обслуживает указанный документ
	 *
	 * @param documentRef NodeRef на документ
	 * @param processInstanceID идентификатор запущеного процесса
	 * @param processDefinitionID идентификатор используемого описания процесса
	 */
	void connectToStatemachine(final NodeRef documentRef, final String processInstanceID, final String processDefinitionID);

	/**
	 * отцепить завершенный процесс от машины состояний
	 *
	 * @param documentRef NodeRef на документ
	 * @param processInstanceID идентификатор запущеного процесса
	 */
	void disconnectFromStatemachine(final NodeRef documentRef, final String processInstanceID);


}
