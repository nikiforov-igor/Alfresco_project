package ru.it.lecm.wcalendar.shedule;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для описания правил повторений особого графика работы.
 *
 * @author vlevin
 */
public interface ISpecialSheduleRaw {

	enum ReiterationType {

		SHIFT, WEEK_DAYS, MONTH_DAYS;
	}

	List<Integer> getMonthDays();

	ReiterationType getReiterationType();

	Date getTimeLimitEnd();

	Date getTimeLimitStart();

	Date getTimeWorkBegins();

	Date getTimeWorkEnds();

	Map<Integer, Boolean> getWeekDays();

	int getWorkingDaysAmount();

	int getWorkingDaysInterval();

	void setMonthDays(List<Integer> monthDays);

	void setReiterationType(ReiterationType rType);

	void setTimeLimitEnd(String timeLimitEnd);

	void setTimeLimitEnd(Date timeLimitEnd);

	void setTimeLimitStart(String timeLimitStart);

	void setTimeLimitStart(Date timeLimitStart);

	void setTimeWorkBegins(String timeBeginStr);

	void setTimeWorkBegins(Date timeWorkBegins);

	void setTimeWorkEnds(String timeEndStr);

	void setTimeWorkEnds(Date timeWorkEnds);

	void setWeekDays(List<Boolean> weekDaysList);

	void setWeekDays(Map<Integer, Boolean> weekDays);

	void setWorkingDaysAmount(int workingDaysAmount);

	void setWorkingDaysInterval(int workingDaysInterval);
}
