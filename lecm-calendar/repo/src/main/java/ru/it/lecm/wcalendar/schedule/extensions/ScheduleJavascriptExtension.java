package ru.it.lecm.wcalendar.schedule.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.extensions.CommonWCalendarJavascriptExtension;
import ru.it.lecm.wcalendar.schedule.ISchedule;
import ru.it.lecm.wcalendar.schedule.ISpecialScheduleRaw;
import ru.it.lecm.wcalendar.schedule.beans.SpecialScheduleRawBean;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaScript root-object под названием "schedule". Предоставляет доступ к
 * методам интерфейса ISchedule из web-script'ов.
 *
 * @author vlevin
 */
public class ScheduleJavascriptExtension extends CommonWCalendarJavascriptExtension {

	private ISchedule scheduleService;
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(ScheduleJavascriptExtension.class);

	public void setScheduleService(ISchedule scheduleService) {
		this.scheduleService = scheduleService;
		this.commonWCalendarService = scheduleService;
	}

	/**
	 * Если node - сотрудник, то возвращает ссылку на расписание подразделения,
	 * в котором сотрудник занимает основную позицию (или вышестоящего
	 * подразделения). Если node - подразделение, то возвращает ссылку на
	 * расписание вышестоящего подразделения. Если расписание к node не
	 * привязано, то возвращает null.
	 *
	 * @param node JSON вида {"nodeRef": SubjRef}, где SubjRef - NodeRef на
	 * сотрудника или орг. единицу.
	 * @return ScriptNode расписания.
	 */
	public ScriptNode getParentScheduleNodeRef(final JSONObject node) {
		String nodeRefStr;
		try {
			nodeRefStr = node.getString("nodeRef");
		} catch (JSONException ex) {
			logger.error(ex.getMessage(), ex);
			throw new WebScriptException(ex.getMessage(), ex);
		}
		return getParentScheduleNodeRef(nodeRefStr);
	}

	/**
	 * Если node - сотрудник, то возвращает ссылку на расписание подразделения,
	 * в котором сотрудник занимает основную позицию (или вышестоящего
	 * подразделения). Если node - подразделение, то возвращает ссылку на
	 * расписание вышестоящего подразделения. Если расписание к node не
	 * привязано, то возвращает null.
	 *
	 * @param node NodeRef на сотрудника или орг. единицу в виде строки
	 * @return ScriptNode расписания.
	 */
	public ScriptNode getParentScheduleNodeRef(final String nodeRefStr) {
		return getParentScheduleNodeRef(new NodeRef(nodeRefStr));
	}

	/**
	 * Если node - сотрудник, то возвращает ссылку на расписание подразделения,
	 * в котором сотрудник занимает основную позицию (или вышестоящего
	 * подразделения). Если node - подразделение, то возвращает ссылку на
	 * расписание вышестоящего подразделения. Если расписание к node не
	 * привязано, то возвращает null.
	 *
	 * @param node NodeRef на сотрудника или орг. единицу в виде строки
	 * @return ScriptNode расписания.
	 */
	public ScriptNode getParentScheduleNodeRef(final ScriptNode nodeRef) {
		return getParentScheduleNodeRef(nodeRef.getNodeRef());
	}

	private ScriptNode getParentScheduleNodeRef(final NodeRef node) {
		NodeRef schedule = scheduleService.getParentSchedule(node);
		if (schedule == null) {
			return null;
		}
		return new ScriptNode(schedule, serviceRegistry, getScope());
	}

	/**
	 * Возвращает время начала работы у данного графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return Время начала работы.
	 */
	public String getScheduleBeginTime(ScriptNode node) {
		return scheduleService.getScheduleBeginTime(node.getNodeRef());
	}

	/**
	 * Возвращает время начала работы у данного графика работы.
	 *
	 * @param nodeRefStr NodeRef на график работы в виде ссылки.
	 * @return Время начала работы.
	 */
	public String getScheduleBeginTime(String nodeRefStr) {
		return scheduleService.getScheduleBeginTime(new NodeRef(nodeRefStr));
	}

	/**
	 * Возвращает время конца работы у данного графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return Время начала работы.
	 */
	public String getScheduleEndTime(ScriptNode node) {
		return scheduleService.getScheduleEndTime(node.getNodeRef());
	}

