package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.Collection;
import java.util.List;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:58
 */
public interface DocumentAttachmentsService {

    String DOCUMENT_ATTACHMENTS_ROOT_NAME = "Вложения";
    String CONSTRAINT_ATTACHMENT_CATEGORIES = "attachment-categories";
    String NOT_SECURITY_MOVE_ATTACHMENT_POLICY = "not_security_move_attachment_policy";
	String NOT_SECURITY_CREATE_VERSION_ATTACHMENT_POLICY = "not_security_create_versionattachment_policy";

	QName TYPE_CATEGORY = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "attachmentsCategory");
	QName ASSOC_CATEGORY_ATTACHMENTS = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "categoryAttachments");
	QName ASPECT_SKIP_ON_CREATE_DOCUMENT = QName.createQName(DocumentService.DOCUMENT_ASPECTS_NAMESPACE_URI, "attachment-skip-on-create-document");

    /**
     * Получение папки с вложениями для документа
     * @param documentRef Ссылка на документ
     * @return Ссылка на папку с вложениями
     */
	NodeRef getRootFolder(final NodeRef documentRef);

    /**
     * Создание папки с вложениями для документа
     * @param documentRef Ссылка на документ
     * @return Ссылка на папку с вложениями
	 * @throws WriteTransactionNeededException
     */
	NodeRef createRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException;

    /**
     * Получения папок с категориями вложений
     * @param documentRef Ссылка на документ
     * @return Список ссылок на папки с категориями вложений
     */
	List<NodeRef> getCategories(final NodeRef documentRef) throws WriteTransactionNeededException;

    /**
     * Получения папок с категориями вложений
     * @param documentTypeQName Тип документа
     * @return Список ссылок на папки с категориями вложений
     */
	List<String> getCategories(final QName documentTypeQName);

	/**
	 * Получение папки для категории вложений.
	 * @param category Имя категории
	 * @param documentRef Ссылка на документ
	 * @return
	 */
	NodeRef getCategory(final String category, final NodeRef documentRef);

    /**
     * Получение категории вложений по типу документа из справочника настроек типов документов
     * @param documentTypeQName Тип документа
     * @return название категории
     */
	String getCategoryNameFromDocTypeSettings(final QName documentTypeQName);

    /**
     * Получение категории вложений по типу документа из справочника настроек типов документов
     * @param documentRef документ
     * @return название категории
     */
	String getCategoryNameFromDocTypeSettings(final NodeRef documentRef);


	/**
	 * Удаление вложения
	 * @param nodeRef Ссылка на вложение
	 */
	void deleteAttachment(NodeRef nodeRef);

	/**
	 * Получение списка версий вложения
	 * @param nodeRef Ссылка на вложение
	 * @return Список версий
	 */
	Collection<Version> getAttachmentVersions(NodeRef nodeRef);

	/**
	 * Проверка, что категория доступна только для чтения
	 * @param nodeRef Ссылка на категорию
	 * @return true, если категория доступна только для чтения
	 */
	boolean isReadonlyCategory(NodeRef nodeRef);

	/**
	 * Логгирование копирования вложений
	 * @param nodeRef Ссылка на вложения
	 */
	void copyAttachmentLog(NodeRef nodeRef, NodeRef parent);

	NodeRef getDocumentByAttachment(NodeRef attachRef);

	NodeRef getDocumentByCategory(NodeRef categoryRef);

	String getCategoryName(NodeRef categoryRef);

	String getCategoryNameByAttachment(NodeRef attachRef);

	NodeRef getCategoryByAttachment(NodeRef attachRef);

	boolean isDocumentAttachment(NodeRef nodeRef);

	boolean isDocumentCategory(NodeRef nodeRef);

    /**
     * Возвращает список вложений определенной категории
     * @param document - документ
     * @param categoryName - имя категории
     * @return
     */
	List<NodeRef> getAttachmentsByCategory(NodeRef document, String categoryName);

	List<NodeRef> getAttachmentsByCategory(NodeRef category);

    /** Программное добавление вложения документу без проверки прав
     *
     * @param attachmentRef - ссылка на вложение
     * @param attachmentCategoryRef - ссылка на категорию вложения
     */
	void addAttachment(NodeRef attachmentRef, NodeRef attachmentCategoryRef);

    /**
     * Разблокировка всех вложений документа и удаление всех ссылок (не ассоциаций) на вложения документа.
     * @param documentRef документ
     */
	void unlockAttachmentsAndClearLinks(NodeRef documentRef);


    /**
     * Добавление слушателя разблокировки вложения
     * @param unlockListener слушатель
     */
	void addAttachmentUnlockListener(AttachmentUnlockListener unlockListener);

}
