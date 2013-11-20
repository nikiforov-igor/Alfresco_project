package ru.it.lecm.contracts.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * User: AZinovin
 * Date: 20.11.13
 */
public class ContractStagesPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private NodeService nodeService;
    private PolicyComponent policyComponent;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ContractsBeanImpl.TYPE_CONTRACT_STAGE, new JavaBehaviour(this, "onUpdateProperties"));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String oldStatus = (String) before.get(ContractsBeanImpl.PROP_STAGE_STATUS);
        String newStatus = (String) after.get(ContractsBeanImpl.PROP_STAGE_STATUS);
        if (newStatus != null && !newStatus.equals(oldStatus)) {
            if ("В работе".equals(newStatus)) {
                nodeService.setProperty(nodeRef, ContractsBeanImpl.PROP_STAGE_START_DATE_REAL, new Date());
            } else if ("Закрыт".equals(newStatus)) {
                nodeService.setProperty(nodeRef, ContractsBeanImpl.PROP_STAGE_END_DATE_REAL, new Date());
            }
        }
    }
}
