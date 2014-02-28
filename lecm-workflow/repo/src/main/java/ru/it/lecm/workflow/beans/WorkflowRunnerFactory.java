package ru.it.lecm.workflow.beans;

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

	private Map<WorkflowType, WorkflowRunner> runners;

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

	public void setRunners(Map<WorkflowType, WorkflowRunner> runners) {
		this.runners = runners;
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
