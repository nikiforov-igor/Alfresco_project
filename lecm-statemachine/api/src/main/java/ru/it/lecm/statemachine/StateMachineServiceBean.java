package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;

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

    WorkflowListBean getWorkflows(String nodeRefParam, String stateParam, int activeWorkflowsLimit);

    WorkflowTaskListBean getTasks(NodeRef nodeRef, String stateParam, boolean isAddSubordinatesTask, int myTasksLimit);
}