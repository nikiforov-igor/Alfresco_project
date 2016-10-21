package ru.it.lecm.documents.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 24.01.13
 * Time: 15:47
 */
public class ExpressionDocument {

	private NodeRef nodeRef;
	private ServiceRegistry serviceRegistry;
    private static DocumentAttachmentsService documentAttachmentsService;
    private static DocumentConnectionService documentConnectionService;
    private static DocumentTableService documentTableService;
	private static DocumentService documentService;
    private static StateMachineServiceBean stateMachineService;

    public ExpressionDocument() {

    }

    public ExpressionDocument(NodeRef nodeRef, ServiceRegistry serviceRegistry) {
		this.nodeRef = nodeRef;
		this.serviceRegistry = serviceRegistry;
	}

    /**
     * Получить NodeRef документа
     * @return NodeRef документа
     */
	public NodeRef getNodeRef() {
		return nodeRef;
	}

    /**
     * Получить тип документа
     * @return prefixed тип документа
     */
    public String type() {
        return serviceRegistry.getNodeService().getType(nodeRef).toPrefixString(serviceRegistry.getNamespaceService());
    }

    /**
     * Получить название типа документа
     * @return название тип документа
     */
    public String typeTitle() {
        return documentService.getDocumentTypeLabel(type());
    }

    /**
     * Получить значение атрибута
     * @param attributeName имя атрибута (в виде prefix:localName)
     * @return значение атрибута
     */
	public Object attr(String attributeName) {
		QName attribute = QName.createQName(attributeName, serviceRegistry.getNamespaceService());
		return serviceRegistry.getNodeService().getProperty(nodeRef, attribute);
	}

    /**
     * Получить значение ассоцации
     * @param assocName имя TARGET ассоциации (в виде prefix:localName)
     * @return значение ассоациации
     */
    public NodeRef assoc(String assocName) {
        List<NodeRef> assocs = assocList(assocName);
        return !assocs.isEmpty() ? assocs.get(0) : null;
    }

    /**
     * Получить значение ассоциации
     * @param  assocName TARGET ассоциация (в виде prefix:localName)
     * @return значение ассоациации
     */
    public List<NodeRef> assocList(String assocName) {
        QName assocTypeName = QName.createQName(assocName, serviceRegistry.getNamespaceService());
        List<NodeRef> assocs = new ArrayList<>();

        List<AssociationRef> associationRefs = serviceRegistry.getNodeService().getTargetAssocs(nodeRef, assocTypeName);
        for (AssociationRef associationRef : associationRefs) {
            NodeRef assocNodeRef = associationRef.getTargetRef();
            if (assocNodeRef != null) {
                assocs.add(assocNodeRef);
            }
        }
        return assocs;
    }

    /**
     * Получить значение атрибута ассоциации
     * @param assocName имя ассоциации (в виде prefix:localName)
     * @param attributeName имя атрибута (в виде prefix:localName)
     * @return значение атрибута ассоциации
     */
    public Object assocAttr(String assocName, String attributeName) {
        List<Object> attrs = assocAttrs(assocName, attributeName);
        return !attrs.isEmpty() ? attrs.get(0) : null;
    }

