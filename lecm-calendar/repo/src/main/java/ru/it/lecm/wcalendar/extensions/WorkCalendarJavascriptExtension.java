package ru.it.lecm.wcalendar.extensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.IWorkCalendar;
import org.mozilla.javascript.Context;

/**
 * JavaScript root-object под названием "workCalendar". Предоставляет доступ к
 * методам интерфейса IWorkCalendar из web-script'ов.
 *
 * @see ru.it.lecm.wcalendar.IWorkCalendar
 * @author vlevin
 */
public class WorkCalendarJavascriptExtension extends BaseScopableProcessorExtension {

	private IWorkCalendar workCalendarService;
	private DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
	}

	/**
	 * Узнать, работает ли сотрудник в указанный день.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param jsDate интересуюшая дата в виде JS-объекта Date.
	 * @return сотрудник работает в указанную дату - true
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public boolean getEmployeeAvailability(final String nodeRefStr, final Object jsDate) {
		Date date;
		date = (Date) Context.jsToJava(jsDate, Date.class);

		return getEmployeeAvailability(new NodeRef(nodeRefStr), date);
	}

	/**
	 * Узнать, работает ли сотрудник в указанный день.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param dateStr интересуюшая дата в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return сотрудник работает в указанную дату - true
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public boolean getEmployeeAvailability(final String nodeRefStr, final String dateStr) {
		Date date;

		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		return getEmployeeAvailability(new NodeRef(nodeRefStr), date);
	}

	/**
	 * Узнать, работает ли сотрудник в указанный день.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param jsDate интересуюшая дата в виде JS-объекта Date.
	 * @return сотрудник работает в указанную дату - true
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public boolean getEmployeeAvailability(final ScriptNode nodeRef, final Object jsDate) {
		Date date;
		date = (Date) Context.jsToJava(jsDate, Date.class);

		return getEmployeeAvailability(nodeRef.getNodeRef(), date);
	}

	/**
	 * Узнать, работает ли сотрудник в указанный день.
	 *
	 * @param nodeRef NodeRef на сотрудника в виде строки.
	 * @param dateStr интересуюшая дата в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return сотрудник работает в указанную дату - true
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public boolean getEmployeeAvailability(final ScriptNode nodeRef, final String dateStr) {
		Date date;

		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		return getEmployeeAvailability(nodeRef.getNodeRef(), date);
	}

	private boolean getEmployeeAvailability(NodeRef node, Date date) {
		return workCalendarService.getEmployeeAvailability(node, date);
	}

	/**
	 * Получить список рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param startStr начало периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @param endStr конец периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return JSON-массив дат рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeWorkindDays(final String nodeRefStr, final String startStr, final String endStr) {
		Date start, end;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		try {
			end = dateParser.parse(endStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + endStr + " as Date! " + ex.getMessage(), ex);
		}

		return getEmployeeWorkindDays(new NodeRef(nodeRefStr), start, end);
	}

	/**
	 * Получить список рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param jsStart начало периода в виде JS-объекта Date.
	 * @param jsEnd конец периода в виде JS-объекта Date.
	 * @return JSON-массив дат рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeWorkindDays(final String nodeRefStr, final Object jsStart, final Object jsEnd) {
		Date start, end;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		return getEmployeeWorkindDays(new NodeRef(nodeRefStr), start, end);
	}

	/**
	 * Получить список рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param startStr начало периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @param endStr конец периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return JSON-массив дат рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeWorkindDays(final ScriptNode nodeRef, final String startStr, final String endStr) {
		Date start, end;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		try {
			end = dateParser.parse(endStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + endStr + " as Date! " + ex.getMessage(), ex);
		}

		return getEmployeeWorkindDays(nodeRef.getNodeRef(), start, end);
	}

	/**
	 * Получить список рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param jsStart начало периода в виде JS-объекта Date.
	 * @param jsEnd конец периода в виде JS-объекта Date.
	 * @return JSON-массив дат рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeWorkindDays(final ScriptNode nodeRef, final Object jsStart, final Object jsEnd) {
		Date start, end;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		return getEmployeeWorkindDays(nodeRef.getNodeRef(), start, end);
	}

	private JSONArray getEmployeeWorkindDays(NodeRef node, Date start, Date end) {
		JSONArray result;
		List<Date> employeeWorkindDays = workCalendarService.getEmployeeWorkindDays(node, start, end);
		result = new JSONArray(employeeWorkindDays);

		return result;
	}

	/**
	 * Получить список выходных дней сотрудника в указанный период времени.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param startStr начало периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @param endStr конец периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return JSON-массив дат выходных дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeNonWorkindDays(final String nodeRefStr, final String startStr, final String endStr) {
		Date start, end;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		try {
			end = dateParser.parse(endStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + endStr + " as Date! " + ex.getMessage(), ex);
		}

		return getEmployeeNonWorkindDays(new NodeRef(nodeRefStr), start, end);

	}

	/**
	 * Получить список выходных дней сотрудника в указанный период времени.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param jsStart начало периода в виде JS-объекта Date.
	 * @param jsEnd конец периода в виде JS-объекта Date.
	 * @return JSON-массив дат выходных дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeNonWorkindDays(final String nodeRefStr, final Object jsStart, final Object jsEnd) {
		Date start, end;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		return getEmployeeNonWorkindDays(new NodeRef(nodeRefStr), start, end);
	}

	/**
	 * Получить список выходных дней сотрудника в указанный период времени.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param startStr начало периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @param endStr конец периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return JSON-массив дат выходных дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeNonWorkindDays(final ScriptNode nodeRef, final String startStr, final String endStr) {
		Date start, end;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		try {
			end = dateParser.parse(endStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + endStr + " as Date! " + ex.getMessage(), ex);
		}

		return getEmployeeNonWorkindDays(nodeRef.getNodeRef(), start, end);

	}

	/**
	 * Получить список выходных дней сотрудника в указанный период времени.
	 *
	 * @param nodeRef NodeRef на сотрудника в виде строки.
	 * @param jsStart начало периода в виде JS-объекта Date.
	 * @param jsEnd конец периода в виде JS-объекта Date.
	 * @return JSON-массив дат выходных дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public JSONArray getEmployeeNonWorkindDays(final ScriptNode nodeRef, final Object jsStart, final Object jsEnd) {
		Date start, end;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		return getEmployeeNonWorkindDays(nodeRef.getNodeRef(), start, end);
	}

	private JSONArray getEmployeeNonWorkindDays(NodeRef node, Date start, Date end) {
		JSONArray result;

		List<Date> employeeNonWorkindDays = workCalendarService.getEmployeeNonWorkindDays(node, start, end);
		result = new JSONArray(employeeNonWorkindDays);

		return result;
	}

	/**
	 * Получить количество рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param startStr начало периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @param endStr конец периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return количество рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public int getEmployeeWorkingDaysNumber(final String nodeRefStr, final String startStr, final String endStr) {
		Date start, end;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		try {
			end = dateParser.parse(endStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + endStr + " as Date! " + ex.getMessage(), ex);
		}

		return getEmployeeWorkingDaysNumber(new NodeRef(nodeRefStr), start, end);
	}

	/**
	 * Получить количество рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param jsStart начало периода в виде JS-объекта Date.
	 * @param jsEnd конец периода в виде JS-объекта Date.
	 * @return количество рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public int getEmployeeWorkingDaysNumber(final String nodeRefStr, final Object jsStart, final Object jsEnd) {
		Date start, end;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		return getEmployeeWorkingDaysNumber(new NodeRef(nodeRefStr), start, end);
	}

	/**
	 * Получить количество рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param startStr начало периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @param endStr конец периода в виде строки yyyy-MM-dd'T'HH:mm:ss.SSS
	 * (напр. 2013-03-04T00:00:00.000)
	 * @return количество рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public int getEmployeeWorkingDaysNumber(final ScriptNode nodeRef, final String startStr, final String endStr) {
		Date start, end;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		try {
			end = dateParser.parse(endStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + endStr + " as Date! " + ex.getMessage(), ex);
		}

		return getEmployeeWorkingDaysNumber(nodeRef.getNodeRef(), start, end);
	}

	/**
	 * Получить количество рабочих дней сотрудника в указанный период времени.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param jsStart начало периода в виде JS-объекта Date.
	 * @param jsEnd конец периода в виде JS-объекта Date.
	 * @return количество рабочих дней сотрудника.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public int getEmployeeWorkingDaysNumber(final ScriptNode nodeRef, final Object jsStart, final Object jsEnd) {
		Date start, end;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		return getEmployeeWorkingDaysNumber(nodeRef.getNodeRef(), start, end);
	}

	private int getEmployeeWorkingDaysNumber(NodeRef nodeRef, Date start, Date end) {
		return workCalendarService.getEmployeeWorkingDaysNumber(nodeRef, start, end);
	}

	/**
	 * Получить плановую дату выполнения сотрудником задачи.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param startStr начало выполнения задачи в виде строки
	 * yyyy-MM-dd'T'HH:mm:ss.SSS (напр. 2013-03-04T00:00:00.000)
	 * @param workingDaysRequired количество рабочих дней, необходимых для
	 * выполнения задачи.
	 * @return плановая дата выполнения задачи.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public Date getPlannedJobFinish(final String nodeRefStr, final String startStr, final int workingDaysRequired) {
		Date start;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		return getPlannedJobFinish(new NodeRef(nodeRefStr), start, workingDaysRequired);
	}

	/**
	 * Получить плановую дату выполнения сотрудником задачи.
	 *
	 * @param nodeRefStr NodeRef на сотрудника в виде строки.
	 * @param jsStart начало выполнения задачи в виде JS-объекта Date
	 * @param workingDaysRequired количество рабочих дней, необходимых для
	 * выполнения задачи.
	 * @return плановая дата выполнения задачи.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public Date getPlannedJobFinish(final String nodeRefStr, final Object jsStart, final int workingDaysRequired) {
		Date start;

		start = (Date) Context.jsToJava(jsStart, Date.class);

		return getPlannedJobFinish(new NodeRef(nodeRefStr), start, workingDaysRequired);
	}

	/**
	 * Получить плановую дату выполнения сотрудником задачи.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param startStr начало выполнения задачи в виде строки
	 * yyyy-MM-dd'T'HH:mm:ss.SSS (напр. 2013-03-04T00:00:00.000)
	 * @param workingDaysRequired количество рабочих дней, необходимых для
	 * выполнения задачи.
	 * @return плановая дата выполнения задачи.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public Date getPlannedJobFinish(final ScriptNode nodeRef, final String startStr, final int workingDaysRequired) {
		Date start;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		return getPlannedJobFinish(nodeRef.getNodeRef(), start, workingDaysRequired);
	}

	/**
	 * Получить плановую дату выполнения сотрудником задачи.
	 *
	 * @param nodeRef NodeRef на сотрудника.
	 * @param jsStart начало выполнения задачи в виде JS-объекта Date
	 * @param workingDaysRequired количество рабочих дней, необходимых для
	 * выполнения задачи.
	 * @return плановая дата выполнения задачи.
	 * @see ru.it.lecm.wcalendar.IWorkCalendar
	 */
	public Date getPlannedJobFinish(final ScriptNode nodeRef, final Object jsStart, final int workingDaysRequired) {
		Date start;

		start = (Date) Context.jsToJava(jsStart, Date.class);

		return getPlannedJobFinish(nodeRef.getNodeRef(), start, workingDaysRequired);
	}

	private Date getPlannedJobFinish(final NodeRef nodeRef, final Date start, final int workingDaysRequired) {
		return workCalendarService.getPlannedJobFinish(nodeRef, start, workingDaysRequired);
	}

	public Date getNextWorkingDateByDays(final String startStr, final int workingDaysNumber) {
		Date start;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getNextWorkingDateByDays(start, workingDaysNumber);
	}

	public Date getNextWorkingDateByDays(final Object jsStart, final int workingDaysNumber) {
		Date start;

		start = (Date) Context.jsToJava(jsStart, Date.class);

		return workCalendarService.getNextWorkingDateByDays(start, workingDaysNumber);
	}

	public Date getNextWorkingDateByHours(final String startStr, final int workingHoursNumber) {
		Date start;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getNextWorkingDateByHours(start, workingHoursNumber);
	}

	public Date getNextWorkingDateByHours(final Object jsStart, final int workingHoursNumber) {
		Date start;

		start = (Date) Context.jsToJava(jsStart, Date.class);

		return workCalendarService.getNextWorkingDateByHours(start, workingHoursNumber);
	}
	public Date getNextWorkingDateByMinutes(final String startStr, final int workingMinutesNumber) {
		Date start;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getNextWorkingDateByMinutes(start, workingMinutesNumber);
	}

	public Date getNextWorkingDateByMinutes(final Object jsStart, final int workingMinutesNumber) {
		Date start;

		start = (Date) Context.jsToJava(jsStart, Date.class);

		return workCalendarService.getNextWorkingDateByMinutes(start, workingMinutesNumber);
	}

	public Date getNextWorkingDate(final String startStr, final String offset) {
		Date start;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getNextWorkingDate(start, offset);
	}

	public Date getNextWorkingDate(final Object jsStart, final String offset) {
		Date start;

		start = (Date) Context.jsToJava(jsStart, Date.class);

		return workCalendarService.getNextWorkingDate(start, offset);
	}

	public Date getEmployeeNextWorkingDay(final ScriptNode node, final Object jsInitialDate, int offset) {
		Date initialDate = (Date) Context.jsToJava(jsInitialDate, Date.class);
		return workCalendarService.getEmployeeNextWorkingDay(node.getNodeRef(), initialDate, offset);
	}

	public Date getEmployeeNextWorkingDay(final String nodeStr, final Object jsInitialDate, int offset) {
		Date initialDate = (Date) Context.jsToJava(jsInitialDate, Date.class);
		return workCalendarService.getEmployeeNextWorkingDay(new NodeRef(nodeStr), initialDate, offset);
	}

	public Date getEmployeeNextWorkingDay(final ScriptNode node, final String initialDateStr, int offset) {
		Date initialDate;

		try {
			initialDate = dateParser.parse(initialDateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + initialDateStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getEmployeeNextWorkingDay(node.getNodeRef(), initialDate, offset);
	}

	public Date getEmployeeNextWorkingDay(final String nodeStr, final String initialDateStr, int offset) {
		Date initialDate;

		try {
			initialDate = dateParser.parse(initialDateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + initialDateStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getEmployeeNextWorkingDay(new NodeRef(nodeStr), initialDate, offset);
	}

	public Date getEmployeePreviousWorkingDay(final ScriptNode node, final Object jsInitialDate, int offset) {
		Date initialDate = (Date) Context.jsToJava(jsInitialDate, Date.class);
		return workCalendarService.getEmployeePreviousWorkingDay(node.getNodeRef(), initialDate, offset);
	}

	public Date getEmployeePreviousWorkingDay(final String nodeStr, final Object jsInitialDate, int offset) {
		Date initialDate = (Date) Context.jsToJava(jsInitialDate, Date.class);
		return workCalendarService.getEmployeePreviousWorkingDay(new NodeRef(nodeStr), initialDate, offset);
	}

	public Date getEmployeePreviousWorkingDay(final ScriptNode node, final String initialDateStr, int offset) {
		Date initialDate;

		try {
			initialDate = dateParser.parse(initialDateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + initialDateStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getEmployeePreviousWorkingDay(node.getNodeRef(), initialDate, offset);
	}

	public Date getEmployeePreviousWorkingDay(final String nodeStr, final String initialDateStr, int offset) {
		Date initialDate;

		try {
			initialDate = dateParser.parse(initialDateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + initialDateStr + " as Date! " + ex.getMessage(), ex);
		}

		return workCalendarService.getEmployeePreviousWorkingDay(new NodeRef(nodeStr), initialDate, offset);
	}
}
