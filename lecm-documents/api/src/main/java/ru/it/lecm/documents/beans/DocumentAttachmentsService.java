package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:58
 */
public interface DocumentAttachmentsService {
    public static final String DOCUMENT_ATTACHMENTS_ROOT_NAME = "Вложения";

    public NodeRef getAttacmentRootFolder(final NodeRef documentRef);
}
