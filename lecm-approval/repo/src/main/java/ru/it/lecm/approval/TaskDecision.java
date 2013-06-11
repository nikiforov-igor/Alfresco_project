package ru.it.lecm.approval;

import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
class TaskDecision {

	private String userName;
	private String decision;
	private Date startDate;
	private String comment;
	private NodeRef commentRef;
	private NodeRef documentRef;
	private String commentFileAttachmentCategoryName;
	private String documentProjectNumber;
	private Date dueDate;
	private String previousUserName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public NodeRef getCommentRef() {
		return commentRef;
	}

	public void setCommentRef(NodeRef commentRef) {
		this.commentRef = commentRef;
	}

	public NodeRef getDocumentRef() {
		return documentRef;
	}

	public void setDocumentRef(NodeRef documentRef) {
		this.documentRef = documentRef;
	}

	public String getCommentFileAttachmentCategoryName() {
		return commentFileAttachmentCategoryName;
	}

	public void setCommentFileAttachmentCategoryName(String commentFileAttachmentCategoryName) {
		this.commentFileAttachmentCategoryName = commentFileAttachmentCategoryName;
	}

	public String getDocumentProjectNumber() {
		return documentProjectNumber;
	}

	public void setDocumentProjectNumber(String documentProjectNumber) {
		this.documentProjectNumber = documentProjectNumber;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getPreviousUserName() {
		return previousUserName;
	}

	public void setPreviousUserName(String previousUserName) {
		this.previousUserName = previousUserName;
	}
}
