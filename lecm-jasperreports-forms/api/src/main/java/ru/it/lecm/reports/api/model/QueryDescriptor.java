package ru.it.lecm.reports.api.model;

import java.util.List;

public interface QueryDescriptor extends Mnemonicable {

	String getText();
	void setText(String text);

    String getSort();
    void setSort(String sort);

	int getOffset();
	void setOffset(int value);

	int getLimit();
	void setLimit(int value);

	int getPgSize();
	void setPgSize(int value);

	boolean isAllVersions();
	void setAllVersions(boolean value);

	/**
	 * Можно присвоить сразу список - несколько значений через запятую или тчк-с-з.
	 * Фактически вызовет {@link #setSupportedNodeTypes}.
	 * @param value
	 */
	void setPreferedNodeType(String value);

	/**
	 * @return Список типов, которые данные дескриптов может обслуживать.
	 * <br/> Первый элемент это результат для getPrereredNodeType 
	 */
	List<String> getSupportedNodeTypes();

	/**
	 * @values Список типов, которые данные дескриптов может обслуживать 
	 */
	void setSupportedNodeTypes(List<String> values);

	/**
	 * Вернуть true, если указанный тип поддерживается данным дескриптором (отчётом)
	 * @param qname короткое или полное название типма
	 * @return
	 */
	boolean isTypeSupported(String qname);
}
