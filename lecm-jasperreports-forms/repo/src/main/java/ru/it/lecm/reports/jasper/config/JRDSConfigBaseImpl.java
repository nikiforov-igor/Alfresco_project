package ru.it.lecm.reports.jasper.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import ru.it.lecm.reports.api.JRXField;
import ru.it.lecm.reports.jasper.utils.MacrosHelper;

/**
 * Реализация для хранения конфы.
 * 
 * @author rabdullin
 */
public class JRDSConfigBaseImpl implements JRDSConfig {

	// мапер простых конфигурационных параметров
	private Map<String, Object> args;
	// private Map<String, Object> defaults;

	// мапер имя поля в jrxml -> описание поля
	private Map<String, JRXField> metaFields;

	/**
	 * Добавить поле с именем из jr-report файла
	 * @param jrFldName
	 * @return
	 */
	public JRXField addField(String jrFldName) {
		JRXField result =  (this.getArgMetaFeilds().contains(jrFldName))
					? this.getMetaFields().get(jrFldName) : null;
		if (result == null) {
			result = new JRXField();
			result.setName(jrFldName);
			this.getMetaFields().put( jrFldName, result);
		}
		return result;
	}

	public Map<String, JRXField> getMetaFields() {
		if (metaFields == null)
			metaFields = new HashMap<String, JRXField>();
		return metaFields;
	}

	@Override
	public List<? extends JRField> getArgMetaFeilds() {
		return new ArrayList<JRXField>(getMetaFields().values());
	}

	@Override
	public Map<String, Object> getArgs() {
		/* 
		 * такая констуркция сработает даже при вызове виртуальных методов в 
		 * конструкторах в отличии от: 
		 *    final private T args = new TImpl()
		 * , которая будет валиться при вызове вирт методов потомков из
		 * базового коструктора.
		 */
		if (args == null)
			args = new HashMap<String, Object>();
		return args;
	}

	/**
	 * Здесь ожидается наполнение поддерживаемыми именами списка defaults (в принципе и metaFields тоже).
	 * Предоставляем дефолтную рпустую реализацию, чтобы можно было использовать
	 * класс напрямую. 
	 * @param defaults список для задания умолчаний
	 */
	protected void setDefaults(Map<String, Object> defaults) {
		// defaults.put("MY_DATA_NAME", "MyValue");
		// defaults.put("USERNAME", "Guest");
	}

	/**
	 * Загрузить значения параметров, имена которых имеются в args
	 * @param params
	 * @throws JRException
	 */
	public void setArgsByJRParams(Map<String, ?> params)
		throws JRException
	{
		if (params == null || params.isEmpty())
			return;

		for(String name: getArgs().keySet()) {
			// пробуем если есть такой параметр
			if ( params.containsKey(name)) {
				final Object jrparam = params.get(name);
				if (jrparam != null) {
					final String value = MacrosHelper.getJRParameterValue(jrparam); 
					this.args.put(name, value);
				}
			}
		} // for
	}

	/**
	 * Получить указанный аргумент в виде строки
	 * @param argName
	 * @param defaultInt значение по-умолчанию, когда нет аргумента argName или 
	 * он типа список/коллекция
	 * @return
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
	 * @param argName
	 * @param defaultInt значение по-умолчанию, когда нет аргумента argName
	 * @return
	 */
	public int getint(final String argName, final int defaultInt) {
		if (getArgs().containsKey(argName)) {
			final Object value = getArgs().get(argName);
			if (value != null && (value instanceof String) && ((String)value).length() > 0) {
				return Integer.parseInt( (String) value);
			}
		}
		return defaultInt;
	}

	/**
	 * Получить указанный аргумент в виде списка
	 * @param argName
	 * @param defaultList значение по-умолчанию, когда нет аргумента argName
	 * @return список или defaultList, если нет такого или он другого типа
	 */
	@SuppressWarnings({ "rawtypes" })
	public List<?> getList(final String argName, final List<?> defaultList) {
		if (getArgs().containsKey(argName)) {
			final Object value = getArgs().get(argName);
			// if (value instanceof List)
			return (List) value;
		}
		return defaultList;
	}

	/**
	 * Получить указанный аргумент в виде списка
	 * @param argName
	 * @return список или Null, если нет такого или он другого типа
	 */
	@SuppressWarnings("rawtypes")
	public List getList(final String argName) {
		return getList(argName, null);
	}

	/**
	 * Получить указанный аргумент в виде мапы
	 * @param argName
	 * @param defaultMap значение по-умолчанию, когда нет аргумента argName
	 * @return список или defaultMap, если нет такого или он другого типа
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getMap(final String argName, final Map<String, Object> defaultMap) {
		if (getArgs().containsKey(argName)) {
			final Object value = getArgs().get(argName);
			// if (value instanceof Map)
			return (Map) value;
		}
		return defaultMap;
	}

	public Map<String, Object> getMap(final String argName) {
		return getMap( argName, null);
	}
}

