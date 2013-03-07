package ru.it.lecm.wcalendar.schedule.beans;

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
import ru.it.lecm.wcalendar.schedule.ISpecialScheduleRaw;

public class SpecialScheduleRawBean implements ISpecialScheduleRaw {

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
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialScheduleRawBean.class);

	public void SpecialScheduleRaw() {
		this.weekDays.put(Calendar.MONDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.TUESDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.WEDNESDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.THURSDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.FRIDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.SATURDAY, Boolean.FALSE);
		this.weekDays.put(Calendar.SUNDAY, Boolean.FALSE);
	}

	@Override
	public Date getTimeLimitStart() {
		return timeLimitStart;
	}

	@Override
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

	@Override
	public void setTimeLimitStart(Date timeLimitStart) {
		this.timeLimitStart = timeLimitStart;
	}

	@Override
	public Date getTimeLimitEnd() {
		return timeLimitEnd;
	}

	@Override
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

	@Override
	public void setTimeLimitEnd(Date timeLimitEnd) {
		this.timeLimitEnd = timeLimitEnd;
	}

	@Override
	public void setTimeWorkBegins(String timeBeginStr) {
		try {
			setTimeWorkBegins(timeParser.parse(timeBeginStr));
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + timeBeginStr + " as Date! " + ex.getMessage(), ex);
		}
	}

	@Override
	public void setTimeWorkBegins(Date timeWorkBegins) {
		this.timeWorkBegins = timeWorkBegins;
	}

	@Override
	public Date getTimeWorkBegins() {
		return this.timeWorkBegins;
	}

	@Override
	public void setTimeWorkEnds(String timeEndStr) {
		try {
			setTimeWorkEnds(timeParser.parse(timeEndStr));
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + timeEndStr + " as Date! " + ex.getMessage(), ex);
		}
	}

	@Override
	public void setTimeWorkEnds(Date timeWorkEnds) {
		this.timeWorkEnds = timeWorkEnds;
	}

	@Override
	public Date getTimeWorkEnds() {
		return this.timeWorkEnds;
	}

	@Override
	public void setMonthDays(List<Integer> monthDays) {
		this.monthDays = monthDays;
	}

	@Override
	public List<Integer> getMonthDays() {
		return this.monthDays;
	}

	@Override
	public void setWeekDays(List<Boolean> weekDaysList) {
		this.weekDays.put(Calendar.MONDAY, weekDaysList.get(0));
		this.weekDays.put(Calendar.TUESDAY, weekDaysList.get(1));
		this.weekDays.put(Calendar.WEDNESDAY, weekDaysList.get(2));
		this.weekDays.put(Calendar.THURSDAY, weekDaysList.get(3));
		this.weekDays.put(Calendar.FRIDAY, weekDaysList.get(4));
		this.weekDays.put(Calendar.SATURDAY, weekDaysList.get(5));
		this.weekDays.put(Calendar.SUNDAY, weekDaysList.get(6));
	}

	@Override
	public void setWeekDays(Map<Integer, Boolean> weekDays) {
		this.weekDays = weekDays;
	}

	@Override
	public Map<Integer, Boolean> getWeekDays() {
		return this.weekDays;
	}

	@Override
	public void setReiterationType(ReiterationType rType) {
		this.reiterationType = rType;
	}

	@Override
	public ReiterationType getReiterationType() {
		return this.reiterationType;
	}

	@Override
	public int getWorkingDaysAmount() {
		return this.workingDaysAmount;
	}

	@Override
	public void setWorkingDaysAmount(int workingDaysAmount) {
		this.workingDaysAmount = workingDaysAmount;
	}

	@Override
	public int getWorkingDaysInterval() {
		return this.workingDaysInterval;
	}

	@Override
	public void setWorkingDaysInterval(int workingDaysInterval) {
		this.workingDaysInterval = workingDaysInterval;
	}
}
