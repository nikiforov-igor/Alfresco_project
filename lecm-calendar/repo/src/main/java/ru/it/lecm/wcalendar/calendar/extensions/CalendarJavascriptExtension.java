package ru.it.lecm.wcalendar.calendar.extensions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.calendar.ICalendar;
import ru.it.lecm.wcalendar.extensions.CommonWCalendarJavascriptExtension;

/**
 *
 * @author vlevin
 */
public class CalendarJavascriptExtension extends CommonWCalendarJavascriptExtension {

	private ICalendar WCalendarService;
	private final static Logger logger = LoggerFactory.getLogger(CalendarJavascriptExtension.class);
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public void setWCalendarService(ICalendar WCalendarService) {
		this.WCalendarService = WCalendarService;
	}

	/**
	 * Проверка календаря на существование. Игнорирует lecm-dic:active. Если
	 * календарь выключен, он считается существующим. Поиск происходит в
	 * контейнере для календарей по умолчанию.
	 *
	 * @param yearToExamine год, существование календаря на который нужно
	 * проверить.
	 * @return true, если календарь существует. false в противном случае.
	 */
	public boolean isCalendarExists(int yearToExamine) {
		return WCalendarService.isCalendarExists(yearToExamine);
	}

	/**
	 * Получить NodeRef объекта типа "календарь" по году.
	 *
	 * @param year интересующий год
	 * @return NodeRef календаря. Если календаря на искомый год нет, то null.
	 */
	public ScriptNode getCalendarByYear(int year) {
		NodeRef calendar = WCalendarService.getCalendarByYear(year);
		if (calendar == null) {
			return null;
		}
		return new ScriptNode(calendar, serviceRegistry);
	}

	/**
	 * Является ли дата рабочим днем с учетом праздиков и рабочих дней,
	 * указанных в календаре. Субботы и воскресенья считаются нерабочими.
	 *
	 * @param day искомая дата.
	 * @return true - рабочий. false - выходной. Календаря на искомую дату нет -
	 * null.
	 */
	public Boolean isWorkingDay(Date day) {
		return WCalendarService.isWorkingDay(day);
	}

	/**
	 * Получить год, к которому привязал объект календаря.
	 *
	 * @param node NodeRef на календарь.
	 * @return год календаря
	 */
	public int getCalendarYear(ScriptNode node) {
		return WCalendarService.getCalendarYear(node.getNodeRef());
	}

	/**
	 * Получить год, к которому привязал объект календаря.
	 *
	 * @param nodeStr NodeRef на календарь в виде строки.
	 * @return год календаря
	 */
	public int getCalendarYear(String nodeStr) {
		return WCalendarService.getCalendarYear(new NodeRef(nodeStr));
	}

	/**
	 * Получить список нестандартных рабочих дней года.
	 *
	 * @param year год, дни которого надо получить.
	 * @return массив ScriptNode'ов на рабочие дни. Если календарь на
	 * запрашиваемый год отсутствует, то null.
	 */
	public Scriptable getAllWorkingDaysByYear(int year) {
		List<NodeRef> allWorkingDays = WCalendarService.getAllWorkingDaysByYear(year);
		if (allWorkingDays == null) {
			return null;
		}
		return getAsScriptable(allWorkingDays);
	}

	/**
	 * Получить список нестандартных выходных дней года.
	 *
	 * @param year год, дни которого надо получить.
	 * @return массив ScriptNode'ов на выходные дни. Если календарь на
	 * запрашиваемый год отсутствует, то null.
	 */
	public Scriptable getAllNonWorkingDaysByYear(int year) {
		List<NodeRef> allNonWorkingDays = WCalendarService.getAllNonWorkingDaysByYear(year);
		if (allNonWorkingDays == null) {
			return null;
		}
		return getAsScriptable(allNonWorkingDays);
	}

	/**
	 * Получить дату рабочего или выходного дня.
	 *
	 * @param node NodeRef на рабочий или выходной день.
	 * @return дата дня в виде строки (2013-11-05T15:42:16.451+0500).
	 */
	public String getSpecialDayDate(ScriptNode node) {
		Date date = WCalendarService.getSpecialDayDate(node.getNodeRef());
		return dateFormat.format(date);
	}

	/**
	 * Получить дату рабочего или выходного дня.
	 *
	 * @param nodeStr NodeRef на рабочий или выходной день в виде строки.
	 * @return дата дня в виде строки (2013-11-05T15:42:16.451+0500).
	 */
	public String getSpecialDayDate(String nodeStr) {
		Date date = WCalendarService.getSpecialDayDate(new NodeRef(nodeStr));
		return dateFormat.format(date);
	}
}
