package ru.it.lecm.wcalendar.extensions;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWCalCommon;

/**
 * Реализация JavaScript root-object для получения информации о контейнерах для
 * календарей, графиков и отсутсвий и их типов данных.
 *
 * @author vlevin
 */
public class WCalendarJavascriptExtension extends BaseScopableProcessorExtension {

	private ServiceRegistry serviceRegistry;
	private IWCalCommon wCalendarService;
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
	public void setWCalService(IWCalCommon wCalendarService) {
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
	 * Проверка, занимает ли сотрудник руководящую позицию.
	 *
	 * @param nodeRef NodeRef сотрудника (lecm-orgstr:employee)
	 * @return true если сотрудник занимает где-либо руководящую позицию.
	 */
	public boolean isEngineer(final String employeeRef) {
		return wCalendarService.getWCalendarDescriptor().isEngineer(new NodeRef(employeeRef));
	}

	/**
	 * Проверка, имеет ли сотрудник роль "Технолог календарей".
	 *
	 * @param nodeRef NodeRef сотрудника (lecm-orgstr:employee)
	 * @return true если сотрудник имеет роль "Технолог календарей".
	 */
	public boolean isBoss(final String employeeRef) {
		return wCalendarService.getWCalendarDescriptor().isBoss(new NodeRef(employeeRef));
	}
}
