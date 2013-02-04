package ru.it.lecm.wcalendar;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface IWCalendar {
	// Задаем namespace-ы из моделей данных

	/**
	 * Namespace: lecm-absence, absence-model.xml
	 */
	String ABSENCE_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/absence/1.0";
	/**
	 * Namespace: lecm-cal, calendar-model.xml
	 */
	String CALENDAR_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/calendar/1.0";
	/**
	 * Namespace: lecm-shed, shedule-model.xml
	 */
	String SHEDULE_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/shedule/1.0";
	/**
	 * Namespace: lecm-wcal, wcal-common.xml
	 */
	String WCAL_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/1.0";

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
	IWCalendar getWCalendarDescriptor();

	/**
	 * Получение правильно оформленного типа данных для календаря, графика
	 * работы или отсутсвия.
	 *
	 * @return qualified name типа данных с namespace-ом
	 */
	QName getWCalendarItemType();
}
