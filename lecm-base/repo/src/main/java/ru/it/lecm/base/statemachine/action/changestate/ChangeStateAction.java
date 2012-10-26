package ru.it.lecm.base.statemachine.action.changestate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import ru.it.lecm.base.statemachine.action.StateMachineAction;
import ru.it.lecm.base.statemachine.action.WorkflowVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 18.10.12
 * Time: 11:44
 */
public class ChangeStateAction extends StateMachineAction {

    public static String PROP_CHANGE_STATE_PREV_TASK_ID = "changeStatePrevTaskId";
    public static String PROP_CHANGE_STATE_CUR_TASK_ID = "changeStateCurTaskId";
    public static String PROP_CHANGE_STATE_ACTION_ID = "changeStateActionId";

    private List<NextState> states = new ArrayList<NextState>();
    private static String PROP_LABEL = "labelId";
    private static String PROP_WORKFLOW = "workflowId";
    private static String PROP_OUTPUT_VARIABLE_NAME = "variable";
    private static String PROP_OUTPUT_VARIABLE_VALUE = "variableValue";

    @Override
    public void init(Element action) {
        String outputVariable = action.attribute(PROP_OUTPUT_VARIABLE_NAME);
        List<Element> attributes  = action.elements("attribute");
        for (Element attribute : attributes) {
            List<Element> parameters = attribute.elements("parameter");
            String actionId = attribute.attribute("name");
            String label = "";
            String workflowId = null;
            String variableValue = "";
            for (Element parameter : parameters) {
                String name = parameter.attribute("name");
                String value = parameter.attribute("value");
                if (PROP_LABEL.equalsIgnoreCase(name)) {
                    label = value;
                } else if (PROP_WORKFLOW.equalsIgnoreCase(name)) {
                    workflowId = "activiti$" + value;
                } else if (PROP_OUTPUT_VARIABLE_VALUE.equalsIgnoreCase(name)) {
                    variableValue = value;
                }
            }
            WorkflowVariables variables = null;
            Element workflowVariablesElement = attribute.element("workflowVariables");
            if (workflowVariablesElement != null) {
                variables = new WorkflowVariables(workflowVariablesElement);
            }
            NextState nextState = new NextState(actionId, label, workflowId, outputVariable, variableValue, variables);
            states.add(nextState);
        }
    }

    public List<NextState> getStates() {
        return states;
    }

    @Override
    public void execute(DelegateExecution execution) {
    }

    public class NextState {

        private String actionId;
        private String label;
        private String workflowId;
        private String outputVariableName;
        private String outputVariableValue;
        private WorkflowVariables variables;

        NextState(String actionId, String label, String workflowId, String outputVariableName, String outputVariableValue, WorkflowVariables variables) {
            this.actionId = actionId;
            this.label = label;
            this.workflowId = workflowId;
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
