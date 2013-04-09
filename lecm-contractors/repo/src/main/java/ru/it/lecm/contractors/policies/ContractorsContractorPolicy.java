package ru.it.lecm.contractors.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

/**
 * @author dgonchar
 */
public class ContractorsContractorPolicy implements NodeServicePolicies.OnUpdateNodePolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdateNodePolicy.QNAME,
                QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "contractor-type"),
                new JavaBehaviour(this, "onUpdateNode"));
    }

    @Override
    public void onUpdateNode(NodeRef contractor) {
        if(nodeService.getProperty(contractor, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "shortname")) == null) {
            return;
        }

        String shortname = nodeService.getProperty(contractor, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "shortname")).toString();
        nodeService.setProperty(contractor, ContentModel.PROP_NAME, shortname.replaceAll("[^_\\-\\dA-Za-zА-Яа-я ]", ""));
    }
}
