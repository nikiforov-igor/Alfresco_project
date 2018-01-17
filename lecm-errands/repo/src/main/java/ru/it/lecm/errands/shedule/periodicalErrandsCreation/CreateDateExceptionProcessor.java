package ru.it.lecm.errands.shedule.periodicalErrandsCreation;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang3.time.DateFormatUtils;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.shedule.exceptionProcessor.ProcessorParamName;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.wcalendar.calendar.ICalendar;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: IGanin
 * Date: 17.11.2017
 * Time: 17:25
 */
public class CreateDateExceptionProcessor extends BaseCreationExceptionProcessor {

    private IWorkCalendar workCalendar;
    private ICalendar wCalendarService;

    public void setWorkCalendar(IWorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public void setwCalendarService(ICalendar wCalendarService) {
        this.wCalendarService = wCalendarService;
    }

    @Override
    public boolean checkConditionsToProcess(Map<ProcessorParamName, Object> params) {
        return !wCalendarService.isWorkingDay(new Date());
    }

    @Override
    public void processException(final Map<ProcessorParamName, Object> params) {
        try {
            final NodeRef periodicalErrandNodeRef = (NodeRef) params.get(ProcessorParamName.PERIODICAL_ERRAND);
            if (periodicalErrandNodeRef != null) {
                // дата создания приходится на нерабочий день - обработать в соответствии с настройкой
                final ErrandsService.CreateDateNotWorkingDayAction createDateNotWorkingDayAction = errandsService.getCreateDateNotWorkingDayAction();
                if (ErrandsService.CreateDateNotWorkingDayAction.MOVE_TO_NEXT_WORKING_DAY.equals(createDateNotWorkingDayAction)) {
                    // Переносим создание поручения вперед на ближайший рабочий день.
                    // Сохраняем информацию чтобы создать поручение когда наступит ближайший рабочий день
                    final Date nextDate = workCalendar.getNextWorkingDateByDays(new Date(), 1);
                    final String nextDateStr = DateFormatUtils.format(nextDate, "dd-MM-yyyy");
                    final Map<String, Set<NodeRef>> delayedErrandsByDate = errandsService.getDelayedErrandsByDate();
                    if (!delayedErrandsByDate.containsKey(nextDateStr)) {
                        delayedErrandsByDate.put(nextDateStr, new HashSet<NodeRef>());
                    }
                    delayedErrandsByDate.get(nextDateStr).add(periodicalErrandNodeRef);
                    errandsService.setDelayedErrandsByDate(delayedErrandsByDate);
                } else if (ErrandsService.CreateDateNotWorkingDayAction.DO_NOT_CREATE.equals(createDateNotWorkingDayAction)) {
                    // ignore creation
                }
            }
        } catch (Throwable e) {
            logger.error("Failed to execute processor " + getClass().getSimpleName(), e);
        }
    }

    @Override
    public boolean isAllowCreation(Map<ProcessorParamName, Object> params) {
        return wCalendarService.isWorkingDay(new Date());
    }
}
