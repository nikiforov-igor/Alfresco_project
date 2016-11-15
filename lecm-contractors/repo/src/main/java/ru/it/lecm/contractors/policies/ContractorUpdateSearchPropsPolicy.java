package ru.it.lecm.contractors.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.contractors.api.Contractors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 14.11.2016
 * Time: 10:23
 */
public class ContractorUpdateSearchPropsPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    private final QName[] AFFECTED_PROPERTIES = {Contractors.PROP_CONTRACTOR_FULLNAME, Contractors.PROP_CONTRACTOR_SHORTNAME};

    private final String SEARCH_POSTFIX = "-search";

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private Contractors contractorsBean;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setContractorsBean(Contractors contractorsBean) {
        this.contractorsBean = contractorsBean;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                Contractors.TYPE_CONTRACTOR, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (nodeService.exists(nodeRef)) {
            List<QName> changedProps = getAffectedProperties(before, after);
            Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
            boolean hasChanges = false;
            for (QName changedProp : changedProps) {
                QName searchProp = QName.createQName(Contractors.CONTRACTOR_NAMESPACE, changedProp.getLocalName() + SEARCH_POSTFIX);

                String updatedValue = contractorsBean.formatContractorName((String) properties.get(changedProp));
                properties.put(searchProp, updatedValue);
                hasChanges = true;
            }
            if (hasChanges) {
                nodeService.setProperties(nodeRef, properties);
            }
        }
    }

    private List<QName> getAffectedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after) {
        List<QName> result = new ArrayList<>();
        for (QName affected : AFFECTED_PROPERTIES) {
            Object prev = before.get(affected);
            Object cur = after.get(affected);
            if (cur != null && !cur.equals(prev)) {
                result.add(affected);
            }
        }
        return result;
    }

}
