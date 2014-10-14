package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;

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

	/**
	 * Останавливает процесс по его Id
	 *
	 * @param processId
	 */
	void terminateProcess(String processId);

	void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value);

	List<NodeRef> getDocumentsWithActiveTasks(String employeeLogin, Set<String> workflowIds, Integer remainingDays);

	void sendSignal(String executionId);

	boolean isServiceWorkflow(WorkflowInstance workflow);

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
