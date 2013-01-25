package ru.it.lecm.wcalendar;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface IWCalCommon {

	/**
	 * Получение ссылки на корневые каталоги для календарей, графиков работы и
	 * отсутствия.
	 *
	 * @return nodeRef контейнера
	 */
	NodeRef getWCalendarContainer();

	/**
	 * Получение обекта класса, реализующего интерфейс IWCalCommon
	 * (CalendarBean, AbsenceBean, SheduleBean).
	 *
	 * @return объект CalendarBean, AbsenceBean или SheduleBean
	 */
	IWCalCommon getWCalendarDescriptor();

	/**
	 * Получение правильно оформленного типа данных для календаря, графика
	 * работы или отсутсвия.
	 *
	 * @return qualified name типа данных с namespace-ом
	 */
	QName getWCalendarItemType();

	/**
	 * Проверка, занимает ли сотрудник руководящую позицию.
	 *
	 * @param nodeRef NodeRef сотрудника (lecm-orgstr:employee)
	 * @return true если сотрудник занимает где-либо руководящую позицию.
	 */
	public boolean isBoss(NodeRef nodeRef);

	/**
	 * Проверка, имеет ли сотрудник роль "Технолог календарей".
	 *
	 * @param nodeRef NodeRef сотрудника (lecm-orgstr:employee)
	 * @return true если сотрудник имеет роль "Технолог календарей".
	 */
	public boolean isEngineer(NodeRef nodeRef);
}
