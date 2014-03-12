package ru.it.lecm.statemachine;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ReceiveTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
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
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.*;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.action.*;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.action.script.WorkflowScript;
import ru.it.lecm.statemachine.assign.AssignExecution;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;
import ru.it.lecm.statemachine.listener.StateMachineHandler;
import ru.it.lecm.statemachine.util.DocumentWorkflowUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 07.09.12
 * Time: 16:22
 * <p/>
 * Вспомогательный класс для Activiti BPM Platform.
 * <p/>
 * Позволяет:
 * 1. Запускать пользовательские процессы из машины состояний
 * 2. Передавать сигнал о завершении пользовательского процесс машине состояний с передачей переменных из пользовательского процесса
 */
public class StateMachineHelper implements StateMachineServiceBean {
	private final static Logger logger = LoggerFactory.getLogger(StateMachineHelper.class);

    public static String ACTIVITI_PREFIX = "activiti$";

    private static ServiceRegistry serviceRegistry;
    private Repository repositoryHelper;
    private static AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
    private static OrgstructureBean orgstructureBean;
    private static DocumentMembersService documentMembersService;
    private static BusinessJournalService businessJournalService;
    private static TransactionService transactionService;
    private static DocumentService documentService;
    private static LecmPermissionService lecmPermissionService;
    private static String BPM_PACKAGE_PREFIX = "bpm_";

    private static String PROP_PARENT_PROCESS_ID = "parentProcessId";

    public void setTransactionService(TransactionService transactionService) {
        StateMachineHelper.transactionService = transactionService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        StateMachineHelper.serviceRegistry = serviceRegistry;
    }

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
        StateMachineHelper.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        StateMachineHelper.orgstructureBean = orgstructureBean;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        StateMachineHelper.documentMembersService = documentMembersService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        StateMachineHelper.businessJournalService = businessJournalService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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
        if (person == null) return null;
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

    public void stopDocumentProcessing(String taskId) {
        nextTransition(ACTIVITI_PREFIX + taskId.replace(ACTIVITI_PREFIX, ""));
    }

