package ru.it.lecm.eds;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: dbashmakov
 * Date: 24.01.14
 * Time: 12:48
 */
public class EDSDocumentServiceImpl extends BaseBean implements EDSDocumentService {
    private final static Logger logger = LoggerFactory.getLogger(EDSDocumentServiceImpl.class);

    private String calendarDayTypeString = "к.д.";
    private String workDayTypeString = "р.д.";
    private String limitlessString = "Без срока";

    private IWorkCalendar calendarBean;

    public void setCalendarDayTypeString(String calendarDayTypeString) {
        this.calendarDayTypeString = calendarDayTypeString;
    }

    public void setWorkDayTypeString(String workDayTypeString) {
        this.workDayTypeString = workDayTypeString;
    }

    public void setLimitlessString(String limitlessString) {
        this.limitlessString = limitlessString;
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public void sendChildChangeSignal(NodeRef baseDoc) {
        try {
            Integer currentCount = (Integer) nodeService.getProperty(baseDoc, PROP_CHILD_CHANGE_SIGNAL_COUNT);
            if (currentCount != null) {
                currentCount++;
            } else {
                currentCount = 1;
            }

            nodeService.setProperty(baseDoc, PROP_CHILD_CHANGE_SIGNAL_COUNT, currentCount);
        } catch (ConcurrencyFailureException ex) {
            logger.warn("Send signal at the same time", ex);
        }
    }

    @Override
    public void resetChildChangeSignal(NodeRef baseDoc) {
        try {
            nodeService.setProperty(baseDoc, PROP_CHILD_CHANGE_SIGNAL_COUNT, 0);
        } catch (ConcurrencyFailureException ex) {
            logger.warn("Send signal at the same time", ex);
        }
    }

    @Override
    public String getComplexDateText(String radio, Date date, String daysType, Integer daysCount) {
        String result = null;
        if (COMPLEX_DATE_RADIO_LIMITLESS.equals(radio)) {
            result = this.limitlessString;
        } else if (COMPLEX_DATE_RADIO_DATE.equals(radio) && date != null) {
            DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
            result = formater.format(date);
        } else if (COMPLEX_DATE_RADIO_DAYS.equals(radio) && daysType != null && daysCount != null) {
            if (COMPLEX_DATE_DAYS_WORK.equals(daysType)) {
                result = daysCount + " " + workDayTypeString;
            } else if (COMPLEX_DATE_DAYS_CALENDAR.equals(daysType)) {
                result = daysCount + " " + calendarDayTypeString;
            }
        }
        return result;
    }

    @Override
    public Date convertComplexDate(String radio, Date date, String daysType, Integer daysCount) {
        if (COMPLEX_DATE_RADIO_DATE.equals(radio)) {
            return date;
        } else if (COMPLEX_DATE_RADIO_DAYS.equals(radio) && daysCount != null && daysType != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 12);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if (COMPLEX_DATE_DAYS_WORK.equals(daysType)) {
                return calendarBean.getNextWorkingDateByDays(cal.getTime(), daysCount);
            } else if (COMPLEX_DATE_DAYS_CALENDAR.equals(daysType)) {
                cal.add(Calendar.DAY_OF_YEAR, daysCount);
                return cal.getTime();
            }
        }
        return null;
    }
}
