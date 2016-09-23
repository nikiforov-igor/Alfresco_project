package ru.it.lecm.mobile.services.formExecutor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptAction;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.StrUtils;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.mobile.objects.*;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.bean.UserActionsService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 16.07.2015
 * Time: 17:04
 */
@javax.jws.WebService(name = "WSActionExecutorPort",
        serviceName = "ActionExecutor",
        portName = "WSActionExecutorPort",
        targetNamespace = "urn:DefaultNamespace",
        endpointInterface = "ru.it.lecm.mobile.services.formExecutor.WSActionExecutor")
public class WSActionExecutorPort implements WSActionExecutor {
    private ObjectFactory objectFactory;
    private WorkflowService workflowService;
    private UserActionsService actionsService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private OrgstructureBean orgstructureBean;
    private StateMachineServiceBean stateMachineHelper;
    private LecmTransactionHelper lecmTransactionHelper;

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public void setActionsService(UserActionsService actionsService) {
        this.actionsService = actionsService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureBean = orgstructureService;
    }

    public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

    public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
        this.lecmTransactionHelper = lecmTransactionHelper;
    }

    @Override
    public WSOEDS getfakesign() {
        return new WSOEDS();
    }

    @Override
    public boolean execute(WSOBJECT object, WSOFORMACTION action, WSOCONTEXT context) {
        if (!NodeRef.isNodeRef(object.getID())) return false;

        final NodeRef nodeRef = new NodeRef(object.getID());
        final String actionId = action.getID();
        final WSOCOLLECTION params = action.getEXTENSION();
        final AuthenticationUtil.RunAsWork<Boolean> runner = new AuthenticationUtil.RunAsWork<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
                    @Override
                    public Boolean execute() throws Throwable {
                        if ("SIGNING".equals(actionId)) {
                            return signing(nodeRef, params);
                        } else if ("APPROVAL".equals(actionId)) {
                            return approval(nodeRef, params);
                        } else if ("REVIEW".equals(actionId)) {
                            return review(nodeRef, params);
                        } else if ("Направить на исполнение".equals(actionId)) {
                            return directToExecute(nodeRef, params);
                        } else if ("approveTask".equals(actionId)) {
                            return approveTask(nodeRef, params);
                        } else if ("ReturnTask".equals(actionId)) {
                            return rejectTask(nodeRef, params);
                        } else if ("Изменить исполнителя".equals(actionId)) {
                            return changeExecutor(nodeRef, params);
                        } else if ("CancelTask".equals(actionId)) {
                            return cancelTask(nodeRef, params);
                        }
                        return false;
                    }
                });
            }
        };

        return AuthenticationUtil.runAs(runner,
                context.getUSERID()
        );
    }

    @Override
    public WSOITEM getitem() {
        return objectFactory.createWSOITEM();
    }

    private boolean signing(NodeRef nodeRef, WSOCOLLECTION params) {
        /*
        Подписание "Подписать документ"
            {http://www.it.ru/logicECM/model/signing/workflow/2.0}decision
            SIGNED
            REJECTED
            WorkflowModel.PROP_COMMENT
       */
        List<Object> items = params.getDATA();
        Map<QName, Serializable> taskParams = new HashMap<>();
        for (Object i : items) {
            WSOITEM item = (WSOITEM) i;
            if (item.getID().equals("Decision")) {
                QName prop = QName.createQName("lecmSign2:decision", namespaceService);
                String value = getValue(item);
                taskParams.put(prop, value);
            } else if (item.getID().equals("Comment")) {
                QName prop = QName.createQName("bpm:comment", namespaceService);
                String value = getValue(item);
                taskParams.put(prop, value);
            }
        }
        String taskId = getTaskId(nodeRef, "Подписать документ");
        if (StringUtils.isNotEmpty(taskId)) {
            execTask(taskId, taskParams);
            return true;
        } else {
            return false;
        }
    }

    private boolean approval(NodeRef nodeRef, WSOCOLLECTION params) {
        /*
        Согласование "Cогласовать документ"
            {http://www.it.ru/logicECM/model/approval/workflow/3.0}decision
            APPROVED
            REJECTED
            APPROVED_WITH_REMARK
            WorkflowModel.PROP_COMMENT
        */
        List<Object> items = params.getDATA();
        Map<QName, Serializable> taskParams = new HashMap<>();
        for (Object i : items) {
            WSOITEM item = (WSOITEM) i;
            if (item.getID().equals("Decision")) {
                QName prop = QName.createQName("lecmApprove3:decision", namespaceService);
                String value = getValue(item);
                taskParams.put(prop, value);
            } else if (item.getID().equals("Comment")) {
                QName prop = QName.createQName("bpm:comment", namespaceService);
                String value = getValue(item);
                taskParams.put(prop, value);
            }
        }
        String taskId = getTaskId(nodeRef, "Согласовать документ");
        if (StringUtils.isNotEmpty(taskId)) {
            execTask(taskId, taskParams);
            return true;
        } else {
            return false;
        }
    }

    private boolean review(NodeRef nodeRef, WSOCOLLECTION params) {
        /*
        Ознакомление "Ознакомление"
        {http://www.it.ru/logicECM/model/review/wokflow/1.0}reviewTaskResult
        REVIEWED*/
        Map<QName, Serializable> taskParams = new HashMap<>();
        QName prop = QName.createQName("lecmReview:decision", namespaceService);
        taskParams.put(prop, "REVIEWED");
        String taskId = getTaskId(nodeRef, "Ознакомление");
        if (StringUtils.isNotEmpty(taskId)) {
            execTask(taskId, taskParams);
            return true;
        } else {
            return false;
        }
    }

    private void execTask(String taskId, Map<QName, Serializable> params) {
        workflowService.updateTask(taskId, params, new HashMap<QName, List<NodeRef>>(), new HashMap<QName, List<NodeRef>>());
        workflowService.endTask(taskId, "Next");
    }

    private String getValue(WSOITEM item) {
        return item.getVALUES().getDATA().get(0).toString();
    }

    private String getTaskId(NodeRef nodeRef, String taskName) {
        HashMap<String, Object> actions =  actionsService.getActions(nodeRef);
        ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String,Object>>) actions.get("actions");
        if (list != null) {
            for (HashMap<String, Object> action : list) {
                String type = (String) action.get("type");
                String label = (String) action.get("label");
                if ("task".equals(type) && taskName.equals(label)) {
                    return (String) action.get("actionId");
                }
            }
        }
        return "";
    }

    private boolean approveTask(NodeRef nodeRef, WSOCOLLECTION params) {
        String workflowId = startWorkflow(nodeRef, "activiti$errandsInitiatorApprove", new HashMap<QName, Serializable>());
        stateMachineHelper.executeTransitionAction(nodeRef, "Подтвердить исполнение", "=" + workflowId + ",");
        return true;
    }

    private boolean rejectTask(NodeRef nodeRef, WSOCOLLECTION params) {
        QName param = QName.createQName("lecmErrandWf:initiatorDeclineReason", namespaceService);
        HashMap<QName, Serializable> wParams = new HashMap<>();
        wParams.put(param, ((WSOITEM) params.getDATA().get(0)).getVALUES().getDATA().get(0).toString());
        String workflowId = startWorkflow(nodeRef, "activiti$errandsInitiatorDecline", wParams);
        stateMachineHelper.executeTransitionAction(nodeRef, "Отправить на доработку", "=" + workflowId + ",");
        return true;
    }

    private boolean directToExecute(NodeRef nodeRef, WSOCOLLECTION params) {
        QName param = QName.createQName("lecmIncomingWf:recipient", namespaceService);
        HashMap<QName, Serializable> wParams = new HashMap<>();
        NodeRef executor = new NodeRef(((WSOPERSON)((WSOITEM) params.getDATA().get(0)).getVALUES().getDATA().get(0)).getID());
        wParams.put(param, executor);
        String workflowId = startWorkflow(nodeRef, "activiti$incomingDirectToExecution", wParams);
        stateMachineHelper.executeTransitionAction(nodeRef, "Направить на исполнение", "=" + workflowId + ",");
        return true;
    }

    private boolean changeExecutor(NodeRef nodeRef, WSOCOLLECTION params) {
        QName execParam = QName.createQName("lecmErrandWf:changeExecutorNewExecutor", namespaceService);
        QName commentParam = QName.createQName("lecmErrandWf:changeExecutorReason", namespaceService);
        HashMap<QName, Serializable> wParams = new HashMap<>();
        for (Object i : params.getDATA()) {
            WSOITEM item = (WSOITEM) i;
            if (item.getID().equals("executor")) {
                NodeRef executor = new NodeRef(((WSOPERSON) item.getVALUES().getDATA().get(0)).getID());
                wParams.put(execParam, executor);
            } else if (item.getID().equals("comment")) {
                wParams.put(commentParam, item.getVALUES().getDATA().get(0).toString());
            }
        }
        String workflowId = startWorkflow(nodeRef, "activiti$errandsChangeExecutor", wParams);
        stateMachineHelper.executeTransitionAction(nodeRef, "Сменить исполнителя", "=" + workflowId + ",");
        return true;
    }

    private boolean cancelTask(NodeRef nodeRef, WSOCOLLECTION params) {
        QName param = QName.createQName("lecmErrandWf:cancelReason", namespaceService);
        HashMap<QName, Serializable> wParams = new HashMap<>();
        wParams.put(param, ((WSOITEM) params.getDATA().get(0)).getVALUES().getDATA().get(0).toString());
        String workflowId = startWorkflow(nodeRef, "activiti$errandsCancel", wParams);
        stateMachineHelper.executeTransitionAction(nodeRef, "Отменить поручение", "=" + workflowId + ",");
        return true;
    }

    private boolean changeDate(NodeRef nodeRef, WSOCOLLECTION params) {
        //Date
        //((WSOITEM) params.getDATA().get(0)).getVALUES().getDATA().get(0)
        return true;
    }


    private String startWorkflow(NodeRef nodeRef, String workflowDefId, Map<QName, Serializable> params) {
        NodeRef assigneeNodeRef = orgstructureBean.getPersonForEmployee(orgstructureBean.getCurrentEmployee());

        Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>(16);
        workflowProps.putAll(params);
        NodeRef stateProcessPackage = workflowService.createPackage(null);
        nodeService.addChild(stateProcessPackage, nodeRef, ContentModel.ASSOC_CONTAINS, ErrandsService.TYPE_ERRANDS);

        workflowProps.put(WorkflowModel.ASSOC_PACKAGE, stateProcessPackage);
        workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, assigneeNodeRef);

        //workflowProps.put(QName.createQName("{}stm_document"), docRef);

        // get the moderated workflow
        WorkflowDefinition wfDefinition = workflowService.getDefinitionByName(workflowDefId);
        // start the workflow
        WorkflowPath path = workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
        List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(path.getId());
        for (WorkflowTask task : tasks) {
            workflowService.endTask(task.getId(), null);
        }
        return path.getInstance().getId();
    }

}
