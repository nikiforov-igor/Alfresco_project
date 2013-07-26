package ru.it.lecm.statemachine.editor.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.editor.StatemachineEditorModel;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 18.02.13
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class XMLExporter {
    private NodeService nodeService;
    private XMLStreamWriter xmlw;

    private static final Set<QName> ignoredQNames = new HashSet<QName>();

    static {
        ignoredQNames.add(StatemachineEditorModel.PROP_STATUS_UUID);
    }

    public XMLExporter(OutputStream resOutputStream, NodeService nodeService) throws XMLStreamException {
        this.xmlw = XMLOutputFactory.newInstance().createXMLStreamWriter(resOutputStream);
        this.nodeService = nodeService;
    }

    public void write(String statusesNodeRef) throws XMLStreamException {
        XMLNode xmlStateMachine = createStateMachineXMLNode(statusesNodeRef);

        xmlw.writeStartDocument("1.0");
        xmlw.writeStartElement(ExportNamespace.STATE_MACHINE);

        writeXMLNode(xmlStateMachine);

        xmlw.writeEndElement();
        xmlw.writeEndDocument();
    }

    public void close() throws XMLStreamException {
        xmlw.close();
    }

    private XMLNode createStateMachineXMLNode(String statusesNodeRef) {
        NodeRef stateMachineNodeRef = nodeService.getPrimaryParent(new NodeRef(statusesNodeRef)).getParentRef();
        XMLNode xmlStateMachine = createXMLNode(stateMachineNodeRef);

        List<XMLNode> xmlStatuses = getFolderChildren(new NodeRef(statusesNodeRef));
        for (XMLNode xmlStatus : xmlStatuses) {
            xmlStateMachine.addSubFolderNode(ExportNamespace.STATUSES, xmlStatus);

            //actions
            NodeRef actionsNodeRef = nodeService.getChildByName(xmlStatus.getNodeRef(), ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.ACTIONS);
            List<XMLNode> xmlActions = getFolderChildren(actionsNodeRef);
            for (XMLNode xmlAction : xmlActions) {
                xmlStatus.addSubFolderNode(ExportNamespace.ACTIONS, xmlAction);

                List<XMLNode> xmlTransitions = getFolderChildren(xmlAction.getNodeRef());
                for (XMLNode xmlTransition : xmlTransitions) {
                    xmlAction.addSubFolderNode(ExportNamespace.TRANSITIONS, xmlTransition);

                    List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(xmlTransition.getNodeRef(), StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
                    for (AssociationRef targetAssoc : targetAssocs) {
                        XMLAssociation xmlAssociation = new XMLAssociation(targetAssoc.getTypeQName().getLocalName(), targetAssoc.getTargetRef().toString());
                        xmlTransition.addAssociation(xmlAssociation);
                    }

                    List<XMLNode> variables = getFolderChildren(xmlTransition.getNodeRef());
                    xmlTransition.addSubFolderNodes(ExportNamespace.VARIABLES, variables);
                }
            }

            //associations
            List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(xmlStatus.getNodeRef(), StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
            for (AssociationRef targetAssoc : targetAssocs) {
                XMLAssociation xmlAssociation = new XMLAssociation(targetAssoc.getTypeQName().getLocalName(), targetAssoc.getTargetRef().toString());
                xmlStatus.addAssociation(xmlAssociation);
            }

            //roles
            NodeRef rolesNodeRef = nodeService.getChildByName(xmlStatus.getNodeRef(), ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.ROLES);
            if (rolesNodeRef != null) {
                NodeRef staticRolesNodeRef = nodeService.getChildByName(rolesNodeRef, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.STATIC_ROLES);
                List<XMLNode> xmlRoles = getXmlRoles(staticRolesNodeRef);
                xmlStatus.addSubFolderNodes(ExportNamespace.STATIC_ROLES, xmlRoles);

                NodeRef dynamicRolesNodeRef = nodeService.getChildByName(rolesNodeRef, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.DYNAMIC_ROLES);
                xmlRoles = getXmlRoles(dynamicRolesNodeRef);
                xmlStatus.addSubFolderNodes(ExportNamespace.DYNAMIC_ROLES, xmlRoles);
            }

            //fields
            NodeRef fieldsNodeRef = nodeService.getChildByName(xmlStatus.getNodeRef(), ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.FIELDS);
            List<XMLNode> fields = getFolderChildren(fieldsNodeRef);
            xmlStatus.addSubFolderNodes(ExportNamespace.FIELDS, fields);

            //categories
            NodeRef categoriesNodeRef = nodeService.getChildByName(xmlStatus.getNodeRef(), ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.CATEGORIES);
            List<XMLNode> categories = getFolderChildren(categoriesNodeRef);
            xmlStatus.addSubFolderNodes(ExportNamespace.CATEGORIES, categories);
        }

        //roles
        NodeRef rolesNodeRef = nodeService.getChildByName(stateMachineNodeRef, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.ROLES_LIST);
        List<XMLNode> xmlRoles = getXmlRoles(rolesNodeRef);
        xmlStateMachine.addSubFolderNodes(ExportNamespace.ROLES, xmlRoles);

        //alternatives
        NodeRef alternativesNodeRef = nodeService.getChildByName(stateMachineNodeRef, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.ALTERNATIVES);
        List<XMLNode> xmlAlternatives = getXmlAlternatives(alternativesNodeRef);
        xmlStateMachine.addSubFolderNodes(ExportNamespace.ALTERNATIVES, xmlAlternatives);

        return xmlStateMachine;
    }

    private List<XMLNode> getFolderChildren(NodeRef folderNodeRef) {
        if (folderNodeRef == null) {
            return new ArrayList<XMLNode>();
        }

        List<XMLNode> result = new ArrayList<XMLNode>();
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(folderNodeRef);
        for (ChildAssociationRef childAssoc : childAssocs) {
            NodeRef childNodeRef = childAssoc.getChildRef();
            XMLNode child = createXMLNode(childNodeRef);
            result.add(child);
        }

        return result;
    }

    private List<XMLNode> getXmlAlternatives(NodeRef alternativesNodeRef) {
        List<XMLNode> xmlAlternatives = getFolderChildren(alternativesNodeRef);

        for (XMLNode xmlAlternative : xmlAlternatives) {
            List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(xmlAlternative.getNodeRef(), StatemachineEditorModel.ASSOC_ALTERNATIVE_STATUS);
            for (AssociationRef targetAssoc : targetAssocs) {
                XMLAssociation assoc = new XMLAssociation(targetAssoc.getTypeQName().getLocalName(), targetAssoc.getTargetRef().toString());
                xmlAlternative.addAssociation(assoc);
            }
        }

        return xmlAlternatives;
    }

    private List<XMLNode> getXmlRoles(NodeRef rolesNodeRef) {
        List<XMLNode> xmlRoles = getFolderChildren(rolesNodeRef);

        for (XMLNode xmlRole : xmlRoles) {
            List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(xmlRole.getNodeRef(), StatemachineEditorModel.ASSOC_ROLE);
            for (AssociationRef targetAssoc : targetAssocs) {
                String businessRoleIdentifier = (String) nodeService.getProperty(targetAssoc.getTargetRef(), OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
                XMLRoleAssociation xmlRoleAssociation = new XMLRoleAssociation(targetAssoc.getTypeQName().getLocalName(), businessRoleIdentifier);
                xmlRole.addRoleAssociation(xmlRoleAssociation);
            }
        }

        return xmlRoles;
    }

    private XMLNode createXMLNode(NodeRef nodeRef) {
        String name = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        QName type = nodeService.getType(nodeRef);
        XMLNode xmlNode = new XMLNode(type.getLocalName(), name, nodeRef);

        //properties
        Map<QName, Serializable> allProperties = nodeService.getProperties(nodeRef);
        Set<QName> stateMachineEditorQNames = filterStateMachineEditorQNames(allProperties.keySet());

        for (QName qName : stateMachineEditorQNames) {
            String propertyValue = allProperties.get(qName).toString();
            if (NodeRef.isNodeRef(propertyValue)) {
                continue;
            }
            XMLProperty xmlProperty = new XMLProperty(qName.getLocalName(), propertyValue);
            xmlNode.addProperty(xmlProperty);
        }

        // aspects
        Set<QName> allAspects = nodeService.getAspects(nodeRef);
        Set<QName> stateMachineEditorAspects = filterStateMachineEditorQNames(allAspects);
        for (QName aspect : stateMachineEditorAspects) {
            xmlNode.addAspect(aspect.getLocalName());
        }

        return xmlNode;
    }

    private void writeXMLNode(XMLNode xmlNode) throws XMLStreamException {
        xmlw.writeStartElement(ExportNamespace.NODE);

        writeElement(ExportNamespace.TYPE, xmlNode.getType());
        writeElement(ExportNamespace.NAME, xmlNode.getName());
        writeElement(ExportNamespace.NODE_REF, xmlNode.getNodeRefString());

        xmlw.writeStartElement(ExportNamespace.PROPERTIES);
        for (XMLProperty xmlProperty : xmlNode.getProperties()) {
            xmlw.writeStartElement(ExportNamespace.PROPERTY);
            writeElement(ExportNamespace.NAME, xmlProperty.getName());
            writeData(ExportNamespace.VALUE, xmlProperty.getValue());
            xmlw.writeEndElement();
        }
        xmlw.writeEndElement();

        xmlw.writeStartElement(ExportNamespace.ASPECTS);
        for (String aspect : xmlNode.getAspects()) {
            xmlw.writeEmptyElement(aspect);
        }
        xmlw.writeEndElement();

        xmlw.writeStartElement(ExportNamespace.ASSOCIATIONS);
        for (XMLAssociation xmlAssociation : xmlNode.getAssociations()) {
            xmlw.writeStartElement(ExportNamespace.ASSOCIATION);
            writeElement(ExportNamespace.TYPE, xmlAssociation.getType());
            writeElement(ExportNamespace.REFERENCE, xmlAssociation.getReference());
            xmlw.writeEndElement();
        }
        xmlw.writeEndElement();

        xmlw.writeStartElement(ExportNamespace.ROLE_ASSOCIATIONS);
        for (XMLRoleAssociation xmlRoleAssociation : xmlNode.getRoleAssociations()) {
            xmlw.writeStartElement(ExportNamespace.ROLE_ASSOCIATION);
            writeElement(ExportNamespace.TYPE, xmlRoleAssociation.getType());
            writeElement(ExportNamespace.BUSINESS_ROLE_NAME, xmlRoleAssociation.getBusinessRoleName());
            xmlw.writeEndElement();
        }
        xmlw.writeEndElement();

        xmlw.writeStartElement(ExportNamespace.SUB_FOLDERS);
        for (Map.Entry<String, List<XMLNode>> subFolder : xmlNode.getSubFolders().entrySet()) {
            xmlw.writeStartElement(ExportNamespace.SUB_FOLDER);
            writeElement(ExportNamespace.NAME, subFolder.getKey());
            xmlw.writeStartElement(ExportNamespace.NODES);
            for (XMLNode childNode : subFolder.getValue()) {
                writeXMLNode(childNode);
            }
            xmlw.writeEndElement();
            xmlw.writeEndElement();
        }
        xmlw.writeEndElement();

        xmlw.writeEndElement();
    }

    private Set<QName> filterStateMachineEditorQNames(Collection<QName> qNames) {
        Set<QName> result = new HashSet<QName>();

        for (QName qName : qNames) {
            if (qName.getNamespaceURI().equals(StatemachineEditorModel.STATEMACHINE_EDITOR_URI) && !ignoredQNames.contains(qName)) {
                result.add(qName);
            }
        }

        return result;
    }

    private void writeElement(String name, String value) throws XMLStreamException {
        xmlw.writeStartElement(name);
        xmlw.writeCharacters(value);
        xmlw.writeEndElement();
    }

    private void writeData(String name, String value) throws XMLStreamException {
        xmlw.writeStartElement(name);
        xmlw.writeCData(value);
        xmlw.writeEndElement();
    }

}