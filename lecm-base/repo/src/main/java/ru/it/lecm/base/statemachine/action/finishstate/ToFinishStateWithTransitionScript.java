package ru.it.lecm.base.statemachine.action.finishstate;

import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.service.ServiceRegistry;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.statemachine.StateMachineHelper;
import ru.it.lecm.base.statemachine.action.StateMachineAction;
import ru.it.lecm.base.statemachine.bean.StateMachineActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:18
 */
public class ToFinishStateWithTransitionScript extends DeclarativeWebScript {

	private static ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		ToFinishStateWithTransitionScript.serviceRegistry = serviceRegistry;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String taskId = req.getParameter("taskId");
		String persistedResponse = req.getParameter("formResponse");
		String actionId = req.getParameter("actionId");

		StateMachineHelper helper = new StateMachineHelper();
		List<StateMachineAction> actions = helper.getTaskActionsByName(taskId, StateMachineActions.getActionName(ToFinishStateWithTransitionAction.class), ExecutionListener.EVENTNAME_TAKE);
		ToFinishStateWithTransitionAction.NextState nextState = null;
		for (StateMachineAction action : actions) {
			ToFinishStateWithTransitionAction toFinishStateWithTransitionAction = (ToFinishStateWithTransitionAction) action;
			List<ToFinishStateWithTransitionAction.NextState> states = toFinishStateWithTransitionAction.getStates();
			for (ToFinishStateWithTransitionAction.NextState state : states) {
				if (state.getActionId().equalsIgnoreCase(actionId)) {
					nextState = state;
				}
			}
		}

		if (nextState != null) {
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put(nextState.getOutputVariableName(), nextState.getOutputVariableValue());
			String executionId = helper.getCurrentExecutionId(taskId);
			helper.setExecutionParamentersByTaskId(taskId, parameters);
			helper.nextTransition(taskId);

			if (!"null".equals(persistedResponse)) {
				int start = persistedResponse.indexOf("=") + 1;
				int end = persistedResponse.indexOf(",");

				String dependencyExecution = persistedResponse.substring(start, end);

				parameters = new HashMap<String, String>();
				parameters.put(ToFinishStateWithTransitionAction.PROP_CHANGE_STATE_ACTION_ID, actionId);
				parameters.put(ToFinishStateWithTransitionAction.PROP_CHANGE_STATE_PREV_TASK_ID, taskId);

				taskId = helper.getCurrentTaskId(executionId);
				parameters.put(ToFinishStateWithTransitionAction.PROP_CHANGE_STATE_CUR_TASK_ID, taskId);

				helper.setExecutionParameters(dependencyExecution, parameters);
				if (nextState.getVariables() != null) {
					helper.setInputVariables(executionId, dependencyExecution, nextState.getVariables().getInput());
				}
			}
		}

		return super.executeImpl(req, status, cache);    //To change body of overridden methods use File | Settings | File Templates.
	}

}
