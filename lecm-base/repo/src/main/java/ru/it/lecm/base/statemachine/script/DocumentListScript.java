package ru.it.lecm.base.statemachine.script;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.*;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.statemachine.StateMachineHelper;
import ru.it.lecm.base.statemachine.StateMachineModel;
import ru.it.lecm.base.statemachine.action.ChooseStateAction;
import ru.it.lecm.base.statemachine.action.StateMachineAction;
import ru.it.lecm.base.statemachine.bean.DocumentStateMachineBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:49
 */
public class DocumentListScript extends DeclarativeWebScript {

    private static ServiceRegistry serviceRegistry;
    private  static DocumentStateMachineBean documentStateMachineBean;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> result = new HashMap<String, Object>();
        String documentType = req.getParameter("documentType");
        String stateMachineId = documentStateMachineBean.getStateMachines().get(documentType);
        if (stateMachineId != null) {
            WorkflowService workflowService = serviceRegistry.getWorkflowService();
            NodeService nodeService = serviceRegistry.getNodeService();
            List<WorkflowDefinition> definitions = workflowService.getAllDefinitionsByName("activiti$" + stateMachineId);
            ArrayList<Map<String, Object>> documents = new ArrayList<Map<String, Object>>();
            for (WorkflowDefinition definition : definitions) {
                List<WorkflowInstance> instances = workflowService.getWorkflows(definition.getId());
                for (WorkflowInstance instance : instances) {
                     List<WorkflowPath> paths = workflowService.getWorkflowPaths(instance.getId());
                    for (WorkflowPath path : paths) {
                        List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(path.getId());
                        for (WorkflowTask task : tasks) {
                            Map<QName, Serializable> properties = task.getProperties();
                            NodeRef packageRef = (NodeRef) properties.get(WorkflowModel.ASSOC_PACKAGE);
                            List<ChildAssociationRef> children = nodeService.getChildAssocs(packageRef);
                            for (ChildAssociationRef child : children) {
                                NodeRef documentRef = child.getChildRef();
                                if (nodeService.getProperty(documentRef, StateMachineModel.PROP_STATUS) != null) {
                                    HashMap<String, Object> document = new HashMap<String, Object>();
                                    document.put("nodeRef", documentRef.toString());
                                    document.put("name", nodeService.getProperty(documentRef, ContentModel.PROP_NAME).toString());
                                    document.put("status", nodeService.getProperty(documentRef, StateMachineModel.PROP_STATUS).toString());
                                    document.put("taskId", task.getId());
                                    ArrayList<HashMap<String, String>> resultStates = new ArrayList<HashMap<String, String>>();
                                    List<StateMachineAction> actions = new StateMachineHelper().getTaskActionsByName(task.getId(), "chooseState", "take");
                                    for (StateMachineAction action : actions) {
                                        ChooseStateAction chooseAction = (ChooseStateAction) action;
                                        List<ChooseStateAction.NextState> states =  chooseAction.getStates();
                                        for (ChooseStateAction.NextState state : states) {
                                            HashMap<String, String> resultState = new HashMap<String, String>();
                                            resultState.put("actionId", state.getActionId());
                                            resultState.put("label", state.getLabel());
                                            resultState.put("workflowId", state.getWorkflowId());
                                            resultStates.add(resultState);
                                        }
                                    }
                                    document.put("states", resultStates);
                                    documents.add(document);
                                }
                            }
                        }
                    }
                }
            }
            result.put("documents", documents);
        } else {
        }
        return result;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        DocumentListScript.serviceRegistry = serviceRegistry;
    }

    public void setDocumentStateMachineBean(DocumentStateMachineBean documentStateMachineBean) {
        DocumentListScript.documentStateMachineBean = documentStateMachineBean;
    }
}
