package ru.it.lecm.documents.beans;

import java.util.List;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.transaction.TransactionService;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;

/**
 *
 * @author vmalygin
 */
public class MessageToRepositoryLoader implements RunAsWork<Void>, RetryingTransactionCallback<Void> {

	private TransactionService transactionService;
	private LecmBasePropertiesService propertiesService;
	private DocumentMessageService documentMessageService;
	private boolean useDefaultMessages;
	private List<String> messages;

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setDocumentMessageService(DocumentMessageService documentMessageService) {
		this.documentMessageService = documentMessageService;
	}

	public void setPropertiesService(LecmBasePropertiesService propertiesService) {
		this.propertiesService = propertiesService;
	}

	public void setUseDefaultMessages(String useDefaultMessages) {
		this.useDefaultMessages = Boolean.valueOf(useDefaultMessages);
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	@Override
	public Void doWork() throws Exception {
		return transactionService.getRetryingTransactionHelper().doInTransaction(this, transactionService.isReadOnly(), false);
	}

	@Override
	public Void execute() throws Throwable {
		for (String messageLocation : messages) {
			documentMessageService.loadMessagesFromLocation(messageLocation, useDefaultMessages);
		}
		return null;
	}


	public void init() {
		try {
			Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.messages.editor.enabled");
			boolean enabled = (editorEnabled == null) ? true : Boolean.valueOf((String) editorEnabled);
			if (enabled) {
				AuthenticationUtil.runAsSystem(this);
			}
		} catch (LecmBaseException ex) {
			throw new IllegalStateException("Cannot read document messages properties");
		}
	}
}
