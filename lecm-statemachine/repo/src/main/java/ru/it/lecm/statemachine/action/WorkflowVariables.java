package ru.it.lecm.statemachine.action;

import org.activiti.engine.impl.util.xml.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 24.10.12
 * Time: 14:48
 */
public class WorkflowVariables {

	private List<WorkflowVariable> input = new ArrayList<WorkflowVariable>();
	private List<WorkflowVariable> output = new ArrayList<WorkflowVariable>();

	public WorkflowVariables(Element workflowVariablesElement) {
		List<Element> variables = workflowVariablesElement.elements("input");
		for (Element variable : variables) {
			input.add(pack(variable));
		}
		variables = workflowVariablesElement.elements("output");
		for (Element variable : variables) {
			output.add(pack(variable));
		}
	}

	public List<WorkflowVariable> getInput() {
		return input;
	}

	public List<WorkflowVariable> getOutput() {
		return output;
	}

	private WorkflowVariable pack(Element workflowVariableElement) {
		String from = workflowVariableElement.attribute("from");
		String to = workflowVariableElement.attribute("to");
		String value = workflowVariableElement.attribute("value");
		return new WorkflowVariable(from, to, value);
	}

	public class WorkflowVariable {

		private String from = null;
		private String to = null;
		private String value = null;

		public WorkflowVariable(String from, String to, String value) {
			this.from = from;
			this.to = to;
			this.value = value;
		}

		public String getFrom() {
			return from;
		}

		public String getTo() {
			return to;
		}

		public String getValue() {
			return value;
		}
	}

}
