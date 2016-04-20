package ru.it.lecm.statemachine.script;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.bean.UserActionsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReturnChiefTaskScript extends DeclarativeWebScript {

    private UserActionsService userActionsService;
    private StateMachineServiceBean stateMachineService;
    private ServiceRegistry serviceRegistry;
    private NotificationsService notificationsService;
    private BusinessJournalService businessJournalService;
    private OrgstructureBean orgstructureBean;
    private NodeService nodeService;

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

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
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
            final HashMap<String, Object> userActions = userActionsService.getActions(documentNodeRef);
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
                        String currentUserName = serviceRegistry.getAuthenticationService().getCurrentUserName();
                        if (!stateMachineService.setTaskAssignee(documentNodeRef, taskId, currentUserName, chiefLogin)) {
                            ArrayList<String> errors = new ArrayList<>();
                            errors.add("При выполнении возвращения задания руководителю произоошла ошибка.");
                            actionResult.put("errors", errors);
                            actionResult.put("doesNotBlock", false);
                        } else {
                            NodeRef chief = orgstructureBean.getEmployeeByPerson(chiefLogin);
                            NodeRef secretary = orgstructureBean.getEmployeeByPerson(currentUserName);

                            Map<String, Object> templateObjects = new HashMap<>();
                            templateObjects.put("secretary", secretary);
                            notificationsService.sendNotificationByTemplate(documentNodeRef, Collections.singletonList(chief), "SECRETARY_RETURN", templateObjects);

                            String chiefShortName = (String) nodeService.getProperty(chief, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                            businessJournalService.log(currentUserName, documentNodeRef, "EXEC_ACTION", "Сотрудник #initiator вернул(а) задачу сотруднику " + chiefShortName + " по документу #mainobject", Collections.singletonList("string"));
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
