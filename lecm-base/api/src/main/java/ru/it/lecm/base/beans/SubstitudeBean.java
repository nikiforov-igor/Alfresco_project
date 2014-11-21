package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

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
	 * Символ обертки объекта в ссылку
	 */
	final String WRAP_AS_LINK_SYMBOL = "!";

	/**
	 * Символ разделяющий название аттрибута от его формата (используется для дат)
	 */
	final String STRING_FORMAT_SYMBOL = "?";

	/**
	 * Символ, указывающий что далее следует псевдо-свойство
	 */
	final String PSEUDO_PROPERTY_SYMBOL = "~";

    final String DEFAULT_VALUE_SYMBOL = "|";

	final String AUTHOR = "AUTHOR";

	final String DEFAULT_OBJECT_TYPE_TEMPLATE = "{cm:name}";
	final String DEFAULT_OBJECT_TYPE_LIST_TEMPLATE = "автор : {!~AUTHOR}, дата изменения: {cm:modified}";

	final String BJ_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";
	final String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";

	final QName PROP_OBJ_TYPE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "objectType-template");
	final QName PROP_OBJ_TYPE_LIST_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "objectType-list-template");
	final QName PROP_OBJ_TYPE_CLASS = QName.createQName(BJ_NAMESPACE_URI, "objectType-class");
	final QName ASSOC_EMPLOYEE_PERSON = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-assoc");

    /**
     * Символ ссылки на родителя
     */
    final String ASSOC_DELIMITER = "; ";

	/**
	 * Получение заголовка элемента в соответствии с форматной строкой.
	 * Выражения в форматной строке должны быть заключены в символы открытия (@see OPEN_SUBSTITUDE_SYMBOL) и закрытия (@see CLOSE_SUBSTITUDE_SYMBOL)
	 *
	 * @param node элемент
	 * @param formatString форматная строка
	 * @return Заголовок элемента
	 */
	public String formatNodeTitle(NodeRef node, String formatString);

	public String formatNodeTitle(NodeRef node, String formatString, String dateFormat, Integer timeZoneOffset);

	public String getObjectDescription(NodeRef object);

	public String getTemplateStringForObject(NodeRef object);

	public String getTemplateStringForObject(NodeRef object, boolean forList);

	public List<NodeRef> getObjectsByTitle(NodeRef object, String formatTitle);

	/**
	 * Получение псевдо свойста или выполнение встроенной функции
	 * @param object исходный узел
	 * @param psedudoProp мнемоника псевдо-свойства или функции (уже без всяких префиксных символов) 
	 * @return список узлов после выполнения функции (псевдо-свойства)
	 */
	public List<NodeRef> getObjectByPseudoProp(NodeRef object, final String psedudoProp);

    public String getFormatStringByPseudoProp(NodeRef object, final String psedudoProp);

    public Object getRealValueByPseudoProp(NodeRef object, final String psedudoProp);

    String getTemplateStringForObject(NodeRef object, boolean forList, boolean returnDefaulIfNull);

    public Object getNodeFieldByFormat(NodeRef node, String formatString);

    public Object getNodeFieldByFormat(NodeRef node, String formatString, String dateFormat, Integer timeZoneOffset);
}
