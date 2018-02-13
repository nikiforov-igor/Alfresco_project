package ru.it.lecm.errands.shedule.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.wcalendar.calendar.ICalendar;

import java.util.Date;
import java.util.Map;

/**
 * User: IGanin
 * Date: 17.11.2017
 * Time: 17:25
 */
public class ControlDateExceptionProcessor extends BaseCreationExceptionProcessor {

    private IWorkCalendar workCalendar;
    private ICalendar wCalendarService;

    public void setWorkCalendar(IWorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public void setwCalendarService(ICalendar wCalendarService) {
        this.wCalendarService = wCalendarService;
    }

    public boolean checkConditionsToProcess(Map<ProcessorParamName, Object> params) {
        final NodeRef periodicalErrand = (NodeRef) params.get(ProcessorParamName.PERIODICAL_ERRAND);
        Date controlDate = errandsService.calculatePeriodicalErrandControlDate(periodicalErrand);
        return controlDate != null && !wCalendarService.isWorkingDay(controlDate);
    }

    public void processException(final Map<ProcessorParamName, Object> params) {
        try {
            final NodeRef errandNodeRef = (NodeRef) params.get(ProcessorParamName.ERRAND);
            if (errandNodeRef != null) {
                Date errandDate = (Date) nodeService.getProperty(errandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
                // Переносим срок исполнения на ближайший рабочий день вперед/назад в зависимости от настройки
                final ErrandsService.ControlDeadlineNotWorkingDayAction action = errandsService.getControlDeadlineNotWorkingDayAction();
                if (ErrandsService.ControlDeadlineNotWorkingDayAction.MOVE_TO_NEXT_WORKING_DAY.equals(action)) {
                    final Date nextDate = workCalendar.getNextWorkingDateByDays(errandDate, 1);
                    nodeService.setProperty(errandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, ErrandsService.LimitationDateRadio.DATE.name());
                    nodeService.setProperty(errandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, nextDate);
                } else if (ErrandsService.ControlDeadlineNotWorkingDayAction.MOVE_TO_PREVIOUS_WORKING_DAY.equals(action)) {
                    final Date nextDate = workCalendar.getNextWorkingDateByDays(errandDate, -1);
                    nodeService.setProperty(errandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, ErrandsService.LimitationDateRadio.DATE.name());
                    nodeService.setProperty(errandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, nextDate);
                }
            }
        } catch (Throwable e) {
            logger.error("Failed to execute processor " + getClass().getSimpleName(), e);
        }
    }

    @Override
    public boolean isAllowCreation(Map<ProcessorParamName, Object> params) {
        return !ErrandsService.ControlDeadlineNotWorkingDayAction.DO_NOT_CREATE.equals(errandsService.getControlDeadlineNotWorkingDayAction());
    }
}
