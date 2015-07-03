package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.arm.beans.childRules.ArmBaseChildRule;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

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
	public static final QName PROP_ARM_SHOW_CALENDAR = QName.createQName(ARM_NAMESPACE_URI, "show-calendar");

	public static final QName TYPE_ARM_BASE_NODE = QName.createQName(ARM_NAMESPACE_URI, "base-node");
	public static final QName TYPE_ARM_ACCORDION = QName.createQName(ARM_NAMESPACE_URI, "accordion");
	public static final QName TYPE_ARM_NODE = QName.createQName(ARM_NAMESPACE_URI, "node");
	public static final QName TYPE_ARM_REPORTS_NODE = QName.createQName(ARM_NAMESPACE_URI, "reports-node");
	public static final QName TYPE_ARM_HTML_NODE = QName.createQName(ARM_NAMESPACE_URI, "html-node");
	public static final QName PROP_NODE_TYPES = QName.createQName(ARM_NAMESPACE_URI, "types");
	public static final QName PROP_SEARCH_TYPE = QName.createQName(ARM_NAMESPACE_URI, "search-type");
	public static final QName PROP_SEARCH_QUERY = QName.createQName(ARM_NAMESPACE_URI, "search-query");
	public static final QName PROP_COUNTER_ENABLE = QName.createQName(ARM_NAMESPACE_URI, "counter-enable");
	public static final QName PROP_COUNTER_QUERY = QName.createQName(ARM_NAMESPACE_URI, "counter-limitation");
	public static final QName PROP_COUNTER_DESCRIPTION = QName.createQName(ARM_NAMESPACE_URI, "counter-description");
	public static final QName PROP_HTML_URL = QName.createQName(ARM_NAMESPACE_URI, "html-url");
	public static final QName PROP_REPORT_CODES = QName.createQName(ARM_NAMESPACE_URI, "reportCodes");
	public static final QName ASSOC_NODE_COLUMNS = QName.createQName(ARM_NAMESPACE_URI, "fields-assoc");
	public static final QName ASSOC_NODE_FILTERS = QName.createQName(ARM_NAMESPACE_URI, "filters-assoc");
	public static final QName ASSOC_NODE_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "node-child-rule-assoc");
	public static final QName PROP_IS_SELECT_BY_ACTIVE_TASKS = QName.createQName(ARM_NAMESPACE_URI, "is-select-by-activeTasks");
	public static final QName PROP_ACTIVE_TASKS_FILTER = QName.createQName(ARM_NAMESPACE_URI, "activeTasks-filter");
	public static final QName ASSOC_ACCORDION_BUSINESS_ROLES = QName.createQName(ARM_NAMESPACE_URI, "business-roles-assoc");
	public static final QName PROP_IS_AGGREGATION_NODE = QName.createQName(ARM_NAMESPACE_URI, "is-aggregation-node");
	public static final QName PROP_IS_FOR_SECRETARIES = QName.createQName(ARM_NAMESPACE_URI, "is-for-secretaries");

	public static final QName TYPE_ARM_COLUMN = QName.createQName(ARM_NAMESPACE_URI, "field");
	public static final QName PROP_COLUMN_TITLE = QName.createQName(ARM_NAMESPACE_URI, "field-title");
	public static final QName PROP_COLUMN_FIELD_NAME = QName.createQName(ARM_NAMESPACE_URI, "field-name");
	public static final QName PROP_COLUMN_FORMAT_STRING = QName.createQName(ARM_NAMESPACE_URI, "field-format-string");
	public static final QName PROP_COLUMN_SORTABLE = QName.createQName(ARM_NAMESPACE_URI, "field-sortable");
	public static final QName PROP_COLUMN_BY_DEFAULT = QName.createQName(ARM_NAMESPACE_URI, "field-by-default");

	public static final QName TYPE_QUERY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "query-child-rule");
	public static final QName PROP_LIST_QUERY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "list-query-child-rule");

	public static final QName TYPE_DICTIONARY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "dictionary-child-rule");
	public static final QName ASSOC_DICTIONARY_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "child-rule-dictionary-assoc");

	public static final QName TYPE_STATUSES_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "statuses-child-rule");
	public static final QName PROP_STATUSES_RULE = QName.createQName(ARM_NAMESPACE_URI, "statuses-rule");
	public static final QName PROP_SELECTED_STATUSES = QName.createQName(ARM_NAMESPACE_URI, "selected-statuses");

	public static final QName TYPE_XPATH_CHILD_RULE = QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule");
	public static final QName PROP_ROOT_XPATH= QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule-root-xpath");
	public static final QName PROP_XPATH_TYPES = QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule-types");
	public static final QName PROP_XPATH_FILTER = QName.createQName(ARM_NAMESPACE_URI, "xpath-child-rule-filter");

	public static final QName ASPECT_ARM_ORDERED = QName.createQName(ARM_NAMESPACE_URI, "ordered");
	public static final QName PROP_ARM_ORDER = QName.createQName(ARM_NAMESPACE_URI, "order");

    public static final QName TYPE_ARM_FILTER = QName.createQName(ARM_NAMESPACE_URI, "filter");
    public static final QName PROP_FILTER_CODE = QName.createQName(ARM_NAMESPACE_URI, "filter-code");
    public static final QName PROP_FILTER_CLASS = QName.createQName(ARM_NAMESPACE_URI, "filter-class");
    public static final QName PROP_FILTER_VALUES = QName.createQName(ARM_NAMESPACE_URI, "filter-values");
    public static final QName PROP_FILTER_QUERY = QName.createQName(ARM_NAMESPACE_URI, "filter-query");
    public static final QName PROP_FILTER_MULTIPLE = QName.createQName(ARM_NAMESPACE_URI, "filter-multiple-select");

    public static final QName TYPE_USER_SETTINGS= QName.createQName(ARM_NAMESPACE_URI, "user-settings");
    public static final QName ASSOC_USER_NODE_COLUMNS = QName.createQName(ARM_NAMESPACE_URI, "user-fields-assoc");

	public static final QName TYPE_ARM_ACCORDION_RUN_AS = QName.createQName(ARM_NAMESPACE_URI, "accordion-runAs");
	public static final QName PROP_ARM_ACCORDION_RUN_AS_PATH = QName.createQName(ARM_NAMESPACE_URI, "path-to-node");
	public static final QName PROP_ARM_ACCORDION_NAME_FORMAT_STRING = QName.createQName(ARM_NAMESPACE_URI, "name-format-string");
	public static final QName ASSOC_ARM_ACCORDION_RUN_AS_EMPLOYEE = QName.createQName(ARM_NAMESPACE_URI, "runAs-employee");
	/**
	 * проверяет что объект является аккордионом
	 */
	public boolean isArmAccordion(NodeRef ref);
	public boolean isRunAsArmAccordion(NodeRef ref);

	/**
	 * проверяет что объект является узлом
	 */
	public boolean isArmNode(NodeRef ref);

	/**
	 * проверяет что объект является узлом с отчётами
	 */
	public boolean isArmReportsNode(NodeRef ref);

    /**
     * проверяет что объект является корректным элементом АРМ
     * @param ref
     * @return
     */
    public boolean isArmElement(NodeRef ref);

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
	public List<ArmFilter> getNodeFilters(NodeRef node);

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
     * Получение колонок узла (списка NodeRef)
     * @param node Узел
     * @return Объект с настройками счётчика
     */
    public List<NodeRef> getNodeColumnsRefs(NodeRef node);
    /**
     * Получение колонок узла для текущего пользователя из его настроек
     * @param node Узел
     * @return Объект с настройками счётчика
     */
    public List<ArmColumn> getUserNodeColumns(NodeRef node);

	/**
	 * Получение запроса для узла
	 * @param node узел
	 * @return запрос
	 */
	public ArmBaseChildRule getNodeChildRule(NodeRef node);

	/**
	 * Агрегировать поисковый запрос для узла
	 * @param nodeRef Узел
	 */
	public void aggregateNode(NodeRef nodeRef);

    /**
     * Получение узел с настройками для узла АРМ
     * @param node узел
     */
    public NodeRef getNodeUserSettings(final NodeRef node);

    /**
     * Создание узел с настройками для узла АРМ
     * @param node узел
     */
    public NodeRef createUserSettingsForNode(final NodeRef node) throws WriteTransactionNeededException;

    void invalidateCache();

    void invalidateCurrentUserCache();
}
