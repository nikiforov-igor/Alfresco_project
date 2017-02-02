package ru.it.lecm.base.beans;


import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.UrlUtil;
import org.springframework.beans.factory.InitializingBean;

import java.util.Properties;

/**
 * User: dbashmakov
 * Date: 26.01.2017
 * Time: 15:05
 */
public class LecmURLServiceImpl implements LecmURLService, InitializingBean {
    private String linkURL;
    private String detailsLinkUrl;
    private String workflowLinkUrl;
    private String documentAttachmentLinkUrl;

    private ServiceRegistry serviceRegistry;
    private Properties globalProperties;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setGlobalProperties(Properties globalProperties) {
        this.globalProperties = globalProperties;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public String getDetailsLinkUrl() {
        return detailsLinkUrl;
    }

    public String getWorkflowLinkUrl() {
        return workflowLinkUrl;
    }

    public String getDocumentAttachmentLinkUrl() {
        return documentAttachmentLinkUrl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String PAGE_PREFIX = "/page/";
        linkURL = PAGE_PREFIX + this.globalProperties.getProperty("lecm.page.view-metadata", "view-metadata");
        detailsLinkUrl = PAGE_PREFIX + this.globalProperties.getProperty("lecm.page.document-details", "document-details");
        workflowLinkUrl = PAGE_PREFIX + this.globalProperties.getProperty("lecm.page.workflow-details", "workflow-details");
        documentAttachmentLinkUrl = PAGE_PREFIX + this.globalProperties.getProperty("lecm.page.document-attachment", "document-attachment");
    }

    /**
     * Оборачиваем узел в ссылку html страницы
     *
     * @param nodeRef
     * @param description
     * @param linkUrl
     * @return
     */
    public String wrapperLink(NodeRef nodeRef, String description, String linkUrl) {
        return wrapperLink(nodeRef.toString(), description, linkUrl);
    }
    /**
     * Оборачиваем узел в ссылку html страницы
     *
     * @param nodeRef
     * @param description
     * @param linkUrl
     * @return
     */
    public String wrapperLink(String nodeRef, String description, String linkUrl) {
        /*убираем /share или share - чтобы избежать дублирования*/
        linkUrl = linkUrl.replaceAll("^//?" + getShareContext(), "");
        return "<a href=\"" + UrlUtil.getShareUrl(serviceRegistry.getSysAdminParams()) + linkUrl + "?nodeRef=" + nodeRef + "\">"
                + description + "</a>";
    }

    /**
     * Оборачиваем узел в ссылку на view-metadata
     * @param node
     * @param description
     * @return
     */
    public String wrapperLink(String node, String description) {
        return wrapperLink(node, description, getLinkURL());
    }

    /**
     * Получить значение свойства share.context из global.properties
     * @return
     */
    public String getShareContext() {
        return serviceRegistry.getSysAdminParams().getShareContext();
    }

    /**
     * Обернуть @param link в контекст.
     * Пример: /{share.context}/link
     * @return
     */
    public String getLinkWithContext(String link) {
        return "/" + getShareContext() + (link.startsWith("/") ? link : "/" + link);
    }

    /**
     * Оборачиваем узел в ссылку на workflow-details
     * @param executionId
     * @param description
     * @return
     */
    public String wrapAsWorkflowLink(String executionId, String description) {
        return "<a href=\"" + UrlUtil.getShareUrl(serviceRegistry.getSysAdminParams()) + getWorkflowLinkUrl() + "?workflowId=" + executionId + "\">" + description + "</a>";
    }
}
