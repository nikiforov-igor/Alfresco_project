package ru.it.lecm.events.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import org.alfresco.service.namespace.NamespaceService;

/**
 * User: AIvkin Date: 25.03.2015 Time: 14:44
 */
public class EventsServiceImpl extends BaseBean implements EventsService {

	private final static Logger logger = LoggerFactory.getLogger(EventsServiceImpl.class);

	private DictionaryBean dictionaryBean;
	private OrgstructureBean orgstructureBean;
	private SearchService searchService;
	private IWorkCalendar workCalendarService;
	private DocumentTableService documentTableService;
	private NotificationsService notificationsService;
	private SearchQueryProcessor organizationQueryProcessor;
	private LecmPermissionService lecmPermissionService;
	private NamespaceService namespaceService;
	
	private ThreadPoolExecutor threadPoolExecutor;

	private EventsNotificationsService eventsNotificationsService;
	
	private TemplateService templateService;
	private JavaMailSender mailService;
	private ContentService contentService;
	private String defaultFromEmail;

	//Уже есть в BaseBean
	//final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private final Map<QName, List<QName>> assocsToUpdateMap = new HashMap<>();
	private final Map<QName, List<QName>> propsToUpdateMap = new HashMap<>();

	private List<String> propsForFilterShowIncalendar = new ArrayList<>();

	public EventsNotificationsService getEventsNotificationsService() {
		return eventsNotificationsService;
	}

	public void setEventsNotificationsService(EventsNotificationsService eventsNotificationsService) {
		this.eventsNotificationsService = eventsNotificationsService;
	}

	@Override
	public Boolean getSendIcalToInvitedMembers() {
		return eventsNotificationsService.getSendIcalToInvitedMembers();
	}
	
	@Override
	public Boolean getSendIcalToMembers() {
		return eventsNotificationsService.getSendIcalToMembers();
	}


	public NamespaceService getNamespaceService() {
		return namespaceService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}
	
	public void init() {
		List<QName> propertiesToCopy = new ArrayList<>();
		propertiesToCopy.add(EventsService.PROP_EVENT_TITLE);
		propertiesToCopy.add(EventsService.PROP_EVENT_DESCRIPTION);
		propertiesToCopy.add(EventsService.PROP_EVENT_MEMBERS_MANDATORY_JSON);
		propertiesToCopy.add(PROP_EVENT_ALL_DAY);

		List<QName> assocsToCopy = new ArrayList<>();
		assocsToCopy.add(EventsService.ASSOC_EVENT_LOCATION);
		assocsToCopy.add(EventsService.ASSOC_EVENT_INITIATOR);
		assocsToCopy.add(EventsService.ASSOC_EVENT_INVITED_MEMBERS);
		assocsToCopy.add(EventsService.ASSOC_EVENT_SUBJECT);

		addUpdateType(TYPE_EVENT, propertiesToCopy, assocsToCopy);
	}

