package ru.it.lecm.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Some string utility procs.
 */
public class StrUtils {
	/**
	 * Check if string is empty or null.
	 * @param s
	 * @return true, if string is null or empty.
	 */
	public static boolean isStringEmpty(final String s)
	{
		return (s == null) || (s.length() == 0);
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
			final String strItem = (item != null) ? item.toString() : "" ;

			result.append(quoteOpen).append(strItem).append(quoteClose);
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

	/**
	 * преобразование строки в boolean с учетом различных представлений true 
	 * (без учета регистра и незначащих пробелов в начале и в конце). 
	 * @param value: значение для преобразования.
	 * @param defaultValue: значение, если value==null или пусто.
	 * @return
	 */
	public static boolean stringToBool( String value, final boolean defaultValue)
	{
		if (value != null) value = value.trim();		
		return (value == null || value.length() == 0) 
				? defaultValue 
						: ( 	value.equalsIgnoreCase("true")
								||	value.equalsIgnoreCase("1")
								||	value.equalsIgnoreCase("+")
								||	value.equalsIgnoreCase("y")
								||	value.equalsIgnoreCase("yes")
								||	value.equalsIgnoreCase("д")
								||	value.equalsIgnoreCase("да")
								)
								; 
	}

	/** Вернуть строку в кавычках.
	 * @param st
	 * @param openQuote открывающая кавыка
	 * @param closeQuote закрывающая кавыка
	 * @return
		public static String inQuotes( final String st, final String openQuote, 
				final String closeQuote) {
			return MessageFormat.format( "{0}{1}{2}", new Object[] { openQuote, st, closeQuote } );
		}
	 */

	/**
	 * Вернуть строку в симметричных кавычках.
	 * @see inQuotes
	 * @param s
	 * @param quote
	 * @return
	 */
	public static String inQuotes( final String s, final char quote) {
		// return inQuotes(s, quote, quote);
		if (s == null)
			return null;

		if ( (s.length() >= 1) && (s.charAt(0) == quote) )
			// строка уже обернута
			return s;

		// выполняем обертку...
		final StringBuffer result = new StringBuffer(); // обертка
		result.append(quote); // обертка
		for (int i = 0; i < s.length(); i++)
		{
			char ch = s.charAt(i);
			if (ch == '\\') {	
				// копирование пары символов - '\' и за ним
				result.append(ch);
				if (++i >= s.length()) break; // for i 
				ch = s.charAt(i);
			} else if (ch == quote) {
				// удваиваем эту кавычку ...
				result.append(ch);
			}
			result.append(ch);
		}
		result.append(quote); // обертка
		return result.toString();
	}

	/**
	 * Обернуть строку в двойные кавычки, с корреткной отработкой вложенных
	 * кавычек и пар "\z".
	 * @param s
	 * @return
	 */
	public static String inQuotes(String s) {
		return inQuotes(s, '"');
	}

	/**
	 * Убрать из строки кавычки:
	 * 		- одиночные сиволы quote просто убрать;
	 * 		- парные quote - оставить один quote из пары;
	 * 		- "\zzz" сохранить как есть (т.е. '\<qoute>' сохранится).
	 * 
	 * (!) Первым символом в строке s должна быть кавычка, иначе строка не принимается за обернутую.
	 * 
	 * @param s: строка для удаления кавычек;
	 * @param quote: символ кавычки;
	 * 
	 * @return строка без кавычек.
	 */
	public static String deQuotes(String s, char quote) {
		if (s == null)
			return null;

		if ( (s.length() < 1) || (s.charAt(0) != quote))
			return s;

		final StringBuffer result = new StringBuffer();
		// сразу пропускаем первую кавычку...
		for (int i = 1; i < s.length(); i++)
		{
			char ch = s.charAt(i);
			if (ch == '\\') {	
				// копирование пары символов - '\' и за ним
				result.append(ch);
				if (++i >= s.length()) break; // for i 
				ch = s.charAt(i);
			} else if (ch == quote) {
				// эту кавычку в любом случае пропускаем...
				if (++i >= s.length()) break; // for i 
				// а следующий символ точно надо выводить...
				ch = s.charAt(i);
			}
			result.append(ch);
		}
		return result.toString();
	}

	/**
	 * Убрать двойные кавычки.
	 * @param s
	 * @return
	 */
	public static String deQuotes(String s) {
		return deQuotes( s, '"' );
	}

	public static String dup(String s, int count) {
		final StringBuilder result = new StringBuilder();
		if (s != null && s.length() > 0)
			while (count > 0) { result.append(s); }
		return result.toString();
	}
}
