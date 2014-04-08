package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/*
 * Оповещения для Бизнес Ролей (тип "lecm-orgstr:business-role")
 */
public class OrgstructureBusinessRolePolicy
		extends SecurityNotificationsPolicyBase
		implements NodeServicePolicies.OnCreateNodePolicy
				, NodeServicePolicies.BeforeDeleteNodePolicy
				, NodeServicePolicies.OnUpdatePropertiesPolicy

{
	// обработчики связанных ссылок на Подразделения, Должности и Сотрудников ...
	final private PolicyBRLinkNotifier policyOU = new PolicyBRLinkNotifier( "OrgUnit");
	final private PolicyBRLinkNotifier policyDP = new PolicyBRLinkNotifier( "DP");
	final private PolicyBRLinkNotifier policyEmployee = new PolicyBRLinkNotifier("Employee");

	@Override
	public void init() {
		// PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		logger.info("Business Role policy hook activated");

		super.init();

		// TYPE_ORGANIZATION_UNIT : "lecm-orgstr:organization-unit"
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, new JavaBehaviour(this, "onCreateNode"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, new JavaBehaviour(this, "beforeDeleteNode"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, new JavaBehaviour(this, "onUpdateProperties"));

		/* ссылки на Подразделения ... */
		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT,
				new JavaBehaviour(this.policyOU, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT,
				new JavaBehaviour(this.policyOU, "onDeleteAssociation"));

		/* ссылки на Должностные Позиции ... */
		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER,
				new JavaBehaviour(this.policyDP, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER,
				new JavaBehaviour(this.policyDP, "onDeleteAssociation"));

		/* ссылки на Сотрудников ... */
		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_EMPLOYEE,
				new JavaBehaviour(this.policyEmployee, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_BUSINESS_ROLE, OrgstructureBean.ASSOC_BUSINESS_ROLE_EMPLOYEE,
				new JavaBehaviour(this.policyEmployee, "onDeleteAssociation"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		final NodeRef nodeBR = childAssocRef.getChildRef();
		// final NodeRef parent = childAssocRef.getParentRef(); // supposed to be null or the main BR folder

		// оповещение securityService по БР ...
		notifyNodeCreated( PolicyUtils.makeBRPos(nodeBR, nodeService) );
	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		logger.debug("deleting business role "+ nodeRef);

		// оповещение securityService по БР ...
		notifyNodeDeactivated( PolicyUtils.makeBRPos(nodeRef, nodeService));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeBR,
			Map<QName, Serializable> before, Map<QName, Serializable> after)
	{
		final Object oldDetails = before.get( OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		final Object newDetails = after.get( OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		final boolean changed = ! PolicyUtils.safeEquals( newDetails, oldDetails);
		if (changed) {
			logger.debug( String.format( "updating details for business role '%s'\n\t from '%s'\n\t to '%s'", nodeBR, oldDetails, newDetails));
			notifyNodeCreated( PolicyUtils.makeBRPos(nodeBR, nodeService) );
		}
	}

	public class PolicyBRLinkNotifier
			implements NodeServicePolicies.OnCreateAssociationPolicy
					, NodeServicePolicies.OnDeleteAssociationPolicy
	{
		final String tag;

		public PolicyBRLinkNotifier(String info) {
			this.tag = info;
		}

		@Override
		public void onCreateAssociation(AssociationRef nodeAssocRef) {
			logger.debug("creating business role link to "+ tag+ ": "+ nodeAssocRef);
			notifyBRAssociationChanged(nodeAssocRef, true);
		}

		@Override
		public void onDeleteAssociation(AssociationRef nodeAssocRef) {
			logger.debug("deleting business role link to "+ tag+ ": "+ nodeAssocRef);
			notifyBRAssociationChanged(nodeAssocRef, false);
		}

		@Override
		public String toString() {
			return "PolicyBRLinkNotifier("+ tag + ")";
		}

	}

}