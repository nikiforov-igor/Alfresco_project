package ru.it.lecm.statemachine.action.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.WorkflowDescriptor;
import ru.it.lecm.statemachine.action.StartWorkflowAction;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.WorkflowVariables;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.action.util.DocumentWorkflowUtil;
import ru.it.lecm.statemachine.bean.StateMachineActions;

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
public class EndWorkflowEvent implements ExecutionListener {

	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		StateMachineHelper helper = new StateMachineHelper();
		String executionId = StateMachineHelper.ACTIVITI_PREFIX + delegateExecution.getId();
		NodeRef document = helper.getStatemachineDocument(executionId);
		DocumentWorkflowUtil utils = new DocumentWorkflowUtil();
		WorkflowDescriptor descriptor = utils.getWorkflowDescriptor(document, executionId);

		if (descriptor != null) {
			String actionName = descriptor.getActionName();
			String actionId = descriptor.getActionId();
			String statemachineId = descriptor.getStatemachineExecutionId();

			List<StateMachineAction> actions = helper.getTaskActionsByName(descriptor.getStartTaskId(), descriptor.getActionName(), descriptor.getEventName());
			if (actions.size() == 0) {
				actions = helper.getHistoricalTaskActionsByName(descriptor.getStartTaskId(), descriptor.getActionName(), descriptor.getEventName());
			}

			List<WorkflowVariables.WorkflowVariable> variables = null;
			if (actionName.equals(StateMachineActions.getActionName(FinishStateWithTransitionAction.class))) {
				for (StateMachineAction action : actions) {
					FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
					for (FinishStateWithTransitionAction.NextState state : finishStateWithTransitionAction.getStates()) {
						if (state.getActionId().equalsIgnoreCase(actionId) && state.getVariables() != null) {
							variables = state.getVariables().getOutput();
						}
					}
				}
				if (variables != null) {
					helper.getOutputVariables(statemachineId, executionId, variables);
				}
				String taskId = helper.getCurrentTaskId(statemachineId);
				helper.nextTransition(taskId);
			} else if (actionName.equals(StateMachineActions.getActionName(UserWorkflow.class))) {
				for (StateMachineAction action : actions) {
					UserWorkflow userWorkflow = (UserWorkflow) action;
					for (UserWorkflow.UserWorkflowEntity entity : userWorkflow.getUserWorkflows()) {
						if (entity.getId().equalsIgnoreCase(actionId) && entity.getVariables() != null) {
							variables = entity.getVariables().getOutput();
						}
					}
				}
			} else if (actionName.equals(StateMachineActions.getActionName(StartWorkflowAction.class))) {
				for (StateMachineAction action : actions) {
					StartWorkflowAction startWorkflowAction = (StartWorkflowAction) action;
					if (startWorkflowAction.getId().equalsIgnoreCase(actionId) && startWorkflowAction.getVariables() != null) {
						variables = startWorkflowAction.getVariables().getOutput();
					}
				}
			}

			if (variables != null) {
				helper.getOutputVariables(statemachineId, executionId, variables);
			}

		}

	}
}
