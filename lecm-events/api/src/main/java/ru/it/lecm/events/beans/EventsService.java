package ru.it.lecm.events.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.Date;
import java.util.List;

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
    public static final QName ASSOC_EVENT_INITIATOR = QName.createQName(EVENTS_NAMESPACE_URI, "initiator-assoc");
    public static final QName ASSOC_EVENT_INVITED_MEMBERS = QName.createQName(EVENTS_NAMESPACE_URI, "invited-members-assoc");
    public static final QName ASSOC_EVENT_TEMP_MEMBERS = QName.createQName(EVENTS_NAMESPACE_URI, "temp-members-assoc");
    public static final QName ASSOC_EVENT_TEMP_RESOURCES = QName.createQName(EVENTS_NAMESPACE_URI, "temp-resources-assoc");

    public static final QName TYPE_EVENT_LOCATION = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "locations");
    public static final QName ASSOC_EVENT_LOCATION_ORGANIZATION = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "locations-organization-assoc");
    public static final QName PROP_EVENT_LOCATION_PRIVILEGE_LEVEL = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "locations-privilege-level");
    public static final QName PROP_EVENT_LOCATION_ADDRESS = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "locations-address");
    public static final QName ASSOC_EVENT_LOCATION_PL_ROLE = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "locations-pl-br-assoc");
    public static final QName PROP_EVENT_LOCATION_PL_LEVEL = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "locations-pl-level");

    public static final QName ASSOC_EVENT_RESOURCE_ORGANIZATION = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "resources-organization-assoc");
    public static final QName ASSOC_EVENT_RESOURCE_RESPONSIBLE = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "resources-responsible-assoc");
    public static final QName PROP_EVENT_RESOURCE_PRIVILEGE_LEVEL = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "resources-privilege-level");
    public static final QName PROP_EVENT_RESOURCE_AVAILABLE = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "resources-available");
    public static final QName ASSOC_EVENT_RESOURCES_PL_ROLE = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "resources-pl-br-assoc");
    public static final QName PROP_EVENT_RESOURCES_PL_LEVEL = QName.createQName(EVENTS_DIC_NAMESPACE_URI, "resources-pl-level");

    public static final QName TYPE_EVENT_MEMBERS_TABLE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "membersTable");
    public static final QName TYPE_EVENT_MEMBERS_TABLE_ROW = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members");
    public static final QName TYPE_EVENT_RESOURCES_TABLE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "resourcesTable");
    public static final QName TYPE_EVENT_RESOURCES_TABLE_ROW = QName.createQName(EVENTS_TS_NAMESPACE_URI, "resources");

    public static final QName ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-employee-assoc");
    public static final QName ASSOC_EVENT_RESOURCES_TABLE_RESOURCE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "resources-data-assoc");

    List<NodeRef> getEvents(String fromDate, String toDate);

    public List<NodeRef> getEvents(String fromDate, String toDate, String additionalFilter);

    NodeRef getEventLocation(NodeRef event);

    List<NodeRef> getAvailableUserLocations();

    List<NodeRef> getAvailableUserResources();

    boolean checkLocationAvailable(NodeRef location, Date fromDate, Date toDate, boolean allDay);

    boolean checkMemberAvailable(NodeRef member, Date fromDate, Date toDate, boolean allDay);

    List<NodeRef> getResourceResponsible(NodeRef resource);

    NodeRef getEventInitiator(NodeRef event);
}
