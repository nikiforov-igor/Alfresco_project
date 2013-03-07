package ru.it.lecm.wcalendar.schedule.policy;

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
import ru.it.lecm.wcalendar.schedule.ISchedule;

/**
 * Полиси, срабатывающее на создание ассоциации с графиком работы. Если это
 * ассоциация с сотрудником или огр. единице, то это означает, что создан новый
 * график работ и надо сделать запись в бизнес-журнал.
 *
 * @author vlevin
 */
public class ScheduleCreatePolicy implements NodeServicePolicies.OnCreateAssociationPolicy {

	private ISchedule scheduleService;
	private PolicyComponent policyComponent;
	private final static Logger logger = LoggerFactory.getLogger(ScheduleCreatePolicy.class);

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "scheduleService", scheduleService);

		logger.info("Initializing ScheduleCreatePolicy");
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, ISchedule.TYPE_SCHEDULE, new JavaBehaviour(this, "onCreateAssociation"));

	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setScheduleService(ISchedule scheduleService) {
		this.scheduleService = scheduleService;
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		final QName assocTypeQName = nodeAssocRef.getTypeQName();
		if (ISchedule.ASSOC_SCHEDULE_EMPLOYEE_LINK.equals(assocTypeQName)) {
			final NodeRef nodeRef = nodeAssocRef.getSourceRef();
			scheduleService.addBusinessJournalRecord(nodeRef, EventCategory.ADD);
			logger.debug(String.format("Policy ScheduleCreatePolicy invoked on %s", nodeRef.toString()));
		}
	}
}
