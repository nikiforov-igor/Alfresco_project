package ru.it.lecm.statemachine.action.finishstate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.WorkflowVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 18.10.12
 * Time: 11:44
 * <p/>
 * Действие машины состояний, при котором машина состояний переходит в режим ожидания
 * и предлагает пользователю выбор дальнейшиго действия над документом.
 */
public class FinishStateWithTransitionAction extends StateMachineAction {

	private List<NextState> states = new ArrayList<NextState>();
	private static String PROP_LABEL = "labelId";
	private static String PROP_WORKFLOW = "workflowId";
	private static String PROP_OUTPUT_VARIABLE_NAME = "variable";
	private static String PROP_OUTPUT_VARIABLE_VALUE = "variableValue";
	private static String PROP_CONDITION_ACCESS = "conditionAccess";

	@Override
	public void init(Element action, String processId) {
		String outputVariable = action.attribute(PROP_OUTPUT_VARIABLE_NAME);
		List<Element> attributes = action.elements("attribute");
		for (Element attribute : attributes) {
			List<Element> parameters = attribute.elements("parameter");
			String actionId = attribute.attribute("name");
			String label = "";
			String workflowId = null;
			String variableValue = "";
			String conditionAccess = "";
			for (Element parameter : parameters) {
				String name = parameter.attribute("name");
				String value = parameter.attribute("value");
				if (PROP_LABEL.equalsIgnoreCase(name)) {
					label = value;
				} else if (PROP_WORKFLOW.equalsIgnoreCase(name)) {
					workflowId = value;
				} else if (PROP_OUTPUT_VARIABLE_VALUE.equalsIgnoreCase(name)) {
					variableValue = value;
				} else if (PROP_CONDITION_ACCESS.equalsIgnoreCase(name)) {
					conditionAccess = value;
				}
			}
			WorkflowVariables variables = new WorkflowVariables(attribute.element("workflowVariables"));
			NextState nextState = new NextState(actionId, label, workflowId, conditionAccess, outputVariable, variableValue, variables);
			states.add(nextState);
		}
	}

	public List<NextState> getStates() {
		return states;
	}

	@Override
	public void execute(DelegateExecution execution) {
		for (NextState state : states) {
			execution.setVariable(state.getOutputVariableName(), "");
		}
	}

	public class NextState {

		private String actionId;
		private String label;
		private String workflowId;
		private String conditionAccess;
		private String outputVariableName;
		private String outputVariableValue;
		private WorkflowVariables variables;

		NextState(String actionId, String label, String workflowId, String conditionAccess, String outputVariableName, String outputVariableValue, WorkflowVariables variables) {
			this.actionId = actionId;
			this.label = label;
			this.workflowId = workflowId;
			this.conditionAccess = conditionAccess;
			this.outputVariableName = outputVariableName;
			this.outputVariableValue = outputVariableValue;
			this.variables = variables;
		}

		public String getActionId() {
			return actionId;
		}

		public String getLabel() {
			return label;
		}

		public String getWorkflowId() {
			return workflowId;
		}

		public String getConditionAccess() {
			return conditionAccess;
		}

		public String getOutputVariableName() {
			return outputVariableName;
		}

		public String getOutputVariableValue() {
			return outputVariableValue;
		}

		public WorkflowVariables getVariables() {
			return variables;
		}
	}
}
