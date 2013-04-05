package ru.it.lecm.statemachine.expression;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
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
	private ExpressionUser user;
	private StandardEvaluationContext context;

    private static Log logger = LogFactory.getLog(Expression.class);

    public Expression(NodeRef document, ServiceRegistry serviceRegistry, OrgstructureBean orgstructureBean) {
        this.doc = new ExpressionDocument(document, serviceRegistry);
        this.user = new ExpressionUser(document, serviceRegistry, orgstructureBean);
        context = new StandardEvaluationContext(this);
    }

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
        try {
            return new SpelExpressionParser().parseExpression(expression).getValue(context, Boolean.class);
        } catch (Exception e) {
            logger.error("Expression: " + expression + " has errors", e);
            return false;
        }
    }

	public Object state(String variableName) {
		return state.get(variableName);
	}

	public ExpressionDocument getDoc() {
		return doc;
	}

    public ExpressionUser getUser() {
        return user;
    }

}
