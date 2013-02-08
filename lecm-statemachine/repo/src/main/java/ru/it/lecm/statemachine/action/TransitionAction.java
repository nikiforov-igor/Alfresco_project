package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import ru.it.lecm.statemachine.expression.Expression;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 28.11.12
 * Time: 13:37
 */
public class TransitionAction extends StateMachineAction {

	private String variableName;
	private String expressionValue;

	private static final String PROP_VARIABLE_NAME = "variableName";
	private static final String PROP_EXPRESSION = "expression";

	@Override
	public void execute(DelegateExecution execution) {
		Expression expression = new Expression(execution, getServiceRegistry());
		boolean result = expression.execute(expressionValue);
		execution.setVariable(variableName, result);
	}

	@Override
	public void init(Element actionElement, String processId) {
		List<Element> attributes = actionElement.elements("attribute");
		for (Element attribute : attributes) {
			String name = attribute.attribute("name");
			String value = attribute.attribute("value");
			if (PROP_VARIABLE_NAME.equalsIgnoreCase(name)) {
				variableName = value;
			} else if (PROP_EXPRESSION.equalsIgnoreCase(name)) {
				expressionValue = value;
			}
		}
	}

	public String getExpression() {
		return expressionValue;
	}
}
