package ru.it.lecm.orgstructure.policies;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 15:07
 */
public class OrgstructureWorkForcePolicy
		extends SecurityJournalizedPolicyBase implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnDeleteNodePolicy {

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	public void init() {
		super.init();
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_WORKFORCE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_WORKFORCE, new JavaBehaviour(this, "onDeleteNode"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef workforce = childAssocRef.getChildRef();
		NodeRef group = orgstructureService.getWorkGroupByWorkForce(workforce);

		final List<String> objects = new ArrayList<String>(1);
		objects.add(workforce.toString());

		businessJournalService.log(group, EventCategory.ADD_GROUP_ROLE, "Сотрудник #initiator внес сведения о добавлении роли #object1 в рабочую группу #mainobject", objects);
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		if (!isNodeArchived) {
			NodeRef workforce = childAssocRef.getChildRef();
			NodeRef group = orgstructureService.getWorkGroupByWorkForce(workforce);

			final List<String> objects = new ArrayList<String>(1);
			objects.add(workforce.toString());

			businessJournalService.log(group, EventCategory.REMOVE_GROUP_ROLE, "Сотрудник #initiator внес сведения об исключении роли #object1 из рабочей группы #mainobject", objects);
		}
	}
}
