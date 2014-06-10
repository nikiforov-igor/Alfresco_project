package ru.it.lecm.statemachine.action;

//import org.activiti.engine.impl.util.xml.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 24.10.12
 * Time: 14:48
 */
public class WorkflowVariables {

	/**
	 * Типы передаваемых значений для переменных процессов
	 */
	public enum Type {
		/**
		 * Тип передачи поле документа
		 */
		FIELD,
		/**
		 * Тип передачи переменная процесса
		 */
		VARIABLE,
		/**
		 * Тип передачи значение
		 */
		VALUE
	}

	private List<WorkflowVariable> input = new ArrayList<WorkflowVariable>();
	private List<WorkflowVariable> output = new ArrayList<WorkflowVariable>();
	
	public WorkflowVariables() {
		
	}

	public WorkflowVariables(WorkflowVariables workflowVariablesElement) {
//		if (workflowVariablesElement == null) return;
//
//		List<Element> variables = workflowVariablesElement.elements("input");
//		for (Element variable : variables) {
//			input.add(pack(variable));
//		}
//		variables = workflowVariablesElement.elements("output");
//		for (Element variable : variables) {
//			output.add(pack(variable));
//		}
	}

	public List<WorkflowVariable> getInput() {
		return input;
	}

	public List<WorkflowVariable> getOutput() {
		return output;
	}
	
	public void addInput(String fromType, String fromValue, String toType, String toValue) {
		input.add(new WorkflowVariable(Type.valueOf(fromType), fromValue, Type.valueOf(toType), toValue));
	}
	
	public void addOutput(String fromType, String fromValue, String toType, String toValue) {
		output.add(new WorkflowVariable(Type.valueOf(fromType), fromValue, Type.valueOf(toType), toValue));
	}

	private WorkflowVariable pack(WorkflowVariables workflowVariableElement) {
//		Type fromType = Type.valueOf(workflowVariableElement.attribute("fromType"));		String fromField = workflowVariableElement.attribute("fromField");
//		String fromValue = workflowVariableElement.attribute("fromValue");
//		Type toType = Type.valueOf(workflowVariableElement.attribute("toType"));
//		String toValue = workflowVariableElement.attribute("toValue");
//		return new WorkflowVariable(fromType, fromValue, toType, toValue);
		return null;
	}

	public class WorkflowVariable {

		private Type fromType = null;
		private String fromValue = null;
		private Type toType = null;
		private String toValue = null;

		public WorkflowVariable(Type fromType, String fromValue, Type toType, String toValue) {
			this.fromType = fromType;
			this.fromValue = fromValue;
			this.toType = toType;
			this.toValue = toValue;
		}

		public Type getFromType() {
			return fromType;
		}

		public String getFromValue() {
			return fromValue;
		}

		public Type getToType() {
			return toType;
		}

		public String getToValue() {
			return toValue;
		}
	}

}
