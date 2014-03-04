package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public class ApprovalWorkflowRunner extends AbstractWorkflowRunner {

	@Override
	public String run(Map<String, Object> variables) {
		checkMandatoryVariables(variables);

		//формирование bpmPackage
		Map<QName, Serializable> properties = getInitialWorkflowProperties(variables);
		//получение workflowDefinition
		WorkflowDefinition workflowDefinition = getWorkflowDefinition(variables);
		// start the workflow
		String instanceId = startWorkflow(workflowDefinition, properties);
		// connect to Statemachine
//		WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, stateMachineExecutionId, workflowId, currentTaskId, actionName, id, eventName);
//		new DocumentWorkflowUtil().addWorkflow(document, executionId, descriptor);
		// log to business journal
//		helper.logStartWorkflowEvent(document, executionId);
		//инициализировать входные переменные
		setInputVariables(instanceId, variables);
		return instanceId;
	}

}
