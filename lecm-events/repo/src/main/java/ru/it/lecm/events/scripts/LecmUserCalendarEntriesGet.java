package ru.it.lecm.events.scripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.ISO8601DateFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.events.beans.EventsService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin
 * Date: 26.03.2015
 * Time: 17:06
 */
public class LecmUserCalendarEntriesGet extends DeclarativeWebScript {
    private SearchService searchService;
    protected NodeService nodeService;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        String fromDate = req.getParameter("from");
        String toDate = req.getParameter("to");

        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        String query = "TYPE:\"lecm-events:document\" AND lecm\\-events:from\\-date:[" + fromDate + " TO " + toDate + "] AND lecm\\-events:to\\-date:[" + fromDate + " TO " + toDate + "]";
        sp.setQuery(query);
//        sp.addSort("@" + ContentModel.PROP_NAME, true);
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSet searchResult = null;
        try {
            searchResult = searchService.query(sp);
            for (ResultSetRow row : searchResult) {
                NodeRef entry = row.getNodeRef();

                // Build the object
                Map<String, Object> result = new HashMap<>();
                boolean isAllDay = (boolean) nodeService.getProperty(entry, EventsService.PROP_EVENT_ALL_DAY);
                String title = (String) nodeService.getProperty(entry, EventsService.PROP_EVENT_TITLE);
                boolean removeTimezone = isAllDay;
                Date start = (Date) nodeService.getProperty(entry, EventsService.PROP_EVENT_FROM_DATE);
                Date end = (Date) nodeService.getProperty(entry, EventsService.PROP_EVENT_TO_DATE);

                result.put("nodeRef", entry.toString());
                result.put("title", title);
                result.put("description", nodeService.getProperty(entry, EventsService.PROP_EVENT_DESCRIPTION));
                result.put("allday", isAllDay);
                result.put("start", removeTimeZoneIfRequired(start, isAllDay, removeTimezone));
                result.put("end", removeTimeZoneIfRequired(end, isAllDay, removeTimezone));
                String legacyDateFormat = "yyyy-MM-dd";
                String legacyTimeFormat = "HH:mm";
                result.put("legacyDateFrom", removeTimeZoneIfRequired(start, isAllDay, removeTimezone, legacyDateFormat));
                result.put("legacyTimeFrom", removeTimeZoneIfRequired(start, isAllDay, removeTimezone, legacyTimeFormat));
                result.put("legacyDateTo", removeTimeZoneIfRequired(end, isAllDay, removeTimezone, legacyDateFormat));
                result.put("legacyTimeTo", removeTimeZoneIfRequired(end, isAllDay, removeTimezone, legacyTimeFormat));
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
        } finally {
            if (searchResult != null) {
                searchResult.close();
            }
        }

        // All done
        Map<String, Object> model = new HashMap<>();
        model.put("events", results);
        return model;
    }

    private static final String ALL_DAY_DATETIME_PATTERN = "yyyy-MM-dd'T00:00:00.000'";
    private static final DateTimeFormatter ALL_DAY_DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T00:00:00.000'");


    protected String removeTimeZoneIfRequired(Date date, Boolean isAllDay, Boolean removeTimezone) {
        return removeTimeZoneIfRequired(date, isAllDay, removeTimezone, null);
    }

    protected String removeTimeZoneIfRequired(Date date, Boolean isAllDay, Boolean removeTimezone, String datePattern) {
        if (removeTimezone) {
            DateTime dateTime = new DateTime(date, DateTimeZone.UTC);

            if (null == datePattern) {
                return dateTime.toString((isAllDay) ? (ALL_DAY_DATETIME_FORMATTER) : (ISODateTimeFormat.dateTime()));
            } else {
                // For Legacy Dates and Times.
                return dateTime.toString(DateTimeFormat.forPattern(datePattern));
            }
        }

        // This is for all other cases, including the case, when UTC time zone is configured

        if (!isAllDay && (null == datePattern)) {
            return ISO8601DateFormat.format(date);
        }

        DateFormat simpleDateFormat = new SimpleDateFormat((null != datePattern) ? (datePattern) : (ALL_DAY_DATETIME_PATTERN));

        return simpleDateFormat.format(date);
    }
}