    /**
     * Проверить, что атрибута содержит одно из перечисленных значений
     * @param attributeName имя атрибута (в виде prefix:localName)
     * @param attrValues проверяемые значения
     * @return true, если содержит, false- в обратном случае или если значение атрибута равно NULL
     */
    public Boolean attrContains(String attributeName, String... attrValues) {
        QName attribute = QName.createQName(attributeName, serviceRegistry.getNamespaceService());
        Object attributeValue = serviceRegistry.getNodeService().getProperty(nodeRef, attribute);
        if (attributeValue != null) {
            String strAttrValue = (String) attributeValue;
            if (attrValues != null) {
                if (attrValues.length == 1 && attrValues[0].startsWith("(")) {
                    attrValues = attrValues[0].substring(1, attrValues[0].length() - 1).split(",");
                }
                for (String value: attrValues) {
                    if (value.length() == 0 || strAttrValue.contains(value)) {
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает тип объекта, на который указывает ассоциация
     * @param assocName имя ассоциации (в виде prefix:localName)
     * @return тип объекта, на который указывает ассоциация или NULL, если пусто
     */
    public String assocClass(String assocName) {
        String assocClazz = null;
        QName assocTypaName = QName.createQName(assocName, serviceRegistry.getNamespaceService());
        List<AssociationRef> associationRefs = serviceRegistry.getNodeService().getTargetAssocs(nodeRef, assocTypaName);
        for (AssociationRef associationRef : associationRefs) {
            NodeRef assocNodeRef = associationRef.getTargetRef();
            if (assocNodeRef != null) {
                assocClazz = serviceRegistry.getNodeService().getType(assocNodeRef).toPrefixString(serviceRegistry.getNamespaceService());
                break;
            }
        }
        return assocClazz;
    }

    /**
     * Получить список значений атрибута ассоциации
     * @param assocName имя ассоциации (в виде prefix:localName)
     * @param attributeName имя атрибута (в виде prefix:localName)
     * @return значение атрибута ассоциации
     */
    public List<Object> assocAttrs(String assocName, String attributeName) {
        QName assocTypaName = QName.createQName(assocName, serviceRegistry.getNamespaceService());
        QName attributeTypeName = QName.createQName(attributeName, serviceRegistry.getNamespaceService());
        List<Object> attributs = new ArrayList<Object>();

        List<AssociationRef> associationRefs = serviceRegistry.getNodeService().getTargetAssocs(nodeRef,assocTypaName);
        for (AssociationRef associationRef : associationRefs) {
            NodeRef assocNodeRef = associationRef.getTargetRef();
            if (assocNodeRef != null) {
                if (serviceRegistry.getNodeService().getProperty(assocNodeRef, attributeTypeName) != null) {
                    attributs.add(serviceRegistry.getNodeService().getProperty(assocNodeRef, attributeTypeName));
                }
            }
        }
        return attributs;
    }

    /**
     * Получить список значений атрибута табличных данных
     * @param tableName имя ассоциации на табличные данные (в виде prefix:localName)
     * @param attributeName имя атрибута (в виде prefix:localName)
     * @return список значений атрибута табличных данных
     */
    public List<Object> tableItemsAttrs(String tableName, String attributeName) {
        QName tableTypeName = QName.createQName(tableName, serviceRegistry.getNamespaceService());
        QName attributeTypeName = QName.createQName(attributeName, serviceRegistry.getNamespaceService());
        List<Object> attributes = new ArrayList<Object>();

        NodeRef table = documentTableService.getTable(nodeRef, tableTypeName);
        if (table != null) {
            List<NodeRef> tableItemsRefs = documentTableService.getTableDataRows(table);
            if (tableItemsRefs != null) {
                for (NodeRef tableItemsRef : tableItemsRefs) {
                    Object attr = serviceRegistry.getNodeService().getProperty(tableItemsRef, attributeTypeName);
                    if (attr != null) {
                        attributes.add(attr);
                    }
                }
            }
        }

        return attributes;
    }

    /**
     * Проверяет категорию на наличие вложений
     * @param attachmentCategory - имя категории
     * @return true - если в категории есть вложения
     */
	public boolean hasCategoryAttachment(String attachmentCategory) {
        return !documentAttachmentsService.getAttachmentsByCategory(nodeRef, attachmentCategory).isEmpty();
	}

    /**
     * Проверяет категорию на возможность записи в неё
     * @param categoryName - имя категории
     * @return true - если категория read-only или не существует
     */
    public boolean isReadOnlyCategory(String categoryName) {
        NodeRef category = documentAttachmentsService.getCategory(categoryName, nodeRef);
        return category == null || documentAttachmentsService.isReadonlyCategory(category);
    }

    /**
     * Проверка наличия связанного документа(-ов) с определенным типом и определенной связью
     * @param connectionType - тип связи (код)
     * @param documentType - тип документа (в виде prefix:localName)
     * @return true - если есть хотя бы один не финальный документ, связанный требуемой связью
     */
    public boolean hasConnectionDocuments(String connectionType, String documentType) {
        QName documentTypeQName = QName.createQName(documentType, serviceRegistry.getNamespaceService());
        List<NodeRef> connectedDocuments = documentConnectionService.getConnectedDocuments(nodeRef, connectionType, documentTypeQName);
        if (connectedDocuments != null) {
            for (NodeRef document: connectedDocuments) {
                if (!stateMachineService.isFinal(document)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Получение имени предыдущего статуса документа
     * @return имя предыдущего статуса
     */
    public String getPreviousStatusName() {
        List<String> previousStatusesNames = stateMachineService.getPreviousStatusesNames(nodeRef);
        return previousStatusesNames.size() > 1 ? previousStatusesNames.get(1) : null;
    }

    /**
     * Проверка на наличие у документа дубликатов
     * @param onlyHasRegDat не используется
     * @param props свойства, по которым ищутся дубликаты (в виде prefix:localName)
     * @return true если в системе есть дубликат документа
     */
	public boolean hasDuplicates(boolean onlyHasRegDat, String... props) {
		NodeService nodeService = serviceRegistry.getNodeService();

		List<QName> types = new ArrayList<QName>();
		types.add(nodeService.getType(this.nodeRef));

		StringBuilder filters = new StringBuilder();
        List<QName> properties = new ArrayList<QName>();
		if (props != null) {
			for (String prop: props) {
				QName propQName = QName.createQName(prop, serviceRegistry.getNamespaceService());
                properties.add(propQName);
			}
		}
        //проверяем свойсва на наличие пустых значений
        boolean hasEmptyProperty = false;
        for (QName property : properties) {
            Serializable value = nodeService.getProperty(this.nodeRef, property);
            hasEmptyProperty = hasEmptyProperty || value == null || "".equals(value);
        }

        if (!properties.isEmpty() && !hasEmptyProperty) {
            for (String prop: props) {
                QName propQName = QName.createQName(prop, serviceRegistry.getNamespaceService());
                properties.add(propQName);
                Serializable propValue = nodeService.getProperty(this.nodeRef, propQName);
                if (propValue != null) {
                    if (filters.length() > 0) {
                        filters.append(" AND ");
                    }
                    filters.append("@").append(prop.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-"))
                            .append(":\"").append(propValue).append("\"");
                }
            }

            List<NodeRef> documents = documentService.getDocumentsByFilter(types, null, null, filters.toString(), null);
            List<NodeRef> filteredDocuments = new ArrayList<NodeRef>();
            if (documents != null) {
                for (NodeRef document: documents) {
                    if (!document.equals(this.nodeRef) && documentService.getDocumentRegNumber(document) != null) {
                        filteredDocuments.add(document);
                    }
                }
            }

		    return !filteredDocuments.isEmpty();
        } else {
            return false;
        }
	}

	/**
	 * Проверка на наличие у документа указанного аспекта
	 * @param aspectName имя аспекта в виде prefix:localName
	 * @return true если аспект навешен на документ, false в противном случае
	 * @throws InvalidQNameException
	 * @throws NamespaceException
	 */
	public boolean hasAspect(final String aspectName) throws InvalidQNameException, NamespaceException {
		QName aspectTypeQName = QName.createQName(aspectName, serviceRegistry.getNamespaceService());
		return serviceRegistry.getNodeService().hasAspect(nodeRef, aspectTypeQName);
	}

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        ExpressionDocument.documentAttachmentsService = documentAttachmentsService;
    }

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		ExpressionDocument.documentConnectionService = documentConnectionService;
	}

    public void setDocumentTableService(DocumentTableService documentTableService) {
        ExpressionDocument.documentTableService = documentTableService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineHelper) {
		ExpressionDocument.stateMachineService = stateMachineHelper;
	}

	public void setDocumentService(DocumentService documentService) {
		ExpressionDocument.documentService = documentService;
	}
}
