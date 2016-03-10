package ru.it.lecm.notifications.beans;

/**
 * Ошибка на этапе выполнения шаблона:
 * неверное имя метода, функции, бина или объекта, неверные параметры функции
 * или метода. Детали см. в эксепшене.
 *
 * @author vlevin
 */
public class TemplateRunException extends Exception {

	public TemplateRunException() {
	}

	public TemplateRunException(String message) {
		super(message);
	}

	public TemplateRunException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateRunException(Throwable cause) {
		super(cause);
	}
}
