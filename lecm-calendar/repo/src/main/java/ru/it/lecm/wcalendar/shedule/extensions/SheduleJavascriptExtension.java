package ru.it.lecm.wcalendar.shedule.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.shedule.beans.SheduleBean;
import ru.it.lecm.wcalendar.shedule.beans.SpecialSheduleRawBean;

/**
 * Реализация JavaScript root-object для получения информации о контейнерах для
 * календарей, графиков и отсутсвий и их типов данных.
 *
 * @author vlevin
 */
public class SheduleJavascriptExtension extends BaseScopableProcessorExtension {

	private SheduleBean SheduleService;
	private ServiceRegistry serviceRegistry;
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(SheduleJavascriptExtension.class);

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setSheduleService(SheduleBean SheduleService) {
		this.SheduleService = SheduleService;
	}

	public ScriptNode getParentSheduleNodeRef(JSONObject node) {
		NodeRef sheduleList = null;
		try {
			sheduleList = SheduleService.getParentShedule(new NodeRef(node.getString("nodeRef")));
		} catch (JSONException ex) {
			logger.error(ex.getMessage(), ex);
			throw new WebScriptException(ex.getMessage(), ex);
		}
		if (sheduleList != null) {
			return new ScriptNode(sheduleList, serviceRegistry);
		} else {
			throw new WebScriptException("Error parsing JSON params!");
		}
	}

	public JSONObject getParentSheduleStdTime(JSONObject node) {
		JSONObject result = null;
		try {
			Map<String, String> JSONMap = SheduleService.getParentSheduleStdTime(new NodeRef(node.getString("nodeRef")));
			result = new JSONObject(JSONMap);
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}
		return result;
	}

	public boolean isSheduleAssociated(NodeRef node) {
		return SheduleService.isSheduleAssociated(node);

	}

	public ScriptNode createNewSpecialShedule(JSONObject json) {
		String assocShedEmployeeStr, sheduleDestinationStr, reiterationType, timeBegin, timeEnd, timeLimitStart, timeLimitEnd;
		NodeRef assocShedEmployeeNode, sheduleDestinationNode;
		SpecialSheduleRawBean rawSheduleData = new SpecialSheduleRawBean();

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
			rawSheduleData.setReiterationType(SpecialSheduleRawBean.ReiterationType.WEEK_DAYS);
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
			rawSheduleData.setReiterationType(SpecialSheduleRawBean.ReiterationType.MONTH_DAYS);
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
			rawSheduleData.setReiterationType(SpecialSheduleRawBean.ReiterationType.SHIFT);
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

		NodeRef createdNode = SheduleService.createNewSpecialShedule(rawSheduleData, assocShedEmployeeNode, sheduleDestinationNode);
		
		if (createdNode == null) {
			throw new WebScriptException("Something has gone wrong: response is empty!");
		} else {
			return new ScriptNode(createdNode, serviceRegistry);
		}
	}

	/**
	 * обернуть список NodeRef-ов в объект типа Scriptable
	 *
	 * @param nodeRefs список NodeRef-ов
	 * @return специальный объект доступный для работы из JS
	 */
	private Scriptable getAsScriptable(List<NodeRef> nodeRefs) {
		Scriptable scope = getScope();
		int size = nodeRefs.size();
		Object[] nodes = new Object[size];
		for (int i = 0; i < size; ++i) {
			nodes[i] = new ScriptNode(nodeRefs.get(i), serviceRegistry, scope);
		}
		return Context.getCurrentContext().newArray(scope, nodes);
	}
}
