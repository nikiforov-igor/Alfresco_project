package ru.it.lecm.dictionary.beans;

import java.io.Serializable;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public interface DictionaryBean {
	public static final String DICTIONARIES_NAMESPACE_URI = "http://www.it.ru/lecm/dictionary/1.0";
	public static final QName TYPE_DICTIONARY = QName.createQName(DICTIONARIES_NAMESPACE_URI, "dictionary");
	public static final QName TYPE_PLANE_DICTIONARY_VALUE = QName.createQName(DICTIONARIES_NAMESPACE_URI, "plane_dictionary_values");
	public static final QName TYPE_HIERARCHICAL_DICTIONARY_VALUE = QName.createQName(DICTIONARIES_NAMESPACE_URI, "hierarchical_dictionary_values");
	public static final QName PROPERTY_DICTIONARY_TYPE = QName.createQName(DICTIONARIES_NAMESPACE_URI, "type");

    String DICTIONARIES_ROOT_NAME = "Сервис Справочники";
	String DICTIONARIES_ROOT_ID = "DICTIONARIES_ROOT_ID";

	public boolean isDictionary(NodeRef ref);
	public boolean isHeirarchicalDictionaryValue(NodeRef ref);
	public boolean isPlaneDictionaryValue(NodeRef ref);
	public boolean isDictionaryValue(NodeRef ref);
	public NodeRef getDictionaryByName(String name);
	public NodeRef getDictionaryValueByParam(String dictionaryName, QName parameterName, String parameterValue);
	public NodeRef getDictionaryValueByParam(NodeRef dictionaryRef, QName parameterName, String parameterValue);
	public NodeRef getDictionaryByDictionaryValue(NodeRef nodeRef);
	public List<NodeRef> getChildren(NodeRef nodeRef);
	public List<NodeRef> getRecordsByParamValue (String dictionaryName, QName parameter, Serializable value);
	public NodeRef getRecordByParamValue (String dictionaryName, QName parameter, Serializable value);
	public NodeRef getDictionariesRoot();
}
