package ru.it.lecm.documents.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.utils.SpELUtils;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 07.02.13
 * Time: 16:44
 */
public class Expression {

	private Map<String, Object> state = new HashMap<>();
	private ExpressionDocument doc;
	private ExpressionUser user;
	private StandardEvaluationContext context;
	private static OrgstructureBean orgstructureBean;
	private static DocumentService documentService;
	private static StateMachineServiceBean stateMachineService;
	private ApplicationContext applicationContext;

    private static final transient Logger logger = LoggerFactory.getLogger(Expression.class);

    public Expression() {
    }

    public Expression(NodeRef document, ServiceRegistry serviceRegistry, ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
        this.doc = new ExpressionDocument(document, serviceRegistry);
        this.user = new ExpressionUser(document, serviceRegistry, orgstructureBean, documentService);
        String executionId = stateMachineService.getStatemachineId(document);
        if (!"Не запущен".equals(executionId)) {
            this.state = stateMachineService.getVariables(executionId);
        }
        this.context = new StandardEvaluationContext(this);
		this.context.setBeanResolver(new BeanFactoryResolver(this.applicationContext));

        // Регистрация утилитарных функций SpEL
        Map<String, Method> templateFunctions = SpELUtils.getTemplateFunctionMethods();
        for (Map.Entry<String, Method> entry : templateFunctions.entrySet()) {
            context.registerFunction(entry.getKey(), entry.getValue());
        }
    }

	public boolean executeAsBoolean(String expression) {
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

    public String executeAsString(String expression) {
        try {
            String result = new SpelExpressionParser().parseExpression(expression, new TemplateParserContext()).getValue(context, String.class);
            return result != null ? result : "";
        } catch (Exception e) {
            logger.error("Expression: " + expression + " has errors", e);
            return "";
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

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }
}
