package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.activiti.engine.RuntimeService;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineEventCategory;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.WorkflowDescriptor;
import ru.it.lecm.statemachine.util.DocumentWorkflowUtil;
import ru.it.lecm.workflow.api.RouteService;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.api.WorkflowRunner;
import ru.it.lecm.workflow.api.WorkflowType;
import ru.it.lecm.workflow.utils.WorkflowVariablesHelper;

/**
 *
 * @author vmalygin
 */
public abstract class AbstractWorkflowRunner implements WorkflowRunner, InitializingBean {

	protected final static String ACTIVITI_PREFIX = "activiti$";
	private final static Logger logger = LoggerFactory.getLogger(AbstractWorkflowRunner.class);

	protected NodeService nodeService;
	protected WorkflowService workflowService;
	protected AlfrescoProcessEngineConfiguration alfrescoProcessEngineConfiguration;
	protected RuntimeService runtimeService;
	protected StateMachineServiceBean stateMachineService;
	protected BusinessJournalService businessJournalService;
	protected RouteService routeService;
	protected WorkflowAssigneesListService workflowAssigneesListService;
	protected PersonService personService;
	protected OrgstructureBean orgstructureService;

	protected WorkflowType workflowType;
	protected List<String> inputVariables;

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setWorkflowService(final WorkflowService workflowService) {
		this.workflowService = workflowService;
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

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
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
		//сохранить ИДшник запущенного бизнес процесса в атрибуты документа
		persistWorkflowId(variables, workflowInstance);
		//прицепиться к машине состояний документа
		connectToStatemachine(variables, workflowInstance);
		//отсигналить что все готово
		stateMachineService.sendSignal(workflowInstance.getId());
		// log to business journal
		logStartWorkflowEvent(variables, workflowInstance);
		return workflowInstance.getId();
	}

	private void persistWorkflowId(final Map<String, Object> variables, final WorkflowInstance instance) {
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
		nodeService.setProperty(documentRef, getWorkflowIdPropQName(), instance.getId());
	}

	private void connectToStatemachine(final Map<String, Object> variables, final WorkflowInstance instance) {
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
		String stateMachineExecutionId = ACTIVITI_PREFIX + stateMachineService.getStatemachineId(documentRef);
		String currentTaskId = stateMachineService.getCurrentTaskId(stateMachineExecutionId);
		WorkflowDescriptor descriptor = new WorkflowDescriptor(instance.getId(), stateMachineExecutionId, instance.getDefinition().getName(), currentTaskId, "", "", "");
		logger.info("!!!!!!!!!!! connectToStatemachine: "+descriptor);
		new DocumentWorkflowUtil().addWorkflow(documentRef, instance.getId(), descriptor);
	}

	private NodeRef getRunAsPerson(final String runAs, final NodeRef runAsEmployee) {
		NodeRef personRef;
		try {
			personRef = personService.getPerson(runAs, false);
		} catch (NoSuchPersonException ex) {
			String msg = String.format("[%s] Can't get person for username %s", this.getClass().getName(), runAs);
			if (logger.isDebugEnabled()) {
				logger.warn(msg, ex);
			} else {
				logger.warn(msg);
			}
			personRef = null;
		}
		if (personRef == null) {
			personRef = (runAsEmployee != null) ? orgstructureService.getPersonForEmployee(runAsEmployee) : null;
		}
		if (personRef == null) {
			String currentUserName = AuthenticationUtil.getFullyAuthenticatedUser();
			try {
				personRef = personService.getPerson(currentUserName, false);
			} catch (NoSuchPersonException ex) {
				String msg = String.format("[%s] Can't get person for username %s", this.getClass().getName(), currentUserName);
				if (logger.isDebugEnabled()) {
					logger.warn(msg, ex);
				} else {
					logger.warn(msg);
				}
				logger.warn("Workflow will be started using user 'workflow'");
				personRef = personService.getPerson("workflow", false);
			}
		}

		return personRef;
	}

	protected abstract Map<QName, Serializable> runImpl(final Map<String, Object> variables, final Map<QName, Serializable> properties);

	protected abstract QName getWorkflowIdPropQName();

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
		String runAs = WorkflowVariablesHelper.getRunAs(variables);
		NodeRef runAsEmployee = WorkflowVariablesHelper.getRunAsEmployee(variables);
		properties.put(WorkflowModel.ASSOC_ASSIGNEE, getRunAsPerson(runAs, runAsEmployee));

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

	protected void logStartWorkflowEvent(final Map<String, Object> variables, final WorkflowInstance instance) {
		if (!stateMachineService.isServiceWorkflow(instance)) {
			String message = "Запущен бизнес-процесс #object1 на документе #mainobject";
			NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
			List<String> objects = Collections.singletonList(instance.getId());
			businessJournalService.log(documentRef, StateMachineEventCategory.START_WORKFLOW, message, objects);
		}
	}
}
