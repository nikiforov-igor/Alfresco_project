package ru.it.lecm.documents.expression;

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

import java.lang.reflect.Method;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 03.02.2017
 * Time: 10:38
 */
public abstract class BaseSpellExpression {
    private static final transient Logger logger = LoggerFactory.getLogger(BaseSpellExpression.class);

    protected ApplicationContext applicationContext;
    protected StandardEvaluationContext evaluationContext;

    protected static OrgstructureBean orgstructureBean;
    protected static DocumentService documentService;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        BaseSpellExpression.orgstructureBean = orgstructureBean;
    }

    public void setDocumentService(DocumentService documentService) {
        BaseSpellExpression.documentService = documentService;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public StandardEvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    public BaseSpellExpression() {

    }

    public BaseSpellExpression(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        evaluationContext = new StandardEvaluationContext(this);
        evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));

        Map<String, Method> templateFunctions = SpELUtils.getTemplateFunctionMethods();
        for (Map.Entry<String, Method> entry : templateFunctions.entrySet()) {
            getEvaluationContext().registerFunction(entry.getKey(), entry.getValue());
        }
    }

    public boolean executeAsBoolean(String expression) {
        if ("".equals(expression)) {
            expression = "true";
        }
        try {
            Boolean result = new SpelExpressionParser().parseExpression(expression).getValue(evaluationContext, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            logger.error("Expression: " + expression + " has errors", e);
            return false;
        }
    }

    public String executeAsString(String expression) {
        return executeAsString(expression, true);
    }

    public String executeAsString(String expression, boolean withContext) {
        try {
            org.springframework.expression.Expression spelExpression =
                    withContext ? new SpelExpressionParser().parseExpression(expression, new TemplateParserContext()) :
                            new SpelExpressionParser().parseExpression(expression);

            String result = spelExpression.getValue(evaluationContext, String.class);
            return result != null ? result : "";
        } catch (Exception e) {
            logger.error("Expression: " + expression + " has errors", e);
            return "";
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
