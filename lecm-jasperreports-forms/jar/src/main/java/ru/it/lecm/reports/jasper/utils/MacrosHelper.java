package ru.it.lecm.reports.jasper.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRValueParameter;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;

import ru.it.lecm.reports.utils.Utils;

/**
 * Класс, реализуюший простые макро-подстановки, в первую очередь для списков свойств.
 * Принимается что в определении макро название содержит первым символом: '$name'.
 * Места в которых надо использовать макрос имеют вид "<name>".
 *
 * @author rabdullin
 */
public class MacrosHelper {

    public static final String MACROPREFIX = "$";

    /**
     * Проверить является ли указанное имя названием макро.
     *
     * @param name
     * @return true, если name это название макроса.
     */
    public static boolean isMacroName(final String name) {
        return (name != null) && name.startsWith(MACROPREFIX);
    }


    /**
     * Выделить в имени только название, убрав спец символы макроса.
     * Пара к isMacroName.
     *
     * @param name значение вида "$abc"
     * @return "abc"
     */
    public static String getMacroKey(String name) {
        return (name == null || name.length() < 2) ? name : name.substring(1);
    }


    /**
     * Получить список макросов из указанного списка свойств.
     *
     * @param props
     * @return список макросов, которые описаны в props (если есть в нём такие)
     *         пустой результат воз-сы как Null
     */
    public static Map<String, String> findMacros(final Properties props) {
        if (props == null) {
            return null;
        }

        final Map<String, String> result = new HashMap<String, String>();

        // получение макро-строк
        for (Object keyobj : props.keySet()) {
            final String key = keyobj.toString();
            if (isMacroName(key)) {
                result.put(getMacroKey(key), props.getProperty(key));
            }
        }

        return result.isEmpty() ? null : result;
    }

    /**
     * Выполнить макроподстановки в значение value
     *
     * @param macros
     * @param value
     * @return
     */
    public static String process(Map<String, String> macros, String value) {
        if (macros == null || value == null || (value.indexOf('<') == -1)) {
            return value;
        }

        for (Map.Entry<String, String> e : macros.entrySet()) {
            if (e.getKey() != null) {
                value = value.replaceAll("<" + e.getKey() + ">", e.getValue());
                if (value.indexOf('<') == -1) // больше нет макросов -> конец ц
                    break; // for
            }
        } // for
        return value;
    }


    /**
     * Получить параметры из указанного набора свойств.
     * Выполняется макросподстановка согласно указанному набору макросов.
     * Ключи-макросы пропускаются и в результирующий набор не попадают.
     *
     * @param props
     * @param macros
     * @return набор ключ-значение
     */
    public static Map<String, String> makeParameters(Properties props, Map<String, String> macros) {
        final Map<String, String> result = new HashMap<String, String>();

        for (Object keyobj : props.keySet()) {
            final String key = keyobj.toString();
            if (isMacroName(key)) {
                continue; // skip macro key
            }
            final String value = process(macros, props.getProperty(key, null)); // преобразовать значение с макроподстановками
            result.put(key, value);
        }
        return result;
    }


    /**
     * Получить параметры из указанного набора свойств.
     * Выбираются и используются макросы из props и в результирующий набор не попадают.
     *
     * @param props
     * @return набор ключ-значение
     */
    public static Map<String, String> makeParameters(Properties props) {
        return makeParameters(props, findMacros(props));
    }

    /**
     * Получение значения JR-параметра в виде строки.
     * Праметры типа JRValueParameter отрабатываются через их value-значения.
     *
     * @param jrparam      параметр
     * @param defaultValue значение по-умолчанию
     * @return
     */
    public static String getJRParameterValue(Object jrparam, String defaultValue) {
        String result = null;
        if (jrparam instanceof JRValueParameter) {
            final Object value = ((JRValueParameter) jrparam).getValue();
            if (value != null) {
                result = value.toString();
            }
        }
        if (jrparam instanceof JRParameter) {
            final JRExpression expr = ((JRParameter) jrparam).getDefaultValueExpression();
            if (expr != null) {
                result = expr.getText();
            }
        } else {
            result = jrparam.toString();
        }
        return Utils.dequote((result != null) ? result : defaultValue);
    }

    public static String getJRParameterValue(Object jrparam) {
        return getJRParameterValue(jrparam, null);
    }

    /**
     * Присвоение свойства бина с игнорированием ошибок.
     *
     * @param destBean
     * @param propName название свойства (в том числе вложенного вида "objPropA.propB.propFinal")
     * @param value    присваиваемое значение
     * @param logger   необ параметр для журналирования ошибок
     * @return
     */
    public static boolean safeSetBeanProperty(Object destBean, String propName, Object value, Logger logger) {
        try {
            BeanUtils.setProperty(destBean, propName, value);
            return true; // SUCCESS
        } catch (Throwable ex) {
            final String msg = String.format("Ignoring error setting property\n\t %s::%s = '%s'\n\t Error: %s"
                    , destBean.getClass().getName(), propName, value, ex.getMessage());
            if (logger != null) {
                logger.error(msg, ex);
            }
        }
        return false;
    }
}
