package ru.it.lecm.contractors.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import java.io.Serializable;

/**
 * @author dgonchar
 */
public class ContractorsRepresentativePolicy implements NodeServicePolicies.OnUpdateNodePolicy {

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
                QName.createQName("http://www.it.ru/lecm/contractors/model/representative/1.0", "representative-type"),
                new JavaBehaviour(this, "onUpdateNode"));
    }

    @Override
    public void onUpdateNode(NodeRef representative) {
        if(nodeService.getProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/representative/1.0", "surname")) == null) {
            return;
        }

        String surname = nodeService.getProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/representative/1.0", "surname")).toString();
        String firstname = nodeService.getProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/representative/1.0", "firstname")).toString();
        Serializable propMiddlename = nodeService.getProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/representative/1.0", "middlename"));
        String middlename = "";
        if (propMiddlename != null) {
            middlename = propMiddlename.toString();
        }

        String fullname = String.format("%s %s %s", surname, firstname, middlename); // "Иванов Иван Иванович"
        fullname = fullname.trim().replaceAll("[^_\\-\\dA-Za-zА-Яа-я ]", "");

        nodeService.setProperty(representative, ContentModel.PROP_NAME, fullname);
    }
}
