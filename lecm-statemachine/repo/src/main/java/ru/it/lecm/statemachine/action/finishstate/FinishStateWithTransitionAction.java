package ru.it.lecm.statemachine.action.finishstate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import ru.it.lecm.statemachine.StatemachineActionConstants;
import ru.it.lecm.statemachine.action.Conditions;
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
	private static String PROP_FORM_FOLDER = "formFolder";
	private static String PROP_FORM_TYPE = "formType";
	private static String PROP_FORM_CONNECTION = "formConnection";
    private static String PROP_IS_SYSTEM_FORM_CONNECTION = "systemFormConnection";
	private static String PROP_SCRIPT = "script";

	@Override
	public void init(Element action, String processId) {
		String outputVariable = action.attribute(PROP_OUTPUT_VARIABLE_NAME);
		List<Element> attributes = action.elements("attribute");
		for (Element attribute : attributes) {
			List<Element> parameters = attribute.elements("parameter");
			String actionId = attribute.attribute("name");
			String label = "";
			String workflowId = null;
			String formFolder = null;
			String formType = null;
			String formConnection = null;
            boolean isSystemFormConnection = true;
			String variableValue = "";
			String conditionAccess = "";
			String script = "";
			boolean stopSubWorkflows = false;
			for (Element parameter : parameters) {
				String name = parameter.attribute("name");
				String value = parameter.attribute("value");
				if (PROP_LABEL.equalsIgnoreCase(name)) {
					label = value;
				} else if (PROP_WORKFLOW.equalsIgnoreCase(name)) {
					workflowId = value;
				} else if (PROP_FORM_TYPE.equalsIgnoreCase(name)) {
                    formType = value;
                } else if (PROP_FORM_FOLDER.equalsIgnoreCase(name)) {
                    formFolder = value;
                } else if (PROP_FORM_CONNECTION.equalsIgnoreCase(name)) {
                    formConnection = value;
                } else if (PROP_IS_SYSTEM_FORM_CONNECTION.equalsIgnoreCase(name)) {
                    isSystemFormConnection = Boolean.valueOf(value);
                } else if (PROP_OUTPUT_VARIABLE_VALUE.equalsIgnoreCase(name)) {
					variableValue = value;
				} else if (PROP_CONDITION_ACCESS.equalsIgnoreCase(name)) {
					conditionAccess = value;
				} else if (PROP_SCRIPT.equalsIgnoreCase(name)) {
                    script = parameter.getText();
                } else if (StatemachineActionConstants.PROP_STOP_SUBWORKFLOWS.equalsIgnoreCase(name)) {
                    stopSubWorkflows = Boolean.parseBoolean(value);
				}
			}
            Conditions conditions = new Conditions(attribute.element("conditions"));
			WorkflowVariables variables = new WorkflowVariables(attribute.element("workflowVariables"));
			NextState nextState = new NextState(actionId, label, workflowId, conditions, outputVariable, variableValue, variables, stopSubWorkflows, formType, formFolder, formConnection, isSystemFormConnection, script);
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
		private String formType;
		private String formFolder;
		private String formConnection;
		private Conditions conditionAccess;
		private String outputVariableName;
		private String outputVariableValue;
		private String script;
		private WorkflowVariables variables;
		private boolean stopSubWorkflows;
		private boolean isSystemFormConnection;

		NextState(String actionId, String label, String workflowId, Conditions conditionAccess, String outputVariableName, String outputVariableValue, WorkflowVariables variables, boolean stopSubWorkflows, String formType, String formFolder, String formConnection, boolean isSystemFormConnection, String script) {
			this.actionId = actionId;
			this.label = label;
			this.workflowId = workflowId;
			this.conditionAccess = conditionAccess;
			this.outputVariableName = outputVariableName;
			this.outputVariableValue = outputVariableValue;
			this.variables = variables;
            this.stopSubWorkflows = stopSubWorkflows;
            this.formType = formType;
            this.formFolder = formFolder;
            this.formConnection = formConnection;
            this.isSystemFormConnection = isSystemFormConnection;
            this.script = script;
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

		public Conditions getConditionAccess() {
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

        public boolean isStopSubWorkflows() {
            return stopSubWorkflows;
        }

        public String getScript() {
            return script;
        }

        public boolean isForm() {
            return formType != null;
        }

        public String getFormType() {
            return formType;
        }

        public String getFormFolder() {
            return formFolder;
        }

        public String getFormConnection() {
            return formConnection;
        }

        public boolean isSystemFormConnection() {
            return isSystemFormConnection;
        }

    }
}
