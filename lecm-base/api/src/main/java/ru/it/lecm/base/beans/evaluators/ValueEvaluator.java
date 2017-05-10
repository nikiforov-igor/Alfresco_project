package ru.it.lecm.base.beans.evaluators;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.PropertyCheck;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * User: dbashmakov
 * Date: 04.05.2017
 * Time: 16:25
 */
public abstract class ValueEvaluator {
    private NamespaceService namespaceService;
    private AuthenticationService authService;
    private NodeService nodeService;
    private SearchService searchService;
    private ValueEvaluatorsManager evaluatorsManager;

    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     регистрации в системе
     */
    @SuppressWarnings("unused")
    void register() {
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
        PropertyCheck.mandatory(this, "authService", authService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "evaluatorsManager", evaluatorsManager);
        PropertyCheck.mandatory(this, "searchService", searchService);

        if (id != null && !id.isEmpty()) {
            evaluatorsManager.resister(this);
        }
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

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setEvaluatorsManager(ValueEvaluatorsManager evaluatorsManager) {
        this.evaluatorsManager = evaluatorsManager;
    }

    abstract public String evaluate(JSONObject config) throws JSONException;
}
