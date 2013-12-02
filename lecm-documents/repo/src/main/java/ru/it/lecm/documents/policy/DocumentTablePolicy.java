package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.*;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.FileNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 14:24
 */
public class DocumentTablePolicy extends BaseBean {

	final static protected Logger logger = LoggerFactory.getLogger(DocumentTablePolicy.class);

	private PolicyComponent policyComponent;
    private BehaviourFilter behaviourFilter;
	private DocumentTableService documentTableService;
	private DocumentAttachmentsService documentAttachmentsService;
	protected NamespaceService namespaceService;
	protected DictionaryService dictionaryService;
	private BusinessJournalService businessJournalService;

	private String lastTransactionId = "";

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	final public void init() {
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onAddAspect", Behaviour.NotificationFrequency.FIRST_EVENT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateDocument", Behaviour.NotificationFrequency.FIRST_EVENT));


		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "createTableDataRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onUpdatePropertiesOfTableDataRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "deleteTableDataRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));


		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onCreateAttachmentAssoc", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onDeleteAttachmentAssoc", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "beforeDeleteTableDataRow"));
	}

	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		if (dictionaryService.isSubClass(aspectTypeQName, DocumentTableService.ASPECT_TABLE_DATA)) {
			AspectDefinition aspectDefinition = dictionaryService.getAspect(aspectTypeQName);
			Map<QName, AssociationDefinition> associations = aspectDefinition.getAssociations();
			if (associations != null) {
				for (AssociationDefinition assoc : associations.values()) {
					QName assocName = assoc.getName();
					ClassDefinition targetClass = assoc.getTargetClass();
					if (targetClass != null) {
						QName asscoClassName = targetClass.getName();
						if (asscoClassName != null && dictionaryService.isSubClass(asscoClassName, DocumentTableService.TYPE_TABLE_DATA)) {
							NodeRef rootFolder = documentTableService.getRootFolder(nodeRef);

							String nodeName = assoc.getTitle();
							if (nodeName == null) {
								nodeName = assocName.toPrefixString(namespaceService);
							}
							nodeName = FileNameValidator.getValidFileName(nodeName);

							NodeRef tableData = createNode(rootFolder, asscoClassName, nodeName, null);
							nodeService.createAssociation(nodeRef, tableData, assocName);

							documentTableService.createTotalRow(tableData);
						}
					}
				}
			}
		}
	}

	public void onCreateDocument(ChildAssociationRef childAssocRef) {
		NodeRef document = childAssocRef.getChildRef();
		Set<QName> aspects = nodeService.getAspects(document);
		for (QName aspect : aspects) {
			onAddAspect(document, aspect);
		}
	}

	public void createTableDataRow(ChildAssociationRef childAssocRef) {
		NodeRef tableData = childAssocRef.getParentRef();
		NodeRef tableRow = childAssocRef.getChildRef();

		//Пересчёт результирующей строки
		documentTableService.recalculateTotalRows(tableData);
		Integer index;
		List<NodeRef> tableRowList;

		//Пересчет индекса строки
		index = (Integer) nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
        // олучение максимального индекса
        tableRowList = documentTableService.getTableDataRows(tableData);
        int maxIndex = 1;
        if (tableRowList != null) {
            maxIndex = tableRowList.size();
        }
		if (index != null && index < maxIndex) { //индекс не может быть больше максимального - чтобы не было разрывов
			// Пересчет индекса с текущей строки
			tableRowList = documentTableService.getTableDataRows(tableData, index);
			if (tableRowList != null) {
				//переприсвоение индексов
				for (NodeRef row : tableRowList) {
					if (!tableRow.equals(row)) {
						index = (Integer) nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
                        try {
                            behaviourFilter.disableBehaviour(row);//блокируем повторный вызов
                            nodeService.setProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW, index + 1);
                        } finally {
                            behaviourFilter.enableBehaviour(row);
                        }
					}
				}
			}
		} else {
			nodeService.setProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW, maxIndex);
		}

		//Обновление данных для поиска
		addSearchDescription(tableData, childAssocRef.getChildRef());

		//Логгирование в бизнес-журнал
		NodeRef document = documentTableService.getDocumentByTableData(tableData);
		if (document != null) {
			List<String> objects = new ArrayList<String>();
			objects.add(tableRow.toString());
			objects.add(tableData.toString());
			businessJournalService.log(document, EventCategory.ADD, "#initiator добавил запись #object1 в таблицу #object2 документа #mainobject", objects);
		}
	}

	public void onUpdatePropertiesOfTableDataRow(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		NodeRef tableData = documentTableService.getTableDataByRow(nodeRef);
		if (tableData != null) {
			Set<QName> changedProperties = getChangedProperties(before, after, nodeService.getType(nodeRef));
			documentTableService.recalculateTotalRows(tableData, changedProperties);
			//Обновление данных для поиска
			recalculateSearchDescription(tableData);
            //Обновление индексов строк
            Integer oldIndex = (Integer) before.get(DocumentTableService.PROP_INDEX_TABLE_ROW);
            Integer newIndex = (Integer) after.get(DocumentTableService.PROP_INDEX_TABLE_ROW);
            int newIndexInt = changeRowIndex(nodeRef, tableData, oldIndex, newIndex);
            after.put(DocumentTableService.PROP_INDEX_TABLE_ROW, newIndexInt);
            //Логгирование в бизнес-журнал
			if (before.size() == after.size()) {
				NodeRef document = documentTableService.getDocumentByTableData(tableData);

				if (document != null) {
					logEditTableDataRow(document, tableData, nodeRef);
				}
			}
		}
	}

    private int changeRowIndex(NodeRef rowRef, NodeRef tableData, Integer oldIndex, Integer newIndex) {
        //Получение максимального индекса
        List<NodeRef> tableRowList = documentTableService.getTableDataRows(tableData);
        int maxIndex = 1;
        if (tableRowList != null) {
            maxIndex = tableRowList.size();
        }
        int newIndexInt = newIndex != null ? newIndex : maxIndex;
        //проверка границ индекса
        if (newIndexInt < 1) {
            newIndexInt = 1;
        } else if (newIndexInt > maxIndex) {
            newIndexInt = maxIndex;
        }
        int oldIndexInt = oldIndex != null ? oldIndex : newIndex;
        //если индекс поменялся
        if (oldIndexInt != newIndexInt) {
            int fromIndex = oldIndexInt < newIndexInt ? oldIndexInt : newIndexInt;
            int toIndex = oldIndexInt < newIndexInt ? newIndexInt : oldIndexInt;
            int direction = oldIndexInt < newIndexInt ? -1 : 1;
            List<NodeRef> tableRowListToChange = documentTableService.getTableDataRows(tableData, fromIndex, toIndex);
            //двигаем остальные записи
            for (NodeRef row : tableRowListToChange) {
                if (!rowRef.equals(row)) {
                    Integer index = (Integer) nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
                    try {
                        behaviourFilter.disableBehaviour(row);//блокируем повторный вызов
                        nodeService.setProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW, index + direction);
                    } finally {
                        behaviourFilter.enableBehaviour(row);
                    }
                }
            }
            //запись нового индекса
            nodeService.setProperty(rowRef, DocumentTableService.PROP_INDEX_TABLE_ROW, newIndexInt);
        }
        return newIndexInt;
    }

    public void logEditTableDataRow(NodeRef document, NodeRef tableData, NodeRef tableDataRow) {
		String transactionId = AlfrescoTransactionSupport.getTransactionId();
		if (!this.lastTransactionId.equals(transactionId)) {
			this.lastTransactionId = transactionId;

			List<String> objects = new ArrayList<String>();
			objects.add(tableDataRow.toString());
			objects.add(tableData.toString());
			businessJournalService.log(document, EventCategory.EDIT, "#initiator изменил запись #object1 в таблице #object2 документа #mainobject", objects);
		}
	}

	public Set<QName> getChangedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after, QName type) {
		Set<QName> result = new HashSet<QName>();
		TypeDefinition typeDefinition = dictionaryService.getType(type);
		if (typeDefinition != null) {
			Map<QName, PropertyDefinition> allProperties = typeDefinition.getProperties();
			if (allProperties != null) {
				for (QName property : allProperties.keySet()) {
					Object prev = before.get(property);
					Object cur = after.get(property);
					if ((cur == null && prev != null) || (cur != null && !cur.equals(prev))) {
						result.add(property);
					}
				}
			}
		}
		return result;
	}

	public void deleteTableDataRow(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		NodeRef tableData = childAssocRef.getParentRef();

		//Пересчёт результирующей строки
		documentTableService.recalculateTotalRows(tableData);
		//Обновление данных для поиска
		recalculateSearchDescription(tableData);
	}

	/**
	 * Добавление связи при создании поручения на основании документа
	 */
	public void onCreateAttachmentAssoc(AssociationRef associationRef) {
		NodeRef tableDataRowRef = associationRef.getSourceRef();
		NodeRef attachmentRef = associationRef.getTargetRef();

		NodeRef document = documentTableService.getDocumentByTableDataRow(tableDataRowRef);
		if (document != null) {

			String categoryName = getCategoryNameByTableDataRow(tableDataRowRef, associationRef.getTypeQName());

			if (categoryName != null) {
				NodeRef categoryRef = documentAttachmentsService.getCategory(categoryName, document);
				//категории могли быть ещё не созданы,
				// тогда они создаются и заново пытаемся получить категорию
				if (categoryRef == null) {
					documentAttachmentsService.getCategories(document);
					categoryRef = documentAttachmentsService.getCategory(categoryName, document);
				}

				if (categoryRef != null) {
					String name = nodeService.getProperty(attachmentRef, ContentModel.PROP_NAME).toString();
					QName assocQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
					nodeService.moveNode(attachmentRef, categoryRef, ContentModel.ASSOC_CONTAINS, assocQname);
				}
			}

			//Логгирование в бизнес-журнал
			NodeRef tableData = documentTableService.getTableDataByRow(tableDataRowRef);
			if (tableData != null) {
				logEditTableDataRow(document, tableData, tableDataRowRef);
			}
		}
	}

	public String getCategoryNameByTableDataRow(NodeRef documentTableDataRef, QName assocType) {
		String assocQName = assocType.toPrefixString(namespaceService);

		QName propertyCategoryQName = QName.createQName(assocQName + "-category", namespaceService);
		PropertyDefinition propertyCategoryDefinition = dictionaryService.getProperty(propertyCategoryQName);
		if (propertyCategoryDefinition != null) {
			Serializable categoryName = nodeService.getProperty(documentTableDataRef, propertyCategoryQName);
			if (categoryName != null) {
				return (String) categoryName;
			}
		}
		return null;
	}

	public void onDeleteAttachmentAssoc(AssociationRef associationRef) {
		NodeRef tableDataRow = associationRef.getSourceRef();

		removeAttachment(tableDataRow, associationRef);

		//Логгирование в бизнес-журнал
		NodeRef tableData = documentTableService.getTableDataByRow(tableDataRow);
		if (tableData != null) {
			NodeRef document = documentTableService.getDocumentByTableData(tableData);
			if (document != null) {
				logEditTableDataRow(document, tableData, tableDataRow);
			}
		}
	}

	public void beforeDeleteTableDataRow(NodeRef nodeRef) {
		final NodeRef tableDataRow = nodeRef;
		final List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
		if (assocs != null) {
			AuthenticationUtil.runAsSystem (new AuthenticationUtil.RunAsWork<Void>() {
				@Override
				public Void doWork() throws Exception {
					RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
					return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
						@Override
						public Void execute() throws Throwable {
							for (AssociationRef assoc : assocs) {
								removeAttachment(tableDataRow, assoc);
							}
							return null;
						}
					}, false, true);
				}
			});
		}
		//Пересчёт индексов
		int index;
		NodeRef tableData = documentTableService.getTableDataByRow(nodeRef);
		if (tableData != null) {
			index = (Integer) nodeService.getProperty(nodeRef, DocumentTableService.PROP_INDEX_TABLE_ROW);
			List<NodeRef> tableRowList = documentTableService.getTableDataRows(tableData, index + 1);
			if (tableRowList != null) {
				//переприсвоение индексов
				for (NodeRef tableRow : tableRowList) {
					index = (Integer) nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                    try {
                        behaviourFilter.disableBehaviour(tableRow);//блокируем повторный вызов
                        nodeService.setProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW, index - 1);
                    } finally {
                        behaviourFilter.enableBehaviour(tableRow);
                    }
				}
			}

			//Логгирование в бизнес-журнал
			NodeRef document = documentTableService.getDocumentByTableData(tableData);
			if (document != null) {
				List<String> objects = new ArrayList<String>();
				objects.add(nodeRef.toString());
				objects.add(tableData.toString());
				businessJournalService.log(document, EventCategory.DELETE, "#initiator удалил запись #object1 в таблице #object2 документа #mainobject", objects);
			}
		}
	}

	public void removeAttachment(NodeRef documentTableDataRef, AssociationRef assoc) {
		String categoryName = getCategoryNameByTableDataRow(documentTableDataRef, assoc.getTypeQName());

		if (categoryName != null) {
			documentAttachmentsService.deleteAttachment(assoc.getTargetRef());
		}
	}

	private void addSearchDescription(NodeRef tableData, NodeRef tableDataRow) {
		NodeRef document = documentTableService.getDocumentByTableData(tableData);
		QName documentTableTextContentProperty = getDocumentTableTextContentProperty(tableData);
		if (documentTableTextContentProperty != null) {
			Serializable oldValue = nodeService.getProperty(document, documentTableTextContentProperty);
			StringBuilder strOldValue = new StringBuilder(oldValue != null ? oldValue.toString() : "");
			String strNewValue = getDataRowContent(tableDataRow);
			if (!strNewValue.isEmpty() && strOldValue.indexOf(strNewValue) != -1) {
				strOldValue.append(strNewValue).append(";");
			}
			nodeService.setProperty(document, documentTableTextContentProperty, strOldValue.toString());
		}
	}

	private void recalculateSearchDescription(NodeRef tableData) {
		StringBuilder builder = new StringBuilder();
		NodeRef document = documentTableService.getDocumentByTableData(tableData);
		QName documentTableTextContentProperty = getDocumentTableTextContentProperty(tableData);
		if (documentTableTextContentProperty != null) {
			List<NodeRef> tableDataRows = documentTableService.getTableDataRows(tableData);
			for (NodeRef tableDataRow : tableDataRows) {
				if (tableDataRow != null) {
					builder.append(getDataRowContent(tableDataRow)).append(";");
				}
			}
			nodeService.setProperty(document, documentTableTextContentProperty, builder.toString());
		}
	}

	private QName getDocumentTableTextContentProperty(NodeRef tableData) {
		NodeRef document = documentTableService.getDocumentByTableData(tableData);
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

    public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
        this.behaviourFilter = behaviourFilter;
    }
}
