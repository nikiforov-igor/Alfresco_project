package ru.it.lecm.statemachine.bean;

import org.alfresco.service.cmr.workflow.WorkflowInstance;
import ru.it.lecm.statemachine.WorkflowListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 20.03.13
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowListPageBean implements WorkflowListBean {
    private int activeWorkflowsTotalCount = 0;
    private List<WorkflowInstance> activeWorkflows = new ArrayList<WorkflowInstance>();
    private List<WorkflowInstance> completedWorkflows = new ArrayList<WorkflowInstance>();

    @Override
    public List<WorkflowInstance> getActiveWorkflows() {
        return activeWorkflows;
    }

    @Override
    public List<WorkflowInstance> getCompletedWorkflows() {
        return completedWorkflows;
    }

    @Override
    public int getActiveWorkflowsTotalCount() {
        return activeWorkflowsTotalCount;
    }

    @Override
    public int getActiveWorkflowsDisplayedCount() {
        return activeWorkflows.size();
    }

    public void setCompletedWorkflows(List<WorkflowInstance> completedWorkflows) {
        if (completedWorkflows != null) {
            this.completedWorkflows = completedWorkflows;
        }
    }

    public void setActiveWorkflows(List<WorkflowInstance> activeWorkflows, int activeWorkflowsLimit) {
        if (activeWorkflows == null) {
            return;
        }

        this.activeWorkflowsTotalCount = activeWorkflows.size();
        if (activeWorkflowsLimit > 0 && activeWorkflowsLimit < activeWorkflows.size()) {
            this.activeWorkflows = activeWorkflows.subList(0, activeWorkflowsLimit);
        } else {
            this.activeWorkflows = activeWorkflows;
        }
    }
}
