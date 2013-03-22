package ru.it.lecm.regnumbers.template;

/**
 * В шаблоне есть синтаксическа ошибка:
 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
 * функций. Детали см. в эксепшене.
 *
 * @author vlevin
 */
public class TemplateParseException extends Exception {

	public TemplateParseException() {
	}

	public TemplateParseException(String message) {
		super(message);
	}

	public TemplateParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateParseException(Throwable cause) {
		super(cause);
	}
}
