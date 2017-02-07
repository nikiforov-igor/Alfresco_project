package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.arm.beans.childRules.ArmBaseChildRule;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:10
 */
public interface ArmService {
	String ARM_ROOT_ID = "ARM_ROOT_ID";
	String ARM_SETTINGS_DICTIONARY_NAME = "Настройки АРМ";

	String ARM_NAMESPACE_URI = "http://www.it.ru/logicECM/arm/1.0";

	QName TYPE_ARM = QName.createQName(ARM_NAMESPACE_URI, "arm");
	QName PROP_ARM_CODE = QName.createQName(ARM_NAMESPACE_URI, "code");
	QName PROP_ARM_SHOW_CALENDAR = QName.createQName(ARM_NAMESPACE_URI, "show-calendar");
	QName PROP_ARM_SHOW_CREATE_BUTTON = QName.createQName(ARM_NAMESPACE_URI, "show-create-button");

	QName TYPE_ARM_BASE_NODE = QName.createQName(ARM_NAMESPACE_URI, "base-node");
	QName TYPE_ARM_ACCORDION = QName.createQName(ARM_NAMESPACE_URI, "accordion");
	QName TYPE_ARM_NODE = QName.createQName(ARM_NAMESPACE_URI, "node");
	QName TYPE_ARM_REPORTS_NODE = QName.createQName(ARM_NAMESPACE_URI, "reports-node");
	QName TYPE_ARM_HTML_NODE = QName.createQName(ARM_NAMESPACE_URI, "html-node");
	QName PROP_NODE_TYPES = QName.createQName(ARM_NAMESPACE_URI, "types");
	QName PROP_SEARCH_TYPE = QName.createQName(ARM_NAMESPACE_URI, "search-type");
	QName PROP_SEARCH_QUERY = QName.createQName(ARM_NAMESPACE_URI, "search-query");
	QName PROP_COUNTER_ENABLE = QName.createQName(ARM_NAMESPACE_URI, "counter-enable");
	QName PROP_COUNTER_QUERY = QName.createQName(ARM_NAMESPACE_URI, "counter-limitation");
	QName PROP_COUNTER_DESCRIPTION = QName.createQName(ARM_NAMESPACE_URI, "counter-description");
	QName PROP_HTML_URL = QName.createQName(ARM_NAMESPACE_URI, "html-url");
	QName PROP_REPORT_CODES = QName.createQName(ARM_NAMESPACE_URI, "reportCodes");
	QName ASSOC_NODE_COLUMNS = QName.createQName(ARM_NAMESPACE_URI, "fields-assoc");
	QName ASSOC_NODE_FILTERS = QName.createQName(ARM_NAMESPACE_URI, "filters-assoc");
	QName ASSOC_NODE_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "node-child-rule-assoc");
	QName PROP_IS_SELECT_BY_ACTIVE_TASKS = QName.createQName(ARM_NAMESPACE_URI, "is-select-by-activeTasks");
	QName PROP_ACTIVE_TASKS_FILTER = QName.createQName(ARM_NAMESPACE_URI, "activeTasks-filter");
	QName PROP_IS_AGGREGATION_NODE = QName.createQName(ARM_NAMESPACE_URI, "is-aggregation-node");
	QName PROP_IS_FOR_SECRETARIES = QName.createQName(ARM_NAMESPACE_URI, "is-for-secretaries");
	QName PROP_ARM_NODE_EXPRESSION = QName.createQName(ARM_NAMESPACE_URI, "expression");

	QName TYPE_ARM_COLUMN = QName.createQName(ARM_NAMESPACE_URI, "field");
	QName PROP_COLUMN_TITLE = QName.createQName(ARM_NAMESPACE_URI, "field-title");
	QName PROP_COLUMN_FIELD_NAME = QName.createQName(ARM_NAMESPACE_URI, "field-name");
	QName PROP_COLUMN_FORMAT_STRING = QName.createQName(ARM_NAMESPACE_URI, "field-format-string");
	QName PROP_COLUMN_SORTABLE = QName.createQName(ARM_NAMESPACE_URI, "field-sortable");
	QName PROP_COLUMN_BY_DEFAULT = QName.createQName(ARM_NAMESPACE_URI, "field-by-default");

	QName TYPE_QUERY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "query-child-rule");
	QName PROP_LIST_QUERY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "list-query-child-rule");

	QName TYPE_DICTIONARY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "dictionary-child-rule");
	QName ASSOC_DICTIONARY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "child-rule-dictionary-assoc");

	QName TYPE_STATUSES_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "statuses-child-rule");
	QName PROP_STATUSES_RULE = QName.createQName(ARM_NAMESPACE_URI, "statuses-rule");
	QName PROP_SELECTED_STATUSES = QName.createQName(ARM_NAMESPACE_URI, "selected-statuses");

	QName TYPE_XPATH_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule");
	QName PROP_ROOT_XPATH= QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule-root-xpath");
	QName PROP_XPATH_TYPES = QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule-types");
	QName PROP_XPATH_FILTER = QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule-filter");

	QName TYPE_SCRIPT_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "script-child-rule");
	QName PROP_ROOT_SCRIPT = QName.createQName(ARM_NAMESPACE_URI, "script-child-rule-root");

	QName ASPECT_ARM_ORDERED = QName.createQName(ARM_NAMESPACE_URI, "ordered");
	QName PROP_ARM_ORDER = QName.createQName(ARM_NAMESPACE_URI, "order");

    QName TYPE_ARM_FILTER = QName.createQName(ARM_NAMESPACE_URI, "filter");
    QName PROP_FILTER_CODE = QName.createQName(ARM_NAMESPACE_URI, "filter-code");
    QName PROP_FILTER_CLASS = QName.createQName(ARM_NAMESPACE_URI, "filter-class");
    QName PROP_FILTER_VALUES = QName.createQName(ARM_NAMESPACE_URI, "filter-values");
    QName PROP_FILTER_QUERY = QName.createQName(ARM_NAMESPACE_URI, "filter-query");
    QName PROP_FILTER_MULTIPLE = QName.createQName(ARM_NAMESPACE_URI, "filter-multiple-select");

    QName TYPE_USER_SETTINGS= QName.createQName(ARM_NAMESPACE_URI, "user-settings");
    QName ASSOC_USER_NODE_COLUMNS = QName.createQName(ARM_NAMESPACE_URI, "user-fields-assoc");

	QName TYPE_ARM_ACCORDION_RUN_AS = QName.createQName(ARM_NAMESPACE_URI, "accordion-runAs");
	QName PROP_ARM_ACCORDION_RUN_AS_PATH = QName.createQName(ARM_NAMESPACE_URI, "path-to-node");
	QName PROP_ARM_ACCORDION_NAME_FORMAT_STRING = QName.createQName(ARM_NAMESPACE_URI, "name-format-string");
	QName ASSOC_ARM_ACCORDION_RUN_AS_EMPLOYEE = QName.createQName(ARM_NAMESPACE_URI, "runAs-employee");

	QName PROP_ARM_SHOW_IN_MENU = QName.createQName(ARM_NAMESPACE_URI, "show-in-menu");
	QName ASSOC_ARM_MENU_BUSINESS_ROLES = QName.createQName(ARM_NAMESPACE_URI, "menu-business-roles-assoc");

	Pattern MULTIPLE_NOT_QUERY = Pattern.compile("^NOT[\\s]+.*(?=\\sOR\\s|\\sAND\\s|\\s\\+|\\s\\-)");

	/**
	 * проверяет что объект является аккордионом
	 */
	boolean isArmAccordion(NodeRef ref);
	boolean isRunAsArmAccordion(NodeRef ref);

	/**
	 * проверяет что объект является узлом
	 */
	boolean isArmNode(NodeRef ref);

	/**
	 * проверяет что объект является узлом с отчётами
	 */
	boolean isArmReportsNode(NodeRef ref);

    /**
     * проверяет что объект является корректным элементом АРМ
     * @param ref
     * @return
     */
	boolean isArmElement(NodeRef ref);

	/**
	 * Получение справочника с настройками АРМ
	 * @return Справочник с настройками АРМ
	 */
	NodeRef getDictionaryArmSettings();

	/**
	 * Получение АРМ-а по коду
	 * @param code код
	 * @return АРМ
	 */
	NodeRef getArmByCode(String code);

	/**
	 * Получение аккордионов для АРМ-а
	 * @param arm АРМ
	 * @return список аккордионов
	 */
	List<NodeRef> getArmAccordions(NodeRef arm);

	/**
	 * Получение АРМ-ов для меню
	 * @return список АРМ-ов для меню
	 */
	List<NodeRef> getArmsForMenu();

	/**
	 * Получение вложенных узлов
	 * @param node Родитель
	 * @return Списоквложенных узлов
	 */
	List<NodeRef> getChildNodes(NodeRef node);

	/**
	 * Получение типов для узла
	 * @param node Узел
	 * @return Список типов
	 */
	List<String> getNodeTypes(NodeRef node);

	/**
	 * Получение типов для узла (используя унаследованные типы)
	 * @param node Узел
	 * @return Список типов
	 */
	Collection<QName> getNodeTypesIncludeInherit(NodeRef node);

	/**
	 * Получение фильтров для узла
	 * @param node Узел
	 * @return Список фильтров
	 */
	List<ArmFilter> getNodeFilters(NodeRef node);

	/**
	 * Получение счётчика узла
	 * @param node Узел
	 * @return Объект с настройками счётчика
	 */
	ArmCounter getNodeCounter(NodeRef node);

	/**
	 * Получение колонок узла
	 * @param node Узел
	 * @return Объект с настройками счётчика
	 */
	List<ArmColumn> getNodeColumns(NodeRef node);

    /**
     * Получение колонок узла (списка NodeRef)
     * @param node Узел
     * @return Объект с настройками счётчика
     */
	List<NodeRef> getNodeColumnsRefs(NodeRef node);
    /**
     * Получение колонок узла для текущего пользователя из его настроек
     * @param node Узел
     * @return Объект с настройками счётчика
     */
	List<ArmColumn> getUserNodeColumns(NodeRef node);

	/**
	 * Получение запроса для узла
	 * @param node узел
	 * @return запрос
	 */
	ArmBaseChildRule getNodeChildRule(NodeRef node);

	/**
	 * Агрегировать поисковый запрос для узла
	 * @param nodeRef Узел
	 */
	void aggregateNode(NodeRef nodeRef);

    /**
     * Получение узел с настройками для узла АРМ
     * @param node узел
     */
	NodeRef getNodeUserSettings(final NodeRef node);

    /**
     * Создание узел с настройками для узла АРМ
     * @param node узел
     */
	NodeRef createUserSettingsForNode(final NodeRef node) throws WriteTransactionNeededException;

    void invalidateCache();

    void invalidateCurrentUserCache();

	Map<QName, Serializable> getCachedProperties(NodeRef nodeRef);

	String getNodeSearchQuery(NodeRef nodeRef);
}