package ru.it.lecm.base.workflow.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import ru.it.lecm.base.workflow.WorkflowController;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:57
 */
public class EndEventListener implements ExecutionListener {

    private static WorkflowController controller;

    public void setWorkflowController(WorkflowController controller) {
        EndEventListener.controller = controller;
    }

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        controller.endProcess(delegateExecution);
    }
}
