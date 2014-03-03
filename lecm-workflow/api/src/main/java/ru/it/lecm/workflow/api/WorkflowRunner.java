package ru.it.lecm.workflow.api;

import java.util.Map;

/**
 *
 * @author vmalygin
 */
public interface WorkflowRunner {
	String run(Map<String, Object> variables);
}
