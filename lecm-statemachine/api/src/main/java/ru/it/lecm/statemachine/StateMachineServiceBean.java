package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface StateMachineServiceBean {

    public Set<StateField> getStateFields(NodeRef document);

    public boolean hasPrivilegeByEmployee(NodeRef employee, NodeRef document, String privilege);

    public boolean hasPrivilegeByEmployee(NodeRef employee, NodeRef document, Collection<String> privileges);

    public boolean hasPrivilegeByPerson(NodeRef person, NodeRef document, String privilege);

    public boolean hasPrivilegeByPerson(NodeRef person, NodeRef document, Collection<String> privileges);

    String nextTransition(String taskId);

    String getCurrentTaskId(String executionId);

    public boolean isDraft(NodeRef document);

    public List<NodeRef> getAssigneesForWorkflow(String workflowId);

    WorkflowTaskListBean getTasks(NodeRef nodeRef, String stateParam, boolean isAddSubordinatesTask, int myTasksLimit);

    public List<WorkflowTask> getDocumentTasks(NodeRef nodeRef);

    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef);

    WorkflowListBean getWorkflows(NodeRef nodeRef, String stateParam, int activeWorkflowsLimit);
}