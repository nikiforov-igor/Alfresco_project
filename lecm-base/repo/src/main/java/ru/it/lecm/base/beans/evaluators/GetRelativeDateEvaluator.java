package ru.it.lecm.base.beans.evaluators;

import org.json.JSONException;
import org.json.JSONObject;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.Calendar;
import java.util.Date;

/**
 * User: dbashmakov
 * Date: 05.05.2017
 * Time: 9:39
 */
public class GetRelativeDateEvaluator extends ValueEvaluator {
    private final String DAY_MODE = "daysMode";
    private final String DAYS_COUNT = "days";
    private final String DATE = "date";
    private final String DATE_MODE = "mode";

    private final String WORK = "WORK";
    private final String CALENDAR = "CALENDAR";

    private IWorkCalendar calendarBean;

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    @Override
    public String evaluate(JSONObject config) throws JSONException {
        String dayMode = config.has(DAY_MODE) ? config.getString(DAY_MODE) : "CALENDAR";
        int days = config.has(DAYS_COUNT) ? config.getInt(DAYS_COUNT) : 0;
        String isoDate = config.has(DATE) ? config.getString(DATE) : null;
        boolean isRelative = config.has(DATE_MODE) && config.getString(DATE_MODE).equals("RELATIVE");

        if (isRelative) {
            Calendar now = Calendar.getInstance();
            switch (dayMode) {
                case WORK: {
                    Date nextDate = calendarBean.getNextWorkingDate(now.getTime(), days, Calendar.DAY_OF_MONTH);
                    if (nextDate != null) {
                        isoDate = BaseBean.DateFormatISO8601.format(nextDate);
                    }
                }
                break;
                case CALENDAR: {
                    now.add(Calendar.DAY_OF_MONTH, days);
                    isoDate = BaseBean.DateFormatISO8601.format(now.getTime());
                }
                break;
            }
        }

        return isoDate;
    }
}
