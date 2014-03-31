package ru.it.lecm.reports.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ArgsHelper {

    private static final Logger logger = LoggerFactory.getLogger(ArgsHelper.class);

    final static String DATE_FMTISO8601 = "yyyy-MM-dd'T'HH:mm";
    final static SimpleDateFormat DateFormatISO8601 = new SimpleDateFormat(DATE_FMTISO8601);

    final static String DATE_FMTWEBSCRIPT = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; // like "2013-04-03T00:00:00.000GMT+06:00"
    final static SimpleDateFormat DateFormatWebScript = new SimpleDateFormat(DATE_FMTWEBSCRIPT);

    final static String DATE_FMT_YMD_HM = "yyyy-MM-dd HH:mm";
    final static SimpleDateFormat DateFormatYMD_HM = new SimpleDateFormat(DATE_FMT_YMD_HM);

    final static String DATE_FMT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    final static SimpleDateFormat DateFormat_YMD_HMS = new SimpleDateFormat(DATE_FMT_YMD_HMS);

    final static String DATE_FMT_YMD = "yyyy-MM-dd";
    final static SimpleDateFormat DateFormat_YMD = new SimpleDateFormat(DATE_FMT_YMD);

    final static SimpleDateFormat[] FORMATS = {DateFormatISO8601, DateFormatWebScript, DateFormatYMD_HM, DateFormat_YMD_HMS, DateFormat_YMD};

    public static Date tryMakeDate(final String value, String info) {
        if (Utils.isStringEmpty(value)){
            return null;
        }

        // пробуем разные форматы ...
        for (SimpleDateFormat fmt : FORMATS) {
            try {
                return fmt.parse(value);
            } catch (ParseException e) {
                // (!) Ignore -> try next ...
            }
        }

        // ни один формат не подходит -> ошибку журналируем ...
        logger.error(String.format("unexpected date value '%s' for field '%s' has unsupported format among [%s] -> ignored as NULL",
                value, info, Utils.getAsString(FORMATS)));

        return null;
    }

    public static Number tryMakeNumber(String value) {
        if (Utils.isStringEmpty(value)){
            return null;
        }

        value = value.replaceAll(",", ".");
        return value.indexOf(".") > 0 ? Double.parseDouble(value) : Long.parseLong(value);
    }

    public static String dateToStr(final Date value, final String ifNULL) {
        return (value != null) ? DateFormatISO8601.format(value) : ifNULL;
    }

    /**
     * Найти в списке аргументов (обычно для request-запроса) указанное значение,
     * а если его нет или он пустой, то использовать значение по-умолчанию.
     *
     * @param args      карта аргументов
     * @param argName   название искомого аргумента
     * @param ifDefault значение по-умолчанию
     */
    static public String findArgs(final Map<String, Object> args, String argName, String ifDefault) {
        if (args.containsKey(argName)) {
            final String value = String.valueOf(args.get(argName));
            if (value != null) {
                return value; // ret found
            }
        }
        return ifDefault; // use default
    }

    /**
     * Найти в списке аргументов (обычно для request-запроса) указанное значение,
     * а если его нет или он пустой, то использовать значение по-умолчанию.
     *
     * @param args      карта аргументов
     * @param argName   название искомого аргумента
     * @param ifDefault значение по-умолчанию
     */
    static public String findArg(final Map<String, Object> args, String argName, String ifDefault) {
        final String value = findArgs(args, argName, null);
        return (value != null) ? value : ifDefault;
    }

    /**
     * Присвоить свойствам указанного бина значения параметров.
     *
     * @param destBean          целевой бин
     * @param propertiesAliases список ключ=название свойства бина,
     *                          значения = список синонимов имён параметров, которые выбираются из parameters,
     *                          до первого совпадения и затем выполняется присвоение свойству destBean (по ключу).
     * @param srcParameters     исхоный список параметров-данных:
     *                          ключ = название параметра (его синонимы могут быть в значениях propertiesAliases),
     *                          значение = соот-щее значение параметра.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void assignParameters(final Object destBean,
                                        final Map<String, String> propertiesAliases,
                                        final Map<String, Object> srcParameters
    ) throws IllegalAccessException, InvocationTargetException {
        if (destBean == null
                || propertiesAliases == null || propertiesAliases.isEmpty()
                || srcParameters == null || srcParameters.isEmpty()
                )
            return;

        // строим карту реально имеющихся параметров ...
        final Map<String, String> result = new HashMap<String, String>();

        // проход по списку синонимов ...
        for (Map.Entry<String, String> epropAlias : propertiesAliases.entrySet()) {
            final String beanPropName = epropAlias.getKey();
            final String aliases = epropAlias.getValue();
            if (aliases == null) {
                // пропускаем пустые ...
                continue;
            }

            // до первого совпадения по имени в параметрах ...
            for (String name : aliases.split("[,;]")) {
                if (srcParameters.containsKey(name.trim())) { // найден синоним среди параметров ...
                    result.put(beanPropName, String.valueOf(srcParameters.get(name.trim())));
                    break;
                }
            } // for name

        } // for epropAlias

        logger.info(String.format("assigning alias list:\n\t%s", result));

        BeanUtils.populate(destBean, result);
    }
}