	/**
	 * Возвращает время конца работы у данного графика работы.
	 *
	 * @param nodeRefStr NodeRef на график работы.
	 * @return Время начала работы.
	 */
	public String getScheduleEndTime(String nodeRefStr) {
		return scheduleService.getScheduleEndTime(new NodeRef(nodeRefStr));
	}

	/**
	 * Получить расписание, привзянное к сотруднику или орг. единице.
	 *
	 * @param node JSON вида {"nodeRef": SubjRef, "fromParent": value1, "exclDefault" : value2},
	 *                где SubjRef - NodeRef на сотрудника или орг. единицу,
	 *                fromParent - флаг, искать ли расписание у родительского эжлемента
	 *             	  exclDefault - флаг, исключать ли дефолтное расписание, если расписание у элемента не найдено
	 * @return NodeRef расписания, привязанного к node. Если таковое отсутствует, то null.
	 */
	public ScriptNode getScheduleByOrgSubject(final JSONObject node) {
		try {
			NodeRef nodeRef = new NodeRef(node.getString("nodeRef"));
			boolean fromParent = node.has("fromParent") && node.getBoolean("fromParent"); // default - false
			boolean exclDefault = node.has("exclDefault") && node.getBoolean("exclDefault"); // default - false

			return getScheduleByOrgSubject(nodeRef, fromParent, exclDefault);
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}
	}

	/**
	 * Получить расписание, привзянное к сотруднику или орг. единице.
	 *
	 * @param nodeRefStr NodeRef на сотрудника или орг. единицу в виде строки
	 * @return NodeRef расписания, привязанного к node. Если таковое
	 * отсутствует, то null.
	 */
	public ScriptNode getScheduleByOrgSubject(final String nodeRefStr) {
		return getScheduleByOrgSubject(new NodeRef(nodeRefStr), false, false);
	}

	/**
	 * Получить расписание, привзянное к сотруднику или орг. единице.
	 *
	 * @param nodeRef NodeRef на сотрудника или орг. единицу
	 * @return NodeRef расписания, привязанного к node. Если таковое
	 * отсутствует, то null.
	 */
	public ScriptNode getScheduleByOrgSubject(final ScriptNode nodeRef) {
		return getScheduleByOrgSubject(nodeRef.getNodeRef(), false, false);
	}

	private ScriptNode getScheduleByOrgSubject(NodeRef node, boolean fromParent, boolean exclDefault) {
		NodeRef schedule = scheduleService.getScheduleByOrgSubject(node, exclDefault || fromParent);
		if (schedule == null && fromParent) {
			schedule = scheduleService.getParentSchedule(node);
			if (schedule == null && !exclDefault) {
				schedule = scheduleService.getDefaultSystemSchedule();
			}
		}
		if (schedule != null) {
			return new ScriptNode(schedule, serviceRegistry, getScope());
		}
		return null;
	}
	/**
	 * Проверяет, привязано ли какое-нибудь расписание к node.
	 *
	 * @param nodeRef NodeRef на сотрудника или орг. единицу. в виде строки
	 * @return true - привязано, false - не привязано.
	 */
	public boolean isScheduleAssociated(final String nodeRef) {
		return isScheduleAssociated(new NodeRef(nodeRef));
	}

	/**
	 * Проверяет, привязано ли какое-нибудь расписание к node.
	 *
	 * @param nodeRef NodeRef на сотрудника или орг. единицу.
	 * @return true - привязано, false - не привязано.
	 */
	public boolean isScheduleAssociated(final ScriptNode nodeRef) {
		return isScheduleAssociated(nodeRef.getNodeRef());
	}

	private boolean isScheduleAssociated(final NodeRef node) {
		return scheduleService.isScheduleAssociated(node);
	}

	/**
	 * Получить тип графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return COMMON - обычный график. SPECIAL - особый.
	 */
	public String getScheduleType(final ScriptNode node) {
		return scheduleService.getScheduleType(node.getNodeRef());
	}

	/**
	 * Получить тип графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return COMMON - обычный график. SPECIAL - особый.
	 */
	public String getScheduleType(final String nodeStr) {
		return scheduleService.getScheduleType(new NodeRef(nodeStr));
	}

