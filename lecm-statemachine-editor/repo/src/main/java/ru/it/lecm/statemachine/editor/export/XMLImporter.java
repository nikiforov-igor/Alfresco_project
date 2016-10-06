package ru.it.lecm.statemachine.editor.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
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
    private static final transient Logger logger = LoggerFactory.getLogger(XMLImporter.class);

    public static final String WRONG_XML_FORMAT = "Wrong XML format! ";
    public static final String ACTION_ID_PROPERTY = "actionId";
    public static final String ACTION_EXECUTION_PROPERTY = "actionExecution";
    private NodeService nodeService;
    private PermissionService permissionService;
    private DictionaryBean serviceDictionary;

    private XMLStreamReader xmlr;

    private NodeRef stateMachineNodeRef;
    private XMLNode newStateMachine;
    private NodeRef statusesNodeRef;
    private NodeRef rolesNodeRef;
    private NodeRef alternativesNodeRef;
    RepositoryStructureHelper repositoryHelper;

    private Map<String, NodeRef> businessRoles = new HashMap<String, NodeRef>();

    //TODO Refactoring in progress
    public XMLImporter(InputStream inputStream, RepositoryStructureHelper repositoryHelper, NodeService nodeService, DictionaryBean serviceDictionary, String stateMachineId) throws XMLStreamException {
        this(inputStream,repositoryHelper,nodeService,serviceDictionary,null,stateMachineId);
    }

    public XMLImporter(InputStream inputStream, RepositoryStructureHelper repositoryHelper, NodeService nodeService,
                       DictionaryBean serviceDictionary, PermissionService permissionService, String stateMachineId) throws XMLStreamException {

        this.nodeService = nodeService;
        this.serviceDictionary = serviceDictionary;
        this.repositoryHelper = repositoryHelper;
        this.permissionService = permissionService;

        try {
            stateMachineNodeRef = getStateMachineFolder(stateMachineId);
        }  catch (WriteTransactionNeededException e) {
            logger.error(e.getMessage());
        }

        this.xmlr = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
    }
    /**
     * Проверяем наличие дефолтной МС.
     * Если пусто - создаем.
     * @param stateMachineId
     * TODO рефакторинг - разбить на составляющие и запихнуть в какой-нибудь сервисный бин
     * + задействовать его же в statemachine.process.get вебскрипте
     */
    private NodeRef getStateMachineFolder(String stateMachineId) throws WriteTransactionNeededException{

        final NodeRef companyHome = repositoryHelper.getHomeRef();
        NodeRef stateMachinesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.STATEMACHINES);
        if(stateMachinesRoot==null) {
                stateMachinesRoot = repositoryHelper.createFolder(companyHome, StatemachineEditorModel.STATEMACHINES);
                if(permissionService!=null) {
                    permissionService.setInheritParentPermissions(stateMachinesRoot, false);
                }
        }
        //проверяем ноду мс
        NodeRef stateMachineNodeRef = nodeService.getChildByName(stateMachinesRoot, ContentModel.ASSOC_CONTAINS, stateMachineId);
        if (stateMachineNodeRef == null) {
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
            properties.put(ContentModel.PROP_NAME, stateMachineId);
            ChildAssociationRef stateMachineChildAssocRef = nodeService.createNode(stateMachinesRoot, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, stateMachineId), StatemachineEditorModel.TYPE_STATEMACHINE, properties);
            stateMachineNodeRef = stateMachineChildAssocRef.getChildRef();
        }
        return stateMachineNodeRef;
    }

    public void importStateMachine() throws XMLStreamException {
        this.newStateMachine = readStateMachine();
        if (newStateMachine == null) {
            throw new XMLStreamException(WRONG_XML_FORMAT);
        }

        Map<QName, Serializable> xmlProperties = getXmlProperties(newStateMachine);
        for (QName qName : xmlProperties.keySet()) {
            nodeService.setProperty(stateMachineNodeRef, qName, xmlProperties.get(qName));
        }

        this.statusesNodeRef = recreateStatusesFolder();
        this.rolesNodeRef = recreateRolesFolder();
        this.alternativesNodeRef = recreateAlternativesFolder();

        importStatuses();
        importRoles(rolesNodeRef, newStateMachine.getSubFolder(ExportNamespace.ROLES));
        importAlternatives();
    }

    public void close() throws XMLStreamException {
        xmlr.close();
    }

    public NodeRef getStatusesNodeRef() {
        return statusesNodeRef;
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

        XMLNode xmlNode = new XMLNode(type, name, new NodeRef(nodeRef));

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

        List<XMLRoleAssociation> xmlRoleAssociations = readRoleAssociations();
        for (XMLRoleAssociation xmlRoleAssociation : xmlRoleAssociations) {
            xmlNode.addRoleAssociation(xmlRoleAssociation);
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

    private List<XMLRoleAssociation> readRoleAssociations() throws XMLStreamException {
        List<XMLRoleAssociation> result = new ArrayList<XMLRoleAssociation>();

        readStartTag(ExportNamespace.ROLE_ASSOCIATIONS);
        while (xmlr.hasNext() && !isEndTag(ExportNamespace.ROLE_ASSOCIATIONS)) {
            xmlr.next();

            if (isStartTag(ExportNamespace.ROLE_ASSOCIATION)) {
                String type = readTextValue(ExportNamespace.TYPE);
                String businessRoleName = readTextValue(ExportNamespace.BUSINESS_ROLE_NAME);
                result.add(new XMLRoleAssociation(type, businessRoleName));
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

    private NodeRef recreateStatusesFolder() {
        Map<QName, Serializable> properties = getBaseProperties(StatemachineEditorModel.STATUSES);
        return recreateNode(stateMachineNodeRef, StatemachineEditorModel.TYPE_STATUSES, properties);
    }

    private NodeRef recreateRolesFolder() {
        Map<QName, Serializable> properties = getBaseProperties(StatemachineEditorModel.ROLES_LIST);
        NodeRef rolesNodeRef = recreateNode(stateMachineNodeRef, ContentModel.TYPE_FOLDER, properties);
        nodeService.setProperty(stateMachineNodeRef, StatemachineEditorModel.PROP_STATIC_ROLES_FOLDER, rolesNodeRef);
        nodeService.setProperty(stateMachineNodeRef, StatemachineEditorModel.PROP_DYNAMIC_ROLES_FOLDER, rolesNodeRef);
        nodeService.addAspect(rolesNodeRef, ContentModel.ASPECT_TEMPORARY, null);
        return rolesNodeRef;
    }

    private NodeRef recreateAlternativesFolder() {
        Map<QName, Serializable> properties = getBaseProperties(StatemachineEditorModel.ALTERNATIVES);
        NodeRef alternativesRef = recreateNode(stateMachineNodeRef, ContentModel.TYPE_FOLDER, properties);
        nodeService.setProperty(stateMachineNodeRef, StatemachineEditorModel.PROP_ALTERNATIVES_FOLDER, alternativesRef.toString());
        nodeService.addAspect(alternativesRef, ContentModel.ASPECT_TEMPORARY, null);
        return alternativesRef;
    }

    private Map<QName, Serializable> getBaseProperties(String propName) {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, propName);
        return properties;
    }

    private Map<QName, Serializable> getXmlProperties(XMLNode xmlNode) {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        for (XMLProperty xmlProperty : xmlNode.getProperties()) {
            properties.put(QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, xmlProperty.getName()), xmlProperty.getValue());
        }

        return properties;
    }

    private Map<QName, Serializable> getAllProperties(XMLNode xmlNode) {
        Map<QName, Serializable> properties = getBaseProperties(xmlNode.getName());
        Map<QName, Serializable> xmlProperties = getXmlProperties(xmlNode);
        properties.putAll(xmlProperties);

        return properties;
    }

    /**
     * Delete old statuses and create new ones (all sub-entities are recreated on recreating status)
     */
    private void importStatuses() {
        List<XMLNode> statusesXMLNode = newStateMachine.getSubFolder(ExportNamespace.STATUSES);
        for (XMLNode statusXMLNode : statusesXMLNode) {
            NodeRef statusNodeRef = recreateNode(statusesNodeRef, statusXMLNode);
            statusXMLNode.setNewNodeRef(statusNodeRef);

            //roles
            NodeRef rolesNodeRef = nodeService.getChildByName(statusNodeRef, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.ROLES);
            if (rolesNodeRef != null) {
                NodeRef staticRolesNodeRef = nodeService.getChildByName(rolesNodeRef, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.STATIC_ROLES);
                importRoles(staticRolesNodeRef, statusXMLNode.getSubFolder(ExportNamespace.STATIC_ROLES));

                NodeRef dynamicRolesNodeRef = nodeService.getChildByName(rolesNodeRef, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.DYNAMIC_ROLES);
                importRoles(dynamicRolesNodeRef, statusXMLNode.getSubFolder(ExportNamespace.DYNAMIC_ROLES));
            }

            //categories
            Map<QName, Serializable> properties = getBaseProperties(StatemachineEditorModel.CATEGORIES);
            NodeRef categoriesNodeRef = recreateNode(statusNodeRef, ContentModel.TYPE_FOLDER, properties);

            List<XMLNode> categories = statusXMLNode.getSubFolder(ExportNamespace.CATEGORIES);
            for (XMLNode category : categories) {
                recreateNode(categoriesNodeRef, category);
            }

            //fields
            properties = getBaseProperties(StatemachineEditorModel.FIELDS);
            NodeRef fieldsNodeRef = recreateNode(statusNodeRef, ContentModel.TYPE_FOLDER, properties);

            List<XMLNode> fields = statusXMLNode.getSubFolder(ExportNamespace.FIELDS);
            for (XMLNode field : fields) {
                recreateNode(fieldsNodeRef, field);
            }
        }

        List<XMLNode> statusXMLNodes = newStateMachine.getSubFolder(ExportNamespace.STATUSES);
        for (XMLNode statusXMLNode : statusXMLNodes) {
            importAssociation(statusXMLNode);
        }

        importActions();
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
                    xmlTransition.setNewNodeRef(transitionNodeRef);
                    importAssociation(xmlTransition);

                    List<XMLNode> xmlVariables = xmlTransition.getSubFolder(ExportNamespace.VARIABLES);
                    for (XMLNode xmlVariable : xmlVariables) {
                        recreateNode(transitionNodeRef, xmlVariable);
                    }
                }
            }
        }
    }


    private void importAssociation(XMLNode xmlNode) {
        for (XMLAssociation xmlAssociation : xmlNode.getAssociations()) {
            XMLNode referenceXmlNode = newStateMachine.findNode(xmlAssociation.getReference());
            nodeService.createAssociation(xmlNode.getNewNodeRef(), referenceXmlNode.getNewNodeRef(), QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, xmlAssociation.getType()));
        }
    }

    private void importRoles(NodeRef rolesNodeRef, List<XMLNode> roles) {
        // получаем список всех ролей для статуса
        List<AssociationRef> existingRoles = new ArrayList<AssociationRef>();
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(rolesNodeRef);
        for (ChildAssociationRef childAssoc : childAssocs) {
            List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(childAssoc.getChildRef(), StatemachineEditorModel.ASSOC_ROLE);
            for (AssociationRef targetAssoc : targetAssocs) {
                existingRoles.add(targetAssoc);
            }
        }

        for (XMLNode role : roles) {
            NodeRef businessRoleRef = getExistingBusinessRoleRef(role);
            if (businessRoleRef != null) { // роль существует в системе
                for (AssociationRef existingRole : existingRoles) {
                    if (existingRole.getTargetRef().equals(businessRoleRef)) {
                        nodeService.deleteNode(existingRole.getSourceRef()); // удаляем имеющуюся ноду
                    }
                }
                NodeRef roleNodeRef = recreateNode(rolesNodeRef, role);
                nodeService.createAssociation(roleNodeRef, businessRoleRef, StatemachineEditorModel.ASSOC_ROLE);
            }
        }
    }

    private NodeRef getExistingBusinessRoleRef(XMLNode role) {
        for (XMLRoleAssociation roleAssociation : role.getRoleAssociations()) {
            NodeRef businessRoleNodeRef = getBusinessRoleNodeRef(roleAssociation.getBusinessRoleName());
            if (businessRoleNodeRef != null) {
                return businessRoleNodeRef;
            }
        }
        return null;
    }

    private NodeRef getBusinessRoleNodeRef(String businessRoleName) {
        if (!businessRoles.containsKey(businessRoleName)) {
            NodeRef nodeRef = serviceDictionary.getRecordByParamValue(OrgstructureBean.BUSINESS_ROLES_DICTIONARY_NAME, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER, businessRoleName);
            businessRoles.put(businessRoleName, nodeRef);
        }

        return businessRoles.get(businessRoleName);
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

    private void importAlternatives() {
        List<XMLNode> alternativesXMLNode = newStateMachine.getSubFolder(ExportNamespace.ALTERNATIVES);
        for (XMLNode alternativeXMLNode : alternativesXMLNode) {
            NodeRef alternativeNodeRef = recreateNode(alternativesNodeRef, alternativeXMLNode);
            alternativeXMLNode.setNewNodeRef(alternativeNodeRef);
            importAssociation(alternativeXMLNode);
        }
    }

}
