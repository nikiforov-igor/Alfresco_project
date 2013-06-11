package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: dbashmakov
 * Date: 12.04.13
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class OrgstructureElementMemberCreateOrUpdatePolicy extends LogicECMAssociationPolicy {

    private SubstitudeBean substitudeService;

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_ELEMENT_MEMBER, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_ELEMENT_MEMBER, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

    }
    @Override
    protected Serializable getSerializable(final NodeRef node){
        return substitudeService.getObjectDescription(node);
    }

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }
}
