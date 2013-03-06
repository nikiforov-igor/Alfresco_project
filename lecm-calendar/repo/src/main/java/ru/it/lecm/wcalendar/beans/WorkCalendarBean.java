package ru.it.lecm.wcalendar.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.wcalendar.absence.IAbsence;
import ru.it.lecm.wcalendar.calendar.ICalendar;
import ru.it.lecm.wcalendar.shedule.IShedule;

/**
 *
 * @author vlevin
 */
public class WorkCalendarBean implements IWorkCalendar {

	private OrgstructureBean orgstructureService;
	private IAbsence absenceService;
	private IShedule sheduleService;
	private ICalendar WCalendarService;
	private SimpleDateFormat yearParser = new SimpleDateFormat("yyyy");
	private final static Logger logger = LoggerFactory.getLogger(WorkCalendarBean.class);

	public final void init() {
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "absenceService", absenceService);
		PropertyCheck.mandatory(this, "sheduleService", sheduleService);
		PropertyCheck.mandatory(this, "wCalendarService", WCalendarService);

	}

	public void setOrgstructureService (OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public boolean getEmployeeAvailability(NodeRef node, Date day) {
		boolean result;
		if (!orgstructureService.isEmployee(node)) {
			String errMessage = "Argument 'node' must be an Employee!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		NodeRef shedule = getSheduleOrParentShedule(node);
		if (shedule == null) {
			String errMessage = "No shedule associated with employee nor with it's parent OUs!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		String sheduleType = sheduleService.getSheduleType(shedule);

		if (IShedule.SHEDULE_TYPE_COMMON.equals(sheduleType)) {
			Boolean isPresent = isEmployeePresent(day, node);

			if (isPresent == null) {
				String errMessage = String.format("No calendar for such year (%d)!", getYearByDate(day));
				IllegalArgumentException t = new IllegalArgumentException(errMessage);
				logger.error(errMessage, t);
				throw t;
			} else {
				result = isPresent;
			}

		} else if (IShedule.SHEDULE_TYPE_SPECIAL.equals(sheduleType)) {
			result = sheduleService.isWorkingDay(shedule, day);
			if (result) {
				result = !absenceService.isEmployeeAbsent(node, day);
			}
		} else {
			String errMessage = String.format("Something wrong with shedule: it has some strange type: %s", sheduleType);
			RuntimeException t = new RuntimeException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}

		return result;
	}

	@Override
	public List<Date> getEmployeeWorkindDays(NodeRef node, Date start, Date end) {
		List<Date> result = new ArrayList<Date>();

		if (!orgstructureService.isEmployee(node)) {
			String errMessage = "Argument \"node\" must be an Employee!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		NodeRef shedule = getSheduleOrParentShedule(node);
		if (shedule == null) {
			String errMessage = "No shedule associated with employee nor with it's parent OUs!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		String sheduleType = sheduleService.getSheduleType(shedule);

		if (IShedule.SHEDULE_TYPE_COMMON.equals(sheduleType)) {
			Date curDay = new Date(start.getTime());
			while (!curDay.after(end)) {
				Boolean isPresent = isEmployeePresent(curDay, node);

				if (isPresent == null) {
					String errMessage = String.format("No calendar for such year (%d)!", getYearByDate(curDay));
					IllegalArgumentException t = new IllegalArgumentException(errMessage);
					logger.error(errMessage, t);
					throw t;
				}

				if (isPresent) {
					result.add(curDay);
				}

				curDay = addDayToDate(curDay);
			}

		} else if (IShedule.SHEDULE_TYPE_SPECIAL.equals(sheduleType)) {
			List<NodeRef> sheduleElements = sheduleService.getSheduleElements(shedule);
			for (NodeRef sheduleElement : sheduleElements) {
				Date elementStart = sheduleService.getSheduleElementStart(sheduleElement);
				Date elementEnd = sheduleService.getSheduleElementEnd(sheduleElement);
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
			String errMessage = "Argument 'node' must be an Employee!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		NodeRef shedule = getSheduleOrParentShedule(node);
		if (shedule == null) {
			String errMessage = "No shedule associated with employee nor with it's parent OUs!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		String sheduleType = sheduleService.getSheduleType(shedule);
		List<NodeRef> sheduleElements = sheduleService.getSheduleElements(shedule);

		Date curDay = new Date(start.getTime());
		while (!curDay.after(end)) {
			boolean toBeAdded = true;

			if (IShedule.SHEDULE_TYPE_COMMON.equals(sheduleType)) {
				Boolean isPresent = isEmployeePresent(curDay, node);

				if (isPresent == null) {
					String errMessage = String.format("No calendar for such year (%d)!", getYearByDate(curDay));
					IllegalArgumentException t = new IllegalArgumentException(errMessage);
					logger.error(errMessage, t);
					throw t;
				}

				if (isPresent) {
					toBeAdded = false;
				}
			} else if (IShedule.SHEDULE_TYPE_SPECIAL.equals(sheduleType)) {
				Date curDayNoTime = resetTime(curDay);

				for (NodeRef sheduleElement : sheduleElements) {
					Date elementStart = sheduleService.getSheduleElementStart(sheduleElement);
					Date elementEnd = sheduleService.getSheduleElementEnd(sheduleElement);

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
			String errMessage = "Argument 'node' must be an Employee!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		NodeRef shedule = getSheduleOrParentShedule(node);
		if (shedule == null) {
			String errMessage = "No shedule associated with employee nor with it's parent OUs!";
			IllegalArgumentException t = new IllegalArgumentException(errMessage);
			logger.error(errMessage, t);
			throw t;
		}
		String sheduleType = sheduleService.getSheduleType(shedule);
		Date curDay = new Date(start.getTime());
		while (i < workingDaysRequired) {
			if (IShedule.SHEDULE_TYPE_COMMON.equals(sheduleType)) {
				Boolean isPresent = isEmployeePresent(curDay, node);

				if (isPresent == null) {
					String errMessage = String.format("No calendar for such year (%d)!", getYearByDate(curDay));
					IllegalArgumentException t = new IllegalArgumentException(errMessage);
					logger.error(errMessage, t);
					throw t;
				}
				if (isPresent) {
					i++;
				}

			} else if (IShedule.SHEDULE_TYPE_SPECIAL.equals(sheduleType)) {
				if (sheduleService.isWorkingDay(shedule, curDay)) {
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

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	public void setSheduleService(IShedule sheduleService) {
		this.sheduleService = sheduleService;
	}

	public void setWCalendarService(ICalendar WCalendarService) {
		this.WCalendarService = WCalendarService;
	}

	private int getYearByDate(Date date) {
		return Integer.valueOf(yearParser.format(date));
	}

	private NodeRef getSheduleOrParentShedule(NodeRef node) {
		NodeRef shedule;
		shedule = sheduleService.getSheduleByOrgSubject(node);
		if (shedule == null) {
			shedule = sheduleService.getParentShedule(node);
		}
		return shedule;
	}

	private Date addDayToDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
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
}
