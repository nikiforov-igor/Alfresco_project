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

    String DICTIONARIES_ROOT_NAME = "Dictionary";

    NodeRef getDictionaryByName(String name);
    List<NodeRef> getChildren(NodeRef nodeRef);
	List<NodeRef> getRecordsByParamValue (String dictionaryName, QName parameter, Serializable value);
	NodeRef getRecordByParamValue (String dictionaryName, QName parameter, Serializable value);
}
