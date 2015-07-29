package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import org.alfresco.repo.site.SiteModel;

/**
 * User: dbashmakov
 * Date: 24.07.2014
 * Time: 12:05
 */
public class OrgstructureOnCreateAssocPolicy extends LogicECMAssociationPolicy {

    private SubstitudeBean substitute;

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_ELEMENT, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_ELEMENT, new JavaBehaviour(this, "onCreateAssociation"));
		
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                SiteModel.TYPE_SITE, new JavaBehaviour(this, "onDeleteAssociation"));
				
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                SiteModel.TYPE_SITE, new JavaBehaviour(this, "onCreateAssociation"));
    }

    @Override
    protected Serializable getSerializable(final NodeRef node){
        return substitute.getObjectDescription(node);
    }

    public void setSubstitute(SubstitudeBean substitute) {
        this.substitute = substitute;
    }
}
