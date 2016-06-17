package ru.it.lecm.documents.beans;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author vmalygin
 */
public class DocumentMessageServiceImpl extends BaseBean implements DocumentMessageService, MessageLookup {

	public final static String DOCUMENT_MESSAGE_FOLDER_ID = "DOCUMENT_MESSAGE_FOLDER_ID";

	private MessageService messageService;
	private NamespaceService namespaceService;
	private RepositoryLocation repoMessagesLocation;

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(DOCUMENT_MESSAGE_FOLDER_ID);
	}

	@Override
	public NodeRef getDocumentMessageFolder() {
		return getServiceRootFolder();
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setRepoMessagesLocation(RepositoryLocation repoMessagesLocation) {
		this.repoMessagesLocation = repoMessagesLocation;
	}

	public void init() {
		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
		transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				initMessages();
				return null;
			}
		}, transactionService.isReadOnly(), false);
	}

	public void initMessages() {
		NodeRef messageFolder = getDocumentMessageFolder();
		String storeRef = messageFolder.getStoreRef().toString();
		String path = ISO9075.decode(nodeService.getPath(messageFolder).toPrefixString(namespaceService));
//		String storeRef = repoMessagesLocation.getStoreRef().toString();
//		String path = repoMessagesLocation.getPath();
		Set<QName> types = new HashSet<>();
		types.add(ContentModel.TYPE_CONTENT);
		List<ChildAssociationRef> refs = nodeService.getChildAssocs(messageFolder, types);
		Set<String> resourceBundleBaseNames = new HashSet<>();
		for (ChildAssociationRef ref : refs) {
			NodeRef messageResource = ref.getChildRef();
			String resourceName = (String) nodeService.getProperty(messageResource, ContentModel.PROP_NAME);
			String bundleBaseName = messageService.getBaseBundleName(resourceName);

			if(resourceBundleBaseNames.add(bundleBaseName)) {
				String repoBundlePath =storeRef + path + "/cm:" + bundleBaseName;
				messageService.registerResourceBundle(repoBundlePath);
			}
		}
	}

	@Override
	public boolean registerResourceBundle(NodeRef messageResource) {
		boolean bundleRegistered;
		NodeRef messageFolder = getDocumentMessageFolder();
		String storeRef = messageFolder.getStoreRef().toString();
		String path = ISO9075.decode(nodeService.getPath(messageFolder).toPrefixString(namespaceService));
//		String storeRef = repoMessagesLocation.getStoreRef().toString();
//		String path = repoMessagesLocation.getPath();
		String resourceName = (String) nodeService.getProperty(messageResource, ContentModel.PROP_NAME);
		String bundleBaseName = messageService.getBaseBundleName(resourceName);
		Set<String> bundles = messageService.getRegisteredBundles();
		if (!bundles.contains(bundleBaseName)) {
			String repoBundlePath =storeRef + path + "/cm:" + bundleBaseName;
			messageService.registerResourceBundle(repoBundlePath);
			bundleRegistered = true;
		} else {
			bundleRegistered = false;
		}
		return bundleRegistered;
	}

	@Override
	public boolean unregisterResourceBundle(NodeRef messageResource) {
		boolean bundleUngeristered;
		NodeRef messageFolder = getDocumentMessageFolder();
		String storeRef = messageFolder.getStoreRef().toString();
		String path = ISO9075.decode(nodeService.getPath(messageFolder).toPrefixString(namespaceService));
//		String storeRef = repoMessagesLocation.getStoreRef().toString();
//		String path = repoMessagesLocation.getPath();
		String resourceName = (String) nodeService.getProperty(messageResource, ContentModel.PROP_NAME);
		String bundleBaseName = messageService.getBaseBundleName(resourceName);
		Set<String> bundles = messageService.getRegisteredBundles();
		if (bundles.contains(bundleBaseName)) {
			String repoBundlePath =storeRef + path + "/cm:" + bundleBaseName;
			messageService.unregisterResourceBundle(repoBundlePath);
			bundleUngeristered = true;
		} else {
			bundleUngeristered = false;
		}
		return bundleUngeristered;
	}

	@Override
	public String getMessage(String messageKey) {
		return messageService.getMessage(messageKey);
	}

	@Override
	public String getMessage(String messageKey, Locale locale) {
		return messageService.getMessage(messageKey, locale);
	}

	@Override
	public String getMessage(String messageKey, Object... params) {
		return messageService.getMessage(messageKey, params);
	}

	@Override
	public String getMessage(String messageKey, Locale locale, Object... params) {
		return messageService.getMessage(messageKey, locale, params);
	}
}
