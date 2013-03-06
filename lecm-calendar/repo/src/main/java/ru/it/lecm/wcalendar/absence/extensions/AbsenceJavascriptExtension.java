package ru.it.lecm.wcalendar.absence.extensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.absence.IAbsence;
import ru.it.lecm.wcalendar.extensions.CommonWCalendarJavascriptExtension;

/**
 * JavaScript root-object под названием "absence". Предоставляет доступ к
 * методам интерфейса IAbsence из web-script'ов.
 *
 * @author vlevin
 */
public class AbsenceJavascriptExtension extends CommonWCalendarJavascriptExtension {

	private IAbsence absenceService;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceJavascriptExtension.class);
	private DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
		this.commonWCalendarService = absenceService;
	}

	/**
	 * Получить список отсутствий по NodeRef-у сотрудника.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @return список NodeRef-ов на объекты типа absence. Если к сотруднику не
	 * привязаны отсутствия, возвращает null
	 */
	public Scriptable getAbsenceByEmployee(final String nodeRefStr) {
		return getAbsenceByEmployee(new NodeRef(nodeRefStr));
	}

	/**
	 * Получить список отсутствий по NodeRef-у сотрудника.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @return список NodeRef-ов на объекты типа absence. Если к сотруднику не
	 * привязаны отсутствия, возвращает null
	 */
	public Scriptable getAbsenceByEmployee(final ScriptNode nodeRef) {
		return getAbsenceByEmployee(nodeRef.getNodeRef());
	}

	/**
	 * Получить список отсутствий по NodeRef-у сотрудника.
	 *
	 * @param node NodeRef на объект типа employee в виде JSONObject ({"nodeRef"
	 * : "NodeRef_на_employee"})
	 * @return список NodeRef-ов на объекты типа absence. Если к сотруднику не
	 * привязаны отсутствия, возвращает null
	 */
	public Scriptable getAbsenceByEmployee(final JSONObject node) {
		try {
			return getAbsenceByEmployee(node.getString("nodeRef"));
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}
	}

	private Scriptable getAbsenceByEmployee(final NodeRef node) {
		List<NodeRef> absenceList = absenceService.getAbsenceByEmployee(node);
		if (absenceList != null) {
			return getAsScriptable(absenceList);
		} else {
			return null;
		}
	}

	/**
	 * Проверить, привязаны ли к сотруднику отсутствия.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @return Расписания привязаны - true. Нет - false.
	 */
	public boolean isAbsenceAssociated(final String nodeRefStr) {
		return isAbsenceAssociated(new NodeRef(nodeRefStr));
	}

	/**
	 * Проверить, привязаны ли к сотруднику отсутствия.
	 *
	 * @param nodeRef NodeRef на объект типа employee в виде строки
	 * @return Расписания привязаны - true. Нет - false.
	 */
	public boolean isAbsenceAssociated(final ScriptNode nodeRef) {
		return isAbsenceAssociated(nodeRef.getNodeRef());
	}

	private boolean isAbsenceAssociated(final NodeRef node) {
		return absenceService.isAbsenceAssociated(node);
	}

	/**
	 * Проверить, можно ли создать отсутствие для указанного сотрудника в
	 * указанном промежутке времени. В одном промежутке времени не может быть
	 * два отсутствия, так что перед созданием нового отсутствия нужно
	 * проверить, не запланировал ли сотрудник отлучиться на это время
	 *
	 * @param json JSON вида: {"nodeRef": "NodeRef_на_employee",
	 * "begin":"время_начала_промежутка_(напр._2013-02-01T00:00:00.000)",
	 * "end":"время_конца_промежутка_(напр._2013-02-02T23:59:59.000)", }
	 * @return true - промежуток свободен, создать отсутствие можно, false - на
	 * данный промежуток отсутствие уже запланировано.
	 */
	public boolean isIntervalSuitableForAbsence(final JSONObject json) {
		String beginStr, endStr, employeeRefStr;
		Date begin, end;

		try {
			beginStr = json.getString("begin");
			endStr = json.getString("end");
			employeeRefStr = json.getString("nodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}

		try {
			begin = dateParser.parse(beginStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + beginStr + " as Date! " + ex.getMessage(), ex);
		}

		try {
			end = dateParser.parse(endStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + endStr + " as Date! " + ex.getMessage(), ex);
		}

		return absenceService.isIntervalSuitableForAbsence(new NodeRef(employeeRefStr), begin, end);
	}

	/**
	 * Проверить, отсутствует ли указанный сотрудника указанный день.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @param dateStr интересующая нас дата отсутствия в виде строки
	 * (2013-06-15T12:43:52.371)
	 * @return true - сотрудник отсутствует в указанный день. false - сотрудник
	 * не планировал отсутствия.
	 */
	public boolean isEmployeeAbsent(final String nodeRefStr, final String dateStr) {
		return isEmployeeAbsent(new NodeRef(nodeRefStr), dateStr);
	}

	/**
	 * Проверить, отсутствует ли указанный сотрудника указанный день.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @param dateStr интересующая нас дата отсутствия в виде строки
	 * (2013-06-15T12:43:52.371)
	 * @return true - сотрудник отсутствует в указанный день. false - сотрудник
	 * не планировал отсутствия.
	 */
	public boolean isEmployeeAbsent(final ScriptNode nodeRef, final String dateStr) {
		return isEmployeeAbsent(nodeRef.getNodeRef(), dateStr);
	}

	/**
	 * Проверить, отсутствует ли указанный сотрудника указанный день.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @param jsDate интересующая нас дата отсутствия в виде JS-объекта Date.
	 * @return true - сотрудник отсутствует в указанный день. false - сотрудник
	 * не планировал отсутствия.
	 */
	public boolean isEmployeeAbsent(final String nodeRefStr, final Object jsDate) {
		Date date = (Date) Context.jsToJava(jsDate, Date.class);
		return isEmployeeAbsent(new NodeRef(nodeRefStr), date);
	}

	/**
	 * Проверить, отсутствует ли указанный сотрудника указанный день.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @param jsDate интересующая нас дата отсутствия в виде JS-объекта Date.
	 * (2013-06-15T12:43:52.371)
	 * @return true - сотрудник отсутствует в указанный день. false - сотрудник
	 * не планировал отсутствия.
	 */
	public boolean isEmployeeAbsent(final ScriptNode nodeRef, final Object jsDate) {
		Date date = (Date) Context.jsToJava(jsDate, Date.class);
		return isEmployeeAbsent(nodeRef.getNodeRef(), date);
	}

	private boolean isEmployeeAbsent(final NodeRef node, final String dateStr) {
		Date date;

		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		return isEmployeeAbsent(node, date);
	}

	private boolean isEmployeeAbsent(final NodeRef node, final Date date) {
		return absenceService.isEmployeeAbsent(node, date);
	}

	/**
	 * Проверить, отсутствует ли сегодня указанный сотрудник.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @return true - сотрудник сегодня отсутствует
	 */
	public boolean isEmployeeAbsentToday(final String nodeRefStr) {
		return isEmployeeAbsentToday(new NodeRef(nodeRefStr));
	}

	/**
	 * Проверить, отсутствует ли сегодня указанный сотрудник.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @return true - сотрудник сегодня отсутствует
	 */
	public boolean isEmployeeAbsentToday(final ScriptNode nodeRef) {
		return isEmployeeAbsentToday(nodeRef.getNodeRef());
	}

	private boolean isEmployeeAbsentToday(final NodeRef node) {
		return absenceService.isEmployeeAbsentToday(node);
	}

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * указанную дату.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @param dateStr дата, на которую надо получить экземпляр отсутствия в виде
	 * строки (2013-06-15T12:43:52.371)
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим на указанную дату. Если такового нет, то null
	 */
	public ScriptNode getActiveAbsence(final String nodeRefStr, final String dateStr) {
		return getActiveAbsence(new NodeRef(nodeRefStr), dateStr);
	}

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * указанную дату.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @param dateStr дата, на которую надо получить экземпляр отсутствия в виде
	 * строки (2013-06-15T12:43:52.371)
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим на указанную дату. Если такового нет, то null
	 */
	public ScriptNode getActiveAbsence(final ScriptNode nodeRef, final String dateStr) {
		return getActiveAbsence(nodeRef.getNodeRef(), dateStr);
	}

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * указанную дату.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @param jsDate дата, на которую надо получить экземпляр отсутствия в виде
	 * JS-объекта Date.
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим на указанную дату. Если такового нет, то null
	 */
	public ScriptNode getActiveAbsence(final String nodeRefStr, final Object jsDate) {
		Date date = (Date) Context.jsToJava(jsDate, Date.class);
		return getActiveAbsence(new NodeRef(nodeRefStr), date);
	}

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * указанную дату.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @param jsDate дата, на которую надо получить экземпляр отсутствия в виде
	 * JS-объекта Date.
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим на указанную дату. Если такового нет, то null
	 */
	public ScriptNode getActiveAbsence(final ScriptNode nodeRef, final Object jsDate) {
		Date date = (Date) Context.jsToJava(jsDate, Date.class);
		return getActiveAbsence(nodeRef.getNodeRef(), date);
	}

	private ScriptNode getActiveAbsence(final NodeRef node, final String dateStr) {
		Date date;

		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}

		return getActiveAbsence(node, date);
	}

	private ScriptNode getActiveAbsence(final NodeRef node, final Date date) {
		NodeRef absenceNode = absenceService.getActiveAbsence(node, date);

		if (absenceNode != null) {
			return new ScriptNode(absenceNode, serviceRegistry);
		} else {
			return null;
		}
	}

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * сегодня.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим сегодня.
	 */
	public ScriptNode getActiveAbsence(final String nodeRefStr) {
		return getActiveAbsence(new NodeRef(nodeRefStr));
	}

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * сегодня.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим сегодня.
	 */
	public ScriptNode getActiveAbsence(final ScriptNode nodeRef) {
		return getActiveAbsence(nodeRef.getNodeRef());
	}

	private ScriptNode getActiveAbsence(final NodeRef node) {
		NodeRef absenceNode;

		absenceNode = absenceService.getActiveAbsence(node);

		if (absenceNode != null) {
			return new ScriptNode(absenceNode, serviceRegistry);
		} else {
			return null;
		}
	}

	/**
	 * Установить параметр "end" у объекта типа absence в определенное значение.
	 *
	 * @param nodeRefStr NodeRef на объект типа absence в виде строки
	 * @param dateStr дата, в которую необходимо установить параметр "end" в
	 * виде строки (2013-06-15T12:43:52.371)
	 */
	public void setAbsenceEnd(final String nodeRefStr, final String dateStr) {
		setAbsenceEnd(new NodeRef(nodeRefStr), dateStr);
	}

	/**
	 * Установить параметр "end" у объекта типа absence в определенное значение.
	 *
	 * @param nodeRef NodeRef на объект типа absence
	 * @param dateStr дата, в которую необходимо установить параметр "end" в
	 * виде строки (2013-06-15T12:43:52.371)
	 */
	public void setAbsenceEnd(final ScriptNode nodeRef, final String dateStr) {
		setAbsenceEnd(nodeRef.getNodeRef(), dateStr);
	}

	/**
	 * Установить параметр "end" у объекта типа absence в определенное значение.
	 *
	 * @param nodeRefStr NodeRef на объект типа absence в виде строки
	 * @param jsDate дата, в которую необходимо установить параметр "end" в виде
	 * JS-объекта Date.
	 */
	public void setAbsenceEnd(final String nodeRefStr, final Object jsDate) {
		Date date = (Date) Context.jsToJava(jsDate, Date.class);
		setAbsenceEnd(new NodeRef(nodeRefStr), date);
	}

	/**
	 * Установить параметр "end" у объекта типа absence в определенное значение.
	 *
	 * @param nodeRef NodeRef на объект типа absence
	 * @param jsDate дата, в которую необходимо установить параметр "end" в виде
	 * JS-объекта Date.
	 */
	public void setAbsenceEnd(final ScriptNode nodeRef, final Object jsDate) {
		Date date = (Date) Context.jsToJava(jsDate, Date.class);
		setAbsenceEnd(nodeRef.getNodeRef(), date);
	}

	private void setAbsenceEnd(final NodeRef node, final String dateStr) {
		Date date;

		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		setAbsenceEnd(node, date);
	}

	private void setAbsenceEnd(final NodeRef node, final Date date) {
		absenceService.setAbsenceEnd(node, date);
	}

	/**
	 * Установить параметр "бессрочное" ("unlimited") у объекта типа absence в
	 * определенное значение.
	 *
	 * @param nodeRefStr NodeRef на объект типа absence в виде строки.
	 * @param unlimited значение, в которое следует установить параметр
	 * "бессрочное".
	 */
	public void setAbsenceUnlimited(final String nodeRefStr, final boolean unlimited) {
		setAbsenceUnlimited(new NodeRef(nodeRefStr), unlimited);
	}

	/**
	 * Установить параметр "бессрочное" ("unlimited") у объекта типа absence в
	 * определенное значение.
	 *
	 * @param nodeRef NodeRef на объект типа absence
	 * @param unlimited значение, в которое следует установить параметр
	 * "бессрочное".
	 */
	public void setAbsenceUnlimited(final ScriptNode nodeRef, final boolean unlimited) {
		setAbsenceUnlimited(nodeRef.getNodeRef(), unlimited);
	}

	private void setAbsenceUnlimited(final NodeRef node, final boolean unlimited) {
		absenceService.setAbsenceUnlimited(node, unlimited);
	}

	/**
	 * Установить параметр "end" у объекта типа absence в текущую дату и время.
	 *
	 * @param nodeRefStr NodeRef на объект типа absence в виде строки
	 */
	public void setAbsenceEnd(final String nodeRefStr) {
		setAbsenceEnd(new NodeRef(nodeRefStr));
	}

	/**
	 * Установить параметр "end" у объекта типа absence в текущую дату и время.
	 *
	 * @param nodeRef NodeRef на объект типа absence в виде строки
	 */
	public void setAbsenceEnd(final ScriptNode nodeRef) {
		setAbsenceEnd(nodeRef.getNodeRef());
	}

	private void setAbsenceEnd(final NodeRef node) {
		absenceService.setAbsenceEnd(node);
	}
}
