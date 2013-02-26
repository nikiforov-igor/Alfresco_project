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
public class XMLWriter {
    public static final String STATUSES = "statuses";
    public static final String ACTIONS = "actions";
    public static final String STATE_MACHINE = "stateMachine";
    public static final String TRANSITIONS = "transitions";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String NODE_REF = "nodeRef";
    public static final String PROPERTIES = "properties";
    public static final String ASPECTS = "aspects";
    public static final String ASSOCIATIONS = "associations";
    public static final String ASSOCIATION = "association";
    public static final String REFERENCE = "reference";
    public static final String CHILD = "child";
    public static final String ROLES = "roles";
    private NodeService nodeService;
    private XMLStreamWriter xmlw;

    public XMLWriter(OutputStream resOutputStream, NodeService nodeService) throws XMLStreamException {
        this.xmlw = XMLOutputFactory.newInstance().createXMLStreamWriter(resOutputStream);
        this.nodeService = nodeService;
    }

    public void write(String statusesNodeRef) throws XMLStreamException {
        XMLNode xmlStateMachine = createStateMachineXMLNode(statusesNodeRef);

        xmlw.writeStartDocument("1.0");
        xmlw.writeStartElement(STATE_MACHINE);

        writeXMLNode(xmlStateMachine);
        writeRoles();
        writeRoles();
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
            xmlStateMachine.addSubFolderNode(STATUSES, xmlStatus);

            //actions
            NodeRef actionsNodeRef = nodeService.getChildByName(statusNodeRef, ContentModel.ASSOC_CONTAINS, ACTIONS);
            List<ChildAssociationRef> actionAssocs = nodeService.getChildAssocs(actionsNodeRef);
            for (ChildAssociationRef actionAssoc : actionAssocs) {
                NodeRef actionNodeRef = actionAssoc.getChildRef();
                XMLNode xmlAction = createXMLNode(actionNodeRef);
                xmlStatus.addSubFolderNode(ACTIONS, xmlAction);

                List<ChildAssociationRef> transitionAssocs = nodeService.getChildAssocs(actionNodeRef);
                for (ChildAssociationRef transitionAssoc : transitionAssocs) {
                    NodeRef transitionNodeRef = transitionAssoc.getChildRef();
                    XMLNode xmlTransition = createXMLNode(transitionNodeRef);
                    xmlAction.addSubFolderNode(TRANSITIONS, xmlTransition);

                    List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(transitionNodeRef, StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
                    for (AssociationRef targetAssoc : targetAssocs) {
                        XMLAssociation xmlAssociation = new XMLAssociation(targetAssoc.getTypeQName().getLocalName(), targetAssoc.getTargetRef().toString());
                        xmlTransition.addAssociation(xmlAssociation);
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
            XMLProperty xmlProperty = new XMLProperty(qName.getLocalName(), "", allProperties.get(qName).toString());
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
        writeElement(TYPE, xmlNode.getType());
        writeElement(NAME, xmlNode.getName());
        writeElement(NODE_REF, xmlNode.getNodeRef());

        xmlw.writeStartElement(PROPERTIES);
        for (XMLProperty xmlProperty : xmlNode.getProperties()) {
            writeElement(xmlProperty.getName(), xmlProperty.getValue());
        }
        xmlw.writeEndElement();

        xmlw.writeStartElement(ASPECTS);
        for (String aspect : xmlNode.getAspects()) {
            xmlw.writeEmptyElement(aspect);
        }
        xmlw.writeEndElement();

        xmlw.writeStartElement(ASSOCIATIONS);
        for (XMLAssociation xmlAssociation : xmlNode.getAssociations()) {
            xmlw.writeStartElement(ASSOCIATION);
            writeElement(TYPE, xmlAssociation.getType());
            writeElement(REFERENCE, xmlAssociation.getReference());
            xmlw.writeEndElement();
        }
        xmlw.writeEndElement();

        for (Map.Entry<String, List<XMLNode>> subFolder : xmlNode.getSubFolders().entrySet()) {
            xmlw.writeStartElement(subFolder.getKey());
            for (XMLNode childNode : subFolder.getValue()) {
                xmlw.writeStartElement(CHILD);
                writeXMLNode(childNode);
                xmlw.writeEndElement();
            }
            xmlw.writeEndElement();
        }
    }

    private void writeRoles() throws XMLStreamException {
        xmlw.writeStartElement(ROLES);
        xmlw.writeAttribute("stub", "stub");
        xmlw.writeEndElement();
    }

    private Set<QName> filterStateMachineEditorQNames(Collection<QName> qNames) {
        Set<QName> result = new HashSet<QName>();

        for (QName qName : qNames) {
            //TO DO: how to filter lecm properties properly?
            // with getNamespaceURI()
            //only stm_editor
            if (qName.getNamespaceURI().contains("logicECM")) {
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

    private class XMLProperty {
        private String name;
        private String type;
        private String value;

        private XMLProperty(String name, String type, String value) {
            if (name == null) {
                throw new IllegalArgumentException("name cannot be null!");
            }

            if (type == null) {
                throw new IllegalArgumentException("type cannot be null!");
            }

            this.name = name;
            this.type = type;
            this.value = value != null ? value : "";
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    private class XMLAssociation {
        private String type;
        private String reference;

        private XMLAssociation(String type, String reference) {
            if (type == null) {
                throw new IllegalArgumentException("type cannot be null!");
            }

            if (reference == null) {
                throw new IllegalArgumentException("reference cannot be null!");
            }

            this.type = type;
            this.reference = reference;
        }

        public String getType() {
            return type;
        }

        public String getReference() {
            return reference;
        }
    }

    private class XMLNode {
        private String type;
        private String name;
        private String nodeRef;
        private List<XMLProperty> properties = new ArrayList<XMLProperty>();
        private List<String> aspects = new ArrayList<String>();
        private List<XMLAssociation> associations = new ArrayList<XMLAssociation>();
        private Map<String, List<XMLNode>> subFolders = new HashMap<String, List<XMLNode>>();

        private XMLNode(String type, String name, String nodeRef) {
            if (type == null) {
                throw new IllegalArgumentException("type cannot be null!");
            }

            if (name == null) {
                throw new IllegalArgumentException("name cannot be null!");
            }

            if (nodeRef == null) {
                throw new IllegalArgumentException("nodeRef cannot be null!");
            }

            this.type = type;
            this.name = name;
            this.nodeRef = nodeRef;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getNodeRef() {
            return nodeRef;
        }

        public List<XMLProperty> getProperties() {
            return properties;
        }

        public void addProperty(XMLProperty xmlProperty) {
            properties.add(xmlProperty);
        }

        public List<String> getAspects() {
            return aspects;
        }

        public void addAspect(String aspect) {
            aspects.add(aspect);
        }

        public List<XMLAssociation> getAssociations() {
            return associations;
        }

        public void addAssociation(XMLAssociation xmlAssociation) {
            associations.add(xmlAssociation);
        }

        public List<XMLNode> getSubFolderNodes(String subFolderName) {
            return subFolders.get(subFolderName);
        }

        public Map<String, List<XMLNode>> getSubFolders() {
            return subFolders;
        }

        public void addSubFolderNode(String subFolderName, XMLNode xmlNode) {
            if (!subFolders.containsKey(subFolderName)) {
                subFolders.put(subFolderName, new ArrayList<XMLNode>());
            }

            List<XMLNode> xmlNodes = subFolders.get(subFolderName);
            xmlNodes.add(xmlNode);
        }
    }

}