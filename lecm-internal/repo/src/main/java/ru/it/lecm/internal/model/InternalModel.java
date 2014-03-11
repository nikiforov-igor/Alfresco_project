package ru.it.lecm.internal.model;

import org.alfresco.service.namespace.QName;

/**
 * User: dbashmakov
 * Date: 11.03.14
 * Time: 16:09
 */
public class InternalModel {

    public static final String INTERNAL_NAMESPACE_URI = "http://www.it.ru/logicECM/internal/1.0";

    public static final QName TYPE_INTERNAL = QName.createQName(INTERNAL_NAMESPACE_URI, "document");
    public static final QName ASSOC_RECIPIENT = QName.createQName(INTERNAL_NAMESPACE_URI, "recipient-assoc");
}
