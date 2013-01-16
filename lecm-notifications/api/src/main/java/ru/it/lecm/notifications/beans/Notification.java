package ru.it.lecm.notifications.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 10:42
 */
public class Notification {
	private String autor;
	private String description;

	private NodeRef objectRef;
	private List<NodeRef> typeRefs;
	private List<NodeRef> recipientEmployeeRefs;
	private List<NodeRef> recipientPositionRefs;
	private List<NodeRef> recipientOrganizationUnitRefs;
	private List<NodeRef> recipientWorkGroupRefs;

	public Notification() {
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public NodeRef getObjectRef() {
		return objectRef;
	}

	public void setObjectRef(NodeRef objectRef) {
		this.objectRef = objectRef;
	}

	public List<NodeRef> getTypeRefs() {
		return typeRefs;
	}

	public void setTypeRefs(List<NodeRef> typeRefs) {
		this.typeRefs = typeRefs;
	}

	public List<NodeRef> getRecipientEmployeeRefs() {
		return recipientEmployeeRefs;
	}

	public void setRecipientEmployeeRefs(List<NodeRef> recipientEmployeeRefs) {
		this.recipientEmployeeRefs = recipientEmployeeRefs;
	}

	public List<NodeRef> getRecipientPositionRefs() {
		return recipientPositionRefs;
	}

	public void setRecipientPositionRefs(List<NodeRef> recipientPositionRefs) {
		this.recipientPositionRefs = recipientPositionRefs;
	}

	public List<NodeRef> getRecipientOrganizationUnitRefs() {
		return recipientOrganizationUnitRefs;
	}

	public void setRecipientOrganizationUnitRefs(List<NodeRef> recipientOrganizationUnitRefs) {
		this.recipientOrganizationUnitRefs = recipientOrganizationUnitRefs;
	}

	public List<NodeRef> getRecipientWorkGroupRefs() {
		return recipientWorkGroupRefs;
	}

	public void setRecipientWorkGroupRefs(List<NodeRef> recipientWorkGroupRefs) {
		this.recipientWorkGroupRefs = recipientWorkGroupRefs;
	}
}
