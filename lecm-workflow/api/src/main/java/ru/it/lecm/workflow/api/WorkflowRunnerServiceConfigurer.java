package ru.it.lecm.workflow.api;

import java.util.Map;

/**
 *
 * @author vlevin
 */
public interface WorkflowRunnerServiceConfigurer {
	Map<WorkflowType, WorkflowRunner> getWorkflowRunners();

}
