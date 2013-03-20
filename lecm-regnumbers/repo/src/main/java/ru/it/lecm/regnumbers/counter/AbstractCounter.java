package ru.it.lecm.regnumbers.counter;

import java.util.Calendar;
import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;
import ru.it.lecm.regnumbers.RegNumbersService;

/**
 *
 * @author vlevin
 */
public abstract class AbstractCounter implements Counter {

	protected NodeService nodeService;
	protected TransactionService transactionService;
	protected NodeRef counterNodeRef;

	AbstractCounter(NodeRef counterNodeRef, NodeService nodeService, TransactionService transactionService) {
		this.counterNodeRef = counterNodeRef;
		this.nodeService = nodeService;
		this.transactionService = transactionService;
	}

	/**
	 * @param nodeRef ссылка на объект счетчика.
	 * @return текущее значение счетчика.
	 */
	protected long getValue(final NodeRef nodeRef) {
		return (Long) nodeService.getProperty(nodeRef, RegNumbersService.PROP_VALUE);
	}

	/**
	 * @param nodeRef ссылка на объект счетчика.
	 * @param value новое значение счетчика.
	 */
	protected void setValue(final NodeRef nodeRef, final long value) {
		nodeService.setProperty(nodeRef, RegNumbersService.PROP_VALUE, value);
	}

	/**
	 * @param nodeRef ссылка на объект счетчика.
	 * @return тип документа, для которого счетчик создан.
	 */
	protected String getDocType(final NodeRef nodeRef) {
		return (String) nodeService.getProperty(nodeRef, RegNumbersService.PROP_DOCTYPE);
	}

	/**
	 * @param nodeRef ссылка на объект счетчика.
	 * @return год, в рамках которого счетчик сейчас существует.
	 */
	protected Integer getYear(final NodeRef nodeRef) {
		return (Integer) nodeService.getProperty(nodeRef, RegNumbersService.PROP_YEAR);
	}

	/**
	 * @param nodeRef ссылка на объект счетчика.
	 * @param year новый год для счетчика.
	 */
	protected void setYear(final NodeRef nodeRef, final int year) {
		nodeService.setProperty(nodeRef, RegNumbersService.PROP_YEAR, year);
	}

	protected int getCurYear() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return cal.get(Calendar.YEAR);
	}
}
