package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;
import java.util.Set;

public interface StateMachineServiceBean {

    public Set<StateField> getStateFields(NodeRef document);
    String nextTransition(String taskId);

    String getCurrentTaskId(String executionId);

    List<WorkflowTaskBean> getMyActiveTasks(String nodeRef);

    List<WorkflowTaskBean> getMyCompleteTasks(String nodeRef);

}