package ru.it.lecm.businessjournal.policies;

import java.io.Serializable;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * @author dbashmakov
 *         Date: 05.02.13
 *         Time: 12:05
 */
public class BusinessJournalOnCreateAssocsPolicy extends LogicECMAssociationPolicy {

	private BusinessJournalService businessJournalService;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	public final void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				BusinessJournalService.TYPE_BR_RECORD, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				BusinessJournalService.TYPE_BR_RECORD, new JavaBehaviour(this, "onCreateAssociation"));

	}
	@Override
	protected Serializable getSerializable(final NodeRef node){
		return businessJournalService.getObjectDescription(node);
	}

}
