package ru.it.lecm.wcalendar.shedule.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.wcalendar.shedule.IShedule;

/**
 * Полиси, срабатывающее на создание ассоциации с графиком работы. Если это
 * ассоциация с сотрудником или огр. единице, то это означает, что создан новый
 * график работ и надо сделать запись в бизнес-журнал.
 *
 * @author vlevin
 */
public class SheduleCreatePolicy implements NodeServicePolicies.OnCreateAssociationPolicy {

	private IShedule sheduleService;
	private PolicyComponent policyComponent;
	private final static Logger logger = LoggerFactory.getLogger(SheduleCreatePolicy.class);

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "sheduleService", sheduleService);

		logger.info("Initializing SheduleCreatePolicy");
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, IShedule.TYPE_SHEDULE, new JavaBehaviour(this, "onCreateAssociation"));

	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setSheduleService(IShedule sheduleService) {
		this.sheduleService = sheduleService;
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		final QName assocTypeQName = nodeAssocRef.getTypeQName();
		if (IShedule.ASSOC_SHEDULE_EMPLOYEE_LINK.equals(assocTypeQName)) {
			final NodeRef nodeRef = nodeAssocRef.getSourceRef();
			sheduleService.addBusinessJournalRecord(nodeRef, EventCategory.ADD);
			logger.debug(String.format("Policy SheduleCreatePolicy invoked on %s", nodeRef.toString()));
		}
	}
}
