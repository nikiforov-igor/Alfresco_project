package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 13:55
 */
public class DocumentConnectionServiceImpl extends BaseBean implements DocumentConnectionService {
	private final static Logger logger = LoggerFactory.getLogger(DocumentConnectionServiceImpl.class);

	private SearchService searchService;
	private NamespaceService namespaceService;
	private LecmPermissionService lecmPermissionService;
	private DictionaryBean dictionaryService;

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public NodeRef getRootFolder(final NodeRef documentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, documentRef);

		final String attachmentsRootName = DOCUMENT_CONNECTIONS_ROOT_NAME;

		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef attachmentsRef = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, attachmentsRootName);
                        if (attachmentsRef == null) {
                            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, attachmentsRootName);
                            QName nodeTypeQName = ContentModel.TYPE_FOLDER;

                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                            properties.put(ContentModel.PROP_NAME, attachmentsRootName);
                            ChildAssociationRef associationRef = nodeService.createNode(documentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
                            attachmentsRef = associationRef.getChildRef();
                            hideNode(attachmentsRef, true);
                        }
						return attachmentsRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public NodeRef getDefaultConnectionType(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		return getDefaultConnectionType(primaryDocumentRef, nodeService.getType(connectedDocumentRef));
	}
	
	@Override
	public NodeRef getDefaultConnectionType(NodeRef primaryDocumentRef, QName connectedDocumentType) {
		return getDefaultConnectionType(primaryDocumentRef, connectedDocumentType, null);
	}

	private NodeRef getDefaultConnectionType(NodeRef primaryDocumentRef, QName connectedDocumentType, NodeRef dictionary) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, primaryDocumentRef);

		if (dictionary == null) {
			dictionary = getAvailableTypeDictionary(primaryDocumentRef, connectedDocumentType);
		}
		if (dictionary != null) {
			List<AssociationRef> defaultTypeAssoc = nodeService.getTargetAssocs(dictionary, ASSOC_DEFAULT_CONNECTION_TYPE);
			if (defaultTypeAssoc != null && defaultTypeAssoc.size() > 0) {
				return defaultTypeAssoc.get(0).getTargetRef();
			}
		}
		return null;
	}

	@Override
	public List<NodeRef> getRecommendedConnectionTypes(NodeRef primaryDocumentRef, QName connectedDocumentType) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, primaryDocumentRef);

		List<NodeRef> results = null;
		NodeRef dictionary = getAvailableTypeDictionary(primaryDocumentRef, connectedDocumentType);
		if (dictionary != null) {
			results = new ArrayList<NodeRef>();
			List<AssociationRef> recommendedTypesAssoc = nodeService.getTargetAssocs(dictionary, ASSOC_RECOMMENDED_CONNECTION_TYPES);
			if (recommendedTypesAssoc != null) {
				for (AssociationRef assocRef : recommendedTypesAssoc) {
					results.add(assocRef.getTargetRef());
				}
			}

			NodeRef defaultType = getDefaultConnectionType(primaryDocumentRef, connectedDocumentType, dictionary);
			if (defaultType != null && !results.contains(defaultType)) {
				results.add(defaultType);
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getRecommendedConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		return getRecommendedConnectionTypes(primaryDocumentRef, nodeService.getType(connectedDocumentRef));
	}


	@Override
	public List<NodeRef> getNotAvailableConnectionTypes(NodeRef primaryDocumentRef, QName connectedDocumentType) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, primaryDocumentRef);

		List<NodeRef> results = null;
		NodeRef dictionary = getAvailableTypeDictionary(primaryDocumentRef, connectedDocumentType);
		if (dictionary != null) {
			results = new ArrayList<NodeRef>();
			List<AssociationRef> notAvailableTypesAssoc = nodeService.getTargetAssocs(dictionary, ASSOC_NOT_AVAILABLE_CONNECTION_TYPES);
			if (notAvailableTypesAssoc != null) {
				for (AssociationRef assocRef : notAvailableTypesAssoc) {
					results.add(assocRef.getTargetRef());
				}
			}

			NodeRef defaultType = getDefaultConnectionType(primaryDocumentRef, connectedDocumentType, dictionary);
			if (defaultType != null && !results.contains(defaultType)) {
				results.add(defaultType);
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getNotAvailableConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		return getNotAvailableConnectionTypes(primaryDocumentRef, nodeService.getType(connectedDocumentRef));
	}


	@Override
	public List<NodeRef> getAvailableConnectionTypes(NodeRef primaryDocumentRef, QName connectedDocumentType) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, primaryDocumentRef);
		List<NodeRef> result = getAllConnectionTypes();
		List<NodeRef> notAvailableTypes = getNotAvailableConnectionTypes(primaryDocumentRef, connectedDocumentType);
		if (notAvailableTypes != null) {
			result.removeAll(notAvailableTypes);
		}
		return result;
	}

	@Override
	public List<NodeRef> getAvailableConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		return getAvailableConnectionTypes(primaryDocumentRef, nodeService.getType(connectedDocumentRef));
	}

	public NodeRef getAvailableTypeDictionary(NodeRef primaryDocumentRef, QName connectedDocumentTypeQname) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, primaryDocumentRef);

		String primaryDocumentType = nodeService.getType(primaryDocumentRef).toPrefixString(namespaceService);
		String connectedDocumentType = connectedDocumentTypeQname.toPrefixString(namespaceService);

		String propPrimaryDocumentType = "@" + PROP_PRIMARY_DOCUMENT_TYPE.toPrefixString(namespaceService).replace(":", "\\:");
		String propConnectedDocumentType = "@" + PROP_CONNECTED_DOCUMENT_TYPE.toPrefixString(namespaceService).replace(":", "\\:");

		String dictionaryType = TYPE_AVAILABLE_CONNECTION_TYPES.toPrefixString(namespaceService);

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

	@Override
	public List<NodeRef> getExistsConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, primaryDocumentRef);

		List<NodeRef> results = new ArrayList<NodeRef>();

		String connectionType = TYPE_CONNECTION.toPrefixString(namespaceService);

		String propPrimaryDocumentRef = "@" + PROP_PRIMARY_DOCUMENT_REF.toPrefixString(namespaceService).replace(":", "\\:");
		String propConnectedDocumentRef = "@" + PROP_CONNECTED_DOCUMENT_REF.toPrefixString(namespaceService).replace(":", "\\:");

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery("TYPE:\"" + connectionType + "\" AND " + propPrimaryDocumentRef + ":\"" +
				primaryDocumentRef + "\" AND " + propConnectedDocumentRef + ":\"" + connectedDocumentRef + "\"");
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			if (resultSet != null) {
				List<NodeRef> connectionRefs = resultSet.getNodeRefs();
				for (NodeRef ref : connectionRefs) {
					List<AssociationRef> typeAssoc = nodeService.getTargetAssocs(ref, ASSOC_CONNECTION_TYPE);
					if (typeAssoc != null && typeAssoc.size() == 1) {
						NodeRef typeRef = typeAssoc.get(0).getTargetRef();
						if (typeRef != null && !results.contains(typeRef)) {
							results.add(typeRef);
						}
					}
				}
			}
		} catch (LuceneQueryParserException e) {
			logger.error("Error while getting exist connection types", e);
		} catch (Exception e) {
			logger.error("Error while getting exist connection types", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getAllConnectionTypes() {
		String type = TYPE_CONNECTION_TYPE.toPrefixString(namespaceService);

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery("TYPE:\"" + type + "\"");
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			return resultSet.getNodeRefs();
		} catch (LuceneQueryParserException e) {
			logger.error("Error while getting connection types", e);
		} catch (Exception e) {
			logger.error("Error while getting connection types", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return null;
	}

	@Override
	public List<NodeRef> getConnections(NodeRef documentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, documentRef);

		List<NodeRef> results = new ArrayList<NodeRef>();

		List<AssociationRef> connections = nodeService.getSourceAssocs(documentRef, ASSOC_PRIMARY_DOCUMENT);
		if (connections != null) {
			for (AssociationRef assocRef: connections) {
				NodeRef connectionRef = assocRef.getSourceRef();

				if (!isArchive(connectionRef) && this.lecmPermissionService.hasReadAccess(connectionRef)) {
					results.add(connectionRef);
				}
			}
		}

		return results;
	}

	@Override
	public List<NodeRef> getConnectionsWithDocument(NodeRef documentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, documentRef);

		List<NodeRef> results = new ArrayList<NodeRef>();

		List<AssociationRef> connections = nodeService.getSourceAssocs(documentRef, ASSOC_CONNECTED_DOCUMENT);
		if (connections != null) {
		 	for (AssociationRef assocRef: connections) {
				 NodeRef connectionRef = assocRef.getSourceRef();

				 if (!isArchive(connectionRef) && this.lecmPermissionService.hasReadAccess(connectionRef)) {
					 results.add(connectionRef);
				 }
			 }
		}

		return results;
	}

	@Override
	public NodeRef createConnection(NodeRef primaryDocumentNodeRef, NodeRef connectedDocumentNodeRef, NodeRef typeNodeRef, boolean isSystem) {
        return createConnection(primaryDocumentNodeRef, connectedDocumentNodeRef, typeNodeRef, isSystem, false);
    }

	@Override
	public NodeRef createConnection(NodeRef primaryDocumentNodeRef, NodeRef connectedDocumentNodeRef, NodeRef typeNodeRef, boolean isSystem, boolean doNotCheckPermission) {
        // ALF-1583
        // При добавлении поручения через блок "Задачи" появляется сообщение "Ваши изменения не удалось сохранить"
        // В транзакцию добавляется переменная DocumentConnectionService.DO_NOT_CHECK_PERMISSION_CREATE_DOCUMENT_LINKS,
        // позволяющая отключить прооверку прав на создание связи к документу.
        // Переменная устанавливается в методе ru.it.lecm.documents.beans.DocumentConnectionServiceImpl.createConnection()
        // Проверяется в ru.it.lecm.documents.policy.DocumentConnectionPolicy.beforeCreateNode()
        AlfrescoTransactionSupport.bindResource(DO_NOT_CHECK_PERMISSION_CREATE_DOCUMENT_LINKS, doNotCheckPermission);

        if (!doNotCheckPermission) {
		    this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_CREATE, primaryDocumentNodeRef);
        }

		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		QName assocQName = ContentModel.ASSOC_CONTAINS;

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(PROP_IS_SYSTEM, isSystem);

		NodeRef connectionsRoot = getRootFolder(primaryDocumentNodeRef);
		ChildAssociationRef associationRef = nodeService.createNode(connectionsRoot, assocTypeQName, assocQName, TYPE_CONNECTION, properties);
		NodeRef connectionNodeRef = associationRef.getChildRef();

		nodeService.createAssociation(connectionNodeRef, primaryDocumentNodeRef, ASSOC_PRIMARY_DOCUMENT);
		nodeService.createAssociation(connectionNodeRef, connectedDocumentNodeRef, ASSOC_CONNECTED_DOCUMENT);
		nodeService.createAssociation(connectionNodeRef, typeNodeRef, ASSOC_CONNECTION_TYPE);

        hideNode(connectionNodeRef, true);

		return connectionNodeRef;
	}

	@Override
	public NodeRef createConnection(NodeRef primaryDocumentNodeRef, NodeRef connectedDocumentNodeRef, String typeDictionaryElementCode, boolean isSystem) {
		NodeRef connectionType = dictionaryService.getDictionaryValueByParam(
					DocumentConnectionService.DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME,
					DocumentConnectionService.PROP_CONNECTION_TYPE_CODE,
					typeDictionaryElementCode);
		if (connectionType != null) {
			return createConnection(primaryDocumentNodeRef, connectedDocumentNodeRef, connectionType, isSystem);
		} else {
			return null;
		}
	}

	@Override
	public void deleteConnection(NodeRef nodeRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_DELETE, nodeRef);

		nodeService.deleteNode(nodeRef);
	}

	@Override
	public boolean isConnection(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_CONNECTION);
		return isProperType(ref, types);
	}

	@Override
	public boolean isConnectionType(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_CONNECTION_TYPE);
		return isProperType(ref, types);
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public List<NodeRef> getConnectedDocuments(NodeRef documentRef, String connectionTypeCode, QName connectedDocumentType) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_VIEW, documentRef);

		List<NodeRef> results = new ArrayList<NodeRef>();

		List<AssociationRef> connections = nodeService.getSourceAssocs(documentRef, ASSOC_PRIMARY_DOCUMENT);
		if (connections != null) {
			NodeRef connectionType = dictionaryService.getDictionaryValueByParam(
					DocumentConnectionService.DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME,
					DocumentConnectionService.PROP_CONNECTION_TYPE_CODE,
					connectionTypeCode);

			for (AssociationRef assocRef: connections) {
				NodeRef connectionRef = assocRef.getSourceRef();

				if (!isArchive(connectionRef) && this.lecmPermissionService.hasReadAccess(connectionRef)) {
					List<AssociationRef> connectionTypeAssoc = nodeService.getTargetAssocs(connectionRef, ASSOC_CONNECTION_TYPE);
					if (connectionType != null && connectionTypeAssoc != null && connectionTypeAssoc.size() == 1
							&& connectionTypeAssoc.get(0).getTargetRef().equals(connectionType)) {
						List<AssociationRef> connectedDocumentAssoc = nodeService.getTargetAssocs(connectionRef, ASSOC_CONNECTED_DOCUMENT);
						if (connectedDocumentAssoc != null && connectedDocumentAssoc.size() == 1) {
							NodeRef connectedDocument = connectedDocumentAssoc.get(0).getTargetRef();
							if (!isArchive(connectedDocument) && this.lecmPermissionService.hasReadAccess(connectedDocument)
									&& nodeService.getType(connectedDocument).equals(connectedDocumentType)) {
								results.add(connectedDocument);
							}
						}
					}
				}
			}
		}

		return results;
	}
}
