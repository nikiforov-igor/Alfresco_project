package ru.it.lecm.reports.api.model;

import java.util.Map;

/**
 * Нечто, имеюшее переводы по локалям
 * @author rabdullin
 */
public interface L18able {

	/**
	 * Зарегистрировать перевод для указанной локали.
	 * @param locale локаль (будет нормирована: см. getNormalizedLocale)
	 * @param translation перевод
	 */
	void regItem( String locale, String translation);


	/**
	 * @return переводы в виде:
	 *    ключ = нормализованная локаль вида "RU-RU" (см getNormalizedLocale),
	 *    значение = перевод для этой локали.
	 * Если имеется ключ Null - то он является локалью по-умолчанию (defaultLocale).
	 * Единственное значение в l18items (с любым ключом-локалью) - тоже будет приниматься как значение по-умолчанию.
	 * @return
	 */
	Map<String, String> getL18items();


	/**
	 * Найти перевод в указанной локали.
	 * @param locale локаль (будет нормирована: см. getNormalizedLocale)
	 * @return перевевёденное значение для локали, если не найдено локали в l18items - поднимается исключение.
	 */
	String getStrict(String locale);


	/**
	 * Найти перевод в указанной локали, если точного соот-вия для локали не будет
	 * попытаться найти значений по-умолчанию, если его нет - вернуть l18default
	 * @param l18default значение по-умолчанию (если нет явно заданной null-локали
	 * для l18items или кол-во элементов в l18items отлично от единственного) 
	 * @param locale
	 * @return перевевёденное значение для указанной локали
	 */
	String get(String locale, String l18default);

	String getDefault(); // тоже что и get(null, "");
}
