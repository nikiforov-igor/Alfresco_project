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
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.TableTotalRowCalculator;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:47
 */
public class DocumentTableServiceImpl extends BaseBean implements DocumentTableService {
	private DocumentService documentService;
	private LecmPermissionService lecmPermissionService;
	private DictionaryService dictionaryService;
	private NamespaceService namespaceService;
	private Map<String, TableTotalRowCalculator> calculators = new HashMap<String, TableTotalRowCalculator>();

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
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
	public boolean isDocumentTableDataRow(NodeRef nodeRef) {
		QName refType = nodeService.getType(nodeRef);
		return refType != null && dictionaryService.isSubClass(refType, TYPE_TABLE_DATA_ROW);
	}

	@Override
	public boolean isDocumentTableData(NodeRef nodeRef) {
		QName refType = nodeService.getType(nodeRef);
		return refType != null && dictionaryService.isSubClass(refType, TYPE_TABLE_DATA);
	}

	@Override
	public NodeRef getDocumentByTableDataRow(NodeRef tableDataRowRef) {
		if (nodeService.exists(tableDataRowRef)) {
			NodeRef tableDataRef = nodeService.getPrimaryParent(tableDataRowRef).getParentRef();
			if (isDocumentTableData(tableDataRef)) {
				NodeRef tableDataRoot = nodeService.getPrimaryParent(tableDataRef).getParentRef();
				if (tableDataRoot != null && nodeService.getProperty(tableDataRoot, ContentModel.PROP_NAME).equals(DOCUMENT_TABLES_ROOT_NAME)) {
					NodeRef document = nodeService.getPrimaryParent(tableDataRoot).getParentRef();
					if (document != null && documentService.isDocument(document)) {
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

//    @Override
//    public void setIndexTableRow(NodeRef documentRef, NodeRef tableDataRef, QName tableDataAssocType) {
//        QName tableDataType = nodeService.getType(tableDataRef);
//        List<NodeRef> tableRowList = findNodesByAssociationRef(documentRef, tableDataAssocType, tableDataType, ASSOCIATION_TYPE.TARGET);
//        if (tableRowList != null) {
//            int maxIndex = 0;
//            int index;
//            String indexStr;
//            for (NodeRef nodeRef : tableRowList) {
//                indexStr = (String)nodeService.getProperty(nodeRef, DocumentTableService.PROP_INDEX_TABLE_ROW);
//                if (indexStr != null && !indexStr.equals("")){
//                    index = Integer.parseInt(indexStr);
//                    if (maxIndex < index){
//                        maxIndex = index;
//                    }
//                }
//            }
//            nodeService.setProperty(tableDataRef,DocumentTableService.PROP_INDEX_TABLE_ROW, maxIndex+1);
//        }
//    }

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
			} else if (createIfNotExist) {
				NodeRef totalRow = createNode(getRootFolder(document), assocDefinition.getTargetClass().getName(), null, null);
				nodeService.createAssociation(document, totalRow, tableDataTotalAssocType);
				recalculateTotalRow(document, totalRow, tableDataType, tableDataAssocType, null);
				List<NodeRef> result = new ArrayList<NodeRef>();
				result.add(totalRow);
				return result;
			}
		}
		return null;
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
				Map<NodeRef, Map<QName, Serializable>> tableRowsProperties = new HashMap<NodeRef, Map<QName, Serializable>>();

				for (QName tableDataProperty: properties) {
					String tableDataPropertyName = tableDataProperty.toPrefixString(namespaceService);

					Map<QName, TableTotalRowCalculator> calculators = getAvailableCalculators(tableDataPropertyName, totalProperties);
					if (calculators != null) {
						List<Serializable> data = new ArrayList<Serializable>();
						for (NodeRef tableRow: tableRows) {
							Map<QName, Serializable> propertiesValue;
							if (tableRowsProperties.containsKey(tableRow)) {
								propertiesValue = tableRowsProperties.get(tableRow);
							} else {
								propertiesValue = nodeService.getProperties(tableRow);
								tableRowsProperties.put(tableRow, propertiesValue);
							}
							if (propertiesValue != null) {
								Serializable value = propertiesValue.get(tableDataProperty);
								if (value != null) {
									data.add(value);
								}
							}
						}

						for (Map.Entry<QName, TableTotalRowCalculator> entry: calculators.entrySet()) {
							Serializable result = entry.getValue().calculate(data);
							nodeService.setProperty(row, entry.getKey(), result);
						}
					}
				}
			}
		}
	}

