package ru.it.lecm.base.workflow;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
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
import ru.it.lecm.base.workflow.policy.WorkflowPolicy;

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
public class WorkflowHelper {

    private static ServiceRegistry serviceRegistry;
    private static AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;

    private static String ACTIVITI_PREFIX = "activiti$";
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
        WorkflowHelper.serviceRegistry = serviceRegistry;
    }

    public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
        WorkflowHelper.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
    }

    public void startUserWorkflowProcessing(final String taskId, final String workflowId, final String assignee) {
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
                        workflowProps.put(QName.createQName("{}" + PROP_PARENT_PROCESS_ID), Long.valueOf(taskId));
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

                        if (!nodeService.hasAspect(document, WorkflowPolicy.WORKFLOW_DOCUMENT_TASK_ASPECT)) {
                            nodeService.addAspect(document, WorkflowPolicy.WORKFLOW_DOCUMENT_TASK_ASPECT, null);
                        }
                        nodeService.setProperty(document, WorkflowPolicy.WORKFLOW_DOCUMENT_TASK_STATE_PROCESS_PROPERTY, taskId);
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);
            }
        };
        timer.schedule(task, 1000);
    }

    public void stopDocumentProcessing(String taskId) {
        WorkflowService workflowService = serviceRegistry.getWorkflowService();
        workflowService.endTask(ACTIVITI_PREFIX + taskId, null);
    }

}
