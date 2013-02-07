package ru.it.lecm.subscriptions.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 06.02.13
 * Time: 16:31
 */
public class SubscriptionsToTypeLogEventOrganizationUnitPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
	final protected Logger logger = LoggerFactory.getLogger(SubscriptionsToTypeLogEventOrganizationUnitPolicy.class);

	private static PolicyComponent policyComponent;
	private static BusinessJournalService businessJournalService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public final void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, SubscriptionsService.ASSOC_DESTINATION_ORGANIZATION_UNIT,
				new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, SubscriptionsService.ASSOC_DESTINATION_ORGANIZATION_UNIT,
				new JavaBehaviour(this, "onDeleteAssociation"));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef subscription = nodeAssocRef.getSourceRef();
		NodeRef employeeRef = nodeAssocRef.getTargetRef();
		List<String> objects = new ArrayList<String>();
		objects.add(employeeRef.toString());
		try {
			businessJournalService.log(subscription, EventCategory.ADD, "Сотрудник #initiator создал подписку #mainobject для подразделения #object1", objects);
		} catch (Exception e) {
			logger.error("Could not create the record business-journal", e);
		}
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef subscription = nodeAssocRef.getSourceRef();
		NodeRef employeeRef = nodeAssocRef.getTargetRef();
		List<String> objects = new ArrayList<String>();
		objects.add(employeeRef.toString());
		try {
			businessJournalService.log(subscription, EventCategory.DELETE, "Сотрудник #initiator удалил подписку #mainobject для подразделения #object1", objects);
		} catch (Exception e) {
			logger.error("Could not create the record business-journal", e);
		}
	}
}
