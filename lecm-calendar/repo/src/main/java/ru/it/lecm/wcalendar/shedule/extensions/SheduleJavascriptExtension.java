package ru.it.lecm.wcalendar.shedule.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.extensions.CommonWCalendarJavascriptExtension;
import ru.it.lecm.wcalendar.shedule.IShedule;
import ru.it.lecm.wcalendar.shedule.ISpecialSheduleRaw;
import ru.it.lecm.wcalendar.shedule.beans.SpecialSheduleRawBean;

/**
 * JavaScript root-object под названием "shedule". Предоставляет доступ к
 * методам интерфейса IShedule из web-script'ов.
 *
 * @author vlevin
 */
public class SheduleJavascriptExtension extends CommonWCalendarJavascriptExtension {

	private IShedule sheduleService;
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(SheduleJavascriptExtension.class);

	public void setSheduleService(IShedule sheduleService) {
		this.sheduleService = sheduleService;
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
	public ScriptNode getParentSheduleNodeRef(final JSONObject node) {
		String nodeRefStr;
		try {
			nodeRefStr = node.getString("nodeRef");
		} catch (JSONException ex) {
			logger.error(ex.getMessage(), ex);
			throw new WebScriptException(ex.getMessage(), ex);
		}
		return getParentSheduleNodeRef(nodeRefStr);
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
	public ScriptNode getParentSheduleNodeRef(final String nodeRefStr) {
		return getParentSheduleNodeRef(new NodeRef(nodeRefStr));
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
	public ScriptNode getParentSheduleNodeRef(final ScriptNode nodeRef) {
		return getParentSheduleNodeRef(nodeRef.getNodeRef());
	}

	private ScriptNode getParentSheduleNodeRef(final NodeRef node) {
		NodeRef shedule = sheduleService.getParentShedule(node);
		return new ScriptNode(shedule, serviceRegistry);
	}

	/**
	 * Возвращает время работы и тип родительского расписания (см.
	 * getParentShedule).
	 *
	 * @param node JSON вида {"nodeRef": SubjRef}, где SubjRef - NodeRef на
	 * сотрудника или орг. единицу.
	 * @return Ключи JSON'а: "type" - тип расписания, "begin" - время начала
	 * работы, "end" - время конца работы.
	 */
	public JSONObject getParentSheduleStdTime(final JSONObject node) {
		String nodeRefStr;
		try {
			nodeRefStr = node.getString("nodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}
		return getParentSheduleStdTime(nodeRefStr);
	}

	/**
	 * Возвращает время работы и тип родительского расписания (см.
	 * getParentShedule).
	 *
	 * @param nodeRefStr NodeRef на сотрудника или орг. единицу в виде строки
	 * @return Ключи JSON'а: "type" - тип расписания, "begin" - время начала
	 * работы, "end" - время конца работы.
	 */
	public JSONObject getParentSheduleStdTime(final String nodeRefStr) {
		return getParentSheduleStdTime(new NodeRef(nodeRefStr));
	}

	/**
	 * Возвращает время работы и тип родительского расписания (см.
	 * getParentShedule).
	 *
	 * @param nodeRef NodeRef на сотрудника или орг. единицу
	 * @return Ключи JSON'а: "type" - тип расписания, "begin" - время начала
	 * работы, "end" - время конца работы.
	 */
	public JSONObject getParentSheduleStdTime(final ScriptNode nodeRef) {
		return getParentSheduleStdTime(nodeRef.getNodeRef());
	}

	private JSONObject getParentSheduleStdTime(final NodeRef node) {
		JSONObject result;

		Map<String, String> JSONMap = sheduleService.getParentSheduleStdTime(node);
		result = new JSONObject(JSONMap);

		return result;
	}

	/**
	 * Получить расписание, привзянное к сотруднику или орг. единице.
	 *
	 * @param node JSON вида {"nodeRef": SubjRef}, где SubjRef - NodeRef на
	 * сотрудника или орг. единицу.
	 * @return NodeRef расписания, привязанного к node. Если таковое
	 * отсутствует, то null.
	 */
	public ScriptNode getSheduleByOrgSubject(final JSONObject node) {
		try {
			return getSheduleByOrgSubject(node.getString("nodeRef"));
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
	public ScriptNode getSheduleByOrgSubject(final String nodeRefStr) {
		return getSheduleByOrgSubject(new NodeRef(nodeRefStr));
	}

	/**
	 * Получить расписание, привзянное к сотруднику или орг. единице.
	 *
	 * @param nodeRef NodeRef на сотрудника или орг. единицу
	 * @return NodeRef расписания, привязанного к node. Если таковое
	 * отсутствует, то null.
	 */
	public ScriptNode getSheduleByOrgSubject(final ScriptNode nodeRef) {
		return getSheduleByOrgSubject(nodeRef.getNodeRef());
	}

	private ScriptNode getSheduleByOrgSubject(NodeRef node) {
		NodeRef nodeRef = sheduleService.getSheduleByOrgSubject(node);
		if (nodeRef != null) {
			return new ScriptNode(nodeRef, serviceRegistry);
		}
		return null;
	}

	/**
	 * Проверяет, привязано ли какое-нибудь расписание к node.
	 *
	 * @param nodeRef NodeRef на сотрудника или орг. единицу. в виде строки
	 * @return true - привязано, false - не привязано.
	 */
	public boolean isSheduleAssociated(final String nodeRef) {
		return isSheduleAssociated(new NodeRef(nodeRef));
	}

	/**
	 * Проверяет, привязано ли какое-нибудь расписание к node.
	 *
	 * @param nodeRef NodeRef на сотрудника или орг. единицу.
	 * @return true - привязано, false - не привязано.
	 */
	public boolean isSheduleAssociated(final ScriptNode nodeRef) {
		return isSheduleAssociated(nodeRef.getNodeRef());
	}

	private boolean isSheduleAssociated(final NodeRef node) {
		return sheduleService.isSheduleAssociated(node);
	}

	/**
	 * Создает новое особое расписание. Опрабатывает данные из формы, складывает
	 * их в обект SpecialSheduleRawBean и передает дальше.
	 *
	 * @param json данные от формы создания нового особого расписания
	 * (specialSheduleForm).
	 * @return ScriptNode созданного расписания. Если не получлось создать, то
	 * генерирует исключение WebScriptException.
	 */
	public ScriptNode createNewSpecialShedule(final JSONObject json) {
		String assocShedEmployeeStr, sheduleDestinationStr, reiterationType, timeBegin, timeEnd, timeLimitStart, timeLimitEnd;
		NodeRef assocShedEmployeeNode, sheduleDestinationNode;
		ISpecialSheduleRaw rawSheduleData = new SpecialSheduleRawBean();

		try {
			assocShedEmployeeStr = json.getString("assoc_lecm-shed_shed-employee-link-assoc_added");
			sheduleDestinationStr = json.getString("alf_destination");
			timeLimitStart = json.getString("prop_lecm-shed_time-limit-start");
			timeLimitEnd = json.getString("prop_lecm-shed_time-limit-end");
			reiterationType = json.getString("reiteration-type");
			timeBegin = json.getString("prop_lecm-shed_std-begin");
			timeEnd = json.getString("prop_lecm-shed_std-end");
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}

		rawSheduleData.setTimeWorkBegins(timeBegin);
		rawSheduleData.setTimeWorkEnds(timeEnd);
		rawSheduleData.setTimeLimitStart(timeLimitStart);
		rawSheduleData.setTimeLimitEnd(timeLimitEnd);

		if (reiterationType.equalsIgnoreCase("week-days")) { // по определенным дням недели
			List<Boolean> weekDaysMask = new ArrayList<Boolean>();
			rawSheduleData.setReiterationType(ISpecialSheduleRaw.ReiterationType.WEEK_DAYS);
			for (int i = 1; i <= 7; i++) {
				try {
					boolean wDay = json.getBoolean("w" + i);
					weekDaysMask.add(wDay);
				} catch (JSONException ex) {
					weekDaysMask.add(false);
					logger.debug(ex.getMessage());
				}
			}
			rawSheduleData.setWeekDays(weekDaysMask);
		} else if (reiterationType.equalsIgnoreCase("month-days")) { // по определенным числам месяца
			String mDaysStr;
			List<Integer> mDaysList = new ArrayList<Integer>();
			rawSheduleData.setReiterationType(ISpecialSheduleRaw.ReiterationType.MONTH_DAYS);
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
			rawSheduleData.setMonthDays(mDaysList);
		} else if (reiterationType.equalsIgnoreCase("shift-work")) { // сменный график
			int workingDaysAmount;
			int workingDaysInterval;
			rawSheduleData.setReiterationType(ISpecialSheduleRaw.ReiterationType.SHIFT);
			try {
				workingDaysAmount = json.getInt("working-days-amount");
				workingDaysInterval = json.getInt("working-days-interval");
			} catch (JSONException ex) {
				throw new WebScriptException(ex.getMessage(), ex);
			}
			rawSheduleData.setWorkingDaysAmount(workingDaysAmount);
			rawSheduleData.setWorkingDaysInterval(workingDaysInterval);
		}
		assocShedEmployeeNode = new NodeRef(assocShedEmployeeStr);
		sheduleDestinationNode = new NodeRef(sheduleDestinationStr);

		NodeRef createdNode = sheduleService.createNewSpecialShedule(rawSheduleData, assocShedEmployeeNode, sheduleDestinationNode);

		if (createdNode == null) {
			throw new WebScriptException("Something has gone wrong: response is empty!");
		} else {
			return new ScriptNode(createdNode, serviceRegistry);
		}
	}
}
