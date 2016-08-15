package ru.it.lecm.dictionary.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.List;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public interface DictionaryBean {
	String DICTIONARIES_NAMESPACE_URI = "http://www.it.ru/lecm/dictionary/1.0";
	QName TYPE_DICTIONARY = QName.createQName(DICTIONARIES_NAMESPACE_URI, "dictionary");
	QName TYPE_PLANE_DICTIONARY_VALUE = QName.createQName(DICTIONARIES_NAMESPACE_URI, "plane_dictionary_values");
	QName TYPE_BIG_PLANE_DICTIONARY_VALUE = QName.createQName(DICTIONARIES_NAMESPACE_URI, "big_plane_dictionary_values");
	QName TYPE_HIERARCHICAL_DICTIONARY_VALUE = QName.createQName(DICTIONARIES_NAMESPACE_URI, "hierarchical_dictionary_values");
	QName PROPERTY_DICTIONARY_TYPE = QName.createQName(DICTIONARIES_NAMESPACE_URI, "type");

    String DICTIONARIES_ROOT_NAME = "Сервис Справочники";
	String DICTIONARIES_ROOT_ID = "DICTIONARIES_ROOT_ID";

	boolean isDictionary(NodeRef ref);
	boolean isHeirarchicalDictionaryValue(NodeRef ref);
	boolean isPlaneDictionaryValue(NodeRef ref);
	boolean isDictionaryValue(NodeRef ref);
	NodeRef getDictionaryByName(String name);
	NodeRef getDictionaryValueByParam(String dictionaryName, QName parameterName, String parameterValue);
	NodeRef getDictionaryValueByParam(NodeRef dictionaryRef, QName parameterName, String parameterValue);
	NodeRef getDictionaryByDictionaryValue(NodeRef nodeRef);
	List<NodeRef> getChildren(NodeRef nodeRef);

	List<NodeRef> getChildrenSortedByName(NodeRef nodeRef);

	List<NodeRef> getAllChildren(NodeRef nodeRef);

	List<NodeRef> getRecordsByParamValue(String dictionaryName, QName parameter, Serializable value);
	NodeRef getRecordByParamValue(String dictionaryName, QName parameter, Serializable value);
	NodeRef getDictionariesRoot();

	/**
	 * Получить коллекцию типов данных всех справочников зарегистрированных в системе
	 * @return коллекция типов данных (ArrayList<String>) справочников вида prefixedString
	 * Элемент строки представляет собой пару name|value
	 */
	Serializable getAllDictionaryTypes();

	/**
	 * Получить коллецию типов данных существующих справочников
	 * @return коллекция типов данных (ArrayList<String>) существующих справочников вида prefixedString
	 * Элемент строки представляет собой пару name|value
	 */
	Serializable getExistDictionaryTypes();

	/**
	 * получить коллекцию пропертей которые есть у заданного типа справочника
	 * включает в себя cm:name и cm:title
	 * @param dicType имя типа для которого получаем список пропертей
	 * @return коллекция пропертей (ArrayList<String>) для указанного типа справочника вида prefixedString
	 * Элемент строки представляет собой пару name|value
	 */
	Serializable getDictionaryTypeProperties(final String dicType);
}
