package ru.it.lecm.dictionary.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public interface DictionaryBean {

    String DICTIONARIES_ROOT_NAME = "Dictionary";
    QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

    NodeRef getDictionaryByName(String name);
    List<NodeRef> getChildren(NodeRef nodeRef);

}
