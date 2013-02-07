package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 06.02.13
 *         Time: 13:46
 */
public class OrgstructureStaffPositionPolicy {

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
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onCreateStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onUpdateStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onCreateStaffPosLog(ChildAssociationRef childAssocRef) {
		NodeRef staffPos = childAssocRef.getChildRef();
		businessJournalService.log(staffPos, EventCategory.ADD, "Сотрудник #initiator добавил новый элемент в справочник «Должностные позиции» -  #mainobject");
	}

	public void onUpdateStaffPosLog(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (before.size() == after.size() && !changed) {
			businessJournalService.log(nodeRef, EventCategory.EDIT, "Сотрудник #initiator внес изменения в сведения о должностной позиции #mainobject");
		}

		if (changed && !curActive) { // бьыли изменения во флаге и подразделение помечено как неактивное
			businessJournalService.log(nodeRef, EventCategory.DELETE, "Сотрудник #initiator удалил сведения о должностной позиции #mainobject");
		}
	}
}
