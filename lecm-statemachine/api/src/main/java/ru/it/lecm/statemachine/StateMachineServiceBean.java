package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;

import java.util.List;

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
    public List<String> getStatuses(String documentType);

    public boolean isFinal(NodeRef document);

    List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser);
}