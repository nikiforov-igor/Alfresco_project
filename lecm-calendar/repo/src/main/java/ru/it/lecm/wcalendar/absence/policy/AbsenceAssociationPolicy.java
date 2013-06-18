package ru.it.lecm.wcalendar.absence.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 *
 * @author vlevin
 */
public class AbsenceAssociationPolicy extends LogicECMAssociationPolicy {
	@Override
	public void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				IAbsence.TYPE_ABSENCE, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				IAbsence.TYPE_ABSENCE, new JavaBehaviour(this, "onCreateAssociation"));

	}
}
