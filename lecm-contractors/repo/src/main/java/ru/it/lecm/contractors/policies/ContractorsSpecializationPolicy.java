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
public class ContractorsSpecializationPolicy implements NodeServicePolicies.OnUpdateNodePolicy {

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
                QName.createQName("http://www.it.ru/lecm/contractors/model/specialization/1.0", "specialization-type"),
                new JavaBehaviour(this, "onUpdateNode"));
    }

    @Override
    public void onUpdateNode(NodeRef specialization) {
        if(nodeService.getProperty(specialization, QName.createQName("http://www.it.ru/lecm/contractors/model/specialization/1.0", "title")) == null) {
            return;
        }

        String title = nodeService.getProperty(specialization, QName.createQName("http://www.it.ru/lecm/contractors/model/specialization/1.0", "title")).toString();
        nodeService.setProperty(specialization, ContentModel.PROP_NAME, title.replaceAll("[^_\\-\\dA-Za-zА-Яа-я ]", ""));
    }
}
