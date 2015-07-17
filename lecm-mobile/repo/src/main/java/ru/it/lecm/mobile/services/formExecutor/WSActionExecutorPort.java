package ru.it.lecm.mobile.services.formExecutor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.mobile.objects.*;

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

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
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
        return true;
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
        return true;
    }

    private boolean review(NodeRef nodeRef, WSOCOLLECTION params) {
        /*
        Ознакомление "Ознакомление"
        {http://www.it.ru/logicECM/model/review/wokflow/1.0}reviewTaskResult
        REVIEWED*/
        return true;
    }

    private void execTask(String taskId, Map<QName, Serializable> params) {
        WorkflowTask task = workflowService.getTaskById(taskId);
        Map<QName, Serializable> properties = new HashMap<>();
        workflowService.updateTask(taskId, properties, new HashMap<QName, List<NodeRef>>(), new HashMap<QName, List<NodeRef>>());
        workflowService.endTask(taskId, "Next");
    }

}
