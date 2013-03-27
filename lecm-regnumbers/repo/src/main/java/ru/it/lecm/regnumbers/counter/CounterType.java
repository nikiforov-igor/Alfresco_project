package ru.it.lecm.regnumbers.counter;

/**
 *
 * @author vlevin
 */
public enum CounterType {

	/**
	 * Глобальный сквозной счетчик.
	 */
	PLAIN("globalPlainCounter"),
	/**
	 * Глобальный счетчик в пределах текущего года.
	 */
	YEAR("globalYearCounter"),
	/**
	 * Сквозной счетчик для типа документа.
	 */
	DOCTYPE_PLAIN("doctypePlainCounter[%s]"),
	/**
	 * Годовой счетчик для типа документа.
	 */
	DOCTYPE_YEAR("doctypeYearCounter[%s]");
	private final String objectName;

	private CounterType(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return Имя объекта в репзитории для данного счетчика.
	 */
	public String objectName() {
		return objectName;
	}
}
