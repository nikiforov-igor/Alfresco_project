package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.workflow.api.RouteAspecsModel;
import ru.it.lecm.workflow.api.WorkflowType;

/**
 *
 * @author snovikov
 */
public class DefaultWorkflowRunner extends AbstractWorkflowRunner {

	public static class DefaultWorkflowType implements WorkflowType {

		@Override
		public String getWorkflowDefinitionId() {
			return "lecmDefaultWorkflow";
		}

		@Override
		public String getType() {
			return "DEFAULT";
		}
	}
	
	@Override
	protected Map<QName, Serializable> runImpl(Map<String, Object> variables, Map<QName, Serializable> properties) {
		return properties;
	}

	@Override
	protected QName getWorkflowIdPropQName() {
		return RouteAspecsModel.PROP_DEFAULT_WORKFLOW_ID;
	}
	
}
