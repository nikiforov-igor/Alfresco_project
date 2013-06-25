package ru.it.lecm.reports.api.model;

/**
 * Нечто, обладающее мнемоническим обозначением.
 * @author rabdullin
 */
public interface Mnemonicable {

	/**
	 * @return Мнемоника
	 */
	String getMnem();

	/**
	 * @param mnemo задать мнемонику
	 */
	void setMnem(String mnemo);
}

