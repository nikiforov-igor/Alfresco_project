package ru.it.lecm.documents.expression;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final transient Logger logger = LoggerFactory.getLogger(ExpressionDocument.class);
    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

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
     * @param searchByPartMatches искать по частичному совпадению
     * @param props свойства, по которым ищутся дубликаты (в виде prefix:localName)
     * @return true если в системе есть дубликат документа
     */
    public boolean hasDuplicates(boolean searchByPartMatches, String... props) {
        if (props != null && props.length > 0) {
            JSONArray searchFields = new JSONArray();
            for (String prop : props) {
                JSONObject fieldObj = new JSONObject();
                try {
                    fieldObj.put("name", prop);
                    fieldObj.put("exactMatch", !searchByPartMatches);
                    searchFields.put(fieldObj);
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            return hasDuplicates(searchFields.toString());
        }
        return false;
    }

    /**
     * Проверка на наличие у документа дубликатов
     * @param searchFieldsJSON свойства и их настройки
     * @return true если в системе есть дубликат документа
     */
    public boolean hasDuplicates(String searchFieldsJSON) {
        try {
            NodeService nodeService = serviceRegistry.getNodeService();
            StringBuilder filters = new StringBuilder();

            JSONArray searchFields = new JSONArray(searchFieldsJSON);
            for (int i = 0; i < searchFields.length(); i++) {
                JSONObject searchField = searchFields.getJSONObject(i);
                String propName = searchField.getString("name");
                boolean searchByExactMatch = searchField.has("exactMatch") && searchField.getBoolean("exactMatch");

                Serializable propValue = nodeService.getProperty(this.nodeRef, QName.createQName(propName, serviceRegistry.getNamespaceService()));
                if (propValue == null || "".equals(propValue)) {
                    return false;
                }

                if (filters.length() > 0) {
                    filters.append(" AND ");
                }

                String value = propValue.toString();
                if (propValue instanceof Date) {
                    value = YYYY_MM_DD.format(propValue);
                }

                filters.append(searchByExactMatch ? "=" : "").append("@").append(propName.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-"))
                        .append(":\"").append(value).append("\"");
            }

            List<QName> types = new ArrayList<>();
            types.add(nodeService.getType(this.nodeRef));

            List<NodeRef> documents = documentService.getDocumentsByFilter(types, null, null, filters.toString(), null);
            if (documents != null) {
                for (NodeRef document : documents) {
                    if (!document.equals(this.nodeRef) && documentService.getDocumentRegNumber(document) != null) {
                        return true;
                    }
                }
            }
        } catch (JSONException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return false;
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
