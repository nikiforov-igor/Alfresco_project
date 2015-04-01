package ru.it.lecm.events.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 25.03.2015
 * Time: 14:43
 */
public interface EventsService {
    public static final String EVENTS_ROOT_ID = "EVENTS_ROOT_ID";

    public static final String EVENTS_NAMESPACE_URI = "http://www.it.ru/logicECM/events/1.0";
    public static final String EVENTS_DIC_NAMESPACE_URI = "http://www.it.ru/logicECM/events/dictionaries/1.0";
    public static final String EVENTS_TS_NAMESPACE_URI = "http://www.it.ru/logicECM/events/table-structure/1.0";

    public static final QName TYPE_EVENT = QName.createQName(EVENTS_NAMESPACE_URI, "document");
    public static final QName PROP_EVENT_TITLE = QName.createQName(EVENTS_NAMESPACE_URI, "title");
    public static final QName PROP_EVENT_FROM_DATE = QName.createQName(EVENTS_NAMESPACE_URI, "from-date");
    public static final QName PROP_EVENT_TO_DATE = QName.createQName(EVENTS_NAMESPACE_URI, "to-date");
    public static final QName PROP_EVENT_ALL_DAY = QName.createQName(EVENTS_NAMESPACE_URI, "all-day");
    public static final QName PROP_EVENT_DESCRIPTION = QName.createQName(EVENTS_NAMESPACE_URI, "description");

    public static final QName ASSOC_EVENT_LOCATION = QName.createQName(EVENTS_NAMESPACE_URI, "location-assoc");
    public static final QName ASSOC_EVENT_TEMP_MEMBERS = QName.createQName(EVENTS_NAMESPACE_URI, "temp-members-assoc");
    public static final QName ASSOC_EVENT_TEMP_RESOURCES = QName.createQName(EVENTS_NAMESPACE_URI, "temp-resources-assoc");

    public static final QName TYPE_EVENT_LOCATION = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "locations");

    public static final QName TYPE_EVENT_MEMBERS_TABLE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "membersTable");
    public static final QName TYPE_EVENT_MEMBERS_TABLE_ROW = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members");
    public static final QName TYPE_EVENT_RESOURCES_TABLE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "resourcesTable");
    public static final QName TYPE_EVENT_RESOURCES_TABLE_ROW = QName.createQName(EVENTS_TS_NAMESPACE_URI, "resources");

    public static final QName ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-employee-assoc");
    public static final QName ASSOC_EVENT_RESOURCES_TABLE_RESOURCE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "resources-data-assoc");

    NodeRef getEventLocation(NodeRef event);
}
