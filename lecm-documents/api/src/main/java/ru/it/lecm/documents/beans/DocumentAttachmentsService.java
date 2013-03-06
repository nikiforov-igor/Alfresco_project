package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:58
 */
public interface DocumentAttachmentsService {
    public static final String DOCUMENT_ATTACHMENTS_ROOT_NAME = "Вложения";

    public static final String CONSTRAINT_ATTACHMENT_CATEGORIES = "attachment-categories";

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
}
