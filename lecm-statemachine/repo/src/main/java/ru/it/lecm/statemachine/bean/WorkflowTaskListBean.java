package ru.it.lecm.statemachine.bean;

import org.alfresco.service.cmr.workflow.WorkflowTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 12.03.13
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowTaskListBean {
    private List<WorkflowTaskBean> myTasks = new ArrayList<WorkflowTaskBean>();
    private List<WorkflowTaskBean> subordinateTasks = new ArrayList<WorkflowTaskBean>();
    private int myTasksTotalCount = 0;
    private boolean showSubordinateTasks = false;

    public String getShowSubordinateTasks() {
        return String.valueOf(showSubordinateTasks);
    }

    public int getMyTasksTotalCount() {
        return myTasksTotalCount;
    }

    public int getMyTasksDisplayedCount() {
        return myTasks.size();
    }

    public List<WorkflowTaskBean> getMyTasks() {
        return myTasks;
    }

    public List<WorkflowTaskBean> getSubordinateTasks() {
        return subordinateTasks;
    }

    public void setMyTasks(List<WorkflowTask> myTasks, int myTasksLimit) {
        if (myTasks == null) {
            return;
        }

        this.myTasksTotalCount = myTasks.size();

        List<WorkflowTask> myTasksDisplayed;
        if (myTasksLimit > 0 && myTasksTotalCount > myTasksLimit) {
            myTasksDisplayed = myTasks.subList(0, myTasksLimit);
        } else {
            myTasksDisplayed = myTasks;
        }

        for (WorkflowTask task : myTasksDisplayed) {
            this.myTasks.add(new WorkflowTaskBean(task));
        }
    }

    public void setSubordinatesTasks(List<WorkflowTask> subordinatesTasks) {
        if (subordinatesTasks == null) {
            return;
        }

        for (WorkflowTask task : subordinatesTasks) {
            this.subordinateTasks.add(new WorkflowTaskBean(task));
        }
    }

    public void setShowSubordinateTasks(boolean showSubordinateTasks) {
        this.showSubordinateTasks = showSubordinateTasks;
    }
}