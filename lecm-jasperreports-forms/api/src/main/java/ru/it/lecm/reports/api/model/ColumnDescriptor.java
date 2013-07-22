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
		extends JavaClassable, L18able, FlagsExtendable {

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

	String getExpression();
	void setExpression(String value);

	ParameterTypedValue getParameterValue();
	void setParameterValue(ParameterTypedValue value);

    String getAlfrescoType();
    void setAlfrescoType(String alfrescoType);

    int getOrder();
    void setOrder(int order);

    public int compareTo(Object obj);
}
