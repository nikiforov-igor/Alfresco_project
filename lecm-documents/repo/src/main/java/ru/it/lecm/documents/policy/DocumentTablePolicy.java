package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DocumentTablePolicy implements
		NodeServicePolicies.OnCreateAssociationPolicy,
		NodeServicePolicies.OnDeleteAssociationPolicy,
		NodeServicePolicies.BeforeDeleteNodePolicy {

	final static protected Logger logger = LoggerFactory.getLogger(DocumentTablePolicy.class);

	private PolicyComponent policyComponent;
	private DocumentTableService documentTableService;
	private DocumentAttachmentsService documentAttachmentsService;
	private NodeService nodeService;
	protected  NamespaceService namespaceService;
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

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	final public void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "beforeDeleteNode"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "calculateTotalRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateTableDataRow", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onDeleteTableDataRow", Behaviour.NotificationFrequency.FIRST_EVENT));
	}

	/**
	 * Добавление связи при создании поручения на основании документа
	 */
	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		NodeRef documentTableDataRef = associationRef.getSourceRef();
		NodeRef attachmentRef = associationRef.getTargetRef();

		String categoryName = getCategoryNameByTableData(documentTableDataRef, associationRef.getTypeQName());

		if (categoryName != null) {
			NodeRef document = documentTableService.getDocumentByTableData(documentTableDataRef);
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

	public String getCategoryNameByTableData(NodeRef documentTableDataRef, QName assocType) {
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

	@Override
	public void onDeleteAssociation(AssociationRef associationRef) {
		NodeRef documentTableDataRef = associationRef.getSourceRef();

		removeAttachment(documentTableDataRef, associationRef);
	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
		if (assocs != null) {
			for (AssociationRef assoc: assocs) {
				removeAttachment(nodeRef, assoc);
			}
		}
	}

	public void removeAttachment(NodeRef documentTableDataRef, AssociationRef assoc) {
		String categoryName = getCategoryNameByTableData(documentTableDataRef, assoc.getTypeQName());

		if (categoryName != null) {
			documentAttachmentsService.deleteAttachment(assoc.getTargetRef());
		}
	}

	/**
	 * Пересчёт результирующей строки при изменении одной из строк
	 */
	public void calculateTotalRow(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(nodeRef);
		if (totalRows != null) {
			AssociationRef documentAssoc = documentTableService.getDocumentAssocByTableData(nodeRef);
			if (documentAssoc != null) {
				NodeRef document = documentAssoc.getSourceRef();
				if (document != null) {
					QName tableDataType = nodeService.getType(nodeRef);
					QName tableDataAssocType = documentAssoc.getTypeQName();
					Set<QName> changedProperties = getChangedProperties(before, after, nodeService.getType(nodeRef));

					documentTableService.recalculateTotalRows(document, totalRows, tableDataType, tableDataAssocType, changedProperties);
				}
			}
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

	/**
	 * Выполнение действий после создания ассоциации между документом и строкой табличных данных
	 */
	public void onCreateTableDataRow(AssociationRef associationRef) {
		NodeRef documentRef = associationRef.getSourceRef();
		NodeRef documentTableDataRef = associationRef.getTargetRef();

		if (documentTableService.isDocumentTableData(documentTableDataRef)) {
			QName tableDataType = nodeService.getType(documentTableDataRef);
			QName tableDataAssocType = associationRef.getTypeQName();
			//Создание результирующей записи, если она ещё не была создана
			List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(documentRef, tableDataType, tableDataAssocType, true);
			documentTableService.recalculateTotalRows(documentRef, totalRows, tableDataType, tableDataAssocType, null);
            //Присвоени максимального индекса
            documentTableService.setIndexTableRow(documentRef, documentTableDataRef, tableDataAssocType);
		}
	}

	/**
	 * Выполнение действий после удаления ассоциации между документом и строкой табличных данных
	 */
	public void onDeleteTableDataRow(AssociationRef associationRef) {
		NodeRef documentRef = associationRef.getSourceRef();
		NodeRef documentTableDataRef = associationRef.getTargetRef();

		if (documentTableService.isDocumentTableData(documentTableDataRef)) {
			QName tableDataType = nodeService.getType(documentTableDataRef);
			QName tableDataAssocType = associationRef.getTypeQName();

			//Пересчёт результирующих строк
			List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(documentRef, tableDataType, tableDataAssocType, true);
			documentTableService.recalculateTotalRows(documentRef, totalRows, tableDataType, tableDataAssocType, null);
		}
	}
}
