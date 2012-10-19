package ru.it.lecm.base.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 18.10.12
 * Time: 11:44
 */
public class ChooseStateAction extends StateMachineAction {

    private List<NextState> states = new ArrayList<NextState>();
    private static String PROP_LABEL = "labelId";
    private static String PROP_WORKFLOW = "workflowId";
    private static String PROP_OUTPUT_VARIABLE_NAME = "outputVariableName";
    private static String PROP_OUTPUT_VARIABLE_VALUE = "outputVariableValue";

    public ChooseStateAction(List<Element> attributes) {
        for (Element attribute : attributes) {
            List<Element> parameters = attribute.elements("parameter");
            String actionId = attribute.attribute("name");
            String label = "";
            String workflowId = "";
            String outputVariableName = "";
            String outputVariableValue = "";
            for (Element parameter : parameters) {
                String name = parameter.attribute("name");
                String value = parameter.attribute("value");
                if (PROP_LABEL.equalsIgnoreCase(name)) {
                    label = value;
                } else if (PROP_WORKFLOW.equalsIgnoreCase(name)) {
                    workflowId = "activiti$" + value;
                } else if (PROP_OUTPUT_VARIABLE_NAME.equalsIgnoreCase(name)) {
                    outputVariableName = value;
                } else if (PROP_OUTPUT_VARIABLE_VALUE.equalsIgnoreCase(name)) {
                    outputVariableValue = value;
                }
            }
            NextState nextState = new NextState(actionId, label, workflowId, outputVariableName, outputVariableValue);
            states.add(nextState);
        }
    }

    public List<NextState> getStates() {
        return states;
    }

    @Override
    public void execute(DelegateExecution execution) {
    }

    @Override
    public String getType() {
        return "chooseState";
    }

    public class NextState {

        private String actionId;
        private String label;
        private String workflowId;
        private String outputVariableName;
        private String outputVariableValue;

        NextState(String actionId, String label, String workflowId, String outputVariableName, String outputVariableValue) {
            this.actionId = actionId;
            this.label = label;
            this.workflowId = workflowId;
            this.outputVariableName = outputVariableName;
            this.outputVariableValue = outputVariableValue;
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
    }
}
