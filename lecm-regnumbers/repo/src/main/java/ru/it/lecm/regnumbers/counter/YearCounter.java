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
public class YearCounter extends AbstractCounter implements RetryingTransactionCallback<Long> {

	YearCounter(NodeRef counterNodeRef, NodeService nodeService, TransactionService transactionService) {
		super(counterNodeRef, nodeService, transactionService);
	}

	@Override
	public Long execute() {
		long value;

		int curYear = getCurYear();
		if (curYear != getYear(counterNodeRef)) {
			setYear(counterNodeRef, curYear);
			value = 0;
		} else {
			value = getValue(counterNodeRef);
		}

		value++;
		setValue(counterNodeRef, value);

		return value;
	}

	@Override
	public long getValue() {
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
		return execute();
//		return transactionHelper.doInTransaction(this, false);
	}
}
