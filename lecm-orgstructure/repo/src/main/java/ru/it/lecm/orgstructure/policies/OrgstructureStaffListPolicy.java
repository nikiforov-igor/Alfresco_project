package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 15:07
 */
public class OrgstructureStaffListPolicy
		extends SecurityJournalizedPolicyBase
{

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	public void init() {
		super.init();
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_LIST, new JavaBehaviour(this, "onCreateStaffListLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_LIST, new JavaBehaviour(this, "onUpdateStaffListLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_LIST, new JavaBehaviour(this, "onDeleteStaffListLog"));
	}

	public void onDeleteStaffListLog(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		if (!isNodeArchived) {
			NodeRef staff = childAssocRef.getChildRef();
			NodeRef unit = orgstructureService.getUnitByStaff(staff);

			final List<String> objects = new ArrayList<String>(1);
			objects.add(staff.toString());

			businessJournalService.log(unit, EventCategory.REMOVE_STAFF_POSITION, "Сотрудник #initiator внес сведения об исключении должности #object1 из подразделения #mainobject", objects);
		}
	}

	public void onCreateStaffListLog(ChildAssociationRef childAssocRef) {
		NodeRef staff = childAssocRef.getChildRef();
		NodeRef unit = orgstructureService.getUnitByStaff(staff);

		final List<String> objects = new ArrayList<String>(1);
		objects.add(staff.toString());

		businessJournalService.log(unit, EventCategory.ADD_STAFF_POSITION, "Сотрудник #initiator  внес сведения о добавлении должности #object1 в подразделение #mainobject", objects);
	}

	public void onUpdateStaffListLog(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevPrimary = (Boolean) before.get(OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);
		final Boolean curPrimary = (Boolean) after.get(OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);
		final boolean changed = !PolicyUtils.safeEquals(prevPrimary, curPrimary);

		NodeRef employee = orgstructureService.getEmployeeByPosition(nodeRef);
		if (changed && employee != null) {
			NodeRef unit = nodeService.getPrimaryParent(nodeRef).getParentRef();

			String category;
			String defaultDescription;
			if (curPrimary) {
				defaultDescription = "Сотрудник #inititator внес сведения о назначении Сотрудника #mainobject руководителем подразделения #object1";
				category = EventCategory.TAKE_BOSS_POSITION;

			} else {
				defaultDescription = "Сотрудник #inititator внес сведения о снятии Сотрудника #mainobject с руководящей позиции в подразделении #object1";
				category = EventCategory.RELEASE_BOSS_POSITION;
			}
			List<String> objects = new ArrayList<String>(1);
			objects.add(unit.toString());
			businessJournalService.log(employee, category, defaultDescription, objects);
		}
	}
}
