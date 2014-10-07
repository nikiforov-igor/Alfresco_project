package ru.it.lecm.businessjournal.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.io.Serializable;

/**
 * User: pmelnikov
 * Date: 10.12.13
 * Time: 15:40
 */
public class RecordObject implements Serializable {
    private NodeRef nodeRef;
    private String description;

    public RecordObject(NodeRef nodeRef, String description) {
        this.nodeRef = nodeRef;
        this.description = description;
    }

	public RecordObject() {
	}

	public void setNodeRef(NodeRef nodeRef) {
		this.nodeRef = nodeRef;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public NodeRef getNodeRef() {
        return nodeRef;
    }

    public String getDescription() {
        return description;
    }
}