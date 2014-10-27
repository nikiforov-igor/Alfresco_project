package ru.it.lecm.statemachine;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ReceiveTaskActivityBehavior;
import org.activiti.engine.impl.calendar.CycleBusinessCalendar;
import org.activiti.engine.impl.calendar.DueDateBusinessCalendar;
import org.activiti.engine.impl.calendar.MapBusinessCalendarManager;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.WorkflowConstants;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.*;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.statemachine.action.*;
import ru.it.lecm.statemachine.action.document.WaitForDocumentChangeAction;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.action.script.WorkflowScript;
import ru.it.lecm.statemachine.assign.AssignExecution;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;
import ru.it.lecm.statemachine.util.DocumentWorkflowUtil;
import ru.it.lecm.wcalendar.IWorkCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: PMelnikov Date: 07.09.12 Time: 16:22
 * <p/>
 * Вспомогательный класс для Activiti BPM Platform.
 * <p/>
 * Позволяет: 1. Запускать пользовательские процессы из машины состояний 2. Передавать сигнал о завершении
 * пользовательского процесс машине состояний с передачей переменных из пользовательского процесса
 */
public class StateMachineHelper implements StateMachineServiceBean, InitializingBean {

	private final static Logger logger = LoggerFactory.getLogger(StateMachineHelper.class);

	public static String ACTIVITI_PREFIX = "activiti$";

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
	private OrgstructureBean orgstructureBean;
	private DocumentMembersService documentMembersService;
	private BusinessJournalService businessJournalService;
	private TransactionService transactionService;
	private DocumentService documentService;
	private DocumentConnectionService documentConnectionService;
	private LecmPermissionService lecmPermissionService;
	private IWorkCalendar workCalendarService;
	private static String BPM_PACKAGE_PREFIX = "bpm_";