	/**
	 * Создает новое особое расписание. Опрабатывает данные из формы, складывает
	 * их в обект SpecialScheduleRawBean и передает дальше.
	 *
	 * @param json данные от формы создания нового особого расписания
	 * (specialScheduleForm).
	 * @return ScriptNode созданного расписания. Если не получлось создать, то
	 * генерирует исключение WebScriptException.
	 */
	public ScriptNode createNewSpecialSchedule(final JSONObject json) {
		String assocSchedEmployeeStr, scheduleDestinationStr, reiterationType, timeBegin, timeEnd, timeLimitStart, timeLimitEnd;
		NodeRef assocSchedEmployeeNode, scheduleDestinationNode;
		ISpecialScheduleRaw rawScheduleData = new SpecialScheduleRawBean();

		try {
			assocSchedEmployeeStr = json.getString("assoc_lecm-sched_sched-employee-link-assoc_added");
			scheduleDestinationStr = json.getString("alf_destination");
			timeLimitStart = json.getString("prop_lecm-sched_time-limit-start");
			timeLimitEnd = json.getString("prop_lecm-sched_time-limit-end");
			reiterationType = json.getString("reiteration-type");
			timeBegin = json.getString("prop_lecm-sched_std-begin");
			timeEnd = json.getString("prop_lecm-sched_std-end");
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}

		rawScheduleData.setTimeWorkBegins(timeBegin);
		rawScheduleData.setTimeWorkEnds(timeEnd);
		rawScheduleData.setTimeLimitStart(timeLimitStart);
		rawScheduleData.setTimeLimitEnd(timeLimitEnd);

		if (reiterationType.equalsIgnoreCase("week-days")) { // по определенным дням недели
			List<Boolean> weekDaysMask = new ArrayList<Boolean>();
			rawScheduleData.setReiterationType(ISpecialScheduleRaw.ReiterationType.WEEK_DAYS);
			for (int i = 1; i <= 7; i++) {
				try {
					boolean wDay = json.getBoolean("w" + i);
					weekDaysMask.add(wDay);
				} catch (JSONException ex) {
					weekDaysMask.add(false);
					logger.debug(ex.getMessage());
				}
			}
			rawScheduleData.setWeekDays(weekDaysMask);
		} else if (reiterationType.equalsIgnoreCase("month-days")) { // по определенным числам месяца
			String mDaysStr;
			List<Integer> mDaysList = new ArrayList<Integer>();
			rawScheduleData.setReiterationType(ISpecialScheduleRaw.ReiterationType.MONTH_DAYS);
			try {
				mDaysStr = json.getString("month-days");
			} catch (JSONException ex) {
				logger.error(ex.getMessage(), ex);
				throw new WebScriptException(ex.getMessage(), ex);
			}
			String[] mDaysArr = mDaysStr.split(",");
			for (int i = 0; i < mDaysArr.length; i++) {
				mDaysList.add(Integer.parseInt(mDaysArr[i]));
			}
			rawScheduleData.setMonthDays(mDaysList);
		} else if (reiterationType.equalsIgnoreCase("shift-work")) { // сменный график
			int workingDaysAmount;
			int workingDaysInterval;
			rawScheduleData.setReiterationType(ISpecialScheduleRaw.ReiterationType.SHIFT);
			try {
				workingDaysAmount = json.getInt("working-days-amount");
				workingDaysInterval = json.getInt("working-days-interval");
			} catch (JSONException ex) {
				throw new WebScriptException(ex.getMessage(), ex);
			}
			rawScheduleData.setWorkingDaysAmount(workingDaysAmount);
			rawScheduleData.setWorkingDaysInterval(workingDaysInterval);
		}
		assocSchedEmployeeNode = new NodeRef(assocSchedEmployeeStr);
		scheduleDestinationNode = new NodeRef(scheduleDestinationStr);

		NodeRef createdNode = scheduleService.createNewSpecialSchedule(rawScheduleData, assocSchedEmployeeNode, scheduleDestinationNode);

		if (createdNode == null) {
			throw new WebScriptException("Something has gone wrong: response is empty!");
		} else {
			return new ScriptNode(createdNode, serviceRegistry, getScope());
		}
	}
}
