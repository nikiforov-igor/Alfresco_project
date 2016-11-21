package ru.it.lecm.contracts.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;

/**
 * Created by dkuchurkin on 26.09.2016.
 */
public class ContractPartnerAssociationPolicy extends LogicECMAssociationPolicy {

    @Override
    protected Serializable getSerializable(NodeRef node) {
        return nodeService.getProperty(node, Contractors.PROP_CONTRACTOR_SHORTNAME);
    }

    @Override
    public void init() {
        super.init();

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, ContractsBeanImpl.ASSOC_CONTRACT_PARTNER, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
