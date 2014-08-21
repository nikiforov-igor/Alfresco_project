package ru.it.lecm.workflow.approval.deprecated;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.WorkflowTaskDecision;

/**
 *
 * @author vlevin
 */
@Deprecated
class TaskDecision extends WorkflowTaskDecision {

	private String comment;
	private NodeRef commentRef;
	private NodeRef documentRef;
	private String commentFileAttachmentCategoryName;
	private String documentProjectNumber;

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
}
