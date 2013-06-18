package ru.it.lecm.wcalendar.absence.policy;

import java.util.Date;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Полиси, срабатывающая на создание ассоциции у объекта типа absence. Если
 * создана ассоциация с employee (abscent-employee-assoc), то значит, что
 * создано новое отсутствие. Если начало отсутствия назначего на сегодня, то
 * следует его запустить.
 *
 * @author vlevin
 */
public class AbsenceStartPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {

	private IAbsence absenceService;
	private PolicyComponent policyComponent;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceStartPolicy.class);

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "absenceService", absenceService);

		logger.info("Initializing AbsenceStartPolicy");
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, IAbsence.TYPE_ABSENCE, new JavaBehaviour(this, "onCreateAssociation"));
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		final QName assocTypeQName = nodeAssocRef.getTypeQName();
		if (IAbsence.ASSOC_ABSENCE_EMPLOYEE.equals(assocTypeQName)) {
			final NodeRef nodeRef = nodeAssocRef.getSourceRef();
			final Date today = new Date();
			final Date absenceStart = absenceService.getAbsenceStartDate(nodeRef);
			absenceService.addBusinessJournalRecord(nodeRef, EventCategory.ADD);
			if (DateUtils.isSameDay(today, absenceStart)) {
				absenceService.startAbsence(nodeRef);
				logger.debug(String.format("Policy AbsenceStartPolicy invoked on %s for employee %s", nodeRef.toString(), nodeAssocRef.getTargetRef().toString()));
			}
		}
	}
}
