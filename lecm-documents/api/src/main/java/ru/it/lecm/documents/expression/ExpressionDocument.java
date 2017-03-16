package ru.it.lecm.documents.expression;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 24.01.13
 * Time: 15:47
 */
public class ExpressionDocument extends ExpressionNode {

    private static DocumentAttachmentsService documentAttachmentsService;
    private static DocumentConnectionService documentConnectionService;
    private static DocumentTableService documentTableService;
	private static DocumentService documentService;
    private static StateMachineServiceBean stateMachineService;

    public ExpressionDocument() {

    }

    public ExpressionDocument(NodeRef nodeRef) {
        super(nodeRef);
    }

    /**
     * Получить название типа документа
     * @return название тип документа
     */
    public String typeTitle() {
        return documentService.getDocumentTypeLabel(type());
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (String prop: props) {
                QName propQName = QName.createQName(prop, serviceRegistry.getNamespaceService());
                properties.add(propQName);
                Serializable propValue = nodeService.getProperty(this.nodeRef, propQName);
                if (propValue != null) {
                    if (filters.length() > 0) {
                        filters.append(" AND ");
                    }

                    String value = propValue.toString();
                    if (propValue instanceof Date) {
                        value = dateFormat.format(propValue);
                    }

                    filters.append("@").append(prop.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-"))
                            .append(":\"").append(value).append("\"");
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
