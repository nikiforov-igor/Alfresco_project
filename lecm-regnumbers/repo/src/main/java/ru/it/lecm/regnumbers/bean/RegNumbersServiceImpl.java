package ru.it.lecm.regnumbers.bean;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.regnumbers.counter.Counter;
import ru.it.lecm.regnumbers.counter.CounterType;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.counter.CounterFactory;

/**
 *
 * @author vlevin
 */
public class RegNumbersServiceImpl extends BaseBean implements RegNumbersService {
	final private static Logger logger = LoggerFactory.getLogger(RegNumbersServiceImpl.class);

	private CounterFactory сounterFactory;

	public void setСounterFactory(CounterFactory сounterFactory) {
		this.сounterFactory = сounterFactory;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "regNumbersCounterFactory", сounterFactory);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
	}

	@Override
	public long getCounterValue(CounterType counterType, NodeRef document) {
		Counter counter = сounterFactory.getCounter(counterType, document);
		return counter.getValue();
	}

	@Override
	public String getNumber(NodeRef documetNode, String templateStr) {
		throw new UnsupportedOperationException("getNumber(NodeRef, String) not supported yet.");
	}

	@Override
	public String getNumber(NodeRef documetNode, NodeRef templateNode) {
		throw new UnsupportedOperationException("getNumber(NodeRef, NodeRef) not supported yet.");
	}

	@Override
	public boolean isNumberUnique(String number) {
		throw new UnsupportedOperationException("isNumberUnique(String) not supported yet.");
	}
}
