package ru.it.lecm.search.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.search.beans.SearchEditorService;

import java.io.Serializable;
import java.util.Map;

/**
 * User: DBashmakov
 * Date: 08.05.2015
 * Time: 14:24
 */
public class SearchQueriesPolicy extends LogicECMAssociationPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy{

    final private static Logger logger = LoggerFactory.getLogger(SearchQueriesPolicy.class);

    private SearchEditorService searchQueriesService;

    public void setSearchQueriesService(SearchEditorService searchQueriesService) {
        this.searchQueriesService = searchQueriesService;
    }

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                SearchEditorService.TYPE_SEARCH_QUERY_DIC, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                SearchEditorService.TYPE_SEARCH_QUERY_DIC, new JavaBehaviour(this, "onCreateAssociation"));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                SearchEditorService.TYPE_SEARCH_QUERY_DIC, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        final String prevSetting = (String) before.get(SearchEditorService.PROP_SEARCH_QUERY_SETTING);
        final String curSetting = (String) after.get(SearchEditorService.PROP_SEARCH_QUERY_SETTING);

        if (!safeEquals(prevSetting, curSetting)) {
            try {
                JSONObject configObj = new JSONObject(curSetting);
                String newQuery = searchQueriesService.buildQuery(configObj);
                nodeService.setProperty(nodeRef, SearchEditorService.PROP_SEARCH_QUERY_QUERY, newQuery);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private boolean safeEquals(Object o1, Object o2) {
        return (o1 == o2) || (o1 != null && o1.equals(o2));
    }
}
