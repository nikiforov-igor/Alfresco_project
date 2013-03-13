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
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.*;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.WorkflowVariables;
import ru.it.lecm.statemachine.assign.AssignExecution;
import ru.it.lecm.statemachine.bean.WorkflowTaskPageBean;
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

    @Override
    public List<WorkflowTaskBean> getMyActiveTasks(String nodeRef) {
        return getMyTasks(nodeRef, true);
    }

    @Override
    public List<WorkflowTaskBean> getMyCompleteTasks(String nodeRef) {
        return getMyTasks(nodeRef, false);
    }

    private List<WorkflowTaskBean> getMyTasks(String nodeRef, boolean isActive) {
        List<WorkflowTask> tasks = getTasksForContent(nodeRef, isActive);

        PersonService personService = serviceRegistry.getPersonService();
        NodeRef loggedUser = personService.getPerson(AuthenticationUtil.getFullyAuthenticatedUser());

        List<WorkflowTask> myTasks = filterTasks(tasks, Collections.singletonList(loggedUser));

        return wrapTasks(myTasks);
    }

    private List<WorkflowTask> getTasksForContent(String nodeRef, boolean isActive) {
        if (nodeRef == null || !NodeRef.isNodeRef(nodeRef)) {
            return new ArrayList<WorkflowTask>();
        }

        List<WorkflowTask> result = new ArrayList<WorkflowTask>();
        WorkflowService workflowService = serviceRegistry.getWorkflowService();

        NodeRef contentNodeRef = new NodeRef(nodeRef);
        List<WorkflowInstance> activeWorkflows = workflowService.getWorkflowsForContent(contentNodeRef, true);
        for (WorkflowInstance workflow : activeWorkflows) {
            List<WorkflowTask> tasks = getWorkflowTasks(workflow, isActive);
            result.addAll(tasks);
        }

        if (!isActive) {
            List<WorkflowInstance> completedWorkflows = workflowService.getWorkflowsForContent(contentNodeRef, false);
            for (WorkflowInstance workflow : completedWorkflows) {
                List<WorkflowTask> tasks = getWorkflowTasks(workflow, false);
                result.addAll(tasks);
            }
        }

        return result;
    }

    private List<WorkflowTask> getWorkflowTasks(WorkflowInstance workflow, boolean isActive) {
        List<WorkflowTask> result = new ArrayList<WorkflowTask>();
        WorkflowService workflowService = serviceRegistry.getWorkflowService();

        WorkflowTask startTask = workflowService.getStartTask(workflow.getId());

        WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
        taskQuery.setProcessId(workflow.getId());
        taskQuery.setTaskState(isActive ? WorkflowTaskState.IN_PROGRESS : WorkflowTaskState.COMPLETED);
        taskQuery.setActive(isActive);

        List<WorkflowTask> workflowTasks = workflowService.queryTasks(taskQuery);
        for (WorkflowTask workflowTask : workflowTasks) {
            if (!startTask.getId().equals(workflowTask.getId())) {
                result.add(workflowTask);
            }
        }

        return result;
    }

    //TODO: compare by owner names
    private List<WorkflowTask> filterTasks(List<WorkflowTask> tasks, List<NodeRef> assignees) {
        List<WorkflowTask> result = new ArrayList<WorkflowTask>();

        for (WorkflowTask task : tasks) {
            String owner = (String) task.getProperties().get(ContentModel.PROP_OWNER);
            NodeRef ownerNodeRef = serviceRegistry.getPersonService().getPerson(owner);

            if (assignees.contains(ownerNodeRef)) {
                result.add(task);
            }
        }

        return result;
    }

    private List<WorkflowTaskBean> wrapTasks(List<WorkflowTask> tasks) {
        List<WorkflowTaskBean> result = new ArrayList<WorkflowTaskBean>();
        for (WorkflowTask task : tasks) {
            result.add(new WorkflowTaskPageBean(task));
        }

        return result;
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
		Task task = taskQuery.taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
		if (task != null) {
			Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
			String activityId = ((ExecutionEntity) execution).getActivityId();
			ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			String processDefinitionId = process.getProcessDefinitionId();
			result = getStateMachineActions(processDefinitionId, activityId, onFire);
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
		RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
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

	public void setExecutionParamentersByTaskId(String taskId, Map<String, String> parameters) {
		TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		Task task = taskQuery.taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
		if (task != null) {
			setExecutionParameters(task.getExecutionId(), parameters);
		}
	}

	public void setExecutionParameters(String executionId, Map<String, String> parameters) {
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
}