	private static String PROP_PARENT_PROCESS_ID = "parentProcessId";

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
	}

	@Override
	public void afterPropertiesSet() {
		MapBusinessCalendarManager mapBusinessCalendarManager = new MapBusinessCalendarManager();
		//mapBusinessCalendarManager.addBusinessCalendar(DurationBusinessCalendar.NAME, new DurationBusinessCalendar());
		mapBusinessCalendarManager.addBusinessCalendar(LecmBusinessCalendar.NAME, new LecmBusinessCalendar(workCalendarService));
		mapBusinessCalendarManager.addBusinessCalendar(DueDateBusinessCalendar.NAME, new DueDateBusinessCalendar());
		mapBusinessCalendarManager.addBusinessCalendar(CycleBusinessCalendar.NAME, new CycleBusinessCalendar());

		activitiProcessEngineConfiguration.setBusinessCalendarManager(mapBusinessCalendarManager);
	}

	@Override
	public boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority) {
		boolean isTransfer = false;
		TaskQuery query = activitiProcessEngineConfiguration.getTaskService().createTaskQuery();
		List<Task> tasks = query.taskAssignee(beforeAuthority).list();
		List<WorkflowInstance> workflows = new ArrayList<WorkflowInstance>();
		workflows.addAll(getDocumentWorkflows(documentRef, true));
		workflows.addAll(getDocumentWorkflows(documentRef, false));
		if (!workflows.isEmpty() && !tasks.isEmpty()) {
			for (WorkflowInstance workflow : workflows) {
				for (Task task : tasks) {
					if (workflow.getId().indexOf(task.getProcessInstanceId()) != -1) {
						task.setAssignee(afterAuthority);
						activitiProcessEngineConfiguration.getTaskService().saveTask(task);
						isTransfer = true;
					}

				}
			}
		}

		return isTransfer;
	}

	/**
	 * Возвращает может ли текущий сотрудник создавать документ определенного типа
	 *
	 * @param type - тип документа
	 * @return
	 */
	@Override
	public boolean isStarter(String type) {
		NodeRef employee = orgstructureBean.getCurrentEmployee();
		return isStarter(type, employee);
	}

	/**
	 * Выборка статусов для всех экземпляров процессов для определенного типа документа
	 *
	 * @param documentType - тип документа
	 * @return
	 */
	@Override
	public List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal) {
		Map<String, StateMachineStatus> statuses = getStateMecheneByName(documentType.replace(":", "_")).getLastVersion().getSettings().getSettingsContent().getStatuses();//List<String> statusesList = new ArrayList<String>(statuses);
		Map<String, StateMachineStatus> filalstatuses = getStateMecheneByName(documentType.replace(":", "_")).getLastVersion().getSettings().getSettingsContent().getFinalStatuses();
		List<String> statusesList = new ArrayList<String>();
		if (includeActive) {
			statusesList.addAll(statuses.keySet());
		}
		if (includeFinal) {
			statusesList.addAll(filalstatuses.keySet());
		}
		Collections.sort(statusesList);
		return statusesList;
	}

	/**
	 * Возвращает бизнес-роли которые могут создавать документ определенного типа
	 *
	 * @param type - тип документа
	 * @return
	 */
	@Override
	public Set<String> getStarterRoles(String documentType) {
		String statmachene = documentType.replace(":", "_");
		Set<String> accessRoles = new HashSet<String>();
		accessRoles.addAll(getStateMecheneByName(statmachene).getLastVersion().getSettings().getSettingsContent().getStarterRoles());
		return accessRoles;
	}

	@Override
	public String getCurrentTaskId(final String executionId) {
		if (executionId == null) {
			return null;
		}

		TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		Task task = taskQuery.processInstanceId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();

		if (task != null) {
			return ACTIVITI_PREFIX + task.getId();
		} else {
			return null;
		}
	}

	@Override
	public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName) {
		String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		boolean result = false;
		if (executionId != null) {
			TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
			TaskQuery taskQuery = taskService.createTaskQuery();
			Task task = taskQuery.processInstanceId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
			if (task != null) {
				String smName = task.getProcessDefinitionId();
				String version = smName.substring(smName.indexOf(":") + 1, smName.lastIndexOf(":"));
				smName = smName.substring(0, smName.indexOf(":"));
				Map<String, LecmPermissionGroup> privaleges = getStateMecheneByName(smName).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(task.getName()).getDynamicRoles();
				LecmPermissionGroup group = privaleges.get(roleName);
				if (group != null) {
					lecmPermissionService.grantDynamicRole(roleName, document, employee.getId(), group);
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	public boolean isReadOnlyCategory(NodeRef document, String category) {
		if (AuthenticationUtil.isRunAsUserTheSystemUser()) {
			return false;   //Системе доступны все категории
		}
		Set<StateField> categories = getStateCategories(document).getFields();
		boolean result = true;
		if (categories != null && categories.size() > 0) {
			for (StateField categoryItem : categories) {
				if (categoryItem.getName().equals(category)) {
					result = !categoryItem.isEditable();
				}
			}
		} else {
			return false;
		}
		return result;
	}

	@Override
	public void checkReadOnlyCategory(NodeRef document, String category) {
		if (category == null || isReadOnlyCategory(document, category)) {
			throw new AlfrescoRuntimeException("Attachments category '" + category + "' is read only for document " + document);
		}
	}

	@Override
	public Map<String, Object> getVariables(String executionId) {
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		return runtimeService.getVariables(executionId.replace(ACTIVITI_PREFIX, ""));
	}

	@Override
	public boolean isDraft(NodeRef document) {
		NodeService nodeService = serviceRegistry.getNodeService();
		if (nodeService.hasAspect(document, StatemachineModel.ASPECT_IS_DRAFT)) {
			return (Boolean) nodeService.getProperty(document, StatemachineModel.PROP_IS_DRAFT);
		} else {
			return false;
//            boolean result = false;
//            List<StateMachineAction> actions = getStatusChangeActions(document);
//            for (StateMachineAction action : actions) {
//                StatusChangeAction statusChangeAction = (StatusChangeAction) action;
//                //TODO возможно, дело в ранее сломанной машине состояний, и такого быть не должно,  но были случаи когда action==null.
//                //Добавил проврку на всякий случай
//                result = result || (null != action && statusChangeAction.isForDraft());
//            }
//            return result;
		}
	}

	@Override
	public boolean hasActiveStatemachine(NodeRef document) {
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		Execution execution = null;
		if (statemachineId != null) {
			execution = activitiProcessEngineConfiguration.getRuntimeService().createExecutionQuery().executionId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();
		}
		return execution != null;
	}

	@Override
	public String getStatemachineId(NodeRef document) {
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		if (statemachineId != null) {
			Execution execution = activitiProcessEngineConfiguration.getRuntimeService().createExecutionQuery().executionId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();
			return execution != null ? execution.getId() : "Не запущен";
		} else {
			return "Не запущен";
		}

	}

	@Override
	public boolean isServiceWorkflow(WorkflowInstance workflow) {
		List<AspectDefinition> aspects = workflow.getDefinition().getStartTaskDefinition().getMetadata().getDefaultAspects();
		if (aspects != null) {
			for (AspectDefinition aspect : aspects) {
				if (aspect.getName().equals(StatemachineModel.ASPECT_IS_SYSTEM_WORKFLOW)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value) {
		if (definitionIds == null || definitionIds.size() == 0) {
			return;
		}
		DocumentWorkflowUtil utils = new DocumentWorkflowUtil();
		List<WorkflowDescriptor> descriptors = utils.getWorkflowDescriptors(document);
		for (String definitionId : definitionIds) {
			definitionId = ACTIVITI_PREFIX + definitionId.replace(ACTIVITI_PREFIX, "");
			for (WorkflowDescriptor descriptor : descriptors) {
				if (definitionId.equals(descriptor.getWorkflowId())) {
					WorkflowInstance instance = serviceRegistry.getWorkflowService().getWorkflowById(descriptor.getExecutionId());
					if (instance != null && instance.isActive()) {
						ExecutionEntity execution = (ExecutionEntity) activitiProcessEngineConfiguration.getRuntimeService().createExecutionQuery().executionId(descriptor.getExecutionId().replace(ACTIVITI_PREFIX, "")).singleResult();
						//Изменяем переменную в процессе
						if (variable != null && value != null) {
							activitiProcessEngineConfiguration.getRuntimeService().setVariable(execution.getId(), variable, value);
						}

						String processId = execution.getProcessInstanceId();
						terminateProcess(processId);
					}
				}
			}
		}
	}

	@Override
	public String getPreviousStatusName(NodeRef document) {
		List<HistoricTaskInstance> tasks = getHistoricalTaskInstances(document);
		String result = null;
		if (!tasks.isEmpty()) {
			result = tasks.get(0).getName();
		}
		return result;
	}

	@Override
	public String getPreviousStatusNameOnTake(NodeRef document) {
		List<HistoricTaskInstance> tasks = getHistoricalTaskInstances(document);
		String result = null;
		if (!tasks.isEmpty() && tasks.size() > 1) {
			result = tasks.get(1).getName();
		}
		return result;
	}

	@Override
	public List<String> getPreviousStatusesNames(NodeRef document) {
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		HistoryService historyService = activitiProcessEngineConfiguration.getHistoryService();
		List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery().processInstanceId(statemachineId.replace(ACTIVITI_PREFIX, "")).orderByHistoricActivityInstanceStartTime().desc().list();
		List<String> result = new ArrayList<>();
		if (!activities.isEmpty()) {
			for (HistoricActivityInstance activity : activities) {
				if (activity.getActivityName() != null) {
					result.add(activity.getActivityName());
				}
			}
		}
		return result;
	}

	@Override
	public boolean isFinal(NodeRef document) {
		Object statemachineId = serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		return statemachineId != null && getExecution((String) statemachineId) == null;
	}

	@Override
	public void /*Используется в AbstractWorkflowRunner*/ sendSignal(String executionId) {
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		Object executionObject = runtimeService.createExecutionQuery().executionId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
		if (executionObject != null) {
			ExecutionEntity execution = (ExecutionEntity) executionObject;
			String activityId = execution.getActivityId();
			ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(execution.getProcessDefinitionId());
			ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
			if (activity != null && activity.getActivityBehavior() instanceof ReceiveTaskActivityBehavior) {
				runtimeService.signal(execution.getId());
			}
		}
	}

	@Override
	public List<NodeRef> getDocumentsWithActiveTasks(String employeeLogin, Set<String> workflowIds, Integer remainingDays) {
		Set<NodeRef> documents = new HashSet<NodeRef>();

		List<WorkflowTask> tasks = getAssignedAndPooledTasks(employeeLogin);
		for (WorkflowTask task : tasks) {
			if (workflowIds == null || workflowIds.isEmpty() || workflowIds.contains(task.getDefinition().getId())) {
				NodeRef doc = getTaskDocument(task, null);
				if (doc != null) {
					if (remainingDays == null) {
						documents.add(doc);
					} else {
						Date dueDate = (Date) task.getProperties().get(WorkflowModel.PROP_DUE_DATE);
						if (dueDate != null) {
							int countDays = (int) ((dueDate.getTime() - (new Date()).getTime()) / (1000 * 60 * 60 * 24));
							if (countDays < remainingDays) {
								documents.add(doc);
							}
						}
					}
				}
			}
		}
		return new ArrayList<>(documents);
	}

	@Override
	public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef, boolean isActive) {
		boolean hasPermission = lecmPermissionService.hasPermission(LecmPermissionService.PERM_WF_LIST, nodeRef);
		if (!hasPermission) {
			return new ArrayList<WorkflowInstance>();
		}

		List<WorkflowInstance> activeWorkflows = serviceRegistry.getWorkflowService().getWorkflowsForContent(nodeRef, isActive);
		String procesId = (String) serviceRegistry.getNodeService().getProperty(nodeRef, StatemachineModel.PROP_STATEMACHINE_ID);
		List<WorkflowInstance> result = new ArrayList<WorkflowInstance>();
		NodeRef workflowSysUser = serviceRegistry.getPersonService().getPerson("workflow");
		for (WorkflowInstance instance : activeWorkflows) {
			if (!workflowSysUser.equals(instance.getInitiator()) && !isServiceWorkflow(instance) && !instance.getId().equals(procesId)) {
				result.add(instance);
			}
		}
		return result;
	}

//  @Override
//  public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef) {
//      return getActiveWorkflows(nodeRef);
//  }
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName, Task task) {
		boolean result = false;
		if (task != null) {
			String smName = task.getProcessDefinitionId();
			String version = smName.substring(smName.indexOf(":") + 1, smName.lastIndexOf(":"));
			smName = smName.substring(0, smName.indexOf(":"));
			Map<String, LecmPermissionGroup> privaleges = getStateMecheneByName(smName).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(task.getName()).getDynamicRoles();
			LecmPermissionGroup group = privaleges.get(roleName);
			if (group != null) {
				lecmPermissionService.grantDynamicRole(roleName, document, employee.getId(), group);
				result = true;
			}
		}
		return result;
	}

	public boolean isServiceWorkflow(String executionId) {
		WorkflowInstance workflow = serviceRegistry.getWorkflowService().getWorkflowById(executionId);
		if (workflow != null) {
			List<AspectDefinition> aspects = workflow.getDefinition().getStartTaskDefinition().getMetadata().getDefaultAspects();
			if (aspects != null) {
				for (AspectDefinition aspect : aspects) {
					if (aspect.getName().equals(StatemachineModel.ASPECT_IS_SYSTEM_WORKFLOW)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String startUserWorkflowProcessing(final String taskId, final String workflowId, final String assignee) {
		final String user = AuthenticationUtil.getFullyAuthenticatedUser();
		WorkflowService workflowService = serviceRegistry.getWorkflowService();
		WorkflowTask task = workflowService.getTaskById(ACTIVITI_PREFIX + taskId);

		Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>(16);
		NodeRef wfPackage = (NodeRef) task.getProperties().get(WorkflowModel.ASSOC_PACKAGE);

		NodeService nodeService = serviceRegistry.getNodeService();
		List<ChildAssociationRef> documents = nodeService.getChildAssocs(wfPackage);

		NodeRef subprocessPackage = workflowService.createPackage(null);
		NodeRef documentRef = null;
		for (ChildAssociationRef document : documents) {
			nodeService.addChild(subprocessPackage, document.getChildRef(), ContentModel.ASSOC_CONTAINS, document.getQName());
			documentRef = document.getChildRef();
		}
		workflowProps.put(WorkflowModel.ASSOC_PACKAGE, subprocessPackage);
		//workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, groupRef);
		AssignExecution assignExecution = new AssignExecution();
		assignExecution.execute(assignee);
		NodeRef person = assignExecution.getNodeRefResult();
		if (person == null) {
			return null;
		}
		workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, person);

		/*
		 List<NodeRef> assignees = Arrays.asList(personManager.get(USER2), personManager.get(USER3));
		 params.put(WorkflowModel.ASSOC_ASSIGNEES, (Serializable) assignees);
		 */

		/*Set<NodeRef> persons = assignExecution.getRealPersons(assignee);
		 if (persons.size() > 1) {
		 //workflowProps.put(WorkflowModel.ASSOC_ASSIGNEES, persons);
		 } else if (persons.size() > 0) {
		 for (NodeRef person : persons) {
		 workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, person);
		 };
		 }*/
        //if (!async) {
		//	workflowProps.put(QName.createQName("{}" + PROP_PARENT_PROCESS_ID), Long.valueOf(taskId));
		//}
		// get the moderated workflow
		WorkflowDefinition wfDefinition = workflowService.getDefinitionByName(workflowId);
		if (wfDefinition == null) {
			throw new IllegalStateException("noworkflow: " + workflowId);
		}

		// start the workflow
		WorkflowPath path = workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
		String instanceId = path.getInstance().getId();
		WorkflowTask startTask = workflowService.getStartTask(instanceId);
		workflowService.endTask(startTask.getId(), null);

		//Добавляем участников к документу.
		List<NodeRef> assignees = getAssigneesForWorkflow(instanceId);
		final NodeRef packageDocument = documentRef;
		for (final NodeRef assigneeRef : assignees) {
			AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					//TODO transaction in loop!!!
					RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
					return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							return documentMembersService.addMember(packageDocument, assigneeRef, new HashMap<QName, Serializable>());
						}
					});
				}
			});
		}

		return instanceId;

	}

	public String getArchiveFolderPath(final String archiveFolderPath) {
		return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<String>() {
			@Override
			public String doWork() throws Exception {
				String result = null;
				try {
					NodeService nodeService = serviceRegistry.getNodeService();
					NodeRef folderRef = repositoryHelper.getCompanyHome();
					StringTokenizer tokenizer = new StringTokenizer(archiveFolderPath, "/");
					while (tokenizer.hasMoreTokens()) {
						String folderName = tokenizer.nextToken();
						if (!"".equals(folderName)) {
							folderRef = nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, folderName);
						}
					}
					result = nodeService.getPath(folderRef).toPrefixString(serviceRegistry.getNamespaceService());
				} catch (Exception e) {
					logger.warn("Archive folder \"" + archiveFolderPath + "\" removed or access denied");
				}
				return result;
			}
		});
	}

	public Set<String> getArchiveFolders(String documentType) {
		HashSet<String> folders = new HashSet<String>();
		String statmachene = documentType.replace(":", "_");
		String archiveFolder = getStateMecheneByName(statmachene).getLastVersion().getSettings().getSettingsContent().getArchiveFolder();
		if (archiveFolder != null && !"".equals(archiveFolder)) {
			archiveFolder = getArchiveFolderPath(archiveFolder);
			folders.add(archiveFolder);
		}

//        List<WorkflowDefinition> definitions = serviceRegistry.getWorkflowService().getAllDefinitionsByName(ACTIVITI_PREFIX + type);
//        for (WorkflowDefinition definition : definitions) {
////            List<WorkflowInstance> instances = serviceRegistry.getWorkflowService().getWorkflows(definition.getId());
////            if (instances.size() > 0) {
//            ProcessDefinitionEntity processDefinitionEntity;
//            try {
//                processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(definition.getId().replace(ACTIVITI_PREFIX, ""));
//            } catch (ActivitiException e) {
//                continue;
//            }
//                List<ActivityImpl> activities = processDefinitionEntity.getActivities();
//            if (activities != null && !activities.isEmpty()) {
//                for (ActivityImpl activity : activities) {
//                    if (activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior) {
//                        List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
//                        if (listeners != null) {
//                            for (ExecutionListener listener : listeners) {
//                                if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
//                                    List<StateMachineAction> result = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("end");
//                                    if (result != null) {
//                                    for (StateMachineAction action : result) {
//                                        if (action.getActionName().equalsIgnoreCase(StateMachineActionsImpl.getActionNameByClass(ArchiveDocumentAction.class))) {
//                                            ArchiveDocumentAction archiveDocumentAction = (ArchiveDocumentAction) action;
//                                            String archiveFolderPath = archiveDocumentAction.getArchiveFolderPath();
//                                            if (archiveFolderPath != null) {
//                                                folders.add(archiveFolderPath);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
////            }
//        }
//
		TypeDefinition documentTypeQName = serviceRegistry.getDictionaryService().getType(QName.createQName(documentType, serviceRegistry.getNamespaceService()));
		for (QName qName : documentTypeQName.getDefaultAspectNames()) {
			if (DocumentService.ASPECT_FINALIZE_TO_UNIT.equals(qName)) {
				NodeRef companyHome = repositoryHelper.getCompanyHome();
				NodeRef companyArchive = serviceRegistry.getNodeService().getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, OrgstructureBean.DOCUMENT_ROOT_NAME);
				if (companyArchive != null) {
					folders.add(serviceRegistry.getNodeService().getPath(companyArchive).toPrefixString(serviceRegistry.getNamespaceService()));
				}
				break;
			}
		}
		return folders;
	}

	private NodeRef serviceRoot = null;
	private NodeRef versionsRoot = null;
	private List<StateMachene> statemachenes = new ArrayList<StateMachene>();

	public NodeRef getServiceRoot() {
		if (serviceRoot == null) {
			serviceRoot = repositoryHelper.findNodeRef("path", "workspace/SpacesStore/Company Home/Business platform/LECM/statemachines".split("/"));
		}
		return serviceRoot;
	}

	public NodeRef getVersionsRoot() {
		if (versionsRoot == null) {
			versionsRoot = serviceRegistry.getNodeService().getChildByName(getServiceRoot(), ContentModel.ASSOC_CONTAINS, "versions");
		}
		return versionsRoot;
	}

	public void resetStateMachene() {
		statemachenes = new ArrayList<StateMachene>();
	}

	public List<StateMachene> getStateMechenes() {
		if (statemachenes.size() == 0) {
			NodeRef versionsRoot = getVersionsRoot();
			//Проверяем versionsRoot, если существует, то хотя бы одна машина была развернута в системе
			if (versionsRoot != null) {
				List<ChildAssociationRef> statemacheneRefs = serviceRegistry.getNodeService().getChildAssocs(versionsRoot);
				for (ChildAssociationRef child : statemacheneRefs) {
					statemachenes.add(new StateMachene(child.getChildRef()));
				}
			}
		}
		return statemachenes;
	}

	public StateMachene getStateMecheneByName(final String name) {
		return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<StateMachene>() {
			@Override
			public StateMachene doWork() throws Exception {
				for (StateMachene sm : getStateMechenes()) {
					if (name.equals(sm.getName())) {
						return sm;
					}
				}
				return new StateMachene();
			}
		});
	}

	public class StateMachene {

		private String name = null;
		private QName lastVersionQN = QName.createQName("lecm-stmeditor:last_version", serviceRegistry.getNamespaceService());
		private QName nameQN = QName.createQName("cm:name", serviceRegistry.getNamespaceService());
		private NodeRef nodeRef = null;
		private Map<String, StateMacheneVersion> versions = new HashMap<String, StateMacheneVersion>();

		public StateMachene() {

		}

		public StateMachene(NodeRef nodeRef) {
			this.nodeRef = nodeRef;
		}

		//

		public String getName() {
			if (nodeRef == null) {
				return null;
			}
			if (name == null) {
				name = (String) serviceRegistry.getNodeService().getProperty(nodeRef, nameQN);
			}
			return name;
		}

		public Long getLastVersionNumber() {
			return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Long>() {
				@Override
				public Long doWork() throws Exception {
					if (nodeRef == null) {
						return null;
					}
					return (Long) serviceRegistry.getNodeService().getProperty(nodeRef, lastVersionQN);
				}
			});
		}

		public Map<String, StateMacheneVersion> getVersions() {
			return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Map<String, StateMacheneVersion>>() {
				@Override
				public Map<String, StateMacheneVersion> doWork() throws Exception {
					if (versions.get("version_" + getLastVersionNumber()) == null && nodeRef != null) {
						List<ChildAssociationRef> versionsRefs = serviceRegistry.getNodeService().getChildAssocs(nodeRef);
						for (ChildAssociationRef child : versionsRefs) {
							String ver = (String) serviceRegistry.getNodeService().getProperty(child.getChildRef(), nameQN);
							versions.put(ver, new StateMacheneVersion(child.getChildRef()));
						}
					}
					return versions;
				}
			});
		}

		public StateMacheneVersion getLastVersion() {
			StateMacheneVersion version = getVersions().get("version_" + getLastVersionNumber());
			if (version == null) {
				return new StateMacheneVersion();
			}
			return version;
		}

		public StateMacheneVersion getVersionByNumber(String number) {
			StateMacheneVersion version = getVersions().get("version_" + number);
			if (version == null) {
				return new StateMacheneVersion();
			}
			return version;
		}
	}

	public class StateMacheneVersion {

		private NodeRef versionRef = null;
		private StateMacheneSettings settings = null;

		public StateMacheneVersion() {

		}

		public StateMacheneVersion(NodeRef versionRef) {
			this.versionRef = versionRef;
		}

		public StateMacheneSettings getSettings() {
			return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<StateMacheneSettings>() {
				@Override
				public StateMacheneSettings doWork() throws Exception {
					if (versionRef == null) {
						return new StateMacheneSettings();
					}
					if (settings == null) {
						NodeRef settingsRef = serviceRegistry.getNodeService().getChildByName(versionRef, ContentModel.ASSOC_CONTAINS, "backup.xml");
						settings = new StateMacheneSettings(settingsRef);
					}
					return settings;
				}
			});
		}
	}

	public class StateMacheneSettings {

		private NodeRef settingsRef = null;
		private String settings = null;
		private String archiveFolder = null;
		private String archiveFolderAdditional = null;
		private Map<String, StateMachineStatus> statuses = new HashMap<String, StateMachineStatus>();
		private Map<String, StateMachineStatus> finalStatuses = new HashMap<String, StateMachineStatus>();
		private List<String> staticRoles = new ArrayList<String>();
		private List<String> dinamicRoles = new ArrayList<String>();
		private List<String> starterRoles = new ArrayList<String>();
		private boolean notArmCreate = false;

		private boolean initialized = false;
		private Lock lock = new ReentrantLock();

		public StateMacheneSettings() {

		}

		public StateMacheneSettings(NodeRef settingsRef) {
			this.settingsRef = settingsRef;
		}

		public StateMacheneSettings getSettingsContent() {
			final StateMacheneSettings self = this;
			if (initialized) {
				return self;
			}
			return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<StateMacheneSettings>() {
				@Override
				public StateMacheneSettings doWork() throws Exception {
					if (settings == null && settingsRef != null) {
						lock.lock();
						if (initialized) {
							return self;
						}
						try {
							ContentReader reader = serviceRegistry.getContentService().getReader(settingsRef, ContentModel.PROP_CONTENT);
							DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
							dbfac.setNamespaceAware(true);
							DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
							Document doc = docBuilder.parse(reader.getContentInputStream());
							Node stateMachine = doc.getFirstChild();
							stateMachine = stateMachine.getFirstChild();
							NodeList stateMachineNodes = stateMachine.getChildNodes();
							for (int i = 0; i < stateMachineNodes.getLength(); i++) {
								Node stateMachineNode = stateMachineNodes.item(i);
								if ("name".equals(stateMachineNode.getLocalName())) {
									//TODO settings = stateMachineNode.getTextContent();
								}
								if ("properties".equals(stateMachineNode.getLocalName())) {
									NodeList stateMachineProps = stateMachineNode.getChildNodes();
									for (int h = 0; h < stateMachineProps.getLength(); h++) {
										Node stateMachineProp = stateMachineProps.item(h);
										if ("name".equals(stateMachineProp.getFirstChild().getLocalName()) && "archiveFolder".equals(stateMachineProp.getFirstChild().getTextContent())) {
											archiveFolder = stateMachineProp.getLastChild().getTextContent();
										}
										if ("name".equals(stateMachineProp.getFirstChild().getLocalName()) && "archiveFolderAdditional".equals(stateMachineProp.getFirstChild().getTextContent())) {
											archiveFolderAdditional = stateMachineProp.getLastChild().getTextContent();
										}
										if ("name".equals(stateMachineProp.getFirstChild().getLocalName()) && "notArmCreate".equals(stateMachineProp.getFirstChild().getTextContent())) {
											notArmCreate = Boolean.valueOf(stateMachineProp.getLastChild().getTextContent());
										}
									}
								}
								if ("subFolders".equals(stateMachineNode.getLocalName())) {
									NodeList stateMachineSubs = stateMachineNode.getChildNodes();
									for (int j = 0; j < stateMachineSubs.getLength(); j++) {
										Node stateMachineSub = stateMachineSubs.item(j);
										if ("name".equals(stateMachineSub.getFirstChild().getLocalName())
												&& "roles-list".equals(stateMachineSub.getFirstChild().getTextContent())) {
											if ("nodes".equals(stateMachineSub.getLastChild().getLocalName())) {
												NodeList rolesListSubs = stateMachineSub.getLastChild().getChildNodes();
												for (int k = 0; k < rolesListSubs.getLength(); k++) {
													Node rolesListSub = rolesListSubs.item(k);
													NodeList rolesListProps = rolesListSub.getChildNodes();
													Boolean isCreator = false;
													Boolean isStatic = false;
													String roleName = "";
													for (int l = 0; l < rolesListProps.getLength(); l++) {
														Node rolesListProp = rolesListProps.item(l);
														if ("type".equals(rolesListProp.getLocalName()) && "static-role-item".equals(rolesListProp.getTextContent())) {
															isStatic = true;
														}
														if ("properties".equals(rolesListProp.getLocalName()) && rolesListProp.getFirstChild() != null) {
															if ("name".equals(rolesListProp.getFirstChild().getFirstChild().getLocalName()) && "isCreator".equals(rolesListProp.getFirstChild().getFirstChild().getTextContent())) {
																isCreator = Boolean.parseBoolean(rolesListProp.getFirstChild().getLastChild().getTextContent());
															}
														}
														if ("roleAssociations".equals(rolesListProp.getLocalName()) && rolesListProp.getFirstChild() != null) {
															if ("type".equals(rolesListProp.getFirstChild().getFirstChild().getLocalName()) && "role-assoc".equals(rolesListProp.getFirstChild().getFirstChild().getTextContent())) {
																roleName = rolesListProp.getFirstChild().getLastChild().getTextContent();
															}
														}
													}
													if (isStatic) {
														staticRoles.add(roleName);
													}
													if (!isStatic) {
														dinamicRoles.add(roleName);
													}
													if (isCreator) {
														starterRoles.add(roleName);
													}
												}
											}
										}
										if ("name".equals(stateMachineSub.getFirstChild().getLocalName())
												&& "statuses".equals(stateMachineSub.getFirstChild().getTextContent())) {
											if ("nodes".equals(stateMachineSub.getLastChild().getLocalName())) {
												NodeList statusNodesSubs = stateMachineSub.getLastChild().getChildNodes();
												for (int k = 0; k < statusNodesSubs.getLength(); k++) {
													Node statusNodesSub = statusNodesSubs.item(k);
													NodeList statusesSubs = statusNodesSub.getChildNodes();
													boolean finalStatus = false;
													StateMachineStatus st = null;
													for (int l = 0; l < statusesSubs.getLength(); l++) {
														Node statusesSub = statusesSubs.item(l);
														if ("type".equals(statusesSub.getLocalName())) {
															if ("endEvent".equals(statusesSub.getTextContent())) {
																finalStatus = true;
															}
														}
														if ("name".equals(statusesSub.getLocalName())) {
															st = new StateMachineStatus(statusesSub.getTextContent());
															if (!finalStatus) {
																statuses.put(statusesSub.getTextContent(), st);
															}
															if (finalStatus) {
																finalStatuses.put(statusesSub.getTextContent(), st);
															}
														}
														if ("subFolders".endsWith(statusesSub.getLocalName())) {
															NodeList statusesSubList = statusesSub.getChildNodes();
															for (int m = 0; m < statusesSubList.getLength(); m++) {
																Node statusesSubNode = statusesSubList.item(m);
																if ("name".equals(statusesSubNode.getFirstChild().getLocalName())
																		&& "fields".equals(statusesSubNode.getFirstChild().getTextContent())) {
																	Node fieldsRoot = statusesSubNode.getLastChild();
																	NodeList fieldsNodes = fieldsRoot.getChildNodes();
																	for (int o = 0; o < fieldsNodes.getLength(); o++) {
																		Node fieldsNode = fieldsNodes.item(o);
																		NodeList fieldsN = fieldsNode.getChildNodes();
																		String fieldName = "";
																		Boolean editable = false;
																		for (int p = 0; p < fieldsN.getLength(); p++) {
																			Node fieldsSub = fieldsN.item(p);
																			if ("name".equals(fieldsSub.getLocalName()) && fieldsSub.getFirstChild() != null) {
																				fieldName = fieldsSub.getTextContent();
																			}
																			if ("properties".equals(fieldsSub.getLocalName()) && fieldsSub.getFirstChild() != null) {
																				editable = Boolean.parseBoolean(fieldsSub.getFirstChild().getLastChild().getTextContent());
																			}
																		}
																		st.getFields().add(new StateFieldImpl(fieldName, editable));
																	}
																}
																if ("name".equals(statusesSubNode.getFirstChild().getLocalName())
																		&& "categories".equals(statusesSubNode.getFirstChild().getTextContent())) {
																	Node categoriesRoot = statusesSubNode.getLastChild();
																	NodeList categoriesNodes = categoriesRoot.getChildNodes();
																	for (int o = 0; o < categoriesNodes.getLength(); o++) {
																		Node categoriesNode = categoriesNodes.item(o);
																		NodeList categoriesN = categoriesNode.getChildNodes();
																		String categoryName = "";
																		Boolean editable = false;
																		for (int p = 0; p < categoriesN.getLength(); p++) {
																			Node categoriesSub = categoriesN.item(p);
																			if ("name".equals(categoriesSub.getLocalName()) && categoriesSub.getFirstChild() != null) {
																				categoryName = categoriesSub.getTextContent();
																			}
																			if ("properties".equals(categoriesSub.getLocalName()) && categoriesSub.getFirstChild() != null) {
																				editable = Boolean.parseBoolean(categoriesSub.getFirstChild().getLastChild().getTextContent());
																			}
																		}
																		st.getCategories().add(new StateFieldImpl(categoryName, editable));
																	}
																}
																if ("name".equals(statusesSubNode.getFirstChild().getLocalName())
																		&& "staticRoles".equals(statusesSubNode.getFirstChild().getTextContent())) {
																	Node staticRolesRoot = statusesSubNode.getLastChild();
																	NodeList staticRolesNodes = staticRolesRoot.getChildNodes();
																	for (int o = 0; o < staticRolesNodes.getLength(); o++) {
																		Node staticRolesNode = staticRolesNodes.item(o);
																		NodeList staticRolesN = staticRolesNode.getChildNodes();
																		String br_name = "";
																		String permissionTypeValue = "";
																		for (int p = 0; p < staticRolesN.getLength(); p++) {
																			Node staticRolesSub = staticRolesN.item(p);
																			if ("properties".equals(staticRolesSub.getLocalName()) && staticRolesSub.getFirstChild() != null) {
																				permissionTypeValue = staticRolesSub.getFirstChild().getLastChild().getTextContent();
																			}
																			if ("roleAssociations".equals(staticRolesSub.getLocalName()) && staticRolesSub.getFirstChild() != null) {
																				br_name = staticRolesSub.getFirstChild().getLastChild().getTextContent();
																			}
																		}
																		LecmPermissionGroup permissionGroup = lecmPermissionService.findPermissionGroup(permissionTypeValue);
																		st.getStaticRoles().put(br_name, permissionGroup);
																	}
																}
																if ("name".equals(statusesSubNode.getFirstChild().getLocalName())
																		&& "dynamicRoles".equals(statusesSubNode.getFirstChild().getTextContent())) {
																	Node dynamicRolesRoot = statusesSubNode.getLastChild();
																	NodeList dynamicRolesNodes = dynamicRolesRoot.getChildNodes();
																	for (int o = 0; o < dynamicRolesNodes.getLength(); o++) {
																		Node dynamicRolesNode = dynamicRolesNodes.item(o);
																		NodeList dynamicRolesN = dynamicRolesNode.getChildNodes();
																		String br_name = "";
																		String permissionTypeValue = "";
																		for (int p = 0; p < dynamicRolesN.getLength(); p++) {
																			Node dynamicRolesSub = dynamicRolesN.item(p);
																			if ("properties".equals(dynamicRolesSub.getLocalName()) && dynamicRolesSub.getFirstChild() != null) {
																				permissionTypeValue = dynamicRolesSub.getFirstChild().getLastChild().getTextContent();
																			}
																			if ("roleAssociations".equals(dynamicRolesSub.getLocalName()) && dynamicRolesSub.getFirstChild() != null) {
																				br_name = dynamicRolesSub.getFirstChild().getLastChild().getTextContent();
																			}
																		}
																		LecmPermissionGroup permissionGroup = lecmPermissionService.findPermissionGroup(permissionTypeValue);
																		st.getDynamicRoles().put(br_name, permissionGroup);
																	}
																}
																if ("name".equals(statusesSubNode.getFirstChild().getLocalName())
																		&& "actions".equals(statusesSubNode.getFirstChild().getTextContent())) {
																	Node actionsRoot = statusesSubNode.getLastChild();
																	NodeList actionsNodes = actionsRoot.getChildNodes();
																	for (int o = 0; o < actionsNodes.getLength(); o++) {
																		Node actionsNode = actionsNodes.item(o);
																		NodeList actionsN = actionsNode.getChildNodes();
																		String type = "";
																		String actionVar = "";
																		for (int p = 0; p < actionsN.getLength(); p++) {
																			Node actionsSub = actionsN.item(p);
																			if ("type".equals(actionsSub.getLocalName())) {
																				type = actionsSub.getTextContent();
																			}
																			if ("nodeRef".equals(actionsSub.getLocalName())) {
																				actionVar = "id" + actionsSub.getTextContent().replace("workspace://SpacesStore/", "").replace("-", "");
																				st.addStatusVar("var" + actionVar);
																			}
																			if ("FinishStateWithTransition".equals(type) && "subFolders".equals(actionsSub.getLocalName())) {
																				if (actionsSub.getFirstChild() != null && "name".equals(actionsSub.getFirstChild().getFirstChild().getLocalName())
																						&& "transitions".equals(actionsSub.getFirstChild().getFirstChild().getTextContent())) {
																					NodeList nl7 = actionsSub.getFirstChild().getLastChild().getChildNodes();
																					for (int q = 0; q < nl7.getLength(); q++) {
																						Node n9 = nl7.item(q);
																						NodeList nl8 = n9.getChildNodes();
																						Boolean stopSubWorkflows = false;
																						String transitionLabel = "";
																						String workflowId = null;
																						String script = null;
																						String transitionVar = "";

																						String transitionFormType = null;
																						Boolean transitionIsSystemFormConnection = true;
																						Boolean transitionIsReverseFormConnection = false;
																						Boolean autoFill = false;
																						String transitionFormFolder = null;
																						String transitionFormConnection = null;

																						Conditions cc = new Conditions();
																						WorkflowVariables wv = new WorkflowVariables();
																						//type - userTransition, transitionWorkflow, transitionForm!
																						for (int r = 0; r < nl8.getLength(); r++) {
																							Node n10 = nl8.item(r);
																							if ("properties".equals(n10.getLocalName())) {
																								NodeList nl9 = n10.getChildNodes();
																								for (int s = 0; s < nl9.getLength(); s++) {
																									Node n11 = nl9.item(s);
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "stopSubWorkflows".equals(n11.getFirstChild().getTextContent())) {
																										stopSubWorkflows = Boolean.parseBoolean(n11.getLastChild().getTextContent());
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionLabel".equals(n11.getFirstChild().getTextContent())) {
																										transitionLabel = n11.getLastChild().getTextContent();
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "workflowId".equals(n11.getFirstChild().getTextContent())) {
																										workflowId = n11.getLastChild().getTextContent();
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "transition-script".equals(n11.getFirstChild().getTextContent())) {
																										script = n11.getLastChild().getTextContent();
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionFormType".equals(n11.getFirstChild().getTextContent())) {
																										transitionFormType = n11.getLastChild().getTextContent();
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionIsSystemFormConnection".equals(n11.getFirstChild().getTextContent())) {
																										transitionIsSystemFormConnection = Boolean.parseBoolean(n11.getLastChild().getTextContent());
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionIsReverseFormConnection".equals(n11.getFirstChild().getTextContent())) {
																										transitionIsReverseFormConnection = Boolean.parseBoolean(n11.getLastChild().getTextContent());
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "document-autofill-enabled".equals(n11.getFirstChild().getTextContent())) {
																										autoFill = Boolean.parseBoolean(n11.getLastChild().getTextContent());
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionFormFolder".equals(n11.getFirstChild().getTextContent())) {
																										transitionFormFolder = n11.getLastChild().getTextContent();
																									} else if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionFormConnection".equals(n11.getFirstChild().getTextContent())) {
																										transitionFormConnection = n11.getLastChild().getTextContent();
																									}
																								}
																							}
																							if ("associations".equals(n10.getLocalName())) {
																								NodeList nl10 = n10.getChildNodes();
																								for (int s = 0; s < nl10.getLength(); s++) {
																									Node n12 = nl10.item(s);
																									if ("type".equals(n12.getFirstChild().getLocalName()) && "transitionStatus".equals(n12.getFirstChild().getTextContent())) {
																										transitionVar = "id" + n12.getLastChild().getTextContent().replace("workspace://SpacesStore/", "").replace("-", "");
																									}
																								}
																							}
																							if ("subFolders".equals(n10.getLocalName())) {
																								if (n10.getFirstChild() != null && "subFolder".equals(n10.getFirstChild().getLocalName())) {
																									//variables
																									NodeList nl11 = n10.getFirstChild().getLastChild().getChildNodes();
																									for (int t = 0; t < nl11.getLength(); t++) {
																										Node n11 = nl11.item(t);//<node>conditionAccess, inputVariable, outputVariable, inputFormVariable
																										NodeList nl12 = n11.getChildNodes();

																										String expression = "";
																										String errorMessage = "";
																										String inputFromType = "";
																										String inputToType = "";
																										String inputToValue = "";
																										String inputFromValue = "";
																										String outputFromType = "";
																										String outputToValue = "";
																										String outputToType = "";
																										String outputFromValue = "";
																										String formInputFromValue = "";
																										String formInputFromType = "";
																										String formInputToValue = "";

																										boolean conditionAccess = false;
																										boolean hideAction = false;
																										boolean doNotBlock = false;
																										boolean inputVariables = false;
																										boolean formInputVariables = false;
																										boolean outputVariables = false;

																										for (int u = 0; u < nl12.getLength(); u++) {
																											Node n12 = nl12.item(u);
																											if ("type".equals(n12.getLocalName()) && "conditionAccess".equals(n12.getTextContent())) {
																												conditionAccess = true;
																											} else if ("type".equals(n12.getLocalName()) && "inputVariable".equals(n12.getTextContent())) {
																												inputVariables = true;
																											} else if ("type".equals(n12.getLocalName()) && "inputFormVariable".equals(n12.getTextContent())) {
																												formInputVariables = true;
																											} else if ("type".equals(n12.getLocalName()) && "outputVariable".equals(n12.getTextContent())) {
																												outputVariables = true;
																											} else if ("properties".equals(n12.getLocalName())) {
																												NodeList nl13 = n12.getChildNodes();
																												for (int y = 0; y < nl13.getLength(); y++) {
																													Node n13 = nl13.item(y);
																													if ("name".equals(n13.getFirstChild().getLocalName()) && "hideAction".equals(n13.getFirstChild().getTextContent())) {
																														hideAction = Boolean.parseBoolean(n13.getLastChild().getTextContent());
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "conditionErrorMessage".equals(n13.getFirstChild().getTextContent())) {
																														errorMessage = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "does-not-block".equals(n13.getFirstChild().getTextContent())) {
																														doNotBlock = Boolean.parseBoolean(n13.getLastChild().getTextContent());
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "condition".equals(n13.getFirstChild().getTextContent())) {
																														expression = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputFromType".equals(n13.getFirstChild().getTextContent())) {
																														inputFromType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputToType".equals(n13.getFirstChild().getTextContent())) {
																														inputToType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputToValue".equals(n13.getFirstChild().getTextContent())) {
																														inputToValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputFromValue".equals(n13.getFirstChild().getTextContent())) {
																														inputFromValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputFromType".equals(n13.getFirstChild().getTextContent())) {
																														outputFromType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputToValue".equals(n13.getFirstChild().getTextContent())) {
																														outputToValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputToType".equals(n13.getFirstChild().getTextContent())) {
																														outputToType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputFromValue".equals(n13.getFirstChild().getTextContent())) {
																														outputFromValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "formInputFromValue".equals(n13.getFirstChild().getTextContent())) {
																														formInputFromValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "formInputFromType".equals(n13.getFirstChild().getTextContent())) {
																														formInputFromType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "formInputToValue".equals(n13.getFirstChild().getTextContent())) {
																														formInputToValue = n13.getLastChild().getTextContent();
																													}

																												}
																											}
																										}
																										if (conditionAccess) {
																											cc.addCondition(expression, errorMessage, hideAction, doNotBlock);
																										} else if (inputVariables) {
																											wv.addInput(inputFromType, inputFromValue, inputToType, inputToValue);
																										} else if (formInputVariables) {
																											wv.addInput(formInputFromType, formInputFromValue, "VARIABLE", formInputToValue);
																										} else if (outputVariables) {
																											wv.addOutput(outputFromType, outputFromValue, outputToType, outputToValue);
																										}
																									}
																								}
																							}
																						}
																						if (st.getActions().get("FinishStateWithTransition") == null) {
																							st.getActions().put("FinishStateWithTransition", new FinishStateWithTransitionAction());
																						}
																						((FinishStateWithTransitionAction) st.getActions().get("FinishStateWithTransition")).addState(actionVar + (q + 1), transitionLabel, workflowId, cc, "var" + actionVar, transitionVar, wv, stopSubWorkflows, transitionFormType, transitionFormFolder, transitionFormConnection, transitionIsSystemFormConnection, transitionIsReverseFormConnection, autoFill, script);
																					}

																				}
																			}
																			if ("WaitForDocumentChange".equals(type) && "subFolders".equals(actionsSub.getLocalName())) {
																				if (actionsSub.getFirstChild() != null && "name".equals(actionsSub.getFirstChild().getFirstChild().getLocalName())
																						&& "transitions".equals(actionsSub.getFirstChild().getFirstChild().getTextContent())) {
																					NodeList nl7 = actionsSub.getFirstChild().getLastChild().getChildNodes();
																					for (int q = 0; q < nl7.getLength(); q++) {
																						Node n9 = nl7.item(q);
																						NodeList nl8 = n9.getChildNodes();
																						Boolean stopSubWorkflows = false;
																						String transitionExpression = "";
																						String transitionDocumentChangeScript = null;
																						String script = null;
																						String transitionVar = "";
																						for (int r = 0; r < nl8.getLength(); r++) {
																							Node n10 = nl8.item(r);
																							if ("properties".equals(n10.getLocalName())) {
																								NodeList nl9 = n10.getChildNodes();
																								for (int s = 0; s < nl9.getLength(); s++) {
																									Node n11 = nl9.item(s);
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "stopSubWorkflows".equals(n11.getFirstChild().getTextContent())) {
																										stopSubWorkflows = Boolean.parseBoolean(n11.getLastChild().getTextContent());
																									}
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionExpression".equals(n11.getFirstChild().getTextContent())) {
																										transitionExpression = n11.getLastChild().getTextContent();
																									}
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionDocumentChangeScript".equals(n11.getFirstChild().getTextContent())) {
																										transitionDocumentChangeScript = n11.getLastChild().getTextContent();
																									}
																								}
																							}
																							if ("associations".equals(n10.getLocalName())) {
																								NodeList nl10 = n10.getChildNodes();
																								for (int s = 0; s < nl10.getLength(); s++) {
																									Node n12 = nl10.item(s);
																									if ("type".equals(n12.getFirstChild().getLocalName()) && "transitionStatus".equals(n12.getFirstChild().getTextContent())) {
																										transitionVar = "id" + n12.getLastChild().getTextContent().replace("workspace://SpacesStore/", "").replace("-", "");
																									}
																								}
																							}
																						}
																						if (st.getActions().get("WaitForDocumentChange") == null) {
																							st.getActions().put("WaitForDocumentChange", new WaitForDocumentChangeAction());
																						}
																						((WaitForDocumentChangeAction) st.getActions().get("WaitForDocumentChange")).addExpression(transitionExpression, "var" + actionVar, transitionVar, stopSubWorkflows, transitionDocumentChangeScript);
																					}

																				}
																			}
																			if ("StatusChange".equals(type) && "subFolders".equals(actionsSub.getLocalName())) {
																				if (st.getActions().get("StatusChange") == null) {
																					st.getActions().put("StatusChange", new StatusChangeAction());
																				}
																			}
																			if ("TransitionAction".equals(type) && "subFolders".equals(actionsSub.getLocalName())) {
																				Node mm = actionsSub.getLastChild();
																				if (actionsSub.getFirstChild() != null && "name".equals(actionsSub.getFirstChild().getFirstChild().getLocalName())
																						&& "transitions".equals(actionsSub.getFirstChild().getFirstChild().getTextContent())) {
																					NodeList nl7 = actionsSub.getFirstChild().getLastChild().getChildNodes();
																					for (int q = 0; q < nl7.getLength(); q++) {
																						Node n9 = nl7.item(q);
																						NodeList nl8 = n9.getChildNodes();
																						Boolean stopSubWorkflows = false;
																						String transitionExpression = "";
																						String variableName = "";
																						for (int r = 0; r < nl8.getLength(); r++) {
																							Node n10 = nl8.item(r);
																							if ("nodeRef".equals(n10.getLocalName())) {
																								variableName = "id" + n10.getTextContent().replace("workspace://SpacesStore/", "").replace("-", "");
																							}
																							if ("properties".equals(n10.getLocalName())) {
																								NodeList nl9 = n10.getChildNodes();
																								for (int s = 0; s < nl9.getLength(); s++) {
																									Node n11 = nl9.item(s);
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "stopSubWorkflows".equals(n11.getFirstChild().getTextContent())) {
																										stopSubWorkflows = Boolean.parseBoolean(n11.getLastChild().getTextContent());
																									}
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "transitionExpression".equals(n11.getFirstChild().getTextContent())) {
																										transitionExpression = n11.getLastChild().getTextContent();
																									}
																								}
																							}
																						}
																						if (st.getActions().get("TransitionAction") == null) {
																							st.getActions().put("TransitionAction", new TransitionAction());
																						}
																						((TransitionAction) st.getActions().get("TransitionAction")).addTransition(variableName, transitionExpression, stopSubWorkflows);
																					}
																				}
																			}
																			if ("UserWorkflow".equals(type) && "subFolders".equals(actionsSub.getLocalName())) {
																				Node mm = actionsSub.getLastChild();
																				if (actionsSub.getFirstChild() != null && "name".equals(actionsSub.getFirstChild().getFirstChild().getLocalName())
																						&& "transitions".equals(actionsSub.getFirstChild().getFirstChild().getTextContent())) {
																					NodeList nl7 = actionsSub.getFirstChild().getLastChild().getChildNodes();
																					for (int q = 0; q < nl7.getLength(); q++) {
																						Node n9 = nl7.item(q);
																						NodeList nl8 = n9.getChildNodes();
																						String workflowLabel = "";
																						String workflowId = null;
																						Conditions cc = new Conditions();
																						WorkflowVariables wv = new WorkflowVariables();
																						for (int r = 0; r < nl8.getLength(); r++) {
																							Node n10 = nl8.item(r);
																							if ("properties".equals(n10.getLocalName())) {
																								NodeList nl9 = n10.getChildNodes();
																								for (int s = 0; s < nl9.getLength(); s++) {
																									Node n11 = nl9.item(s);
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "workflowLabel".equals(n11.getFirstChild().getTextContent())) {
																										workflowLabel = n11.getLastChild().getTextContent();
																									}
																									if ("name".equals(n11.getFirstChild().getLocalName()) && "workflowId".equals(n11.getFirstChild().getTextContent())) {
																										workflowId = n11.getLastChild().getTextContent();
																									}
																								}
																							}
																							if ("subFolders".equals(n10.getLocalName())) {
																								if (n10.getFirstChild() != null && "subFolder".equals(n10.getFirstChild().getLocalName())) {
																									NodeList nl11 = n10.getFirstChild().getLastChild().getChildNodes();
																									for (int t = 0; t < nl11.getLength(); t++) {
																										Node n11 = nl11.item(t);//<node>conditionAccess, outputVariable
																										NodeList nl12 = n11.getChildNodes();
																										String expression = "";
																										String errorMessage = "";
																										String inputFromType = "";
																										String inputToType = "";
																										String inputToValue = "";
																										String inputFromValue = "";
																										String outputFromType = "";
																										String outputToValue = "";
																										String outputToType = "";
																										String outputFromValue = "";
																										boolean hideAction = false;
																										boolean doesNotBlock = false;
																										boolean conditionAccess = false;
																										boolean inputVariables = false;
																										boolean outputVariables = false;
																										for (int u = 0; u < nl12.getLength(); u++) {
																											Node n12 = nl12.item(u);
																											if ("type".equals(n12.getLocalName()) && "conditionAccess".equals(n12.getTextContent())) {
																												conditionAccess = true;
																											} else if ("type".equals(n12.getLocalName()) && "inputVariable".equals(n12.getTextContent())) {
																												inputVariables = true;
																											} else if ("type".equals(n12.getLocalName()) && "outputVariable".equals(n12.getTextContent())) {
																												outputVariables = true;
																											} else if ("properties".equals(n12.getLocalName())) {
																												NodeList nl13 = n12.getChildNodes();
																												for (int y = 0; y < nl13.getLength(); y++) {
																													Node n13 = nl13.item(y);
																													if ("name".equals(n13.getFirstChild().getLocalName()) && "hideAction".equals(n13.getFirstChild().getTextContent())) {
																														hideAction = Boolean.parseBoolean(n13.getLastChild().getTextContent());
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "conditionErrorMessage".equals(n13.getFirstChild().getTextContent())) {
																														errorMessage = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "does-not-block".equals(n13.getFirstChild().getTextContent())) {
																														doesNotBlock = Boolean.parseBoolean(n13.getLastChild().getTextContent());
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "condition".equals(n13.getFirstChild().getTextContent())) {
																														expression = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputFromType".equals(n13.getFirstChild().getTextContent())) {
																														inputFromType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputToType".equals(n13.getFirstChild().getTextContent())) {
																														inputToType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputToValue".equals(n13.getFirstChild().getTextContent())) {
																														inputToValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "inputFromValue".equals(n13.getFirstChild().getTextContent())) {
																														inputFromValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputFromType".equals(n13.getFirstChild().getTextContent())) {
																														outputFromType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputToValue".equals(n13.getFirstChild().getTextContent())) {
																														outputToValue = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputToType".equals(n13.getFirstChild().getTextContent())) {
																														outputToType = n13.getLastChild().getTextContent();
																													} else if ("name".equals(n13.getFirstChild().getLocalName()) && "outputFromValue".equals(n13.getFirstChild().getTextContent())) {
																														outputFromValue = n13.getLastChild().getTextContent();
																													}
																												}
																											}
																										}
																										if (conditionAccess) {
																											cc.addCondition(expression, errorMessage, hideAction, doesNotBlock);
																										} else if (inputVariables) {
																											wv.addInput(inputFromType, inputFromValue, inputToType, inputToValue);
																										} else if (outputVariables) {
																											wv.addOutput(outputFromType, outputFromValue, outputToType, outputFromValue);
																										}
																									}
																								}
																							}
																						}
																						if (st.getActions().get("UserWorkflow") == null) {
																							st.getActions().put("UserWorkflow", new UserWorkflow());
																						}
																						((UserWorkflow) st.getActions().get("UserWorkflow")).addUserWorkflow(actionVar + (q + 1), workflowLabel, workflowId, cc, wv);
																					}
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
							initialized = true;
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						} finally {
							lock.unlock();
						}
					}
					return self;
				}
			});
		}

		public Map<String, StateMachineStatus> getStatuses() {
			return statuses;
		}

		public Map<String, StateMachineStatus> getFinalStatuses() {
			return finalStatuses;
		}

		public List<String> getStaticRoles() {
			return staticRoles;
		}

		public List<String> getDinamicRoles() {
			return dinamicRoles;
		}

		public List<String> getStarterRoles() {
			return starterRoles;
		}

		public String getArchiveFolder() {
			return archiveFolder;
		}

		public String getArchiveFolderAdditional() {
			return archiveFolderAdditional;
		}

		public boolean isNotArmCreate() {
			return notArmCreate;
		}

		public StateMachineStatus getStatusByName(String name) {
			if (statuses.get(name) != null) {
				return statuses.get(name);
			}
			if (finalStatuses.get(name) != null) {
				return finalStatuses.get(name);
			}
			return null;
		}
	}

	public class StateMachineStatus {

		private String name;
		private Map<String, StateMachineAction> actions = new HashMap<String, StateMachineAction>();
		Map<String, LecmPermissionGroup> staticRoles = new HashMap<String, LecmPermissionGroup>();
		Map<String, LecmPermissionGroup> dynamicRoles = new HashMap<String, LecmPermissionGroup>();
		Set<StateField> categories = new HashSet<StateField>();
		Set<StateField> fields = new HashSet<StateField>();
		List<String> vars = new ArrayList<String>();

		public StateMachineStatus() {

		}

		public StateMachineStatus(String name) {
			this.name = name;
		}

		public StateMachineAction getActionByName(String actionName) {
			return actions.get(actionName);
		}

		public Map<String, StateMachineAction> getActions() {
			return actions;
		}

		public Map<String, LecmPermissionGroup> getStaticRoles() {
			return staticRoles;
		}

		public Map<String, LecmPermissionGroup> getDynamicRoles() {
			return dynamicRoles;
		}

		public Set<StateField> getCategories() {
			return categories;
		}

		public Set<StateField> getFields() {
			return fields;
		}

		public void addStatusVar(String varName) {
			vars.add(varName);
		}

		public List<String> getStatusVars() {
			return vars;
		}

		public String toString() {
			return name;
		}
	}

	public List<StateMachineAction> getTaskActionsByName(String taskName, String processDefinitionId, String actionType) {
		List<StateMachineAction> result = new ArrayList<StateMachineAction>();
		String smName = processDefinitionId.substring(0, processDefinitionId.indexOf(":"));
		StateMachineAction action = getStateMecheneByName(smName).getLastVersion().getSettings().getSettingsContent().getStatusByName(taskName).getActionByName(actionType);

		if (action != null) {
			result.add(action);
		}

		return result;
	}

	public List<StateMachineAction> getTaskActionsByName(String taskId, String actionType) {
		List<StateMachineAction> result = new ArrayList<StateMachineAction>();
		if (taskId != null) {
			TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
			TaskQuery taskQuery = taskService.createTaskQuery();
			Task task = taskQuery.taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
			if (task != null) {
				String smName = task.getProcessDefinitionId();
				String version = smName.substring(smName.indexOf(":") + 1, smName.lastIndexOf(":"));
				smName = smName.substring(0, smName.indexOf(":"));
				StateMachineAction action = getStateMecheneByName(smName).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(task.getName()).getActionByName(actionType);
				if (action != null) {
					result.add(action);
				}
			}
		}
		return result;
	}

	public List<StateMachineAction> getHistoricalTaskActionsByName(String taskId, String actionType, String onFire) {
		List<StateMachineAction> actions = getHistoricalTaskActions(taskId, onFire);
		List<StateMachineAction> result = new ArrayList<StateMachineAction>();
		for (StateMachineAction action : actions) {
			if (action != null && StringUtils.equals(action.getActionName(), actionType)) {
				result.add(action);
			}
		}
		return result;
	}

	//TODO почему не сразу execution? Используется WaitForDocumentChangeListenerPolicy

	public void setExecutionParamentersByTaskId(String taskId, Map<String, Object> parameters) {
		TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		Task task = taskQuery.taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
		if (task != null) {
			setExecutionParameters(task.getExecutionId(), parameters);
		}
	}

	public void setExecutionParameters(String executionId, Map<String, Object> parameters) {
		for (String key : parameters.keySet()) {
			RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
			runtimeService.setVariable(executionId.replace(ACTIVITI_PREFIX, ""), key, parameters.get(key));
		}
	}

	public String nextTransition(final String taskId) {
		//String currentStatemachine = getCurrentExecutionId(taskId);
		final WorkflowService workflowService = serviceRegistry.getWorkflowService();
		String newTaskId = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<String>() {
			@Override
			public String doWork() throws Exception {
				return workflowService.endTask(taskId, null).getId();
			}
		});
		//executePostponedActions(currentStatemachine);
		return newTaskId;
	}

	public void sendMessage(String messageName, String processId) {
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		Execution executionId = runtimeService.createExecutionQuery().processInstanceId(processId).messageEventSubscriptionName(messageName).singleResult();
		runtimeService.messageEventReceived(messageName, executionId.getId());
	}

	public StateFields getStateFields(NodeRef document) {
		String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		if (executionId != null) {
			TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
			TaskQuery taskQuery = taskService.createTaskQuery();
			Task task = taskQuery.processInstanceId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
			if (task != null) {
				String smName = task.getProcessDefinitionId();
				String version = smName.substring(smName.indexOf(":") + 1, smName.lastIndexOf(":"));
				smName = smName.substring(0, smName.indexOf(":"));
				Set<StateField> result = getStateMecheneByName(smName).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(task.getName()).getFields();
				return new StateFields(true, result);
			}
		}
		return new StateFields(false);
//        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
//        if (executionId != null) {
//            String taskId = getCurrentTaskId(executionId);
//            if (taskId != null) {
//                List<StateMachineAction> actions = getStatusChangeActions(document);
//                Set<StateField> result = new HashSet<StateField>();
//                for (StateMachineAction action : actions) {
//                    StatusChangeAction statusChangeAction = (StatusChangeAction) action;
//                    result.addAll(statusChangeAction.getFields());
//                }
//                return new StateFields(true, result);
//            }
//        }
//        return new StateFields(false);
	}

	//TODO Используется только в  /lecm/statemachine/api/field/editable
	public boolean isEditableField(NodeRef document, String field) {
		String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		field = field.replace(":", "_");
		if (executionId != null) {
			TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
			TaskQuery taskQuery = taskService.createTaskQuery();
			Task task = taskQuery.processInstanceId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
			if (task != null) {
				String smName = task.getProcessDefinitionId();
				String version = smName.substring(smName.indexOf(":") + 1, smName.lastIndexOf(":"));
				smName = smName.substring(0, smName.indexOf(":"));
				Set<StateField> fields = getStateMecheneByName(smName).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(task.getName()).getFields();
				for (StateField stateField : fields) {
					if (stateField.getName().equals(field)) {
						return stateField.isEditable();
					}
				}
				return false;
			}
		}
		return false;
//        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
//        if (executionId != null) {
//            String taskId = getCurrentTaskId(executionId);
//            if (taskId != null) {
//                List<StateMachineAction> actions = getStatusChangeActions(document);
//                Set<StateField> fields = new HashSet<StateField>();
//                for (StateMachineAction action : actions) {
//                    StatusChangeAction statusChangeAction = (StatusChangeAction) action;
//	                fields.addAll(statusChangeAction.getFields());
//                }
//	            for (StateField stateField: fields) {
//		            if (stateField.getName().equals(field)) {
//			            return stateField.isEditable();
//		            }
//	            }
//                return false;
//            }
//        }
//        return false;
	}

	public String getCurrentExecutionId(String taskId) {
		TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		Task task = taskQuery.taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
		return task == null ? null : task.getExecutionId();
	}

	public void setInputVariables(String stateMachineExecutionId, String workflowExecutionId, List<WorkflowVariables.WorkflowVariable> variables) {
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		for (WorkflowVariables.WorkflowVariable variable : variables) {
			String value = "";
			if (variable.getFromType() == WorkflowVariables.Type.VARIABLE) {
				value = (String) runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFromValue());
			} else if (variable.getFromType() == WorkflowVariables.Type.FIELD) {
				NodeService nodeService = serviceRegistry.getNodeService();

				NodeRef wPackage = ((ActivitiScriptNode) runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
				List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
				if (documents.size() > 0) {
					NodeRef document = documents.get(0).getChildRef();
					QName propertyName = QName.createQName(variable.getFromValue(), serviceRegistry.getNamespaceService());
					Object valueObj = nodeService.getProperty(document, propertyName);
					if (valueObj != null) {
						value = valueObj.toString();
					}
				}
			} else if (variable.getFromType() == WorkflowVariables.Type.VALUE) {
				value = variable.getFromValue();
			}

			if (variable.getToType() == WorkflowVariables.Type.VARIABLE) {
				runtimeService.setVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getToValue(), value);
			} else if (variable.getToType() == WorkflowVariables.Type.FIELD) {
				NodeService nodeService = serviceRegistry.getNodeService();

				NodeRef wPackage = ((ActivitiScriptNode) runtimeService.getVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
				List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
				if (documents.size() > 0) {
					NodeRef document = documents.get(0).getChildRef();
					QName propertyName = QName.createQName(variable.getToValue(), serviceRegistry.getNamespaceService());
					nodeService.setProperty(document, propertyName, value);
				}
			}
		}
	}

	/**
	 * Извлекает список переменных и их значений, которые необходимо передать в запускаемый процесс
	 *
	 * @param stateMachineExecutionId
	 * @param variables
	 * @return
	 */
	public Map<String, String> getInputVariablesMap(String stateMachineExecutionId, List<WorkflowVariables.WorkflowVariable> variables) {
		HashMap<String, String> result = new HashMap<String, String>();
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		for (WorkflowVariables.WorkflowVariable variable : variables) {
			String value = "";
			if (variable.getFromType() == WorkflowVariables.Type.VARIABLE) {
				Object varObject = runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFromValue());
				if (varObject instanceof ActivitiScriptNode) {
					value = ((ActivitiScriptNode) varObject).getNodeRef().toString();
				} else {
					value = /*varObject*/ (String) runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFromValue());
				}
			} else if (variable.getFromType() == WorkflowVariables.Type.FIELD) {
				NodeService nodeService = serviceRegistry.getNodeService();

				NodeRef wPackage = ((ActivitiScriptNode) runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
				List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
				if (documents.size() > 0) {
					NodeRef document = documents.get(0).getChildRef();
					QName propertyName = QName.createQName(variable.getFromValue(), serviceRegistry.getNamespaceService());
					PropertyDefinition propDef = serviceRegistry.getDictionaryService().getProperty(propertyName);
					Object pv = nodeService.getProperty(document, propertyName);
					if (propDef != null) {
						if (propDef.getDataType().getName().equals(DataTypeDefinition.DATE) || propDef.getDataType().getName().equals(DataTypeDefinition.DATETIME)) {
							SimpleDateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
							value = pv != null ? DateFormatISO8601.format(pv) : "";
						} else {
							value = pv != null ? pv.toString() : "";
						}
					}
				}
			} else if (variable.getFromType() == WorkflowVariables.Type.VALUE) {
				value = variable.getFromValue();
			}

			result.put(variable.getToValue(), value);
		}
		return result;
	}

	public void getOutputVariables(String stateMachineExecutionId, Map<String, Object> executionVariables, List<WorkflowVariables.WorkflowVariable> variables) {
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		for (WorkflowVariables.WorkflowVariable variable : variables) {
			Object value = "";
			if (variable.getFromType() == WorkflowVariables.Type.VARIABLE) {
				value = executionVariables.get(variable.getFromValue());
			} else if (variable.getFromType() == WorkflowVariables.Type.FIELD) {
				NodeService nodeService = serviceRegistry.getNodeService();

				NodeRef wPackage = ((ActivitiScriptNode) executionVariables.get("bpm_package")).getNodeRef();
				List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
				if (documents.size() > 0) {
					NodeRef document = documents.get(0).getChildRef();
					QName propertyName = QName.createQName(variable.getFromValue(), serviceRegistry.getNamespaceService());
					value = nodeService.getProperty(document, propertyName).toString();
				}
			} else if (variable.getFromType() == WorkflowVariables.Type.VALUE) {
				value = variable.getFromValue();
			}

			if (variable.getToType() == WorkflowVariables.Type.VARIABLE) {
				runtimeService.setVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getToValue(), value);
			} else if (variable.getToType() == WorkflowVariables.Type.FIELD) {
				NodeService nodeService = serviceRegistry.getNodeService();

				NodeRef wPackage = ((ActivitiScriptNode) runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
				List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
				if (documents.size() > 0) {
					NodeRef document = documents.get(0).getChildRef();
					QName propertyName = QName.createQName(variable.getToValue(), serviceRegistry.getNamespaceService());
					nodeService.setProperty(document, propertyName, (Serializable) value);
				}
			}
		}
	}

	public NodeRef getStatemachineDocument(final String executionId) {
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		ActivitiScriptNode s = (ActivitiScriptNode) runtimeService.getVariable(executionId.replace(ACTIVITI_PREFIX, ""), "bpm_package");
		NodeRef nodeRef = s.getNodeRef();
		List<ChildAssociationRef> documents = serviceRegistry.getNodeService().getChildAssocs(nodeRef);
		if (documents.size() > 0) {
			return documents.get(0).getChildRef();
		} else {
			return null;
		}
	}

	//TODO!!!!
	public Execution getExecution(String executionId) {/*Снаружи только в TimerActionHelper*/

		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		return runtimeService.createProcessInstanceQuery().processInstanceId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
	}

	/**
	 * Проверка наличия машины состояний у документа
	 *
	 * @param document
	 * @return
	 */
	public boolean hasStatemachine(NodeRef document) {
		Object statemachineId = serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		return statemachineId != null;
	}

	/**
	 *
	 * @param document
	 * @return Версия машины состояний для документа
	 */
	public String getStatemachineVersion(NodeRef document) {
		String result = null;
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		Execution execution = activitiProcessEngineConfiguration.getRuntimeService().createExecutionQuery().executionId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();
		if (execution != null) {
			result = ((DelegateExecution) execution).getProcessDefinitionId();
//            String taskId = getCurrentTaskId(execution.getId());
//            //TODO Сразу передавать нужные параметры
//            List<StateMachineAction> actions = getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(StatusChangeAction.class));
//            for (StateMachineAction action : actions) {
//                if (action instanceof  StatusChangeAction) {
//                    result = ((StatusChangeAction) action).getVersion();
//                }
//            }
		} else {
			HistoricProcessInstance process = activitiProcessEngineConfiguration.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();
			result = process.getProcessDefinitionId();
//
//            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(process.getProcessDefinitionId());
//            List<ActivityImpl> activities = processDefinitionEntity.getActivities();
//            ActivityImpl activity = null;
//            for (ActivityImpl act : activities) {
//                if (act.getActivityBehavior() instanceof UserTaskActivityBehavior) {
//                    activity = act;
//                }
//            }
//            if (activity != null) {
//                List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
//                if (listeners != null) {
//                    for (ExecutionListener listener : listeners) {
//                        if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
//                            List<StateMachineAction> actions = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("start");
//                            for (StateMachineAction action : actions) {
//                                if (action instanceof  StatusChangeAction) {
//                                    result = ((StatusChangeAction) action).getVersion();
//                                }
//                            }
//                        }
//                    }
//                }
//            }
		}
		return result;
	}

	public String getDocumentStatus(NodeRef document) {
		Serializable status = serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATUS);
		return status == null ? null : status.toString();
	}

	public TransitionResponse executeUserAction(NodeRef document, String actionId) {
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		String currentTask = getCurrentTaskId(statemachineId);
		return executeUserAction(document, currentTask, actionId, FinishStateWithTransitionAction.class, null);
	}

	public TransitionResponse executeActionByName(NodeRef document, String actionName) {
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		String currentTask = getCurrentTaskId(statemachineId);
		TransitionResponse response = new TransitionResponse();
		//TODO Сразу передавать нужные параметры
		List<StateMachineAction> actions = getTaskActionsByName(currentTask, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class));
		FinishStateWithTransitionAction.NextState nextState = null;
		String id = "";
		for (StateMachineAction action : actions) {
			FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
			List<FinishStateWithTransitionAction.NextState> states = finishStateWithTransitionAction.getStates();
			for (FinishStateWithTransitionAction.NextState state : states) {
				if (state.getLabel().equalsIgnoreCase(actionName)) {
					id = state.getActionId();
				}
			}
		}
		response = executeUserAction(document, id);
		return response;
	}

	//Используется в ???

	public TransitionResponse executeUserAction(NodeRef document, String taskId, String actionId, Class<? extends StateMachineAction> actionType, String persistedResponse) {
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		TransitionResponse response = new TransitionResponse();
		if (FinishStateWithTransitionAction.class.equals(actionType)) {
			//TODO Сразу передавать нужные параметры
			List<StateMachineAction> actions = getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class));
			FinishStateWithTransitionAction.NextState nextState = null;
			//TODO получать без перебора всех значений
			for (StateMachineAction action : actions) {
				FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
				List<FinishStateWithTransitionAction.NextState> states = finishStateWithTransitionAction.getStates();
				for (FinishStateWithTransitionAction.NextState state : states) {
					if (state.getActionId().equalsIgnoreCase(actionId)) {
						nextState = state;
					}
				}
			}
			if (nextState != null) {
				response = executeTransitionAction(document, statemachineId, taskId, nextState, persistedResponse);
			}
		} else if (UserWorkflow.class.equals(actionType)) {
			response = executeUserWorkflowAction(document, statemachineId, taskId, actionId, persistedResponse);
		}
		return response;
	}

	//Используется в групповых операциях

	public void executeTransitionAction(NodeRef document, String actionName) {
		String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		String taskId = getCurrentTaskId(statemachineId);
		//TODO Сразу передавать нужные параметры
		List<StateMachineAction> actions = getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class));
		FinishStateWithTransitionAction.NextState nextState = null;
		//TODO получать без перебора всех значений
		for (StateMachineAction action : actions) {
			FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
			List<FinishStateWithTransitionAction.NextState> states = finishStateWithTransitionAction.getStates();
			for (FinishStateWithTransitionAction.NextState state : states) {
				if (state.getLabel().equalsIgnoreCase(actionName)) {
					nextState = state;
				}
			}
		}
		if (nextState != null) {
			executeTransitionAction(document, statemachineId, taskId, nextState, null);
		}

	}

	//Используется в скрипте машины состояний (ОРД)

	public void executeTransitionAction(NodeRef document, String actionName, Task task) {
		List<StateMachineAction> actions = getTaskActionsByName(task.getName(), task.getProcessDefinitionId(), StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class));
		FinishStateWithTransitionAction.NextState nextState = null;
		//TODO получать без перебора всех значений
		for (StateMachineAction action : actions) {
			FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
			List<FinishStateWithTransitionAction.NextState> states = finishStateWithTransitionAction.getStates();
			for (FinishStateWithTransitionAction.NextState state : states) {
				if (state.getLabel().equalsIgnoreCase(actionName)) {
					nextState = state;
				}
			}
		}
		if (nextState != null) {
			executeTransitionAction(document, ACTIVITI_PREFIX + task.getProcessInstanceId(), ACTIVITI_PREFIX + task.getId(), nextState, null);
		}

	}

	public List<WorkflowTask> filterTasksByAssignees(List<WorkflowTask> tasks, List<NodeRef> assigneesEmployees) {
		if (tasks == null || tasks.isEmpty() || assigneesEmployees == null || assigneesEmployees.isEmpty()) {
			return new ArrayList<WorkflowTask>();
		}

		List<NodeRef> persons = new ArrayList<NodeRef>();
		for (NodeRef employee : assigneesEmployees) {
			NodeRef person = orgstructureBean.getPersonForEmployee(employee);
			if (person != null) {
				persons.add(person);
			}
		}

		List<WorkflowTask> result = new ArrayList<WorkflowTask>();
		for (WorkflowTask task : tasks) {
			String owner = (String) task.getProperties().get(ContentModel.PROP_OWNER);
			if (owner == null) {
				result.add(task);
				continue;
			}

			NodeRef ownerPerson = serviceRegistry.getPersonService().getPerson(owner);
			if (persons.contains(ownerPerson)) {
				result.add(task);
			}
		}

		return result;
	}

	public void stopDocumentSubWorkflows(NodeRef document, String currentExecutionId) {
		//TODO DONE document есть в верхнем вызове
		//NodeRef document = getStatemachineDocument(stateMachineExecutionId);
		DocumentWorkflowUtil documentWorkflowUtil = new DocumentWorkflowUtil();
		List<WorkflowDescriptor> workflowDescriptors = documentWorkflowUtil.getWorkflowDescriptors(document);
		for (WorkflowDescriptor workflowDescriptor : workflowDescriptors) {
			if (currentExecutionId == null || !currentExecutionId.equals(workflowDescriptor.getExecutionId())) {
				WorkflowInstance instance = serviceRegistry.getWorkflowService().getWorkflowById(workflowDescriptor.getExecutionId());
				if (instance != null && instance.isActive()) {
					serviceRegistry.getWorkflowService().deleteWorkflow(workflowDescriptor.getExecutionId());
				}
			}
			documentWorkflowUtil.removeWorkflow(document, workflowDescriptor.getExecutionId());
		}
	}

	public List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser) {
		List<WorkflowTask> result = new ArrayList<WorkflowTask>();

		List<WorkflowTask> tasks = getAssignedAndPooledTasks(fullyAuthenticatedUser);
		for (WorkflowTask task : tasks) {
			if (getTaskDocument(task, documentTypes) != null) {
				result.add(task);
			}
		}

		return result;
	}

	public List<WorkflowTask> getAssignedAndPooledTasks(String fullyAuthenticatedUser) {
		List<WorkflowTask> result = new ArrayList<WorkflowTask>();
		WorkflowService workflowService = serviceRegistry.getWorkflowService();

		List<WorkflowTask> assignedTasks = workflowService.getAssignedTasks(fullyAuthenticatedUser, WorkflowTaskState.IN_PROGRESS);
		result.addAll(assignedTasks);

		List<WorkflowTask> pooledTasks = workflowService.getPooledTasks(fullyAuthenticatedUser);
		result.addAll(pooledTasks);

		return result;
	}

	/**
	 * Останавливает процесс по его Id
	 *
	 * @param processId
	 */
	@Override
	public void terminateProcess(String processId) {
//        ExecutionEntity process = (ExecutionEntity) activitiProcessEngineConfiguration.getRuntimeService().createProcessInstanceQuery().processInstanceId(processId).singleResult();
//        //Завершаем процесс
//        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(process.getProcessDefinitionId());
//        List<ActivityImpl> activities = definition.getActivities();
//        ActivityImpl endActivity = null;
//        for (ActivityImpl activity : activities) {
//            if (activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior) {
//                endActivity = activity;
//            }
//        }
//        if (endActivity != null) {
//            process.setProcessDefinition(definition);
//            process.setActivity(endActivity);
//            try {
//                process.end();
//            } catch (Exception e) {
//                //logger.error(e.getMessage(), e);
//            }
//
//        }
		activitiProcessEngineConfiguration.getRuntimeService().deleteProcessInstance(processId, WorkflowConstants.PROP_CANCELLED);

		// Convert historic process instance
		HistoricProcessInstance deletedInstance = activitiProcessEngineConfiguration.getHistoryService().createHistoricProcessInstanceQuery()
				.processInstanceId(processId)
				.singleResult();

		// Delete the historic process instance
		activitiProcessEngineConfiguration.getHistoryService().deleteHistoricProcessInstance(deletedInstance.getId());
		//activitiProcessEngineConfiguration.getRuntimeService().deleteProcessInstance(processId, "cancelled");
	}

	public void /*Используется в StateMacheneCreateDocumentPolicy*/ executePostponedActions(String executionId) {
		String taskId = getCurrentTaskId(executionId);
		List<StateMachineAction> actions = getTaskActions(taskId, ExecutionListener.EVENTNAME_START);
		for (StateMachineAction action : actions) {
			if (action instanceof PostponedAction) {
				PostponedAction postponedAction = (PostponedAction) action;
				postponedAction.postponedExecution(taskId, this);
			}
		}
	}

	public void /*Используется в ScriptAction, WaitFordocumentChangeListenerPolicy*/ executeScript(String script, String statemachineId) {
		try {
			Execution execution = getExecution(statemachineId);
			if (execution != null) {
				Map<String, Object> vars = activitiProcessEngineConfiguration.getRuntimeService().getVariables(execution.getId());
				WorkflowScript base = new WorkflowScript(vars, activitiProcessEngineConfiguration);
				base.setScript(new FixedValue(script));
				base.notify((DelegateExecution) execution);
			}
		} catch (Exception e) {
			logger.error("Error while script execution", e);
		}

	}

	public NodeRef getTaskDocument(WorkflowTask task, List<String> documentTypes) {
		NodeRef wfPackage = (NodeRef) task.getProperties().get(WorkflowModel.ASSOC_PACKAGE);
		List<ChildAssociationRef> childAssocs = serviceRegistry.getNodeService().getChildAssocs(wfPackage);
		for (ChildAssociationRef childAssoc : childAssocs) {
			NodeRef document = childAssoc.getChildRef();
			if (documentTypes == null) {
				return document;
			}
			QName documentType = serviceRegistry.getNodeService().getType(document);
			if (documentTypes.contains(documentType.getLocalName())) {
				return document;
			}
		}
		return null;
	}

	public List<WorkflowTask> getDocumentTasks(NodeRef documentRef, boolean activeTasks) {
		boolean hasPermission = lecmPermissionService.hasPermission(LecmPermissionService.PERM_WF_TASK_LIST, documentRef);
		if (!hasPermission) {
			return new ArrayList<WorkflowTask>();
		}

		List<WorkflowTask> result = new ArrayList<WorkflowTask>();
		WorkflowService workflowService = serviceRegistry.getWorkflowService();

		List<WorkflowInstance> activeWorkflows = workflowService.getWorkflowsForContent(documentRef, true);
		String executionId = (String) serviceRegistry.getNodeService().getProperty(documentRef, StatemachineModel.PROP_STATEMACHINE_ID);
		for (WorkflowInstance workflow : activeWorkflows) {
			if (!executionId.equals(workflow.getId())) {
				List<WorkflowTask> tasks = getWorkflowTasks(workflow, activeTasks);
				result.addAll(tasks);
			}
		}

		if (!activeTasks) {
			List<WorkflowInstance> completedWorkflows = workflowService.getWorkflowsForContent(documentRef, false);
			for (WorkflowInstance workflow : completedWorkflows) {
				List<WorkflowTask> tasks = getWorkflowTasks(workflow, false);
				result.addAll(tasks);
			}
		}

		return result;
	}

	public void /*Используется в StartWorkflowAction и StartWorkflowScript*/ logStartWorkflowEvent(NodeRef document, String executionId) {
		if (!isServiceWorkflow(executionId)) {
			businessJournalService.log(document, StateMachineEventCategory.START_WORKFLOW, "Запущен бизнес-процесс #object1 на документе #mainobject", Collections.singletonList(executionId));
		}
	}

	public void /*Используется только в EndWorkflowEvent*/ logEndWorkflowEvent(NodeRef document, String executionId) {
		if (!isServiceWorkflow(executionId)) {
			businessJournalService.log(document, StateMachineEventCategory.END_WORKFLOW, "Завершен бизнес-процесс #object1 на документе #mainobject", Collections.singletonList(executionId));
		}
	}
    //public void stopDocumentProcessing(String taskId) {
	//    nextTransition(ACTIVITI_PREFIX + taskId.replace(ACTIVITI_PREFIX, ""));
	//}
	//
	//public void addProcessDependency(String currentTask, String dependencyProcess) {
	//  String taskId = currentTask.replace(ACTIVITI_PREFIX, "");
	//  RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
	//  Execution execution = runtimeService.createExecutionQuery().processInstanceId(dependencyProcess.replace(ACTIVITI_PREFIX, "")).singleResult();
	//  runtimeService.setVariable(execution.getId(), PROP_PARENT_PROCESS_ID, Long.valueOf(taskId));
	//}
	//
	//public void getOutputVariables(String stateMachineExecutionId, String workflowExecutionId, List<WorkflowVariables.WorkflowVariable> variables) {
	//  RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
	//  for (WorkflowVariables.WorkflowVariable variable : variables) {
	//      String value = "";
	//      if (variable.getFromType() == WorkflowVariables.Type.VARIABLE) {
	//          value = runtimeService.getVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFromValue()).toString();
	//      } else if (variable.getFromType() == WorkflowVariables.Type.FIELD) {
	//          NodeService nodeService = serviceRegistry.getNodeService();
	//
	//          NodeRef wPackage = ((ActivitiScriptNode) runtimeService.getVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
	//          List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
	//          if (documents.size() > 0) {
	//              NodeRef document = documents.get(0).getChildRef();
	//              QName propertyName = QName.createQName(variable.getFromValue(), serviceRegistry.getNamespaceService());
	//              value = nodeService.getProperty(document, propertyName).toString();
	//          }
	//      } else if (variable.getFromType() == WorkflowVariables.Type.VALUE) {
	//          value = variable.getFromValue();
	//      }
	//
	//      if (variable.getToType() == WorkflowVariables.Type.VARIABLE) {
	//          runtimeService.setVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getToValue(), value);
	//      } else if (variable.getToType() == WorkflowVariables.Type.FIELD) {
	//          NodeService nodeService = serviceRegistry.getNodeService();
	//
	//          NodeRef wPackage = ((ActivitiScriptNode) runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
	//          List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
	//          if (documents.size() > 0) {
	//              NodeRef document = documents.get(0).getChildRef();
	//              QName propertyName = QName.createQName(variable.getToValue(), serviceRegistry.getNamespaceService());
	//              nodeService.setProperty(document, propertyName, value);
	//          }
	//      }
	//  }
	//}
	//
	//public List<WorkflowTask> getDocumentTasks(NodeRef nodeRef) {
	//    return getActiveTasks(nodeRef);
	//}
	//
	//public List<WorkflowTask> /*Используется в ActionScript и StateMacheneWebScriptBean*/getActiveTasks(NodeRef nodeRef) {
	//    return getDocumentTasks(nodeRef, true);
	//}
	//
	//public List<WorkflowTask> /*Используется в StateMacheneWebScriptBean*/getCompletedTasks(NodeRef nodeRef) {
	//    return getDocumentTasks(nodeRef, false);
	//}
	//
	//public List<WorkflowInstance> getActiveWorkflows(NodeRef nodeRef) {
	//  return getWorkflows(nodeRef, true);
	//}
	//
	//public List<WorkflowInstance> getCompletedWorkflows(NodeRef nodeRef) {
	//  return getWorkflows(nodeRef, false);
	//}
	//public NodeRef getTaskDocument(WorkflowTask task) {
	//  return getTaskDocument(task, null);
	//}
	//
	//public List<NodeRef> getDocumentsWithActiveTasks(NodeRef employee) {
	//  return getDocumentsWithActiveTasks(employee, null);
	//}

    //////////////////////////////////////////////////////Private/////////////////////////////////////////////////
	private List<StateMachineAction> getStateMachineActions(String processDefinitionId, String activityId, String onFire) {
		List<StateMachineAction> result = new ArrayList<StateMachineAction>();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(processDefinitionId);
		ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
		String name = (String) activity.getProperty("name");//taskDefinition,name,documentation,type
//        if(activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
//        	List<TaskListener> l = ((UserTaskActivityBehavior)activity.getActivityBehavior()).getTaskDefinition().getTaskListener("complete");
//        	if (l != null) {
//                for (TaskListener listener : l) {
//                    if (listener instanceof ClassDelegate) {
//                    	String action = ((ClassDelegate) listener).getClassName();
//                    	if("ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction".equals(action)) {
//
//                    		FinishStateWithTransitionAction f = new FinishStateWithTransitionAction();
//
//                    		result.add(f);
//                    	}
//                    }
//                }
//            }
//        }
		String smName = processDefinitionId.substring(0, processDefinitionId.indexOf(":"));
		String version = processDefinitionId.substring(processDefinitionId.indexOf(":") + 1, processDefinitionId.lastIndexOf(":"));
		result.add(getStateMecheneByName(smName).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(name).getActionByName("FinishStateWithTransition"));
//        List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
//        if (listeners != null) {
//            for (ExecutionListener listener : listeners) {
//                if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
//					List<StateMachineAction> actions = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get(onFire);
//                    if (actions != null) {
//						result = actions;
//					}
//                }
//            }
//        }
		return result;
	}

