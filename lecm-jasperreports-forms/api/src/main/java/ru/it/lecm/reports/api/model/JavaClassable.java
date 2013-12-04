package ru.it.lecm.reports.api.model;

/**
 * Индикатор того, что имеется сопоставленный java-класс.
 * @author rabdullin
 */
public interface JavaClassable {

	/**
	 * Название Java-класса.
	 */
	String getClassName();

	void setClassName(String value);
}
