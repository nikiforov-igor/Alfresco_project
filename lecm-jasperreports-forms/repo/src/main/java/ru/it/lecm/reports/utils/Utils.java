package ru.it.lecm.reports.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.jasper.utils.ArgsHelper;


public class Utils {

	final static Logger logger = LoggerFactory.getLogger(Utils.class);

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
	public static String quoted( final String s) {
		return QUOTE + s+ QUOTE;
	}

	public static String dequote( String s) {
		if (s == null || s.length() <= 2) return null;
		final int b = (s.charAt(0) == '"') ? 1 : 0;
		int e = s.length();
		if (s.charAt(e-1) == QUOTE) e--; 
		return s.substring(b, e);
	}

	/**
	 * Экранировка символов [':', '-'] в указанной строке символом '\' для Lucene-строк
	 * @return
	 */
	public static String luceneEncode(String s) {
		return doCharsProtection( s, ":-");
	}

	private static final char CH_WALL = '\\'; // символ экранировки

	/**
	 * Экранировка указанных символов в строке символом '\'
	 * @param s экранируемая строка
	 * @param chars символы, которые подлежат экранировке
	 * @return
	 */
	public static String doCharsProtection(String s, String chars) {
		if (isStringEmpty(s) || isStringEmpty(chars))
			return s;
		final StringBuilder result = new StringBuilder();
		for(int i = 0; i < s.length(); i++) {
			final char ch = s.charAt(i);
			if (	CH_WALL == ch   /* сам символ "экрана" тоже надо экранировать */
					|| chars.indexOf(ch) >= 0
				)// надо экранировку
				result.append(CH_WALL);
			result.append(ch); // сам символ
		}
		return result.toString();
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
	 * null-safe проверка объектов на равенство, при этом принимается null == null.
	 * @param a
	 * @param b
	 * @return true если объекты равны или оба одновременно null, иначе false
	 */
	public static boolean isSafelyEquals(Object a, Object b) {
		return (a == b) ? true : ((a == null) ? (b == null) : a.equals(b));
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

		// если даты не по-порядку - поменяем их местами
		if (from != null && upto !=  null) {
			if (from.after(upto)) {
				final Date temp = from;
				from = upto;
				upto = temp;
			}
		}

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
	public static String emmitNumericIntervalCheck( String fldName, Number from, Number upto) {
		final boolean needEmmition = (from != null || upto !=  null);
		if (!needEmmition)
			return null;
		// если даты не по-порядку - поменяем их местами
		if (from != null && upto !=  null) {
			if (from.doubleValue() > upto.doubleValue()) {
				final Number temp = from;
				from = upto;
				upto = temp;
			}
		}

		// add " ... [X TO Y]"
		//  используем формат без разделителя, чтобы нормально выполнялся строковый поиск ...
		final String stMIN = (from != null) ? String.format( "%12.0f", from.doubleValue()) : "MIN";
		final String stMAX = (upto != null) ? String.format( "%12.0f", upto.doubleValue()) : "MAX";
		return " @"+ fldName+ ":[" + stMIN + " TO "+ stMAX+ "]";
	}


	/**
	 * Сгенерировать условие для единичного параметра (кавыки " добавляются здесь).
	 * Если параметр не задан (null) - поднимается исключение с сообщением errMsg, если raiseIfNull=true.
	 * @param bquery текст запроса для добавления
	 * @param column
	 * @param prefix текст перед добавляемым условием
	 * @param errMsg сообщение, выводимое при пустом параметре и raiseIfNull = true
	 * @param raiseIfNull true, чтобы поднять исключение когда параметр не задан
	 * @return true, если условия по параметру было добавлено 
	 * и false, когда raiseIfNull = false или исключение иначе
	 */
	public static boolean emmitParamCondition(final StringBuilder bquery
			, ColumnDescriptor column
			, final String prefix
			, final String errMsg
			, boolean raiseIfNull
			) {
		if (column == null || column.getParameterValue().getBound1() == null) {
			if (raiseIfNull)
				throw new RuntimeException(errMsg);
			return false;
		}
		bquery.append( prefix+ Utils.quoted(column.getParameterValue().getBound1().toString()));
		return true;
	}


	/**
	 * Сгенерировать условие для единичного параметра (кавыки " добавляются здесь).
	 * Если параметр не задан ничего не добавляется.
	 * @param bquery текст запроса для добавления
	 * @param column
	 * @param prefix текст перед добавляемым условием
	 * @return true, если условия по параметру было добавлено и false иначе
	 */
	public static boolean emmitParamCondition(final StringBuilder bquery
			, ColumnDescriptor column
			, final String prefix
			) {
		return emmitParamCondition(bquery, column, prefix, null, false);
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

	/**
	 * По  названию класса вернуть известный класс или defaultClass.
	 * @param vClazzOrAlias проверяемое название класса - полное имя типа или алиас
	 * @return 
	 */
	public static Class<?> getJavaClassByName( final String vClazz, final Class<?> defaultClass)
	{
		Class<?> byAlias = findKnownType(vClazz);
		if (byAlias != null)
			return byAlias;

		// не найдено синононима -> ищем по полному имени
		if (vClazz != null) {
			try {
				return Class.forName(vClazz);
			} catch (ClassNotFoundException ex) {
				// ignore class name fail by default
				logger.warn( String.format( "Unknown java class '%s' -> used as '%s'", vClazz, defaultClass));
			}
		}

		return defaultClass;
	}

	// для возможности задавать типы алиасами
	private static Map<String, Class<?>> knownTypes = null;

	/**
	 * По короткому названию класса вернуть известный класс или null, если такого не зарегистрировано.
	 * @param vClazzAlias проверяемый алиас класса
	 * @return 
	 */
	private static Class<?> findKnownType(String vClazzAlias) {
		if (vClazzAlias == null)
			return null;

		if (knownTypes == null) {
			knownTypes = new HashMap<String, Class<?>>();

			// TODO: (RUSA) вынести всё это в бины

			knownTypes.put("integer", Integer.class);
			knownTypes.put("int", Integer.class);

			knownTypes.put("bool", Boolean.class);
			knownTypes.put("boolean", Boolean.class);
			// knownTypes.put("yesno", Boolean.class);
			// knownTypes.put("logical", Boolean.class);

			knownTypes.put("long", Long.class);
			knownTypes.put("longint", Long.class);

			knownTypes.put("id", String.class);
			knownTypes.put("string", String.class);

			knownTypes.put("date", Date.class);

			knownTypes.put("numeric", Number.class);
			knownTypes.put("number", Number.class);
			knownTypes.put("float", Float.class);
			knownTypes.put("double", Double.class);
		}

		final String skey = vClazzAlias.toLowerCase();
		return (knownTypes.containsKey(skey)) ? knownTypes.get(skey) : null;
	}

}
