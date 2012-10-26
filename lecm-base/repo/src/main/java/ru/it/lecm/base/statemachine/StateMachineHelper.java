package ru.it.lecm.base.statemachine;

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
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.statemachine.action.StateMachineAction;
import ru.it.lecm.base.statemachine.action.WorkflowVariables;
import ru.it.lecm.base.statemachine.listener.StateMachineHandler;

import java.io.Serializable;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 07.09.12
 * Time: 16:22
 *
 * Вспомогательный класс для Activiti BPM Platform.
 *
 * Позволяет:
 * 1. Запускать пользовательские процессы из машины состояний
 * 2. Передавать сигнал о завершении пользовательского процесс машине состояний с передачей переменных из пользовательского процесса
 *
 */
public class StateMachineHelper {

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

    public void startUserWorkflowProcessing(final String taskId, final String workflowId, final String assignee) {
        startUserWorkflowProcessing(taskId, workflowId, assignee, false);
    }

    public void startUserWorkflowProcessing(final String taskId, final String workflowId, final String assignee, final boolean async) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

                    @Override
                    public Object doWork() throws Exception {
                        // код
                        WorkflowService workflowService = serviceRegistry.getWorkflowService();
                        WorkflowTask task = workflowService.getTaskById(ACTIVITI_PREFIX + taskId);

                        PersonService personService = serviceRegistry.getPersonService();
                        NodeRef assigneeNodeRef = personService.getPerson(assignee);

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
                        workflowProps.put(WorkflowModel.ASSOC_ASSIGNEES, assigneeNodeRef);
                        workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, assigneeNodeRef);
                        if (!async) {
                            workflowProps.put(QName.createQName("{}" + PROP_PARENT_PROCESS_ID), Long.valueOf(taskId));
                        }
                        // get the moderated workflow
                        WorkflowDefinition wfDefinition = workflowService.getDefinitionByName(ACTIVITI_PREFIX + workflowId);
                        if (wfDefinition == null) {
                            // handle workflow definition does not exist
                            throw new IllegalStateException("noworkflow: " + workflowId);
                        }
                        // start the workflow
                        workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
                        //workflowService.endTask(task.getId(), null);
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);

            }
        };

        timer.schedule(task, 1000);
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

                        if (!nodeService.hasAspect(document, StateMachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
                            nodeService.addAspect(document, StateMachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK, null);
                        }
                        nodeService.setProperty(document, StateMachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS, taskId);
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
            ProcessInstance process= runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService() )
                    .getDeployedProcessDefinition(process.getProcessDefinitionId());
            ActivityImpl activity = processDefinitionEntity.findActivity(((ExecutionEntity) execution).getActivityId());
            List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
            for (ExecutionListener listener : listeners) {
                if (listener instanceof StateMachineHandler) {
                    result = ((StateMachineHandler) listener).getEvents().get(onFire);
                }
            }
        }
        return result;
    }

    public List<StateMachineAction> getTaskActionsByName(String taskId, String actionType, String onFire) {
        List<StateMachineAction> actions = getTaskActions(taskId, onFire);
        List<StateMachineAction> result = new ArrayList<StateMachineAction>();
        for (StateMachineAction action : actions) {
            if (action.getActionName().equalsIgnoreCase(actionType)) {
                result.add(action);
            }
        }
        return  result;
    }

    public List<StateMachineAction> getHistoricalTaskActions(String taskId, String onFire) {
        List<StateMachineAction> result = new ArrayList<StateMachineAction>();
        HistoryService historyService = activitiProcessEngineConfiguration.getHistoryService();
        RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId.replace(ACTIVITI_PREFIX, "")).singleResult();
        if (task != null) {
            ProcessInstance process= runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) activitiProcessEngineConfiguration.getRepositoryService() )
                    .getDeployedProcessDefinition(process.getProcessDefinitionId());
            ActivityImpl activity = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());
            List<ExecutionListener> listeners = activity.getExecutionListeners().get("start");
            for (ExecutionListener listener : listeners) {
                if (listener instanceof StateMachineHandler) {
                    result = ((StateMachineHandler) listener).getEvents().get(onFire);
                }
            }
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
        return  result;
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

    public void nextTransition(String taskId) {
        WorkflowService workflowService = serviceRegistry.getWorkflowService();
        workflowService.endTask(taskId, null);
    }

    public String getCurrentTaskId(String executionId) {
        TaskService taskService = activitiProcessEngineConfiguration.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task task = taskQuery.executionId(executionId.replace(ACTIVITI_PREFIX, "")).singleResult();
        return ACTIVITI_PREFIX + task.getId();
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
            if (variable.getFrom() == null) {
                runtimeService.setVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getTo(), variable.getValue());
            } else {
                Object value = runtimeService.getVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFrom());
                runtimeService.setVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getTo(), value);
            }
        }
    }

    public void getOutputVariables(String stateMachineExecutionId, String workflowExecutionId, List<WorkflowVariables.WorkflowVariable> variables) {
        RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
        for (WorkflowVariables.WorkflowVariable variable : variables) {
            if (variable.getFrom() == null) {
                runtimeService.setVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getTo(), variable.getValue());
            } else {
                Object value = runtimeService.getVariable(workflowExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getFrom());
                runtimeService.setVariable(stateMachineExecutionId.replace(ACTIVITI_PREFIX, ""), variable.getTo(), value);
            }
        }
    }

}
