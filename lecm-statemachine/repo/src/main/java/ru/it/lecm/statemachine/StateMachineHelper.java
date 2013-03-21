package ru.it.lecm.statemachine;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.*;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.StatusChangeAction;
import ru.it.lecm.statemachine.action.WorkflowVariables;
import ru.it.lecm.statemachine.assign.AssignExecution;
import ru.it.lecm.statemachine.bean.StateMachineActions;
import ru.it.lecm.statemachine.bean.WorkflowListPageBean;
import ru.it.lecm.statemachine.bean.WorkflowTaskListPageBean;
import ru.it.lecm.statemachine.listener.StateMachineHandler;

import java.io.Serializable;
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

	public static String ACTIVITI_PREFIX = "activiti$";

	private static ServiceRegistry serviceRegistry;
	private static AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
	private static OrgstructureBean orgstructureBean;


	private static String BPM_PACKAGE_PREFIX = "bpm_";

	private static String PROP_PARENT_PROCESS_ID = "parentProcessId";

	private static HashSet<String> ignoredKeys = new HashSet<String>();

	static {
		ignoredKeys.add("cancelled");
		ignoredKeys.add("workflowinstanceid");
		ignoredKeys.add("companyhome");
		ignoredKeys.add("parentProcessId");
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineHelper.serviceRegistry = serviceRegistry;
	}

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		StateMachineHelper.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        StateMachineHelper.orgstructureBean = orgstructureBean;
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
		for (ChildAssociationRef document : documents) {
			nodeService.addChild(subprocessPackage, document.getChildRef(), ContentModel.ASSOC_CONTAINS, document.getQName());
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

		return instanceId;

	}

    public void stopUserWorkflowProcessing(DelegateExecution delegateExecution) {
		Object taskId = delegateExecution.getVariable(PROP_PARENT_PROCESS_ID);
		if (taskId != null) {
			WorkflowService workflowService = serviceRegistry.getWorkflowService();
			WorkflowTask task = workflowService.getTaskById(ACTIVITI_PREFIX + taskId.toString());
			String processId = task.getPath().getId().replace(ACTIVITI_PREFIX, "");
			RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
			Map<String, Object> variables = delegateExecution.getVariables();
			for (String key : variables.keySet()) {
				if (!key.startsWith(BPM_PACKAGE_PREFIX) && !ignoredKeys.contains(key)) {
					runtimeService.setVariable(processId, key, delegateExecution.getVariable(key));
				}
			}
			workflowService.endTask(ACTIVITI_PREFIX + taskId.toString(), null);
		}
	}

	public void startDocumentProcessing(final String taskId) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

					@Override
					public Object doWork() throws Exception {
						NodeService nodeService = serviceRegistry.getNodeService();

						WorkflowService workflowService = serviceRegistry.getWorkflowService();
						WorkflowTask task = workflowService.getTaskById(ACTIVITI_PREFIX + taskId);

						NodeRef wfPackage = (NodeRef) task.getProperties().get(WorkflowModel.ASSOC_PACKAGE);

						NodeRef document = null;
						List<ChildAssociationRef> documents = nodeService.getChildAssocs(wfPackage);
						for (ChildAssociationRef item : documents) {
							document = item.getChildRef();
						}

						if (!nodeService.hasAspect(document, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
							nodeService.addAspect(document, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK, null);
						}
						nodeService.setProperty(document, StatemachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS, taskId);
						return null;
					}
				}, AuthenticationUtil.SYSTEM_USER_NAME);
			}
		};
		timer.schedule(task, 1000);
	}

	public void stopDocumentProcessing(String taskId) {
		nextTransition(ACTIVITI_PREFIX + taskId);
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
	 * Выбирает список действий для старта процесса последней версии.
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
		WorkflowService workflowService = serviceRegistry.getWorkflowService();
		return workflowService.endTask(taskId, null).getId();
	}

	@Override
	public String getCurrentTaskId(String executionId) {
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
    public Set<StateField> getStateFields(NodeRef document) {
        List<StateMachineAction> actions = getStatusChangeActions(document);
        Set<StateField> result = new HashSet<StateField>();
        for (StateMachineAction action : actions) {
            StatusChangeAction statusChangeAction = (StatusChangeAction) action;
            result.addAll(statusChangeAction.getFields());
        }
        return result;
    }

    @Override
    public boolean hasPrivilegeByEmployee(NodeRef employee, NodeRef document, String privilege) {
        HashSet<String> privileges = new HashSet<String>();
        privileges.add(privilege);
        return  hasPrivilegeByEmployee(employee, document, privileges);
    }

    @Override
    public boolean hasPrivilegeByEmployee(NodeRef employee, NodeRef document, Collection<String> privileges) {
        List<NodeRef> businessRoles = orgstructureBean.getEmployeeRoles(employee);
        //Выбираем роли сотрудника
        HashSet<String> employeeRoles = new HashSet<String>();
        for (NodeRef role : businessRoles) {
            String name = (String) serviceRegistry.getNodeService().getProperty(role, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
            employeeRoles.add(name);
        }

        //Выбираем привилегии для ролей сотрудника
        HashSet<String> employeePrivileges = new HashSet<String>();
        List<StateMachineAction> actions = getStatusChangeActions(document);
        for (StateMachineAction action : actions) {
            StatusChangeAction statusChangeAction = (StatusChangeAction) action;
            Map<String, String> statePrivileges = statusChangeAction.getPrivileges();
            for (String role : employeeRoles) {
                String privilege = statePrivileges.get(role);
                if (privilege != null) {
                    employeePrivileges.add(privilege);
                }
            }
        }

        for (String privilege : privileges) {
            if (employeePrivileges.contains(privilege)) return true;
        }

        return false;
    }

    @Override
    public boolean hasPrivilegeByPerson(NodeRef person, NodeRef document, String privilege) {
        HashSet<String> privileges = new HashSet<String>();
        privileges.add(privilege);
        return hasPrivilegeByPerson(person, document, privileges);
    }

    @Override
    public boolean hasPrivilegeByPerson(NodeRef person, NodeRef document, Collection<String> privileges) {
        NodeRef employee = orgstructureBean.getEmployeeByPerson(person);
        if (employee == null) {
            return false;
        } else {
            return hasPrivilegeByEmployee(employee, document, privileges);
        }
    }

    public String getCurrentExecutionId(String taskId) {
		TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		Task task = taskQuery.taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
		return task.getExecutionId();
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
					value = nodeService.getProperty(document, propertyName).toString();
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

	public NodeRef getStatemachineDocument(String executionId) {
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

	public static ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

    @Override
    public boolean isDraft(NodeRef document) {
        boolean result = false;
        List<StateMachineAction> actions = getStatusChangeActions(document);
        for (StateMachineAction action : actions) {
            StatusChangeAction statusChangeAction = (StatusChangeAction) action;
            result = result || statusChangeAction.isForDraft();
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

    private List<StateMachineAction> getStateMachineActions(String processDefinitionId, String activityId, String onFire) {
		List<StateMachineAction> result = new ArrayList<StateMachineAction>();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService()).getDeployedProcessDefinition(processDefinitionId);
		ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
		List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
		for (ExecutionListener listener : listeners) {
			if (listener instanceof StateMachineHandler) {
				result = ((StateMachineHandler) listener).getEvents().get(onFire);
			}
		}
		return result;
	}

    private List<StateMachineAction> getStatusChangeActions(NodeRef document) {
        String executionId = (String) serviceRegistry.getNodeService().getProperty(document, StatemachineModel.PROP_STATEMACHINE_ID);
        String taskId = getCurrentTaskId(executionId);
        return getTaskActionsByName(taskId, StateMachineActions.getActionName(StatusChangeAction.class), ExecutionListener.EVENTNAME_START);
    }

    enum BPMState {
        NA, ACTIVE, COMPLETED, ALL;

        public static BPMState getValue(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (Exception e) {
                return NA;
            }
        }
    }

    @Override
    public WorkflowTaskListBean getTasks(NodeRef nodeRef, String stateParam, boolean isAddSubordinatesTask, int myTasksLimit) {
        if (nodeRef == null) {
            return new WorkflowTaskListPageBean();
        }
        BPMState state = BPMState.getValue(stateParam);

        List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
        if (state == BPMState.ACTIVE || state == BPMState.ALL) {
            tasks.addAll(getDocumentTasks(nodeRef, true));
        }

        if (state == BPMState.COMPLETED || state == BPMState.ALL) {
            tasks.addAll(getDocumentTasks(nodeRef, false));
        }

        WorkflowTaskListPageBean result = new WorkflowTaskListPageBean();

        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        boolean isBoss = orgstructureBean.isBoss(currentEmployee);
        result.setShowSubordinateTasks(isBoss);

        List<WorkflowTask> myTasks = filterTasksByAssignees(tasks, Collections.singletonList(currentEmployee));
        result.setMyTasks(myTasks, myTasksLimit);

        if (isAddSubordinatesTask) {
            List<NodeRef> subordinateEmployees = orgstructureBean.getBossSubordinate(currentEmployee);
            List<WorkflowTask> subordinatesTasks = filterTasksByAssignees(tasks, subordinateEmployees);
            result.setSubordinatesTasks(subordinatesTasks);
        }

        return result;
    }

    private List<WorkflowTask> filterTasksByAssignees(List<WorkflowTask> tasks, List<NodeRef> assigneesEmployees) {
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
            NodeRef ownerPerson = serviceRegistry.getPersonService().getPerson(owner);
            if (persons.contains(ownerPerson)) {
                result.add(task);
            }
        }

        return result;
    }

    @Override
    public List<WorkflowTask> getDocumentTasks(NodeRef nodeRef) {
        return getDocumentTasks(nodeRef, true);
    }

    @Override
    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef) {
        List<WorkflowInstance> result = new ArrayList<WorkflowInstance>();
        NodeRef workflowSysUser = serviceRegistry.getPersonService().getPerson("workflow");
        List<WorkflowInstance> activeWorkflows = serviceRegistry.getWorkflowService().getWorkflowsForContent(nodeRef, true);
        for (WorkflowInstance instance :activeWorkflows) {
            if (!workflowSysUser.equals(instance.getInitiator())) {
                result.add(instance);
            }
        }
        return result;
    }

    private List<WorkflowTask> getDocumentTasks(NodeRef nodeRef, boolean activeTasks) {
        List<WorkflowTask> result = new ArrayList<WorkflowTask>();
        WorkflowService workflowService = serviceRegistry.getWorkflowService();

        List<WorkflowInstance> activeWorkflows = workflowService.getWorkflowsForContent(nodeRef, true);
        for (WorkflowInstance workflow : activeWorkflows) {
            List<WorkflowTask> tasks = getWorkflowTasks(workflow, activeTasks);
            result.addAll(tasks);
        }

        if (!activeTasks) {
            List<WorkflowInstance> completedWorkflows = workflowService.getWorkflowsForContent(nodeRef, false);
            for (WorkflowInstance workflow : completedWorkflows) {
                List<WorkflowTask> tasks = getWorkflowTasks(workflow, false);
                result.addAll(tasks);
            }
        }

        return result;
    }

    private List<WorkflowTask> getWorkflowTasks(WorkflowInstance workflow, boolean activeTasks) {
        List<WorkflowTask> result = new ArrayList<WorkflowTask>();
        WorkflowService workflowService = serviceRegistry.getWorkflowService();

        WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
        taskQuery.setProcessId(workflow.getId());
        taskQuery.setTaskState(activeTasks ? WorkflowTaskState.IN_PROGRESS : WorkflowTaskState.COMPLETED);
        taskQuery.setActive(activeTasks);

        WorkflowTask startTask = workflowService.getStartTask(workflow.getId());
        List<WorkflowTask> workflowTasks = workflowService.queryTasks(taskQuery);
        for (WorkflowTask workflowTask : workflowTasks) {
            if (!startTask.getId().equals(workflowTask.getId())) {
                result.add(workflowTask);
            }
        }

        return result;
    }

    @Override
    public WorkflowListBean getWorkflows(NodeRef nodeRef, String stateParam, int activeWorkflowsLimit) {
        if (nodeRef == null) {
            return new WorkflowListPageBean();
        }
        BPMState workflowState = BPMState.getValue(stateParam);

        WorkflowService workflowService = serviceRegistry.getWorkflowService();
        WorkflowListPageBean result = new WorkflowListPageBean();

        List<WorkflowInstance> activeWorkflows = workflowService.getWorkflowsForContent(nodeRef, true);
        result.setActiveWorkflows(activeWorkflows, activeWorkflowsLimit);

        if (workflowState == BPMState.ALL) {
            List<WorkflowInstance> completedWorkflows = workflowService.getWorkflowsForContent(nodeRef, false);
            result.setCompletedWorkflows(completedWorkflows);
        }

        return result;
    }

}
