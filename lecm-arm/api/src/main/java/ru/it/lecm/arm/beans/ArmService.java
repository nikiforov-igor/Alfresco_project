package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.arm.beans.query.ArmBaseQuery;
import ru.it.lecm.arm.beans.query.ArmStaticQuery;

import java.util.Collection;
import java.util.List;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:10
 */
public interface ArmService {
	public static final String ARM_ROOT_ID = "ARM_ROOT_ID";
	public static final String ARM_SETTINGS_DICTIONARY_NAME = "Настройки АРМ";

	public static final String ARM_NAMESPACE_URI = "http://www.it.ru/logicECM/arm/1.0";

	public static final QName TYPE_ARM = QName.createQName(ARM_NAMESPACE_URI, "arm");
	public static final QName PROP_ARM_CODE = QName.createQName(ARM_NAMESPACE_URI, "code");

	public static final QName TYPE_ARM_ACCORDION = QName.createQName(ARM_NAMESPACE_URI, "accordion");
	public static final QName TYPE_ARM_NODE = QName.createQName(ARM_NAMESPACE_URI, "node");
	public static final QName PROP_NODE_TYPES = QName.createQName(ARM_NAMESPACE_URI, "types");
	public static final QName PROP_NODE_FILTERS = QName.createQName(ARM_NAMESPACE_URI, "filters");
	public static final QName PROP_COUNTER_ENABLE = QName.createQName(ARM_NAMESPACE_URI, "counter-enable");
	public static final QName PROP_COUNTER_QUERY = QName.createQName(ARM_NAMESPACE_URI, "counter-limitation");
	public static final QName PROP_COUNTER_DESCRIPTION = QName.createQName(ARM_NAMESPACE_URI, "counter-description");
	public static final QName ASSOC_NODE_COLUMNS = QName.createQName(ARM_NAMESPACE_URI, "fields-assoc");
	public static final QName ASSOC_NODE_QUERY = QName.createQName(ARM_NAMESPACE_URI, "node-query-assoc");
	public static final QName ASSOC_ACCORDION_QUERY = QName.createQName(ARM_NAMESPACE_URI, "accordion-query-assoc");

	public static final QName TYPE_ARM_COLUMN = QName.createQName(ARM_NAMESPACE_URI, "field");
	public static final QName PROP_COLUMN_TITLE = QName.createQName(ARM_NAMESPACE_URI, "field-title");
	public static final QName PROP_COLUMN_FIELD_NAME = QName.createQName(ARM_NAMESPACE_URI, "field-name");
	public static final QName PROP_COLUMN_FORMAT_STRING = QName.createQName(ARM_NAMESPACE_URI, "field-format-string");
	public static final QName PROP_COLUMN_SORTABLE = QName.createQName(ARM_NAMESPACE_URI, "field-sortable");

	public static final QName TYPE_STATIC_QUERY = QName.createQName(ARM_NAMESPACE_URI, "static-query");
	public static final QName PROP_SEARCH_QUERY = QName.createQName(ARM_NAMESPACE_URI, "search-query");

	public static final QName TYPE_DYNAMIC_QUERY = QName.createQName(ARM_NAMESPACE_URI, "dynamic-query");
	public static final QName PROP_LIST_QUERY = QName.createQName(ARM_NAMESPACE_URI, "list-query");

	public static final QName TYPE_DICTIONARY_DYNAMIC_QUERY = QName.createQName(ARM_NAMESPACE_URI, "dynamic-dictionary-query");
	public static final QName ASSOC_DICTIONARY_QUERY = QName.createQName(ARM_NAMESPACE_URI, "query-dictionary-assoc");


	/**
	 * проверяет что объект является аккордионом
	 */
	public boolean isArmAccordion(NodeRef ref);

	/**
	 * проверяет что объект является epkjv
	 */
	public boolean isArmNode(NodeRef ref);

	/**
	 * Получение справочника с настройками АРМ
	 * @return Справочник с настройками АРМ
	 */
	public NodeRef getDictionaryArmSettings();

	/**
	 * Получение АРМ-а по коду
	 * @param code код
	 * @return АРМ
	 */
	public NodeRef getArmByCode(String code);

	/**
	 * Получение аккордионов для АРМ-а
	 * @param arm АРМ
	 * @return список аккордионов
	 */
	public List<NodeRef> getArmAccordions(NodeRef arm);

	/**
	 * Получение вложенных узлов
	 * @param node Родитель
	 * @return Списоквложенных узлов
	 */
	public List<NodeRef> getChildNodes(NodeRef node);

	/**
	 * Получение типов для узла
	 * @param node Узел
	 * @return Список типов
	 */
	public List<String> getNodeTypes(NodeRef node);

	/**
	 * Получение типов для узла (используя унаследованные типы)
	 * @param node Узел
	 * @return Список типов
	 */
	public Collection<QName> getNodeTypesIncludeInherit(NodeRef node);

	/**
	 * Получение фильтров для узла
	 * @param node Узел
	 * @return Список фильтров
	 */
	public List<String> getNodeFilters(NodeRef node);

	/**
	 * Получение счётчика узла
	 * @param node Узел
	 * @return Объект с настройками счётчика
	 */
	public ArmCounter getNodeCounter(NodeRef node);

	/**
	 * Получение колонок узла
	 * @param node Узел
	 * @return Объект с настройками счётчика
	 */
	public List<ArmColumn> getNodeColumns(NodeRef node);

	/**
	 * Получение запроса для аккордиона
	 * @param accordion аккордион
	 * @return статический запрос
	 */
	public ArmStaticQuery getAccordionQuery(NodeRef accordion);

	/**
	 * Получение запроса для узла
	 * @param node узел
	 * @return запрос
	 */
	public ArmBaseQuery getNodeQuery(NodeRef node);
}
