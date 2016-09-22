package ru.it.lecm.regnumbers.counter;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vlevin
 */
public class PlainCounter extends AbstractCounter implements RetryingTransactionCallback<Long> {
	final static protected Logger logger = LoggerFactory.getLogger(PlainCounter.class);
	
	PlainCounter(NodeRef counterNodeRef, NodeService nodeService, TransactionService transactionService) {
		super(counterNodeRef, nodeService, transactionService);
	}

	@Override
	public Long execute() {
		long value = getValue(counterNodeRef);
		value++;
		setValue(counterNodeRef, value);
		return value;
	}

	@Override
	public long getValue() {
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
		return execute();
//		return transactionHelper.doInTransaction(this, false, true);
	}
}
