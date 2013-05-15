package ru.it.lecm.statemachine.script;

import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.documents.beans.DocumentFrequencyAnalysisService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.action.Conditions;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.bean.StateMachineActions;
import ru.it.lecm.statemachine.expression.Expression;

import java.io.Serializable;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:49
 */
public class ActionsScript extends DeclarativeWebScript {

    private static ServiceRegistry serviceRegistry;
    private static DocumentFrequencyAnalysisService frequencyAnalysisService;
    private static OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        ActionsScript.orgstructureService = orgstructureService;
    }

    public void setFrequencyAnalysisService(DocumentFrequencyAnalysisService frequencyAnalysisService) {
        ActionsScript.frequencyAnalysisService = frequencyAnalysisService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        if (req.getParameter("documentNodeRef") == null) {
            JSONObject jsonResponse = new JSONObject();
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("result", jsonResponse.toString());
            return response;
        }

        NodeRef nodeRef = new NodeRef(req.getParameter("documentNodeRef"));
        NodeService nodeService = serviceRegistry.getNodeService();
        String statemachineId = (String) nodeService.getProperty(nodeRef, StatemachineModel.PROP_STATEMACHINE_ID);
        if (statemachineId != null) {
            if (req.getParameter("actionId") != null) {
                String actionId = req.getParameter("actionId");
                HashMap<String, Object> result = getActions(nodeRef, statemachineId);
                ArrayList<HashMap<String, Object>> actions = (ArrayList<HashMap<String, Object>>) result.get("actions");
                HashMap<String, Object> action = null;
                for (HashMap<String, Object> a : actions) {
                    if (a.get("actionId").equals(actionId)) {
                        action = a;
                        break;
                    }
                }
                if (action != null) {
                    HashMap<String, Object> actionResult = new HashMap<String, Object>();
                    actionResult.put("errors", action.get("errors"));
                    actionResult.put("fields", action.get("fields"));
                    JSONObject jsonResponse = new JSONObject(actionResult);
                    HashMap<String, Object> response = new HashMap<String, Object>();
                    response.put("result", jsonResponse.toString());
                    return response;
                } else {
                    JSONObject jsonResponse = new JSONObject();
                    HashMap<String, Object> response = new HashMap<String, Object>();
                    response.put("result", jsonResponse.toString());
                    return response;
                }
            } else {
                HashMap<String, Object> result = getActions(nodeRef, statemachineId);
                JSONObject jsonResponse = new JSONObject(result);
                HashMap<String, Object> response = new HashMap<String, Object>();
                response.put("result", jsonResponse.toString());
                return response;
            }
        } else {
            JSONObject jsonResponse = new JSONObject();
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("result", jsonResponse.toString());
            return response;
        }
    }

    private HashMap<String, Object> getActions(NodeRef nodeRef, String statemachineId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> actionsList = new ArrayList<HashMap<String, Object>>();
        NodeService nodeService = serviceRegistry.getNodeService();
        WorkflowService workflowService = serviceRegistry.getWorkflowService();
        List<WorkflowPath> paths = workflowService.getWorkflowPaths(statemachineId);
        for (WorkflowPath path : paths) {
            List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(path.getId());
            for (WorkflowTask task : tasks) {
                Map<QName, Serializable> properties = task.getProperties();
                NodeRef packageRef = (NodeRef) properties.get(WorkflowModel.ASSOC_PACKAGE);
                List<ChildAssociationRef> children = nodeService.getChildAssocs(packageRef);
                for (ChildAssociationRef child : children) {
                    NodeRef documentRef = child.getChildRef();
                    if (nodeService.getProperty(documentRef, StatemachineModel.PROP_STATUS) != null) {
                        result.put("taskId", task.getId());

                        Expression expression = new Expression(documentRef, serviceRegistry, orgstructureService);

                        ArrayList<HashMap<String, Object>> resultStates = new ArrayList<HashMap<String, Object>>();
                        List<StateMachineAction> actions = new StateMachineHelper().getTaskActionsByName(task.getId(), StateMachineActions.getActionName(FinishStateWithTransitionAction.class), ExecutionListener.EVENTNAME_TAKE);
                        for (StateMachineAction action : actions) {
                            FinishStateWithTransitionAction finishWithTransitionAction = (FinishStateWithTransitionAction) action;
                            List<FinishStateWithTransitionAction.NextState> states = finishWithTransitionAction.getStates();
                            for (FinishStateWithTransitionAction.NextState state : states) {
                                ArrayList<String> messages = new ArrayList<String>();
                                HashSet<String> fields = new HashSet<String>();
                                boolean hideAction = false;
                                for (Conditions.Condition condition : state.getConditionAccess().getConditions()) {
                                    if (!expression.execute(condition.getExpression())) {
                                        messages.add(condition.getErrorMessage());
                                        fields.addAll(condition.getFields());
                                        hideAction = hideAction || condition.isHideAction();
                                    }
                                }

                                long count = getActionCount(nodeRef, state.getActionId());

                                if (!hideAction) {
                                    HashMap<String, Object> resultState = new HashMap<String, Object>();
                                    resultState.put("type", "trans");
                                    resultState.put("actionId", state.getActionId());
                                    resultState.put("label", state.getLabel());
                                    resultState.put("workflowId", state.getWorkflowId());
                                    resultState.put("errors", messages);
                                    resultState.put("fields", fields);
                                    resultState.put("count", count);
                                    actionsList.add(resultState);
                                }
                            }
                        }

                        actions = new StateMachineHelper().getTaskActionsByName(task.getId(), StateMachineActions.getActionName(UserWorkflow.class), ExecutionListener.EVENTNAME_TAKE);
                        for (StateMachineAction action : actions) {
                            UserWorkflow userWorkflow = (UserWorkflow) action;
                            List<UserWorkflow.UserWorkflowEntity> entities = userWorkflow.getUserWorkflows();
                            for (UserWorkflow.UserWorkflowEntity entity : entities) {
                                ArrayList<String> messages = new ArrayList<String>();
                                HashSet<String> fields = new HashSet<String>();
                                for (Conditions.Condition condition : entity.getConditionAccess().getConditions()) {
                                    if (!expression.execute(condition.getExpression())) {
                                        messages.add(condition.getErrorMessage());
                                        fields.addAll(condition.getFields());
                                    }
                                }

                                long count = getActionCount(nodeRef, entity.getId());

                                HashMap<String, Object> workflow = new HashMap<String, Object>();
                                workflow.put("type", "user");
                                workflow.put("actionId", entity.getId());
                                workflow.put("label", entity.getLabel());
                                workflow.put("workflowId", entity.getWorkflowId());
                                workflow.put("errors", messages);
                                workflow.put("fields", fields);
                                workflow.put("count",count);
                                actionsList.add(workflow);
                            }
                        }
                    }

                    sort(actionsList);
                    result.put("actions", actionsList);
                }
            }
        }
        return result;
    }

    private long getActionCount(NodeRef nodeRef, String id) {
        NodeService nodeService = serviceRegistry.getNodeService();
        QName type = nodeService.getType(nodeRef);
        String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());

        NodeRef employee = orgstructureService.getCurrentEmployee();
        long count = 0;
        if (employee != null) {
            count = frequencyAnalysisService.getFrequencyCount(employee, shortTypeName, id);
        }
        return count;
    }

    private void sort(ArrayList<HashMap<String, Object>> unsorted) {
        class ElementComparator<T extends Map> implements Comparator<T> {
            @Override
            public int compare(T o1, T o2) {
                Long count1 = (Long) o1.get("count");
                Long count2 = (Long) o2.get("count");
                return count2.compareTo(count1);
            }
        }
        Collections.sort(unsorted, new ElementComparator<HashMap>());
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ActionsScript.serviceRegistry = serviceRegistry;
    }

}
