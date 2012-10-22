package ru.it.lecm.base.statemachine.script;

import org.alfresco.service.ServiceRegistry;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.statemachine.StateMachineHelper;
import ru.it.lecm.base.statemachine.action.ChangeStateAction;
import ru.it.lecm.base.statemachine.action.StateMachineAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:18
 */
public class ChangeStateScript extends DeclarativeWebScript {

    private static ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ChangeStateScript.serviceRegistry = serviceRegistry;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        String taskId = req.getParameter("taskId");
        String persistedResponse = req.getParameter("formResponse");
        String actionId = req.getParameter("actionId");

        StateMachineHelper helper = new StateMachineHelper();
        List<StateMachineAction> actions = helper.getTaskActionsByName(taskId, "chooseState", "take");
        ChangeStateAction.NextState nextState = null;
        for (StateMachineAction action : actions) {
            ChangeStateAction changeStateAction = (ChangeStateAction) action;
            List<ChangeStateAction.NextState> states = changeStateAction.getStates();
            for (ChangeStateAction.NextState state : states) {
                if (state.getActionId().equalsIgnoreCase(actionId)) {
                    nextState = state;
                }
            }
        }

        if (nextState != null) {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put(nextState.getOutputVariableName(), nextState.getOutputVariableValue());
            String executionId = helper.getCurrentExecutionId(taskId);
            helper.setExecutionParamenters(taskId, parameters);
            helper.nextTransition(taskId);

            int start = persistedResponse.indexOf("=") + 1;
            int end = persistedResponse.indexOf(",");
            String dependencyExecution = persistedResponse.substring(start, end);

            taskId = helper.getCurrentTaskId(executionId);
            helper.addProcessDependency(taskId, dependencyExecution);
        }

        req.getServiceMatch().getTemplateVars().get("taskId");
        return super.executeImpl(req, status, cache);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
