package ru.it.lecm.statemachine;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 12.03.13
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public interface WorkflowTaskListBean {
    List<WorkflowTaskBean> getMyTasks();

    List<WorkflowTaskBean> getSubordinateTasks();

    int getMyTasksTotalCount();

    int getMyTasksLimitedCount();

    String getShowSubordinateTasks();
}
