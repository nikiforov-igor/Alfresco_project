package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.activiti.engine.RuntimeService;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.api.WorkflowRunner;

/**
 *
 * @author vmalygin
 */
public abstract class AbstractWorkflowRunner implements WorkflowRunner {

	protected final static String DOCUMENT_REF = "documentRef";
	protected final static String ROUTE_REF = "routeRef";
	protected final static String WORKFLOW_DEFINITION = "workflowDefinition";

	protected NodeService nodeService;
	protected WorkflowService workflowService;
	protected OrgstructureBean orgstructureBean;
	protected  AlfrescoProcessEngineConfiguration alfrescoProcessEngineConfiguration;

	protected List<String> inputVariables;

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setWorkflowService(final WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public void setAlfrescoProcessEngineConfiguration(AlfrescoProcessEngineConfiguration alfrescoProcessEngineConfiguration) {
		this.alfrescoProcessEngineConfiguration = alfrescoProcessEngineConfiguration;
	}

	public void setInputVariables(final List<String> inputVariables) {
		this.inputVariables = inputVariables;
	}

	protected void checkMandatoryVariables(final Map<String, Object> variables) {
		for (String inputVariable : inputVariables) {
			if (!variables.containsKey(inputVariable)) {
				String msg = String.format("'%s' is a mandatory.input variable", inputVariable);
				throw new IllegalArgumentException(msg);
			}
		}
	}

	protected Map<QName, Serializable> getInitialWorkflowProperties(final Map<String, Object> variables) {
		NodeRef documentRef = (NodeRef) variables.get(DOCUMENT_REF);

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		//bpmPackage
		NodeRef subprocessPackage = workflowService.createPackage(null);
		String documentName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
		QName qname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, documentName);
		nodeService.addChild(subprocessPackage, documentRef, ContentModel.ASSOC_CONTAINS, qname);
		properties.put(WorkflowModel.ASSOC_PACKAGE, subprocessPackage);
		//assignee
		NodeRef currentEmployeeRef = orgstructureBean.getCurrentEmployee();
		NodeRef personRef = orgstructureBean.getPersonForEmployee(currentEmployeeRef);
		properties.put(WorkflowModel.ASSOC_ASSIGNEE, personRef);

		return properties;
	}

	protected WorkflowDefinition getWorkflowDefinition(final Map<String, Object> variables) {
		String workflowDefinition = (String) variables.get(WORKFLOW_DEFINITION);
		WorkflowDefinition definition = workflowService.getDefinitionByName(workflowDefinition);
		if (definition == null) {
			String msg = String.format("No workflow definition found for %s", workflowDefinition);
			throw new IllegalArgumentException(msg);
		}
		return definition;
	}

	protected String startWorkflow(final WorkflowDefinition workflowDefinition, final Map<QName, Serializable> properties) {
		WorkflowPath path = workflowService.startWorkflow(workflowDefinition.getId(), properties);
		String instanceId = path.getInstance().getId();
		WorkflowTask startTask = workflowService.getStartTask(instanceId);
		workflowService.endTask(startTask.getId(), null);
		return instanceId;
	}

	protected void setInputVariables(final String executionId, final Map<String, Object> variables) {
		RuntimeService runtimeService = alfrescoProcessEngineConfiguration.getRuntimeService();
		for (Entry<String, Object> var : variables.entrySet()) {
			runtimeService.setVariable(executionId, var.getKey(), var.getValue());
		}
	}

}
