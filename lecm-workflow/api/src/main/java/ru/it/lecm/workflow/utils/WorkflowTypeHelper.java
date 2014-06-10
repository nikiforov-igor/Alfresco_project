package ru.it.lecm.workflow.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import ru.it.lecm.workflow.api.WorkflowType;

/**
 *
 * @author vmalygin
 */
public final class WorkflowTypeHelper {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowTypeHelper.class);

	private static Collection<WorkflowType> getWorkflowTypes() {
		ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		Collection<WorkflowType> result;
		if (context != null) {
			Map<String, WorkflowType> workflowTypes = context.getBeansOfType(WorkflowType.class);
			result = workflowTypes.values();
		} else {
			logger.error("Application context is not avaliable. Check usage of WorkflowTypeHelper class");
			result = new ArrayList<>();
		}
		return result;
	}

	private static WorkflowType getWorkflowTypeByTypeOrDefinition(final String candidate) {
		Collection<WorkflowType> types = getWorkflowTypes();
		WorkflowType result = null;
		for (WorkflowType workflowType : types) {
			if (candidate.equals(workflowType.getType()) || candidate.equals(workflowType.getWorkflowDefinitionId())) {
				result = workflowType;
				break;
			}
		}
		return result;
	}

	public static WorkflowType getWorkflowTypeByType(final String type) {
		WorkflowType workflowType = getWorkflowTypeByTypeOrDefinition(type);
		if (workflowType != null) {
			return workflowType;
		} else {
			throw new IllegalArgumentException(String.format("type '%s' is invalid. Appropriate WorkflowType not found!", type));
		}
	}

	public static WorkflowType getWorkflowTypeByDefinition(final String workflowDefinitionId) {
		WorkflowType workflowType = getWorkflowTypeByTypeOrDefinition(workflowDefinitionId);
		if (workflowType != null) {
			return workflowType;
		} else {
			throw new IllegalArgumentException(String.format("workflow definition '%s' is invalid. Appropriate WorkflowType not found!", workflowDefinitionId));
		}
	}

	private WorkflowTypeHelper() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of WorkflowTypeHelper class.");
	}
}
