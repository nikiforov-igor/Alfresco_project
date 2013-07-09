package ru.it.lecm.reports.model;

import java.util.HashMap;
import java.util.Map;

import ru.it.lecm.reports.api.model.L18able;

/**
 * Локализованное название.
 * Локали-языки не стандартизируем - просто строковые названия вида "страна-подмножество".
 * Регистр для тут роли не играет. Пример: "ru-ru" == "RU-ru" == "RU".
 * Хеш и равенство считается по всем наличным ключам и значениям.
 * @author rabdullin
 */
public class L18Value implements L18able {

	private static final String NO_TRANSLATIOMN_FOR_LOCALE_S1 = "No translatiomn for locale '%s'";

	final private Map<String, String> l18items = new HashMap<String, String>(2);


	public L18Value() {
		super();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((l18items == null) ? 0 : l18items.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final L18Value other = (L18Value) obj;
		if (l18items == null) {
			if (other.l18items != null)
				return false;
		} else if (!l18items.equals(other.l18items))
			return false;
		return true;
	}


	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("L18 [");
		builder.append( l18items == null ? "NULL" : "count "+ l18items.size() + " ");
		if (l18items != null) {
			builder.append(l18items.toString());
		}
		builder.append("]");
		return builder.toString();
	}


	/**
	 * Выполнить нормализацию локали:
	 *   1) выполняется перевод в верхний регистр
	 *   2) короткие локали вида "X" дополняются до "X-X"
	 *   3) null или пустая строка остаётся как null.
	 * @param locale
	 * @return нормализованная локаль вида "страна-регион", например, "RU-RU" или "EN-US"
	 */
	public static String getNormalizedLocale(String locale) {
		if (locale == null || locale.length() == 0)
			return null;
		if (!locale.contains("-") && !locale.contains("_"))
			locale += "-"+ locale;
		return locale.toUpperCase();
	}


	/**
	 * @return переводы в виде:
	 *    ключ = нормализованная локаль вида "RU-RU" (см getNormalizedLocale),
	 *    значение = перевод для этой локали.
	 * Если имеется ключ Null - то он является локалью по-умолчанию (defaultLocale).
	 * Единственное значение в l18items (с любым ключом-локалью) - тоже будет приниматься как значение по-умолчанию.
	 * @return
	 */
	public Map<String, String> getL18items() {
		return l18items;
	}


	/**
	 * Зарегистрировать перевод для указанной локали.
	 * @param locale локаль (будет нормирована: см. getNormalizedLocale)
	 * @param translation перевод
	 */
	public void regItem( String locale, String translation) {
		final String key = getNormalizedLocale(locale);
		l18items.put(key, translation);
	}


	/**
	 * Найти перевод в указанной локали.
	 * @param locale локаль (будет нормирована: см. getNormalizedLocale)
	 * @return перевевёденное значение для локали, если не найдено локали в l18items - поднимается исключение.
	 */
	public String getStrict(String locale) {
		final String result = findTranslated(locale);
		if (result == null)
			throw new RuntimeException( String.format(NO_TRANSLATIOMN_FOR_LOCALE_S1, getNormalizedLocale(locale)));
		return result;
	}


	/**
	 * Найти перевод в указанной локали, если точного соот-вия для локали не будет
	 * попытаться найти значений по-умолчанию, если его нет - вернуть l18default
	 * @param l18default значение по-умолчанию (если нет явно заданной null-локали
	 * для l18items или кол-во элементов в l18items отлично от единственного) 
	 * @param locale
	 * @return перевевёденное значение для указанной локали
	 */
	public String get(String locale, String l18default) {
		String result = findTranslated(locale);
		if (result == null) { // выработаем значение по-умолчанию ...
			result = getDefaultLocaleTranslation();
			if (result == null)
				result = l18default;
		}
		return result;
	}

	/**
	 * Вернуть значение перевода по-умолчанию:
	 *    если имеется локаль==null, воз-ся её перевод,
	 *    иначе если в переводах ровно одно значение (с любой локалью) - воз-ся оно,
	 *    иначе вернуть null 
	 * @return
	 */
	protected String getDefaultLocaleTranslation() {
		String result = null;
		if (!l18items.isEmpty()) { // выработаем значение по-умолчанию ...
			if (l18items.containsKey(null))
				result = l18items.get(null); // явно заданное умолчание (ключ=null)
			else if (l18items.size() == 1)
				result = l18items.entrySet().iterator().next().getValue(); // значение единственного элемента
		}
		return result;
	}

	/**
	 * Найти перевод в указанной локали (регистр симолвов локали роли не играет).
	 * @param locale
	 * @return перевевёденное значение для локали, если не найдено воз-ся null.
	 */
	protected String findTranslated(String locale) {
		final String key = getNormalizedLocale(locale);
		return (l18items.containsKey(key)) ? l18items.get(key) : null; 
	}


}
