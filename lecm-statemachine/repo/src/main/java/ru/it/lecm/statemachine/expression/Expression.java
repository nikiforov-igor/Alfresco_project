package ru.it.lecm.statemachine.expression;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import ru.it.lecm.statemachine.StateMachineHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 07.02.13
 * Time: 16:44
 */
public class Expression {

	private Map<String, Object> state = new HashMap<String, Object>();
	private ExpressionDocument doc;
	private StandardEvaluationContext context;

	public Expression(DelegateExecution execution, ServiceRegistry serviceRegistry) {
		StateMachineHelper helper = new StateMachineHelper();
		NodeRef documentRef = helper.getStatemachineDocument(execution.getId());
		doc = new ExpressionDocument(documentRef, serviceRegistry);
		state = execution.getVariables();
		context = new StandardEvaluationContext(this);
	}

	public Expression(NodeRef doc, ServiceRegistry serviceRegistry) {
		this.doc = new ExpressionDocument(doc, serviceRegistry);
		context = new StandardEvaluationContext(this);
	}

	public Expression(NodeRef doc, Map<String, Object> variables, ServiceRegistry serviceRegistry) {
		this.doc = new ExpressionDocument(doc, serviceRegistry);
		state = variables;
		context = new StandardEvaluationContext(this);
	}

	public boolean execute(String expression) {
		if ("".equals(expression)) {
			expression = "true";
		}
		return new SpelExpressionParser().parseExpression(expression).getValue(context, Boolean.class);
	}

	public Object state(String variableName) {
		return state.get(variableName);
	}

	public ExpressionDocument getDoc() {
		return doc;
	}

}
