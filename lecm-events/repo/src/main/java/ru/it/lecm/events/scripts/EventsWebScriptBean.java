package ru.it.lecm.events.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

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

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setEventService(EventsService eventService) {
        this.eventService = eventService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public List<Map<String, Object>> getUserEvents(String fromDate, String toDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<NodeRef> events = eventService.getEvents(fromDate, toDate);
        for (NodeRef entry : events) {
            // Build the object
            Map<String, Object> result = new HashMap<>();
            boolean isAllDay = (boolean) nodeService.getProperty(entry, EventsService.PROP_EVENT_ALL_DAY);
            String title = (String) nodeService.getProperty(entry, EventsService.PROP_EVENT_TITLE);
            Date start = (Date) nodeService.getProperty(entry, EventsService.PROP_EVENT_FROM_DATE);
            Date end = (Date) nodeService.getProperty(entry, EventsService.PROP_EVENT_TO_DATE);

            result.put("nodeRef", entry.toString());
            result.put("title", title);
            result.put("description", nodeService.getProperty(entry, EventsService.PROP_EVENT_DESCRIPTION));
            result.put("allday", isAllDay);

            result.put("members", eventService.getEventMembers(entry));
            result.put("invitedMembers", eventService.getEventInvitedMembers(entry));

            NodeRef location = eventService.getEventLocation(entry);
            if (location != null) {
                result.put("where", nodeService.getProperty(location, ContentModel.PROP_NAME));
            } else {
                result.put("where", "");
            }
            result.put("start", formatDate(start, isAllDay));
            result.put("end", formatDate(end, isAllDay));
            String legacyDateFormat = "yyyy-MM-dd";
            String legacyTimeFormat = "HH:mm";
            result.put("legacyDateFrom", formatDate(start, isAllDay, legacyDateFormat));
            result.put("legacyTimeFrom", formatDate(start, isAllDay, legacyTimeFormat));
            result.put("legacyDateTo", formatDate(end, isAllDay, legacyDateFormat));
            result.put("legacyTimeTo", formatDate(end, isAllDay, legacyTimeFormat));
            // Check the permissions the user has on the entry
//                AccessStatus canEdit = permissionService.hasPermission(entry.getNodeRef(), PermissionService.WRITE);
//                AccessStatus canDelete = permissionService.hasPermission(entry.getNodeRef(), PermissionService.DELETE);
            result.put("canEdit", true);
            result.put("canDelete", true);

            // Replace nulls with blank strings for the JSON
            for (String key : result.keySet()) {
                if (result.get(key) == null) {
                    result.put(key, "");
                }
            }

            // Save this one
            results.add(result);
        }

        return results;
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

    public Scriptable getAvailableUserLocations() {
        List<NodeRef> results = eventService.getAvailableUserLocations();
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
}
