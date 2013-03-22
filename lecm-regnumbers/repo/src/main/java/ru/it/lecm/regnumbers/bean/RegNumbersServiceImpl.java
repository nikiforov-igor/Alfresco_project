package ru.it.lecm.regnumbers.bean;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.Parser;
import ru.it.lecm.regnumbers.template.ParserImpl;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

/**
 *
 * @author vlevin
 */
public class RegNumbersServiceImpl extends BaseBean implements RegNumbersService, ApplicationContextAware {

	final private static Logger logger = LoggerFactory.getLogger(RegNumbersServiceImpl.class);
	private ApplicationContext applicationContext;

	public final void init() {
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public String getNumber(NodeRef documentNode, String templateStr) throws TemplateParseException, TemplateRunException {
		Parser parser = new ParserImpl(applicationContext);
		return parser.runTemplate(templateStr, documentNode);
	}

	@Override
	public String getNumber(NodeRef documentNode, NodeRef templateNode) {
		throw new UnsupportedOperationException("getNumber(NodeRef, NodeRef) not supported yet.");
	}

	@Override
	public boolean isNumberUnique(String number) {
		throw new UnsupportedOperationException("isNumberUnique(String) not supported yet.");
	}

	@Override
	public String validateTemplate(String templateStr, boolean verbose) {
		String result = "";
		Parser parser = new ParserImpl(applicationContext);
		try {
			parser.parseTemplate(templateStr);
		} catch (TemplateParseException ex) {
			result = ex.getMessage() + " because of following: " + ex.getCause().getMessage();
			if (verbose) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				ex.printStackTrace(printWriter);

				result += stringWriter.toString();
			}
		}

		return result;
	}
}
