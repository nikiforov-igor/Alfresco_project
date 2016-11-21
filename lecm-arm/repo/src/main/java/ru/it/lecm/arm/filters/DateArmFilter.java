package ru.it.lecm.arm.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.arm.beans.filters.ArmDocumentsFilter;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: AIvkin
 * Date: 04.03.14
 * Time: 9:27
 */
public class DateArmFilter implements ArmDocumentsFilter {
	final private static Logger logger = LoggerFactory.getLogger(DateArmFilter.class);
	public static final DateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static enum DateFilterEnum {
		OVERDUE,
		IN_THE_WORK,
		WITH_UPCOMING_PERIOD,
		ALL
	}

	private IWorkCalendar calendarBean;

	public void setCalendarBean(IWorkCalendar calendarBean) {
		this.calendarBean = calendarBean;
	}

	@Override
	public String getQuery(String fields, List<String> args) {
		StringBuilder resultedQuery = new StringBuilder();
		if (args != null && !args.isEmpty() && fields != null && !fields.isEmpty()) {
			logger.debug("Filter args: " + StringUtils.collectionToCommaDelimitedString(args));
			logger.debug("Filter params: " + fields);

			String filterValue = args.get(0);

			if (filterValue == null) {
				return resultedQuery.toString();
			}

			try {
				String dateCompareQuery = "";
				Calendar now = Calendar.getInstance();
				switch (DateFilterEnum.valueOf(filterValue.toUpperCase())) {
					case OVERDUE: {
						now.add(Calendar.DAY_OF_MONTH, - 1);
						dateCompareQuery = "[MIN TO \"" + DateFormat.format(now.getTime()) + "\"]";
						break;
					}
					case IN_THE_WORK: {
						dateCompareQuery = "[\"" + DateFormat.format(now.getTime()) + "\" TO MAX]";
						break;
					}
					case WITH_UPCOMING_PERIOD: {
						Date start = now.getTime();
						Date end = calendarBean.getNextWorkingDate(start, 2, Calendar.DAY_OF_MONTH);
						dateCompareQuery = "[\"" + DateFormat.format(start) + "\" TO \"" + DateFormat.format(end) + "\"]";
						break;
					}
					case ALL: {
						break;
					}
					default: {
						break;
					}
				}

				if (!dateCompareQuery.isEmpty()) {
					boolean addOR = false;
					for (String field : fields.split(",")) {
						String fieldProp = field.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

						resultedQuery.append(addOR ? " OR " : "").append("@").append(fieldProp).append(":").append(dateCompareQuery);
						addOR = true;
					}
				}
			} catch (Exception ignored) {
				logger.warn("Incorrect filter! Filter args:" + StringUtils.collectionToCommaDelimitedString(args));
			}
		}
		return resultedQuery.toString();
	}
}
