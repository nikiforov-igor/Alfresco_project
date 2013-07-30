package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 30.07.13
 * Time: 10:47
 */
public interface DocumentWorkflowCommentsService {
	public static final String DOCUMENT_WORKFLOW_COMMENTS_ROOT_NAME = "Комментарии процессов";

	public static final QName TYPE_DOCUMENT_WORKFLOW_COMMENT = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "documentWorkflowComment");

	public static final QName PROP_DOCUMENT_WORKFLOW_COMMENT_TEXT = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "documentWorkflowCommentText");

	/**
	 * Получение папки с вложениями для документра
	 * @param documentRef Ссылка на документ
	 * @return Ссылка на папку с вложениями
	 */
	public NodeRef getRootFolder(final NodeRef documentRef);

	public NodeRef createWorkflowComment(NodeRef documentRef, String comment);
}
