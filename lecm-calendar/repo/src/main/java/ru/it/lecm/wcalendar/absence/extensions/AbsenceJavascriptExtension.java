package ru.it.lecm.wcalendar.absence.extensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
	 * @param node NodeRef на объект типа employee в виде JSONObject
	 * ({"nodeRef" : "NodeRef_на_employee"})
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
		DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
}
