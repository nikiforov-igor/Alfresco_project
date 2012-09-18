package ru.it.lecm.base.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.json.JSONObject;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;

import java.util.Timer;
import java.util.TimerTask;

/**
 * User: PMelnikov
 * Date: 07.09.12
 * Time: 16:22
 */
public class WorkflowController {

    private static ServiceRegistry serviceRegistry;
    private static String activitiPrefix = "activiti$";

    private boolean isInitialized = false;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        WorkflowController.serviceRegistry = serviceRegistry;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void executeTask(final String taskId, final JSONObject descriptor) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                WorkflowService workflowService = serviceRegistry.getWorkflowService();
                WorkflowTask task = workflowService.getTaskById(activitiPrefix + taskId);
                workflowService.endTask(task.getId(), null);
            }
        };
        timer.schedule(task, 1000);
    }

    public void terminateSubprocess(DelegateExecution delegateExecution) {

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
