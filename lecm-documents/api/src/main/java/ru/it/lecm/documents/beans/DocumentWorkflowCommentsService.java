package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

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
	 * Получение папки с комментариями процессов для документа
	 * @param documentRef Ссылка на документ
	 * @return Ссылка на папку с комментариями
	 */
	public NodeRef getRootFolder(final NodeRef documentRef);

	/**
	 * Получение папки с комментариями процессов для документа
	 * или ее создание в случае отсутствия
	 * @param documentRef Ссылка на документ
	 * @return Ссылка на папку с комментариями
	 * @throws WriteTransactionNeededException
	 */
	//public NodeRef getOrCreateRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException;

	/**
	 * Создание папки с комментариями процессов для документа
	 * @param documentRef Ссылка на документ
	 * @return Ссылка на папку с комментариями
	 * @throws WriteTransactionNeededException
	 */
	public NodeRef createRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException;

	public NodeRef createWorkflowComment(NodeRef documentRef, String comment);
}
