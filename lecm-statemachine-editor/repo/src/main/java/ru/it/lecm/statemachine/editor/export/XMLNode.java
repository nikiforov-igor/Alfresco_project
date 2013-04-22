package ru.it.lecm.statemachine.editor.export;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 28.02.13
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */

public class XMLNode {
    private String type;
    private String name;
    private NodeRef nodeRef;
    private NodeRef newNodeRef;
    private List<XMLProperty> properties = new ArrayList<XMLProperty>();
    private List<String> aspects = new ArrayList<String>();
    private List<XMLAssociation> associations = new ArrayList<XMLAssociation>();
    private List<XMLRoleAssociation> roleAssociations = new ArrayList<XMLRoleAssociation>();
    private Map<String, List<XMLNode>> subFolders = new HashMap<String, List<XMLNode>>();

    public XMLNode(String type, String name, NodeRef nodeRef) {
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

    public NodeRef getNodeRef() {
        return nodeRef;
    }

    public String getNodeRefString() {
        return nodeRef.toString();
    }

    public NodeRef getNewNodeRef() {
        return newNodeRef;
    }

    public void setNewNodeRef(NodeRef newNodeRef) {
        this.newNodeRef = newNodeRef;
    }

    public List<XMLProperty> getProperties() {
        return properties;
    }

    public XMLProperty getProperty(String name) {
        for (XMLProperty property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }

        return null;
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

    public List<XMLRoleAssociation> getRoleAssociations() {
        return roleAssociations;
    }

    public void addRoleAssociation(XMLRoleAssociation roleAssociation) {
        roleAssociations.add(roleAssociation);
    }

    public Map<String, List<XMLNode>> getSubFolders() {
        return subFolders;
    }

    public List<XMLNode> getSubFolder(String subFolderName) {
        return subFolders.containsKey(subFolderName) ? subFolders.get(subFolderName) : new ArrayList<XMLNode>();
    }

    public void addSubFolderNodes(String subFolderName, List<XMLNode> xmlNodes) {
        for (XMLNode xmlNode : xmlNodes) {
            addSubFolderNode(subFolderName, xmlNode);
        }
    }

    public void addSubFolderNode(String subFolderName, XMLNode xmlNode) {
        if (!subFolders.containsKey(subFolderName)) {
            subFolders.put(subFolderName, new ArrayList<XMLNode>());
        }

        List<XMLNode> xmlNodes = subFolders.get(subFolderName);
        xmlNodes.add(xmlNode);
    }

    public void addSubFolder(String subFolderName, List<XMLNode> xmlNodes) {
        subFolders.put(subFolderName, xmlNodes != null ? xmlNodes : new ArrayList<XMLNode>());
    }

    public XMLNode findNode(String nodeRef) {
         if (getNodeRefString().equals(nodeRef)) {
            return this;
        }

        for (List<XMLNode> xmlNodes : getSubFolders().values()) {
            for (XMLNode xmlNode : xmlNodes) {
                XMLNode node = xmlNode.findNode(nodeRef);
                if (node != null) {
                    return node;
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "XMLNode{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", nodeRef='" + nodeRef + '\'' +
                '}';
    }
}
