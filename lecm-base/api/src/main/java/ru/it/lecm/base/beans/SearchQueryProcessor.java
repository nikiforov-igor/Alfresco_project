package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.PropertyCheck;

import java.util.Map;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 9:57
 */
public abstract class SearchQueryProcessor {
    protected NamespaceService namespaceService;
    protected AuthenticationService authService;
    protected NodeService nodeService;
    protected SearchService searchService;

    protected SearchQueryProcManager processorManager;

    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setProcessorManager(SearchQueryProcManager processorManager) {
        this.processorManager = processorManager;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
    /**
     регистрации в системе
     */
    @SuppressWarnings("unused")
    void register() {
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
        PropertyCheck.mandatory(this, "authService", authService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "processorManager", processorManager);

        if (id != null && !id.isEmpty()) {
            processorManager.resisterProcessor(this);
        }
    }

    abstract public String getQuery(Map<String, Object> params);
}
