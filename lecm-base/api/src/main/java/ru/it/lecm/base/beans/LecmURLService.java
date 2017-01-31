package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: dbashmakov
 * Date: 26.01.2017
 * Time: 15:04
 */
public interface LecmURLService {
    /**
     * Оборачиваем узел в ссылку html страницы
     *
     * @param nodeRef
     * @param description
     * @param linkUrl
     * @return
     */
    String wrapperLink(NodeRef nodeRef, String description, String linkUrl);
    String wrapperLink(String nodeRef, String description, String linkUrl);

    /**
     * Оборачиваем узел в ссылку на view-metadata
     * @param node
     * @param description
     * @return
     */
    String wrapperLink(String node, String description);

    /**
     * Получить значение свойства share.context из global.properties
     * @return
     */
    String getShareContext();

    /**
     * Обернуть @param link в контекст.
     * Пример: /{share.context}link
     * @return
     */
    String getLinkWithContext(String link);

    /**
     * Оборачиваем узел в ссылку на workflow-details
     * @param executionId
     * @param description
     * @return
     */
    String wrapAsWorkflowLink(String executionId, String description);

    /**
     * Получить значения различных ссылок
     * @return
     */
    String getLinkURL();
    String getDetailsLinkUrl();
    String getWorkflowLinkUrl();
    String getDocumentAttachmentLinkUrl();
}
