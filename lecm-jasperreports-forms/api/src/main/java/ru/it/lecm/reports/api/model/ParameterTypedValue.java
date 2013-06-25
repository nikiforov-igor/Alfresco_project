package ru.it.lecm.reports.api.model;

public interface ParameterTypedValue extends ParameterType {

	/**
	 * Единственное значение параметра или значение параметра слева.
	 * @return
	 */
	Object getBound1();
	void setBound1(Object value);

	/**
	 * Если параметр имеет единственное значение - не используется, иначе значение параметра справа.
	 * @return
	 */
	Object getBound2();
	void setBound2(Object value);

}
