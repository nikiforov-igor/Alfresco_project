package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 11:43
 */
public interface SubstitudeBean {

	/**
	 * Символ ссылки на родителя
	 */
	final String PARENT_SYMBOL = "..";

	/**
	 * Разделитель элементов в выражении
	 */
	final String SPLIT_TRANSITIONS_SYMBOL = "/";

	/**
	 * Символ эквивалентности в условиях
	 */
	final String EQUALS_SYMBOL = "=";

	/**
	 * Разделитель условий
	 */
	final String SPLIT_EXPRESSION_SYMBOL = ",";

	/**
	 * Символ открытия условия
	 */
	final String OPEN_EXPRESSIONS_SYMBOL = "(";

	/**
	 * Символ закрытия условий
	 */
	final String CLOSE_EXPRESSIONS_SYMBOL = ")";

	/**
	 * Символ открытия выражения
	 */
	final String OPEN_SUBSTITUDE_SYMBOL = "{";

	/**
	 * Символ закрытия выражения
	 */
	final String CLOSE_SUBSTITUDE_SYMBOL = "}";

	/**
	 * Получение заголовка элемента в соответствии с форматной строкой.
	 * Выражения в форматной строке должны быть заключены в символы открытия (@see OPEN_SUBSTITUDE_SYMBOL) и закрытия (@see CLOSE_SUBSTITUDE_SYMBOL)
	 *
	 * @param node элемент
	 * @param formatString форматная строка
	 * @return Заголовок элемента
	 */
	public String formatNodeTitle(NodeRef node, String formatString);
}
