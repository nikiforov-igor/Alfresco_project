package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;

import java.util.List;

public interface StateMachineServiceBean {

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

    public List<String> executeUserAction(NodeRef document, String actionId);
}