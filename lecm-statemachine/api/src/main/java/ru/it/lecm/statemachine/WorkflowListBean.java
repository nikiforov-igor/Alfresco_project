package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.workflow.WorkflowInstance;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 12.03.13
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public interface WorkflowListBean {

    List<WorkflowInstance> getActiveWorkflows();

    List<WorkflowInstance> getCompletedWorkflows();

    int getActiveWorkflowsTotalCount();

    int getActiveWorkflowsDisplayedCount();
}
