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
    public static final QName PROP_EVENT_REPEATABLE = QName.createQName(EVENTS_NAMESPACE_URI, "repeatable");
    public static final QName PROP_EVENT_REPEATABLE_RULE = QName.createQName(EVENTS_NAMESPACE_URI, "repeatable-rule");
    public static final QName PROP_EVENT_REPEATABLE_START_PERIOD = QName.createQName(EVENTS_NAMESPACE_URI, "repeatable-start-period");
    public static final QName PROP_EVENT_REPEATABLE_END_PERIOD = QName.createQName(EVENTS_NAMESPACE_URI, "repeatable-end-period");
    public static final QName PROP_EVENT_IS_REPEATED = QName.createQName(EVENTS_NAMESPACE_URI, "is-repeated");
    public static final QName PROP_EVENT_MEMBERS_MANDATORY_JSON = QName.createQName(EVENTS_NAMESPACE_URI, "temp-members-assoc-json");
    public static final QName PROP_EVENT_SEND_NOTIFICATIONS = QName.createQName(EVENTS_NAMESPACE_URI, "send-notifications");
    public static final QName PROP_EVENT_SHOW_IN_CALENDAR= QName.createQName(EVENTS_NAMESPACE_URI, "show-in-calendar");

    public static final QName ASSOC_EVENT_LOCATION = QName.createQName(EVENTS_NAMESPACE_URI, "location-assoc");
    public static final QName ASSOC_EVENT_INITIATOR = QName.createQName(EVENTS_NAMESPACE_URI, "initiator-assoc");
    public static final QName ASSOC_EVENT_INVITED_MEMBERS = QName.createQName(EVENTS_NAMESPACE_URI, "invited-members-assoc");
    public static final QName ASSOC_EVENT_TEMP_MEMBERS = QName.createQName(EVENTS_NAMESPACE_URI, "temp-members-assoc");
    public static final QName ASSOC_EVENT_TEMP_RESOURCES = QName.createQName(EVENTS_NAMESPACE_URI, "temp-resources-assoc");
    public static final QName ASSOC_EVENT_REPEATED_EVENTS = QName.createQName(EVENTS_NAMESPACE_URI, "repeated-events-assoc");
    public static final QName ASSOC_NEXT_REPEATED_EVENT = QName.createQName(EVENTS_NAMESPACE_URI, "next-repeated-event-assoc");
    public static final QName ASSOC_EVENT_OLD_MEMBERS = QName.createQName(EVENTS_NAMESPACE_URI, "old-members-assoc");
    public static final QName ASSOC_EVENT_SUBJECT = QName.createQName(EVENTS_NAMESPACE_URI, "subject-assoc");

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

    public static final QName PROP_EVENT_MEMBERS_STATUS = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-status");
    public static final QName PROP_EVENT_MEMBERS_DECLINE_REASON = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-decline-reason");
    public static final QName PROP_EVENT_MEMBERS_FROM_DATE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-from-date");
    public static final QName PROP_EVENT_MEMBERS_TO_DATE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-to-date");
    public static final QName PROP_EVENT_MEMBERS_ALL_DAY = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-all-day");
    public static final QName PROP_EVENT_MEMBERS_PARTICIPATION_REQUIRED = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-participation-required");
    public static final QName ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-employee-assoc");
    public static final QName ASSOC_EVENT_RESOURCES_TABLE_RESOURCE = QName.createQName(EVENTS_TS_NAMESPACE_URI, "resources-data-assoc");
    public static final QName ASSOC_EVENT_MEMBERS = QName.createQName(EVENTS_TS_NAMESPACE_URI, "members-assoc");

    public static final String EVENT_LINK_URL = "/share/page/event";

    public static final String EVENTS_ENGINEER_ROLE = "EVENTS_ENGINEER";

    List<NodeRef> getEvents(String fromDate, String toDate);

    public List<NodeRef> getEvents(String fromDate, String toDate, String additionalFilter);

    List<NodeRef> getEvents(String fromDate, String toDate, String additionalFilter, boolean excludeDeclined);

    public List<NodeRef> searchEvents(String filter);

    NodeRef getEventLocation(NodeRef event);

    List<NodeRef> getEventMembers(NodeRef event);

    List<NodeRef> getEventInvitedMembers(NodeRef event);

    List<NodeRef> getEventResources(NodeRef event);

    List<NodeRef> getNearestEvents(String fromDate, int maxCount, String additionalFilter);

    List<NodeRef> getAvailableUserLocations(String fromDate, String toDate, NodeRef ignoreNode);

    List<NodeRef> getAvailableUserResources();

    boolean checkLocationAvailable(NodeRef location, Date fromDate, Date toDate, boolean allDay);
    boolean checkLocationAvailable(NodeRef location, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay);

    boolean checkMemberAvailable(NodeRef member, Date fromDate, Date toDate, boolean allDay);
    boolean checkMemberAvailable(NodeRef member, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay);

    List<NodeRef> getResourceResponsible(NodeRef resource);

    NodeRef getEventInitiator(NodeRef event);

    NodeRef getMemberTable(NodeRef event);

    NodeRef getMemberTableRow(NodeRef event, NodeRef employee);

    NodeRef getResourceTableRow(NodeRef event, NodeRef employee);

    String getCurrentEmployeeMemberStatus(NodeRef event);

    String getEmployeeMemberStatus(NodeRef event, NodeRef employee);

    String wrapAsEventLink(NodeRef documentRef);

    void onAfterUpdate(NodeRef event, String updateRepeated);

    void onAfterUpdate(NodeRef event, String updateRepeated, boolean sendToInvitedMembers);

    List<NodeRef> getNextRepeatedEvents(NodeRef event);

    List<NodeRef> getPrevRepeatedEvents(NodeRef event);

    List<NodeRef> getAllRepeatedEvents(NodeRef event);

	void sendNotificationsToInvitedMembers(NodeRef event, Boolean isFirst);
	
	void sendCancelNotifications(NodeRef event);

	void sendIcalNotificationsToInvitedMembers(NodeRef event, boolean firstTime);

	void addUpdateType(QName TYPE_MEETINGS_DOCUMENT, List<QName> propertiesToCopy, List<QName> assocsToCopy);

    public String getAdditionalFilterForCalendarShow();
}
