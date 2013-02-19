package ru.it.lecm.wcalendar.absence.shedule;

import java.util.List;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Действие для старта отсутствия. Используется в шедулере
 * (AbsenceStartShedule).
 *
 * @see ru.it.lecm.wcalendar.absence.shedule.AbsenceStartShedule
 *
 * @author vlevin
 */
public class AbsenceStartScheduleExecutor extends ActionExecuterAbstractBase {

	private final static Logger logger = LoggerFactory.getLogger(AbsenceStartScheduleExecutor.class);
	private IAbsence absenceService;

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		logger.debug(String.format("Absence [%s] is starting.", nodeRef.toString()));
		absenceService.startAbsence(nodeRef);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
	}
}
