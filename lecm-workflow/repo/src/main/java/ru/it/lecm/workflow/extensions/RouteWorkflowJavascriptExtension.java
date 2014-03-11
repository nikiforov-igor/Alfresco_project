package ru.it.lecm.workflow.extensions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.WorkflowType;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.RouteAspecsModel;
import ru.it.lecm.workflow.api.WorkflowRunner;
import ru.it.lecm.workflow.api.WorkflowRunnerService;
import ru.it.lecm.workflow.utils.WorkflowVariablesHelper;

/**
 *
 * @author vmalygin
 */
public class RouteWorkflowJavascriptExtension extends BaseWebScript {

	private final static Logger logger = LoggerFactory.getLogger(RouteWorkflowJavascriptExtension.class);

	private NodeService nodeService;
	private WorkflowRunnerService workflowRunnerService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setWorkflowRunnerService(WorkflowRunnerService workflowRunnerService) {
		this.workflowRunnerService = workflowRunnerService;
	}

	public void makeDocumentRoutable(final ScriptNode document, final ScriptNode route) {
		NodeRef documentRef = document.getNodeRef();
		NodeRef routeRef = route.getNodeRef();
		Serializable isRegisterAfterSigned = nodeService.getProperty(routeRef, LecmWorkflowModel.PROP_IS_REGISTER_AFTER_SIGNED);

		Map<QName, Serializable> routableProps = new HashMap<QName, Serializable>();
		routableProps.put(RouteAspecsModel.PROP_ROUTEREF, routeRef);
		routableProps.put(RouteAspecsModel.PROP_IS_REGISTER_AFTER_SIGNED, isRegisterAfterSigned);

		boolean hasRoutableAspect = nodeService.hasAspect(documentRef, RouteAspecsModel.ASPECT_ROUTABLE);
		if (!hasRoutableAspect) {
			nodeService.addAspect(documentRef, RouteAspecsModel.ASPECT_ROUTABLE, routableProps);
		} else {
			nodeService.addProperties(documentRef, routableProps);
		}
	}

	public String startWorkflow(final Map<String, Object> variables) {
		String workflowDefinition = WorkflowVariablesHelper.getWorkflowDefinition(variables);
		WorkflowType workflowType = WorkflowType.getById(workflowDefinition);
		WorkflowRunner workflowRunner = workflowRunnerService.getWorkflowRunner(workflowType);
		return workflowRunner.run(variables);
	}
}
