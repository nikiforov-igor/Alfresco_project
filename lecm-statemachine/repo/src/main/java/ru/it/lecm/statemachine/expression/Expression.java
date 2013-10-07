package ru.it.lecm.statemachine.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import ru.it.lecm.documents.beans.DocumentService;
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
	private static OrgstructureBean orgstructureBean;
	private static DocumentService documentService;

    private static final transient Logger logger = LoggerFactory.getLogger(Expression.class);

    public Expression() {
    }

    public Expression(NodeRef document, ServiceRegistry serviceRegistry) {
        StateMachineHelper helper = new StateMachineHelper();
        NodeRef documentRef = document;
        this.doc = new ExpressionDocument(documentRef, serviceRegistry);
        this.user = new ExpressionUser(document, serviceRegistry, orgstructureBean, documentService);
        String executionId = helper.getStatemachineId(document);
        if (executionId != null) {
            this.state = helper.getVariables(executionId);
        }
        this.context = new StandardEvaluationContext(this);
    }

	public boolean execute(String expression) {
		if ("".equals(expression)) {
			expression = "true";
		}
        try {
	        Boolean result = new SpelExpressionParser().parseExpression(expression).getValue(context, Boolean.class);
	        return result != null && result;
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

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        Expression.orgstructureBean = orgstructureBean;
    }

    public void setDocumentService(DocumentService documentService) {
        Expression.documentService = documentService;
    }
}
