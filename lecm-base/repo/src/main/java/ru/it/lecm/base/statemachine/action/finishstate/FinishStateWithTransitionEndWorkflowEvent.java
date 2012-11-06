package ru.it.lecm.base.statemachine.action.finishstate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import ru.it.lecm.base.statemachine.StateMachineHelper;
import ru.it.lecm.base.statemachine.action.StateMachineAction;
import ru.it.lecm.base.statemachine.action.WorkflowVariables;
import ru.it.lecm.base.statemachine.bean.StateMachineActions;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:57
 * <p/>
 * Класс-слушатель окончания процесса.
 * По завершению передает сигнал об окончании пользовательского процесса
 * для дальнейшего оповещения машины состояний.
 */
public class FinishStateWithTransitionEndWorkflowEvent implements ExecutionListener {

	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		if (delegateExecution.getVariable(FinishStateWithTransitionAction.PROP_CHANGE_STATE_PREV_TASK_ID) != null) {
			String prevTaskId = (String) delegateExecution.getVariable(FinishStateWithTransitionAction.PROP_CHANGE_STATE_PREV_TASK_ID);
			String curTaskId = (String) delegateExecution.getVariable(FinishStateWithTransitionAction.PROP_CHANGE_STATE_CUR_TASK_ID);
			String actionId = (String) delegateExecution.getVariable(FinishStateWithTransitionAction.PROP_CHANGE_STATE_ACTION_ID);
			List<StateMachineAction> actions = new StateMachineHelper().getHistoricalTaskActionsByName(prevTaskId, StateMachineActions.getActionName(FinishStateWithTransitionAction.class), EVENTNAME_TAKE);
			StateMachineHelper helper = new StateMachineHelper();
			for (StateMachineAction action : actions) {
				FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
				for (FinishStateWithTransitionAction.NextState state : finishStateWithTransitionAction.getStates()) {
					if (state.getActionId().equalsIgnoreCase(actionId) && state.getVariables() != null) {
						List<WorkflowVariables.WorkflowVariable> variables = state.getVariables().getOutput();
						helper.getOutputVariables(helper.getCurrentExecutionId(curTaskId), delegateExecution.getId(), variables);
					}
				}
			}
			helper.nextTransition(curTaskId);
		}
	}
}
