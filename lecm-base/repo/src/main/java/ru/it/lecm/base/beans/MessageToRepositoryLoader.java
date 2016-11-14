package ru.it.lecm.base.beans;

import java.util.List;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 *
 * @author vmalygin
 */
public class MessageToRepositoryLoader extends AbstractLifecycleBean implements RunAsWork<Void>, RetryingTransactionCallback<Void> {

	private RepoAdminService repoAdminService;
	private TransactionService transactionService;
	private LecmBasePropertiesService propertiesService;
	private LecmMessageService lecmMessageService;
	private boolean useDefaultMessages;
	private List<String> messages;

	public void setRepoAdminService(RepoAdminService repoAdminService) {
		this.repoAdminService = repoAdminService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setLecmMessageService(LecmMessageService lecmMessageService) {
		this.lecmMessageService = lecmMessageService;
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
		return transactionService.getRetryingTransactionHelper().doInTransaction(this, transactionService.isReadOnly());
	}

	@Override
	public Void execute() throws Throwable {
		for (String messageLocation : messages) {
			loadMessagesFromLocation(messageLocation, useDefaultMessages);
		}
		return null;
	}

	private void loadMessagesFromLocation(String messageLocation, boolean useDefault) {
		String locationBaseName = messageLocation;
		int idx = messageLocation.lastIndexOf('/');
		if (idx != -1) {
			if (idx < messageLocation.length() - 1) {
				locationBaseName = messageLocation.substring(idx + 1);
			} else {
				locationBaseName = null;
			}
		}
		if (locationBaseName == null || "".equals(locationBaseName)) {
			throw new AlfrescoRuntimeException("Loading message from location failed - missing bundle base name");
		}
		List<String> messageBundles = repoAdminService.getMessageBundles();
		boolean bundleExists = messageBundles.contains(locationBaseName);
		if (!bundleExists || useDefault) {
			if (bundleExists) {
				/*
				undeployMessageBundle выполняет удаление нод из репозитория,
				но не помечает их как sys:temporary, поэтому они накапливаются в корзине
				надо придумать как их вычищать оттуда
				*/
				repoAdminService.undeployMessageBundle(locationBaseName);
			}
			repoAdminService.deployMessageBundle(messageLocation);
		}
	}

	@Override
	protected void onBootstrap(ApplicationEvent ae) {
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

	@Override
	protected void onShutdown(ApplicationEvent ae) {		
	}
}
