package ru.it.lecm.workflow.api;

import java.util.Map;
import ru.it.lecm.workflow.WorkflowType;

/**
 *
 * @author vmalygin
 */
public interface WorkflowRunner {
	WorkflowType getWorkflowType();
	String run(Map<String, Object> variables);
}
