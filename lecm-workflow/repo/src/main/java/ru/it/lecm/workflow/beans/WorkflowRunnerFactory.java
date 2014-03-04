package ru.it.lecm.workflow.beans;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import ru.it.lecm.workflow.api.WorkflowRunner;
import ru.it.lecm.workflow.WorkflowType;
import ru.it.lecm.workflow.api.WorkflowRunnerService;

/**
 *
 * @author vmalygin
 */
public class WorkflowRunnerFactory implements FactoryBean<WorkflowRunnerFactory>, WorkflowRunnerService {

	private final static Class<WorkflowRunnerFactory> CLASS = WorkflowRunnerFactory.class;
	private final static Logger logger = LoggerFactory.getLogger(CLASS);

	private final Map<WorkflowType, WorkflowRunner> runners = new EnumMap<WorkflowType, WorkflowRunner>(WorkflowType.class);

	@Override
	public WorkflowRunnerFactory getObject() throws Exception {
		return this;
	}

	@Override
	public Class<?> getObjectType() {
		return CLASS;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setRunners(List<WorkflowRunner> runners) {
		for (WorkflowRunner runner : runners) {
			WorkflowType workflowType = runner.getWorkflowType();
			boolean hasPreviousRunner = this.runners.containsKey(workflowType);
			if (hasPreviousRunner) {
				logger.warn("New workflow runner was registered for workflow type {}. Previous workflow runner is lost. Please check your configuration");
			}
			this.runners.put(workflowType, runner);
		}
	}

	@Override
	public WorkflowRunner getWorkflowRunner(final WorkflowType workflowType) {
		if (runners.containsKey(workflowType)) {
			return runners.get(workflowType);
		} else {
			String msg = String.format("There is no any WorkflowRunner registered for WorkflowType %s", workflowType);
			logger.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}
}
