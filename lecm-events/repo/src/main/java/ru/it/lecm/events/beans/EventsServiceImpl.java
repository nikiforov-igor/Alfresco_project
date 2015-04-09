package ru.it.lecm.events.beans;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin
 * Date: 25.03.2015
 * Time: 14:44
 */
public class EventsServiceImpl extends BaseBean implements EventsService {
    private DictionaryBean dictionaryBean;
    private OrgstructureBean orgstructureBean;
    private SearchService searchService;
    private IWorkCalendar workCalendarService;
    private DocumentTableService documentTableService;

    final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(EVENTS_ROOT_ID);
    }

    @Override
    public NodeRef getEventLocation(NodeRef event) {
        return findNodeByAssociationRef(event, ASSOC_EVENT_LOCATION, TYPE_EVENT_LOCATION, ASSOCIATION_TYPE.TARGET);
    }

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setWorkCalendarService(IWorkCalendar workCalendarService) {
        this.workCalendarService = workCalendarService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public List<NodeRef> getEvents(String fromDate, String toDate) {
        return getEvents(fromDate, toDate, "");
    }

    public List<NodeRef> getEvents(String fromDate, String toDate, String additionalFilter) {
        List<NodeRef> results = new ArrayList<>();

        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
//        sp.addSort("@" + ContentModel.PROP_NAME, true);
        String query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[MIN TO " + toDate + "] AND @lecm\\-events\\:to\\-date:[" + fromDate + " TO MAX]" + additionalFilter;
        sp.setQuery(query);

        ResultSet searchResult = null;
        try {
            searchResult = searchService.query(sp);
            for (ResultSetRow row : searchResult) {
                results.add(row.getNodeRef());
            }
        } finally {
            if (searchResult != null) {
                searchResult.close();
            }
        }

        return results;
    }

    @Override
    public List<NodeRef> getAvailableUserLocations() {
        List<NodeRef> results = null;
        NodeRef locationsDic = dictionaryBean.getDictionaryByName("Места проведения мероприятий");
        if (locationsDic != null) {
            results = new ArrayList<>();

            List<NodeRef> locations = dictionaryBean.getChildren(locationsDic);
            NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();
            int currentUserLocationPrivilegeLevel = getCurrentUserLocationPrivilegeLevel();

            for (NodeRef location : locations) {
                NodeRef locationOrganization = findNodeByAssociationRef(location, ASSOC_EVENT_LOCATION_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
                Integer locationPrivilegeLevel = (Integer) nodeService.getProperty(location, PROP_EVENT_LOCATION_PRIVILEGE_LEVEL);

                if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(locationOrganization) &&
                        (locationPrivilegeLevel == 0 || currentUserLocationPrivilegeLevel >= locationPrivilegeLevel)) {
                    results.add(location);
                }
            }
        }
        return results;
    }

    @Override
    public List<NodeRef> getAvailableUserResources() {
        List<NodeRef> results = null;
        NodeRef resourcesDic = dictionaryBean.getDictionaryByName("Ресурсы");
        if (resourcesDic != null) {
            results = new ArrayList<>();

            List<NodeRef> resources = dictionaryBean.getChildren(resourcesDic);
            NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();
            int currentUserResourcesPrivilegeLevel = getCurrentUserResourcesPrivilegeLevel();

            for (NodeRef resource : resources) {
                if ((Boolean) nodeService.getProperty(resource, PROP_EVENT_RESOURCE_AVAILABLE)) {
                    NodeRef resourceOrganization = findNodeByAssociationRef(resource, ASSOC_EVENT_RESOURCE_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
                    Integer resourcePrivilegeLevel = (Integer) nodeService.getProperty(resource, PROP_EVENT_RESOURCE_PRIVILEGE_LEVEL);

                    if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(resourceOrganization) &&
                            (resourcePrivilegeLevel == 0 || currentUserResourcesPrivilegeLevel >= resourcePrivilegeLevel)) {
                        results.add(resource);
                    }
                }
            }
        }
        return results;
    }

    private NodeRef getCurrentEmployeeOrganization() {
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        if (currentEmployee != null) {
            return orgstructureBean.getEmployeeOrganization(currentEmployee);
        }
        return null;
    }

    private int getCurrentUserLocationPrivilegeLevel() {
        return getCurrentUserDicPrivilegeLevel("Уровни привилегий для выбора мест проведения", ASSOC_EVENT_LOCATION_PL_ROLE, PROP_EVENT_LOCATION_PL_LEVEL);
    }

    private int getCurrentUserResourcesPrivilegeLevel() {
        return getCurrentUserDicPrivilegeLevel("Уровни привилегий для выбора ресурсов", ASSOC_EVENT_RESOURCES_PL_ROLE, PROP_EVENT_RESOURCES_PL_LEVEL);
    }

    private int getCurrentUserDicPrivilegeLevel(String dicName, QName dicRoleAssoc, QName dicLevelProp) {
        int result = 0;
        NodeRef privilegeLevelDic = dictionaryBean.getDictionaryByName(dicName);
        if (privilegeLevelDic != null) {
            List<NodeRef> privilegeLevels = dictionaryBean.getChildren(privilegeLevelDic);
            for (NodeRef privilegeLevel : privilegeLevels) {
                NodeRef role = findNodeByAssociationRef(privilegeLevel, dicRoleAssoc, null, ASSOCIATION_TYPE.TARGET);
                if (role != null) {
                    String roleId = orgstructureBean.getBusinessRoleIdentifier(role);
                    if (roleId != null && orgstructureBean.isCurrentEmployeeHasBusinessRole(roleId)) {
                        Integer level = (Integer) nodeService.getProperty(privilegeLevel, dicLevelProp);
                        if (level != null && level > result) {
                            result = level;
                        }
                    }
                }
            }
        }
        return result;
    }

    public boolean checkLocationAvailable(NodeRef location, Date fromDate, Date toDate, boolean allDay) {
        if (allDay) {
            Calendar fromDateCal = Calendar.getInstance();
            fromDateCal.setTime(fromDate);
            fromDateCal.set(Calendar.HOUR_OF_DAY, 0);
            fromDateCal.set(Calendar.MINUTE, 1);
            fromDateCal.set(Calendar.SECOND, 0);
            fromDateCal.set(Calendar.MILLISECOND, 0);
            fromDate = fromDateCal.getTime();

            Calendar toDateCal = Calendar.getInstance();
            toDateCal.setTime(toDate);
            toDateCal.set(Calendar.HOUR_OF_DAY, 23);
            toDateCal.set(Calendar.MINUTE, 59);
            toDateCal.set(Calendar.SECOND, 0);
            toDateCal.set(Calendar.MILLISECOND, 0);
            toDate = toDateCal.getTime();
        }

        String additionalFilter = " AND @lecm\\-events\\:location\\-assoc\\-ref:\"" + location.toString() + "\"";

        List<NodeRef> events = getEvents(DateFormatISO8601.format(fromDate), DateFormatISO8601.format(toDate), additionalFilter);

        return events == null || events.size() == 0;
    }

    public boolean checkMemberAvailable(NodeRef member, Date fromDate, Date toDate, boolean allDay) {
        List<Date> employeeWorkindDays = workCalendarService.getEmployeeWorkindDays(member, fromDate, toDate);
        if (employeeWorkindDays.size() == 0) {
            return false;
        } else {
            if (allDay) {
                Calendar fromDateCal = Calendar.getInstance();
                fromDateCal.setTime(fromDate);
                fromDateCal.set(Calendar.HOUR_OF_DAY, 0);
                fromDateCal.set(Calendar.MINUTE, 1);
                fromDateCal.set(Calendar.SECOND, 0);
                fromDateCal.set(Calendar.MILLISECOND, 0);
                fromDate = fromDateCal.getTime();

                Calendar toDateCal = Calendar.getInstance();
                toDateCal.setTime(toDate);
                toDateCal.set(Calendar.HOUR_OF_DAY, 23);
                toDateCal.set(Calendar.MINUTE, 59);
                toDateCal.set(Calendar.SECOND, 0);
                toDateCal.set(Calendar.MILLISECOND, 0);
                toDate = toDateCal.getTime();
            }

            String additionalFilter = " AND @lecm\\-events\\:temp\\-members\\-assoc\\-ref:\"" + member.toString() + "\"";

            List<NodeRef> events = getEvents(DateFormatISO8601.format(fromDate), DateFormatISO8601.format(toDate), additionalFilter);

            return events == null || events.size() == 0;
        }
    }

    public List<NodeRef> getResourceResponsible(NodeRef resource) {
        return findNodesByAssociationRef(resource, ASSOC_EVENT_RESOURCE_RESPONSIBLE, null, ASSOCIATION_TYPE.TARGET);
    }

    public NodeRef getEventInitiator(NodeRef event) {
        return findNodeByAssociationRef(event, ASSOC_EVENT_INITIATOR, null, ASSOCIATION_TYPE.TARGET);
    }

    public NodeRef getMemberTableRow(NodeRef event, NodeRef employee) {
        NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
        if (tableDataRootFolder != null) {
            Set<QName> typeSet = new HashSet<>(1);
            typeSet.add(EventsService.TYPE_EVENT_MEMBERS_TABLE);
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
            if (childAssocs != null && childAssocs.size() == 1) {
                NodeRef table = childAssocs.get(0).getChildRef();
                if (table != null) {
                    List<NodeRef> rows = documentTableService.getTableDataRows(table);
                    if (rows != null) {
                        for (NodeRef row: rows) {
                            NodeRef rowEmployee = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
                            if (rowEmployee.equals(employee)) {
                                return row;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getCurrentEmployeeMemberStatus(NodeRef event) {
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        if (currentEmployee != null) {
            NodeRef memberTableRow = getMemberTableRow(event, currentEmployee);
            if (memberTableRow != null) {
                return (String) nodeService.getProperty(memberTableRow, PROP_EVENT_MEMBERS_STATUS);
            }
        }
        return null;
    }

    public String wrapAsEventLink(NodeRef documentRef) {
        return wrapperLink(documentRef, (String) nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING), EVENT_LINK_URL);
    }
}
