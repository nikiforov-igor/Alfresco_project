package ru.it.lecm.dictionary.export;

import org.alfresco.service.namespace.QName;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 07.11.12
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
public interface ExportNamespace {
	public static final String ATTR_NAME = "name";
    String ATTR_TYPE = "type";
    String ATTR_PATH = "path";
    String ATTR_UPDATE_MODE = "updateMode";
    String DICTIONARY_NAMESPACE_URI = "http://www.it.ru/lecm/dictionary/1.0";
    QName DICTIONARY = QName.createQName(DICTIONARY_NAMESPACE_URI, "dictionary");
    QName PROP_TYPE = QName.createQName(DICTIONARY_NAMESPACE_URI, "type");
    String DICTIONARIES_ROOT_NAME = "Dictionary";
    String TAG_PROPERTY = "property";
    String TAG_DICTIONARY = "dictionary";
    String TAG_ITEMS = "items";
    String TAG_ITEM = "item";
    String TAG_ASSOCS = "assocs";
    String TAG_ASSOC = "assoc";
}
