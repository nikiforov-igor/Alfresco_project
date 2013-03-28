package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;

import java.util.List;

public interface StateMachineServiceBean {

    public StateFields getStateFields(NodeRef document);

    String nextTransition(String taskId);

    String getCurrentTaskId(String executionId);

    public boolean isDraft(NodeRef document);

    public List<NodeRef> getAssigneesForWorkflow(String workflowId);

    WorkflowTaskListBean getTasks(NodeRef nodeRef, String stateParam, boolean isAddSubordinatesTask, int myTasksLimit);

    public List<WorkflowTask> getDocumentTasks(NodeRef nodeRef);

    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef);

    WorkflowListBean getWorkflows(NodeRef nodeRef, String stateParam, int activeWorkflowsLimit);

    public String getDocumentStatus(NodeRef document);

    public List<String> executeUserAction(NodeRef document, String actionId);
}