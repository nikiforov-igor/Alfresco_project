package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Collections;
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
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineEventCategory;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.workflow.WorkflowType;
import ru.it.lecm.workflow.api.RouteService;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.api.WorkflowRunner;
import ru.it.lecm.workflow.utils.WorkflowVariablesHelper;

/**
 *
 * @author vmalygin
 */
public abstract class AbstractWorkflowRunner implements WorkflowRunner, InitializingBean {

	protected final static String ACTIVITI_PREFIX = "activiti$";

	protected NodeService nodeService;
	protected WorkflowService workflowService;
	protected OrgstructureBean orgstructureBean;
	protected AlfrescoProcessEngineConfiguration alfrescoProcessEngineConfiguration;
	protected RuntimeService runtimeService;
	protected StateMachineServiceBean stateMachineService;
	protected BusinessJournalService businessJournalService;
	protected RouteService routeService;
	protected WorkflowAssigneesListService workflowAssigneesListService;

	protected WorkflowType workflowType;
	protected List<String> inputVariables;

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setWorkflowService(final WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setOrgstructureBean(final OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public void setAlfrescoProcessEngineConfiguration(final AlfrescoProcessEngineConfiguration alfrescoProcessEngineConfiguration) {
		this.alfrescoProcessEngineConfiguration = alfrescoProcessEngineConfiguration;
	}

	public void setStateMachineService(final StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public void setBusinessJournalService(final BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setRouteService(final RouteService routeService) {
		this.routeService = routeService;
	}

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	@Override
	public WorkflowType getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(WorkflowType workflowType) {
		this.workflowType = workflowType;
	}

	public void setInputVariables(final List<String> inputVariables) {
		this.inputVariables = inputVariables;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.runtimeService = alfrescoProcessEngineConfiguration.getRuntimeService();
	}

	@Override
	public String run(final Map<String, Object> variables) {
		checkMandatoryVariables(variables);
		//формирование bpmPackage
		Map<QName, Serializable> properties = getInitialWorkflowProperties(variables);
		//получение workflowDefinition
		WorkflowDefinition workflowDefinition = getWorkflowDefinition(variables);
		properties = runImpl(variables, properties);
		// start the workflow
		WorkflowInstance workflowInstance = startWorkflow(workflowDefinition, properties);
		//инициализировать входные переменные
		setInputVariables(workflowInstance.getId(), variables);
		//отсигналить что все готово
		stateMachineService.sendSignal(workflowInstance.getId());
		// log to business journal
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
		logStartWorkflowEvent(documentRef, workflowInstance);
		return workflowInstance.getId();
	}

	protected abstract Map<QName, Serializable> runImpl(final Map<String, Object> variables, final Map<QName, Serializable> properties);

	protected void checkMandatoryVariables(final Map<String, Object> variables) {
		for (String inputVariable : inputVariables) {
			ParameterCheck.mandatoryString(inputVariable, (String) variables.get(inputVariable));
		}
	}

	protected Map<QName, Serializable> getInitialWorkflowProperties(final Map<String, Object> variables) {
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);

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
		String workflowDefinition = WorkflowVariablesHelper.getWorkflowDefinition(variables);
		WorkflowDefinition definition = workflowService.getDefinitionByName(ACTIVITI_PREFIX + workflowDefinition);
		if (definition == null) {
			String msg = String.format("No workflow definition found for %s", workflowDefinition);
			throw new IllegalArgumentException(msg);
		}
		return definition;
	}

	protected WorkflowInstance startWorkflow(final WorkflowDefinition workflowDefinition, final Map<QName, Serializable> properties) {
		WorkflowPath path = workflowService.startWorkflow(workflowDefinition.getId(), properties);
		WorkflowInstance instance = path.getInstance();

		WorkflowTask startTask = workflowService.getStartTask(instance.getId());
		workflowService.endTask(startTask.getId(), null);
		return instance;
	}

	protected void setInputVariables(final String executionId, final Map<String, Object> variables) {
		for (Entry<String, Object> var : variables.entrySet()) {
			runtimeService.setVariable(executionId.replace(ACTIVITI_PREFIX, ""), var.getKey(), var.getValue());
		}
	}

	protected void logStartWorkflowEvent(final NodeRef document, final WorkflowInstance instance) {
		if (!stateMachineService.isServiceWorkflow(instance)) {
			String message = "Запущен бизнес-процесс #object1 на документе #mainobject";
			List<String> objects = Collections.singletonList(instance.getId());
			businessJournalService.log(document, StateMachineEventCategory.START_WORKFLOW, message, objects);
		}
	}
}
