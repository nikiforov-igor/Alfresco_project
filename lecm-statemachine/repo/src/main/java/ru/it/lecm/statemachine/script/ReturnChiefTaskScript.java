package ru.it.lecm.statemachine.script;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.bean.UserActionsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReturnChiefTaskScript extends DeclarativeWebScript {

    public static final QName ORIGINAL_EMPLOYEE = QName.createQName("", "originalEmployee");
    public static final QName EFFECTIVE_EMPLOYEE = QName.createQName("", "effectiveEmployee");

    private UserActionsService userActionsService;
    private StateMachineServiceBean stateMachineService;
    private ServiceRegistry serviceRegistry;
    private NotificationsService notificationsService;
    private BusinessJournalService businessJournalService;
    private WorkflowService workflowService;

    public void setStateMachineService(LifecycleStateMachineHelper stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setUserActionsService(UserActionsService userActionsService) {
        this.userActionsService = userActionsService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        final String documentRef = req.getParameter("documentNodeRef");
        final String actionId = req.getParameter("actionId");
        final HashMap<String, Object> actionResult = new HashMap<>();

        if (documentRef == null) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add("Ошибка скрипта. Параметр documentNodeRef не определен.");
            actionResult.put("errors", errors);
            actionResult.put("doesNotBlock", false);
            actionResult.put("fields", new ArrayList<String>());
        }
        else if (actionId == null) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add("Ошибка скрипта. Параметр actionId не определен.");
            actionResult.put("errors", errors);
            actionResult.put("doesNotBlock", false);
            actionResult.put("fields", new ArrayList<String>());
        }
        else {
            final NodeRef documentNodeRef = new NodeRef(documentRef);
            HashMap<String, Object> userActions = userActionsService.getActions(documentNodeRef);
            ArrayList<HashMap<String, Object>> actions = (ArrayList<HashMap<String, Object>>) userActions.get("actions");
            HashMap<String, Object> action = null;
            for (HashMap<String, Object> a : actions) {
                if (a.get("actionId").equals(actionId)) {
                    action = a;
                    break;
                }
            }
            if (action != null && action.containsKey("chiefLogin")) {
                actionResult.put("fields", action.get("fields"));
                final String chiefLogin = (String)action.get("chiefLogin");
                final String taskId = actionId.indexOf("$") > 0 ? actionId.substring(actionId.indexOf("$") + 1) : actionId;
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork() {
                    @Override
                    public Object doWork() throws Exception {
                        if (!stateMachineService.setTaskAssignee(documentNodeRef, taskId, serviceRegistry.getAuthenticationService().getCurrentUserName(), chiefLogin)) {
                            ArrayList<String> errors = new ArrayList<>();
                            errors.add("При выполнении возвращения задания руководителю произоошла ошибка.");
                            actionResult.put("errors", errors);
                            actionResult.put("doesNotBlock", false);
                        } else {
                            WorkflowTask task = workflowService.getTaskById(actionId);
                            NodeRef recipient = (NodeRef) task.getProperties().get(ORIGINAL_EMPLOYEE);
                            NodeRef secretary = (NodeRef) task.getProperties().get(EFFECTIVE_EMPLOYEE);
                            Map<String, Object> notificationTemplateModel = new HashMap<>();
                            notificationTemplateModel.put("mainObject", documentNodeRef);
                            notificationTemplateModel.put("secretary", secretary);
                            Notification notification = new Notification(notificationTemplateModel);
                            notificationsService.fillNotificationByTemplateCode(notification, "SECRETARY_RETURN");
                            notification.setRecipientEmployeeRefs(Collections.singletonList(recipient));
                            notification.setAuthor(AuthenticationUtil.getSystemUserName());
                            notification.setObjectRef(documentNodeRef);
                            notificationsService.sendNotification(notification);
                            //TODO: создание записи в БЖ
                        }
                        return null;
                    }
                });
            }
            else {
                ArrayList<String> errors = new ArrayList<>();
                errors.add("Ошибка скрипта. Задание не найдено.");
                actionResult.put("errors", errors);
                actionResult.put("doesNotBlock", false);
                actionResult.put("fields", new ArrayList<String>());
            }
        }

        HashMap<String, Object> response = new HashMap<>();
        if (actionResult.size() > 0) {
            response.put("result", new JSONObject(actionResult).toString());
        }
        else {
            response.put("result", new JSONObject().toString());
        }
        return response;
    }
}
