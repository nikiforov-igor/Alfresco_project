package ru.it.lecm.businessjournal.policies;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * @author dbashmakov Date: 24.01.13 Time: 10:47
 */
public class BusinessJournalEventCategoryUpdatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private final static Logger logger = LoggerFactory.getLogger(BusinessJournalEventCategoryUpdatePolicy.class);

    private PolicyComponent policyComponent;
    private BusinessJournalService service;

    public BusinessJournalService getService() {
        return service;
    }

    public void setService(BusinessJournalService service) {
        this.service = service;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "businessJournalService", service);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
            BusinessJournalService.TYPE_EVENT_CATEGORY, new JavaBehaviour(this, "onUpdateProperties"));
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (after.containsKey(BusinessJournalService.PROP_EVENT_CAT_ON)) {
            if (before.containsKey(BusinessJournalService.PROP_EVENT_CAT_ON)) {
                Boolean beforeValue = (Boolean) before.get(BusinessJournalService.PROP_EVENT_CAT_ON);
                Boolean afterValue = (Boolean) after.get(BusinessJournalService.PROP_EVENT_CAT_ON);
                if (!afterValue.equals(beforeValue)) {
                    service.dropCache();
                }
            }
        }

    }
}
