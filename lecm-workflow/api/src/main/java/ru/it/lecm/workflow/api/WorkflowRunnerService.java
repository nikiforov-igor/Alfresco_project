package ru.it.lecm.workflow.api;

import ru.it.lecm.workflow.WorkflowType;

/**
 *
 * @author vmalygin
 */
public interface WorkflowRunnerService {
	WorkflowRunner getWorkflowRunner(final WorkflowType workflowType);
}
