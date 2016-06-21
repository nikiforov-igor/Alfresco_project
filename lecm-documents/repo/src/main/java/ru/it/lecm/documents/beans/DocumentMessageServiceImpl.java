package ru.it.lecm.documents.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.alfresco.util.PropertyMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author vmalygin
 */
public class DocumentMessageServiceImpl extends BaseBean implements DocumentMessageService, MessageLookup {

	public final static String DOCUMENT_MESSAGE_FOLDER_ID = "DOCUMENT_MESSAGE_FOLDER_ID";

	private MessageService messageService;
	private NamespaceService namespaceService;
	private ContentService contentService;
//	private RepositoryLocation repoMessagesLocation;

	List<Locale> availableLocales;

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

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setLocales(String availableLocales) {
		this.availableLocales = new ArrayList<>();
		if (availableLocales != null) {
			String[] locales = StringUtils.split(availableLocales, ',');
			for (String locale : locales) {
				this.availableLocales.add(LocaleUtils.toLocale(locale));
			}
		}
	}

	@Override
	public List<Locale> getAvailableLocales() {
		return this.availableLocales;
	}

//	public void setRepoMessagesLocation(RepositoryLocation repoMessagesLocation) {
//		this.repoMessagesLocation = repoMessagesLocation;
//	}

/*
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
*/

	@Override
	public void loadMessagesFromLocation(String messageLocation, boolean useDefault) {
		//два прохода: в зависимости от useDefault мы или грузим или не грузим ресурсный файл в репу
		//находим в репе ресурсный файл, и регистрируем его в пучке
		NodeRef messageFolder = getDocumentMessageFolder();
		ClassPathResource resource = new ClassPathResource(messageLocation);
		String filename = resource.getFilename();
		NodeRef messageResource = nodeService.getChildByName(messageFolder, ContentModel.ASSOC_CONTAINS, filename);
		if (messageResource == null || useDefault) {
			boolean updateContent = false;
			if (messageResource == null) {
				//создать ноду
				QName assocName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, filename);
				PropertyMap props = new PropertyMap();
				props.put(ContentModel.PROP_NAME, filename);
				messageResource = nodeService.createNode(messageFolder, ContentModel.ASSOC_CONTAINS, assocName, ContentModel.TYPE_CONTENT, props).getChildRef();
				updateContent = true;
			} else {
				InputStream classpathInputStream = null;
				InputStream repoInputStream = null;
				//сравнить контент
				try {
					classpathInputStream = resource.getInputStream();
					ContentReader reader = contentService.getReader(messageResource, ContentModel.PROP_CONTENT);
					repoInputStream = reader.getContentInputStream();
					updateContent = !IOUtils.contentEquals(classpathInputStream, repoInputStream);
				} catch (IOException ex) {
				} finally {
					IOUtils.closeQuietly(classpathInputStream);
					IOUtils.closeQuietly(repoInputStream);
				}
			}
			if (updateContent) {
				//залить новую версию контента
				InputStream classpathInputStream = null;
				try {
					classpathInputStream = resource.getInputStream();
					if (!nodeService.hasAspect(messageResource, ContentModel.ASPECT_VERSIONABLE)) {
						nodeService.addAspect(messageResource, ContentModel.ASPECT_VERSIONABLE, null);
					}
					ContentWriter contentWriter = contentService.getWriter(messageResource, ContentModel.PROP_CONTENT, true);
					contentWriter.setEncoding("ISO-8859-1");
					contentWriter.setMimetype("text/plain");
					contentWriter.putContent(classpathInputStream);
				} catch (IOException ex) {
				} finally {
					IOUtils.closeQuietly(classpathInputStream);
				}
			}
		}
		registerResourceBundle(messageResource);
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
