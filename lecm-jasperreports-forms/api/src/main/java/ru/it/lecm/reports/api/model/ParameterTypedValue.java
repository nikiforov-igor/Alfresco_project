package ru.it.lecm.reports.api.model;

public interface ParameterTypedValue extends ParameterType {

	/**
	 * Единственное значение параметра или значение параметра слева.
	 */
	Object getBound1();
	void setBound1(Object value);

	/**
	 * Если параметр имеет единственное значение - не используется, иначе значение параметра справа.
	 */
	Object getBound2();
	void setBound2(Object value);

	/**
	 * Обязательность параметра (true) или опцион (false)
	 */
	boolean isRequired();
	void setRequired(boolean flag);

	/**
	 * Пуст параметр (true) или заполнен (false)
	 */
	boolean isEmpty();
}
