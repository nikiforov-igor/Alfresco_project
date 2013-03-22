package ru.it.lecm.regnumbers.template;

import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 *
 * @author vlevin
 */
public class ParserImpl implements Parser {

	private Document doc;
	private StandardEvaluationContext context;
	private ApplicationContext applicationContext;
	private ExpressionParser expressionParser;
	final private static org.slf4j.Logger logger = LoggerFactory.getLogger(ParserImpl.class);

	public ParserImpl(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;

		context = new StandardEvaluationContext(this);
		context.setBeanResolver(new BeanFactoryResolver(applicationContext));

		context.registerFunction("formatDate", Utils.getDeclaredMethod("formatDate", String.class, Date.class));
		context.registerFunction("formatCurrentDate", Utils.getDeclaredMethod("formatCurrentDate", String.class));
		context.registerFunction("formatNumber", Utils.getDeclaredMethod("formatNumber", String.class, Long.class));
		context.registerFunction("employeeOrgUnitCode", Utils.getDeclaredMethod("employeeOrgUnitCode", NodeRef.class));
		context.registerFunction("employeeInitials", Utils.getDeclaredMethod("employeeInitials", NodeRef.class));
		context.registerFunction("employeeNumber", Utils.getDeclaredMethod("employeeNumber", NodeRef.class));

		expressionParser = new SpelExpressionParser();
	}

	@Override
	public String runTemplate(String templateStr, NodeRef documentNode) throws TemplateParseException, TemplateRunException {
		String result = null;
		setDoc(documentNode);
		Expression exp = null;
		try {
			exp = expressionParser.parseExpression(templateStr);
		} catch (ParseException ex) {
			String errorMsg = String.format("Error parsing registration number template '%s'", templateStr);
			logger.error(errorMsg, ex);
			throw new TemplateParseException(errorMsg, ex);
		} catch (Exception ex) {
			String errorMsg = String.format("Internal error parsing registration number template '%s'", templateStr);
			logger.error(errorMsg, ex);
			throw new TemplateParseException(errorMsg, ex);
		}
		if (exp != null) {
			try {
				result = exp.getValue(context, String.class);
				logger.debug("Generated new regnum: " + result);
			} catch (EvaluationException ex) {
				String errorMsg = String.format("Error getting registration number using template '%s'", templateStr);
				logger.error(errorMsg, ex);
				throw new TemplateRunException(errorMsg, ex);
			}
		}
		return result;
	}

	@Override
	public void parseTemplate(String templateStr) throws TemplateParseException {

		try {
			expressionParser.parseExpression(templateStr);
		} catch (ParseException ex) {
			String errorMsg = String.format("Error parsing registration number template '%s'", templateStr);
			logger.debug("Validating template: " + errorMsg);
			throw new TemplateParseException(errorMsg, ex);
		} catch (Exception ex) {
			String errorMsg = String.format("Internal error parsing registration number template '%s'", templateStr);
			logger.debug("Validating template: " + errorMsg);
			throw new TemplateParseException(errorMsg, ex);
		}

	}

	public Document getDoc() {
		return doc;
	}

	private void setDoc(NodeRef doc) {
		this.doc = new DocumentImpl(doc, applicationContext);
	}
}
