package ru.it.lecm.base.processors;

import org.alfresco.service.cmr.preference.PreferenceService;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * User: aplam
 * Date: 10.08.2016
 * Time: 10:00
 */
public class InTwoWorkingDaysProcessor extends SearchQueryProcessor {

	public static final DateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private IWorkCalendar calendarBean;
    
    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Calendar now = Calendar.getInstance();
		Date start = now.getTime();
		Date end = calendarBean.getNextWorkingDate(start, 2, Calendar.DAY_OF_MONTH);        
        sbQuery.append("[\"").append(DateFormat.format(start)).append("\" TO \"").append(DateFormat.format(end)).append("\"]");
        return sbQuery.toString();
    }
    
	public void setCalendarBean(IWorkCalendar calendarBean) {
		this.calendarBean = calendarBean;
	}
}
