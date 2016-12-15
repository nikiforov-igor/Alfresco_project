package ru.it.lecm.resolutions.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.resolutions.api.ResolutionsService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.Calendar;
import java.util.Date;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:41
 */
public class ResolutionsServiceImpl extends BaseBean implements ResolutionsService {
    private IWorkCalendar calendarBean;

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public Date calculateResolutionExecutionDate(String radio, Integer days, String daysType, Date date) {
        if (EXECUTION_DATE_RADIO_DATE.equals(radio) && date != null) {
            return date;
        } else if (EXECUTION_DATE_RADIO_DAYS.equals(radio) && days != null && daysType != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 12);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if (EXECUTION_DATE_DAYS_WORK.equals(daysType)) {
                return calendarBean.getNextWorkingDateByDays(cal.getTime(), days);
            } else if (EXECUTION_DATE_DAYS_CALENDAR.equals(daysType)) {
                cal.add(Calendar.DAY_OF_YEAR, days);
                return cal.getTime();
            }
        }
        return null;
    }
}
