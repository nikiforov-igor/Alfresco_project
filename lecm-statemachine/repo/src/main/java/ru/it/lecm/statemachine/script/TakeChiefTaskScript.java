package ru.it.lecm.statemachine.script;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.bean.UserActionsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TakeChiefTaskScript extends DeclarativeWebScript {

    private UserActionsService userActionsService;
    private StateMachineServiceBean stateMachineService;
    private ServiceRegistry serviceRegistry;

    public void setStateMachineService(LifecycleStateMachineHelper stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setUserActionsService(UserActionsService userActionsService) {
        this.userActionsService = userActionsService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        final String documentRef = req.getParameter("documentNodeRef");
        final String actionId = req.getParameter("actionId");
        final HashMap<String, Object> actionResult = new HashMap<String, Object>();

        if (documentRef == null) {
            ArrayList<String> errors = new ArrayList<String>();
            errors.add("Ошибка скрипта. Параметр documentNodeRef не определен.");
            actionResult.put("errors", errors);
            actionResult.put("doesNotBlock", false);
            actionResult.put("fields", new ArrayList<String>());
        }
        else if (actionId == null) {
            ArrayList<String> errors = new ArrayList<String>();
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
            if (action != null && "chief_task".equalsIgnoreCase((String)action.get("type"))) {
                actionResult.put("fields", action.get("fields"));
                final String chiefLogin = (String)action.get("chiefLogin");
                final String taskId = actionId.indexOf("$") > 0 ? actionId.substring(actionId.indexOf("$") + 1) : actionId;
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork() {
                    @Override
                    public Object doWork() throws Exception {
                        if (!stateMachineService.setWorkflowTaskProperty(documentNodeRef, actionId, StatemachineModel.PROP_CHIEF_LOGIN, chiefLogin)) {
                            ArrayList<String> errors = new ArrayList<String>();
                            errors.add("При изменении свойств задания руководителя произоошла ошибка.");
                            actionResult.put("errors", errors);
                            actionResult.put("doesNotBlock", false);
                        }
                        else if (!stateMachineService.setTaskAssignee(documentNodeRef, taskId, chiefLogin, serviceRegistry.getAuthenticationService().getCurrentUserName())) {
                            ArrayList<String> errors = new ArrayList<String>();
                            errors.add("При выполнении передачи задания руководителя произоошла ошибка.");
                            actionResult.put("errors", errors);
                            actionResult.put("doesNotBlock", false);
                        }
                        return null;
                    }
                });
            }
            else {
                ArrayList<String> errors = new ArrayList<String>();
                errors.add("Ошибка скрипта. Задание не найдено.");
                actionResult.put("errors", errors);
                actionResult.put("doesNotBlock", false);
                actionResult.put("fields", new ArrayList<String>());
            }
        }

        HashMap<String, Object> response = new HashMap<String, Object>();
        if (actionResult.size() > 0) {
            response.put("result", new JSONObject(actionResult).toString());
        }
        else {
            response.put("result", new JSONObject().toString());
        }
        return response;
    }
}
