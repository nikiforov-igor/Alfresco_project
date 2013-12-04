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
	 *    <li> ключ = нормализованное название локали в виде "Язык-Страна", 
	 * например,а "RU-RU" (см getNormalizedLocale),
	 *    <li> значение = перевод для этой локали.
	 * <br/> Если имеется ключ Null - то он является локалью по-умолчанию (defaultLocale).
	 * <br/> Единственное значение в l18items (с любым ключом-локалью) - тоже будет приниматься как значение по-умолчанию.
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
	 * @return перевевёденное значение для указанной локали
	 */
	String get(String locale, String l18default);

	/**
	 * Получить описание в локали по-умолчанию.
	 * @return тоже что и get(null, null);
	 */
	String getDefault();
}
