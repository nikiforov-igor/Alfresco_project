package ru.it.lecm.businessjournal.policies;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

import java.io.Serializable;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 07.04.2014
 * Time: 10:50
 */
public class BusinessJournalObjTypeUpdatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.BeforeDeleteNodePolicy{
    private PolicyComponent policyComponent;

    private SimpleCache<String, NodeRef> objTypeCache;
    private SimpleCache<NodeRef, String> typeTemplateCache;
    private SimpleCache<NodeRef, String> typeListTemplateCache;

    public void setObjTypeCache(SimpleCache<String, NodeRef> objTypeCache) {
        this.objTypeCache = objTypeCache;
    }

    public void setTypeTemplateCache(SimpleCache<NodeRef, String> typeTemplateCache) {
        this.typeTemplateCache = typeTemplateCache;
    }

    public void setTypeListTemplateCache(SimpleCache<NodeRef, String> typeListTemplateCache) {
        this.typeListTemplateCache = typeListTemplateCache;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                BusinessJournalService.TYPE_OBJECT_TYPE, new JavaBehaviour(this, "onUpdateProperties",  Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                BusinessJournalService.TYPE_OBJECT_TYPE, new JavaBehaviour(this, "beforeDeleteNode"));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (after.containsKey(BusinessJournalService.PROP_OBJ_TYPE_TEMPLATE)) {
            if (before.containsKey(BusinessJournalService.PROP_OBJ_TYPE_TEMPLATE)) {
                String beforeValue = (String) before.get(BusinessJournalService.PROP_OBJ_TYPE_TEMPLATE);
                String afterValue = (String) after.get(BusinessJournalService.PROP_OBJ_TYPE_TEMPLATE);
                if (!afterValue.equals(beforeValue)) {
                    typeTemplateCache.put(nodeRef, afterValue);
                }
            }
        }
        if (after.containsKey(BusinessJournalService.PROP_OBJ_TYPE_LIST_TEMPLATE)) {
            if (before.containsKey(BusinessJournalService.PROP_OBJ_TYPE_LIST_TEMPLATE)) {
                String beforeValue = (String) before.get(BusinessJournalService.PROP_OBJ_TYPE_LIST_TEMPLATE);
                String afterValue = (String) after.get(BusinessJournalService.PROP_OBJ_TYPE_LIST_TEMPLATE);
                if (!afterValue.equals(beforeValue)) {
                    typeListTemplateCache.put(nodeRef,afterValue);
                }
            }
        }
    }

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        objTypeCache.clear();

        typeListTemplateCache.remove(nodeRef);
        typeTemplateCache.remove(nodeRef);
    }
}
