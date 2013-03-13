package ru.it.lecm.statemachine;

import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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

    String getWorkflowTaskState();

    String getWorkflowTaskStateMessage();

    Map<QName, Serializable> getProperties();

    public enum WorkflowTaskState {
        WORKFLOWTASKSTATE_NA, WORKFLOWTASKSTATE_NEW, WORKFLOWTASKSTATE_OVERDUE, WORKFLOWTASKSTATE_SOON
    }
}
