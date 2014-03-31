package ru.it.lecm.wcalendar.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.wcalendar.absence.IAbsence;
import ru.it.lecm.wcalendar.calendar.ICalendar;
import ru.it.lecm.wcalendar.schedule.ISchedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author vlevin
 */
public class WorkCalendarBean implements IWorkCalendar {

	private OrgstructureBean orgstructureService;
	private IAbsence absenceService;
	private ISchedule scheduleService;
	private ICalendar WCalendarService;
	private final static SimpleDateFormat yearParser = new SimpleDateFormat("yyyy");
	private final static Logger logger = LoggerFactory.getLogger(WorkCalendarBean.class);

	public final void init() {
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "absenceService", absenceService);
		PropertyCheck.mandatory(this, "scheduleService", scheduleService);
		PropertyCheck.mandatory(this, "wCalendarService", WCalendarService);

	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public boolean getEmployeeAvailability(NodeRef node, Date day) {
		boolean result;
		if (!orgstructureService.isEmployee(node)) {
			throw new IllegalArgumentException("Argument 'node' must be an Employee!");
		}
		NodeRef schedule = getScheduleOrParentSchedule(node);
		if (schedule == null) {
			logger.trace("No schedule associated with employee {} nor with it's parent OUs!", node);
			return false;
		}
		String scheduleType = scheduleService.getScheduleType(schedule);

		if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
			Boolean isPresent = isEmployeePresent(day, node);

			if (isPresent == null) {
				logger.warn("No calendar for such year ({})!", getYearByDate(day));
				result = false;
			} else {
				result = isPresent;
			}

		} else if (ISchedule.SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
			result = scheduleService.isWorkingDay(schedule, day) && !absenceService.isEmployeeAbsent(node, day);
		} else {
			String errMessage = String.format("Something wrong with schedule: it has some strange type: %s", scheduleType);
			throw new IllegalStateException(errMessage);
		}

