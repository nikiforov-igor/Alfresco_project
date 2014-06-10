package ru.it.lecm.workflow.api;


/**
 *
 * @author vmalygin
 */
public interface WorkflowRunnerService {
	WorkflowRunner getWorkflowRunner(final WorkflowType workflowType);
}
