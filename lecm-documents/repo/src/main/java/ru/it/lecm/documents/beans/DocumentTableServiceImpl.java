package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:47
 */
public class DocumentTableServiceImpl extends BaseBean implements DocumentTableService {
	private final static Logger logger = LoggerFactory.getLogger(DocumentTableServiceImpl.class);

	private LecmPermissionService lecmPermissionService;
	private DictionaryService dictionaryService;
	private NamespaceService namespaceService;
	private Map<String, TableTotalRowCalculator> calculators;

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setCalculators(Map<String, TableTotalRowCalculator> calculators) {
		this.calculators = calculators;
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public NodeRef getRootFolder(final NodeRef documentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);

		final String attachmentsRootName = DOCUMENT_TABLES_ROOT_NAME;

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
						}
						return attachmentsRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public boolean isDocumentTableData(NodeRef nodeRef) {
		QName refType = nodeService.getType(nodeRef);
		return refType != null && dictionaryService.isSubClass(refType, TYPE_TABLE_DATA_ROW);
	}

	@Override
	public NodeRef getDocumentByTableData(NodeRef tableDataRef) {
		if (nodeService.exists(tableDataRef)) {
			NodeRef tableDataRoot = nodeService.getPrimaryParent(tableDataRef).getParentRef();
			if (tableDataRoot != null && nodeService.getProperty(tableDataRoot, ContentModel.PROP_NAME).equals(DOCUMENT_TABLES_ROOT_NAME)) {
				NodeRef document = nodeService.getPrimaryParent(tableDataRoot).getParentRef();
				if (document != null) {
					QName testType = nodeService.getType(document);
					Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
					if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
						return document;
					}
				}
			}
		}
		return null;
	}

	public AssociationRef getDocumentAssocByTableData(NodeRef tableDataRef) {
		if (nodeService.exists(tableDataRef)) {
			List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(tableDataRef, RegexQNamePattern.MATCH_ALL);
			if (sourceAssocs != null && sourceAssocs.size() > 0) {
				for (AssociationRef assoc: sourceAssocs) {
					NodeRef document = assoc.getSourceRef();
					if (document != null) {
						QName testType = nodeService.getType(document);
						Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
						if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
							return assoc;
						}
					}
				}
			}
		}
		return null;
	}

    @Override
    public void setIndexTableRow(NodeRef documentRef, NodeRef tableDataRef, QName tableDataAssocType) {
        QName tableDataType = nodeService.getType(tableDataRef);
        List<NodeRef> tableRowList = findNodesByAssociationRef(documentRef, tableDataAssocType, tableDataType, ASSOCIATION_TYPE.TARGET);
        if (tableRowList != null) {
            int maxIndex = 0;
            int index;
            String indexStr = "";
            for (NodeRef nodeRef : tableRowList) {
                indexStr = (String)nodeService.getProperty(nodeRef, DocumentTableService.PROP_INDEX_TABLE_ROW);
                if (indexStr != null && indexStr != ""){
                    index = Integer.parseInt(indexStr);
                    if (maxIndex < index){
                        maxIndex = index;
                    }
                }
            }
            nodeService.setProperty(tableDataRef,DocumentTableService.PROP_INDEX_TABLE_ROW, maxIndex+1);
        }
    }

    @Override
	public List<NodeRef> getTableDataTotalRows(NodeRef tableDataRef) {
		AssociationRef documentAssoc = getDocumentAssocByTableData(tableDataRef);
		if (documentAssoc != null) {
			NodeRef document = documentAssoc.getSourceRef();

			return getTableDataTotalRows(document, nodeService.getType(tableDataRef), documentAssoc.getTypeQName(), false);
		}

		return null;
	}

	@Override
	public List<NodeRef> getTableDataTotalRows(NodeRef document, QName tableDataType, QName tableDataAssocType, boolean createIfNotExist) {
		String tableDataAssocQName = tableDataAssocType.toPrefixString(namespaceService);
		QName tableDataTotalAssocType = QName.createQName(tableDataAssocQName + DOCUMENT_TABLE_TOTAL_ASSOC_POSTFIX, namespaceService);

		AssociationDefinition assocDefinition = dictionaryService.getAssociation(tableDataTotalAssocType);
		if (assocDefinition != null) {
			List<AssociationRef> totalRowAssocs = nodeService.getTargetAssocs(document, tableDataTotalAssocType);
			if (totalRowAssocs != null && totalRowAssocs.size() > 0) {
				List<NodeRef> result = new ArrayList<NodeRef>();
				for (AssociationRef assoc: totalRowAssocs) {
					result.add(assoc.getTargetRef());
				}
				return result;
			} else {
				NodeRef totalRow = createNode(getRootFolder(document), assocDefinition.getTargetClass().getName(), null, null);
				nodeService.createAssociation(document, totalRow, tableDataTotalAssocType);
				recalculateTotalRow(document, totalRow, tableDataType, tableDataAssocType, null);
				List<NodeRef> result = new ArrayList<NodeRef>();
				result.add(totalRow);
				return result;
			}
		} else {
			return null;
		}
	}

	@Override
	public void recalculateTotalRows(NodeRef document, List<NodeRef> rows, QName tableDataType, QName tableDataAssocType, Set<QName> properties) {
		if (rows != null) {
			for (NodeRef row: rows) {
				recalculateTotalRow(document, row, tableDataType, tableDataAssocType, properties);
			}
		}
	}

	@Override
	public void recalculateTotalRow(NodeRef document, NodeRef row, QName tableDataType, QName tableDataAssocType, Set<QName> properties) {
		if (row != null) {
			if (properties == null) {
				properties = getAllTypeProperties(tableDataType);
			}
			Set<QName> totalProperties = getAllTypeProperties(nodeService.getType(row));

			if (totalProperties != null && properties != null) {
				List<NodeRef> tableRows = getTableDataRows(document, tableDataAssocType);
				if (tableRows != null) {
					for (QName tableDataProperty: properties) {
						String tableDataPropertyName = tableDataProperty.toPrefixString(namespaceService);
						for (QName totalRowProperty: totalProperties) {
							String totalRowPropertyName = totalRowProperty.toPrefixString(namespaceService);

							if (totalRowPropertyName.startsWith(tableDataPropertyName)) {
								String postfix = totalRowPropertyName.substring(tableDataPropertyName.length());
								runCalculator(row, tableRows, tableDataProperty, totalRowProperty, postfix);
							}
						}
					}
				}
			}
		}
	}

	private void runCalculator(NodeRef row, List<NodeRef> tableRows, QName tableDataProperty, QName totalRowProperty, String postfix) {
		TableTotalRowCalculator calculator = calculators.get(postfix);
		if (calculator != null) {
			List<Serializable> data = new ArrayList<Serializable>();
			for (NodeRef tableRow: tableRows) {
				data.add(nodeService.getProperty(tableRow, tableDataProperty));
			}

			Serializable result = calculator.calculate(data);
			nodeService.setProperty(row, totalRowProperty, result);
		}
	}

	private Set<QName> getAllTypeProperties(QName type) {
		if (type != null) {
			TypeDefinition typeDefinition = dictionaryService.getType(type);
			if (typeDefinition != null) {
				Map<QName, PropertyDefinition> allProperties = typeDefinition.getProperties();
				if (allProperties != null) {
					return allProperties.keySet();
				}
			}
		}
		return null;
	}

	private List<NodeRef> getTableDataRows(NodeRef document, QName tableDataAssocType) {
		if (tableDataAssocType != null) {
			List<AssociationRef> tableRowsAssoc = nodeService.getTargetAssocs(document, tableDataAssocType);
			if (tableRowsAssoc != null && tableRowsAssoc.size() > 0) {
				List<NodeRef> result = new ArrayList<NodeRef>(tableRowsAssoc.size());
				for (AssociationRef assoc: tableRowsAssoc) {
					result.add(assoc.getTargetRef());
				}
				return result;
			}
		}
		return null;
	}
}
