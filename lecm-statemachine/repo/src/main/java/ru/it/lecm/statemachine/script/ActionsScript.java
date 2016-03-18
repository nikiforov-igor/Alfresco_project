package ru.it.lecm.statemachine.script;

import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.bean.UserActionsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:49
 */
public class ActionsScript extends DeclarativeWebScript {

    private UserActionsService userActionsService;
    private LifecycleStateMachineHelper stateMachineService;

    public void setStateMachineService(LifecycleStateMachineHelper stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setUserActionsService(UserActionsService userActionsService) {
        this.userActionsService = userActionsService;
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
        //LifecycleStateMachineHelper helper = new LifecycleStateMachineHelper();

        if (taskId != null && taskId.startsWith(LifecycleStateMachineHelper.ACTIVITI_PREFIX) && stateMachineService.getCurrentExecutionId(taskId) == null) {
            HashMap<String, Object> actionResult = new HashMap<String, Object>();
            ArrayList<String> errors = new ArrayList<String>();
            errors.add("Статус документа изменился! Обновите страницу документа для получения списка доступных действий.");
            actionResult.put("errors", errors);
            actionResult.put("doesNotBlock", false);
            actionResult.put("fields", new ArrayList<String>());
            JSONObject jsonResponse = new JSONObject(actionResult);
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("result", jsonResponse.toString());
            return response;
        }

        NodeRef nodeRef = new NodeRef(documentRef);
        if (req.getParameter("actionId") != null) {
            String actionId = req.getParameter("actionId");
            HashMap<String, Object> result = userActionsService.getActions(nodeRef);
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
                actionResult.put("doesNotBlock", action.get("doesNotBlock"));
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
            HashMap<String, Object> result = userActionsService.getActions(nodeRef);
            JSONObject jsonResponse = new JSONObject(result);
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("result", jsonResponse.toString());
            return response;
        }
    }

}
