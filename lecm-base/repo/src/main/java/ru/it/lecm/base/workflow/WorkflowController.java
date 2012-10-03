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
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 07.09.12
 * Time: 16:22
 */
public class WorkflowController {

    private static ServiceRegistry serviceRegistry;
    private static AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;

    private static String ACTIVITI_PREFIX = "activiti$";
    private static String BPM_PACKAGE_PREFIX = "bpm_";

    private boolean isInitialized = false;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        WorkflowController.serviceRegistry = serviceRegistry;
    }

    public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
        WorkflowController.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void executeTask(final String taskId, final String workflowId, final String assignee) {
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
                        workflowProps.put(WorkflowModel.PROP_COMMENT, taskId);
                        // get the moderated workflow
                        WorkflowDefinition wfDefinition = workflowService.getDefinitionByName(ACTIVITI_PREFIX + workflowId);
                        if (wfDefinition == null) {
                            // handle workflow definition does not exist
                            throw new IllegalStateException("noworkflow: " + workflowId);
                        }
                        // start the workflow
                        WorkflowPath wfPath = workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
                        //workflowService.endTask(task.getId(), null);
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);

            }
        };

        timer.schedule(task, 1000);
    }

    public void endProcess(DelegateExecution delegateExecution) {
        Long taskId = null;
        try {
            taskId = Long.valueOf((String)delegateExecution.getVariable("bpm_comment"));
        } catch (NumberFormatException e) {
        }
        if (taskId != null) {
            WorkflowService workflowService = serviceRegistry.getWorkflowService();
            WorkflowTask task = workflowService.getTaskById(ACTIVITI_PREFIX + taskId.toString());
            String processId = task.getPath().getId().replace(ACTIVITI_PREFIX, "");
            RuntimeService runtimeService = activitiProcessEngineConfiguration.getRuntimeService();
            Map<String, Object> variables = delegateExecution.getVariables();
            for (String key : variables.keySet()) {
                if (!key.startsWith(BPM_PACKAGE_PREFIX)) {
                    runtimeService.setVariable(processId, key, delegateExecution.getVariable(key));
                }
            }
            workflowService.endTask(ACTIVITI_PREFIX + taskId.toString(), null);
        }
    }

/*
    @Override
    public void afterPropertiesSet() throws Exception {

        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<String>() {
            @Override
            public String doWork() throws Exception {
                NodeService nodeService = serviceRegistry.getNodeService();
                StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
                NodeRef root = nodeService.getRootNode(storeRef);
                SearchService search = serviceRegistry.getSearchService();
                ResultSet result = search.query(storeRef, SearchService.LANGUAGE_XPATH, "/app:company_home/cm:workflow");
                if (result.length() == 0) {
                    Map<QName, Serializable> props = null;
                    props.put(ContentModel.PROP_NAME, "workflow");
                    nodeService.createNode(
                            new NodeRef(storeRef, "/app:company_home/"),
                            QName.createQName("cm:contains"),
                            null,
                            QName.createQName("cm:folder"),
                            props
                    );
                }
                return "";
            }
        });

    }
*/


}
