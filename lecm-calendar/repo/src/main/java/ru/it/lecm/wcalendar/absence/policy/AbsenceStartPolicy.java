package ru.it.lecm.wcalendar.absence.policy;

import java.util.Calendar;
import java.util.Date;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.CalendarCategory;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Полиси, срабатывающая на создание ассоциции у объекта типа absence. Если
 * создана ассоциация с employee (abscent-employee-assoc), то значит, что
 * создано новое отсутствие. Если начало отсутствия назначего на сегодня, то
 * следует его запустить.
 *
 * @author vlevin
 */
public class AbsenceStartPolicy implements OnCreateAssociationPolicy {

	private IAbsence absenceService;
	private PolicyComponent policyComponent;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceStartPolicy.class);

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "absenceService", absenceService);

		logger.info("Initializing AbsenceStartPolicy");
		policyComponent.bindAssociationBehaviour(OnCreateAssociationPolicy.QNAME, IAbsence.TYPE_ABSENCE, IAbsence.ASSOC_ABSENCE_EMPLOYEE, new JavaBehaviour(this, "onCreateAssociation"));
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef absenceRef = nodeAssocRef.getSourceRef();
		Date absenceBegin = absenceService.getAbsenceStartDate(absenceRef);
		Date absenceEnd = absenceService.getAbsenceEndDate(absenceRef);
		absenceBegin = DateUtils.truncate(absenceBegin, Calendar.DATE);
		absenceEnd = DateUtils.setHours(absenceEnd, 23);
		absenceEnd = DateUtils.setMinutes(absenceEnd, 59);
		absenceEnd = DateUtils.setSeconds(absenceEnd, 59);
		absenceEnd = DateUtils.setMilliseconds(absenceEnd, 0);
		absenceService.setAbsenceBegin(absenceRef, absenceBegin);
		absenceService.setAbsenceEnd(absenceRef, absenceEnd);
		Date today = new Date();
		absenceService.addBusinessJournalRecord(absenceRef, CalendarCategory.ADD_ABSENCE);
		if (absenceBegin.compareTo(today) <= 0) {
			absenceService.startAbsence(absenceRef);
			logger.debug(String.format("Policy AbsenceStartPolicy invoked on %s for employee %s", absenceRef.toString(), nodeAssocRef.getTargetRef().toString()));
		}
	}
}
