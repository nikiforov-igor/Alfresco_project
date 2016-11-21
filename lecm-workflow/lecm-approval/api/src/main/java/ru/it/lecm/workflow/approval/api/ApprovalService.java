package ru.it.lecm.workflow.approval.api;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 *
 * @author vmalygin
 */
public interface ApprovalService {
	public static final String APPROVAL_ASPECTS_NAMESPACE = "http://www.it.ru/logicECM/model/approval/aspects/1.0";
	
	public static final QName ASPECT_APPROVAL_DATA = QName.createQName(APPROVAL_ASPECTS_NAMESPACE, "approvalDataAspect");
	public static final QName ASSOC_APPROVAL_FOLDER = QName.createQName(APPROVAL_ASPECTS_NAMESPACE, "approvalFolderRef");
	
	/**
	 * получение NodeRef-ы на корневую папку "Сервис Согласование"
	 * @return NodeRef на корневую папку или null, если такой нет
	 */
	NodeRef getApprovalFolder();

	/**
	 * получение NodeRef-ы на объект с глобальными настройками согласования
	 * @return NodeRef на объект с глобальными настройками согласования или null, если такого нет
	 */
	NodeRef getSettings();

	/**
	 * Получить срок согласования по умолчанию.
	 * @return срок согласования в днях
	 */
	int getApprovalTerm();

	/**
	 * получение NodeRef-ы на папку "Согласование" внутри указанного документа
	 * @param documentRef NodeRef-а на документ
	 * @return NodeRef на папку "Согласование" или null, если такой нет
	 */
	NodeRef getDocumentApprovalFolder(final NodeRef documentRef);

	/**
	 * создание папки "Согласование" внутри указанного документа
	 * @param documentRef NodeRef-а на документ
	 * @return NodeRef на папку "Согласование"
	 */
	NodeRef createDocumentApprovalFolder(final NodeRef documentRef);

	/**
	 * получение NodeRef-ы на папку "Согласование/История" внутри указанного документа
	 * @param documentRef NodeRef-а на документ
	 * @return NodeRef на папку "Согласование/История" или null, если такой нет
	 * @throws AlfrescoRuntimeException если папка "Согласование" не существует, а мы попытались получить папку "Согласование/История"
	 */
	NodeRef getDocumentApprovalHistoryFolder(final NodeRef documentRef);

	/**
	 * создание папки "Согласование/История" внутри указанного документа
	 * @param documentRef NodeRef-а на документ
	 * @return NodeRef на папку "Согласование/История"
	 * @throws AlfrescoRuntimeException если папка "Согласование" не существует, а мы попытались создать папку "Согласование/История"
	 */
	NodeRef createDocumentApprovalHistoryFolder(final NodeRef documentRef);

	boolean checkExpression(NodeRef nodeRef, String expression);

	void copyToDocumentAttachmentCategory(NodeRef attachment, NodeRef document, String filename) throws WriteTransactionNeededException;
}
