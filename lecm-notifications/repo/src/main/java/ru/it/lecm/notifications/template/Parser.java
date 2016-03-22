package ru.it.lecm.notifications.template;

import ru.it.lecm.notifications.beans.TemplateParseException;
import ru.it.lecm.notifications.beans.TemplateRunException;
import java.util.Map;

/**
 *
 * @author vkuprin
 */
public interface Parser {

	/**
	 * Сгенерировать сообщение по данному шаблону.
	 *
	 * @param templateStr шаблон сообщения в виде строки.
	 * @param objectsMap список объектов. передаваемых для обработки в шаблон
	 * @return сгененриванное сообщение.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 */
	String runTemplate(String templateStr, Map<String,Object> objectsMap) throws TemplateParseException, TemplateRunException;

	/**
	 * Проверить, является ли шаблон синтаксически верным с точни зрения
	 * SpEL.
	 *
	 * @param templateStr строка-шаблон.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 */
	void parseTemplate(String templateStr) throws TemplateParseException;
}
