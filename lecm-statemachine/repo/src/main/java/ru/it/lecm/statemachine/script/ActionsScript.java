package ru.it.lecm.statemachine.script;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.actions.bean.GroupActionsService;
import ru.it.lecm.documents.beans.DocumentFrequencyAnalysisService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.action.Conditions;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
    private static DocumentService documentService;
    private static final QName PROP_DUE_DATE = QName.createQName(NamespaceService.BPM_MODEL_1_0_URI, "dueDate");
    private GroupActionsService groupActionsService;
    private AuthenticationService authService;
    private LecmPermissionService lecmPermissionService;
    private StateMachineHelper stateMachineService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public void setStateMachineService(StateMachineHelper stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        ActionsScript.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        ActionsScript.documentService = documentService;
    }

    public void setFrequencyAnalysisService(DocumentFrequencyAnalysisService frequencyAnalysisService) {
        ActionsScript.frequencyAnalysisService = frequencyAnalysisService;
    }

    public void setGroupActionsService(GroupActionsService groupActionsService) {
        this.groupActionsService = groupActionsService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        String documentRef = req.getParameter("documentNodeRef");
        String taskId = req.getParameter("taskId");

        if (req.getParameter("documentNodeRef") == null) {
            JSONObject jsonResponse = new JSONObject();
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("result", jsonResponse.toString());
            return response;
        }
        /*NEW HELPER*/
        //StateMachineHelper helper = new StateMachineHelper();

        if (taskId != null && stateMachineService.getCurrentExecutionId(taskId) == null && !stateMachineService.isFinal(new NodeRef(documentRef))) {
            HashMap<String, Object> actionResult = new HashMap<String, Object>();
            ArrayList<String> errors = new ArrayList<String>();
            errors.add("Статус документа изменился! Обновите страницу документа для получения списка доступных действий.");
            actionResult.put("errors", errors);
            actionResult.put("fields", new ArrayList<String>());
            JSONObject jsonResponse = new JSONObject(actionResult);
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("result", jsonResponse.toString());
            return response;
        }

        NodeRef nodeRef = new NodeRef(documentRef);
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

    private HashMap<String, Object> getActions(final NodeRef nodeRef, String statemachineId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> actionsList = new ArrayList<HashMap<String, Object>>();
        NodeService nodeService = serviceRegistry.getNodeService();
        final WorkflowService workflowService = serviceRegistry.getWorkflowService();
        List<WorkflowPath> paths = workflowService.getWorkflowPaths(statemachineId);

        Map<String, Long> counts = getActionsCounts(nodeRef);
        List<WorkflowTask> activeTasks = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<List<WorkflowTask>>() {
            @Override
            public List<WorkflowTask> doWork() throws Exception {
                return stateMachineService.getDocumentTasks(nodeRef, true);
            }
        });

        boolean hasExecutionPermission = stateMachineService.isFinal(nodeRef) || (lecmPermissionService.hasPermission("_lecmPerm_ActionExec", nodeRef, authService.getCurrentUserName())
                || (lecmPermissionService.hasPermission("LECM_BASIC_PG_Initiator", nodeRef, authService.getCurrentUserName()) && stateMachineService.isDraft(nodeRef)));

        List<WorkflowTask> userTasks = stateMachineService.getAssignedAndPooledTasks(authService.getCurrentUserName());
        for (WorkflowTask activeTask : activeTasks) {
            for (WorkflowTask userTask : userTasks) {
                if (activeTask.getId().equals(userTask.getId())) {
                    HashMap<String, Object> taskStruct = new HashMap<String, Object>();
                    taskStruct.put("type", "task");
                    taskStruct.put("actionId", userTask.getId());
                    taskStruct.put("label", userTask.getTitle());
                    taskStruct.put("count", Long.MAX_VALUE);
                    taskStruct.put("isForm", false);
                    Serializable dueDate = userTask.getProperties().get(PROP_DUE_DATE);
                    taskStruct.put("dueDate", dueDate == null ? null : dateFormat.format((Date) dueDate));
                    actionsList.add(taskStruct);
                }
            }
        }

        for (WorkflowPath path : paths) {
            final String pathId = path.getId();
            List<WorkflowTask> tasks = org.alfresco.repo.security.authentication.AuthenticationUtil.runAsSystem(new org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork<List<WorkflowTask>>() {
                @Override
                public List<WorkflowTask> doWork() throws Exception {
                    return workflowService.getTasksForWorkflowPath(pathId);
                }
            });

            for (WorkflowTask task : tasks) {
                result.put("taskId", task.getId());
                Map<QName, Serializable> properties = task.getProperties();
                NodeRef packageRef = (NodeRef) properties.get(WorkflowModel.ASSOC_PACKAGE);
                NodeRef documentRef = nodeService.getChildAssocs(packageRef).get(0).getChildRef();

                if (hasExecutionPermission) {

                    //TODO Сразу передавать нужные параметры
                    List<StateMachineAction> actions = stateMachineService.getTaskActionsByName(task.getId(), StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class));
                    for (StateMachineAction action : actions) {
                        FinishStateWithTransitionAction finishWithTransitionAction = (FinishStateWithTransitionAction) action;
                        List<FinishStateWithTransitionAction.NextState> states = finishWithTransitionAction.getStates();
                        for (FinishStateWithTransitionAction.NextState state : states) {
                            ArrayList<String> messages = new ArrayList<String>();
                            HashSet<String> fields = new HashSet<String>();
                            boolean hideAction = false;
                            for (Conditions.Condition condition : state.getConditionAccess().getConditions()) {
                                if (!documentService.execExpression(documentRef, condition.getExpression())) {
                                    messages.add(condition.getErrorMessage());
                                    fields.addAll(condition.getFields());
                                    hideAction = hideAction || condition.isHideAction();
                                }
                            }

                            Map<String, String> variables = stateMachineService.getInputVariablesMap(statemachineId, state.getVariables().getInput());

                            if (!hideAction) {
                                Long count = counts.get(state.getActionId());
                                if (count == null) {
                                    count = 0L;
                                }

                                HashMap<String, Object> resultState = new HashMap<String, Object>();
                                resultState.put("type", "trans");
                                resultState.put("actionId", state.getActionId());
                                resultState.put("label", state.getLabel());
                                resultState.put("workflowId", state.getWorkflowId());
                                resultState.put("errors", messages);
                                resultState.put("fields", fields);
                                resultState.put("count", count);
                                resultState.put("variables", variables);
                                resultState.put("isForm", state.isForm());
                                if (state.isForm()) {
                                    resultState.put("formType", state.getFormType());
                                    resultState.put("formFolder", getDestinationFolder(state.getFormFolder()).toString());
                                }
                                actionsList.add(resultState);

                            }
                        }
                    }

                    //TODO getTaskActionsByName сразу передавать нужные параметры
                    actions = stateMachineService.getTaskActionsByName(task.getId(), StateMachineActionsImpl.getActionNameByClass(UserWorkflow.class));
                    for (StateMachineAction action : actions) {
                        UserWorkflow userWorkflow = (UserWorkflow) action;
                        List<UserWorkflow.UserWorkflowEntity> entities = userWorkflow.getUserWorkflows();
                        for (UserWorkflow.UserWorkflowEntity entity : entities) {
                            ArrayList<String> messages = new ArrayList<String>();
                            HashSet<String> fields = new HashSet<String>();
                            boolean hideAction = false;
                            for (Conditions.Condition condition : entity.getConditionAccess().getConditions()) {
                                if (!documentService.execExpression(documentRef, condition.getExpression())) {
                                    messages.add(condition.getErrorMessage());
                                    fields.addAll(condition.getFields());
                                    hideAction = hideAction || condition.isHideAction();
                                }
                            }

                            Map<String, String> variables = stateMachineService.getInputVariablesMap(statemachineId, entity.getVariables().getInput());

                            if (!hideAction) {
                                Long count = counts.get(entity.getId());
                                if (count == null) {
                                    count = 0L;
                                }
                                HashMap<String, Object> workflow = new HashMap<String, Object>();
                                workflow.put("type", "user");
                                workflow.put("actionId", entity.getId());
                                workflow.put("label", entity.getLabel());
                                workflow.put("workflowId", entity.getWorkflowId());
                                workflow.put("errors", messages);
                                workflow.put("fields", fields);
                                workflow.put("count", count);
                                workflow.put("variables", variables);
                                workflow.put("isForm", false);
                                actionsList.add(workflow);
                            }
                        }
                    }
                }
            }
        }
        //Сортируем по частоте использования
        sort(actionsList);

        if (hasExecutionPermission) {
            List<NodeRef> groupActions = groupActionsService.getActiveActions(nodeRef);
            for (NodeRef action : groupActions) {
                HashMap<String, Object> actionStruct = new HashMap<String, Object>();
                actionStruct.put("type", "group");
                actionStruct.put("actionId", nodeService.getProperty(action, ContentModel.PROP_NAME));
                actionStruct.put("label", nodeService.getProperty(action, ContentModel.PROP_NAME));
                actionStruct.put("isForm", nodeService.getChildAssocs(action).size() > 0);
                QName type = nodeService.getType(action);
                if (type.equals(GroupActionsService.TYPE_GROUP_DOCUMENT_ACTION)) {
                    actionStruct.put("subtype", "document");
                    actionStruct.put("documentType", nodeService.getProperty(action, GroupActionsService.PROP_DOCUMENT_TYPE));
                    actionStruct.put("formFolder", documentService.getDraftRoot().toString());
                } else if (type.equals(GroupActionsService.TYPE_GROUP_WORKFLOW_ACTION)) {
                    actionStruct.put("subtype", "workflow");
                    actionStruct.put("workflowType", nodeService.getProperty(action, GroupActionsService.PROP_WORKFLOW));
                } else {
                    actionStruct.put("subtype", "script");
                }
                actionsList.add(actionStruct);
            }
        }
        if(actionsList.size()>0) {
        	result.put("actions", actionsList);
        }
        return result;
    }

    private Map<String, Long> getActionsCounts(NodeRef nodeRef) {
        NodeService nodeService = serviceRegistry.getNodeService();
        QName type = nodeService.getType(nodeRef);
        String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());

        NodeRef employee = orgstructureService.getCurrentEmployee();
        Map<String, Long> counts = new HashMap<String, Long>();
        if (employee != null) {
            counts = frequencyAnalysisService.getFrequenciesCountsByDocType(employee, shortTypeName);
        }
        return counts;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ActionsScript.serviceRegistry = serviceRegistry;
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

    private NodeRef getDestinationFolder(String path) {
        NodeRef result = documentService.getDraftRoot();
        if (path == null) {
            return result;
        }
        NodeService nodeService = serviceRegistry.getNodeService();
        StringTokenizer pathTokenizer = new StringTokenizer(path, "/");
        while (pathTokenizer.hasMoreTokens()) {
            String folder = pathTokenizer.nextToken();
            if (!"".equals(folder)) {
                NodeRef folderRef = nodeService.getChildByName(result, ContentModel.ASSOC_CONTAINS, folder);
                if (folderRef == null) {
                    folderRef = createFolder(result, folder);
                }
                result = folderRef;
            }
        }
        return result;
    }

    private NodeRef createFolder(final NodeRef parent, final String name) {
        final NodeService nodeService = serviceRegistry.getNodeService();
        final HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
        props.put(ContentModel.PROP_NAME, name);
        ChildAssociationRef childAssocRef = serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef>() {
            @Override
            public ChildAssociationRef execute() throws Throwable {
                ChildAssociationRef childAssocRef = nodeService.createNode(
                        parent,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
                        ContentModel.TYPE_FOLDER,
                        props);
                return childAssocRef;
            }
        }, false, true);
        return childAssocRef.getChildRef();
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }
}
