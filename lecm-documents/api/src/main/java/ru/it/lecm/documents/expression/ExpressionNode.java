package ru.it.lecm.documents.expression;

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
 * Date: 06.02.2017
 * Time: 13:38
 */
public class ExpressionNode {

    protected NodeRef nodeRef;
    protected static ServiceRegistry serviceRegistry;

    public ExpressionNode() {

    }

    public ExpressionNode(NodeRef nodeRef) {
        this.nodeRef = nodeRef;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ExpressionNode.serviceRegistry = serviceRegistry;
    }
    /**
     * Получить NodeRef
     * @return NodeRef
     */
    public NodeRef getNodeRef() {
        return nodeRef;
    }

    /**
     * Получить тип
     * @return prefixed тип
     */
    public String type() {
        return serviceRegistry.getNodeService().getType(nodeRef).toPrefixString(serviceRegistry.getNamespaceService());
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
     * Проверка на наличие у документа указанного аспекта
     * @param aspectName имя аспекта в виде prefix:localName
     * @return true если аспект навешен на документ, false в противном случае
     * @throws InvalidQNameException
     * @throws NamespaceException
     */
    public boolean hasAspect(final String aspectName) throws NamespaceException {
        QName aspectTypeQName = QName.createQName(aspectName, serviceRegistry.getNamespaceService());
        return serviceRegistry.getNodeService().hasAspect(nodeRef, aspectTypeQName);
    }
}
