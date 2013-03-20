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

    WorkflowTaskListBean getMyActiveTasks(String nodeRef, int loadCount);

    List<WorkflowTaskBean> getMyCompleteTasks(String nodeRef);

    public boolean isDraft(NodeRef document);

    public List<NodeRef> getAssigneesForWorkflow(String workflowId);
}