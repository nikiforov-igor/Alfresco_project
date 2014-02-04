package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

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
	public static final QName TYPE_ARM_ACCORDION = QName.createQName(ARM_NAMESPACE_URI, "accordion");
	public static final QName TYPE_ARM_BASE_NODE = QName.createQName(ARM_NAMESPACE_URI, "base-node");
	public static final QName TYPE_ARM_NODE = QName.createQName(ARM_NAMESPACE_URI, "node");

	public static final QName PROP_ARM_CODE = QName.createQName(ARM_NAMESPACE_URI, "code");
	public static final QName PROP_NODE_TYPES = QName.createQName(ARM_NAMESPACE_URI, "types");
	public static final QName PROP_NODE_FILTERS = QName.createQName(ARM_NAMESPACE_URI, "filters");
	public static final QName PROP_COUNTER_ENABLE = QName.createQName(ARM_NAMESPACE_URI, "counter-enable");
	public static final QName PROP_COUNTER_QUERY = QName.createQName(ARM_NAMESPACE_URI, "counter-limitation");
	public static final QName PROP_COUNTER_DESCRIPTION = QName.createQName(ARM_NAMESPACE_URI, "counter-description");

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
}
