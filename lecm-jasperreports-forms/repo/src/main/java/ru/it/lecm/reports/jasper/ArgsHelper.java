package ru.it.lecm.reports.jasper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.jasper.utils.Utils;

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

	public static NodeRef makeNodeRef(String value, String info) {
		if (Utils.isStringEmpty(value))
			return null;

		NodeRef result;
		try {
			result = new NodeRef(value);
		} catch (Throwable e) {
			logger.error( String.format( "unexpected nodeRef value '%s' for field '%s' -> ignored as NULL", value, info), e);
			result = null;
		}
		return result;
	}

}
