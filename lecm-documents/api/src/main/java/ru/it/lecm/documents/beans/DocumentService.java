package ru.it.lecm.documents.beans;

import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 28.02.13
 * Time: 16:03
 */
public interface DocumentService {
    public static final String DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/document/1.0";

    public static final QName TYPE_BASE_DOCUMENT = QName.createQName(DOCUMENT_NAMESPACE_URI, "base");

    public static final QName PROP_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "present-string");
    public static final QName PROP_LIST_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "list-present-string");

    public static final String CONSTRAINT_PRESENT_STRING = "present-string-constraint";
}
