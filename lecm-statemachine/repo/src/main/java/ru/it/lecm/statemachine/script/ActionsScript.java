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
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.action.Conditions;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.assign.AssignExecution;
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

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (req.getParameter("documentNodeRef") == null) return result;

        NodeRef nodeRef = new NodeRef(req.getParameter("documentNodeRef"));
        NodeService nodeService = serviceRegistry.getNodeService();
        String statemachineId = (String) nodeService.getProperty(nodeRef, StatemachineModel.PROP_STATEMACHINE_ID);
        if (statemachineId != null) {
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

                            Expression expression = new Expression(documentRef, serviceRegistry);

                            ArrayList<HashMap<String, Object>> resultStates = new ArrayList<HashMap<String, Object>>();
                            List<StateMachineAction> actions = new StateMachineHelper().getTaskActionsByName(task.getId(), StateMachineActions.getActionName(FinishStateWithTransitionAction.class), ExecutionListener.EVENTNAME_TAKE);
                            for (StateMachineAction action : actions) {
                                FinishStateWithTransitionAction finishWithTransitionAction = (FinishStateWithTransitionAction) action;
                                List<FinishStateWithTransitionAction.NextState> states = finishWithTransitionAction.getStates();
                                for (FinishStateWithTransitionAction.NextState state : states) {
                                    ArrayList<String> messages = new ArrayList<String>();
                                    HashSet<String> fields = new HashSet<String>();
                                    for (Conditions.Condition condition : state.getConditionAccess().getConditions()) {
                                        if (!expression.execute(condition.getExpression())) {
                                            messages.add(condition.getErrorMessage());
                                            fields.addAll(condition.getFields());
                                        }
                                    }

                                    HashMap<String, Object> resultState = new HashMap<String, Object>();
                                    resultState.put("actionId", state.getActionId());
                                    resultState.put("label", state.getLabel());
                                    resultState.put("workflowId", state.getWorkflowId());
                                    resultState.put("errors", messages);
                                    resultState.put("fields", fields);
                                    resultStates.add(resultState);
                                }
                            }
                            result.put("states", resultStates);

                            ArrayList<HashMap<String, Object>> workflows = new ArrayList<HashMap<String, Object>>();
                            actions = new StateMachineHelper().getTaskActionsByName(task.getId(), StateMachineActions.getActionName(UserWorkflow.class), ExecutionListener.EVENTNAME_TAKE);
                            for (StateMachineAction action : actions) {
                                UserWorkflow userWorkflow = (UserWorkflow) action;
                                List<UserWorkflow.UserWorkflowEntity> entities = userWorkflow.getUserWorkflows();
                                AssignExecution assignExecution = new AssignExecution();
                                for (UserWorkflow.UserWorkflowEntity entity : entities) {
                                    ArrayList<String> messages = new ArrayList<String>();
                                    HashSet<String> fields = new HashSet<String>();
                                    for (Conditions.Condition condition : entity.getConditionAccess().getConditions()) {
                                        if (!expression.execute(condition.getExpression())) {
                                            messages.add(condition.getErrorMessage());
                                            fields.addAll(condition.getFields());
                                        }
                                    }
                                    HashMap<String, Object> workflow = new HashMap<String, Object>();
                                    workflow.put("id", entity.getId());
                                    workflow.put("label", entity.getLabel());
                                    workflow.put("workflowId", entity.getWorkflowId());
                                    assignExecution.execute(entity.getAssignee());
                                    List<String> refs = new ArrayList<String>();
                                    if (assignExecution.getNodeRefResult() != null) {
                                        refs.add(assignExecution.getNodeRefResult().toString());
                                    }
                                    workflow.put("assignees", refs);
                                    workflow.put("errors", messages);
                                    workflow.put("fields", fields);
                                    workflows.add(workflow);
                                }
                            }
                            result.put("workflows", workflows);
                        }
                    }
                }
            }
        }
        return result;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ActionsScript.serviceRegistry = serviceRegistry;
    }

}
