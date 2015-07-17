package ru.it.lecm.mobile.services.formExecutor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptAction;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.StrUtils;
import ru.it.lecm.mobile.objects.*;
import ru.it.lecm.statemachine.bean.ActionsScriptBean;

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
    private ActionsScriptBean actionsService;
    private NamespaceService namespaceService;

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public void setActionsService(ActionsScriptBean actionsService) {
        this.actionsService = actionsService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
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
                if ("SIGNING".equals(actionId)) {
                    return signing(nodeRef, params);
                } else if ("APPROVAL".equals(actionId)) {
                    return approval(nodeRef, params);
                } else if ("REVIEW".equals(actionId)) {
                    return review(nodeRef, params);
                }
                return false;
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
}
