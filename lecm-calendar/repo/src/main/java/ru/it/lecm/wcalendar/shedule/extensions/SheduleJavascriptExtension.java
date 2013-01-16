package ru.it.lecm.wcalendar.shedule.extensions;

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
import ru.it.lecm.wcalendar.shedule.beans.SheduleBean;

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
			return null;
		}
		if (sheduleList != null) {
			return new ScriptNode(sheduleList, serviceRegistry);
		} else {
			return null;
		}
	}

	public JSONObject getParentSheduleStdTime(JSONObject node) {
		JSONObject result = null;
		try {
			Map<String, String> JSONMap = SheduleService.getParentSheduleStdTime(new NodeRef(node.getString("nodeRef")));
			result = new JSONObject(JSONMap);
		} catch (JSONException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
		return result;
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
