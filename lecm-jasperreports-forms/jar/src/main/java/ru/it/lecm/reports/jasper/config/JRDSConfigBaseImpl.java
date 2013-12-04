package ru.it.lecm.reports.jasper.config;

import net.sf.jasperreports.engine.*;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.utils.Utils;

import java.util.*;

/**
 * Реализация для хранения параметров в виде конфы (см args).
 *
 * @author rabdullin
 */
public class JRDSConfigBaseImpl implements JRDSConfig {

    // мапер простых конфигурационных параметров
    private Map<String, Object> args;

    // мапер имя поля в jrxml -> описание поля
    private Map<String, DataFieldColumn> metaFields;

    public void clear() {
        this.args = null;
        this.metaFields = null;
    }

    /**
     * Добавить поле с именем из jr-report файла
     *
     */
    public DataFieldColumn addField(String jrFldName) {
        DataFieldColumn result = (this.getArgMetaFeilds().contains(jrFldName))
                ? this.getMetaFields().get(jrFldName) : null;
        if (result == null) {
            result = new DataFieldColumn();
            result.setName(jrFldName);
            this.getMetaFields().put(jrFldName, result);
        }
        return result;
    }

    /**
     * Добавить указанное поле
     *
     */
    public void addField(DataFieldColumn jrFld) {
        if (jrFld != null) {
            this.getMetaFields().put(jrFld.getName(), jrFld);
        }
    }

    public Map<String, DataFieldColumn> getMetaFields() {
        if (metaFields == null) {
            metaFields = new HashMap<String, DataFieldColumn>();
        }
        return metaFields;
    }

    @Override
    public List<? extends JRField> getArgMetaFeilds() {
        return new ArrayList<DataFieldColumn>(getMetaFields().values());
    }

    @Override
    public Map<String, Object> getArgs() {
        /*
         * такая конструкция сработает даже при вызове виртуальных методов в
		 * конструкторах в отличии от: 
		 *    final private T args = new TImpl()
		 * , которая будет валиться при вызове вирт методов потомков из
		 * базового коструктора.
		 */
        if (args == null) {
            args = new HashMap<String, Object>();
        }
        return args;
    }

    /**
     * Здесь ожидается наполнение поддерживаемыми именами списка defaults (в принципе и metaFields тоже).
     * Предоставляем дефолтную пустую реализацию, чтобы можно было использовать
     * класс напрямую.
     *
     * @param destDefaults список для задания в нём умолчаний
     */
    protected void setDefaults(Map<String, Object> destDefaults) {
    }

    /**
     * Загрузить значения параметров, имена которых имеются в args
     *
     * @throws JRException
     */
    public void setArgsByJRParams(Map<String, ?> params) throws JRException {
        if (params == null || params.isEmpty()) {
            return;
        }

        for (String name : getArgs().keySet()) {
            // пробуем если есть такой параметр
            if (params.containsKey(name)) {
                final Object jrparam = params.get(name);
                if (jrparam != null) {
                    final String value = getJRParameterValue(jrparam);
                    this.args.put(name, value);
                }
            }
        } // for
    }

    /**
     * Получить указанный аргумент в виде строки
     *
     * @param defaultStr значение по-умолчанию, когда нет аргумента argName или
     *                   он типа список/коллекция
     */
    public String getstr(final String argName, final String defaultStr) {
        if (getArgs().containsKey(argName)) {
            final Object value = getArgs().get(argName);
            if (!(value instanceof Collection)) {
                return value == null ? null : value.toString();
            }
        }
        return defaultStr;
    }

    public String getstr(final String argName) {
        return getstr(argName, null);
    }

    /**
     * Получить указанный аргумент в виде целого
     *
     * @param defaultInt значение по-умолчанию, когда нет аргумента argName
     */
    public int getint(final String argName, final int defaultInt) {
        if (getArgs().containsKey(argName)) {
            final Object value = getArgs().get(argName);
            if (value != null && (value instanceof String) && ((String) value).length() > 0) {
                return Integer.parseInt((String) value);
            }
        }
        return defaultInt;
    }

    /**
     * Получить указанный аргумент в виде списка
     *
     * @param defaultList значение по-умолчанию, когда нет аргумента argName
     * @return список или defaultList, если нет такого или он другого типа
     */
    @SuppressWarnings({"rawtypes"})
    public List<?> getList(final String argName, final List<?> defaultList) {
        if (getArgs().containsKey(argName)) {
            final Object value = getArgs().get(argName);
            return (List) value;
        }
        return defaultList;
    }

    /**
     * Получить указанный аргумент в виде списка
     *
     * @return список или Null, если нет такого или он другого типа
     */
    @SuppressWarnings("rawtypes")
    public List getList(final String argName) {
        return getList(argName, null);
    }

    /**
     * Получить указанный аргумент в виде мапы
     *
     * @param defaultMap значение по-умолчанию, когда нет аргумента argName
     * @return список или defaultMap, если нет такого или он другого типа
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, Object> getMap(final String argName, final Map<String, Object> defaultMap) {
        if (getArgs().containsKey(argName)) {
            final Object value = getArgs().get(argName);
            return (Map) value;
        }
        return defaultMap;
    }

    public Map<String, Object> getMap(final String argName) {
        return getMap(argName, null);
    }

    /**
     * Выполнить макроподстановки в значение valuen
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
     * Получение значения JR-параметра в виде строки.
     * Праметры типа JRValueParameter отрабатываются через их value-значения.
     *
     * @param jrparam      параметр
     * @param defaultValue значение по-умолчанию
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
}

