package ru.it.lecm.reports.jasper.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.service.namespace.QName;

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
	}
