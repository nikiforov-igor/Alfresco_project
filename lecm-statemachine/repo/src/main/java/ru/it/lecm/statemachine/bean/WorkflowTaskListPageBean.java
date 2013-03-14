package ru.it.lecm.statemachine.bean;

import ru.it.lecm.statemachine.WorkflowTaskBean;
import ru.it.lecm.statemachine.WorkflowTaskListBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 12.03.13
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowTaskListPageBean implements WorkflowTaskListBean {
    private List<WorkflowTaskBean> tasks;
    private int totalTasksCount;

    public WorkflowTaskListPageBean(List<WorkflowTaskBean> tasks, int totalTasksCount) {
        this.tasks = tasks;
        this.totalTasksCount = totalTasksCount;
    }

    @Override
    public int getTotalTasksCount() {
        return totalTasksCount;
    }

    @Override
    public List<WorkflowTaskBean> getTasks() {
        return tasks;
    }
}