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
	 * Комментарий календаря, lecm-cal:comment
	 */
	QName PROP_CALENDAR_COMMENT = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "comment");
	/**
	 * Тип для объекта Календарь, lecm-cal:calendar
	 */
	QName TYPE_CALENDAR = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "calendar");
	/**
	 * Тип для объекта Особвые дни, от которого наследуются выходные и рабочие,
	 * lecm-cal:special-days
	 */
	QName TYPE_SPECIAL_DAYS = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "special-days");
	/**
	 * Тип для объекта Рабочие дни, lecm-cal:working-days
	 */
	QName TYPE_WORKING_DAYS = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "working-days");
	/**
	 * Тип для объекта Выходные дни, lecm-cal:non-working-days
	 */
	QName TYPE_NON_WORKING_DAYS = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "non-working-days");
	/**
	 * Проперти у Особого дня: причина, lecm-cal:reason
	 */
	QName PROP_SPECIAL_DAY_REASON = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "reason");
	/**
	 * Проперти у Особого дня: дата, lecm-cal:day
	 */
	QName PROP_SPECIAL_DAY_DAY = QName.createQName(IWCalendar.CALENDAR_NAMESPACE, "day");
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

	/**
	 * Добавить запись в бизнес-журнал об операции над календарем. Пока
	 * необходимо писать только про изменение календаря.
	 *
	 * @param node NodeRef на #mainobject (объект календаря)
	 * @param category категория события (EventCategory)
	 */
	public void addBusinessJournalRecord(NodeRef node, String category);
}
