package ru.it.lecm.orgstructure.policies;

import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;

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
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_LIST, new JavaBehaviour(this, "onCreateNode"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef nodeDP = childAssocRef.getChildRef();
		NodeRef orgUnit = childAssocRef.getParentRef();
		List<NodeRef> staffList = orgstructureService.getUnitStaffLists(orgUnit);
		final boolean isBoss = staffList.size() >= 1 && staffList.get(0).equals(nodeDP);
		nodeService.setProperty(nodeDP, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS, isBoss);

		// оповещение securityService по Должностной Позиции ...
		notifyChangeDP( nodeDP, isBoss, orgUnit);
	}

}
