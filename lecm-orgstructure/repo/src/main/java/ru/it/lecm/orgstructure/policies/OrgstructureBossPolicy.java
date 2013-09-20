package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 11.12.12
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class OrgstructureBossPolicy
		extends SecurityNotificationsPolicyBase
		implements NodeServicePolicies.OnCreateNodePolicy
				// , NodeServicePolicies.OnUpdateNodePolicy
{
	@Override
	public void init() {
		super.init();
		policyComponent.bindClassBehaviour(
				NodeServicePolicies.OnCreateNodePolicy.QNAME
			, OrgstructureBean.TYPE_STAFF_LIST
			, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT)
		);
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		final NodeRef staff = childAssocRef.getChildRef(); // type: "lecm-orgstr:staff-list"
		final NodeRef orgUnit = childAssocRef.getParentRef();
		List<NodeRef> staffList = orgstructureService.getUnitStaffLists(orgUnit);
		final boolean isBoss = staffList.size() == 1 && staffList.get(0).equals(staff);
		nodeService.setProperty(staff, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS, isBoss);

		// оповещение securityService по Должностной Позиции ...
		// final NodeRef nodeDP = this.orgstructureService.getPositionByStaff(staff);
		notifyChangeDP( staff, isBoss, orgUnit);
	}

}
