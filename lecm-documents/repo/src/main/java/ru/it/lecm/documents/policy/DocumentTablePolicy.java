package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
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
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 14:24
 */
public class DocumentTablePolicy extends BaseBean {

	final static protected Logger logger = LoggerFactory.getLogger(DocumentTablePolicy.class);

	private PolicyComponent policyComponent;
	private DocumentTableService documentTableService;
	private DocumentAttachmentsService documentAttachmentsService;
	protected NamespaceService namespaceService;
	protected DictionaryService dictionaryService;

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

	final public void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onCreateAttachmentAssoc", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onDeleteAttachmentAssoc", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "deleteTableDataRow"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this, "createTableDataAssoc", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

//		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
//				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "calculateTotalRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

//		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
//				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateTableDataRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//
//		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
//				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onDeleteTableDataRow", Behaviour.NotificationFrequency.FIRST_EVENT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onAddAspect", Behaviour.NotificationFrequency.FIRST_EVENT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateDocument", Behaviour.NotificationFrequency.FIRST_EVENT));
	}

	/**
	 * Добавление связи при создании поручения на основании документа
	 */
	public void onCreateAttachmentAssoc(AssociationRef associationRef) {
		NodeRef tableDataRowRef = associationRef.getSourceRef();
		NodeRef attachmentRef = associationRef.getTargetRef();

		String categoryName = getCategoryNameByTableDataRow(tableDataRowRef, associationRef.getTypeQName());

		if (categoryName != null) {
			NodeRef document = documentTableService.getDocumentByTableDataRow(tableDataRowRef);
			if (document != null) {
				NodeRef categoryRef = documentAttachmentsService.getCategory(categoryName, document);
				//категории могли быть ещё не созданы,
				// тогда они создаются и заново пытаемся получить категорию
				if (categoryRef == null) {
					documentAttachmentsService.getCategories(document);
					categoryRef = documentAttachmentsService.getCategory(categoryName, document);
				}

				if (categoryRef != null) {
					String name = nodeService.getProperty (attachmentRef, ContentModel.PROP_NAME).toString ();
					QName assocQname = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, name);
					nodeService.moveNode(attachmentRef, categoryRef, ContentModel.ASSOC_CONTAINS, assocQname);
				}
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
		NodeRef documentTableDataRef = associationRef.getSourceRef();

		removeAttachment(documentTableDataRef, associationRef);
	}

	public void deleteTableDataRow(NodeRef nodeRef) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
		if (assocs != null) {
			for (AssociationRef assoc: assocs) {
				removeAttachment(nodeRef, assoc);
			}
		}

		//Пересчёт результирующей строки
		NodeRef tableData = documentTableService.getTableDataByRow(nodeRef);
		if (tableData != null) {
			documentTableService.recalculateTotalRows(tableData);
		}
	}

	public void removeAttachment(NodeRef documentTableDataRef, AssociationRef assoc) {
		String categoryName = getCategoryNameByTableDataRow(documentTableDataRef, assoc.getTypeQName());

		if (categoryName != null) {
			documentAttachmentsService.deleteAttachment(assoc.getTargetRef());
		}
	}

	public void createTableDataAssoc(ChildAssociationRef childAssocRef) {
		NodeRef tableData = childAssocRef.getParentRef();

		//Пересчёт результирующей строки
		documentTableService.recalculateTotalRows(tableData);
	}

	/**
	 * Пересчёт результирующей строки при изменении одной из строк
	 */
//	public void calculateTotalRow(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
//		List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(nodeRef);
//		if (totalRows != null) {
//			AssociationRef documentAssoc = documentTableService.getDocumentAssocByTableData(nodeRef);
//			if (documentAssoc != null) {
//				NodeRef document = documentAssoc.getSourceRef();
//				if (document != null) {
//					QName tableDataType = nodeService.getType(nodeRef);
//					QName tableDataAssocType = documentAssoc.getTypeQName();
//					Set<QName> changedProperties = getChangedProperties(before, after, nodeService.getType(nodeRef));
//
//					documentTableService.recalculateTotalRows(document, totalRows, tableDataType, tableDataAssocType, changedProperties);
//				}
//			}
//		}
//	}

//	public Set<QName> getChangedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after, QName type) {
//		Set<QName> result = new HashSet<QName>();
//		TypeDefinition typeDefinition = dictionaryService.getType(type);
//		if (typeDefinition != null) {
//			Map<QName, PropertyDefinition> allProperties = typeDefinition.getProperties();
//			if (allProperties != null) {
//				for (QName property : allProperties.keySet()) {
//					Object prev = before.get(property);
//					Object cur = after.get(property);
//					if ((cur == null && prev != null) || (cur != null && !cur.equals(prev))) {
//						result.add(property);
//					}
//				}
//			}
//		}
//		return result;
//	}

	/**
	 * Выполнение действий после создания ассоциации между документом и строкой табличных данных
	 */
