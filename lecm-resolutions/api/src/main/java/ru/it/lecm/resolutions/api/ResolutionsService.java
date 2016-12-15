package ru.it.lecm.resolutions.api;

import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:40
 */
public interface ResolutionsService {
    String RESOLUTION_NAMESPACE_URI = "http://www.it.ru/logicECM/resolutions/1.0";

    QName TYPE_RESOLUTION_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "document");

    QName ASSOC_BASE_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "base-document-assoc");
}
