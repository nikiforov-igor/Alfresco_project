package ru.it.lecm.workflow.extensions;

import java.util.Map;
import java.util.Map.Entry;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;

/**
 *
 * @author vmalygin
 */
public class RouteWorkflowJavascriptExtension extends BaseWebScript {

	private final static Logger logger = LoggerFactory.getLogger(RouteWorkflowJavascriptExtension.class);

	public void exploreExecutionVariables(final DelegateExecution execution) {

		Map<String, Object> variables = execution.getVariables();
		for(Entry<String, Object> entry : variables.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			String simpleName = (value == null) ? "null":value.getClass().getSimpleName();
			Object args[] = {key, simpleName, value};
			logger.debug("variable {}\t\tsimpleName {}\t\tvalue {}", args);
		}
	}
}