//	public void onCreateTableDataRow(AssociationRef associationRef) {
//		NodeRef documentRef = associationRef.getSourceRef();
//		NodeRef documentTableDataRef = associationRef.getTargetRef();
//
//		if (documentTableService.isDocumentTableDataRow(documentTableDataRef)) {
//			QName tableDataType = nodeService.getType(documentTableDataRef);
//			QName tableDataAssocType = associationRef.getTypeQName();
//			//Создание результирующей записи, если она ещё не была создана
//			List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(documentRef, tableDataType, tableDataAssocType, true);
//			documentTableService.recalculateTotalRows(documentRef, totalRows, tableDataType, tableDataAssocType, null);
//            //Присвоение максимального индекса
//            List<NodeRef> tableRowList = documentTableService.getTableDataRows(documentRef, tableDataAssocType);
//            if (tableRowList != null) {
//                int maxIndex = 0;
//                int index;
//                String indexStr;
//                for (NodeRef tableRow : tableRowList) {
//                    indexStr = (String)nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
//                    if (indexStr != null && !indexStr.equals("")){
//                        index = Integer.parseInt(indexStr);
//                        if (maxIndex < index){
//                            maxIndex = index;
//                        }
//                    }
//                }
//                nodeService.setProperty(documentTableDataRef,DocumentTableService.PROP_INDEX_TABLE_ROW, maxIndex+1);
//		    }
//        }
//	}

	/**
	 * Выполнение действий после удаления ассоциации между документом и строкой табличных данных
	 */
//	public void onDeleteTableDataRow(AssociationRef associationRef) {
//		NodeRef documentRef = associationRef.getSourceRef();
//		NodeRef documentTableDataRef = associationRef.getTargetRef();
//
//		if (documentTableService.isDocumentTableDataRow(documentTableDataRef)) {
//			QName tableDataType = nodeService.getType(documentTableDataRef);
//			QName tableDataAssocType = associationRef.getTypeQName();
//
//			//Пересчёт результирующих строк
//			List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(documentRef, tableDataType, tableDataAssocType, false);
//			documentTableService.recalculateTotalRows(documentRef, totalRows, tableDataType, tableDataAssocType, null);
//            //Персчет индексов
//            int index;
//            String indexStr;
//            indexStr = (String)nodeService.getProperty(documentTableDataRef, DocumentTableService.PROP_INDEX_TABLE_ROW);
//            if (indexStr != null && !indexStr.equals("")){
//                index = Integer.parseInt(indexStr);
//                List<NodeRef> tableRowList = documentTableService.getTableDataRows(documentRef, tableDataAssocType, index+1);
//                if (tableRowList != null){
//                    //переприсвоение индексов
//                    for (NodeRef tableRow : tableRowList) {
//                        indexStr = (String)nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
//                        if (indexStr != null && !indexStr.equals("")){
//                            index = Integer.parseInt(indexStr);
//                            if (indexStr != null && !indexStr.equals("")){
//                                nodeService.setProperty(tableRow,DocumentTableService.PROP_INDEX_TABLE_ROW, index-1);
//                            }
//                        }
//                    }
//                }
//            }
//		}
//	}

	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
   	    if (dictionaryService.isSubClass(aspectTypeQName, DocumentTableService.ASPECT_TABLE_DATA)) {
	        AspectDefinition aspectDefinition = dictionaryService.getAspect(aspectTypeQName);
	        Map<QName, AssociationDefinition> associations = aspectDefinition.getAssociations();
	        if (associations != null) {
		        for (AssociationDefinition assoc: associations.values()) {
			        QName assocName = assoc.getName();
			        ClassDefinition targetClass = assoc.getTargetClass();
		            if (targetClass != null) {
			            QName asscoClassName = targetClass.getName();
			            if (asscoClassName != null && dictionaryService.isSubClass(asscoClassName, DocumentTableService.TYPE_TABLE_DATA)) {
				            NodeRef rootFolder = documentTableService.getRootFolder(nodeRef);
				            String nodeName = FileNameValidator.getValidFileName(assocName.toPrefixString(namespaceService));

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
		for (QName aspect: aspects) {
			onAddAspect(document, aspect);
		}
	}
}
