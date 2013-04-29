package ru.it.lecm.statemachine.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;

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

    public ExpressionDocument() {

    }

    public ExpressionDocument(NodeRef nodeRef, ServiceRegistry serviceRegistry) {
		this.nodeRef = nodeRef;
		this.serviceRegistry = serviceRegistry;
	}

	//Значение аттрибута
	public Object attr(String attributeName) {
		QName attribute = QName.createQName(attributeName,serviceRegistry.getNamespaceService());
		return serviceRegistry.getNodeService().getProperty(nodeRef, attribute);
	}

    public Object assocAttr(String assocName, String attributeName) {
        QName assocTypaName = QName.createQName(assocName, serviceRegistry.getNamespaceService());
        QName attributeTypeName = QName.createQName(attributeName, serviceRegistry.getNamespaceService());

        List<AssociationRef> associationRefs = serviceRegistry.getNodeService().getTargetAssocs(nodeRef,assocTypaName);
        for (AssociationRef associationRef : associationRefs) {
            NodeRef assocNodeRef = associationRef.getTargetRef();
            if (assocNodeRef != null) {
                return serviceRegistry.getNodeService().getProperty(assocNodeRef, attributeTypeName);
            }
        }
        return null;
    }

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

	//Наличие вложения с определенным типом
	public boolean hasCategoryAttachment(String attachmentCategory) {
        return !documentAttachmentsService.getAttachmentsByCategory(nodeRef, attachmentCategory).isEmpty();
	}

	//Проверка условий на корректность хотя бы у одного из вложений
	public boolean anyAttachmentAttribute(String attributeName, String condition, String value) {
		return true;
	}

	//Проверка условий на корректность у всех вложений
	public boolean allAttachmentAttribute(String attributeName, String condition, String value) {
		return true;
	}

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        ExpressionDocument.documentAttachmentsService = documentAttachmentsService;
    }
}
