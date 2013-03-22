package ru.it.lecm.regnumbers.bean;

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
	public String getNumber(NodeRef documetNode, String templateStr) {
		Parser parser = new ParserImpl(applicationContext);
		return parser.runTemplate(templateStr, documetNode);
	}

	@Override
	public String getNumber(NodeRef documetNode, NodeRef templateNode) {
		throw new UnsupportedOperationException("getNumber(NodeRef, NodeRef) not supported yet.");
	}

	@Override
	public boolean isNumberUnique(String number) {
		throw new UnsupportedOperationException("isNumberUnique(String) not supported yet.");
	}

	@Override
	public boolean validateTemplate(String templateStr) {
		Parser parser = new ParserImpl(applicationContext);
		return parser.validateTemplate(templateStr);
	}
}
