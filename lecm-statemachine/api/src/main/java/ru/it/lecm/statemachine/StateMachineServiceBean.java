package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StateMachineServiceBean {

    public static final String REDIRECT_VARIABLE = "lecm_redirect_url";
    public static final String ROLE_WITHOUT_PRIVELEGES = "LECM_BASIC_PG_None";

    /**
     * Возвращает список категорий для документа с флагами "редактируемый/не редактируемый"
     *
     * @param document
     * @return
     */
    public boolean isReadOnlyCategory(NodeRef document, String category);

    public boolean hasActiveStatemachine(NodeRef document);

    public String getCurrentTaskId(String executionId);

    /**
     * Возвращает true, если документ находится в статусе Черновик
     * @param document
     * @return
     */
    public boolean isDraft(NodeRef document);

    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef, boolean isActive);

    /**
     * Возвращает может ли текущий сотрудник создавать документ определенного типа
     * @param type - тип документа
     * @return
     */
    public boolean isStarter(String type);

    /**
     * Возвращает список возможных статусов для определенного типа документа
     * @param documentType - тип документа
     * @return
     */
    public List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal);

    public boolean isFinal(NodeRef document);

    /**
     * Выдача сотруднику динамической роли и привелегии согласно текущему статусу документа
     * @param document документ
     * @param employee сотрудник
     * @param roleName имя роли
     * @return
     */
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName);

    /**
     * @param document - документ
     * @return Имя предыдущего статуса
     */
    public String getPreviousStatusName(NodeRef document);

    /**
     * @param document - документ
     * @return Имя предыдущего статуса для статусов в ожидании
     */
    public String getPreviousStatusNameOnTake(NodeRef document);

    public List<String> getPreviousStatusesNames(NodeRef document);

    public String getStatemachineId(NodeRef document);

    public Map<String, Object> getVariables(String executionId);

    /**
     * Возвращает бизнес-роли которые могут создавать документ определенного типа
     * @param documentType - тип документа
     * @return
     */
    public Set<String> getStarterRoles(String documentType);

    public void checkReadOnlyCategory(NodeRef document, String category);

    public boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority);

    public void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value);

    public List<NodeRef> getDocumentsWithActiveTasks(NodeRef employee, Set<String> workflowIds, Integer remainingDays);

    public void sendSignal(String executionId);

    public boolean isServiceWorkflow(WorkflowInstance workflow);
    
    public void resetStateMachene();
}