	@Override
	public void addUpdateType(QName type, List<QName> properties, List<QName> assocs) {
		if (null != type) {
			assocsToUpdateMap.put(type, assocs);
			propsToUpdateMap.put(type, properties);
		}
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public void setMailService(JavaMailSender mailService) {
		this.mailService = mailService;
	}

	public void setDefaultFromEmail(String defaultFromEmail) {
		this.defaultFromEmail = defaultFromEmail;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setOrganizationQueryProcessor(SearchQueryProcessor organizationQueryProcessor) {
		this.organizationQueryProcessor = organizationQueryProcessor;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(EVENTS_ROOT_ID);
	}

	@Override
	public NodeRef getEventLocation(NodeRef event) {
		return findNodeByAssociationRef(event, ASSOC_EVENT_LOCATION, TYPE_EVENT_LOCATION, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public List<NodeRef> getEventMembers(NodeRef event) {
		return findNodesByAssociationRef(event, ASSOC_EVENT_TEMP_MEMBERS, null, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public List<NodeRef> getEventResources(NodeRef event) {
		return findNodesByAssociationRef(event, ASSOC_EVENT_TEMP_RESOURCES, null, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public List<NodeRef> getEventInvitedMembers(NodeRef event) {
		return findNodesByAssociationRef(event, ASSOC_EVENT_INVITED_MEMBERS, null, ASSOCIATION_TYPE.TARGET);
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

	public void setPropsForFilterShowIncalendar(List<String> propsForFilterShowIncalendar) {
		this.propsForFilterShowIncalendar = propsForFilterShowIncalendar;
	}

	@Override
	public List<NodeRef> getEvents(String fromDate, String toDate) {
		return getEvents(fromDate, toDate, "");
	}

	@Override
	public List<NodeRef> getEvents(String fromDate, String toDate, String additionalFilter) {
		return getEvents(fromDate, toDate, additionalFilter, false);
	}

	@Override
	public List<NodeRef> getEvents(String fromDate, String toDate, String additionalFilter, boolean excludeDeclined) {
		List<NodeRef> results = new ArrayList<>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[MIN TO \"" + toDate + "\"> AND @lecm\\-events\\:to\\-date:<\"" + fromDate + "\" TO MAX] AND @lecm\\-events\\:removed: false AND @lecm\\-events\\:show\\-in\\-calendar: true " + additionalFilter;
		query += " AND (" + organizationQueryProcessor.getQuery(null) + ")";
		sp.setQuery(query);

		ResultSet searchResult = null;
		try {
			searchResult = searchService.query(sp);
			results = searchResult.getNodeRefs();
		} finally {
			if (searchResult != null) {
				searchResult.close();
			}
		}
		if (excludeDeclined) {
			results = filterDeclinedEvents(results);
		}
		return results;
	}

	@Override
	public List<NodeRef> searchEvents(String filter) {
		List<NodeRef> results = new ArrayList<>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:removed: false  AND @lecm\\-events\\:show\\-in\\-calendar: true";
		if (filter != null && filter.length() > 0) {
			query += " AND " + filter;
		}
		sp.setQuery(query);

		ResultSet searchResult = null;
		try {
			searchResult = searchService.query(sp);
			results = searchResult.getNodeRefs();
		} finally {
			if (searchResult != null) {
				searchResult.close();
			}
		}

		return results;
	}

	@Override
	public List<NodeRef> getNearestEvents(String fromDate, int maxCount, String additionalFilter) {
		List<NodeRef> results = new ArrayList<>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		sp.addSort(EventsService.PROP_EVENT_FROM_DATE.toString(), true);
		String query;
		if (maxCount > 0) {
			sp.setMaxItems(maxCount);
			query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[\"" + fromDate + "\" TO MAX> AND @lecm\\-events\\:removed: false AND @lecm\\-events\\:show\\-in\\-calendar: true " + (additionalFilter == null ? "" : additionalFilter);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[\"" + fromDate + "\" TO \"" + DateFormatISO8601.format(calendar.getTime()) + "\"> AND @lecm\\-events\\:removed: false AND @lecm\\-events\\:show\\-in\\-calendar: true " + (additionalFilter == null ? "" : additionalFilter);
		}
		sp.setQuery(query + " AND (" + organizationQueryProcessor.getQuery(null) + ")");

		ResultSet searchResult = null;
		try {
			searchResult = searchService.query(sp);
			results = searchResult.getNodeRefs();
		} finally {
			if (searchResult != null) {
				searchResult.close();
			}
		}

		return filterDeclinedEvents(results);
	}

	List<NodeRef> filterDeclinedEvents(List<NodeRef> events) {
		if (!isShowDeclined()) {
			List<NodeRef> filteredResults = new ArrayList<>();
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			for (NodeRef event : events) {
				if (!"DECLINED".equals(getEmployeeMemberStatus(event, currentEmployee))) {
					filteredResults.add(event);
				}
			}
			return filteredResults;
		} else {
			return events;
		}
	}

	@Override
	public List<NodeRef> getAvailableUserLocations(String fromDate, String toDate, NodeRef ignoreNode) {
		List<NodeRef> results = new ArrayList<>();
		NodeRef locationsDic = dictionaryBean.getDictionaryByName("Места проведения мероприятий");
		if (locationsDic != null) {
			List<NodeRef> locations = dictionaryBean.getChildren(locationsDic);
			NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();
			int currentUserLocationPrivilegeLevel = getCurrentUserLocationPrivilegeLevel();

			for (NodeRef location : locations) {
				NodeRef locationOrganization = findNodeByAssociationRef(location, ASSOC_EVENT_LOCATION_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
				Integer locationPrivilegeLevel = (Integer) nodeService.getProperty(location, PROP_EVENT_LOCATION_PRIVILEGE_LEVEL);

				if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(locationOrganization)
						&& (locationPrivilegeLevel == 0 || currentUserLocationPrivilegeLevel >= locationPrivilegeLevel)) {
					results.add(location);
				}
			}
		}

		if (fromDate != null && toDate != null) {
			final String fromDateFinal = fromDate;
			final String toDateFinal = toDate;
			final NodeRef ignoreNodeFinal = ignoreNode;

			Set<NodeRef> unavailableLocations = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Set<NodeRef>>() {
				@Override
				public Set<NodeRef> doWork() throws Exception {
					Set<NodeRef> results = new HashSet<>();

					String additionalFilter = "";
					if (ignoreNodeFinal != null) {
						additionalFilter += " AND NOT ID:\"" + ignoreNodeFinal.toString() + "\"";
					}
					List<NodeRef> existEvents = getEvents(fromDateFinal, toDateFinal, additionalFilter);
					if (existEvents != null) {
						for (NodeRef event : existEvents) {
							NodeRef location = getEventLocation(event);
							if (location != null) {
								results.add(location);
							}
						}
					}
					return results;
				}
			});

			results.removeAll(unavailableLocations);
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

					if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(resourceOrganization)
							&& (resourcePrivilegeLevel == 0 || currentUserResourcesPrivilegeLevel >= resourcePrivilegeLevel)) {
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

	@Override
	public boolean checkLocationAvailable(NodeRef location, Date fromDate, Date toDate, boolean allDay) {
		return checkLocationAvailable(location, null, fromDate, toDate, allDay);
	}

	@Override
	public boolean checkLocationAvailable(NodeRef location, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay) {
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
		if (ignoreNode != null) {
			additionalFilter += " AND NOT ID:\"" + ignoreNode.toString() + "\"";
		}

		List<NodeRef> events = getEvents(DateFormatISO8601.format(fromDate), DateFormatISO8601.format(toDate), additionalFilter);

		return events == null || events.isEmpty();
	}

	@Override
	public boolean checkMemberAvailable(NodeRef member, Date fromDate, Date toDate, boolean allDay) {
		return checkMemberAvailable(member, null, fromDate, toDate, allDay);
	}

	@Override
	public boolean checkMemberAvailable(NodeRef member, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay) {
		List<Date> employeeWorkindDays = workCalendarService.getEmployeeWorkindDays(member, fromDate, toDate);
		if (employeeWorkindDays.isEmpty()) {
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

			String additionalFilter = " AND @lecm\\-events\\:temp\\-members\\-assoc\\-ref:\"*" + member.toString() + "*\"";
			if (ignoreNode != null) {
				additionalFilter += " AND NOT ID:\"" + ignoreNode.toString() + "\"";
			}

			List<NodeRef> events = getEvents(DateFormatISO8601.format(fromDate), DateFormatISO8601.format(toDate), additionalFilter);

			return events == null || events.isEmpty();
		}
	}

	@Override
	public List<NodeRef> getResourceResponsible(NodeRef resource) {
		return findNodesByAssociationRef(resource, ASSOC_EVENT_RESOURCE_RESPONSIBLE, null, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public NodeRef getEventInitiator(NodeRef event) {
		return findNodeByAssociationRef(event, ASSOC_EVENT_INITIATOR, null, ASSOCIATION_TYPE.TARGET);
	}

	public NodeRef getResourcesTable(NodeRef event) {
		return documentTableService.getTable(event, TYPE_EVENT_RESOURCES_TABLE);
	}

	@Override
	public NodeRef getMemberTable(NodeRef event) {
		List<AssociationRef> membersTable = nodeService.getTargetAssocs(event, EventsService.ASSOC_EVENT_MEMBERS);
		if (membersTable != null && !membersTable.isEmpty()) {
			return membersTable.get(0).getTargetRef();
		}
		return null;
	}

	@Override
	public NodeRef getResourceTableRow(NodeRef event, NodeRef employee) {
		NodeRef table = getResourcesTable(event);
		if (table != null) {
			List<NodeRef> rows = documentTableService.getTableDataRows(table);
			if (rows != null) {
				for (NodeRef row : rows) {
					NodeRef rowEmployee = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_RESOURCES_TABLE_RESOURCE, null, ASSOCIATION_TYPE.TARGET);
					if (rowEmployee.equals(employee)) {
						return row;
					}
				}
			}
		}
		return null;
	}

	@Override
	public NodeRef getMemberTableRow(NodeRef event, NodeRef employee) {
		NodeRef table = getMemberTable(event);
		if (table != null) {
			List<NodeRef> rows = documentTableService.getTableDataRows(table);
			if (rows != null) {
				for (NodeRef row : rows) {
					NodeRef rowEmployee = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
					if (rowEmployee.equals(employee)) {
						return row;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getCurrentEmployeeMemberStatus(NodeRef event) {
		NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
		return getEmployeeMemberStatus(event, currentEmployee);
	}

	@Override
	public String getEmployeeMemberStatus(NodeRef event, NodeRef employee) {
		if (employee != null) {
			NodeRef memberTableRow = getMemberTableRow(event, employee);
			if (memberTableRow != null) {
				return (String) nodeService.getProperty(memberTableRow, PROP_EVENT_MEMBERS_STATUS);
			}
		}
		return null;
	}

	@Override
	public String wrapAsEventLink(NodeRef documentRef) {
		return wrapperLink(documentRef, (String) nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING), EVENT_LINK_URL);
	}

	@Override
	public void onAfterUpdate(NodeRef event, String updateRepeated) {
		onAfterUpdate(event, updateRepeated, false);
	}

	@Override
	public void onAfterUpdate(NodeRef event, String updateRepeated, boolean sendToInvitedMembers) {
		updateMembers(event);
		Boolean send_notifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
		send_notifications = null == send_notifications ? false : send_notifications;
		if (sendToInvitedMembers && send_notifications) {
			sendNotificationsToInvitedMembers(event, false);
		}

		Boolean repeatable = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE);
		if (repeatable != null && repeatable && updateRepeated != null) {
			updateRepeatedEvents(event, updateRepeated);
		}
	}

	private void updateMembers(NodeRef event) {
		List<NodeRef> newMembers = getEventMembers(event);
		List<NodeRef> oldMembers = findNodesByAssociationRef(event, ASSOC_EVENT_OLD_MEMBERS, null, ASSOCIATION_TYPE.TARGET);

		List<NodeRef> oldAndNewMembers = new ArrayList<>();
		for (NodeRef oldMember : oldMembers) {
			if (newMembers.contains(oldMember)) {
				oldAndNewMembers.add(oldMember);
			}
		}

		Boolean send_notifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
		send_notifications = null == send_notifications ? false : send_notifications;
		if (send_notifications) {
			NodeRef initiator = getEventInitiator(event);
			Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
			if (initiator != null && fromDate != null) {
				sendNotificationsToMembers(event, false, oldAndNewMembers);
			}
		}
		nodeService.setAssociations(event, EventsService.ASSOC_EVENT_OLD_MEMBERS, getEventMembers(event));
	}

	@Override
	public void sendNotificationsToInvitedMembers(NodeRef event, Boolean isFirst) {
		sendNotificationsToInvitedMembers(event, isFirst, null);
	}

	@Override
	public void sendNotificationsToInvitedMembers(NodeRef event, Boolean isFirst, List<NodeRef> recipients) {
		List<NodeRef> invitedMembers = getEventInvitedMembers(event);
		if (null == recipients || recipients.isEmpty()) {
			recipients = invitedMembers;
		} else {
			recipients.retainAll(invitedMembers);
		}
		
		eventsNotificationsService.notifyEvent(event, isFirst, recipients);
	}

	@Override
	public void sendNotificationsToMembers(NodeRef event, Boolean isFirst) {
		sendNotificationsToMembers(event, isFirst, null);
	}

	/**
	 *
	 * @param event
	 * @param recipients if null - send to all members
	 * @param isFirst
	 */
	@Override
	public void sendNotificationsToMembers(NodeRef event, Boolean isFirst, List<NodeRef> recipients) {
		List<NodeRef> members = getEventMembers(event);
		if (!members.contains(getEventInitiator(event))) {
			members.add(getEventInitiator(event));
		}
		if (null == recipients || recipients.isEmpty()) {
			recipients = members;
		} else {
			recipients.retainAll(members);
		}
		eventsNotificationsService.notifyEvent(event, isFirst, recipients);
	}

	@Override
	public void notifyAttendeeRemoved(NodeRef event, NodeRef attendee) {
		eventsNotificationsService.notifyAttendeeRemoved(event, attendee);
	}


	@Override
	public void notifyEventCncelled(NodeRef event) {
		eventsNotificationsService.notifyEventCancelled(event);
	}

	@Override
	public List<NodeRef> getNextRepeatedEvents(NodeRef event) {
		List<NodeRef> results = new ArrayList<>();
		QName eventType = nodeService.getType(event);
		NodeRef nextEvent = findNodeByAssociationRef(event, ASSOC_NEXT_REPEATED_EVENT, eventType, ASSOCIATION_TYPE.TARGET);
		while (nextEvent != null && !nextEvent.equals(event)) {
			results.add(nextEvent);
			nextEvent = findNodeByAssociationRef(nextEvent, ASSOC_NEXT_REPEATED_EVENT, eventType, ASSOCIATION_TYPE.TARGET);
		}
		return results;
	}

	@Override
	public List<NodeRef> getPrevRepeatedEvents(NodeRef event) {
		List<NodeRef> results = new ArrayList<>();
		QName eventType = nodeService.getType(event);
		NodeRef nextEvent = findNodeByAssociationRef(event, ASSOC_NEXT_REPEATED_EVENT, eventType, ASSOCIATION_TYPE.SOURCE);
		while (nextEvent != null && !nextEvent.equals(event)) {
			results.add(nextEvent);
			nextEvent = findNodeByAssociationRef(nextEvent, ASSOC_NEXT_REPEATED_EVENT, eventType, ASSOCIATION_TYPE.SOURCE);
		}
		return results;
	}

	@Override
	public List<NodeRef> getAllRepeatedEvents(NodeRef event) {
		List<NodeRef> results = new ArrayList<>();
		results.addAll(getNextRepeatedEvents(event));
		results.addAll(getPrevRepeatedEvents(event));
		return results;
	}

	private void updateRepeatedEvents(NodeRef event, String updateRepeated) {
		List<NodeRef> updateEvents = new ArrayList<>();
		if ("ALL".equals(updateRepeated)) {
			updateEvents = getAllRepeatedEvents(event);
		} else if ("ALL_NEXT".equals(updateRepeated)) {
			updateEvents = getNextRepeatedEvents(event);
		} else if ("ALL_PREV".equals(updateRepeated)) {
			updateEvents = getPrevRepeatedEvents(event);
		}
		for (NodeRef repeatedEvent : updateEvents) {
			updateRepeatedEvent(event, repeatedEvent);
		}
	}

	private void updateRepeatedEvent(NodeRef event, NodeRef repeatedEvent) {
		// копируем свойства
		Map<QName, Serializable> oldProperties = nodeService.getProperties(event);
		Map<QName, Serializable> newProperties = nodeService.getProperties(repeatedEvent);
		QName eventType = nodeService.getType(event);

		List<QName> propertiesToCopy = propsToUpdateMap.get(eventType);

		Calendar calOldFrom = Calendar.getInstance();
		calOldFrom.setTime((Date) oldProperties.get(PROP_EVENT_FROM_DATE));
		Calendar calNewFrom = Calendar.getInstance();
		calNewFrom.setTime((Date) newProperties.get(PROP_EVENT_FROM_DATE));
		calNewFrom.set(Calendar.HOUR_OF_DAY, calOldFrom.get(Calendar.HOUR_OF_DAY));
		calNewFrom.set(Calendar.MINUTE, calOldFrom.get(Calendar.MINUTE));
		newProperties.put(PROP_EVENT_FROM_DATE, calNewFrom.getTime());

		Calendar calOldTo = Calendar.getInstance();
		calOldTo.setTime((Date) oldProperties.get(PROP_EVENT_TO_DATE));
		Calendar calNewTo = Calendar.getInstance();
		calNewTo.setTime((Date) newProperties.get(PROP_EVENT_TO_DATE));
		calNewTo.set(Calendar.HOUR_OF_DAY, calOldTo.get(Calendar.HOUR_OF_DAY));
		calNewTo.set(Calendar.MINUTE, calOldTo.get(Calendar.MINUTE));
		newProperties.put(PROP_EVENT_TO_DATE, calNewTo.getTime());

		for (QName propName : propertiesToCopy) {
			newProperties.put(propName, oldProperties.get(propName));
		}
		nodeService.setProperties(repeatedEvent, newProperties);

		//Копируем ассоциации
		List<QName> assocsToCopy = assocsToUpdateMap.get(eventType);
		for (QName assocQName : assocsToCopy) {
			List<NodeRef> targets = findNodesByAssociationRef(event, assocQName, null, ASSOCIATION_TYPE.TARGET);
			nodeService.setAssociations(repeatedEvent, assocQName, targets);
		}

		//Обновляем участников
		List<NodeRef> eventMembers = getEventMembers(event);
		List<NodeRef> repeatedEventMembers = getEventMembers(repeatedEvent);
		for (NodeRef member : eventMembers) {
			if (!repeatedEventMembers.contains(member)) {
				nodeService.createAssociation(repeatedEvent, member, ASSOC_EVENT_TEMP_MEMBERS);
			}
		}
		for (NodeRef member : repeatedEventMembers) {
			if (!eventMembers.contains(member)) {
				nodeService.removeAssociation(repeatedEvent, member, ASSOC_EVENT_TEMP_MEMBERS);
			}
		}

		//Обновляем ресурсы
		List<NodeRef> eventResources = getEventResources(event);
		List<NodeRef> repeatedEventResources = getEventResources(repeatedEvent);
		for (NodeRef resource : eventResources) {
			if (!repeatedEventResources.contains(resource)) {
				nodeService.createAssociation(repeatedEvent, resource, ASSOC_EVENT_TEMP_RESOURCES);
			}
		}
		for (NodeRef resource : repeatedEventResources) {
			if (!eventResources.contains(resource)) {
				nodeService.removeAssociation(repeatedEvent, resource, ASSOC_EVENT_TEMP_RESOURCES);
			}
		}

		onAfterUpdate(repeatedEvent, null);
	}

	@Override
	public String getAdditionalFilterForCalendarShow() {
		NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
		String result = "";
		if (currentEmployee != null && !orgstructureBean.isEmployeeHasBusinessRole(currentEmployee, EVENTS_ENGINEER_ROLE)
				&& this.propsForFilterShowIncalendar != null && this.propsForFilterShowIncalendar.size() > 0) {
			result += " AND (";
			int i = 0;
			for (String prop : this.propsForFilterShowIncalendar) {
				if (i > 0) {
					result += " OR ";
				}
				result += "@" + prop.replaceAll("-", "\\\\-").replaceAll(":", "\\\\:") + ": \"*" + currentEmployee + "*\"";
				i++;
			}
			result += ")";
		}
		return result;

	}

	@Override
	public NodeRef getCurrentUserSettingsNode() {
		final NodeRef rootFolder = this.getServiceRootFolder();
		final String settingsObjectName = authService.getCurrentUserName() + "_" + EVENTS_SETTINGS_NODE_NAME;

		return nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
	}

	@Override
	public NodeRef createCurrentUserSettingsNode() throws WriteTransactionNeededException {
		try {
			lecmTransactionHelper.checkTransaction();
		} catch (TransactionNeededException ex) {
			throw new WriteTransactionNeededException("Can't create settings node");
		}

		final NodeRef rootFolder = this.getServiceRootFolder();
		final String settingsObjectName = authService.getCurrentUserName() + "_" + EVENTS_SETTINGS_NODE_NAME;

		NodeRef settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
		if (settingsRef == null) {
			QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
			QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, settingsObjectName);
			QName nodeTypeQName = TYPE_EVENTS_USER_SETTINGS;

			Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
			properties.put(ContentModel.PROP_NAME, settingsObjectName);
			ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
			settingsRef = associationRef.getChildRef();
		}
		return settingsRef;
	}

	@Override
	public boolean isShowDeclined() {
		NodeRef settings = getCurrentUserSettingsNode();
		if (settings != null) {
			return (Boolean) nodeService.getProperty(settings, USER_SETTINGS_PROP_SHOW_DECLINED);
		}
		return false;
	}
}
