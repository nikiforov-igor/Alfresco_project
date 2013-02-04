package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/*
 * Оповещения для Бизнес Ролей (тип "lecm-orgstr:business-role")
 */
public class OrgstructureBusinessRolePolicy
		extends SecurityNotificationsPolicyBase
		implements NodeServicePolicies.OnCreateNodePolicy
				, NodeServicePolicies.OnDeleteNodePolicy

				, NodeServicePolicies.OnCreateAssociationPolicy
				, NodeServicePolicies.OnDeleteAssociationPolicy
{
	@Override
	public void init() {
		// PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		super.init();

		// TYPE_ORGANIZATION_UNIT : "lecm-orgstr:organization-unit"
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, new JavaBehaviour(this, "onCreateNode"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, new JavaBehaviour(this, "onDeleteNode"));

		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT,
				new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT,
				new JavaBehaviour(this, "onDeleteAssociation"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		final NodeRef nodeBR = childAssocRef.getChildRef();
		// final NodeRef parent = childAssocRef.getParentRef(); // supposed to be null or the main BR folder

		// оповещение securityService по БР ...
		notifyNodeCreated( PolicyUtils.makeBRPos(nodeBR, nodeService) );
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		final NodeRef nodeBR = childAssocRef.getChildRef();

		// оповещение securityService по БР ...
		notifyNodeDeactivated( PolicyUtils.makeBRPos(nodeBR, nodeService));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		notifyBRAssociationChanged(nodeAssocRef, true);
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		notifyBRAssociationChanged(nodeAssocRef, false);
	}

}