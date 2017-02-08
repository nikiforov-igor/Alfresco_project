package ru.it.lecm.base.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.QName;

import java.util.ArrayList;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 23.07.2014
 * Time: 17:09
 */
@Deprecated
public class ExpressionNode {
    private NodeRef nodeRef;
    private ServiceRegistry serviceRegistry;

    public ExpressionNode() {

    }

    public ExpressionNode(NodeRef nodeRef, ServiceRegistry serviceRegistry) {
        this.nodeRef = nodeRef;
        this.serviceRegistry = serviceRegistry;
    }

    public NodeRef getNodeRef() {
        return nodeRef;
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

    /**
     * Возвращает тип объекта, на который указывает ассоциация
     * @param assocName имя ассоциации
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
     * Проверка на наличие у документа указанного аспекта
     * @param aspectName имя аспекта в виде prefix:localName
     * @return true если аспект навешен на документ, false в противном случае
     * @throws org.alfresco.service.namespace.InvalidQNameException
     * @throws org.alfresco.service.namespace.NamespaceException
     */
    public boolean hasAspect(final String aspectName) throws InvalidQNameException, NamespaceException {
        QName aspectTypeQName = QName.createQName(aspectName, serviceRegistry.getNamespaceService());
        return serviceRegistry.getNodeService().hasAspect(nodeRef, aspectTypeQName);
    }
}
