package ru.it.lecm.events.beans;

import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 25.03.2015
 * Time: 14:43
 */
public interface EventsService {
    public static final String EVENTS_ROOT_ID = "EVENTS_ROOT_ID";

    public static final String EVENTS_NAMESPACE_URI = "http://www.it.ru/logicECM/events/1.0";
    public static final QName TYPE_EVENT = QName.createQName(EVENTS_NAMESPACE_URI, "document");
    public static final QName PROP_EVENT_TITLE = QName.createQName(EVENTS_NAMESPACE_URI, "title");
    public static final QName PROP_EVENT_FROM_DATE = QName.createQName(EVENTS_NAMESPACE_URI, "from-date");
    public static final QName PROP_EVENT_TO_DATE = QName.createQName(EVENTS_NAMESPACE_URI, "to-date");
    public static final QName PROP_EVENT_ALL_DAY = QName.createQName(EVENTS_NAMESPACE_URI, "all-day");
    public static final QName PROP_EVENT_DESCRIPTION = QName.createQName(EVENTS_NAMESPACE_URI, "description");
}
