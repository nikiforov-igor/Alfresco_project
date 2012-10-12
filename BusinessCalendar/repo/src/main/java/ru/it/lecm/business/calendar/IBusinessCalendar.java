package ru.it.lecm.business.calendar;

import java.util.Date;

import org.json.JSONArray;

/**
 * ИНтерфейс бизнес-календаря
 * @author rabdullin
 */
public interface IBusinessCalendar {

	/**
	 * Получить указанный календарь или весь список календарей
	 * 
	 * @param calendarId id календаря, значения null или пусто используются для 
	 * получения всего списка
	 * @return список с одним календарём (если calendarId!=null) или со всеми
	 */
	JSONArray getCalendar(String calendarId);

	/**
	 * Создание нового календаря
	 * @param data
	 * @return id созданного узла
	 */
	String createCalendar(JSONArray data);

	/**
	 * Обновление данных указанного календаря
	 * @param calendarId
	 * @param data
	 */
	void updateCalendar(String calendarId, JSONArray data);

	/**
	 * Удаление указанного календаря
	 * @param calendarId
	 */
	void deleteCalendar(String calendarId);

	/**
	 * Получить события календаря в указанном интервале дат
	 * @param calendarId id календаря
	 * @param dateFrom начальная дата, Null если не гораничивается
	 * @param dateTo конечная дата, Null если не гораничивается
	 * @return
	 */
	JSONArray getEvents(String calendarId, Date dateFrom, Date dateTo);

	/**
	 * Создание нового событиz календаря
	 * @param calendarId id календаря, в котором будет создаваться событие
	 * @param data
	 * @return id созданного узла
	 */
	String createEvent(String calendarId, JSONArray data);

	/**
	 * Обновление данных указанного события
	 * @param eventId
	 * @param data
	 */
	void updateEvent(String eventId, JSONArray data);

	/**
	 * Удалить указанное событие
	 * @param eventId
	 */
	void deleteEvent(String eventId);

}
