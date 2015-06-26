package ru.it.lecm.events.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.actions.bean.GroupActionsService;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin
 * Date: 30.03.2015
 * Time: 15:17
 */
public class EventsWebScriptBean extends BaseWebScript {
    private NodeService nodeService;
    private EventsService eventService;
    private OrgstructureBean orgstructureBean;
    private DictionaryService dictionaryService;
    private GroupActionsService actionsService;
    private LecmPermissionService lecmPermissionService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setEventService(EventsService eventService) {
        this.eventService = eventService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setActionsService(GroupActionsService actionsService) {
        this.actionsService = actionsService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public List<Map<String, Object>> getUserEvents(String fromDate, String toDate) {
        return getUserEvents(fromDate, toDate, false, null);
    }

    public List<Map<String, Object>> getUserEvents(String fromDate, String toDate, boolean loadActions, String mode) {
        List<NodeRef> events = eventService.getEvents(fromDate, toDate, eventService.getAdditionalFilterForCalendarShow());
        return processEvents(events, loadActions, true, mode);
    }

    private List<Map<String, Object>> processEvents( List<NodeRef> events, boolean loadActions, boolean excludeDeclined, String mode) {
        List<Map<String, Object>> results = new ArrayList<>();
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        String legacyDateFormat = "yyyy-MM-dd";
        String legacyTimeFormat = "HH:mm";
        boolean isMini = "mini".equalsIgnoreCase(mode);
        boolean isFull = "full".equalsIgnoreCase(mode);
        for (NodeRef entry : events) {
            String memberStatus = eventService.getEmployeeMemberStatus(entry, currentEmployee);
            if (excludeDeclined && "DECLINED".equals(memberStatus)) {
                continue;
            }
            // Build the object
            Map<String, Object> result = new HashMap<>();
            boolean isAllDay = (boolean) nodeService.getProperty(entry, EventsService.PROP_EVENT_ALL_DAY);
            Date start = (Date) nodeService.getProperty(entry, EventsService.PROP_EVENT_FROM_DATE);
            Date end = (Date) nodeService.getProperty(entry, EventsService.PROP_EVENT_TO_DATE);
            result.put("start", formatDate(start, isAllDay));
            result.put("startDate", start);
            result.put("end", formatDate(end, isAllDay));
            result.put("legacyDateFrom", formatDate(start, isAllDay, legacyDateFormat));
            result.put("legacyTimeFrom", formatDate(start, isAllDay, legacyTimeFormat));
            result.put("legacyDateTo", formatDate(end, isAllDay, legacyDateFormat));
            result.put("legacyTimeTo", formatDate(end, isAllDay, legacyTimeFormat));
            if (!isMini) {
                String title = (String) nodeService.getProperty(entry, EventsService.PROP_EVENT_TITLE);
                result.put("nodeRef", entry.toString());
                result.put("title", title);
                result.put("description", nodeService.getProperty(entry, EventsService.PROP_EVENT_DESCRIPTION));
                result.put("allday", isAllDay);

                NodeRef location = eventService.getEventLocation(entry);
                if (location != null) {
                    result.put("where", nodeService.getProperty(location, ContentModel.PROP_NAME));
                } else {
                    result.put("where", "");
                }

                result.put("userMemberStatus", memberStatus);
                result.put("userIsInitiator", eventService.getEventInitiator(entry).equals(currentEmployee));

                String typeTitle = "";
                TypeDefinition typeDef = dictionaryService.getType(nodeService.getType(entry));
                if (typeDef != null) {
                    typeTitle = typeDef.getTitle(dictionaryService);
                }
                result.put("typeTitle", typeTitle);

                // Check the permissions the user has on the entry
//                AccessStatus canEdit = permissionService.hasPermission(entry.getNodeRef(), PermissionService.WRITE);
//                AccessStatus canDelete = permissionService.hasPermission(entry.getNodeRef(), PermissionService.DELETE);
                result.put("canEdit", lecmPermissionService.hasPermission(LecmPermissionService.PERM_ATTR_EDIT, entry));
                result.put("canDelete", true);
                if (!isFull) {
                    result.put("members", eventService.getEventMembers(entry));
                    result.put("invitedMembers", eventService.getEventInvitedMembers(entry));
                    result.put("actions", loadActions ? actionsService.getActiveActions(entry) : Collections.EMPTY_LIST);
                }
            }
            // Replace nulls with blank strings for the JSON
            for (String key : result.keySet()) {
                if (result.get(key) == null) {
                    result.put(key, "");
                }
            }

            // Save this one
            results.add(result);
        }
        Collections.sort(results, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Date date1 = (Date) o1.get("startDate");
                Date date2 = (Date) o2.get("startDate");
                if (date1.before(date2)) {
                    return -1;
                } else if (date1.after(date2)) {
                    return 1;
                } else {
                    return 0;
                }

            }
        });
        return results;
    }

