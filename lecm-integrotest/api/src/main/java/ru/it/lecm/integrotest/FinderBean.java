package ru.it.lecm.integrotest;

import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface FinderBean {

	public static final String TYPE_NODE = "nodeType";
	public static final String PROP_NAME = "propName";
	public static final String PROP_VALUE = "value";

	/**
	 * Разименовать строку из префикса типа в полное название типа, при этом,
	 * если строка не является префиком - преобразование не производится.
	 * @param uriOrPrefix
	 * @return
	 */
	String resolveUri(String uriOrPrefix);

	/**
	 * Создание имени-указателя с возможными префиксами
	 * @param qname имя, с возможным префиксом namespace
	 * @return
	 */
	QName makeQName(String qname);

	/**
	 * Создание имени-указателя с возможными префиксами
	 * @param uri имя или префикс namespace
	 * @param part
	 * @return
	 */
	QName makeQName(String uri, String part);

	/**
	 * Найти узел указанного типа с указанным свойством
	 * @param nodeType
	 * @param propName
	 * @param value
	 * @return
	 */
	NodeRef findNodeByProp( QName nodeType, QName propName, String value);
	NodeRef findNodeByProp( String nodeType, String propName, String value);

	List<NodeRef> findNodesByProp( QName nodeType, QName propName, String value);
	List<NodeRef> findNodesByProp( String nodeType, String propName, String value);

	/**
	 * Поиск узла по параметрам, указанным в map
	 * @param args отсюда используются параметры "nodeType", "propName", "value"
	 * @return
	 */
	NodeRef findNodeByProp( Map<String,Object> args);

	/**
	 * Поиск Департамента по имени
	 * @param value
	 * @return
	 */
	NodeRef findOUByName(String value);

	/**
	 * Поиск Должностной позиции по имени
	 * @param value
	 * @return
	 */
	NodeRef findDpByName(String value);

	/**
	 * Поиск Служащего по имени
	 * @param value
	 * @return
	 */
	NodeRef findEmployeeByName(String value);

	/**
	 * Выполнить поисковое разименование, чтобы гарантировать наличие в args
	 * аргумента с именем argNodeId:
	 *  - если argNodeId заполнен - ничего не происходит,
	 *  - иначе, если есть argNodeRef - выполняется поиск узла согласно search-значению 
	 *  в argNodeRef и id найденного узла присваивается для argNodeId. 
	 * @param args список аргументов, в котором проверяется наличие значения
	 * @param argNodeId ключ, соот-щий основному значению в args, которое должно 
	 * гарантироваться данным поиском
	 * @param argNodeRef ключ для поискового минизапроса, который будет искать 
	 * узел, id которого надо присвоить в аргументах args для ключа argNodeId, 
	 * если соот-щее значение будт пустым. 
	 * Формат ссылки такой:
	 *    "nodeType=название_типа;propName=название_свойства;value=значение"
	 * 
	 */
	void ensureNodePresent( Map<String, Object> args, String argNodeId, String argNodeRef);

	/**
	 * Получить список родительских узлов, первым будет идти основной родитель.
	 * @param ref
	 * @return
	 */
	List<NodeRef> getParents( NodeRef ref);
}
