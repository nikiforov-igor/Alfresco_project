package ru.it.lecm.workflow.beans;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.workflow.api.WorkflowRunner;
import ru.it.lecm.workflow.api.WorkflowRunnerServiceConfigurer;
import ru.it.lecm.workflow.api.WorkflowType;

/**
 *
 * @author vlevin
 */
public class WorkflowRunnerInjector {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowRunnerInjector.class);
	private AbstractWorkflowRunner workflowRunner;
	private WorkflowRunnerServiceConfigurer workflowRunnerService;

	public void setWorkflowRunner(AbstractWorkflowRunner workflowRunner) {
		this.workflowRunner = workflowRunner;
	}

	public void setWorkflowRunnerService(WorkflowRunnerServiceConfigurer workflowRunnerService) {
		this.workflowRunnerService = workflowRunnerService;
	}

	void init() {
		Map<WorkflowType, WorkflowRunner> runners = workflowRunnerService.getWorkflowRunners();
		WorkflowType workflowType = workflowRunner.getWorkflowType();
		boolean hasPreviousRunner = runners.containsKey(workflowType);
		if (hasPreviousRunner) {
			logger.warn("New workflow runner was registered for workflow type {} {}. Previous workflow runner is lost. Please check your configuration", workflowType.getType(), workflowType.getWorkflowDefinitionId());
		}
		runners.put(workflowType, workflowRunner);
	}

}
