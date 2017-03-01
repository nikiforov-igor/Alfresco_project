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
import ru.it.lecm.wcalendar.ICommonWCalendar;

/**
 * Реализация JavaScript root-object для получения информации о контейнерах для
 * календарей, графиков и отсутствий и их типов данных.
 *
 * @author vlevin
 */
public class CommonWCalendarJavascriptExtension extends BaseScopableProcessorExtension {

	protected ServiceRegistry serviceRegistry;
	protected ICommonWCalendar commonWCalendarService;
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(CommonWCalendarJavascriptExtension.class);

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * Получить nodeRef на контейнер, в котором находится commonWCalendarService.
	 *
	 * @return ScriptNode c nodeRef-ом на контейнер, если контейнер определен
	 */
	public ScriptNode getWCalendarContainer() {
		NodeRef container = commonWCalendarService.getWCalendarDescriptor().getWCalendarContainer();
		if (container != null) {
			return new ScriptNode(container, serviceRegistry, getScope());
		} else {
			container = commonWCalendarService.getWCalendarDescriptor().createWCalendarContainer();
			if(container != null) {
				return new ScriptNode(container, serviceRegistry, getScope());
			}
		}
		return null;
	}

	/**
	 * Получить тип данных wCalendarServiceю
	 *
	 * @return строка с типом данных, если тот определен.
	 */
	public String getItemType() {
		QName itemType = commonWCalendarService.getWCalendarDescriptor().getWCalendarItemType();
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
	protected Scriptable getAsScriptable(final List<NodeRef> nodeRefs) {
		Scriptable scope = getScope();
		int size = nodeRefs.size();
		Object[] nodes = new Object[size];
		for (int i = 0; i < size; ++i) {
			nodes[i] = new ScriptNode(nodeRefs.get(i), serviceRegistry, scope);
		}
		return Context.getCurrentContext().newArray(scope, nodes);
	}

}
