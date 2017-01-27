package ru.it.lecm.base.beans;


import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;

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
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        return "<a href=\"" + serverUrl + linkUrl + "?nodeRef=" + nodeRef + "\">"
                + description + "</a>";
    }

    /**
     * Оборачиваем узел в ссылку на view-metadata
     * @param node
     * @param description
     * @return
     */
    public String wrapperLink(String node, String description) {
        return wrapperLink(node, description, getLinkWithContext(LINK_URL));
    }


    /**
     * Получить значение свойства share.context из global.properties
     * @return
     */
    public String getShareContext() {
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        return params.getShareContext();
    }

    /**
     * Обернуть @param link в контекст.
     * Пример: /{share.context}link
     * @return
     */
    public String getLinkWithContext(String link) {
        return "/" + getShareContext() + link;
    }

    /**
     * Оборачиваем узел в ссылку на workflow-details
     * @param executionId
     * @param description
     * @return
     */
    public String wrapAsWorkflowLink(String executionId, String description) {
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        return "<a href=\"" + serverUrl + getLinkWithContext(WORKFLOW_LINK_URL) + "?workflowId=" + executionId + "\">" + description + "</a>";
    }
}
