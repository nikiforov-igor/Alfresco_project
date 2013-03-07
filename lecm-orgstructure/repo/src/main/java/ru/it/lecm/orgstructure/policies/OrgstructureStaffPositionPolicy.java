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
import ru.it.lecm.security.Types;

/**
 * @author dbashmakov
 *         Date: 06.02.13
 *         Time: 13:46
 */
public class OrgstructureStaffPositionPolicy
		extends SecurityJournalizedPolicyBase
{
	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public final void init() {
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onCreateStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onUpdateStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onDeleteStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onCreateStaffPosLog(ChildAssociationRef childAssocRef) {
		final NodeRef staffPos = childAssocRef.getChildRef();
		businessJournalService.log(staffPos, EventCategory.ADD, "Сотрудник #initiator добавил новый элемент в справочник «Должностные позиции» -  #mainobject");

		// оповещение по должности для создания SG_DP ...
		this.orgSGNotifier.notifyChangeDP( staffPos);
	}

	public void onUpdateStaffPosLog(NodeRef staffPos, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (before.size() == after.size() && !changed) {
			businessJournalService.log(staffPos, EventCategory.EDIT, "Сотрудник #initiator внес изменения в сведения о должностной позиции #mainobject");
		}

		if (changed && !curActive) { // бьыли изменения во флаге и подразделение помечено как неактивное
			businessJournalService.log(staffPos, EventCategory.DELETE, "Сотрудник #initiator удалил сведения о должностной позиции #mainobject");
		}

		if (changed) {
			final Types.SGDeputyPosition sgDP = (Types.SGDeputyPosition) PolicyUtils.makeDeputyPos(staffPos, nodeService, orgstructureService, logger);
			// оповещение по должности для связывания/отвязки SG_DP ...
			if (curActive) {
				this.orgSGNotifier.notifyNodeCreated(sgDP);
				this.orgSGNotifier.notifyChangeDP( staffPos);
			} else
				this.orgSGNotifier.notifyNodeDeactivated(sgDP);

		}
	}

	public void onDeleteStaffPosLog(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		final NodeRef staffPos = childAssocRef.getChildRef();
		final Types.SGDeputyPosition sgDP = (Types.SGDeputyPosition) PolicyUtils.makeDeputyPos(staffPos, nodeService, orgstructureService, logger);
		this.orgSGNotifier.notifyNodeDeactivated(sgDP);

	}

}