    public List<Map<String, Object>> searchUserEvents(String filter) {
        List<NodeRef> events;
        if (filter.length() > 0) {
            events = eventService.searchEvents(filter + eventService.getAdditionalFilterForCalendarShow());
        } else {
            events = new ArrayList<>();
        }

        return processEvents(events, false, false, null);
    }

    public Scriptable getUserNearestEvents(int maxItems) {
        return createScriptable(eventService.getNearestEvents(formatDate(new Date(), false), maxItems, eventService.getAdditionalFilterForCalendarShow()));
    }

    private String formatDate(Date date, Boolean isAllDay) {
        return formatDate(date, isAllDay, null);
    }

    private String formatDate(Date date, Boolean isAllDay, String datePattern) {
        String ALL_DAY_DATETIME_PATTERN = "yyyy-MM-dd'T00:00:00.000'";

        if (!isAllDay && (null == datePattern)) {
            return ISO8601DateFormat.format(date);
        }

        DateFormat simpleDateFormat = new SimpleDateFormat((null != datePattern) ? (datePattern) : (ALL_DAY_DATETIME_PATTERN));

        return simpleDateFormat.format(date);
    }

    public Scriptable getAvailableUserLocations(String fromDate, String toDate, String ignoreNode) {
        NodeRef ignoreNodeRef = null;
        if (ignoreNode != null) {
            ignoreNodeRef = new NodeRef(ignoreNode);
        }

        List<NodeRef> results = eventService.getAvailableUserLocations(fromDate, toDate, ignoreNodeRef);
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

    public Scriptable getAvailableUserResources() {
        List<NodeRef> results = eventService.getAvailableUserResources();
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

    public Scriptable getEventMembers(String event) {
        ParameterCheck.mandatory("event", event);

        NodeRef eventRef = new NodeRef(event);
        if (this.nodeService.exists(eventRef)) {
            List<NodeRef> results = eventService.getEventMembers(eventRef);
            if (results != null) {
                return createScriptable(results);
            }
        }
        return null;
    }

    public boolean checkLocationAvailable(String location, String ignoreNode,  String fromDate, String toDate, boolean allDay) {
        ParameterCheck.mandatory("location", location);
        ParameterCheck.mandatory("fromDate", fromDate);
        ParameterCheck.mandatory("toDate", toDate);
        ParameterCheck.mandatory("allDay", allDay);

        NodeRef locationRef = new NodeRef(location);
        if (this.nodeService.exists(locationRef)) {
            NodeRef ignoreNodeRef = null;
            if (ignoreNode != null) {
                ignoreNodeRef = new NodeRef(ignoreNode);
            }

            return eventService.checkLocationAvailable(locationRef, ignoreNodeRef, ISO8601DateFormat.parse(fromDate), ISO8601DateFormat.parse(toDate), allDay);
        }

        return false;
    }

    public boolean checkMemberAvailable(String member, String ignoreNode, String fromDate, String toDate, boolean allDay) {
        ParameterCheck.mandatory("member", member);
        ParameterCheck.mandatory("fromDate", fromDate);
        ParameterCheck.mandatory("toDate", toDate);
        ParameterCheck.mandatory("allDay", allDay);

        NodeRef memberRef = new NodeRef(member);
        if (this.nodeService.exists(memberRef)) {
            NodeRef ignoreNodeRef = null;
            if (ignoreNode != null) {
                ignoreNodeRef = new NodeRef(ignoreNode);
            }

            return eventService.checkMemberAvailable(memberRef, ignoreNodeRef, ISO8601DateFormat.parse(fromDate), ISO8601DateFormat.parse(toDate), allDay);
        }

        return false;
    }

    public ScriptNode getCurrentEmployeeEventMemberRow(ScriptNode event) {
        ParameterCheck.mandatory("event", event);

        NodeRef row = eventService.getMemberTableRow(event.getNodeRef(), orgstructureBean.getCurrentEmployee());

        if (row != null) {
            return new ScriptNode(row, serviceRegistry, getScope());
        }

        return null;
    }

    public ScriptNode getEmployeeEventMemberRow(String event, String member) {
        ParameterCheck.mandatory("event", event);
        ParameterCheck.mandatory("member", member);

        NodeRef row = eventService.getMemberTableRow(new NodeRef(event), new NodeRef(member));

        if (row != null) {
            return new ScriptNode(row, serviceRegistry, getScope());
        }

        return null;
    }

    public Scriptable getEventMembers(ScriptNode event) {
        return createScriptable(eventService.getEventMembers(event.getNodeRef()));
    }

    public Scriptable getEventInvitedMembers(ScriptNode event) {
        return createScriptable(eventService.getEventInvitedMembers(event.getNodeRef()));
    }

    public Scriptable getEventResources(ScriptNode event) {
        return createScriptable(eventService.getEventResources(event.getNodeRef()));
    }

	public Scriptable getResourceResponsible(ScriptNode resource) {
		return createScriptable(eventService.getResourceResponsible(resource.getNodeRef()));
	}
	
    public String wrapperEventLink(ScriptNode node, String description) {
        return wrapperLink(node.getNodeRef().toString(), description, EventsService.EVENT_LINK_URL);
    }

    public void onAfterUpdate(String event, String updateRepeated) {
        ParameterCheck.mandatory("event", event);
        ParameterCheck.mandatory("updateRepeated", updateRepeated);

        NodeRef eventRef = new NodeRef(event);
        if (this.nodeService.exists(eventRef)) {
            eventService.onAfterUpdate(eventRef, updateRepeated);
        }
    }

    public void onAfterUpdate(ScriptNode event, String updateRepeated, boolean sendToInvitedMembers) {
        ParameterCheck.mandatory("event", event);
        ParameterCheck.mandatory("sendToInvitedMembers", sendToInvitedMembers);

        if (this.nodeService.exists(event.getNodeRef())) {
            eventService.onAfterUpdate(event.getNodeRef(), updateRepeated, sendToInvitedMembers);
        }
    }
	
	public void notifyInvitedMembers(ScriptNode event, boolean firstTime) {
		ParameterCheck.mandatory("event", event);
		ParameterCheck.mandatory("firstTime", firstTime);
		eventService.sendNotificationsToInvitedMembers(event.getNodeRef(), firstTime);
	}
	
	public void sendCancelNotifications(ScriptNode event) {
		ParameterCheck.mandatory("event", event);
		eventService.notifyEventCncelled(event.getNodeRef());
	}


    public Scriptable getRepetableEvents(ScriptNode event, String filterType) {
        List<NodeRef> repeatableEvents = new ArrayList<>();
        if ("ALL".equals(filterType)) {
            repeatableEvents = eventService.getAllRepeatedEvents(event.getNodeRef());
        } else if ("ALL_NEXT".equals(filterType)) {
            repeatableEvents = eventService.getNextRepeatedEvents(event.getNodeRef());
        } else if ("ALL_PREV".equals(filterType)) {
            repeatableEvents = eventService.getPrevRepeatedEvents(event.getNodeRef());
        }
        return createScriptable(repeatableEvents);
    }
}
