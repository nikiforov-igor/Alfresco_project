package ru.it.lecm.base.workflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import ru.it.lecm.base.workflow.WorkflowController;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:53
 */
public class LogicECMWorkflowTask implements TaskListener {

    private FixedValue workflowId;
    private FixedValue assignee;
    private static WorkflowController controller;

    public void setWorkflowController(WorkflowController controller) {
        LogicECMWorkflowTask.controller = controller;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        controller.executeTask(delegateTask.getId(), workflowId.getExpressionText(), assignee.getExpressionText());
    }

}
