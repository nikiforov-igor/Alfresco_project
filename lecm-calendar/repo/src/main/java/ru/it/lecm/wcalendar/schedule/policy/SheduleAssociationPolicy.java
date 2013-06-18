package ru.it.lecm.wcalendar.schedule.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.wcalendar.schedule.ISchedule;

/**
 *
 * @author vlevin
 */
public class SheduleAssociationPolicy extends LogicECMAssociationPolicy {

	@Override
	public void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				ISchedule.TYPE_SCHEDULE, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ISchedule.TYPE_SCHEDULE, new JavaBehaviour(this, "onCreateAssociation"));

	}
}
