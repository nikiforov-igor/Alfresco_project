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
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.absence.IAbsence;
import ru.it.lecm.wcalendar.extensions.WCalendarJavascriptExtension;

/**
 * JavaScript root-object под названием "absence". Предоставляет доступ к
 * методам интерфейса IAbsence из web-script'ов.
 *
 * @author vlevin
 */
public class AbsenceJavascriptExtension extends WCalendarJavascriptExtension {

	private IAbsence absenceService;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceJavascriptExtension.class);
	private DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	/**
	 * Получить список отсутствий по NodeRef-у сотрудника.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @return список NodeRef-ов на объекты типа absence. Если к сотруднику не
	 * привязаны отсутствия, возвращает null
	 */
	public Scriptable getAbsenceByEmployee(final String nodeRefStr) {
		List<NodeRef> absenceList = absenceService.getAbsenceByEmployee(new NodeRef(nodeRefStr));
		if (absenceList != null) {
			return getAsScriptable(absenceList);
		} else {
			return null;
		}
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

	/**
	 * Проверить, привязаны ли к сотруднику отсутствия.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @return Расписания привязаны - true. Нет - false.
	 */
	public boolean isAbsenceAssociated(final String nodeRefStr) {
		return absenceService.isAbsenceAssociated(new NodeRef(nodeRefStr));
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

	// TODO Зпилить реализацию нижеследующих методов, которая будет принимать не String, а ScriptNode
	/**
	 * Проверить, отсутствует ли указанный сотрудника указанный день.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @param dateStr интересующая нас дата отсутствия в виде строки
	 * (2013-06-15T12:43:52.371)
	 * @return true - сотрудник отсутствует в указанный день. false - сотрудник
	 * не планировал отсутствия.
	 */
	public boolean isEmployeeAbsent(String nodeRefStr, String dateStr) {
		Date date;
		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		return absenceService.isEmployeeAbsent(new NodeRef(nodeRefStr), date);
	}

	/**
	 * Проверить, отсутствует ли сегодня указанный сотрудник.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @return true - сотрудник сегодня отсутствует
	 */
	public boolean isEmployeeAbsentToday(String nodeRefStr) {
		return absenceService.isEmployeeAbsentToday(new NodeRef(nodeRefStr));
	}

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * указанную дату.
	 *
	 * @param nodeRefStr NodeRef на объект типа employee в виде строки
	 * @param date дата, на которую надо получить экземпляр отсутствия в виде
	 * строки (2013-06-15T12:43:52.371)
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим на указанную дату. Если такового нет, то null
	 */
	public ScriptNode getActiveAbsence(String nodeRefStr, String dateStr) {
		Date date;
		NodeRef absenceNode;
		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		absenceNode = absenceService.getActiveAbsence(new NodeRef(nodeRefStr), date);

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
	public ScriptNode getActiveAbsence(String nodeRefStr) {
		NodeRef absenceNode;
		absenceNode = absenceService.getActiveAbsence(new NodeRef(nodeRefStr));

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
	public void setAbsenceEnd(String nodeRefStr, String dateStr) {
		Date date;
		try {
			date = dateParser.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Can not parse " + dateStr + " as Date! " + ex.getMessage(), ex);
		}
		absenceService.setAbsenceEnd(new NodeRef(nodeRefStr), date);
	}

	/**
	 * Установить параметр "end" у объекта типа absence в текущую дату и время.
	 *
	 * @param nodeRefStr NodeRef на объект типа absence в виде строки
	 */
	public void setAbsenceEnd(String nodeRefStr) {
		absenceService.setAbsenceEnd(new NodeRef(nodeRefStr));
	}
}
