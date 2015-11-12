package ru.it.lecm.wcalendar.calendar;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.wcalendar.ICommonWCalendar;

import java.util.Date;
import java.util.List;

/**
 *
 * @author vlevin
 */
public interface ICalendar extends ICommonWCalendar {

	/**
	 * Имя для контейнера, в котором хранятся календари
	 */
	String CONTAINER_NAME = "WCalContainer";
	/**
	 * Год календаря, lecm-cal:year
	 */
	QName PROP_CALENDAR_YEAR = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "year");
	/**
	 * Комментарий календаря, lecm-cal:comment
	 */
	QName PROP_CALENDAR_COMMENT = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "comment");
	/**
	 * Тип для объекта Календарь, lecm-cal:calendar
	 */
	QName TYPE_CALENDAR = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "calendar");
	/**
	 * Тип для объекта Особвые дни, от которого наследуются выходные и рабочие,
	 * lecm-cal:special-days
	 */
	QName TYPE_SPECIAL_DAYS = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "special-days");
	/**
	 * Тип для объекта Рабочие дни, lecm-cal:working-days
	 */
	QName TYPE_WORKING_DAYS = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "working-days");
	/**
	 * Тип для объекта Выходные дни, lecm-cal:non-working-days
	 */
	QName TYPE_NON_WORKING_DAYS = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "non-working-days");
	/**
	 * Проперти у Особого дня: причина, lecm-cal:reason
	 */
	QName PROP_SPECIAL_DAY_REASON = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "reason");
	/**
	 * Проперти у Особого дня: дата, lecm-cal:day
	 */
	QName PROP_SPECIAL_DAY_DAY = QName.createQName(ICommonWCalendar.CALENDAR_NAMESPACE, "day");
	/**
	 * Корневой контейнер для календарей, lecm-wcal:wcal-container
	 */
	QName TYPE_WCAL_CONTAINER = QName.createQName(ICommonWCalendar.WCAL_NAMESPACE, "wcal-container");

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
	 * Добавить запись в бизнес-журнал об операции над календарем. Пока
	 * необходимо писать только про изменение календаря.
	 *
	 * @param node NodeRef на #mainobject (объект календаря)
	 * @param category категория события (EventCategory)
	 */
	void addBusinessJournalRecord(NodeRef node, String category);

    Boolean isWorkingDay(Date day, List<NodeRef> allWorkingDaysByYear, List<NodeRef> allNonWorkingDaysByYear);

    /**
	 * Получить NodeRef объекта типа "календарь" по году.
	 *
	 * @param year интересующий год
	 * @return NodeRef календаря. Если календаря на искомый год нет, то null.
	 */
	NodeRef getCalendarByYear(int year);

	/**
	 * Является ли дата рабочим днем с учетом праздиков и рабочих дней,
	 * указанных в календаре. Субботы и воскресенья считаются нерабочими.
	 *
	 * @param day искомая дата.
	 * @return true - рабочий. false - выходной. Календаря на искомую дату нет -
	 * null.
	 */
	Boolean isWorkingDay(Date day);

	/**
	 * Получить год, к которому привязал объект календаря.
	 *
	 * @param node NodeRef на календарь.
	 * @return год календаря
	 */
	int getCalendarYear(NodeRef node);

	/**
	 * Получить список рабочих или выходных дней календаря.
	 *
	 * @param calendar NodeRef на календарь.
	 * @param dayType тип необходимых дней: рабочие или выходные.
	 * @return список NodeRef'ов на дни.
	 */
	List<NodeRef> getDaysInCalendarByType(NodeRef calendar, QName dayType);

	/**
	 * Получить список нестандартных рабочих дней года.
	 *
	 * @param year год, дни которого надо получить.
	 * @return список NodeRef'ов на рабочие дни. Если календарь на запрашиваемый
	 * год отсутствует, то null.
	 */
	List<NodeRef> getAllWorkingDaysByYear(int year);

	/**
	 * Получить список нестандартных выходных дней года.
	 *
	 * @param year год, дни которого надо получить.
	 * @return список NodeRef'ов на выходные дни. Если календарь на
	 * запрашиваемый год отсутствует, то null.
	 */
	List<NodeRef> getAllNonWorkingDaysByYear(int year);

	/**
	 * Получить дату рабочего или выходного дня.
	 *
	 * @param node NodeRef на рабочий или выходной день.
	 * @return дата дня.
	 */
	Date getSpecialDayDate(NodeRef node);
}