		return result;
	}

	@Override
	public List<Date> getEmployeeWorkindDays(NodeRef node, Date start, Date end) {
		List<Date> result = new ArrayList<Date>();

		if (!orgstructureService.isEmployee(node)) {
			throw new IllegalArgumentException("Argument \"node\" must be an Employee!");
		}
		NodeRef schedule = getScheduleOrParentSchedule(node);
		if (schedule == null) {
			logger.trace("No schedule associated with employee nor with it's parent OUs!");
			return result;
		}
		String scheduleType = scheduleService.getScheduleType(schedule);

		if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
			Date curDay = new Date(start.getTime());
			while (!curDay.after(end)) {
				Boolean isPresent = isEmployeePresent(curDay, node);

				if (isPresent != null && isPresent) {
					result.add(curDay);
				} else if (isPresent == null) {
					logger.warn("No calendar for such year ({})!", getYearByDate(curDay));
				}

				curDay = addDayToDate(curDay);
			}

		} else if (ISchedule.SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
			List<NodeRef> scheduleElements = scheduleService.getScheduleElements(schedule);
			for (NodeRef scheduleElement : scheduleElements) {
				Date elementStart = scheduleService.getScheduleElementStart(scheduleElement);
				Date elementEnd = scheduleService.getScheduleElementEnd(scheduleElement);
				Date curElementDay = new Date(elementStart.getTime());
				while (!curElementDay.after(elementEnd)) {
					if (!curElementDay.before(start) && !curElementDay.after(end)) {
						if (!absenceService.isEmployeeAbsent(node, curElementDay)) {
							result.add(curElementDay);
						}
					}
					curElementDay = addDayToDate(curElementDay);
				}
			}
		}

		return result;
	}

	@Override
	public List<Date> getEmployeeNonWorkindDays(NodeRef node, Date start, Date end) {
		List<Date> result = new ArrayList<Date>();

		if (!orgstructureService.isEmployee(node)) {
			throw new IllegalArgumentException("Argument 'node' must be an Employee!");
		}
		NodeRef schedule = getScheduleOrParentSchedule(node);
		if (schedule == null) {
			logger.trace("No schedule associated with employee nor with it's parent OUs!");
			return result;
		}
		String scheduleType = scheduleService.getScheduleType(schedule);
		List<NodeRef> scheduleElements = scheduleService.getScheduleElements(schedule);

		Date curDay = new Date(start.getTime());
		while (!curDay.after(end)) {
			boolean toBeAdded = true;

			if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
				Boolean isPresent = isEmployeePresent(curDay, node);

				if (isPresent != null && isPresent) {
					toBeAdded = false;
				} else if (isPresent == null) {
					logger.warn("No calendar for such year ({})!", getYearByDate(curDay));
				}

			} else if (ISchedule.SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
				Date curDayNoTime = resetTime(curDay);

				for (NodeRef scheduleElement : scheduleElements) {
					Date elementStart = scheduleService.getScheduleElementStart(scheduleElement);
					Date elementEnd = scheduleService.getScheduleElementEnd(scheduleElement);

					if (!curDayNoTime.before(elementStart) && !curDayNoTime.after(elementEnd)) {
						if (!absenceService.isEmployeeAbsent(node, curDay)) {
							toBeAdded = false;
						}
						break;
					}
				}
			}

			if (toBeAdded) {
				result.add(curDay);
			}
			curDay = addDayToDate(curDay);
		}

		return result;
	}

	@Override
	public int getEmployeeWorkingDaysNumber(NodeRef node, Date start, Date end) {
		List<Date> employeeWorkindDays = getEmployeeWorkindDays(node, start, end);
		return employeeWorkindDays.size();
	}

	@Override
	public Date getPlannedJobFinish(NodeRef node, Date start, int workingDaysRequired) {
		int i = 0, j = 0;
		Date result = null;
		if (!orgstructureService.isEmployee(node)) {
			throw new IllegalArgumentException("Argument 'node' must be an Employee!");
		}
		NodeRef schedule = getScheduleOrParentSchedule(node);
		if (schedule == null) {
			logger.trace("No schedule associated with employee nor with it's parent OUs!");
			return result;
		}
		String scheduleType = scheduleService.getScheduleType(schedule);
		Date curDay = new Date(start.getTime());
		while (i < workingDaysRequired) {
			if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
				Boolean isPresent = isEmployeePresent(curDay, node);

				if (isPresent != null && isPresent) {
					i++;
				} else if (isPresent == null) {
					logger.warn("No calendar for such year ({})!", getYearByDate(curDay));
				}

			} else if (ISchedule.SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
				if (scheduleService.isWorkingDay(schedule, curDay)) {
					if (!absenceService.isEmployeeAbsent(node, curDay)) {
						i++;
					}
				}
			}

			if (i == workingDaysRequired - 1) {
				result = new Date(curDay.getTime());
			} else {
				curDay = addDayToDate(curDay);
			}
			// лекарство от бесконечного цикла.
			if (++j == 1000) {
				break;
			}
		}
		return result;
	}

	@Override
	public Date getNextWorkingDate(Date start, int workingDaysNumber) {
		int i = 0;

		Date curDay = new Date(start.getTime());
		while (i <= workingDaysNumber) {
			if (i != workingDaysNumber) {
				curDay = addDayToDate(curDay);
			}

			Boolean isWorkingDay = WCalendarService.isWorkingDay(curDay);
			if (isWorkingDay == null) {
				return null;
			} else if (isWorkingDay) {
				i++;
			}

		}
		return curDay;
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	public void setScheduleService(ISchedule scheduleService) {
		this.scheduleService = scheduleService;
	}

	public void setWCalendarService(ICalendar WCalendarService) {
		this.WCalendarService = WCalendarService;
	}

	private int getYearByDate(Date date) {
		return Integer.parseInt(yearParser.format(date));
	}

	private NodeRef getScheduleOrParentSchedule(NodeRef node) {
		NodeRef schedule;
		schedule = scheduleService.getScheduleByOrgSubject(node);
		if (schedule == null) {
			schedule = scheduleService.getParentSchedule(node);
		}
		return schedule;
	}

	private Date addDayToDate(Date date) {
		return shiftDate(date, 1);
	}

	private Date substractDayFromDate(Date date) {
		return shiftDate(date, -1);
	}

	/**
	 * сдвинуть дату на указанное число дней
	 * @param date дата которую будем двигать
	 * @param amount кол-во дней для сдвига. Если больше нуля то в будущее. Если меньше, то в прошлое
	 * @return новая дата
	 */
	private Date shiftDate(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, amount);
		return c.getTime();
	}

	private Boolean isEmployeePresent(Date day, NodeRef node) {
		Boolean result;
		Boolean isWorking = WCalendarService.isWorkingDay(day);

		if (isWorking == null) {
			result = null;
		} else {
			if (isWorking) {
				result = !absenceService.isEmployeeAbsent(node, day);
			} else {
				result = false;
			}
		}
		return result;
	}

	/**
	 * Устанавливает часы, минуты, секунды и миллисекунды в 00:00:00.000
	 *
	 * @param day Дата, у которой надо сбросить поля времени.
	 * @return Дата с обнуленными полями времени.
	 */
	protected Date resetTime(final Date day) {
		Date resetDay = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		resetDay.setTime(cal.getTimeInMillis());
		return resetDay;
	}

	@Override
	public Date getEmployeeNextWorkingDay(NodeRef node, Date initialDate, int offset) {
		return getEmployeeWorkingDayOffset(node, initialDate, offset, true);
	}

	@Override
	public Date getEmployeePreviousWorkingDay(NodeRef node, Date initialDate, int offset) {
		return getEmployeeWorkingDayOffset(node, initialDate, offset, false);
	}

	private Date getEmployeeWorkingDayOffset(NodeRef node, Date initialDate, int offset, boolean shiftToFuture) {
		Date curDate = shiftDate(initialDate, offset);

		Calendar calendar = Calendar.getInstance();
		while (!getEmployeeAvailability(node, curDate)) {
			curDate = shiftToFuture ? addDayToDate(curDate) : substractDayFromDate(curDate);
			calendar.setTime(curDate);
			int year = calendar.get(Calendar.YEAR);
			if (!WCalendarService.isCalendarExists(year)) {
				curDate = null;
				break;
			}
		}
		return curDate;
	}
}
