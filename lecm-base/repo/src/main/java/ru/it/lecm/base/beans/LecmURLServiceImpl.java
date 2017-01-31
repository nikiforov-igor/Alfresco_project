package ru.it.lecm.base.beans;


import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.UrlUtil;

/**
 * User: dbashmakov
 * Date: 26.01.2017
 * Time: 15:05
 */
public class LecmURLServiceImpl implements LecmURLService {

    protected ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
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
        if (linkUrl.startsWith("/" + getShareContext())
                || linkUrl.startsWith(getShareContext())) {
            linkUrl = linkUrl.replace("/" + getShareContext(), "").replace(getShareContext(), "");
        }
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
        return "<a href=\"" + UrlUtil.getShareUrl(serviceRegistry.getSysAdminParams()) + LINK_URL + "?nodeRef=" + node + "\">" + description + "</a>";
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
        return "<a href=\"" + UrlUtil.getShareUrl(serviceRegistry.getSysAdminParams()) + WORKFLOW_LINK_URL + "?workflowId=" + executionId + "\">" + description + "</a>";
    }
}
