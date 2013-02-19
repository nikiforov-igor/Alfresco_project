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
		Calendar cal = Calendar.getInstance();
		// включить следующую строку, если шедулер будет запускаться не в начале дня, а в конце
//		cal.add(Calendar.DATE, 1); 
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		Date today = cal.getTime();
		absenceService.setAbsenceEnd(nodeRef, today);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
	}
}
