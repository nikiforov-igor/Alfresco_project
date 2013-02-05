package ru.it.lecm.wcalendar.extensions;

import java.util.List;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWCalendar;

/**
 * Реализация JavaScript root-object для получения информации о контейнерах для
 * календарей, графиков и отсутсвий и их типов данных.
 *
 * @author vlevin
 */
public class WCalendarJavascriptExtension extends BaseScopableProcessorExtension {

	protected ServiceRegistry serviceRegistry;
	protected IWCalendar wCalendarService;
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(WCalendarJavascriptExtension.class);

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * Получить от Spring-а экземпляр CalendarBean, AbsenceBean или SheduleBean
	 *
	 * @param wCalendarService передается Spring-ом
	 */
	public void setWCalService(IWCalendar wCalendarService) {
		this.wCalendarService = wCalendarService;
	}

	/**
	 * Получить nodeRef на контейнер, в котором находится wCalendarService.
	 *
	 * @return ScriptNode c nodeRef-ом на контейнер, если контейнер определен
	 */
	public ScriptNode getWCalendarContainer() {
		NodeRef container = wCalendarService.getWCalendarDescriptor().getWCalendarContainer();
		if (container != null) {
			return new ScriptNode(container, serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * Получить тип данных wCalendarServiceю
	 *
	 * @return строка с типом данных, если тот определен.
	 */
	public String getItemType() {
		QName itemType = wCalendarService.getWCalendarDescriptor().getWCalendarItemType();
		if (itemType != null) {
			NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();
			return itemType.toPrefixString(namespacePrefixResolver);
		}
		return null;
	}

	/**
	 * обернуть список NodeRef-ов в объект типа Scriptable
	 *
	 * @param nodeRefs список NodeRef-ов
	 * @return специальный объект, доступный для работы из JS
	 */
	protected Scriptable getAsScriptable(List<NodeRef> nodeRefs) {
		Scriptable scope = getScope();
		int size = nodeRefs.size();
		Object[] nodes = new Object[size];
		for (int i = 0; i < size; ++i) {
			nodes[i] = new ScriptNode(nodeRefs.get(i), serviceRegistry, scope);
		}
		return Context.getCurrentContext().newArray(scope, nodes);
	}
}
