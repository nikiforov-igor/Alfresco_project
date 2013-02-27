package ru.it.lecm.wcalendar.extensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.IWorkCalendar;
import org.mozilla.javascript.Context;

/**
 * JavaScript root-object обертка для интерфейса IWorkCalendar.
 *
 * @see ru.it.lecm.wcalendar.IWorkCalendar
 * @author vlevin
 */
public class WorkCalendarJavascriptExtension extends CommonWCalendarJavascriptExtension {

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
	public boolean getEmployeeAvailability(String nodeRefStr, Object jsDate) {
		Date date;
		date = (Date) Context.jsToJava(jsDate, Date.class);

		return workCalendarService.getEmployeeAvailability(new NodeRef(nodeRefStr), date);
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
	public boolean getEmployeeAvailability(String nodeRefStr, String dateStr) {
		Date date;

		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		return workCalendarService.getEmployeeAvailability(new NodeRef(nodeRefStr), date);
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
	public JSONArray getEmployeeWorkindDays(String nodeRefStr, String startStr, String endStr) {
		Date start, end;
		JSONArray result;

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

		List<Date> employeeWorkindDays = workCalendarService.getEmployeeWorkindDays(new NodeRef(nodeRefStr), start, end);
		result = new JSONArray(employeeWorkindDays);

		return result;
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
	public JSONArray getEmployeeWorkindDays(String nodeRefStr, Object jsStart, Object jsEnd) {
		Date start, end;
		JSONArray result;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		List<Date> employeeWorkindDays = workCalendarService.getEmployeeWorkindDays(new NodeRef(nodeRefStr), start, end);
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
	public JSONArray getEmployeeNonWorkindDays(String nodeRefStr, String startStr, String endStr) {
		Date start, end;
		JSONArray result;

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

		List<Date> employeeNonWorkindDays = workCalendarService.getEmployeeNonWorkindDays(new NodeRef(nodeRefStr), start, end);
		result = new JSONArray(employeeNonWorkindDays);

		return result;

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
	public JSONArray getEmployeeNonWorkindDays(String nodeRefStr, Object jsStart, Object jsEnd) {
		Date start, end;
		JSONArray result;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		List<Date> employeeNonWorkindDays = workCalendarService.getEmployeeNonWorkindDays(new NodeRef(nodeRefStr), start, end);
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
	public int getEmployeeWorkingDaysNumber(String nodeRefStr, String startStr, String endStr) {
		Date start, end;
		int result;

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

		result = workCalendarService.getEmployeeWorkingDaysNumber(new NodeRef(nodeRefStr), start, end);
		return result;

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
	public int getEmployeeWorkingDaysNumber(String nodeRefStr, Object jsStart, Object jsEnd) {
		Date start, end;
		int result;

		start = (Date) Context.jsToJava(jsStart, Date.class);
		end = (Date) Context.jsToJava(jsEnd, Date.class);

		result = workCalendarService.getEmployeeWorkingDaysNumber(new NodeRef(nodeRefStr), start, end);
		return result;

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
	public Date getPlannedJobFinish(String nodeRefStr, String startStr, int workingDaysRequired) {
		Date start, result;

		try {
			start = dateParser.parse(startStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + startStr + " as Date! " + ex.getMessage(), ex);
		}

		result = workCalendarService.getPlannedJobFinish(new NodeRef(nodeRefStr), start, workingDaysRequired);

		return result;
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
	public Date getPlannedJobFinish(String nodeRefStr, Object jsStart, int workingDaysRequired) {
		Date start, result;

		start = (Date) Context.jsToJava(jsStart, Date.class);

		result = workCalendarService.getPlannedJobFinish(new NodeRef(nodeRefStr), start, workingDaysRequired);

		return result;
	}
}
