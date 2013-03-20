package ru.it.lecm.statemachine;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 12.03.13
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public interface WorkflowTaskBean {
    String getId();

    String getName();

    String getTitle();

    String getDescription();

    Date getStartDate();

    Date getDueDate();

    String getStatusMessage();

    String getType();

    String getTypeMessage();

    int getPriority();

    String getWorkflowTaskPriority();

    String getPriorityMessage();

    public enum WorkflowTaskType {
        WORKFLOWTASKTYPE_NA, WORKFLOWTASKTYPE_OVERDUE, WORKFLOWTASKTYPE_SOON
    }

    public enum WorkflowTaskPriority {
        WORKFLOWTASKPRIORITY_LOW(1), WORKFLOWTASKPRIORITY_MEDIUM(2), WORKFLOWTASKPRIORITY_HIGH(3);
        int priority;

        private WorkflowTaskPriority(int priority) {
            this.priority = priority;
        }

        public static WorkflowTaskPriority getValue(int priority) {
            for (WorkflowTaskPriority workflowTaskPriority : values()) {
                if (workflowTaskPriority.priority == priority) {
                    return workflowTaskPriority;
                }
            }

            return null;
        }
    }
}
