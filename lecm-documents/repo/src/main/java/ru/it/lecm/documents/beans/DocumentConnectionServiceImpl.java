package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
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

	private final Object lock = new Object();

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public NodeRef getRootFolder(final NodeRef documentRef) {
		final String attachmentsRootName = DOCUMENT_CONNECTIONS_ROOT_NAME;

		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef attachmentsRef;
						synchronized (lock) {
							attachmentsRef = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, attachmentsRootName);
							if (attachmentsRef == null) {
								QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
								QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, attachmentsRootName);
								QName nodeTypeQName = ContentModel.TYPE_FOLDER;

								Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
								properties.put(ContentModel.PROP_NAME, attachmentsRootName);
								ChildAssociationRef associationRef = nodeService.createNode(documentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
								attachmentsRef = associationRef.getChildRef();
							}
						}
						return attachmentsRef;
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
			List<AssociationRef> defaultTypeAssoc = nodeService.getTargetAssocs(dictionary, ASSOC_DEFAULT_CONNECTION_TYPE);
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
			List<AssociationRef> availableTypesAssoc = nodeService.getTargetAssocs(dictionary, ASSOC_AVAILABLE_CONNECTION_TYPES);
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

    public List<NodeRef> getExistsConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef) {
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
                for (NodeRef ref: connectionRefs) {
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

    public List<NodeRef> getConnections(NodeRef primaryDocumentRef, int skipCount, int maxItems) {
        List<NodeRef> results = new ArrayList<NodeRef>();

        String connectionType = TYPE_CONNECTION.toPrefixString(namespaceService);

        String propPrimaryDocumentRef = "@" + PROP_PRIMARY_DOCUMENT_REF.toPrefixString(namespaceService).replace(":", "\\:");

        SearchParameters parameters = new SearchParameters();
        parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        parameters.addSort("@" + ContentModel.PROP_MODIFIED, false);
        parameters.setQuery("TYPE:\"" + connectionType + "\" AND " + propPrimaryDocumentRef + ":\"" +
                primaryDocumentRef + "\"");
        parameters.setSkipCount(skipCount);
        parameters.setMaxItems(maxItems);
        ResultSet resultSet = null;
        try {
            resultSet = searchService.query(parameters);
            if (resultSet != null) {
                results = resultSet.getNodeRefs();
            }
        } catch (LuceneQueryParserException e) {
            logger.error("Error while getting connections", e);
        } catch (Exception e) {
            logger.error("Error while getting connections", e);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return results;
    }
}
