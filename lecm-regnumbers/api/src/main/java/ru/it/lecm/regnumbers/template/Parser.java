package ru.it.lecm.regnumbers.template;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public interface Parser {

	/**
	 * Сгенерировать номер документа до данному шаблону номера.
	 *
	 * @param templateStr шаблон номера документа в виде строки.
	 * @param documentNode ссылка на экземпляр документа, для которого
	 * необходимо
	 * сгенерировать номер.
	 * @return сгененриванный номер документа.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 */
	String runTemplate(String templateStr, NodeRef documentNode) throws TemplateParseException, TemplateRunException;

	/**
	 * Проверить, является ли шаблон номера синтаксический верным с точни зрения
	 * SpEL.
	 *
	 * @param templateStr строка-шаблон.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 */
	void parseTemplate(String templateStr) throws TemplateParseException;
}
