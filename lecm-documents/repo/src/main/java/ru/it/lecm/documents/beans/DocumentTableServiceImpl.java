package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.TableTotalRowCalculator;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin Date: 18.10.13 Time: 13:47
 */
public class DocumentTableServiceImpl extends BaseBean implements DocumentTableService {

        private final static Logger logger = LoggerFactory.getLogger(DocumentTableServiceImpl.class);

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
//		TODO: Разделение метода. Теперь создание происходит отдельно в методе createRootFolder, здесь только получение
//		Вызвано тем, что метод вызывается в вебскрипте без транзакции
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);

		return nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, DOCUMENT_TABLES_ROOT_NAME);
	}

//	@Override
//	public NodeRef getOrCreateRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException {
//		//TODO Рефакторинг AL-2733
//		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);
//
//		NodeRef rootFolder;
//		rootFolder = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, DOCUMENT_TABLES_ROOT_NAME);
//
//		if (null == rootFolder){
//			rootFolder = createRootFolder(documentRef);
//		}
//
//		return rootFolder;
//	}

	@Override
	public NodeRef createRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException {
		//TODO Рефакторинг AL-2733
                //TODO Проверить, какой permission здесь нужен, и нужна ли вообще проверка.
                this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);
		NodeRef docTablesRootRef =  createFolder(documentRef, DOCUMENT_TABLES_ROOT_NAME);
		hideNode(docTablesRootRef, true);

		return docTablesRootRef;
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
			NodeRef tableDataRef = getTableDataByRow(tableDataRowRef);
			return getDocumentByTableData(tableDataRef);
		}
		return null;
	}

    @Override
    public NodeRef getDocumentByTableData(NodeRef tableDataRef) {
        if (tableDataRef != null && nodeService.exists(tableDataRef)) {
	        ChildAssociationRef tableDataRootAssoc = nodeService.getPrimaryParent(tableDataRef);
	        if (tableDataRootAssoc != null) {
	            NodeRef tableDataRoot = tableDataRootAssoc.getParentRef();
	            if (tableDataRoot != null && nodeService.getProperty(tableDataRoot, ContentModel.PROP_NAME).equals(DOCUMENT_TABLES_ROOT_NAME)) {
	                ChildAssociationRef documentToTableDataAssociation = nodeService.getPrimaryParent(tableDataRoot);
	                NodeRef document = documentToTableDataAssociation.getParentRef();
	                if (document != null && documentService.isDocument(document)) {
	                    return documentToTableDataAssociation.getParentRef();
	                }
	            }
	        }
        }
        return null;
    }

    @Override
	public NodeRef getTableDataByRow(NodeRef tableDataRowRef) {
		if (nodeService.exists(tableDataRowRef)) {
			NodeRef tableDataRef = nodeService.getPrimaryParent(tableDataRowRef).getParentRef();
			if (isDocumentTableData(tableDataRef)) {
				return tableDataRef;
			}
		}
		return null;
	}

    @Override
	public List<NodeRef> getTableDataTotalRows(NodeRef tableDataRef) {
	    QName totalRowType = getTableDataTotalRowType(tableDataRef);
	    if (totalRowType != null) {
		    Set<QName> totalTypes = new HashSet<QName>();
		    totalTypes.add(totalRowType);
		    List<ChildAssociationRef> totalRowsAssocs = nodeService.getChildAssocs(tableDataRef, totalTypes);

		    if (totalRowsAssocs != null) {
			    List<NodeRef> result = new ArrayList<NodeRef>();
			    for (ChildAssociationRef assoc: totalRowsAssocs) {
					result.add(assoc.getChildRef());
			    }
			    return result;
		    }
	    }

		return null;
	}

	@Override
	public List<NodeRef> getTableDataRows(NodeRef tableDataRef) {
		QName totalRowType = getTableDataRowType(tableDataRef);
		if (totalRowType != null) {
			Set<QName> totalTypes = new HashSet<QName>();
			totalTypes.add(totalRowType);
			List<ChildAssociationRef> totalRowsAssocs = nodeService.getChildAssocs(tableDataRef, totalTypes);

			if (totalRowsAssocs != null) {
				List<NodeRef> result = new ArrayList<NodeRef>();
				for (ChildAssociationRef assoc: totalRowsAssocs) {
					result.add(assoc.getChildRef());
				}
				return result;
			}
		}

		return null;
	}

	public QName getTableDataRowType(NodeRef tableDataRef) {
		if (tableDataRef != null && nodeService.exists(tableDataRef)) {
			String propType = (String) nodeService.getProperty(tableDataRef, PROP_TABLE_ROW_TYPE);
			if (propType != null && propType.length() > 0) {
				return QName.createQName(propType, namespaceService);
			}
		}
		return null;
	}

	public QName getTableDataTotalRowType(NodeRef tableDataRef) {
		if (tableDataRef != null && nodeService.exists(tableDataRef)) {
			String propType = (String) nodeService.getProperty(tableDataRef, PROP_TABLE_TOTAL_ROW_TYPE);
			if (propType != null && propType.length() > 0) {
				return QName.createQName(propType, namespaceService);
			}
		}
		return null;
	}

	@Override
	public NodeRef createTotalRow(NodeRef tableDataRef) {
		QName totalRowType = getTableDataTotalRowType(tableDataRef);
		if (totalRowType != null) {
                    try {
                        return createNode(tableDataRef, totalRowType, null, null);
                    } catch (WriteTransactionNeededException ex) {
                        logger.debug("Can't create node.", ex);
                        return null;
                    }
		}
		return null;
	}

	@Override
	public void recalculateTotalRows(NodeRef tableDataRef) {
		recalculateTotalRows(tableDataRef, null);
	}

	@Override
	public void recalculateTotalRows(NodeRef tableDataRef, Set<QName> properties) {
		List<NodeRef> totalRows = getTableDataTotalRows(tableDataRef);
		if (totalRows != null) {
			for (NodeRef row: totalRows) {
				recalculateTotalRow(tableDataRef, row, properties);
			}
		}
	}

	@Override
	public void recalculateTotalRow(NodeRef tableDataRef, NodeRef row, Set<QName> properties) {
		if (row != null) {
			if (properties == null) {
				properties = getAllTypeProperties(getTableDataRowType(tableDataRef));
			}
			Set<QName> totalProperties = getAllTypeProperties(nodeService.getType(row));

			if (totalProperties != null && properties != null) {
				List<NodeRef> tableRows = getTableDataRows(tableDataRef);
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

    @Override
    public List<NodeRef> getTableDataRows(NodeRef tableDataRef, int beginIndex) {
        List<NodeRef> tableRows = getTableDataRows(tableDataRef);
        List<NodeRef> result = new ArrayList<NodeRef>();
        Serializable index;
        if (tableRows != null) {
            for (NodeRef tableRow : tableRows) {
                index = nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                if (index != null && (Integer) index >= beginIndex) {
                    result.add(tableRow);
                }
            }
        }
        return result;
    }

    @Override
    public List<NodeRef> getTableDataRows(NodeRef tableDataRef, int beginIndex, int endIndex) {
        List<NodeRef> tableRows = getTableDataRows(tableDataRef, beginIndex);
        List<NodeRef> result = new ArrayList<NodeRef>();
        Serializable index;
        if (tableRows != null) {
            for (NodeRef tableRow : tableRows) {
                index = nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                if (index != null && (Integer) index <= endIndex) {
                    result.add(tableRow);
                }
            }
        }
        return result;
    }

    public String moveTableRow(final NodeRef tableRow, final Boolean up) {

        AuthenticationUtil.RunAsWork<String> move = new AuthenticationUtil.RunAsWork<String>() {
            @Override
            public String doWork() throws Exception {
//				TODO: Вынес транзакции непосредственно в точки вызовов в веб-скрипте.
                        int index = (Integer) nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);

                        int newIndex = up ? (index - 1) : (index + 1);

                        NodeRef tableDataRef = getTableDataByRow(tableRow);
                        NodeRef exchangeRef = getTableRowByIndex(tableDataRef, newIndex);
                        if (null != exchangeRef) {
                            nodeService.setProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW, newIndex);
                            return exchangeRef.toString();
                        } else {
                            return null;
                        }
                    }
        };
        return AuthenticationUtil.runAsSystem(move);
    }

    public NodeRef getTableRowByIndex(NodeRef tableDataRef, Integer index) {
        QName totalRowType = getTableDataRowType(tableDataRef);
        if (totalRowType != null) {
            Set<QName> totalTypes = new HashSet<QName>();
            totalTypes.add(totalRowType);
            List<ChildAssociationRef> totalRowsAssocs = nodeService.getChildAssocs(tableDataRef, totalTypes);
            if (totalRowsAssocs != null) {
                for (ChildAssociationRef assoc : totalRowsAssocs) {
                    NodeRef row = assoc.getChildRef();
                    if (index.equals(nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW))) {
                        return row;
                        }
                    }
            }
        }
        return null;
    }

    @Override
    public String moveTableRowUp(final NodeRef tableRow) {
        return moveTableRow(tableRow, Boolean.TRUE);
    }

    @Override
    public String moveTableRowDown(final NodeRef tableRow) {
        return moveTableRow(tableRow, Boolean.FALSE);
    }

    @Override
    public void addCalculator(String postfix, TableTotalRowCalculator calculator) {
            if (!calculators.containsKey(postfix)) {
                    calculators.put(postfix, calculator);
            }
    }

    private NodeRef copyRow(NodeRef source, NodeRef target) {
        NodeRef result = null;
        if (isDocumentTableData(target) && isDocumentTableDataRow(source)) {
            QName rawType = nodeService.getType(source);
            DocumentCopySettings settings = DocumentCopySettingsBean.getSettingsForDocType(rawType.toPrefixString(namespaceService));
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
            if (settings != null) {
                // копируем свойства
                List<String> propertiesToCopy = settings.getPropsToCopy();
                Map<QName, Serializable> originalProperties = nodeService.getProperties(source);
                for (String propName : propertiesToCopy) {
                    try {
                        QName propQName = QName.createQName(propName, namespaceService);
                        if (propQName != null) {
                            properties.put(propQName, originalProperties.get(propQName));
                        }
                    } catch (InvalidQNameException invalid) {
                        logger.warn("Invalid QName for document property:" + propName);
                    }
                }
            }
            // создаем ноду
            ChildAssociationRef createdNodeAssoc = nodeService.createNode(target,
                    ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                            GUID.generate()), rawType, properties);

            if (createdNodeAssoc != null && createdNodeAssoc.getChildRef() != null) {
                result = createdNodeAssoc.getChildRef();
                if (settings != null) {
                    // копируем ассоциации
                    List<String> assocsToCopy = settings.getAssocsToCopy();
                    for (String assocName : assocsToCopy) {
                        try {
                            QName assocQName = QName.createQName(assocName, namespaceService);
                            if (assocQName != null) {
                                List<NodeRef> targets = findNodesByAssociationRef(source, assocQName, null, ASSOCIATION_TYPE.TARGET);
                                nodeService.setAssociations(result, assocQName, targets);

                            }
                        } catch (InvalidQNameException invalid) {
                            logger.warn("Invalid QName for document assoc:" + assocName);
                        }
                    }
                }
            }
            
        }
        return result;
    }
    
    @Override
    public NodeRef copyTableData(NodeRef document, NodeRef tableDataRef) {
        if (isDocumentTableData(tableDataRef)) {
            List<ChildAssociationRef> items =  nodeService.getChildAssocs(tableDataRef);
            NodeRef targetTable =  findNodesByAssociationRef(document, RegexQNamePattern.MATCH_ALL, nodeService.getType(tableDataRef), ASSOCIATION_TYPE.TARGET).get(0);
            for (ChildAssociationRef itemAssociationRef : items) {
                NodeRef item = itemAssociationRef.getChildRef();
                copyRow(item, targetTable);
            }
            return targetTable;
        } else {
            return null;
        }
    }

	@Override
	public NodeRef getTable(NodeRef document, QName tableType) {
		NodeRef tableDataRootFolder = getRootFolder(document);
		if (tableDataRootFolder != null) {
			Set<QName> typeSet = new HashSet<>(1);
			typeSet.add(tableType);
			List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
			if (childAssocs != null && childAssocs.size() == 1) {
				return childAssocs.get(0).getChildRef();
			}
		}
		return null;
	}

	public void recalculateSearchDescription(NodeRef tableData) {
		StringBuilder textContentBuilder = new StringBuilder();
		NodeRef document = getDocumentByTableData(tableData);
		QName documentTableTextContentProperty = getDocumentTableTextContentProperty(tableData);
		//получаем карту соответствия атрибутов строки и документа
		Map<String, QName> tableIndexProperties = getDocumentTableIndexProperties(tableData);
		HashMap<QName, StringBuilder> indexPropertiesBuilders = new HashMap<>();    //здесь будем собирать тексты
		if (documentTableTextContentProperty != null) {
			List<NodeRef> tableDataRows = getTableDataRows(tableData);
			for (NodeRef tableDataRow : tableDataRows) {
				if (tableDataRow != null) {
					textContentBuilder.append(getDataRowContent(tableDataRow)).append(";"); //свойство с данными всех строк, возможно больше не требуется
					Map<QName, Serializable> rowProperties = nodeService.getProperties(tableDataRow);
					for (Map.Entry<QName, Serializable> rowProp : rowProperties.entrySet()) {
						//ищем подходящие атрибуты
						QName docProp = tableIndexProperties.get(rowProp.getKey().getLocalName());
						if (docProp != null) {
							//дописываем в соответствующие свойства
							if (indexPropertiesBuilders.get(docProp) == null) {
								indexPropertiesBuilders.put(docProp, new StringBuilder(rowProp.getValue() + ";"));
							} else {
								indexPropertiesBuilders.get(docProp).append(rowProp.getValue()).append(";");
							}
						}
					}
				}
			}
			//сохраняем собраные свойства
			for (QName qName : tableIndexProperties.values()) {
				StringBuilder value = indexPropertiesBuilders.get(qName);
				nodeService.setProperty(document, qName, value == null ? null : value.toString());
			}
			nodeService.setProperty(document, documentTableTextContentProperty, textContentBuilder.toString());
		}
	}

	private QName getDocumentTableTextContentProperty(NodeRef tableData) {
		NodeRef document = getDocumentByTableData(tableData);
		QName documentToTableDataAssociation = null;
		if (document != null) {
			for (AssociationRef associationRef : nodeService.getSourceAssocs(tableData, RegexQNamePattern.MATCH_ALL)) {
				if (document.equals(associationRef.getSourceRef())) {
					documentToTableDataAssociation = associationRef.getTypeQName();
					break;
				}
			}
			if (documentToTableDataAssociation != null) {
				QName documentTableTextContentProperty = QName.createQName(documentToTableDataAssociation.toPrefixString(namespaceService) + "-text-content", namespaceService);
				PropertyDefinition propertyDefinition = dictionaryService.getProperty(documentTableTextContentProperty);
				if (propertyDefinition != null) {
					return documentTableTextContentProperty;
				}
			}
		}
		return null;
	}

	/**
	 * Найти все свойства документа, в которые необходимо "пробрасывать" соответствующие свойства из записей таблицы
	 * Имена этих свойств должны быть вида <имя ассоциации на таблицу>-<имя атрибута в строке>
	 *
	 * @param tableData таблица
	 * @return карта вида <localName свойства записи таблицы, QName свойства документа
	 */
	private Map<String, QName> getDocumentTableIndexProperties(NodeRef tableData) {
		Map<String, QName> map = new HashMap<>();
		NodeRef document = getDocumentByTableData(tableData);
		QName documentToTableDataAssociation = null;
		if (document != null) {
			//получаем название ассоциации на табличные данные
			for (AssociationRef associationRef : nodeService.getSourceAssocs(tableData, RegexQNamePattern.MATCH_ALL)) {
				if (document.equals(associationRef.getSourceRef())) {
					documentToTableDataAssociation = associationRef.getTypeQName();
					break;
				}
			}
			if (documentToTableDataAssociation != null) {
				String documentToTableDataAssociationLocalName = documentToTableDataAssociation.getLocalName();
				int beginIndex = documentToTableDataAssociationLocalName.length() + 1;
				QName documentType = nodeService.getType(document);
				Map<QName, PropertyDefinition> allProperties = dictionaryService.getPropertyDefs(documentType);
				if (allProperties != null) {
					//перебираем все свойства документа
					for (QName property : allProperties.keySet()) {
						String localName = property.getLocalName();
						//запоминаем те, которые начинаются с имени ассоциации
						if (localName.startsWith(documentToTableDataAssociationLocalName)) {
							map.put(localName.substring(beginIndex), property);
						}
					}
				}
			}
		}

		return map;
	}

	private String getDataRowContent(NodeRef node) {
		if (!dictionaryService.isSubClass(nodeService.getType(node), DocumentTableService.TYPE_TABLE_DATA_ROW)) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		Map<QName, Serializable> properties = nodeService.getProperties(node);
		for (Map.Entry<QName, Serializable> propertyEntry : properties.entrySet()) {
			QName qName = propertyEntry.getKey();
			if (qName.getNamespaceURI().equals(NamespaceService.SYSTEM_MODEL_1_0_URI)
					|| qName.equals(ContentModel.PROP_CREATOR)
					|| qName.equals(ContentModel.PROP_CREATED)
					|| qName.equals(ContentModel.PROP_MODIFIER)
					|| qName.equals(ContentModel.PROP_MODIFIED)
					|| qName.getLocalName().endsWith("-text-content")
					|| qName.getLocalName().endsWith("-ref")) {
				continue;
			}
			PropertyDefinition propertyDefinition = dictionaryService.getProperty(qName);
			if (propertyDefinition.isIndexed()) {
				Serializable value = propertyEntry.getValue();
				if (value != null)
					stringBuilder.append(value).append(" ");
			}
		}

		return stringBuilder.length() > 0 ? stringBuilder.substring(0, stringBuilder.length() - 1) : "";
	}
}
