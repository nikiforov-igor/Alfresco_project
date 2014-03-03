package ru.it.lecm.workflow.beans;

import java.util.List;
import ru.it.lecm.workflow.api.WorkflowRunner;

/**
 *
 * @author vmalygin
 */
public abstract class AbstractWorkflowRunner implements WorkflowRunner {

	protected List<String> inputVariables;

	public void setInputVariables(List<String> inputVariables) {
		this.inputVariables = inputVariables;
	}
}
