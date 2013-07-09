package ru.it.lecm.reports.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArgsHelper {

	private static final Logger logger = LoggerFactory.getLogger(ArgsHelper.class);

	// org.alfresco.util.ISO8601DateFormat;
	final static String DATE_FMTISO8601 = "yyyy-MM-dd'T'HH:mm";
	final static SimpleDateFormat DateFormatISO8601 = new SimpleDateFormat(DATE_FMTISO8601);

	final static String DATE_FMTWEBSCRIPT = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; // like "2013-04-03T00:00:00.000GMT+06:00"
	final static SimpleDateFormat DateFormatWebScript = new SimpleDateFormat(DATE_FMTWEBSCRIPT);

	final static SimpleDateFormat[] FORMATS = { DateFormatISO8601, DateFormatWebScript}; 

	public static Date makeDate(final String value, String info) {
		if (Utils.isStringEmpty(value))
			return null;

		// пробуем разные форматы ...
		for (SimpleDateFormat fmt: FORMATS) {
			try {
				final Date result = fmt.parse(value);
				return result;
			} catch (ParseException e) {
				// (!) Ignore -> try next ... 
			}
		}

		// ни один формат не подходит -> ошибку журналируем ...
		logger.error( String.format( "unexpected date value '%s' for field '%s' has unsupported format among [%s] -> ignored as NULL", 
				value, info, Utils.getAsString(FORMATS) ));

		return null;
	}

	public static String dateToStr( final Date value, final String ifNULL) {
		return (value != null) ? DateFormatISO8601.format(value) : ifNULL;
	}

	/**
	 * 
	 * @param value значение NodeRef
	 * @param destInfoTag целевое название присваиваемого объекта, наример, "id котректа" или "contragent"
	 * @return сформированный NodeRef или null если строка пуста
	 */
	public static NodeRef makeNodeRef(String value, String destInfoTag) {
		if (Utils.isStringEmpty(value) || value.trim().length() == 0)
			return null;

		NodeRef result;
		try {
			result = new NodeRef(value.trim());
		} catch (Throwable e) {
			logger.error( String.format( "unexpected nodeRef value '%s' for field '%s' -> ignored as NULL", value, destInfoTag), e);
			result = null;
		}
		return result;
	}

	/**
	 * 
	 * @param value
	 * @param destInfoTag
	 * @return непустой список [NodeRef] или null, если строка пуста
	 */
	public static List<NodeRef> makeNodeRefs(String value, String destInfoTag) {
		if (Utils.isStringEmpty(value))
			return null;

		final List<NodeRef> result = new ArrayList<NodeRef>();
		try {
			final String[] items = value.split("[;,]");
			if (items != null) {
				int i = -1;
				for(String s: items) {
					i++;
					final NodeRef ref = makeNodeRef( s, String.format( "%s.newItem[%d]", destInfoTag, i));
					if (ref != null)
						result.add(ref);
				}
			}
		} catch (Throwable e) {
			logger.error( String.format( "Invalid nodeRef values '%s' for field '%s' -> ignored as NULL list", value, destInfoTag), e);
			return null;
		}
		return result.isEmpty() ? null : result;
	}

	/**
	 * Найти в списке аргументов (обычно для request-запроса) указанное значение,
	 * а если его нет или он пустой, то использовать значение по-умолчанию.
	 * @param args карта аргументов
	 * @param argName название искомого аргумента
	 * @param ifDefault значение по-умолчанию
	 * @return
	 */
	static public String[] findArgs( final Map<String, String[]> args, String argName
			, String[] ifDefault) 
	{
		if (args.containsKey(argName)) {
			final String[] values = args.get(argName);
			if (values != null && values.length > 0)
				return values; // ret found
		}
		return ifDefault; // use default
	}

	/**
	 * Найти в списке аргументов (обычно для request-запроса) указанное значение,
	 * а если его нет или он пустой, то использовать значение по-умолчанию.
	 * @param args карта аргументов
	 * @param argName название искомого аргумента
	 * @param ifDefault значение по-умолчанию
	 * @return
	 */
	static public String findArg( final Map<String, String[]> args, String argName
			, String ifDefault) 
	{
		final String[] values = findArgs(args, argName, null);
		return (values != null) ? values[0] : ifDefault;
	}

}
