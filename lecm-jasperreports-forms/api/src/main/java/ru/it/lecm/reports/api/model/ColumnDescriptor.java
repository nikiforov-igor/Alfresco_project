package ru.it.lecm.reports.api.model;


/**
 * Описатель колонки данных:
 *   мнемоника и локализованное название,
 *   тип данных,
 *   выражение для получения данных ("адресация") - простое полей, функцией или ссылкой,
 *   является ли параметризуемой,
 *   произвольные доп флаги.
 * 
 * @author rabdullin
 *
 */
public interface ColumnDescriptor
		extends JavaClassable, L18able, FlagsExtendable, Comparable<ColumnDescriptor>
{

	/**
	 * Мнемоника колонки - для использования как ссылка на колонку в конфигурации,
	 * request-аргументах (если не задано явно название праметра)
	 * @return
	 */
	String getColumnName();
	void setColumnName(String colname);

	/**
	 * Является ли колонка спецальной, например, константой для запроса или 
	 * чем-то подобным. По-умолчанию false. Специальные колонки не включаются 
	 * атоматом в шаблоны отчётов в часть вывода.
	 * @return 
	 */
	boolean isSpecial();
	void setSpecial(boolean flag);

	JavaDataType getDataType();
	void setDataType(JavaDataType value);

	/**
	 * Выражение в терминах Провайдера НД для получения значения колонки.
	 * Сейчас используется так:
	 *    1) значения внутри одианрых "{...}" принимаются за ссылки на поля или ассоциации,
	 *    2) значения внутри двойных "{{...}}" - данные для провайдера
	 *    3) другие воспринимаются как константы
	 * @return
	 */
	String getExpression();
	void setExpression(String value);

	/**
	 * Получение ссылочной qname-строки для выражения expression (удаление '{' и '}')
	 * @return
	 */
	String getQNamedExpression();

	ParameterTypedValue getParameterValue();
	void setParameterValue(ParameterTypedValue value);

	// TODO: перенести в interface ParameterTyped 
	/** связанный тип параметра альфреско */
	String getAlfrescoType();
	void setAlfrescoType(String alfrescoType);

	// TODO: перенести в interface ParameterTyped 
	/**
	 * Порядковый номер в списке выбора параметров (сравнение тоже в порядке order)
	 * @return
	 */
	int getOrder();
	void setOrder(int order);

}
