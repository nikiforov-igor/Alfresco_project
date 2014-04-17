package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StateMachineServiceBean {

    public static final String REDIRECT_VARIABLE = "lecm_redirect_url";

    /**
     * Возвращает список полей для документа с флагами "редактируемый/не редактируемый"
     *
     * @param document
     * @return
     */
    public StateFields getStateFields(NodeRef document);

    /**
     * Возвращает список категорий для документа с флагами "редактируемый/не редактируемый"
     *
     * @param document
     * @return
     */
    public boolean isReadOnlyCategory(NodeRef document, String category);

    /**
     * Проверка наличия машины состояний у документа
     * @param document
     * @return
     */
    public boolean hasStatemachine(NodeRef document);

    public boolean hasActiveStatemachine(NodeRef document);

    public void checkReadOnlyCategory(NodeRef document, String category);

    String nextTransition(String taskId);

    String getCurrentTaskId(String executionId);

    /**
     * Возвращает true, если документ находится в статусе Черновик
     * @param document
     * @return
     */
    public boolean isDraft(NodeRef document);

    public List<NodeRef> getAssigneesForWorkflow(String workflowId);

    public List<WorkflowTask> getDocumentTasks(NodeRef nodeRef);

    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef);

    public String getDocumentStatus(NodeRef document);

    public TransitionResponse executeUserAction(NodeRef document, String actionId);

    /**
     * Передает текущие задачи пользователя другому пользователю
     */
    public  boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority);

    /**
     * Возвращает может ли текущий сотрудник создавать документ определенного типа
     * @param type - тип документа
     * @return
     */
    public boolean isStarter(String type);

    /**
     * Возвращает может ли сотрудник создавать документ определенного типа
     * @param type - тип документа
     * @param employee - сотрудник
     * @return
     */
    public boolean isStarter(String type, NodeRef employee);

    /**
     * Возвращает список возможных статусов для определенного типа документа
     * @param documentType - тип документа
     * @return
     */
    public List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal);

    public boolean isFinal(NodeRef document);

    List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser);

    public void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value);

    /**
     * Возвращает список архивных папок для типа документа
     * @param documentType - тип документа
     * @return
     */
    public Set<String> getArchiveFolders(String documentType);

    /**
     *
     * @param document
     * @return Версия машины состояний для документа
     */
    public String getStatemachineVersion(NodeRef document);

    /**
     * Выдача сотруднику динамической роли и привелегии согласно текущему статусу документа
     * @param document документ
     * @param employee сотрудник
     * @param roleName имя роли
     * @return
     */
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName);

    /**
     * Возвращает true, если поле возможно редактировать
     * @param document
     * @param field
     * @return
     */
    public boolean isEditableField(NodeRef document, String field);

    /**
     * @param document - документ
     * @return Имя предыдущего статуса
     */
    public String getPreviousStatusName(NodeRef document);

    public String getStatemachineId(NodeRef document);

    public Map<String, Object> getVariables(String executionId);

    void executeTransitionAction(NodeRef document, String actionName);

    boolean isServiceWorkflow(String executionId);

	boolean isServiceWorkflow(WorkflowInstance workflow);

	void sendSignal(String executionId);

    /**
     * Возвращает бизнес-роли которые могут создавать документ определенного типа
     * @param type - тип документа
     * @return
     */
    public Set<String> getStarterRoles(String documentType);
}
