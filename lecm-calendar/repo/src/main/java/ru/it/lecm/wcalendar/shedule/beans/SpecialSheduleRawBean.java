package ru.it.lecm.wcalendar.shedule.beans;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;

public class SpecialSheduleRawBean {

	private Date timeWorkBegins;
	private Date timeWorkEnds;
	private Date timeLimitStart;
	private Date timeLimitEnd;
	private Map<Integer, Boolean> weekDays = new HashMap<Integer, Boolean>();
	private List<Integer> monthDays;
	private ReiterationType reiterationType;
	private int workingDaysAmount;
	private int workingDaysInterval;
	private DateFormat timeParser = new SimpleDateFormat("HH:mm");
	private DateFormat dateParser1 = new SimpleDateFormat("yyyy-MM-dd"); // 2013-12-30
	private DateFormat dateParser2 = new SimpleDateFormat("d/M/yyyy"); // 30/9/2013
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialSheduleRawBean.class);

	public static enum ReiterationType {

		SHIFT, WEEK_DAYS, MONTH_DAYS;
	}

	public void SpecialSheduleRaw() {
		this.weekDays.put(Calendar.MONDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.TUESDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.WEDNESDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.THURSDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.FRIDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.SATURDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.SUNDAY, Boolean.FALSE);
	}

	public Date getTimeLimitStart() {
		return timeLimitStart;
	}

	public void setTimeLimitStart(String timeLimitStart) {
		try {
			setTimeLimitStart(dateParser1.parse(timeLimitStart));
		} catch (ParseException x) {
			try {
				setTimeLimitStart(dateParser2.parse(timeLimitStart));
			} catch (ParseException ex) {
				throw new WebScriptException("Can not parse " + timeLimitStart + " as Date! " + ex.getMessage(), ex);
			}
		}
	}

	public void setTimeLimitStart(Date timeLimitStart) {
		this.timeLimitStart = timeLimitStart;
	}

	public Date getTimeLimitEnd() {
		return timeLimitEnd;
	}

	public void setTimeLimitEnd(String timeLimitEnd) {
		try {
			setTimeLimitEnd(dateParser1.parse(timeLimitEnd));
		} catch (ParseException x) {
			try {
				setTimeLimitEnd(dateParser2.parse(timeLimitEnd));
			} catch (ParseException ex) {
				throw new WebScriptException("Can not parse " + timeLimitEnd + " as Date! " + ex.getMessage(), ex);
			}
		}
	}

	public void setTimeLimitEnd(Date timeLimitEnd) {
		this.timeLimitEnd = timeLimitEnd;
	}

	public void setTimeWorkBegins(String timeBeginStr) {
		try {
			setTimeWorkBegins(timeParser.parse(timeBeginStr));
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + timeBeginStr + " as Date! " + ex.getMessage(), ex);
		}
	}

	public void setTimeWorkBegins(Date timeWorkBegins) {
		this.timeWorkBegins = timeWorkBegins;
	}

	public Date getTimeWorkBegins() {
		return this.timeWorkBegins;
	}

	public void setTimeWorkEnds(String timeEndStr) {
		try {
			setTimeWorkEnds(timeParser.parse(timeEndStr));
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + timeEndStr + " as Date! " + ex.getMessage(), ex);
		}
	}

	public void setTimeWorkEnds(Date timeWorkEnds) {
		this.timeWorkEnds = timeWorkEnds;
	}

	public Date getTimeWorkEnds() {
		return this.timeWorkEnds;
	}

	public void setMonthDays(List<Integer> monthDays) {
		this.monthDays = monthDays;
	}

	public List<Integer> getMonthDays() {
		return this.monthDays;
	}

	public void setWeekDays(List<Boolean> weekDaysList) {
		this.weekDays.put(Calendar.MONDAY, weekDaysList.get(0));
		this.weekDays.put(Calendar.TUESDAY, weekDaysList.get(1));
		this.weekDays.put(Calendar.WEDNESDAY, weekDaysList.get(2));
		this.weekDays.put(Calendar.THURSDAY, weekDaysList.get(3));
		this.weekDays.put(Calendar.FRIDAY, weekDaysList.get(4));
		this.weekDays.put(Calendar.SATURDAY, weekDaysList.get(5));
		this.weekDays.put(Calendar.SUNDAY, weekDaysList.get(6));
	}

	public void setWeekDays(Map<Integer, Boolean> weekDays) {
		this.weekDays = weekDays;
	}

	public Map<Integer, Boolean> getWeekDays() {
		return this.weekDays;
	}

	public void setReiterationType(ReiterationType rType) {
		this.reiterationType = rType;
	}

	public ReiterationType getReiterationType() {
		return this.reiterationType;
	}

	public int getWorkingDaysAmount() {
		return this.workingDaysAmount;
	}

	public void setWorkingDaysAmount(int workingDaysAmount) {
		this.workingDaysAmount = workingDaysAmount;
	}

	public int getWorkingDaysInterval() {
		return this.workingDaysInterval;
	}

	public void setWorkingDaysInterval(int workingDaysInterval) {
		this.workingDaysInterval = workingDaysInterval;
	}
}
