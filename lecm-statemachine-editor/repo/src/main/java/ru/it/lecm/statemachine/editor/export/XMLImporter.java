package ru.it.lecm.statemachine.editor.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.statemachine.editor.StatemachineEditorModel;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 27.02.13
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class XMLImporter {
    public static final String WRONG_XML_FORMAT = "Wrong XML format! ";
    public static final String ACTION_ID_PROPERTY = "actionId";
    public static final String ACTION_EXECUTION_PROPERTY = "actionExecution";
    private NodeService nodeService;

    private XMLStreamReader xmlr;

    private NodeRef stateMachineNodeRef;
    private XMLNode newStateMachine;

    public XMLImporter(InputStream inputStream, RepositoryStructureHelper repositoryHelper, NodeService nodeService, String stateMachineId) throws XMLStreamException {
        this.nodeService = nodeService;

        final NodeRef companyHome = repositoryHelper.getHomeRef();
        NodeRef stateMachinesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.STATEMACHINES);
        this.stateMachineNodeRef = nodeService.getChildByName(stateMachinesRoot, ContentModel.ASSOC_CONTAINS, stateMachineId);

        this.xmlr = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
    }

    public void importStateMachine() throws XMLStreamException {
        this.newStateMachine = readStateMachine();
        if (newStateMachine == null) {
            throw new XMLStreamException(WRONG_XML_FORMAT);
        }

        recreateRolesAndStatusesFolders();
        importStatuses();
        importActions();
    }

    public void close() throws XMLStreamException {
        xmlr.close();
    }

    private XMLNode readStateMachine() throws XMLStreamException {
        XMLNode stateMachine = null;
        while (xmlr.hasNext() && !isEndTag(ExportNamespace.STATE_MACHINE)) {
            xmlr.next();

            if (isStartTag(ExportNamespace.STATE_MACHINE)) {
                xmlr.nextTag();
                stateMachine = readXMLNode();
            }
        }

        return stateMachine;
    }

    //enter on start "node"
    //return on end "node"
    private XMLNode readXMLNode() throws XMLStreamException {
        String type = readTextValue(ExportNamespace.TYPE);
        String name = readTextValue(ExportNamespace.NAME);
        String nodeRef = readTextValue(ExportNamespace.NODE_REF);

        XMLNode xmlNode = new XMLNode(type, name, nodeRef);

        List<XMLProperty> xmlProperties = readProperties();
        for (XMLProperty xmlProperty : xmlProperties) {
            xmlNode.addProperty(xmlProperty);
        }

        List<String> aspects = readAspects();
        for (String aspect : aspects) {
            xmlNode.addAspect(aspect);
        }

        List<XMLAssociation> xmlAssociations = readAssociations();
        for (XMLAssociation xmlAssociation : xmlAssociations) {
            xmlNode.addAssociation(xmlAssociation);
        }

        Map<String, List<XMLNode>> subFolders = readSubFolders();
        for (Map.Entry<String, List<XMLNode>> entry : subFolders.entrySet()) {
            xmlNode.addSubFolder(entry.getKey(), entry.getValue());
        }

        xmlr.nextTag();
        if (!isEndTag(ExportNamespace.NODE)) {
            throw new XMLStreamException(WRONG_XML_FORMAT + "Closing " + ExportNamespace.NODE + " tag not found.");
        }

        return xmlNode;
    }

    //enter on tag before <properties>
    //exit on tag </properties>
    private List<XMLProperty> readProperties() throws XMLStreamException {
        List<XMLProperty> result = new ArrayList<XMLProperty>();

        readStartTag(ExportNamespace.PROPERTIES);
        while (xmlr.hasNext() && !isEndTag(ExportNamespace.PROPERTIES)) {
            xmlr.next();

            if (isStartTag(ExportNamespace.PROPERTY)) {
                String name = readTextValue(ExportNamespace.NAME);
                String value = readTextValue(ExportNamespace.VALUE);
                result.add(new XMLProperty(name, value));
            }
        }

        return result;
    }

    private List<String> readAspects() throws XMLStreamException {
        List<String> result = new ArrayList<String>();

        readStartTag(ExportNamespace.ASPECTS);
        while (xmlr.hasNext() && !isEndTag(ExportNamespace.ASPECTS)) {
            xmlr.next();

            if (XMLStreamConstants.START_ELEMENT == xmlr.getEventType()) {
                String name = xmlr.getLocalName();
                result.add(name);
            }
        }

        return result;
    }

    //enter on tag before <associations>
    //return on </associations>
    private List<XMLAssociation> readAssociations() throws XMLStreamException {
        List<XMLAssociation> result = new ArrayList<XMLAssociation>();

        readStartTag(ExportNamespace.ASSOCIATIONS);
        while (xmlr.hasNext() && !isEndTag(ExportNamespace.ASSOCIATIONS)) {
            xmlr.next();

            if (isStartTag(ExportNamespace.ASSOCIATION)) {
                String type = readTextValue(ExportNamespace.TYPE);
                String reference = readTextValue(ExportNamespace.REFERENCE);
                result.add(new XMLAssociation(type, reference));
            }
        }

        return result;
    }

    //enter on tag before <subFolders>
    private Map<String, List<XMLNode>> readSubFolders() throws XMLStreamException {
        Map<String, List<XMLNode>> result = new HashMap<String, List<XMLNode>>();

        readStartTag(ExportNamespace.SUB_FOLDERS);
        while (xmlr.hasNext() && !isEndTag(ExportNamespace.SUB_FOLDERS)) {
            xmlr.next();

            if (isStartTag(ExportNamespace.SUB_FOLDER)) {
                String subFolderName = readTextValue(ExportNamespace.NAME);
                List<XMLNode> xmlNodes = readNodes();
                result.put(subFolderName, xmlNodes);
            }
        }

        return result;
    }

    private List<XMLNode> readNodes() throws XMLStreamException {
        List<XMLNode> result = new ArrayList<XMLNode>();

        readStartTag(ExportNamespace.NODES);
        while (xmlr.hasNext() && !isEndTag(ExportNamespace.NODES)) {
            xmlr.next();

            if (isStartTag(ExportNamespace.NODE)) {
                XMLNode xmlNode = readXMLNode();
                result.add(xmlNode);
            }
        }

        return result;
    }

    private String readTextValue(String tagName) throws XMLStreamException {
        readStartTag(tagName);
        return StringUtils.trimWhitespace(xmlr.getElementText());
    }

    private boolean isStartTag(String tagName) {
        return XMLStreamConstants.START_ELEMENT == xmlr.getEventType() && tagName.equals(xmlr.getLocalName());
    }

    private boolean isEndTag(String tagName) {
        return XMLStreamConstants.END_ELEMENT == xmlr.getEventType() && tagName.equals(xmlr.getLocalName());
    }

    private void readStartTag(String tagName) throws XMLStreamException {
        xmlr.nextTag();
        if (!isStartTag(tagName)) {
            throw new XMLStreamException(WRONG_XML_FORMAT + tagName + " tag not found.");
        }
    }

    private void recreateRolesAndStatusesFolders() {
        Map<QName, Serializable> properties = getBaseProperties(StatemachineEditorModel.STATUSES);
        recreateNode(stateMachineNodeRef, StatemachineEditorModel.TYPE_STATUSES, properties);

        properties = getBaseProperties(StatemachineEditorModel.ROLES);
        NodeRef rolesNodeRef = recreateNode(stateMachineNodeRef, StatemachineEditorModel.TYPE_ROLES, properties);
        nodeService.setProperty(stateMachineNodeRef, StatemachineEditorModel.PROP_ROLES_FOLDER, rolesNodeRef);
    }

    private Map<QName, Serializable> getBaseProperties(String propName) {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, propName);
        return properties;
    }

    private Map<QName, Serializable> getAllProperties(XMLNode xmlNode) {
        Map<QName, Serializable> properties = getBaseProperties(xmlNode.getName());
        for (XMLProperty xmlProperty : xmlNode.getProperties()) {
            properties.put(QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, xmlProperty.getName()), xmlProperty.getValue());
        }

        return properties;
    }

    /**
     * Delete old statuses and create new ones (all sub-entities are recreated on recreating status)
     */
    private void importStatuses() {
        NodeRef statusesNodeRef = nodeService.getChildByName(stateMachineNodeRef, StatemachineEditorModel.TYPE_STATUSES, StatemachineEditorModel.TYPE_STATUSES.getLocalName());

        List<XMLNode> statusesXMLNode = newStateMachine.getSubFolder(ExportNamespace.STATUSES);
        for (XMLNode statusXMLNode : statusesXMLNode) {
            NodeRef statusNodeRef = recreateNode(statusesNodeRef, statusXMLNode);
            statusXMLNode.setNewNodeRef(statusNodeRef);
        }
    }

    /**
     * Actions are not recreated. Just set new properties to existing actions.
     */
    private void importActions() {
        List<XMLNode> statusXMLNodes = newStateMachine.getSubFolder(ExportNamespace.STATUSES);
        for (XMLNode statusXMLNode : statusXMLNodes) {
            List<ChildAssociationRef> actionAssocs = getActionsAssociationRefs(statusXMLNode);
            for (ChildAssociationRef actionAssoc : actionAssocs) {
                NodeRef actionNodeRef = actionAssoc.getChildRef();
                String actionId = (String) nodeService.getProperty(actionNodeRef, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, ACTION_ID_PROPERTY));
                String actionExecution = (String) nodeService.getProperty(actionNodeRef, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, ACTION_EXECUTION_PROPERTY));

                XMLNode xmlAction = getXmlAction(statusXMLNode, actionId, actionExecution);
                if (xmlAction == null) {
                    continue;
                }

                Map<QName, Serializable> properties = getAllProperties(xmlAction);
                nodeService.setProperties(actionNodeRef, properties);

                List<XMLNode> xmlTransitions = xmlAction.getSubFolder(ExportNamespace.TRANSITIONS);
                for (XMLNode xmlTransition : xmlTransitions) {
                    NodeRef transitionNodeRef = recreateNode(actionNodeRef, xmlTransition);
                    for (XMLAssociation xmlAssociation : xmlTransition.getAssociations()) {
                        XMLNode xmlNode = newStateMachine.findNode(xmlAssociation.getReference());
                        nodeService.createAssociation(transitionNodeRef, xmlNode.getNewNodeRef(), QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, xmlAssociation.getType()));
                    }

                    List<XMLNode> xmlVariables = xmlTransition.getSubFolder(ExportNamespace.VARIABLES);
                    for (XMLNode xmlVariable : xmlVariables) {
                        recreateNode(transitionNodeRef, xmlVariable);
                    }
                }
            }
        }
    }

    private List<ChildAssociationRef> getActionsAssociationRefs(XMLNode statusXMLNode) {
        NodeRef actionsNodeRef = nodeService.getChildByName(statusXMLNode.getNewNodeRef(), ContentModel.ASSOC_CONTAINS, ExportNamespace.ACTIONS);
        return nodeService.getChildAssocs(actionsNodeRef);
    }

    private XMLNode getXmlAction(XMLNode xmlStatus, String actionId, String actionExecution) {
        for (XMLNode xmlAction : xmlStatus.getSubFolder(ExportNamespace.ACTIONS)) {
            XMLProperty actionIdProperty = xmlAction.getProperty(ACTION_ID_PROPERTY);
            if (actionIdProperty == null || !actionIdProperty.getValue().equals(actionId)) {
                continue;
            }

            XMLProperty actionExecutionProperty = xmlAction.getProperty(ACTION_EXECUTION_PROPERTY);
            if (actionExecutionProperty == null || !actionExecutionProperty.getValue().equals(actionExecution)) {
                continue;
            }

            return xmlAction;
        }

        return null;
    }

    private NodeRef recreateNode(NodeRef parentNodeRef, XMLNode xmlNode) {
        Map<QName, Serializable> properties = getAllProperties(xmlNode);
        return recreateNode(parentNodeRef, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, xmlNode.getType()), properties);
    }

    private NodeRef recreateNode(NodeRef parentNodeRef, QName nodeType, Map<QName, Serializable> properties) {
        String name = properties.get(ContentModel.PROP_NAME).toString();
        NodeRef node = nodeService.getChildByName(parentNodeRef, ContentModel.ASSOC_CONTAINS, name);
        if (node != null) {
            nodeService.deleteNode(node);
        }

        node = nodeService.createNode(parentNodeRef, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                nodeType,
                properties).getChildRef();

        return node;
    }

}
