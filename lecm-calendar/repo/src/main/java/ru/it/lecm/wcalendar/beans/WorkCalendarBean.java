package ru.it.lecm.wcalendar.beans;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.wcalendar.absence.IAbsence;
import ru.it.lecm.wcalendar.calendar.ICalendar;
import ru.it.lecm.wcalendar.schedule.ISchedule;

import java.text.SimpleDateFormat;
import java.util.*;

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

        List<NodeRef> absenceByEmployee = absenceService.getAbsenceByEmployee(node);

        Map<Integer, List<NodeRef>> allWorkingDaysByYearMap = new HashMap<>(2);
        Map<Integer, List<NodeRef>>  allNonWorkingDaysByYearMap = new HashMap<>(2);
        if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
            Date curDay = new Date(start.getTime());
            while (!curDay.after(end)) {
                int yearByDate = getYearByDate(curDay);
                List<NodeRef> allWorkingDaysByYear = allWorkingDaysByYearMap.get(yearByDate);
                if (allWorkingDaysByYear == null) {
                    allWorkingDaysByYear = WCalendarService.getAllWorkingDaysByYear(yearByDate);
                    allWorkingDaysByYearMap.put(yearByDate, allWorkingDaysByYear);
                }
                List<NodeRef> allNonWorkingDaysByYear = allNonWorkingDaysByYearMap.get(yearByDate);
                if (allNonWorkingDaysByYear == null) {
                    allNonWorkingDaysByYear = WCalendarService.getAllNonWorkingDaysByYear(yearByDate);
                    allNonWorkingDaysByYearMap.put(yearByDate, allNonWorkingDaysByYear);
                }
                Boolean isPresent = isEmployeePresent(curDay, absenceByEmployee, allWorkingDaysByYear, allNonWorkingDaysByYear);

				if (isPresent != null && isPresent) {
					result.add(curDay);
				} else if (isPresent == null) {
					logger.warn("No calendar for such year ({})!", yearByDate);
				}

				curDay = DateUtils.addDays(curDay, 1);
			}

		} else if (ISchedule.SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
			List<NodeRef> scheduleElements = scheduleService.getScheduleElements(schedule);
			for (NodeRef scheduleElement : scheduleElements) {
				Date elementStart = scheduleService.getScheduleElementStart(scheduleElement);
				Date elementEnd = scheduleService.getScheduleElementEnd(scheduleElement);
				Date curElementDay = new Date(elementStart.getTime());
				while (!curElementDay.after(elementEnd)) {
					if (!curElementDay.before(start) && !curElementDay.after(end)) {
						if (!absenceService.isEmployeeAbsent(curElementDay, absenceByEmployee)) {
							result.add(curElementDay);
						}
					}
					curElementDay = DateUtils.addDays(curElementDay, 1);
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
        List<NodeRef> absenceByEmployee = absenceService.getAbsenceByEmployee(node);
        Map<Integer, List<NodeRef>> allWorkingDaysByYearMap = new HashMap<>(2);
        Map<Integer, List<NodeRef>>  allNonWorkingDaysByYearMap = new HashMap<>(2);

        while (!curDay.after(end)) {
			boolean toBeAdded = true;

			if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
                int yearByDate = getYearByDate(curDay);
                List<NodeRef> allWorkingDaysByYear = allWorkingDaysByYearMap.get(yearByDate);
                if (allWorkingDaysByYear == null) {
                    allWorkingDaysByYear = WCalendarService.getAllWorkingDaysByYear(yearByDate);
                    allWorkingDaysByYearMap.put(yearByDate, allWorkingDaysByYear);
                }
                List<NodeRef> allNonWorkingDaysByYear = allNonWorkingDaysByYearMap.get(yearByDate);
                if (allNonWorkingDaysByYear == null) {
                    allNonWorkingDaysByYear = WCalendarService.getAllNonWorkingDaysByYear(yearByDate);
                    allNonWorkingDaysByYearMap.put(yearByDate, allNonWorkingDaysByYear);
                }
                Boolean isPresent = isEmployeePresent(curDay, absenceByEmployee, allWorkingDaysByYear, allNonWorkingDaysByYear);


                if (isPresent != null && isPresent) {
					toBeAdded = false;
				} else if (isPresent == null) {
					logger.warn("No calendar for such year ({})!", getYearByDate(curDay));
				}

			} else if (ISchedule.SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
				Date curDayNoTime = DateUtils.truncate(curDay, Calendar.DATE);

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
			curDay = DateUtils.addDays(curDay, 1);
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
        List<NodeRef> absenceByEmployee = absenceService.getAbsenceByEmployee(node);
        Map<Integer, List<NodeRef>> allWorkingDaysByYearMap = new HashMap<>(2);
        Map<Integer, List<NodeRef>>  allNonWorkingDaysByYearMap = new HashMap<>(2);

        while (i < workingDaysRequired) {
			if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
                int yearByDate = getYearByDate(curDay);
                List<NodeRef> allWorkingDaysByYear = allWorkingDaysByYearMap.get(yearByDate);
                if (allWorkingDaysByYear == null) {
                    allWorkingDaysByYear = WCalendarService.getAllWorkingDaysByYear(yearByDate);
                    allWorkingDaysByYearMap.put(yearByDate, allWorkingDaysByYear);
                }
                List<NodeRef> allNonWorkingDaysByYear = allNonWorkingDaysByYearMap.get(yearByDate);
                if (allNonWorkingDaysByYear == null) {
                    allNonWorkingDaysByYear = WCalendarService.getAllNonWorkingDaysByYear(yearByDate);
                    allNonWorkingDaysByYearMap.put(yearByDate, allNonWorkingDaysByYear);
                }
                Boolean isPresent = isEmployeePresent(curDay, absenceByEmployee, allWorkingDaysByYear, allNonWorkingDaysByYear);


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
				curDay = DateUtils.addDays(curDay, 1);
			}
			// лекарство от бесконечного цикла.
			if (++j == 1000) {
				break;
			}
		}
		return result;
	}

	@Override
	public Date getNextWorkingDate(Date start, int offset, int timeUnit) {
		Date result;
		switch (timeUnit) {
			case Calendar.DAY_OF_MONTH:
				result = getNextWorkingDateByDays(start, offset);
				break;
			case Calendar.HOUR_OF_DAY:
				result = getNextWorkingDateByHours(start, offset);
				break;
			case Calendar.MINUTE:
				result = getNextWorkingDateByMinutes(start, offset);
				break;
			default:
				String msg = String.format("Unknown type: %d", timeUnit);
				throw new AlfrescoRuntimeException(msg);
		}

		return result;
	}

	@Override
	public Date getNextWorkingDateByDays(Date start, int workingDaysNumber) {
		int i = 0;

		Date curDay = new Date(start.getTime());
		while (i <= workingDaysNumber) {
			if (i != workingDaysNumber) {
				curDay = DateUtils.addDays(curDay, 1);
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

	@Override
	public Date getNextWorkingDateByHours(Date startDate, int hoursAmount) {
		return getNextWorkingDateByMinutes(startDate, hoursAmount * 60);
	}

	@Override
	public Date getNextWorkingDateByMinutes(Date startDate, int minutesAmount) {
		Date result, actualStartDate;

		int workDayEndHour, workDayEndMinute, workDayDurationInMinutes, daysShift,
				minutesShift, minutesTillEndOfTheDay, delta;

		NodeRef defaultSchedule = scheduleService.getDefaultSystemSchedule();
		String workDayEndTime = scheduleService.getScheduleEndTime(defaultSchedule);
		workDayEndHour = Integer.parseInt(workDayEndTime.split(":")[0]);
		workDayEndMinute = Integer.parseInt(workDayEndTime.split(":")[1]);
		workDayDurationInMinutes = scheduleService.getWorkDayDurationInMinutes(defaultSchedule);
		daysShift = minutesAmount / workDayDurationInMinutes;
		minutesShift = minutesAmount - (workDayDurationInMinutes * daysShift);
		minutesTillEndOfTheDay = minutesBetween(startDate,
				new DateTime(startDate).withHourOfDay(workDayEndHour).withMinuteOfHour(workDayEndMinute).toDate());

		delta = minutesShift - minutesTillEndOfTheDay;
		if (delta >= 0) {
			daysShift++;
			minutesShift = delta;
			// сбрасываем время на время начала рабочего дня
			String workDayBeginTime = scheduleService.getScheduleBeginTime(defaultSchedule);
			int workDayBeginHour = Integer.parseInt(workDayBeginTime.split(":")[0]);
			int workDayBeginMinute = Integer.parseInt(workDayBeginTime.split(":")[1]);

			actualStartDate = new DateTime(startDate).withHourOfDay(workDayBeginHour).withMinuteOfHour(workDayBeginMinute).toDate();
		} else {
			actualStartDate = startDate;
		}

		result = DateUtils.addMinutes(getNextWorkingDateByDays(actualStartDate, daysShift), minutesShift);

		return result;

	}

	@Override
	public Date getNextWorkingDate(Date date, String offsetStr) {
		if (offsetStr == null) {
			return null;
		}

		int timeUnit, offset;
		String modifierStr = offsetStr.substring(offsetStr.length() - 1);
		if ("m".equalsIgnoreCase(modifierStr)) {
			timeUnit = Calendar.MINUTE;
			offset = Integer.parseInt(offsetStr.substring(0, offsetStr.length() - 1));
		} else if ("h".equalsIgnoreCase(modifierStr)) {
			timeUnit = Calendar.HOUR_OF_DAY;
			offset = Integer.parseInt(offsetStr.substring(0, offsetStr.length() - 1));
		} else if ("d".equalsIgnoreCase(modifierStr)) {
			timeUnit = Calendar.DAY_OF_MONTH;
			offset = Integer.parseInt(offsetStr.substring(0, offsetStr.length() - 1));
		} else {
			timeUnit = Calendar.DAY_OF_MONTH;
			offset = Integer.parseInt(offsetStr);
		}

		return getNextWorkingDate(date, offset, timeUnit);
	}

	@Override
	public boolean isWorkingDayForEmployee(NodeRef employeeNode, Date day) {
		boolean result;
		if (!orgstructureService.isEmployee(employeeNode)) {
			throw new IllegalArgumentException("Argument 'employeeNode' must be an Employee!");
		}
		NodeRef schedule = getScheduleOrParentSchedule(employeeNode);
		if (schedule == null) {
			logger.trace("No schedule associated with employee {} nor with it's parent OUs!", employeeNode);
			return false;
		}
		String scheduleType = scheduleService.getScheduleType(schedule);

		if (ISchedule.SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
			Boolean isWorkingDay = WCalendarService.isWorkingDay(day);

			if (isWorkingDay == null) {
				logger.warn("No calendar for such year ({})!", getYearByDate(day));
				result = false;
			} else {
				result = isWorkingDay;
			}

		} else if (ISchedule.SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
			result = scheduleService.isWorkingDay(schedule, day) && !absenceService.isEmployeeAbsent(employeeNode, day);
		} else {
			String errMessage = String.format("Something wrong with schedule: it has some strange type: %s", scheduleType);
			throw new IllegalStateException(errMessage);
		}

		return result;
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

	private int minutesBetween(Date start, Date end) {
		DateTime startDT = new DateTime(start),
				endDT = new DateTime(end);

		return Minutes.minutesBetween(startDT, endDT).getMinutes();
	}

	private int getYearByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
	}

	private NodeRef getScheduleOrParentSchedule(NodeRef node) {
		NodeRef schedule;
		schedule = scheduleService.getScheduleByOrgSubject(node);
		if (schedule == null) {
			schedule = scheduleService.getParentSchedule(node);
		}
		return schedule;
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

	private Boolean isEmployeePresent(Date day, List<NodeRef> employeeAbsences) {
		Boolean result;
		Boolean isWorking = WCalendarService.isWorkingDay(day);

		if (isWorking == null) {
			result = null;
		} else {
			if (isWorking) {
				result = !absenceService.isEmployeeAbsent(day, employeeAbsences);
			} else {
				result = false;
			}
		}
		return result;
	}

	private Boolean isEmployeePresent(Date day, List<NodeRef> employeeAbsences, List<NodeRef> allWorkingDaysByYear, List<NodeRef> allNonWorkingDaysByYear) {
		Boolean result;
		Boolean isWorking = WCalendarService.isWorkingDay(day, allWorkingDaysByYear, allNonWorkingDaysByYear);

		if (isWorking == null) {
			result = null;
		} else {
			if (isWorking) {
				result = !absenceService.isEmployeeAbsent(day, employeeAbsences);
			} else {
				result = false;
			}
		}
		return result;
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
		Date curDate = DateUtils.addDays(initialDate, offset);

		Calendar calendar = Calendar.getInstance();
		while (!getEmployeeAvailability(node, curDate)) {
			curDate = shiftToFuture ? DateUtils.addDays(curDate, 1) : DateUtils.addDays(curDate, -1);
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
