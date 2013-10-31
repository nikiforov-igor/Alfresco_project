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
import java.util.HashSet;
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
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onAddAspect", Behaviour.NotificationFrequency.FIRST_EVENT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateDocument", Behaviour.NotificationFrequency.FIRST_EVENT));


		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "createTableDataRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "updatePropertiesOfTotalRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

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
				for (AssociationDefinition assoc: associations.values()) {
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
		for (QName aspect: aspects) {
			onAddAspect(document, aspect);
		}
	}

	public void createTableDataRow(ChildAssociationRef childAssocRef) {
		NodeRef tableData = childAssocRef.getParentRef();
        NodeRef tableRow = childAssocRef.getChildRef();

		//Пересчёт результирующей строки
		documentTableService.recalculateTotalRows(tableData);
        int index;
        List<NodeRef> tableRowList;
        String indexStr;

        //Пересчет индекса строки
        indexStr = (String)nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
        if (indexStr != null && !indexStr.equals("")) {
            // Пересчет индекса с текущей строки
            index = Integer.parseInt(indexStr);
            tableRowList = documentTableService.getTableDataRows(tableData, index);
            if (tableRowList != null){
                //переприсвоение индексов
                for (NodeRef row : tableRowList) {
                    if (!tableRow.equals(row)){
                        indexStr = (String)nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
                        if (indexStr != null && !indexStr.equals("")){
                            index = Integer.parseInt(indexStr);
                            nodeService.setProperty(row,DocumentTableService.PROP_INDEX_TABLE_ROW, index+1);
                        }
                    }
                }
            }
        } else {
            // Присвоение максимального индекса
            tableRowList = documentTableService.getTableDataRows(tableData);
            if (tableRowList != null) {
                int maxIndex = 0;
                for (NodeRef row : tableRowList) {
                    indexStr = (String)nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
                    if (indexStr != null && !indexStr.equals("")){
                        index = Integer.parseInt(indexStr);
                        if (maxIndex < index){
                            maxIndex = index;
                        }
                    }
                }
                nodeService.setProperty(tableRow,DocumentTableService.PROP_INDEX_TABLE_ROW, maxIndex+1);
            }
        }

	}

	public void updatePropertiesOfTotalRow(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		NodeRef tableData = documentTableService.getTableDataByRow(nodeRef);
		if (tableData != null) {
			Set<QName> changedProperties = getChangedProperties(before, after, nodeService.getType(nodeRef));
			documentTableService.recalculateTotalRows(tableData, changedProperties);
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

	public void beforeDeleteTableDataRow(NodeRef nodeRef) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
		if (assocs != null) {
			for (AssociationRef assoc: assocs) {
				removeAttachment(nodeRef, assoc);
			}
		}
        //Персчет индексов
        int index;
        String indexStr;
        NodeRef tableData = documentTableService.getTableDataByRow(nodeRef);
        if (tableData != null) {
            indexStr = (String)nodeService.getProperty(nodeRef, DocumentTableService.PROP_INDEX_TABLE_ROW);
            if (indexStr != null && !indexStr.equals("")){
                index = Integer.parseInt(indexStr);
                List<NodeRef> tableRowList = documentTableService.getTableDataRows(tableData, index+1);
                if (tableRowList != null){
                    //переприсвоение индексов
                    for (NodeRef tableRow : tableRowList) {
                        indexStr = (String)nodeService.getProperty(tableRow, DocumentTableService.PROP_INDEX_TABLE_ROW);
                        if (indexStr != null && !indexStr.equals("")){
                            index = Integer.parseInt(indexStr);
                            nodeService.setProperty(tableRow,DocumentTableService.PROP_INDEX_TABLE_ROW, index-1);
                        }
                    }
                }
            }
        }
	}

	public void removeAttachment(NodeRef documentTableDataRef, AssociationRef assoc) {
		String categoryName = getCategoryNameByTableDataRow(documentTableDataRef, assoc.getTypeQName());

		if (categoryName != null) {
			documentAttachmentsService.deleteAttachment(assoc.getTargetRef());
		}
	}
}
