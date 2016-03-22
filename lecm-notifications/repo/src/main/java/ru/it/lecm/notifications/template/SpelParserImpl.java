package ru.it.lecm.notifications.template;

import ru.it.lecm.notifications.beans.TemplateParseException;
import ru.it.lecm.notifications.beans.TemplateRunException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Класс служит контекстом для парсера Spring Expression Language.
 *
 * @author vkuprin
 * на основе ru.it.lecm.regnumbers.template.ParserImpl
 */
public class SpelParserImpl extends AbstractParserImpl {

	private final StandardEvaluationContext context;
	private final ExpressionParser expressionParser;
	final private static org.slf4j.Logger logger = LoggerFactory.getLogger(SpelParserImpl.class);
	/**
	 * Ограничители для выражений, которые интерпретирует SpEL. Текст между
	 * указанными символами (сочетаниями символов) считается интерпретируемым
	 * выражением. Вне них - текстом.
	 */
	final private static TemplateParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext("{", "}");

	public SpelParserImpl(ApplicationContext applicationContext) {
		super(applicationContext);

		context = new StandardEvaluationContext(this);
		context.setBeanResolver(new BeanFactoryResolver(applicationContext));

		// Регистрация утилитарных функций SpEL
		Map<String, Method> templateFunctions = Utils.getTemplateFunctionMethods();
		for (Entry<String, Method> entry : templateFunctions.entrySet()) {
			context.registerFunction(entry.getKey(), entry.getValue());
		}
		expressionParser = new SpelExpressionParser();
	}

	@Override
	public String runTemplate(String templateStr, Map<String,Object> objectsMap) throws TemplateParseException, TemplateRunException {
		String result = null;
		setObjects(objectsMap);
		context.setVariables(getObjects().getFullMap());
		Expression exp = null;

		try {
			exp = expressionParser.parseExpression(templateStr, TEMPLATE_PARSER_CONTEXT);
		} catch (ParseException ex) {
			String errorMsg = String.format("Error parsing template '%s'", templateStr);
			logger.error(errorMsg, ex);
			throw new TemplateParseException(errorMsg, ex);
		} catch (Exception ex) {
			String errorMsg = String.format("Internal error parsing template '%s'", templateStr);
			logger.error(errorMsg, ex);
			throw new TemplateParseException(errorMsg, ex);
		}
		if (exp != null) {
			try {
				result = exp.getValue(context, String.class);
				logger.debug("Generated new message: " + result);
			} catch (EvaluationException ex) {
				String errorMsg = String.format("Error getting notification message using template '%s'", templateStr);
				logger.error(errorMsg, ex);
				throw new TemplateRunException(errorMsg, ex);
			}
		}
		return result;
	}

	@Override
	public void parseTemplate(String templateStr) throws TemplateParseException {

		try {
			expressionParser.parseExpression(templateStr, TEMPLATE_PARSER_CONTEXT);
		} catch (ParseException ex) {
			String errorMsg = String.format("Error parsing template '%s'", templateStr);
			logger.debug("Validating template: " + errorMsg);
			throw new TemplateParseException(errorMsg, ex);
		} catch (Exception ex) {
			String errorMsg = String.format("Internal error parsing template '%s'", templateStr);
			logger.debug("Validating template: " + errorMsg);
			throw new TemplateParseException(errorMsg, ex);
		}
	}
}
