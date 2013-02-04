package ru.it.lecm.wcalendar.calendar;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.wcalendar.IWCalendar;

/**
 *
 * @author vlevin
 */
public interface ICalendar {

	/**
	 * Имя для контейнера, в котором хранятся календари
	 */
	String CONTAINER_NAME = "WCalContainer";
	/**
	 * Год календаря, lecm-cal:year
	 */
	QName PROP_CALENDAR_YEAR = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "year");
	/**
	 * Тип для объекта Календарь, lecm-cal:calendar
	 */
	QName TYPE_CALENDAR = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "calendar");
	/**
	 * Корневой контейнер для календарей, lecm-wcal:wcal-container
	 */
	QName TYPE_WCAL_CONTAINER = QName.createQName(IWCalendar.WCAL_NAMESPACE, "wcal-container");

	/**
	 * Проверка календаря на существование. Игнорирует lecm-dic:active. Если
	 * календарь выключен, он считается существующим. Поиск происходит в
	 * контейнере для календарей по умолчанию.
	 *
	 * @param yearToExamine год, существование календаря на который нужно
	 * проверить.
	 * @return true, если календарь существует. false в противном случае.
	 */
	boolean isCalendarExists(int yearToExamine);

	/**
	 * Проверка календаря на существование. Игнорирует lecm-dic:active. Если
	 * календарь выключен, он считается существующим.
	 *
	 * @param parentNodeRef nodeRef контейнера, в котором лежат календари.
	 * @param yearToExamine год, существование календаря на который нужно
	 * проверить.
	 * @return true, если календарь существует. false в противном случае.
	 */
	boolean isCalendarExists(NodeRef parentNodeRef, int yearToExamine);
}
