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
public class ContractorsLegalFormPolicy implements NodeServicePolicies.OnUpdateNodePolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private String propertiesService;

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
                QName.createQName("http://www.it.ru/lecm/contractors/model/legalform/1.0", "legalform-type"),
                new JavaBehaviour(this, "onUpdateNode"));
    }

    @Override
    public void onUpdateNode(NodeRef legalform) {
        if(nodeService.getProperty(legalform, QName.createQName("http://www.it.ru/lecm/contractors/model/legalform/1.0", "full-title")) == null) {
            return;
        }

        String title = nodeService.getProperty(legalform, QName.createQName("http://www.it.ru/lecm/contractors/model/legalform/1.0", "full-title")).toString();
        nodeService.setProperty(legalform, ContentModel.PROP_NAME, title.replaceAll("[^_\\-\\dA-Za-zА-Яа-я ]", ""));
    }

    public void setPropertiesService(String propertiesService) {
        this.propertiesService = propertiesService;
    }

    public String getPropertiesService() {
        return propertiesService;
    }
}
