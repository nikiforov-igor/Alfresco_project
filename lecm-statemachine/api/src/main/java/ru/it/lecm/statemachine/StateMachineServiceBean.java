package ru.it.lecm.statemachine;

import java.util.List;

public interface StateMachineServiceBean {
    String nextTransition(String taskId);

    String getCurrentTaskId(String executionId);

    List<WorkflowTaskBean> getMyActiveTasks(String nodeRef);

    List<WorkflowTaskBean> getMyCompleteTasks(String nodeRef);
}