//    private List<StateMachineAction> getStatusChangeActions(NodeRef document) {
//        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
//        String taskId = getCurrentTaskId(executionId);
//        //TODO Сразу передавать нужные параметры
//        return getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(StatusChangeAction.class));
//    }
	private List<WorkflowTask> getWorkflowTasks(WorkflowInstance workflow, boolean activeTasks) {
		List<WorkflowTask> result = new ArrayList<WorkflowTask>();
		WorkflowService workflowService = serviceRegistry.getWorkflowService();

		WorkflowTask startTask = workflowService.getStartTask(workflow.getId());

		WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
		taskQuery.setProcessId(workflow.getId());
		taskQuery.setTaskState(activeTasks ? WorkflowTaskState.IN_PROGRESS : WorkflowTaskState.COMPLETED);
		taskQuery.setActive(workflow.isActive());

		List<WorkflowTask> workflowTasks = workflowService.queryTasks(taskQuery);
		for (WorkflowTask workflowTask : workflowTasks) {
			if (!startTask.getId().equals(workflowTask.getId())) {
				result.add(workflowTask);
			}
		}

		return result;
	}

//    private List<WorkflowInstance> filterWorkflows(List<WorkflowInstance> workflows) {
//        List<WorkflowInstance> result = new ArrayList<WorkflowInstance>();
//        NodeRef workflowSysUser = serviceRegistry.getPersonService().getPerson("workflow");
//        for (WorkflowInstance instance : workflows) {
//            if (!workflowSysUser.equals(instance.getInitiator()) && !isServiceWorkflow(instance)) {
//                result.add(instance);
//            }
//        }
//        return result;
//    }
	//TODO Метод для рефакторинга !!!!
	//document - документ это наше все stopDocumentSubWorkflows, addWorkflow, createConnection
	//statemachineId - используется для executeScript, new WorkflowDescriptor, setInputVariables, setExecutionParameters
	//taskId - используется для nextTransition, new WorkflowDescriptor
	//nextState - теепрь передается вместо actionId который нужен был только для получения nextState =)
	//persistedResponse - хз
	private TransitionResponse executeTransitionAction(final NodeRef document, final String statemachineId, String taskId, FinishStateWithTransitionAction.NextState nextState, String persistedResponse) {
		TransitionResponse response = new TransitionResponse();
		List<String> errors = new ArrayList<String>();

		if (nextState != null) {
			if (nextState.isStopSubWorkflows()) {
				//TODO DONE теперь первый параметр документ а не id машины состояний
				stopDocumentSubWorkflows(document, null);
			}

			if (!"".equals(nextState.getScript()) && nextState.getScript() != null) {
				final String script = nextState.getScript();
				AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
					@Override
					public Object doWork() throws Exception {
						executeScript(script, statemachineId);
						return null;
					}
				});
			}

			if (!"".equals(nextState.getOutputVariableValue())) {
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				parameters.put(nextState.getOutputVariableName(), nextState.getOutputVariableValue());
				//TODO DONE может сразу execution? раньше через таск
				setExecutionParameters(statemachineId, parameters);
				nextTransition(taskId);
			}

			if (!nextState.isForm()) {
				String dependencyExecution = parseExecutionId(persistedResponse);
				if (dependencyExecution != null) {
					WorkflowDescriptor descriptor = new WorkflowDescriptor(dependencyExecution, statemachineId, nextState.getWorkflowId(), taskId, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class), nextState.getActionId(), ExecutionListener.EVENTNAME_TAKE);
					new DocumentWorkflowUtil().addWorkflow(document, dependencyExecution, descriptor);
					setInputVariables(statemachineId, dependencyExecution, nextState.getVariables().getInput());

					//Добавляем участников к документу.
					List<NodeRef> assignees = getAssigneesForWorkflow(dependencyExecution);
					for (final NodeRef assignee : assignees) {
						AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
							@Override
							public NodeRef doWork() throws Exception {
								//TODO transaction in loop!!!
								RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
								return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
									@Override
									public NodeRef execute() throws Throwable {
										return documentMembersService.addMember(document, assignee, new HashMap<QName, Serializable>());
									}
								});
							}
						});
					}

					//Берем адрес переадресации
					Object redirect = activitiProcessEngineConfiguration.getRuntimeService().getVariable(dependencyExecution.replace(ACTIVITI_PREFIX, ""), StateMachineServiceBean.REDIRECT_VARIABLE);
					if (redirect != null) {
						response.setRedirect(redirect.toString());
					}
					//Посылаем сигнал, если процесс с ожиданием
					sendSignal(dependencyExecution);
				}
			} else {
				if (NodeRef.isNodeRef(persistedResponse)) {
					response.setRedirect("document?nodeRef=" + persistedResponse);
					if (nextState.getFormConnection() != null && !"".equals(nextState.getFormConnection())) {
						documentConnectionService.createConnection(new NodeRef(persistedResponse), document, nextState.getFormConnection(), nextState.isSystemFormConnection());
					}
				}
			}
		} else {
			errors.add("Данный actionId не существует для документа в текущем статусе");
		}
		response.setErrors(errors);
		return response;
	}

	private TransitionResponse executeUserWorkflowAction(final NodeRef document, String statemachineId, String taskId, String actionId, String persistedResponse) {
		TransitionResponse response = new TransitionResponse();
		List<String> errors = new ArrayList<String>();
		//TODO Сразу передавать нужные параметры
		List<StateMachineAction> actions = getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(UserWorkflow.class));
		if (actions.size() == 0) {
			actions = getHistoricalTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(UserWorkflow.class), ExecutionListener.EVENTNAME_TAKE);
		}
		UserWorkflow.UserWorkflowEntity workflow = null;
		for (StateMachineAction action : actions) {
			UserWorkflow userWorkflow = (UserWorkflow) action;
			List<UserWorkflow.UserWorkflowEntity> workflows = userWorkflow.getUserWorkflows();
			for (UserWorkflow.UserWorkflowEntity workflowEntity : workflows) {
				if (workflowEntity.getId().equalsIgnoreCase(actionId)) {
					workflow = workflowEntity;
				}
			}
		}
		if (workflow != null && persistedResponse != null && !"null".equals(persistedResponse)) {
			String dependencyExecution = parseExecutionId(persistedResponse);
			WorkflowDescriptor descriptor = new WorkflowDescriptor(dependencyExecution, statemachineId, workflow.getWorkflowId(), taskId, StateMachineActionsImpl.getActionNameByClass(UserWorkflow.class), actionId, ExecutionListener.EVENTNAME_TAKE);
			/*Добавляет workflow в документ???*/
			new DocumentWorkflowUtil().addWorkflow(document, dependencyExecution, descriptor);

			setInputVariables(statemachineId, dependencyExecution, workflow.getVariables().getInput());

			//Добавляем участников к документу.
			List<NodeRef> assignees = getAssigneesForWorkflow(dependencyExecution);
			for (final NodeRef assignee : assignees) {
				AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
					@Override
					public NodeRef doWork() throws Exception {
						//TODO transaction in loop!!!
						RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
						return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
							@Override
							public NodeRef execute() throws Throwable {
								return documentMembersService.addMember(document, assignee, new HashMap<QName, Serializable>());
							}
						});
					}
				});
			}

			//Берем адрес переадресации
			Object redirect = activitiProcessEngineConfiguration.getRuntimeService().getVariable(dependencyExecution.replace(ACTIVITI_PREFIX, ""), StateMachineServiceBean.REDIRECT_VARIABLE);
			if (redirect != null) {
				response.setRedirect(redirect.toString());
			}
			//Посылаем сигнал, если процесс с ожиданием
			sendSignal(dependencyExecution);
		} else {
			errors.add("Данный actionId не существует для документа в текущем статусе");
		}
		return response;
	}

	private StateFields getStateCategories(NodeRef document) {
		String procesId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
		if (procesId != null) {
			TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
			TaskQuery taskQuery = taskService.createTaskQuery();
			Task task = taskQuery.processInstanceId(procesId.replace(ACTIVITI_PREFIX, "")).singleResult();
			if (task != null) {
				String smName = task.getProcessDefinitionId();
				String version = smName.substring(smName.indexOf(":") + 1, smName.lastIndexOf(":"));
				smName = smName.substring(0, smName.indexOf(":"));
				Set<StateField> result = getStateMecheneByName(smName).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(task.getName()).getCategories();
				return new StateFields(true, result);
			}
		}
		return new StateFields(false);
	}

	private List<StateMachineAction> getTaskActions(String taskId, String onFire) {
		List<StateMachineAction> result = new ArrayList<StateMachineAction>();
		TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		if (taskId != null) {
			Task task = taskQuery.taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
			if (task != null) {
				Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
				String activityId = ((ExecutionEntity) execution).getActivityId();
				ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
				String processDefinitionId = process.getProcessDefinitionId();
				result = getStateMachineActions(processDefinitionId, activityId, onFire);
			}
		}
		return result;
	}

	/**
	 * Возвращает может ли сотрудник создавать документ определенного типа
	 *
	 * @param type - тип документа
	 * @param employee - сотрудник
	 * @return
	 */
	private boolean isStarter(String type, NodeRef employee) {
		String statmachene = type.replace(":", "_");
        List<String> accessRoles = getStateMecheneByName(statmachene).getLastVersion().getSettings().getSettingsContent().getStarterRoles();
        final String employeeLogin = orgstructureBean.getEmployeeLogin(employee);
        @SuppressWarnings("unchecked") Set<String> auth = (Set<String>) AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                return serviceRegistry.getAuthorityService().getAuthoritiesForUser(employeeLogin);
            }
        });

        for (String accessRole : accessRoles) {
            if (auth.contains("GROUP__LECM$BR%" + accessRole)) {
                return true;
            }
        }
		return false;
	}

	/**
	 * Возвращает можно ли создавать документ из АРМ-а
	 *
	 * @param type - тип документа
	 * @return true - если нельзя создавать из АРМ-а
	 */
	@Override
	public boolean isNotArmCreate(String type) {
		String statmachene = type.replace(":", "_");
		return getStateMecheneByName(statmachene).getLastVersion().getSettings().getSettingsContent().isNotArmCreate();
	}

