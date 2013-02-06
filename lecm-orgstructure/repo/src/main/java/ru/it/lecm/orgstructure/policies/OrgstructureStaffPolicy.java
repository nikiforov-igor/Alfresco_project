package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 15:07
 */
public class OrgstructureStaffPolicy
		extends SecurityJournalizedPolicyBase
		implements NodeServicePolicies.OnUpdatePropertiesPolicy 
{

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	public void init() {
		PropertyCheck.mandatory(this, "authService", authService);
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_LIST, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevPrimary = (Boolean) before.get(OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);
		final Boolean curPrimary = (Boolean) after.get(OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);
		final boolean changed = !PolicyUtils.safeEquals(prevPrimary, curPrimary);

		NodeRef employee = orgstructureService.getEmployeeByPosition(nodeRef);
		if (changed && employee != null) {
			NodeRef unit = nodeService.getPrimaryParent(nodeRef).getParentRef();

			String initiator = authService.getCurrentUserName();

			String category;
			String defaultDescription;
			if (curPrimary) {
				defaultDescription = "Сотрудник #mainobject назначен руководителем в подразделении #object1";
				category = EventCategory.TAKE_BOSS_POSITION;

			} else {
				defaultDescription = "Сотрудник #mainobject снят с руководящей позиции в подразделении #object1";
				category = EventCategory.RELEASE_BOSS_POSITION;
			}
			List<String> objects = new ArrayList<String>(1);
			objects.add(unit.toString());
			businessJournalService.log(initiator, employee, category, defaultDescription, objects);
		}
	}
}
