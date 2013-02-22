package ru.it.lecm.base.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 13:55
 */
public class DocumentConnectionServiceImpl extends BaseBean implements DocumentConnectionService {
	private final static Logger logger = LoggerFactory.getLogger(DocumentConnectionServiceImpl.class);

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private SearchService searchService;
	private NamespaceService namespaceService;

	private final Object lock = new Object();

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public NodeRef getConnectionsRootRef() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DOCUMENT_CONNECTIONS_ROOT_NAME);
	}

	/**
	 * Получение узла подписки, в котором хрянится информация об подписках.
	 * Если такой узел отсутствует - он создаётся автоматически (внутри /CompanyHome)
	 * @return
	 */
	public NodeRef init() {
		final String rootName = DOCUMENT_CONNECTIONS_ROOT_NAME;
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef connectionsRef;
						synchronized (lock) {
							// еще раз пытаемся получить директорию (на случай если она уже была создана другим потоком
							connectionsRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
									rootName);
							if (connectionsRef == null) {
								QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
								QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
								QName nodeTypeQName = ContentModel.TYPE_FOLDER;

								Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, rootName);
								ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
								connectionsRef = associationRef.getChildRef();
							}
						}
						return connectionsRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	public NodeRef getDefaultConnectionType(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		return getDefaultConnectionType(primaryDocumentRef, connectedDocumentRef, null);
	}

	private NodeRef getDefaultConnectionType(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef, NodeRef dictionary) {
		if (dictionary == null) {
			dictionary = getAvailableTypeDictionary(primaryDocumentRef, connectedDocumentRef);
		}
		if (dictionary != null) {
			List<AssociationRef> defaultTypeAssoc = nodeService.getTargetAssocs(dictionary, DocumentConnectionService.ASSOC_DEFAULT_CONNECTION_TYPE);
			if (defaultTypeAssoc != null && defaultTypeAssoc.size() > 0) {
				return defaultTypeAssoc.get(0).getTargetRef();
			}
		}
		return null;
	}

	public List<NodeRef> getAvailableConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		List<NodeRef> results = null;
		NodeRef dictionary = getAvailableTypeDictionary(primaryDocumentRef, connectedDocumentRef);
		if (dictionary != null) {
			results = new ArrayList<NodeRef>();
			List<AssociationRef> availableTypesAssoc = nodeService.getTargetAssocs(dictionary, DocumentConnectionService.ASSOC_AVAILABLE_CONNECTION_TYPES);
			if (availableTypesAssoc != null) {
				for (AssociationRef assocRef: availableTypesAssoc) {
					results.add(assocRef.getTargetRef());
				}
			}

			NodeRef defaultType = getDefaultConnectionType(primaryDocumentRef, connectedDocumentRef, dictionary);
			if (defaultType != null && !results.contains(defaultType)) {
				results.add(defaultType);
			}
		}
		return results;
	}

	public NodeRef getAvailableTypeDictionary(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		String primaryDocumentType = nodeService.getType(primaryDocumentRef).toPrefixString(namespaceService);
		String connectedDocumentType = nodeService.getType(connectedDocumentRef).toPrefixString(namespaceService);

		String propPrimaryDocumentType = "@" + DocumentConnectionService.PROP_PRIMARY_DOCUMENT_TYPE.toPrefixString(namespaceService).replace(":", "\\:");
		String propConnectedDocumentType = "@" + DocumentConnectionService.PROP_CONNECTED_DOCUMENT_TYPE.toPrefixString(namespaceService).replace(":", "\\:");

		String dictionaryType = DocumentConnectionService.TYPE_AVAILABLE_CONNECTION_TYPES.toPrefixString(namespaceService);

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery("TYPE:\"" + dictionaryType + "\" AND " + propPrimaryDocumentType + ":\"" +
				primaryDocumentType + "\" AND " + propConnectedDocumentType + ":\"" + connectedDocumentType + "\"");
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			if (resultSet.length() > 0) {
				return resultSet.getRow(0).getNodeRef();
			}
		} catch (LuceneQueryParserException e) {
			logger.error("Error while getting dictionary available connection types", e);
		} catch (Exception e) {
			logger.error("Error while getting dictionary available connection types", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return null;
	}

	public List<NodeRef> getAllConnectionTypes() {
		String type = DocumentConnectionService.TYPE_CONNECTION_TYPES.toPrefixString(namespaceService);

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery("TYPE:\"" + type + "\"");
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			return resultSet.getNodeRefs();
		} catch (LuceneQueryParserException e) {
			logger.error("Error while getting dictionary available connection types", e);
		} catch (Exception e) {
			logger.error("Error while getting dictionary available connection types", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return null;
	}
}