//    /**
//     * Выбирает список действий для старта процесса последней версии.
//     *
//     * @param definitionKey - Id процесса в схеме BPMN
//     * @return
//     */
//    private List<StateMachineAction> getStartActions(String definitionKey) {
//        RepositoryServiceImpl repositoryService = (RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService();
//        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionKey(definitionKey).latestVersion().singleResult();
//        String processDefinitionId = processDefinitionEntity.getId();
//        String activityId = "start";
//        String onFire = "take";
//        return getStateMachineActions(processDefinitionId, activityId, onFire);
//    }
	private List<StateMachineAction> getHistoricalTaskActions(String taskId, String onFire) {
		List<StateMachineAction> result = new ArrayList<StateMachineAction>();
		HistoryService historyService = activitiProcessEngineConfiguration.getHistoryService();
		HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
		if (task != null) {
			result = getStateMachineActions(task.getProcessDefinitionId(), task.getTaskDefinitionKey(), onFire);
		}
		return result;
	}

	private List<HistoricTaskInstance> getHistoricalTaskInstances(NodeRef document) {
		String statemachineId = getStatemachineId(document);
		HistoryService historyService = activitiProcessEngineConfiguration.getHistoryService();
		return historyService.createHistoricTaskInstanceQuery().executionId(statemachineId).orderByTaskId().desc().list();
	}

	private List<NodeRef> getAssigneesForWorkflow(String workflowId) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		WorkflowInstance instance = serviceRegistry.getWorkflowService().getWorkflowById(workflowId);
		List<WorkflowTask> tasks = getWorkflowTasks(instance, true);
		for (WorkflowTask task : tasks) {
			String owner = (String) task.getProperties().get(ContentModel.PROP_OWNER);
			NodeRef ownerNodeRef = serviceRegistry.getPersonService().getPerson(owner);
			NodeRef employee = orgstructureBean.getEmployeeByPerson(ownerNodeRef);
			if (employee != null) {
				result.add(employee);
			}
		}
		return result;
	}

	private String /*Используется только в StartWorkflowScript*/ parseExecutionId(String persistedResponse) {
		if (persistedResponse == null || "null".equals(persistedResponse)) {
			return null;
		}

		int start = persistedResponse.indexOf("=") + 1;
		int end = persistedResponse.indexOf(",");

		try {
			return persistedResponse.substring(start, end);
		} catch (Exception e) {
			return null;
		}
	}
	//	//Используется один раз
	//  private boolean hasDocuments(WorkflowTask task, List<String> documentTypes) {
	//      return getTaskDocument(task, documentTypes) != null;
	//  }
	//
	//  /**
	//   * Выборка имен статусов для определенного описателя процесса
	//   * @param processDefinitionEntity
	//   * @return Список имен статусов
	//   */
	//  private HashSet<String> getDefinitionStatuses(ProcessDefinitionEntity processDefinitionEntity, boolean includeActive, boolean includeFinal) {
	//      HashSet<String> statuses = new HashSet<String>();
	//      List<ActivityImpl> activities = processDefinitionEntity.getActivities();
	//      for (ActivityImpl activity : activities) {
	//          List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
	//          if (listeners != null) {
	//              for (ExecutionListener listener : listeners) {
	//                  if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
	//                      List<StateMachineAction> result;
	//                      if (includeActive) {
	//                          result = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("start");
	//                          for (StateMachineAction action : result) {
	//                              if (action.getActionName().equalsIgnoreCase(StateMachineActionsImpl.getActionNameByClass(StatusChangeAction.class))) {
	//                                  StatusChangeAction statusAction = (StatusChangeAction) action;
	//                                  statuses.add(statusAction.getStatus());
	//                              }
	//                          }
	//                      }
	//                      if (includeFinal) {
	//                          result = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("end");
	//                          for (StateMachineAction action : result) {
	//                              if (action.getActionName().equalsIgnoreCase(StateMachineActionsImpl.getActionNameByClass(ArchiveDocumentAction.class))) {
	//                                  ArchiveDocumentAction archiveDocumentAction = (ArchiveDocumentAction) action;
	//                                  statuses.add(archiveDocumentAction.getStatusName());
	//                              }
	//                          }
	//                      }
	//                  }
	//              }
	//          }
	//      }
	//      return statuses;
	//  }
	//
	//private String getWorkflowDescription(String executionId) {
	//  WorkflowInstance workflow = serviceRegistry.getWorkflowService().getWorkflowById(executionId);
	//  return workflow.getDefinition().getTitle();
	//}

	@Override
	public void connectToStatemachine(final NodeRef documentRef, final String processInstanceID, final String processDefinitionID) {
		String stateMachineExecutionId = ACTIVITI_PREFIX + getStatemachineId(documentRef);
		String currentTaskId = getCurrentTaskId(stateMachineExecutionId);
		WorkflowDescriptor descriptor = new WorkflowDescriptor(processInstanceID, stateMachineExecutionId, processDefinitionID, currentTaskId, "", "", "");
		new DocumentWorkflowUtil().addWorkflow(documentRef, processInstanceID, descriptor);
	}

	@Override
	public void disconnectFromStatemachine(final NodeRef documentRef, final String processInstanceID) {
		new DocumentWorkflowUtil().removeWorkflow(documentRef, processInstanceID);
	}
}