    public boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority) {
        boolean isTransfer = false;
        TaskQuery query = activitiProcessEngineConfiguration.getTaskService().createTaskQuery();
        List<Task> tasks = query.taskAssignee(beforeAuthority).list();
        List<WorkflowInstance> workflows = new ArrayList<WorkflowInstance>();
        workflows.addAll(getActiveWorkflows(documentRef));
        workflows.addAll(getCompletedWorkflows(documentRef));
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

    public List<StateMachineAction> getTaskActions(String taskId, String onFire) {
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
     * Возвращает может ли текущий сотрудник создавать документ определенного типа
     * @param type - тип документа
     * @return
     */
    public boolean isStarter(String type) {
        NodeRef employee = orgstructureBean.getCurrentEmployee();
        return isStarter(type, employee);
    }

    /**
     * Выборка статусов для всех экземпляров процессов для определенного типа документа
     * @param documentType - тип документа
     * @return
     */
    @Override
    public List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal) {
        HashSet<String> statuses = new HashSet<String>();
        String type = documentType.replace(":", "_");

        //Выбираем статусы для текущей машины состояний даже если у нее нет ни одного запущенного процесса
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) activitiProcessEngineConfiguration.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(type).latestVersion().singleResult();
        if (processDefinitionEntity != null) {
            processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(processDefinitionEntity.getId());
            HashSet<String> result = getDefinitionStatuses(processDefinitionEntity, includeActive, includeFinal);
            statuses.addAll(result);
        }

        //Выбираем все статусы для всех процессов
        List<WorkflowDefinition> definitions = serviceRegistry.getWorkflowService().getAllDefinitionsByName(ACTIVITI_PREFIX + type);
        for (WorkflowDefinition definition : definitions) {
//            List<WorkflowInstance> instances = serviceRegistry.getWorkflowService().getActiveWorkflows(definition.getId());
//            if (instances.size() > 0) {
                processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(definition.getId().replace(ACTIVITI_PREFIX, ""));
            if (processDefinitionEntity != null) {
                HashSet<String> result = getDefinitionStatuses(processDefinitionEntity, includeActive, includeFinal);
                statuses.addAll(result);
            }
//            }
        }
        List<String> statusesList = new ArrayList<String>(statuses);
        Collections.sort(statusesList);
        return statusesList;
    }

    @Override
    public Set<String> getArchiveFolders(String documentType) {
        HashSet<String> folders = new HashSet<String>();
        String type = documentType.replace(":", "_");
        List<WorkflowDefinition> definitions = serviceRegistry.getWorkflowService().getAllDefinitionsByName(ACTIVITI_PREFIX + type);
        for (WorkflowDefinition definition : definitions) {
//            List<WorkflowInstance> instances = serviceRegistry.getWorkflowService().getWorkflows(definition.getId());
//            if (instances.size() > 0) {
            ProcessDefinitionEntity processDefinitionEntity;
            try {
                processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(definition.getId().replace(ACTIVITI_PREFIX, ""));
            } catch (ActivitiException e) {
                continue;
            }
                List<ActivityImpl> activities = processDefinitionEntity.getActivities();
            if (activities != null && !activities.isEmpty()) {
                for (ActivityImpl activity : activities) {
                    if (activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior) {
                        List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
                        if (listeners != null) {
                            for (ExecutionListener listener : listeners) {
                                if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
                                    List<StateMachineAction> result = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("end");
                                    if (result != null) {
                                    for (StateMachineAction action : result) {
                                        if (action.getActionName().equalsIgnoreCase(StateMachineActionsImpl.getActionNameByClass(ArchiveDocumentAction.class))) {
                                            ArchiveDocumentAction archiveDocumentAction = (ArchiveDocumentAction) action;
                                            String archiveFolderPath = archiveDocumentAction.getArchiveFolderPath();
                                            if (archiveFolderPath != null) {
                                                folders.add(archiveFolderPath);
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
//            }
        }

        TypeDefinition documentTypeQName = serviceRegistry.getDictionaryService().getType(QName.createQName(documentType, serviceRegistry.getNamespaceService()));
        for (QName qName : documentTypeQName.getDefaultAspectNames()) {
            if (DocumentService.ASPECT_FINALIZE_TO_UNIT.equals(qName)) {
                NodeRef companyHome = repositoryHelper.getCompanyHome();
                NodeRef companyArchive = serviceRegistry.getNodeService().getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, OrgstructureBean.DOCUMENT_ROOT_NAME);
                if (companyArchive != null) {
                    folders.add(serviceRegistry.getNodeService().getPath(companyArchive).toPrefixString(getServiceRegistry().getNamespaceService()));
                }
                break;
            }
        }
        return folders;
    }

    /**
     * Возвращает может ли сотрудник создавать документ определенного типа
     * @param type - тип документа
     * @param employee - сотрудник
     * @return
     */
    public boolean isStarter(String type, NodeRef employee) {
        Set<String> accessRoles = new HashSet<String>();
        List<StateMachineAction> actions = getStartActions(type.replace(":", "_"));
        for (StateMachineAction action : actions) {
            if (action instanceof DocumentPermissionAction) {
                DocumentPermissionAction permissions = (DocumentPermissionAction) action;
                accessRoles.addAll(permissions.getRoles());
            }
        }
        List<NodeRef> roleRefs = orgstructureBean.getEmployeeRoles(employee, true, true);
        for (NodeRef role : roleRefs) {
            String name = (String) serviceRegistry.getNodeService().getProperty(role, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
            if (accessRoles.contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Выбирает список действий для старта процесса последней версии.
     *
     * @param definitionKey - Id процесса в схеме BPMN
     * @return
     */
    public List<StateMachineAction> getStartActions(String definitionKey) {
        RepositoryServiceImpl repositoryService = (RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionKey(definitionKey).latestVersion().singleResult();
        String processDefinitionId = processDefinitionEntity.getId();
        String activityId = "start";
        String onFire = "take";
        return getStateMachineActions(processDefinitionId, activityId, onFire);
    }

    public List<StateMachineAction> getTaskActionsByName(String taskId, String actionType, String onFire) {
        List<StateMachineAction> actions = getTaskActions(taskId, onFire);
        List<StateMachineAction> result = new ArrayList<StateMachineAction>();
        for (StateMachineAction action : actions) {
            if (action.getActionName().equalsIgnoreCase(actionType)) {
                result.add(action);
            }
        }
        return result;
    }

    public List<StateMachineAction> getHistoricalTaskActions(String taskId, String onFire) {
        List<StateMachineAction> result = new ArrayList<StateMachineAction>();
        HistoryService historyService = activitiProcessEngineConfiguration.getHistoryService();
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
        if (task != null) {
            result = getStateMachineActions(task.getProcessDefinitionId(), task.getTaskDefinitionKey(), onFire);
        }
        return result;
    }

    public List<StateMachineAction> getHistoricalTaskActionsByName(String taskId, String actionType, String onFire) {
        List<StateMachineAction> actions = getHistoricalTaskActions(taskId, onFire);
        List<StateMachineAction> result = new ArrayList<StateMachineAction>();
        for (StateMachineAction action : actions) {
            if (action.getActionName().equalsIgnoreCase(actionType)) {
                result.add(action);
            }
        }
        return result;
    }

    public void addProcessDependency(String currentTask, String dependencyProcess) {
        String taskId = currentTask.replace(ACTIVITI_PREFIX, "");
        RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(dependencyProcess.replace(ACTIVITI_PREFIX, "")).singleResult();
        runtimeService.setVariable(execution.getId(), PROP_PARENT_PROCESS_ID, Long.valueOf(taskId));
    }

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

    @Override
    public String nextTransition(String taskId) {
        String currentStatemachine = getCurrentExecutionId(taskId);
        WorkflowService workflowService = serviceRegistry.getWorkflowService();
        String newTaskId = workflowService.endTask(taskId, null).getId();
        executePostponedActions(currentStatemachine);
        return newTaskId;
    }

    @Override
    public String getCurrentTaskId(String executionId) {
        if (executionId == null) {
            return null;
        }
        TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task task = taskQuery.executionId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
        if (task != null) {
            return ACTIVITI_PREFIX + task.getId();
        } else {
            return null;
        }
    }

    @Override
    public StateFields getStateFields(NodeRef document) {
        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        if (executionId != null) {
            String taskId = getCurrentTaskId(executionId);
            if (taskId != null) {
                List<StateMachineAction> actions = getStatusChangeActions(document);
                Set<StateField> result = new HashSet<StateField>();
                for (StateMachineAction action : actions) {
                    StatusChangeAction statusChangeAction = (StatusChangeAction) action;
                    result.addAll(statusChangeAction.getFields());
                }
                return new StateFields(true, result);
            }
        }
        return new StateFields(false);
    }

    @Override
    public boolean isEditableField(NodeRef document, String field) {
        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        if (executionId != null) {
            String taskId = getCurrentTaskId(executionId);
            if (taskId != null) {
                List<StateMachineAction> actions = getStatusChangeActions(document);
                Set<StateField> fields = new HashSet<StateField>();
                for (StateMachineAction action : actions) {
                    StatusChangeAction statusChangeAction = (StatusChangeAction) action;
	                fields.addAll(statusChangeAction.getFields());
                }
	            for (StateField stateField: fields) {
		            if (stateField.getName().equals(field)) {
			            return stateField.isEditable();
		            }
	            }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName) {
        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        boolean result = false;
        if (executionId != null) {
            String taskId = getCurrentTaskId(executionId);
            if (taskId != null) {
                List<StateMachineAction> actions = getStatusChangeActions(document);
                for (StateMachineAction action : actions) {
                    StatusChangeAction statusChangeAction = (StatusChangeAction) action;
                    Map<String, LecmPermissionService.LecmPermissionGroup> privaleges = statusChangeAction.getDynamicPrivileges();
                    LecmPermissionService.LecmPermissionGroup group = privaleges.get(roleName);
                    if (group != null) {
                        lecmPermissionService.grantDynamicRole(roleName, document, employee.getId(), group);
                        result = true;
                    }
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
                value = runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFromValue()).toString();
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
                    value = runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFromValue()).toString();
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
                        if (propDef.getDataType().getName().equals(DataTypeDefinition.DATE) || propDef.getDataType().getName().equals(DataTypeDefinition.DATETIME) ) {
                            SimpleDateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
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

    public void getOutputVariables(String stateMachineExecutionId, String workflowExecutionId, List<WorkflowVariables.WorkflowVariable> variables) {
        RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
        for (WorkflowVariables.WorkflowVariable variable : variables) {
            String value = "";
            if (variable.getFromType() == WorkflowVariables.Type.VARIABLE) {
                value = runtimeService.getVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFromValue()).toString();
            } else if (variable.getFromType() == WorkflowVariables.Type.FIELD) {
                NodeService nodeService = serviceRegistry.getNodeService();

                NodeRef wPackage = ((ActivitiScriptNode) runtimeService.getVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
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
                    nodeService.setProperty(document, propertyName, value);
                }
            }
        }
    }

    public NodeRef getStatemachineDocument(final String executionId) {
        RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
        NodeRef nodeRef = ((ActivitiScriptNode) runtimeService.getVariable(executionId.replace(ACTIVITI_PREFIX, ""), "bpm_package")).getNodeRef();
        List<ChildAssociationRef> documents = serviceRegistry.getNodeService().getChildAssocs(nodeRef);
        if (documents.size() > 0) {
            return documents.get(0).getChildRef();
        } else {
            return null;
        }
    }

    public Map<String, Object> getVariables(String executionId) {
        RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
        return runtimeService.getVariables(executionId.replace(ACTIVITI_PREFIX, ""));
    }

    public Execution getExecution(String executionId) {
        RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
        return runtimeService.createProcessInstanceQuery().processInstanceId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
    }

    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    @Override
    public boolean isDraft(NodeRef document) {
        NodeService nodeService = serviceRegistry.getNodeService();
        if (nodeService.hasAspect(document, StatemachineModel.ASPECT_IS_DRAFT)) {
            return (Boolean) nodeService.getProperty(document, StatemachineModel.PROP_IS_DRAFT);
        } else {
            boolean result = false;
            List<StateMachineAction> actions = getStatusChangeActions(document);
            for (StateMachineAction action : actions) {
                StatusChangeAction statusChangeAction = (StatusChangeAction) action;
                result = result || statusChangeAction.isForDraft();
            }
            return result;
        }
    }

    public boolean hasStatemachine(NodeRef document) {
        Object statemachineId = serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        return statemachineId != null;
    }

    public boolean hasActiveStatemachine(NodeRef document) {
        String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        Execution execution = null;
        if (statemachineId != null) {
            execution = activitiProcessEngineConfiguration.getRuntimeService().createExecutionQuery().executionId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();
        }
        return execution != null;
    }

    public String getStatemachineId(NodeRef document) {
        String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        Execution execution = null;
        if (statemachineId != null) {
            execution = activitiProcessEngineConfiguration.getRuntimeService().createExecutionQuery().executionId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();
            return execution != null ? execution.getId() : null;
        } else {
            return null;
        }

    }

    public String getStatemachineVersion(NodeRef document) {
        String result = null;
        String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        Execution execution = activitiProcessEngineConfiguration.getRuntimeService().createExecutionQuery().executionId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();
        if (execution != null) {
            String taskId = getCurrentTaskId(execution.getId());
            List<StateMachineAction> actions = getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(StatusChangeAction.class), ExecutionListener.EVENTNAME_START);
            for (StateMachineAction action : actions) {
                if (action instanceof  StatusChangeAction) {
                    result = ((StatusChangeAction) action).getVersion();
                }
            }
        } else {
            HistoricProcessInstance process = activitiProcessEngineConfiguration.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(statemachineId.replace(ACTIVITI_PREFIX, "")).singleResult();

            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(process.getProcessDefinitionId());
            List<ActivityImpl> activities = processDefinitionEntity.getActivities();
            ActivityImpl activity = null;
            for (ActivityImpl act : activities) {
                if (act.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                    activity = act;
                }
            }
            if (activity != null) {
                List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
                if (listeners != null) {
                    for (ExecutionListener listener : listeners) {
                        if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
                            List<StateMachineAction> actions = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("start");
                            for (StateMachineAction action : actions) {
                                if (action instanceof  StatusChangeAction) {
                                    result = ((StatusChangeAction) action).getVersion();
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getDocumentStatus(NodeRef document) {
        Serializable status = serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATUS);
        return status == null ? null : status.toString();
    }

    @Override
    public List<NodeRef> getAssigneesForWorkflow(String workflowId) {
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

    @Override
    public TransitionResponse executeUserAction(NodeRef document, String actionId) {
        String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        String currentTask = getCurrentTaskId(statemachineId);
        return executeUserAction(document, currentTask, actionId, FinishStateWithTransitionAction.class, null);
    }

    public TransitionResponse executeActionByName(NodeRef document, String actionName) {
        String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        String currentTask = getCurrentTaskId(statemachineId);
        TransitionResponse response = new TransitionResponse();
        List<StateMachineAction> actions = getTaskActionsByName(currentTask, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class), ExecutionListener.EVENTNAME_TAKE);
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


    public TransitionResponse executeUserAction(NodeRef document, String taskId, String actionId, Class<? extends StateMachineAction> actionType, String persistedResponse) {
        String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        TransitionResponse response = new TransitionResponse();
        if (FinishStateWithTransitionAction.class.equals(actionType)) {
            response = executeTransitionAction(document, statemachineId, taskId, actionId, persistedResponse);
        } else if (UserWorkflow.class.equals(actionType)) {
            response = executeUserWorkflowAction(document, statemachineId, taskId, actionId, persistedResponse);
        }
        return response;
    }

    @Override
    public void executeTransitionAction(NodeRef document, String actionName) {
        String statemachineId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        String taskId = getCurrentTaskId(statemachineId);

        List<StateMachineAction> actions = getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class), ExecutionListener.EVENTNAME_TAKE);
        FinishStateWithTransitionAction.NextState nextState = null;
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
            executeTransitionAction(document, statemachineId, taskId, nextState.getActionId(), null);
        }

    }

    public void logEndWorkflowEvent(NodeRef document, String executionId) {
	    if (!isServiceWorkflow(executionId)) {
	        businessJournalService.log(document, StateMachineEventCategory.END_WORKFLOW, "Завершен бизнес-процесс #object1 на документе #mainobject", Collections.singletonList(executionId));
	    }
    }

    public void logStartWorkflowEvent(NodeRef document, String executionId) {
	    if (!isServiceWorkflow(executionId)) {
	        businessJournalService.log(document, StateMachineEventCategory.START_WORKFLOW, "Запущен бизнес-процесс #object1 на документе #mainobject", Collections.singletonList(executionId));
	    }
    }

	@Override
	public boolean isServiceWorkflow(String executionId) {
		WorkflowInstance workflow = serviceRegistry.getWorkflowService().getWorkflowById(executionId);
		if (workflow != null) {
			return isServiceWorkflow(workflow);
		}
		return false;
	}

	@Override
	public boolean isServiceWorkflow(WorkflowInstance workflow) {
		List<AspectDefinition> aspects = workflow.getDefinition().getStartTaskDefinition().getMetadata().getDefaultAspects();
		if (aspects != null) {
			for (AspectDefinition aspect: aspects) {
				if (aspect.getName().equals(StatemachineModel.ASPECT_IS_SYSTEM_WORKFLOW)) {
					return true;
				}
			}
		}
		return false;
	}

    public String parseExecutionId(String persistedResponse) {
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

    @Override
    public List<WorkflowTask> getDocumentTasks(NodeRef nodeRef) {
        return getActiveTasks(nodeRef);
    }

    public List<WorkflowTask> getActiveTasks(NodeRef nodeRef) {
        return getDocumentTasks(nodeRef, true);
    }

    public List<WorkflowTask> getCompletedTasks(NodeRef nodeRef) {
        return getDocumentTasks(nodeRef, false);
    }
    @Override
    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef) {
        return getActiveWorkflows(nodeRef);
    }

    public List<WorkflowInstance> getActiveWorkflows(NodeRef nodeRef) {
        return getWorkflows(nodeRef, true);
    }

    public List<WorkflowInstance> getCompletedWorkflows(NodeRef nodeRef) {
        return getWorkflows(nodeRef, false);
    }

    public void stopDocumentSubWorkflows(String stateMachineExecutionId) {
        stopDocumentSubWorkflows(stateMachineExecutionId, null);
    }

    public void stopDocumentSubWorkflows(String stateMachineExecutionId, String currentExecutionId) {
        NodeRef document = getStatemachineDocument(stateMachineExecutionId);
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

    @Override
    public void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value) {
        if (definitionIds == null || definitionIds.size() == 0) return;
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
    public List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser) {
        List<WorkflowTask> result = new ArrayList<WorkflowTask>();

        List<WorkflowTask> tasks = getAssignedAndPooledTasks(fullyAuthenticatedUser);
        for (WorkflowTask task : tasks) {
            if (hasDocuments(task, documentTypes)) {
                result.add(task);
            }
        }

        return result;
    }

    private List<WorkflowInstance> getWorkflows(NodeRef nodeRef, boolean isActive) {
        boolean hasPermission = lecmPermissionService.hasPermission(LecmPermissionService.PERM_WF_LIST, nodeRef);
        if (!hasPermission) {
            return new ArrayList<WorkflowInstance>();
        }

        List<WorkflowInstance> activeWorkflows = serviceRegistry.getWorkflowService().getWorkflowsForContent(nodeRef, isActive);
        return filterWorkflows(activeWorkflows);
    }

    private List<WorkflowTask> getDocumentTasks(NodeRef documentRef, boolean activeTasks) {
        boolean hasPermission = lecmPermissionService.hasPermission(LecmPermissionService.PERM_WF_TASK_LIST, documentRef);
        if (!hasPermission) {
            return new ArrayList<WorkflowTask>();
        }

        List<WorkflowTask> result = new ArrayList<WorkflowTask>();
        WorkflowService workflowService = serviceRegistry.getWorkflowService();

        List<WorkflowInstance> activeWorkflows = workflowService.getWorkflowsForContent(documentRef, true);
        for (WorkflowInstance workflow : activeWorkflows) {
            List<WorkflowTask> tasks = getWorkflowTasks(workflow, activeTasks);
            result.addAll(tasks);
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
     * @param processId
     */
    public void terminateProcess(String processId) {
        ExecutionEntity process = (ExecutionEntity) activitiProcessEngineConfiguration.getRuntimeService().createProcessInstanceQuery().processInstanceId(processId).singleResult();
        //Завершаем процесс
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(process.getProcessDefinitionId());
        List<ActivityImpl> activities = definition.getActivities();
        ActivityImpl endActivity = null;
        for (ActivityImpl activity : activities) {
            if (activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior) {
                endActivity = activity;
            }
        }
        if (endActivity != null) {
            process.setProcessDefinition(definition);
            process.setActivity(endActivity);
            try {
                process.end();
            } catch (Exception e) {
                //logger.error(e.getMessage(), e);
            }

        }
        activitiProcessEngineConfiguration.getRuntimeService().deleteProcessInstance(processId, "cancelled");
    }

    public void executePostponedActions(String executionId) {
        String taskId = getCurrentTaskId(executionId);
        List<StateMachineAction> actions = getTaskActions(taskId, ExecutionListener.EVENTNAME_START);
        for(StateMachineAction action : actions) {
            if (action instanceof PostponedAction) {
                PostponedAction postponedAction = (PostponedAction) action;
                postponedAction.postponedExecution(taskId, this);
            }
        }
    }

    @Override
    public String getPreviousStatusName(NodeRef document) {
        String statemachineId = getStatemachineId(document);
        HistoryService historyService = activitiProcessEngineConfiguration.getHistoryService();
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery().executionId(statemachineId).orderByTaskId().desc().list();
        String result = null;
        if (tasks.size() > 1) {
            result = tasks.get(1).getName();
        }
        return result;
    }

    public void executeScript(String script, String statemachineId) {
        try {
            Execution execution = getExecution(statemachineId);
            if (execution != null) {
                Map<String, Object> vars =  activitiProcessEngineConfiguration.getRuntimeService().getVariables(execution.getId());
                WorkflowScript base = new WorkflowScript(vars, activitiProcessEngineConfiguration);
                base.setScript(new FixedValue(script));
                base.notify((DelegateExecution) execution);
            }
        } catch (Exception e) {
            logger.error("Error while script execution", e);
        }

    }

    private List<StateMachineAction> getStateMachineActions(String processDefinitionId, String activityId, String onFire) {
        List<StateMachineAction> result = new ArrayList<StateMachineAction>();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(processDefinitionId);
        ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
        List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
        if (listeners != null) {
            for (ExecutionListener listener : listeners) {
                if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
                    result = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get(onFire);
                }
            }
        }
        return result;
    }

    private List<StateMachineAction> getStatusChangeActions(NodeRef document) {
        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        String taskId = getCurrentTaskId(executionId);
        return getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(StatusChangeAction.class), ExecutionListener.EVENTNAME_START);
    }

    private String getWorkflowDescription(String executionId) {
        WorkflowInstance workflow = serviceRegistry.getWorkflowService().getWorkflowById(executionId);
        return workflow.getDefinition().getTitle();
    }

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

    private List<WorkflowInstance> filterWorkflows(List<WorkflowInstance> workflows) {
        List<WorkflowInstance> result = new ArrayList<WorkflowInstance>();
        NodeRef workflowSysUser = serviceRegistry.getPersonService().getPerson("workflow");
        for (WorkflowInstance instance : workflows) {
            if (!workflowSysUser.equals(instance.getInitiator()) && !isServiceWorkflow(instance)) {
                result.add(instance);
            }
        }
        return result;
    }

    private TransitionResponse executeTransitionAction(final NodeRef document, final String statemachineId, String taskId, String actionId, String persistedResponse) {
        TransitionResponse response = new TransitionResponse();
        List<String> errors = new ArrayList<String>();
        List<StateMachineAction> actions = getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class), ExecutionListener.EVENTNAME_TAKE);
        FinishStateWithTransitionAction.NextState nextState = null;
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
            if (nextState.isStopSubWorkflows()) {
                new StateMachineHelper().stopDocumentSubWorkflows(statemachineId);
            }

            if (!"".equals(nextState.getScript())) {
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
                setExecutionParamentersByTaskId(taskId, parameters);
                nextTransition(taskId);
            }

            if (!nextState.isForm()) {
                String dependencyExecution = parseExecutionId(persistedResponse);
                if (dependencyExecution != null) {
                    WorkflowDescriptor descriptor = new WorkflowDescriptor(dependencyExecution, statemachineId, nextState.getWorkflowId(), taskId, StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class), actionId, ExecutionListener.EVENTNAME_TAKE);
                    new DocumentWorkflowUtil().addWorkflow(document, dependencyExecution, descriptor);
                    setInputVariables(statemachineId, dependencyExecution, nextState.getVariables().getInput());

                    //Добавляем участников к документу.
                    List<NodeRef> assignees = getAssigneesForWorkflow(dependencyExecution);
                    for (final NodeRef assignee : assignees) {
                        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
                            @Override
                            public NodeRef doWork() throws Exception {
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
        StateMachineHelper helper = new StateMachineHelper();
        List<StateMachineAction> actions = helper.getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(UserWorkflow.class), ExecutionListener.EVENTNAME_TAKE);
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
            new DocumentWorkflowUtil().addWorkflow(document, dependencyExecution, descriptor);

            helper.setInputVariables(statemachineId, dependencyExecution, workflow.getVariables().getInput());

            //Добавляем участников к документу.
            List<NodeRef> assignees = helper.getAssigneesForWorkflow(dependencyExecution);
            for (final NodeRef assignee : assignees) {
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
                    @Override
                    public NodeRef doWork() throws Exception {
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
        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        if (executionId != null) {
            String taskId = getCurrentTaskId(executionId);
            if (taskId != null) {
                List<StateMachineAction> actions = getStatusChangeActions(document);
                Set<StateField> result = new HashSet<StateField>();
                for (StateMachineAction action : actions) {
                    StatusChangeAction statusChangeAction = (StatusChangeAction) action;
                    result.addAll(statusChangeAction.getCategories());
                }
                return new StateFields(true, result);
            }
        }
        return new StateFields(false);
    }

    @Override
    public boolean isFinal(NodeRef document) {
        Object statemachineId = serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        return statemachineId != null && getExecution((String) statemachineId) == null;
    }

	@Override
    public void sendSignal(String executionId) {
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

    private boolean hasDocuments(WorkflowTask task, List<String> documentTypes) {
        return getTaskDocument(task, documentTypes) != null;
    }

    public NodeRef getTaskDocument(WorkflowTask task, List<String> documentTypes) {
        NodeRef wfPackage = (NodeRef) task.getProperties().get(WorkflowModel.ASSOC_PACKAGE);
        List<ChildAssociationRef> childAssocs = serviceRegistry.getNodeService().getChildAssocs(wfPackage);
        for (ChildAssociationRef childAssoc : childAssocs) {
            NodeRef document = childAssoc.getChildRef();
            QName documentType = serviceRegistry.getNodeService().getType(document);
            if (documentTypes.contains(documentType.getLocalName())) {
                return document;
            }
        }

        return null;
    }

    /**
     * Выборка имен статусов для определенного описателя процесса
     * @param processDefinitionEntity
     * @return Список имен статусов
     */
    private HashSet<String> getDefinitionStatuses(ProcessDefinitionEntity processDefinitionEntity, boolean includeActive, boolean includeFinal) {
        HashSet<String> statuses = new HashSet<String>();
        List<ActivityImpl> activities = processDefinitionEntity.getActivities();
        for (ActivityImpl activity : activities) {
            List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
            if (listeners != null) {
                for (ExecutionListener listener : listeners) {
                    if (listener instanceof StateMachineHandler.StatemachineTaskListener) {
                        List<StateMachineAction> result;
                        if (includeActive) {
                            result = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("start");
                            for (StateMachineAction action : result) {
                                if (action.getActionName().equalsIgnoreCase(StateMachineActionsImpl.getActionNameByClass(StatusChangeAction.class))) {
                                    StatusChangeAction statusAction = (StatusChangeAction) action;
                                    statuses.add(statusAction.getStatus());
                                }
                            }
                        }
                        if (includeFinal) {
                            result = ((StateMachineHandler.StatemachineTaskListener) listener).getEvents().get("end");
                            for (StateMachineAction action : result) {
                                if (action.getActionName().equalsIgnoreCase(StateMachineActionsImpl.getActionNameByClass(ArchiveDocumentAction.class))) {
                                    ArchiveDocumentAction archiveDocumentAction = (ArchiveDocumentAction) action;
                                    statuses.add(archiveDocumentAction.getStatusName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return statuses;
    }

}
