package ru.it.lecm.statemachine.bean;

import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.statemachine.WorkflowTaskBean;
import ru.it.lecm.statemachine.WorkflowTaskListBean;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 12.03.13
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowTaskListPageBean implements WorkflowTaskListBean {
    private List<WorkflowTaskBean> myTasks = new ArrayList<WorkflowTaskBean>();
    private List<WorkflowTaskBean> subordinateTasks = new ArrayList<WorkflowTaskBean>();
    private int myTasksTotalCount = 0;
    private boolean showSubordinateTasks = false;

    @Override
    public String getShowSubordinateTasks() {
        return String.valueOf(showSubordinateTasks);
    }

    @Override
    public int getMyTasksTotalCount() {
        return myTasksTotalCount;
    }

    @Override
    public int getMyTasksDisplayedCount() {
        return myTasks.size();
    }

    @Override
    public List<WorkflowTaskBean> getMyTasks() {
        return myTasks;
    }

    @Override
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
            this.myTasks.add(new WorkflowTaskPageBean(task));
        }
    }

    public void setSubordinatesTasks(List<WorkflowTask> subordinatesTasks) {
        if (subordinatesTasks == null) {
            return;
        }

        for (WorkflowTask task : subordinatesTasks) {
            this.subordinateTasks.add(new WorkflowTaskPageBean(task));
        }
    }

    public void setShowSubordinateTasks(boolean showSubordinateTasks) {
        this.showSubordinateTasks = showSubordinateTasks;
    }

    class WorkflowTaskPageBean implements WorkflowTaskBean {
        public static final String WORKFLOW_STATUS_PREFIX = "workflowStatus.";

        private WorkflowTask workflowTask;
        private WorkflowTaskType type = WorkflowTaskType.WORKFLOWTASKTYPE_NA;
        private int priority;

        public WorkflowTaskPageBean(WorkflowTask workflowTask) {
            if (workflowTask == null) {
                throw new IllegalArgumentException("WorkflowTask is null!");
            }

            this.workflowTask = workflowTask;

            Serializable priorityProperty = workflowTask.getProperties().get(WorkflowModel.PROP_PRIORITY);
            if (priorityProperty != null) {
                this.priority = (Integer) priorityProperty;
            }

            setType();
        }

        public String getId() {
            return workflowTask.getId();
        }

        public String getName() {
            return workflowTask.getName();
        }

        public String getTitle() {
            return workflowTask.getTitle();
        }

        public String getDescription() {
            return workflowTask.getDescription();
        }

        public Date getStartDate() {
            Serializable property = workflowTask.getProperties().get(WorkflowModel.PROP_START_DATE);
            return property != null ? (Date) property : null;
        }

        public Date getDueDate() {
            Serializable property = workflowTask.getProperties().get(WorkflowModel.PROP_DUE_DATE);
            return property != null ? (Date) property : null;
        }

        public String getType() {
            return type.toString();
        }

        public String getTypeMessage() {
            return getMessage(type.toString());
        }

        public String getStatusMessage() {
            if (!workflowTask.getProperties().containsKey(WorkflowModel.PROP_STATUS)) {
                return "";
            }

            String status = (String) workflowTask.getProperties().get(WorkflowModel.PROP_STATUS);
            return getMessage(WORKFLOW_STATUS_PREFIX + status);
        }

        public int getPriority() {
            return priority;
        }

        public String getWorkflowTaskPriority() {
            WorkflowTaskPriority workflowTaskPriority = WorkflowTaskPriority.getValue(priority);
            return workflowTaskPriority != null ? workflowTaskPriority.toString() : "";
        }

        public String getPriorityMessage() {
            WorkflowTaskPriority workflowTaskPriority = WorkflowTaskPriority.getValue(priority);
            return workflowTaskPriority != null ? getMessage(workflowTaskPriority.toString()) : "";
        }

        private String getMessage(String key) {
            String message = I18NUtil.getMessage(key, I18NUtil.getLocale());
            return message != null ? message : "";
        }

        private void setType() {
            if (workflowTask.getState() != WorkflowTaskState.IN_PROGRESS) {
                return;
            }

            Date dueDate = getDueDate();
            if (dueDate == null) {
                return;
            }

            if (isDateOverdue(dueDate)) {
                this.type = WorkflowTaskType.WORKFLOWTASKTYPE_OVERDUE;
                return;
            }

            if (isDateSoon(dueDate)) {
                this.type = WorkflowTaskType.WORKFLOWTASKTYPE_SOON;
            }
        }

        private boolean isDateOverdue(Date date) {
            if (date == null) {
                return false;
            }

            Date todayMidnight = DateUtils.truncate(new Date(), Calendar.DATE);
            return date.before(todayMidnight);
        }

        private boolean isDateSoon(Date date) {
            if (date == null) {
                return false;
            }

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, 2);

            Date soonMidnight = DateUtils.truncate(calendar.getTime(), Calendar.DATE);
            return date.before(soonMidnight);
        }
    }
}