	private Map<QName, TableTotalRowCalculator> getAvailableCalculators(String tableDataPropertyName, Set<QName> totalProperties) {
		Map<QName, TableTotalRowCalculator> result = new HashMap<QName, TableTotalRowCalculator>();
		for (QName totalRowProperty: totalProperties) {
			String totalRowPropertyName = totalRowProperty.toPrefixString(namespaceService);

			if (totalRowPropertyName.startsWith(tableDataPropertyName)) {
				String postfix = totalRowPropertyName.substring(tableDataPropertyName.length());

				TableTotalRowCalculator calculator = calculators.get(postfix);
				if (calculator != null) {
					result.put(totalRowProperty, calculator);
				}
			}
		}

		return  result;
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

	public List<NodeRef> getTableDataRows(NodeRef document, QName tableDataAssocType) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		if (tableDataAssocType != null) {
			List<AssociationRef> tableRowsAssoc = nodeService.getTargetAssocs(document, tableDataAssocType);
			if (tableRowsAssoc != null && tableRowsAssoc.size() > 0) {
				result = new ArrayList<NodeRef>(tableRowsAssoc.size());
				for (AssociationRef assoc: tableRowsAssoc) {
					result.add(assoc.getTargetRef());
				}
			}
		}
		return result;
	}

    @Override
    public List<NodeRef> getTableDataRows(NodeRef document, QName tableDataAssocType, int beginIndex) {
        List<NodeRef> tableRows = getTableDataRows(document, tableDataAssocType);
        List<NodeRef> result = new ArrayList<NodeRef>();
        String indexStr;
        int index;
        if (tableRows != null){
            for(NodeRef tableRow : tableRows){
                indexStr = (String)nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                if (indexStr != null && !indexStr.equals("")){
                    index = Integer.parseInt(indexStr);
                    if (index >= beginIndex){
                        result.add(tableRow);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<NodeRef> getTableDataRows(NodeRef document, QName tableDataAssocType, int beginIndex, int endIndex) {
        List<NodeRef> tableRows = getTableDataRows(document, tableDataAssocType, beginIndex);
        List<NodeRef> result = new ArrayList<NodeRef>();
        String indexStr;
        int index;
        if (tableRows != null) {
            for(NodeRef tableRow : tableRows){
                indexStr = (String)nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                if (indexStr != null && !indexStr.equals("")){
                    index = Integer.parseInt(indexStr);
                    if (index <= endIndex){
                        result.add(tableRow);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean isMoveTableRowUp(final NodeRef tableRow, final String assocTypeStr) {

        AuthenticationUtil.RunAsWork<Boolean> moveUp = new AuthenticationUtil.RunAsWork<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
                    @Override
                    public Boolean execute() throws Throwable {
                        String indexStr;
                        int endIndex, index;
                        List<NodeRef> tableRows;
                        indexStr = (String) nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                        if (indexStr != null && !indexStr.equals("")) {
                            endIndex = Integer.parseInt(indexStr);
                            if (endIndex != 1) {
                                NodeRef document = getDocumentByTableDataRow(tableRow);
                                QName assocType = QName.createQName(assocTypeStr, namespaceService);
                                tableRows = getTableDataRows(document, assocType, endIndex - 1, endIndex);
                                if (tableRows.size() == 2) {
                                    for (NodeRef row : tableRows) {
                                        indexStr = (String) nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
                                        if (indexStr != null && !indexStr.equals("")) {
                                            index = Integer.parseInt(indexStr);
                                            if (index == endIndex) {
                                                nodeService.setProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW, (endIndex - 1));
                                            } else {
                                                nodeService.setProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW, endIndex);
                                            }
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(moveUp);

    }

    @Override
    public boolean isMoveTableRowDown(final NodeRef tableRow, final String assocTypeStr) {

        AuthenticationUtil.RunAsWork<Boolean> moveDown = new AuthenticationUtil.RunAsWork<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
                    @Override
                    public Boolean execute() throws Throwable {
                        String indexStr;
                        int startIndex, index;
                        List<NodeRef> tableRows;

                        indexStr = (String) nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                        if (indexStr != null && !indexStr.equals("")) {
                            startIndex = Integer.parseInt(indexStr);
                            NodeRef document = getDocumentByTableDataRow(tableRow);
                            QName assocType = QName.createQName(assocTypeStr, namespaceService);
                            tableRows = getTableDataRows(document, assocType, startIndex, startIndex + 1);
                            if (tableRows.size() == 2) {
                                for (NodeRef row : tableRows) {
                                    indexStr = (String) nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
                                    if (indexStr != null && !indexStr.equals("")) {
                                        index = Integer.parseInt(indexStr);
                                        if (index == startIndex) {
                                            nodeService.setProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW, (startIndex + 1));
                                        } else {
                                            nodeService.setProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW, startIndex);
                                        }
                                    }
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(moveDown);

    }


    @Override
	public void addCalculator(String postfix, TableTotalRowCalculator calculator) {
		if (!calculators.containsKey(postfix)) {
			calculators.put(postfix, calculator);
		}
	}
}
