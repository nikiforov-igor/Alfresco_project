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
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 06.02.13
 * Time: 13:36
 */
public class SubscriptionsToObjectLogEventsPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
	final protected Logger logger = LoggerFactory.getLogger(SubscriptionsToObjectLogEventsPolicy.class);

	private static PolicyComponent policyComponent;
	private static BusinessJournalService businessJournalService;
	private OrgstructureBean orgstructureService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public final void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_OBJECT, SubscriptionsService.ASSOC_SUBSCRIPTION_OBJECT,
				new JavaBehaviour(this, "onCreateAssociation"));

	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef subscription = nodeAssocRef.getSourceRef();
		NodeRef objectRef = nodeAssocRef.getTargetRef();
		List<String> objects = new ArrayList<String>();
		objects.add(objectRef.toString());
		try {
			if (orgstructureService.isEmployee(objectRef)) {
				businessJournalService.log(subscription, EventCategory.ADD, "#initiator подписался(ась) на действия сотрудника  #object1", objects);
			} else if (orgstructureService.isWorkGroup(objectRef)) {
				businessJournalService.log(subscription, EventCategory.ADD, "#initiator подписался(ась) на действия рабочей группы #object1", objects);
			} else {
				businessJournalService.log(subscription, EventCategory.ADD, "#initiator подписался(ась) на #object1", objects);
			}
		} catch (Exception e) {
			logger.error("Could not create the record business-journal", e);
		}
	}
}
