package ru.it.lecm.statemachine.bean;

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
import ru.it.lecm.actions.bean.GroupActionsService;
import ru.it.lecm.documents.beans.DocumentFrequencyAnalysisService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretaryService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.action.Conditions;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.WorkflowVariables;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserActionsServiceImpl implements UserActionsService {

    private ServiceRegistry serviceRegistry;
    private DocumentFrequencyAnalysisService frequencyAnalysisService;
    private OrgstructureBean orgstructureService;
    private SecretaryService secretaryService;
    private DocumentService documentService;
    private AuthenticationService authService;
    private LecmPermissionService lecmPermissionService;
    private LifecycleStateMachineHelper stateMachineService;
    private GroupActionsService groupActionsService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private final static String STATEMACHINE_EDITOR_URI = "http://www.it.ru/logicECM/statemachine/editor/1.0";
    private final static QName PROP_FORM_INPUT_TO_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "formInputToValue");
    private final static QName PROP_FORM_INPUT_FROM_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "formInputFromType");
    private final static QName PROP_FORM_INPUT_FROM_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "formInputFromValue");
    private final static QName PROP_DUE_DATE = QName.createQName(NamespaceService.BPM_MODEL_1_0_URI, "dueDate");

    public void setStateMachineService(LifecycleStateMachineHelper stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setFrequencyAnalysisService(DocumentFrequencyAnalysisService frequencyAnalysisService) {
        this.frequencyAnalysisService = frequencyAnalysisService;
    }

    public void setSecretaryService(SecretaryService secretaryService) {
        this.secretaryService = secretaryService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setGroupActionsService(GroupActionsService groupActionsService) {
        this.groupActionsService = groupActionsService;
    }

    @Override
    public HashMap<String, Object> getActions(final NodeRef nodeRef) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> actionsList = new ArrayList<HashMap<String, Object>>();
        NodeService nodeService = serviceRegistry.getNodeService();
        final WorkflowService workflowService = serviceRegistry.getWorkflowService();
        final String currentUserName = authService.getCurrentUserName();

        Map<String, Long> counts = getActionsCounts(nodeRef);
        List<WorkflowTask> activeTasks = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<List<WorkflowTask>>() {
            @Override
            public List<WorkflowTask> doWork() throws Exception {
                return stateMachineService.getDocumentTasks(nodeRef, true);
            }
        });

        boolean hasExecutionPermission = stateMachineService.isFinal(nodeRef) || (lecmPermissionService.hasPermission("_lecmPerm_ActionExec", nodeRef, currentUserName)
                || (lecmPermissionService.hasPermission("LECM_BASIC_PG_Initiator", nodeRef, currentUserName) && stateMachineService.isDraft(nodeRef)));

        List<WorkflowTask> userTasks = stateMachineService.getAssignedAndPooledTasks(currentUserName);
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
                    Serializable chiefLogin = userTask.getProperties().get(StatemachineModel.PROP_CHIEF_LOGIN);
                    if (chiefLogin != null && !chiefLogin.equals(currentUserName)) {
                        taskStruct.put("chiefLogin", chiefLogin.toString());
                        NodeRef chief = orgstructureService.getEmployeeByPerson(chiefLogin.toString());
                        if (chief != null) {
                            String shortName = (String) nodeService.getProperty(chief, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                            taskStruct.put("chiefShortName", shortName);
                        }

                    }

                    actionsList.add(taskStruct);
                }
            }
        }

        NodeRef currentEmployee = orgstructureService.getEmployeeByPerson(currentUserName);
        List<NodeRef> chiefNodeRefList = secretaryService.getChiefs(currentEmployee);
        for (NodeRef chiefNodeRef : chiefNodeRefList) {
            String chiefLogin = orgstructureService.getEmployeeLogin(chiefNodeRef);
            String chiefShortName = (String) nodeService.getProperty(chiefNodeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
            List<WorkflowTask> chiefTasks = stateMachineService.getAssignedAndPooledTasks(chiefLogin, true);
            for (WorkflowTask activeTask : activeTasks) {
                for (WorkflowTask chiefTask : chiefTasks) {
                    if (activeTask.getId().equals(chiefTask.getId())) {
                        HashMap<String, Object> taskStruct = new HashMap<String, Object>();
                        taskStruct.put("type", "chief_task");
                        taskStruct.put("actionId", chiefTask.getId());
                        taskStruct.put("label", chiefTask.getTitle());
                        taskStruct.put("chiefLogin", chiefLogin);
                        taskStruct.put("chiefShortName", chiefShortName);
                        taskStruct.put("count", Long.MAX_VALUE);
                        taskStruct.put("isForm", false);
                        Serializable dueDate = chiefTask.getProperties().get(PROP_DUE_DATE);
                        taskStruct.put("dueDate", dueDate == null ? null : dateFormat.format((Date) dueDate));
                        actionsList.add(taskStruct);
                    }
                }
            }
        }

        String statemachineId = (String) nodeService.getProperty(nodeRef, StatemachineModel.PROP_STATEMACHINE_ID);
        if (statemachineId != null) {
            List<WorkflowPath> paths = workflowService.getWorkflowPaths(statemachineId);
            for (WorkflowPath path : paths) {
                final String pathId = path.getId();
                List<WorkflowTask> tasks = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<List<WorkflowTask>>() {
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
                                boolean doesNotBlock = true;
                                for (Conditions.Condition condition : state.getConditionAccess().getConditions()) {
                                    if (!documentService.execExpression(documentRef, condition.getExpression())) {
                                        messages.add(condition.getErrorMessage());
                                        fields.addAll(condition.getFields());
                                        hideAction = hideAction || condition.isHideAction();
                                        doesNotBlock = doesNotBlock && condition.isDoesNotBlock();
                                    }
                                }

                                Map<String, String> variables = stateMachineService.getInputVariablesMap(statemachineId, state.getVariables().getInput());
                                variables.put("assoc_packageItems", documentRef.toString());

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
                                resultState.put("doesNotBlock", doesNotBlock);
                                resultState.put("doNotAskForConfirmation", state.isDoNotAskForConfirmation());
                                resultState.put("fields", fields);
                                resultState.put("count", count);
                                resultState.put("variables", variables);
                                resultState.put("isForm", state.isForm());
                                resultState.put("hideAction", hideAction);
                                if (state.isForm()) {
                                    resultState.put("documentType", state.getFormType());
                                    resultState.put("createUrl", documentService.getCreateUrl(QName.createQName(state.getFormType(), serviceRegistry.getNamespaceService())));
                                    resultState.put("formFolder", getDestinationFolder(state.getFormFolder()).toString());
                                    resultState.put("connectionType", state.getFormConnection());
                                    resultState.put("connectionIsSystem", state.isSystemFormConnection());
                                    resultState.put("connectionIsReverse", state.isReverseFormConnection());
                                    resultState.put("autoFill", state.isAutoFill());
                                }
                                actionsList.add(resultState);
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
                                boolean doesNotBlock = true;
                                for (Conditions.Condition condition : entity.getConditionAccess().getConditions()) {
                                    if (!documentService.execExpression(documentRef, condition.getExpression())) {
                                        messages.add(condition.getErrorMessage());
                                        fields.addAll(condition.getFields());
                                        hideAction = hideAction || condition.isHideAction();
                                        doesNotBlock = doesNotBlock && condition.isDoesNotBlock();
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
                                    workflow.put("doesNotBlock", doesNotBlock);
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
        }

        List<NodeRef> groupActions = null;
        if (hasExecutionPermission) {
            groupActions = groupActionsService.getActiveActions(nodeRef);
        }
        else if (lecmPermissionService.hasReadAccess(nodeRef, currentUserName)) {
            groupActions = groupActionsService.getActionsForReader(nodeRef);
        }

        if (groupActions != null) {
            boolean hasStatemachine = stateMachineService.hasActiveStatemachine(nodeRef);
            for (NodeRef action : groupActions) {
                HashMap<String, Object> actionStruct = new HashMap<String, Object>();
                actionStruct.put("type", "group");
                actionStruct.put("actionId", nodeService.getProperty(action, ContentModel.PROP_NAME));
                Object title = nodeService.getProperty(action, ContentModel.PROP_TITLE);
                if (title == null || "".equals(title)) {
                    actionStruct.put("label", nodeService.getProperty(action, ContentModel.PROP_NAME));
                } else {
                    actionStruct.put("label", title);
                }

                QName type = nodeService.getType(action);
                if (type.equals(GroupActionsService.TYPE_GROUP_DOCUMENT_ACTION)) {
                    actionStruct.put("subtype", "document");
                    actionStruct.put("documentType", nodeService.getProperty(action, GroupActionsService.PROP_DOCUMENT_TYPE));
                    actionStruct.put("createUrl", documentService.getCreateUrl(QName.createQName(nodeService.getProperty(action, GroupActionsService.PROP_DOCUMENT_TYPE).toString(), serviceRegistry.getNamespaceService())));
                    actionStruct.put("connectionType", nodeService.getProperty(action, GroupActionsService.PROP_DOCUMENT_CONNECTION));
                    actionStruct.put("connectionIsSystem", nodeService.getProperty(action, GroupActionsService.PROP_DOCUMENT_CONNECTION_SYSTEM));
                    actionStruct.put("autoFill", nodeService.getProperty(action, GroupActionsService.PROP_DOCUMENT_AUTO_FILL));
                    actionStruct.put("formFolder", documentService.getDraftRoot().toString());
                    Map<String, String> processingVars = processingVariables(nodeRef, action, statemachineId, hasStatemachine);
                    actionStruct.put("variables", processingVars);
                    actionStruct.put("isForm", false);
                } else if (type.equals(GroupActionsService.TYPE_GROUP_WORKFLOW_ACTION)) {
                    actionStruct.put("subtype", "workflow");
                    actionStruct.put("workflowType", nodeService.getProperty(action, GroupActionsService.PROP_WORKFLOW));
                    Map<String, String> processingVars = processingVariables(nodeRef, action, statemachineId, hasStatemachine);
                    actionStruct.put("variables", processingVars);
                    actionStruct.put("isForm", false);
                } else {
                    actionStruct.put("subtype", "script");
                    actionStruct.put("isForm", nodeService.getChildAssocs(action).size() > 0);
                }
                actionsList.add(actionStruct);
            }
        }
        if (actionsList.size() > 0) {
            result.put("actions", actionsList);
        }
        return result;
    }


    private Map<String, String> processingVariables(NodeRef document, NodeRef action, String statemachineId, boolean hasStatemachine) {
        NodeService nodeService = serviceRegistry.getNodeService();
        WorkflowVariables variables = new WorkflowVariables();
        List<ChildAssociationRef> vars = nodeService.getChildAssocs(action);
        for (ChildAssociationRef var : vars) {
            String formInputFromValue = nodeService.getProperty(var.getChildRef(), PROP_FORM_INPUT_FROM_VALUE).toString();
            String formInputFromType;
            if (!hasStatemachine && "stm_document".equals(formInputFromValue)) {
                formInputFromType = WorkflowVariables.Type.VALUE.toString();
                formInputFromValue = document.toString();
            } else {
                formInputFromType = nodeService.getProperty(var.getChildRef(), PROP_FORM_INPUT_FROM_TYPE).toString();
            }
            String formInputToValue = nodeService.getProperty(var.getChildRef(), PROP_FORM_INPUT_TO_VALUE).toString();
            variables.addInput(formInputFromType, formInputFromValue, WorkflowVariables.Type.VARIABLE.toString(), formInputToValue);
        }
        return stateMachineService.getInputVariablesMap(statemachineId, document, variables.getInput());
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
//        ChildAssociationRef childAssocRef = serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef>() {
//            @Override
//            public ChildAssociationRef execute() throws Throwable {
                ChildAssociationRef childAssocRef = nodeService.createNode(
                        parent,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
                        ContentModel.TYPE_FOLDER,
                        props);
//                return childAssocRef;
//            }
//        }, false, true);
        return childAssocRef.getChildRef();
    }
}
