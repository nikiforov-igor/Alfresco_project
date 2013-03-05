package ru.it.lecm.statemachine.editor.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
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
        writeRoles();

        xmlw.writeEndElement();
        xmlw.writeEndDocument();
    }

    public void close() throws XMLStreamException {
        xmlw.close();
    }

    private XMLNode createStateMachineXMLNode(String statusesNodeRef) {
        NodeRef stateMachineNodeRef = nodeService.getPrimaryParent(new NodeRef(statusesNodeRef)).getParentRef();
        XMLNode xmlStateMachine = createXMLNode(stateMachineNodeRef);

        List<ChildAssociationRef> statusAssocs = nodeService.getChildAssocs(new NodeRef(statusesNodeRef));
        for (ChildAssociationRef statusAssoc : statusAssocs) {
            NodeRef statusNodeRef = statusAssoc.getChildRef();

            XMLNode xmlStatus = createXMLNode(statusNodeRef);
            xmlStateMachine.addSubFolderNode(ExportNamespace.STATUSES, xmlStatus);

            //actions
            NodeRef actionsNodeRef = nodeService.getChildByName(statusNodeRef, ContentModel.ASSOC_CONTAINS, ExportNamespace.ACTIONS);
            List<ChildAssociationRef> actionAssocs = nodeService.getChildAssocs(actionsNodeRef);
            for (ChildAssociationRef actionAssoc : actionAssocs) {
                NodeRef actionNodeRef = actionAssoc.getChildRef();
                XMLNode xmlAction = createXMLNode(actionNodeRef);
                xmlStatus.addSubFolderNode(ExportNamespace.ACTIONS, xmlAction);

                List<ChildAssociationRef> transitionAssocs = nodeService.getChildAssocs(actionNodeRef);
                for (ChildAssociationRef transitionAssoc : transitionAssocs) {
                    NodeRef transitionNodeRef = transitionAssoc.getChildRef();
                    XMLNode xmlTransition = createXMLNode(transitionNodeRef);
                    xmlAction.addSubFolderNode(ExportNamespace.TRANSITIONS, xmlTransition);

                    List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(transitionNodeRef, StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
                    for (AssociationRef targetAssoc : targetAssocs) {
                        XMLAssociation xmlAssociation = new XMLAssociation(targetAssoc.getTypeQName().getLocalName(), targetAssoc.getTargetRef().toString());
                        xmlTransition.addAssociation(xmlAssociation);
                    }

                    List<ChildAssociationRef> variableAssocs = nodeService.getChildAssocs(transitionNodeRef);
                    for (ChildAssociationRef variableAssoc : variableAssocs) {
                        NodeRef variableNodeRef = variableAssoc.getChildRef();
                        XMLNode variable = createXMLNode(variableNodeRef);
                        xmlTransition.addSubFolderNode(ExportNamespace.VARIABLES, variable);
                    }
                }
            }
        }

        return xmlStateMachine;
    }

    private XMLNode createXMLNode(NodeRef nodeRef) {
        String name = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        QName type = nodeService.getType(nodeRef);
        XMLNode xmlNode = new XMLNode(type.getLocalName(), name, nodeRef.toString());

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
        writeElement(ExportNamespace.NODE_REF, xmlNode.getNodeRef());

        xmlw.writeStartElement(ExportNamespace.PROPERTIES);
        for (XMLProperty xmlProperty : xmlNode.getProperties()) {
            xmlw.writeStartElement(ExportNamespace.PROPERTY);
            writeElement(ExportNamespace.NAME, xmlProperty.getName());
            writeElement(ExportNamespace.VALUE, xmlProperty.getValue());
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

    private void writeRoles() throws XMLStreamException {
        xmlw.writeStartElement(ExportNamespace.ROLES);
        xmlw.writeAttribute("stub", "stub");
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
}