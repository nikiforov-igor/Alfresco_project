package ru.it.lecm.wcalendar.absence.shedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Действие для продления бессрочного отсутствия. Используется в шедулере
 * (AbsenceProlongationShedule).
 *
 * @see ru.it.lecm.wcalendar.absence.shedule.AbsenceProlongationShedule
 *
 * @author vlevin
 */
public class AbsenceProlongationScheduleExecutor extends ActionExecuterAbstractBase {

	private final static Logger logger = LoggerFactory.getLogger(AbsenceProlongationScheduleExecutor.class);
	private IAbsence absenceService;

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		logger.debug(String.format("Absence [%s] prolonged.", nodeRef.toString()));
		Date absenceEnd = absenceService.getAbsenceEndDate(nodeRef);
		Calendar cal = Calendar.getInstance();
		cal.setTime(absenceEnd);
		cal.add(Calendar.HOUR_OF_DAY, 24);
		Date tomorrow = cal.getTime();

		absenceService.addBusinessJournalRecord(nodeRef, EventCategory.EDIT);
		absenceService.setAbsenceEnd(nodeRef, tomorrow);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
	}
}
