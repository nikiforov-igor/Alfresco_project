package ru.it.lecm.barcode.beans;

import java.util.List;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;

/**
 *
 * @author vlevin
 */
class BarcodePrintServiceRunner implements Runnable, AuthenticationUtil.RunAsWork<Object>, RetryingTransactionHelper.RetryingTransactionCallback<Object> {

	private final NodeRef documentRef;
	private final List<String> additionalStrings;
	private final TransactionService transactionService;
	private final BarcodePrintServiceImpl barcodePrintService;
	private final Logger logger;

	public BarcodePrintServiceRunner(NodeRef documentRef, List<String> additionalStrings, TransactionService transactionService, BarcodePrintServiceImpl barcodePrintService, Logger logger) {
		this.documentRef = documentRef;
		this.additionalStrings = additionalStrings;
		this.transactionService = transactionService;
		this.barcodePrintService = barcodePrintService;
		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			transactionService.getRetryingTransactionHelper().doInTransaction(this, true, true);
		} catch (Exception ex) {
			logger.error("Error printing barcode", ex);
		}
	}

	@Override
	public Object doWork() throws Exception {
		barcodePrintService.printSync(documentRef, additionalStrings);
		return null;
	}

	@Override
	public Object execute() throws Throwable {
		return AuthenticationUtil.runAsSystem(this);
	}
}
