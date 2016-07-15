package ru.it.lecm.documents.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.alfresco.model.ContentModel;
import static org.alfresco.repo.admin.RepoAdminServiceImpl.CRITERIA_ALL;
import static org.alfresco.repo.admin.RepoAdminServiceImpl.defaultSubtypeOfContent;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author vmalygin
 */
public class DocumentMessageServiceImpl extends BaseBean implements DocumentMessageService {

	private final static Locale[] DEFAULT_LOCALES = { LocaleUtils.toLocale("ru"), LocaleUtils.toLocale("ru_RU") };
	public final static String DOCUMENT_MESSAGE_FOLDER_ID = "DOCUMENT_MESSAGE_FOLDER_ID";

	private final static Logger logger = LoggerFactory.getLogger(DocumentMessageServiceImpl.class);

	private MessageService messageService;
	private NamespaceService namespaceService;
	private RepositoryLocation repoMessagesLocation;
	private SearchService searchService;

	List<Locale> availableLocales = new ArrayList<>();
	List<Locale> fallbackLocales = Arrays.asList(DEFAULT_LOCALES);

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

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	private List<Locale> toLocales(String localesString) {
		List<Locale> locales = new ArrayList<>();
		if (localesString != null) {
			for (String locale : StringUtils.split(localesString, ',')) {
				try {
					locales.add(LocaleUtils.toLocale(locale));
				} catch (IllegalArgumentException ex) {
					logger.error(ex.getMessage());
					if (logger.isTraceEnabled()) {
						logger.trace(ex.getMessage(), ex);
					}
				}
			}
		}
		return locales;
	}

	public void setLocales(String availableLocales) {
		List<Locale> locales = toLocales(availableLocales);
		if (locales.size() > 0) {
			this.availableLocales = locales;
		}
	}

	public void setFallback(String fallbackLocales) {
		List<Locale> locales = toLocales(fallbackLocales);
		if (locales.size() > 0) {
			this.fallbackLocales = locales;
		}
	}

	@Override
	public List<Locale> getAvailableLocales() {
		return this.availableLocales;
	}

	@Override
	public List<Locale> getFallbackLocales() {
		return this.fallbackLocales;
	}

	public void setRepoMessagesLocation(RepositoryLocation repoMessagesLocation) {
		this.repoMessagesLocation = repoMessagesLocation;
	}

	public void init() {
		StoreRef storeRef = repoMessagesLocation.getStoreRef();
		NodeRef rootNode = nodeService.getRootNode(storeRef);
		String path = repoMessagesLocation.getPath();
		List<NodeRef> nodeRefs = searchService.selectNodes(rootNode, path + CRITERIA_ALL + "[" + defaultSubtypeOfContent + "]", null, namespaceService, false);
		Set<String> resourceBundleBaseNames = new HashSet<>();
		for (NodeRef nodeRef : nodeRefs) {
			String resourceName = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			String bundleBaseName = messageService.getBaseBundleName(resourceName);

			if(resourceBundleBaseNames.add(bundleBaseName)) {
				String repoBundlePath =storeRef.toString() + path + "/cm:" + bundleBaseName;
				messageService.registerResourceBundle(repoBundlePath);
			}
		}
	}
}
