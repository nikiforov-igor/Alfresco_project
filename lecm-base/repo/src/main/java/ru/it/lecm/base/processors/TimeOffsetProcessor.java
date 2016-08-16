package ru.it.lecm.base.processors;

import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * User: apalm Date: 10.08.2016 Time: 10:00
 */
public class TimeOffsetProcessor extends SearchQueryProcessor {

	public static final DateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final String MINUTE = "MINUTE";
	private static final String HOUR = "HOUR";
	private static final String DAY = "DAY";

	private IWorkCalendar calendarBean;

	/*
	 * Usage example: {{TIME_OFFSET({metricUnit:'DAY',value:2})}}
	 */

	@Override
	public String getQuery(Map<String, Object> params) {
		StringBuilder sbQuery = new StringBuilder();

		String metricUnit = "";
		Integer value = null;

		Object metricUnitObject = params != null ? params.get("metricUnit") : null;
		if (metricUnitObject != null) {
			metricUnit = metricUnitObject.toString();
		} else {
			metricUnit = "DAY";
		}

		Object valueObject = params != null ? params.get("value") : null;
		if (valueObject != null) {
			value = (Integer) valueObject;
		} else {
			value = 1;
		}

		Calendar now = Calendar.getInstance();
		Date start = now.getTime();
		Date end = null;

		int timeUnit = 0;
		switch (metricUnit) {
		case MINUTE:
			timeUnit = Calendar.MINUTE;
			break;
		case HOUR:
			timeUnit = Calendar.HOUR_OF_DAY;
			break;
		case DAY:
			timeUnit = Calendar.DAY_OF_MONTH;
			break;
		default:
			timeUnit = Calendar.DAY_OF_MONTH;
		}
		
		end = calendarBean.getNextWorkingDate(start, value, timeUnit);
		sbQuery.append("[\"").append(DateFormat.format(start)).append("\" TO \"").append(DateFormat.format(end))
				.append("\"]");
		return sbQuery.toString();
	}

	public void setCalendarBean(IWorkCalendar calendarBean) {
		this.calendarBean = calendarBean;
	}
}
