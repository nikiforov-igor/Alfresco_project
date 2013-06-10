package ru.it.lecm.reports.jasper.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.service.namespace.QName;

import ru.it.lecm.reports.jasper.ArgsHelper;

public class Utils {

	private Utils() {
	}

	/**
	 * Check if string is empty or null.
	 * @param s
	 * @return true, if string is null or empty.
	 */
	public static boolean isStringEmpty(final String s)
	{
		return (s == null) || (s.length() == 0);
	}

	final public static char QUOTE = '\"';
	static String quoted( final String s) {
		return QUOTE + s+ QUOTE;
	}

	/**
	 * Get value, replacing empty one it by default value.
	 * @param obj
	 * @param defIfEmpty
	 * @return @param(obj) if it is not empty, otherwise @param(defIfEmpty).
	 */
	public static String nvl( Object obj, String defIfEmpty)
	{
		return coalesce( obj, defIfEmpty);
	}

	/**
	 * Вернуть первое непустое строковое представление элементов списка.
	 * @param values
	 * @return не-null строку, если в списке values нашёлся объект такой что
	 * .toString() != null и null если таких объектов нет.
	 */
	public static String coalesce( Object ... values)
	{
		if (values != null)
			for(final Object obj: values) {
				if (obj != null) { 
					final String val = obj.toString();
					if (val != null) 
						return val; // FOUND NON-NULL value
				}
			}
		return null; // all vales are null
	}

	/**
	 * Журналирование данных.
	 * @param dest целевой буфер для вывода
	 * @param props список свойств для журналирования
	 * @param info сообщение, выводится в журнал если не null
	 */
	public static StringBuilder dumpAlfData( final StringBuilder dest, final Map<QName, Serializable> props, final String info) {
		final StringBuilder result = (dest != null) ? dest : new StringBuilder();
		if (info != null)
			result.append(info);
		result.append("\n");
		if (props != null) {
			result.append( String.format( "\t[%s]\t %25s\t %s\n", 'n', "fldName", "value"));
			int i = 0;
			for (Map.Entry<QName, Serializable> e: props.entrySet()) {
				i++;
				result.append( String.format( "\t[%d]\t %25s\t '%s'\n", i, e.getKey(), nvl(e.getValue(), "NULL")));
			}
		}
		return result;
	}

	public static StringBuilder dumpAlfData( final Map<QName, Serializable> props, final String info) {
		return dumpAlfData( new StringBuilder(), props, info);
	}


	/**
	 * Make string enumeration of the items as list with delimiters.  
	 * @param col
	 * @param delimiter
	 * @param quoteOpen открывающая кавычка.
	 * @param quoteClose закрывающая кавычка.
	 * @return
	 */
	public static String getAsString( final Collection<?> col, 
			final String delimiter, String quoteOpen, String quoteClose)
	{
		if (col == null)
			return null;
		if (quoteOpen == null) quoteOpen = "";
		if (quoteClose == null) quoteClose = "";
		final StringBuffer result = new StringBuffer(5);
		final Iterator<?> itr = col.iterator();
		// final String fmtStr = (isStringEmpty(quote)) ? "{1}" : "{0}{1}{2}";
		while (itr.hasNext()) {
			final Object item = itr.next();

			// if (item instanceof XXXObject) strItem = ((XXXObject) item).getId().getId().toString();
			// else
			final String strItem = (item != null) 
					? quoteOpen + item.toString() + quoteClose
					: "NULL" ;

			result.append(strItem);
			if (delimiter != null && itr.hasNext()) {
				result.append(delimiter);
			}
		}
		return result.toString();
	}

	/**
	 * Make string enumeration of the items as list with delimiters.  
	 * @param col
	 * @param delimiter
	 * @param quote ограничители-кавычки отдельных элементов.
	 * @return
	 */
	public static String getAsString( final Collection<?> col, 
			final String delimiter, final String quote) {
		return getAsString( col, delimiter, quote, quote);
	}

	/**
	 * Вернуть список с разделителем без ограничителей-кавычек.
	 * @param coll
	 * @param delimiter
	 * @return
	 */
	public static String getAsString(Collection<?> col, String delimiter) {
		return getAsString( col, delimiter, null);
	}

	/**
	 * Вернуть список с разделителем запятая.
	 * @param coll
	 * @return
	 */
	public static String getAsString(Collection<?> col) {
		return getAsString( col, ", ");
	}

	public static String getAsString(Object[] args) {
		return (args == null) ? "NULL" : getAsString( Arrays.asList(args), ", ");
	}

	/**
	 * Сформировать lucene-style проверку попадания поля даты в указанный интервал.
	 * Формируется условие вида " @fld:[ x TO y]"
	 * Если обе даты пустые - ничего не формируется
	 * @param fldName (!) экранированное имя поля, (!) без символа '@' в начале
	 * @param from дата начала
	 * @param upto дата конца
	 * @return условие проверки вхождения даты в диапазон или NULL, если обе даты NULL
	 */
	public static String emmitDateIntervalCheck( String fldName, Date from, Date upto) {
		final boolean needEmmition = (from != null || upto !=  null);
		if (!needEmmition)
			return null;
		// add " ... [X TO Y]"
		final String stMIN = ArgsHelper.dateToStr( from, "MIN");
		final String stMAX = ArgsHelper.dateToStr( upto, "MAX");
		return " @"+ fldName+ ":[" + stMIN + " TO "+ stMAX+ "]";
	}

	/**
	 * Сформировать lucene-style проверку попадания поля числа в указанный интервал.
	 * Формируется условие вида " @fld:[ x TO y]"
	 * Если обе границы пустые - ничего не формируется
	 * @param fldName (!) экранированное имя поля, (!) без символа '@' в начале
	 * @param from числовая границы слева
	 * @param upto числовая границы справа
	 * @return условие проверки вхождения числа в диапазон или NULL, если обе границы NULL
	 */
	public static String emmitNumericIntervalCheck( String fldName, Double from, Double upto) {
		final boolean needEmmition = (from != null || upto !=  null);
		if (!needEmmition)
			return null;
		// add " ... [X TO Y]"
		final String stMIN = (from != null) ? String.format( "%f", from) : "MIN";
		final String stMAX = (upto != null) ? String.format( "%f", upto) : "MAX";
		return " @"+ fldName+ ":[" + stMIN + " TO "+ stMAX+ "]";
	}


	final public static int MILLIS_PER_DAY = 86400000;

	/**
	 * Вычислить длительность в днях между парой дат
	 * @param startAt время начала
	 * @param endAt время окончания
	 * @param defaultValue значение по-умолчанию
	 * @return разницу в днях (возможно нецелое значение) между двумя датами;
	 * значение по-умолчанию воз-ся если одна из дат или обе null. 
	 */
	final public static float calcDurationInDays(Date startAt, Date endAt, float defaultValue) {
		if (startAt == null || endAt == null)
			return defaultValue;
		final double duration_ms = (endAt.getTime() - startAt.getTime());
		return (float) (duration_ms / MILLIS_PER_DAY);
	}

}
