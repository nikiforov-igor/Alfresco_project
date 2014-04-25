package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;

import java.util.Collection;
import java.util.List;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:58
 */
public interface DocumentAttachmentsService {
    public static final String DOCUMENT_ATTACHMENTS_ROOT_NAME = "Вложения";

    public static final String CONSTRAINT_ATTACHMENT_CATEGORIES = "attachment-categories";

    public static final String NOT_SECURITY_MOVE_ATTACHMENT_POLICY = "not_security_move_attachment_policy";

	public static final QName TYPE_CATEGORY = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "attachmentsCategory");
	public static final QName ASSOC_CATEGORY_ATTACHMENTS = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "categoryAttachments");

    /**
     * Получение папки с вложениями для документра
     * @param documentRef Ссылка на документ
     * @return Ссылка на папку с вложениями
     */
    public NodeRef getRootFolder(final NodeRef documentRef);

    /**
     * Получения папок с категориями вложений
     * @param documentRef Ссылка на документ
     * @return Список ссылок на папки с категориями вложений
     */
    public List<NodeRef> getCategories(final NodeRef documentRef);

    /**
     * Получения папок с категориями вложений
     * @param documentTypeQName Тип документа
     * @return Список ссылок на папки с категориями вложений
     */
    public List<String> getCategories(final QName documentTypeQName);

	/**
	 * Получение папки для категории вложений.
	 * @param category Имя категории
	 * @param documentRef Ссылка на документ
	 * @return
	 */
	public NodeRef getCategory(final String category, final NodeRef documentRef);

	/**
	 * Удаление вложения
	 * @param nodeRef Ссылка на вложение
	 */
	public void deleteAttachment(NodeRef nodeRef);

	/**
	 * Получение списка версий вложения
	 * @param nodeRef Ссылка на вложение
	 * @return Список версий
	 */
	public Collection<Version> getAttachmentVersions(NodeRef nodeRef);

	/**
	 * Проверка, что категория доступна только для чтения
	 * @param nodeRef Ссылка на категорию
	 * @return true, если категория доступна только для чтения
	 */
	public boolean isReadonlyCategory(NodeRef nodeRef);

	/**
	 * Логгирование копирования вложений
	 * @param nodeRef Ссылка на вложения
	 */
	public void copyAttachmentLog(NodeRef nodeRef, NodeRef parent);

	public NodeRef getDocumentByAttachment(NodeRef attachRef);

	public NodeRef getDocumentByCategory(NodeRef categoryRef);

	public String getCategoryName(NodeRef categoryRef);

	public String getCategoryNameByAttachment(NodeRef attachRef);

	public NodeRef getCategoryByAttachment(NodeRef attachRef);

	public boolean isDocumentAttachment(NodeRef nodeRef);

	public boolean isDocumentCategory(NodeRef nodeRef);

    /**
     * Возвращает список вложений определенной категории
     * @param document - документ
     * @param categoryName - имя категории
     * @return
     */
    public List<NodeRef> getAttachmentsByCategory(NodeRef document, String categoryName);

	public List<NodeRef> getAttachmentsByCategory(NodeRef category);

    /** Программное добавление вложения документу без проверки прав
     *
     * @param attachmentRef - ссылка на вложение
     * @param attachmentCategoryRef - ссылка на категорию вложения
     */
    public void addAttachment(NodeRef attachmentRef, NodeRef attachmentCategoryRef);

}
