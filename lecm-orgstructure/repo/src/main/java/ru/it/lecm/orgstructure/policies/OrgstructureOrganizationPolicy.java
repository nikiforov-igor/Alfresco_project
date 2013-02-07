package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 06.02.13
 *         Time: 11:29
 */
public class OrgstructureOrganizationPolicy {

	private PolicyComponent policyComponent;
	private BusinessJournalService businessJournalService;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION, new JavaBehaviour(this, "onUpdatePropertiesLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onUpdatePropertiesLog(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		if (before.size() == after.size()) {
			businessJournalService.log(nodeRef, EventCategory.EDIT, "Сотрудник #initiator внес изменения в сведения об организации", null);
		}
	}
}
