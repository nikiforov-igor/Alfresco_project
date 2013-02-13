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

    String DICTIONARIES_ROOT_NAME = "Dictionary";

	public boolean isDictionary(NodeRef ref);
	public boolean isHeirarchicalDictionaryValue(NodeRef ref);
	public boolean isPlaneDictionaryValue(NodeRef ref);
	public boolean isDictionaryValue(NodeRef ref);
	NodeRef getDictionaryByName(String name);
    NodeRef getDictionaryByDictionaryValue(NodeRef nodeRef);
    List<NodeRef> getChildren(NodeRef nodeRef);
	List<NodeRef> getRecordsByParamValue (String dictionaryName, QName parameter, Serializable value);
	NodeRef getRecordByParamValue (String dictionaryName, QName parameter, Serializable value);
}
