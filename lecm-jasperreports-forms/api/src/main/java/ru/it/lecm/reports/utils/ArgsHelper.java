package ru.it.lecm.reports.utils;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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

    public static String dateToStr(final Date value, final String ifNULL) {
        return (value != null) ? DateFormatISO8601.format(value) : ifNULL;
    }

    /**
     * @param value       значение NodeRef
     * @return сформированный NodeRef или null если строка пуста
     */
    public static NodeRef makeNodeRef(String value) {
        if (Utils.isStringEmpty(value) || value.trim().length() == 0) {
            return null;
        }

        NodeRef result;
        try {
            result = new NodeRef(value.trim());
        } catch (Throwable e) {
            logger.error(String.format("unexpected nodeRef value '%s' -> ignored as NULL", value), e);
            result = null;
        }
        return result;
    }

    /**
     * По строковому списку id узлов получить список NodeRef.
     * Разделители в списке запятая или точка с запятой.
     *
     * @return непустой список [NodeRef] или null, если строка пуста
     */
    public static List<NodeRef> makeNodeRefs(String value, String destInfoTag) {
        if (Utils.isStringEmpty(value)) {
            return null;
        }

        final List<NodeRef> result = new ArrayList<NodeRef>();
        try {
            final String[] items = value.split("[;,]");
            if (items != null) {
                int i = -1;
                for (String s : items) {
                    i++;
                    final NodeRef ref = makeNodeRef(s);
                    if (ref != null) {
                        result.add(ref);
                    }
                }
            }
        } catch (Throwable e) {
            logger.error(String.format("Invalid nodeRef values '%s' for field '%s' -> ignored as NULL list", value, destInfoTag), e);
            return null;
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * Найти в списке аргументов (обычно для request-запроса) указанное значение,
     * а если его нет или он пустой, то использовать значение по-умолчанию.
     *
     * @param args      карта аргументов
     * @param argName   название искомого аргумента
     * @param ifDefault значение по-умолчанию
     */
    static public String[] findArgs(final Map<String, String[]> args, String argName, String[] ifDefault) {
        if (args.containsKey(argName)) {
            final String[] values = args.get(argName);
            if (values != null && values.length > 0) {
                return values; // ret found
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
    static public String findArg(final Map<String, String[]> args, String argName, String ifDefault) {
        final String[] values = findArgs(args, argName, null);
        return (values != null) ? values[0] : ifDefault;
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
                                        final Map<String, String[]> propertiesAliases,
                                        final Map<String, String[]> srcParameters
    ) throws IllegalAccessException, InvocationTargetException {
        if (destBean == null
                || propertiesAliases == null || propertiesAliases.isEmpty()
                || srcParameters == null || srcParameters.isEmpty()
                )
            return;

        // строим карту реально имеющихся параметров ...
        final Map<String, String[]> result = new HashMap<String, String[]>();

        // проход по списку синонимов ...
        for (Map.Entry<String, String[]> epropAlias : propertiesAliases.entrySet()) {
            final String beanPropName = epropAlias.getKey();
            final String[] aliases = epropAlias.getValue();
            if (aliases == null || aliases.length == 0) {
                // пропускаем пустые ...
                continue;
            }

            // до первого совпадения по имени в параметрах ...
            for (String name : aliases) {
                if (srcParameters.containsKey(name)) { // найден синоним среди параметров ...
                    result.put(beanPropName, srcParameters.get(name));
                    break;
                }
            } // for name

        } // for epropAlias

        logger.info(String.format("assigning alias list:\n\t%s", result));

        BeanUtils.populate(destBean, result);
    }
}
