package ru.it.lecm.regnumbers.counter;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;

/**
 *
 * @author vlevin
 */
public class PlainCounter extends AbstractCounter implements RetryingTransactionCallback<Long> {

	PlainCounter(NodeRef counterNodeRef, NodeService nodeService, TransactionService transactionService) {
		super(counterNodeRef, nodeService, transactionService);
	}

	@Override
	public Long execute() throws Throwable {
		long value = getValue(counterNodeRef);
		value++;
		setValue(counterNodeRef, value);
		return value;
	}

	@Override
	public long getValue() {
		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();

		return transactionHelper.doInTransaction(this, false, true);
	}
}
