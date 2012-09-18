package ru.it.lecm.base.workflow.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.task.DelegationState;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import ru.it.lecm.base.workflow.WorkflowController;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:53
 */
public class ManualTaskListener implements TaskListener {

    private FixedValue descriptor;
    private static WorkflowController controller;

    public void setWorkflowController(WorkflowController controller) {
        ManualTaskListener.controller = controller;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        JSONObject jsonDescriptor = new JSONObject(descriptor.getExpressionText());
        controller.executeTask(delegateTask.getId(), jsonDescriptor);
    }

}
