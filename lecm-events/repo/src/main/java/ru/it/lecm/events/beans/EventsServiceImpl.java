package ru.it.lecm.events.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin Date: 25.03.2015 Time: 14:44
 */
public class EventsServiceImpl extends BaseBean implements EventsService {

	private final static Logger logger = LoggerFactory.getLogger(EventsServiceImpl.class);

	private TransactionListener transactionListener;
	private static final String EVENTS_TRANSACTION_LISTENER = "events_transaction_listaner";
	private static final boolean DAY_BEGIN = true;
	private static final boolean DAY_END = false;

	private DictionaryBean dictionaryBean;
	private OrgstructureBean orgstructureBean;
	private SearchService searchService;
	private IWorkCalendar workCalendarService;
	private DocumentTableService documentTableService;
	private SearchQueryProcessor organizationQueryProcessor;
	private DocumentConnectionService documentConnectionService;
	private NamespaceService namespaceService;

	private EventsNotificationsService eventsNotificationsService;

	private final Map<QName, List<QName>> assocsToUpdateMap = new HashMap<>();
	private final Map<QName, List<QName>> propsToUpdateMap = new HashMap<>();

	private final static String WEEK_DAYS = "week-days";
	private final static String MONTH_DAYS = "month-days";
	
	private List<String> propsForFilterShowInCalendar = new ArrayList<>();
	
	public void setEventsNotificationsService(EventsNotificationsService eventsNotificationsService) {
		this.eventsNotificationsService = eventsNotificationsService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public Boolean getSendIcalToInvitedMembers() {
		return eventsNotificationsService.getSendIcalToInvitedMembers();
	}

	@Override
	public Boolean getSendIcalToMembers() {
		return eventsNotificationsService.getSendIcalToMembers();
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
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

		transactionListener = new EventsTransactionListener();
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

	public void setOrganizationQueryProcessor(SearchQueryProcessor organizationQueryProcessor) {
		this.organizationQueryProcessor = organizationQueryProcessor;
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
		return getEventMembers(event, false);
	}

	@Override
	public List<NodeRef> getEventMembers(NodeRef event, boolean useAssocsFromConfig) {
		List<String> assocsToGetMembers = getPropsForFilterShowInCalendar();
		if (useAssocsFromConfig && !assocsToGetMembers.isEmpty()) {
			Set<NodeRef> members = new HashSet<>();
			for (String assocToGetMembers : assocsToGetMembers) {
				QName assocToGetQName = QName.createQName(assocToGetMembers, namespaceService);
				List<NodeRef> membersFromAssoc = findNodesByAssociationRef(event, assocToGetQName, null, ASSOCIATION_TYPE.TARGET);
				if (membersFromAssoc != null) {
					members.addAll(findNodesByAssociationRef(event, assocToGetQName, null, ASSOCIATION_TYPE.TARGET));
				}
			}
			return new ArrayList<>(members);
		} else {
			return findNodesByAssociationRef(event, ASSOC_EVENT_TEMP_MEMBERS, null, ASSOCIATION_TYPE.TARGET);
		}
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

	public void setPropsForFilterShowInCalendar(List<String> propsForFilterShowInCalendar) {
		this.propsForFilterShowInCalendar = propsForFilterShowInCalendar;
	}

	@Override
	public List<String> getPropsForFilterShowInCalendar() {
		return propsForFilterShowInCalendar;
	}

	@Override
	public void addPropsForFilterShowInCalendar(List<String> props) {
		if (props != null) {
			propsForFilterShowInCalendar.addAll(props);
		}
	}

	@Override
	public List<NodeRef> getEvents(Date fromDate, Date toDate) {
		return getEvents(fromDate, toDate, "");
	}

	@Override
	public List<NodeRef> getEvents(Date fromDate, Date toDate, String additionalFilter) {
		return getEvents(fromDate, toDate, additionalFilter, false, null, true);
	}

	@Override
	public List<NodeRef> getEvents(Date fromDate, Date toDate, String additionalFilter, boolean excludeDeclined) {
		return getEvents(fromDate, toDate, additionalFilter, false, null, true);
	}

	@Override
	public List<NodeRef> getEvents(Date fromDate, Date toDate, String additionalFilter, boolean excludeDeclined, String lastCreated, boolean onlyForCalendar) {
		List<NodeRef> results = new ArrayList<>();
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[MIN TO \"" + ISO8601DateFormat.format(toDate) + "\"> AND @lecm\\-events\\:to\\-date:<\"" + ISO8601DateFormat.format(fromDate) + "\" TO MAX] AND @lecm\\-events\\:removed: false";
		query += onlyForCalendar ? " AND @lecm\\-events\\:show\\-in\\-calendar: true " + additionalFilter : " " + additionalFilter;
		query += " AND (" + organizationQueryProcessor.getQuery(null) + ")";
		sp.setQuery(query);

		ResultSet searchResult = null;
		try {
			searchResult = searchService.query(sp);
			results.addAll(searchResult.getNodeRefs());
		} finally {
			if (searchResult != null) {
				searchResult.close();
			}
		}
		if (lastCreated != null) {
			String[] lastCreatedItems = lastCreated.split(",");
			for (String lastCreatedItem : lastCreatedItems) {
				if (NodeRef.isNodeRef(lastCreatedItem)) {
					NodeRef ref = new NodeRef(lastCreatedItem);
					if (!results.contains(ref)) {
						Map<QName, Serializable> properties = nodeService.getProperties(ref);
						Date propFromDate = (Date) properties.get(PROP_EVENT_FROM_DATE);
						Date propToDate = (Date) properties.get(PROP_EVENT_TO_DATE);
						Boolean removed = (Boolean) properties.get(PROP_EVENT_REMOVED);
						Boolean showInCalendar = (Boolean) properties.get(PROP_EVENT_SHOW_IN_CALENDAR);
						if (propFromDate != null && propFromDate.before(toDate) && propToDate != null && propToDate.after(fromDate)
								&& !removed && showInCalendar) {
							results.add(ref);
						}
					}
				}
			}
		}
		if (excludeDeclined) {
			results = filterDeclinedEvents(results);
		}
		return results;
	}

	@Override
	public List<NodeRef> getEvents(Date fromDate, Date toDate, String additionalFilter, String lastCreated) {
		return getEvents(fromDate, toDate, additionalFilter, false, lastCreated, true);
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
	public List<NodeRef> getNearestEvents(Date fromDate, int maxCount, String additionalFilter) {
		List<NodeRef> results = new ArrayList<>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		sp.addSort(EventsService.PROP_EVENT_FROM_DATE.toString(), true);
		String query;
		if (maxCount > 0) {
			sp.setMaxItems(maxCount);
			query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[\"" + ISO8601DateFormat.format(fromDate) + "\" TO MAX> AND @lecm\\-events\\:removed: false AND @lecm\\-events\\:show\\-in\\-calendar: true " + (additionalFilter == null ? "" : additionalFilter);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[\"" + ISO8601DateFormat.format(fromDate) + "\" TO \"" + ISO8601DateFormat.format(calendar.getTime()) + "\"> AND @lecm\\-events\\:removed: false AND @lecm\\-events\\:show\\-in\\-calendar: true " + (additionalFilter == null ? "" : additionalFilter);
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

	private List<NodeRef> filterDeclinedEvents(List<NodeRef> events) {
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
	public List<NodeRef> getAvailableUserLocations(final Date fromDate, final Date toDate, NodeRef ignoreNode) {
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
			final NodeRef ignoreNodeFinal = ignoreNode;

			Set<NodeRef> unavailableLocations = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Set<NodeRef>>() {
				@Override
				public Set<NodeRef> doWork() throws Exception {
					Set<NodeRef> results = new HashSet<>();

					String additionalFilter = "";
					if (ignoreNodeFinal != null) {
						additionalFilter += " AND NOT ID:\"" + ignoreNodeFinal.toString() + "\"";
					}
					List<NodeRef> existEvents = getEvents(fromDate, toDate, additionalFilter);
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
		return checkLocationAvailable(location, ignoreNode, fromDate, toDate, allDay, 0);
	}

	@Override
	public boolean checkLocationAvailable(NodeRef location, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay, int clientTimezoneOffset) {
		String additionalFilter = " AND @lecm\\-events\\:location\\-assoc\\-ref:\"" + location.toString() + "\"";
		if (ignoreNode != null) {
			additionalFilter += " AND NOT ID:\"" + ignoreNode.toString() + "\"";
		}

		List<NodeRef> events = getEvents(fromDate, toDate, additionalFilter);

		return events == null || events.isEmpty();
	}

	private Date toFullDate(Date date, boolean isBegin) {
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(date);
		if (isBegin == DAY_BEGIN) {
			dateCal.set(Calendar.HOUR_OF_DAY, 0);
			dateCal.set(Calendar.MINUTE, 1);
		}
		else {
			dateCal.set(Calendar.HOUR_OF_DAY, 23);
			dateCal.set(Calendar.MINUTE, 59);
		}
		dateCal.set(Calendar.SECOND, 0);
		dateCal.set(Calendar.MILLISECOND, 0);
		return dateCal.getTime();
	}

	@Override
	public boolean checkMemberAvailable(NodeRef member, Date fromDate, Date toDate, boolean allDay) {
		return checkMemberAvailable(member, null, fromDate, toDate, allDay);
	}

	@Override
	public boolean checkMemberAvailable(NodeRef member, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay) {
		return checkMemberAvailable(member, ignoreNode, fromDate, toDate, allDay, false);
	}

	@Override
	public boolean checkMemberAvailable(NodeRef member, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay, boolean useAssocsFromConfig) {
		List<Date> employeeWorkindDays = workCalendarService.getEmployeeWorkindDays(member, fromDate, toDate);
		if (employeeWorkindDays.isEmpty()) {
			return false;
		} else {
			if (allDay) {
				fromDate = toFullDate(fromDate, DAY_BEGIN);
				toDate = toFullDate(toDate, DAY_END);
			}

			StringBuilder additionalFilter = new StringBuilder();
			List<String> assocsToGetMembers = getPropsForFilterShowInCalendar();
			if (!useAssocsFromConfig || assocsToGetMembers.isEmpty()) {
				assocsToGetMembers = new ArrayList<>();
				assocsToGetMembers.add("lecm-events:temp-members-assoc");
			}
			additionalFilter.append(" AND (");
			for (String assocToGetMember : assocsToGetMembers) {
				additionalFilter.append("@")
						.append(assocToGetMember.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-"))
						.append("\\-ref:\"*")
						.append(member.toString())
						.append("*\" OR ");
			}
			if (additionalFilter.length() > 0) {
				additionalFilter.delete(additionalFilter.length() - 4, additionalFilter.length());
			}
			additionalFilter.append(")");

			if (ignoreNode != null) {
				additionalFilter.append(" AND NOT ID:\"").append(ignoreNode.toString()).append("\"");
			}

			List<NodeRef> events = getEvents(fromDate, toDate, additionalFilter.toString(), false, null, false);

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

	private NodeRef getResourcesTable(NodeRef event) {
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
	public void onAfterUpdate(NodeRef event, String updateRepeated, boolean forceSending) {
		onAfterUpdate(event, updateRepeated, false);
	}

	@Override
	public void onAfterUpdate(NodeRef event, String updateRepeated, boolean sendToInvitedMembers, boolean forceSending) {
		updateMembers(event, forceSending);
		if (sendToInvitedMembers) {
			sendNotificationsToInvitedMembers(event, false, false);
		}

		Boolean repeatable = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE);
		if (repeatable != null && repeatable && updateRepeated != null) {
			updateRepeatedEvents(event, updateRepeated);
		}
	}

	private void updateMembers(NodeRef event, Boolean forceSending) {
		List<NodeRef> newMembers = getEventMembers(event);
		List<NodeRef> oldMembers = findNodesByAssociationRef(event, ASSOC_EVENT_OLD_MEMBERS, null, ASSOCIATION_TYPE.TARGET);

		List<NodeRef> oldAndNewMembers = new ArrayList<>();
		for (NodeRef oldMember : oldMembers) {
			if (newMembers.contains(oldMember)) {
				oldAndNewMembers.add(oldMember);
			}
		}

		NodeRef initiator = getEventInitiator(event);
		Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
		if (initiator != null && fromDate != null) {
			sendNotificationsToMembers(event, false, oldAndNewMembers, forceSending);
		}
		
		nodeService.setAssociations(event, EventsService.ASSOC_EVENT_OLD_MEMBERS, getEventMembers(event));
	}

	@Override
	public void sendNotificationsToInvitedMembers(NodeRef event, Boolean isFirst, Boolean force) {
		sendNotificationsToInvitedMembers(event, isFirst, null, force);
	}

	@Override
	public void sendNotificationsToInvitedMembers(NodeRef event, Boolean isFirst, List<NodeRef> recipients, Boolean force) {
		List<NodeRef> invitedMembers = getEventInvitedMembers(event);
		if (null == recipients || recipients.isEmpty()) {
			recipients = invitedMembers;
		} else {
			recipients.retainAll(invitedMembers);
		}

		sendNotifications(event, isFirst, recipients, force);
	}

	@Override
	public void sendNotificationsToMembers(NodeRef event, Boolean isFirst, Boolean force) {
		sendNotificationsToMembers(event, isFirst, null, force);
	}

	/**
	 *
	 * @param event
	 * @param recipients if null - send to all members
	 * @param isFirst
	 */
	@Override
	public void sendNotificationsToMembers(NodeRef event, Boolean isFirst, List<NodeRef> recipients, Boolean force) {
		List<NodeRef> members = getEventMembers(event);
		if (!members.contains(getEventInitiator(event))) {
			members.add(getEventInitiator(event));
		}
		if (null == recipients || recipients.isEmpty()) {
			recipients = members;
		} else {
			recipients.retainAll(members);
		}
		sendNotifications(event, isFirst, recipients, force);
	}

	@Override
	public void sendNotifications(NodeRef event, Boolean isFirst, List<NodeRef> recipients, Boolean force) {
		Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
		sendNotifications = (sendNotifications==null?false:sendNotifications) || force;
		if (sendNotifications) {
			bindTransactionListener();
			Map<NodeRef, Action> pendingActions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
			Action action = pendingActions.get(event);
			if (null == action) {
				action = new Action(event);
				pendingActions.put(event, action);
			}
			String actionType = isFirst ? Action.CREATE : Action.UPDATE;
			for (NodeRef recipient : recipients) {
				String currentType = action.getRecipients().get(recipient);
				if (null == currentType || actionType.equals(Action.CREATE)) {
					action.getRecipients().put(recipient, actionType);
				}
			}
		}	
//		eventsNotificationsService.notifyEvent(event, isFirst, recipients);
	}

	@Override
	public void notifyAttendeeRemoved(NodeRef event, NodeRef attendee) {
		bindTransactionListener();
		Map<NodeRef, Action> pendingActions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
		Action action = pendingActions.get(event);
		if (null == action) {
			action = new Action(event);
			pendingActions.put(event, action);
		}
		action.getRecipients().put(attendee, Action.REMOVE);
		//eventsNotificationsService.notifyAttendeeRemoved(event, attendee);
	}

	@Override
	public void notifyEventCancelled(NodeRef event) {
		bindTransactionListener();
		Map<NodeRef, Action> pendingActions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
		Action action = pendingActions.get(event);
		if (null == action) {
			action = new Action(event);
			pendingActions.put(event, action);
		}
		action.canceled = true;
		//eventsNotificationsService.notifyEventCancelled(event);
	}

	public void createRepeated(NodeRef event) {
		bindTransactionListener();
		Map<NodeRef, Action> pendingActions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
		Action action = pendingActions.get(event);
		if (null == action) {
			action = new Action(event);
			pendingActions.put(event, action);
		}
		action.created = true;
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

		onAfterUpdate(repeatedEvent, null, false);
	}

	@Override
	public String getAdditionalFilterForCalendarShow() {
		NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
		StringBuilder result = new StringBuilder();
		if (currentEmployee != null && !orgstructureBean.isEmployeeHasBusinessRole(currentEmployee, EVENTS_ENGINEER_ROLE)
				&& this.propsForFilterShowInCalendar != null && this.propsForFilterShowInCalendar.size() > 0) {
			result.append(" AND (");
			int i = 0;
			for (String prop : this.propsForFilterShowInCalendar) {
				if (i > 0) {
					result.append(" OR ");
				}
				result.append("@").append(prop.replaceAll("-", "\\\\-").replaceAll(":", "\\\\:")).append("\\-ref: \"*").append(currentEmployee).append("*\"");
				i++;
			}
			result.append(")");
		}
		return result.toString();

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

	private void bindTransactionListener() {
		AlfrescoTransactionSupport.bindListener(this.transactionListener);

		Map<NodeRef, Action> pendingActions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
		if (pendingActions == null) {
			pendingActions = new HashMap<>();
			AlfrescoTransactionSupport.bindResource(EVENTS_TRANSACTION_LISTENER, pendingActions);
		}
	}

	private class EventsTransactionListener implements TransactionListener {

		@Override
		public void flush() {

		}

		@Override
		public void beforeCommit(boolean readOnly) {

		}

		@Override
		public void beforeCompletion() {

		}

		@Override
		public void afterCommit() {
			logger.debug("AfterCommit start");
			// TODO: Совсем плохо падает без обёртки в транзакцию.
			// Надо отсмотреть на предмет потенциальных блокировок
			final HashMap<NodeRef, Action> actions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
			transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
				@Override
				public Void execute() throws Throwable {
					if (actions != null) {
						List<NodeRef> nodes = new LinkedList<>(actions.keySet());
						while (!nodes.isEmpty()) {
							NodeRef node = nodes.remove(0);
							final Action action = actions.remove(node);
							if (action.created) {
								createRepeated(action.event);
							}
							sendNotifications(action);
						}
					}
					return null;
				}
			}, true, true);
			logger.debug("AfterCommit finished");
		}

		private void sendNotifications(final Action action) {
			final NodeRef event = action.getEvent();
			//Рассылка уведомлений
			Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
			Boolean deleted = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_REMOVED);
			sendNotifications = null == sendNotifications ? false : sendNotifications;
			if (!deleted || action.canceled) {
				transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
					@Override
					public Void execute() throws Throwable {
						if (action.canceled) {
							eventsNotificationsService.notifyEventCancelled(event);
						} else {
							List<NodeRef> create = new ArrayList<>();
							List<NodeRef> update = new ArrayList<>();
							for (NodeRef recipient : action.recipients.keySet()) {
								String notificationType = action.getRecipients().get(recipient);
								switch (notificationType) {
									case Action.CREATE:
										create.add(recipient);
										break;
									case Action.REMOVE:
										eventsNotificationsService.notifyAttendeeRemoved(event, recipient);
										break;
									case Action.UPDATE:
										update.add(recipient);
										break;
								}
							}
							eventsNotificationsService.notifyEventCreated(event, create);
							eventsNotificationsService.notifyEventUpdated(event, update);
						}
						return null;
					}
				}, false, true);

			}

		}

		private void createRepeated(final NodeRef event) {
			//Запись старых участников
			final List<NodeRef> members = getEventMembers(event);
			if (members != null) {
				transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
					@Override
					public Void execute() throws Throwable {
						for (NodeRef member : members) {
							nodeService.createAssociation(event, member, EventsService.ASSOC_EVENT_OLD_MEMBERS);
						}
						return null;
					}
				}, false, true);
			}

			//Создание повторных
			Boolean repeatable = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE);
			Boolean isRepeated = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_IS_REPEATED);
			if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) { //Создание повторных происходит сразу только для мероприятий
				if (repeatable != null && repeatable && (isRepeated == null || !isRepeated)) {
					final String ruleContent = (String) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE_RULE);
					final Date startPeriod = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE_START_PERIOD);
					final Date endPeriod = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE_END_PERIOD);

					final Date startEventDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
					final Date endEvenDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_TO_DATE);

					if (ruleContent != null && startPeriod != null && endPeriod != null) {
						try {
							JSONObject rule = new JSONObject(ruleContent);
							String type = rule.getString("type");
							JSONArray data = rule.getJSONArray("data");

							List<Integer> weekDays = new ArrayList<>();
							List<Integer> monthDays = new ArrayList<>();

							if (type.equals(WEEK_DAYS)) {
								for (int i = 0; i < data.length(); i++) {
									if (data.getInt(i) == 7) {
										weekDays.add(Calendar.SUNDAY);
									} else {
										weekDays.add(data.getInt(i) + 1);
									}
								}
							} else if (type.equals(MONTH_DAYS)) {
								for (int i = 0; i < data.length(); i++) {
									monthDays.add(data.getInt(i));
								}
							}

							final List<Integer> weekDaysFinal = weekDays;
							final List<Integer> monthDaysFinal = monthDays;

							final Date eventFromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);

							transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
								@Override
								public Void execute() throws Throwable {
									NodeRef lastCreatedEvent = null;

									final Calendar calStart = Calendar.getInstance();
									calStart.setTime(startPeriod);

									Calendar fromCal = Calendar.getInstance();
									fromCal.setTime(startEventDate);
									calStart.set(Calendar.HOUR_OF_DAY, fromCal.get(Calendar.HOUR_OF_DAY));
									calStart.set(Calendar.MINUTE, fromCal.get(Calendar.MINUTE));

									Calendar calEnd = Calendar.getInstance();
									calEnd.setTime(endPeriod);

									Calendar toCal = Calendar.getInstance();
									toCal.setTime(endEvenDate);
									calEnd.set(Calendar.HOUR_OF_DAY, toCal.get(Calendar.HOUR_OF_DAY));
									calEnd.set(Calendar.MINUTE, toCal.get(Calendar.MINUTE));

									boolean createdEventConnection = false;

									while (calStart.before(calEnd)) {
										int weekDay = calStart.get(Calendar.DAY_OF_WEEK);
										int monthDay = calStart.get(Calendar.DAY_OF_MONTH);
										if (weekDaysFinal.contains(weekDay) || monthDaysFinal.contains(monthDay)) {

											QName docType = nodeService.getType(event);
											NodeRef parentRef = nodeService.getPrimaryParent(event).getParentRef();
											QName assocQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
											Map<QName, Serializable> properties = copyProperties(event, calStart.getTime());

											if (properties != null) {
												// создаем ноду
												ChildAssociationRef createdNodeAssoc = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQname, docType, properties);
												NodeRef createdEvent = createdNodeAssoc.getChildRef();
												documentConnectionService.createRootFolder(createdEvent);
												copyAssocs(event, createdEvent);

												nodeService.createAssociation(event, createdEvent, EventsService.ASSOC_EVENT_REPEATED_EVENTS);

												Date createdFromDate = (Date) nodeService.getProperty(createdEvent, EventsService.PROP_EVENT_FROM_DATE);
												Date lastCreatedFromDate = null;
												if (lastCreatedEvent != null) {
													lastCreatedFromDate = (Date) nodeService.getProperty(lastCreatedEvent, EventsService.PROP_EVENT_FROM_DATE);
												}

												if ((lastCreatedFromDate == null || lastCreatedFromDate.before(eventFromDate)) && createdFromDate.after(eventFromDate)) {
													if (lastCreatedEvent != null) {
														documentConnectionService.createConnection(lastCreatedEvent, event, "hasRepeated", true);
														nodeService.createAssociation(lastCreatedEvent, event, EventsService.ASSOC_NEXT_REPEATED_EVENT);
													}
													documentConnectionService.createConnection(event, createdEvent, "hasRepeated", true);
													nodeService.createAssociation(event, createdEvent, EventsService.ASSOC_NEXT_REPEATED_EVENT);

													createdEventConnection = true;
												} else if (lastCreatedEvent != null) {
													documentConnectionService.createConnection(lastCreatedEvent, createdEvent, "hasRepeated", true);
													nodeService.createAssociation(lastCreatedEvent, createdEvent, EventsService.ASSOC_NEXT_REPEATED_EVENT);
												}

												lastCreatedEvent = createdEvent;
											}
										}

										calStart.add(Calendar.DAY_OF_YEAR, 1);
									}

									if (!createdEventConnection && lastCreatedEvent != null) {
										documentConnectionService.createConnection(lastCreatedEvent, event, "hasRepeated", true);
										nodeService.createAssociation(lastCreatedEvent, event, EventsService.ASSOC_NEXT_REPEATED_EVENT);
									}

									return null;
								}
							}, false, true);

						} catch (JSONException e) {
							logger.error("Error parse repeatable rule", e);
						}
					}
				}
			}
		}

		public Map<QName, Serializable> copyProperties(NodeRef event, Date newStartDate) {
			Map<QName, Serializable> oldProperties = nodeService.getProperties(event);
			Map<QName, Serializable> newProperties = new HashMap<>();

			Date dateFrom = (Date) oldProperties.get(EventsService.PROP_EVENT_FROM_DATE);
			int dayCount = daysBetween(dateFrom, newStartDate);
			if (dayCount != 0) {
				// копируем свойства
				List<QName> propertiesToCopy = new ArrayList<>();
				propertiesToCopy.add(EventsService.PROP_EVENT_TITLE);
				propertiesToCopy.add(EventsService.PROP_EVENT_ALL_DAY);
				propertiesToCopy.add(EventsService.PROP_EVENT_DESCRIPTION);
				propertiesToCopy.add(EventsService.PROP_EVENT_MEMBERS_MANDATORY_JSON);

				propertiesToCopy.add(EventsService.PROP_EVENT_REPEATABLE);
				propertiesToCopy.add(EventsService.PROP_EVENT_REPEATABLE_RULE);
				propertiesToCopy.add(EventsService.PROP_EVENT_REPEATABLE_START_PERIOD);
				propertiesToCopy.add(EventsService.PROP_EVENT_REPEATABLE_END_PERIOD);

				for (QName propName : propertiesToCopy) {
					newProperties.put(propName, oldProperties.get(propName));
				}

				Calendar calFrom = Calendar.getInstance();
				calFrom.setTime(dateFrom);
				calFrom.add(Calendar.DAY_OF_YEAR, dayCount);
				newProperties.put(EventsService.PROP_EVENT_FROM_DATE, calFrom.getTime());

				Calendar calTo = Calendar.getInstance();
				calTo.setTime((Date) oldProperties.get(EventsService.PROP_EVENT_TO_DATE));
				calTo.add(Calendar.DAY_OF_YEAR, dayCount);
				newProperties.put(EventsService.PROP_EVENT_TO_DATE, calTo.getTime());

				newProperties.put(EventsService.PROP_EVENT_IS_REPEATED, true);

				return newProperties;
			} else {
				return null;
			}
		}

		public void copyAssocs(NodeRef oldEvent, NodeRef newEvent) {
			List<QName> assocsToCopy = new ArrayList<>();
			assocsToCopy.add(EventsService.ASSOC_EVENT_INITIATOR);
			assocsToCopy.add(EventsService.ASSOC_EVENT_LOCATION);
			assocsToCopy.add(EventsService.ASSOC_EVENT_INVITED_MEMBERS);
			assocsToCopy.add(EventsService.ASSOC_EVENT_TEMP_MEMBERS);
			assocsToCopy.add(EventsService.ASSOC_EVENT_TEMP_RESOURCES);
			assocsToCopy.add(EventsService.ASSOC_EVENT_SUBJECT);

			for (QName assocQName : assocsToCopy) {
				List<NodeRef> targets = findNodesByAssociationRef(oldEvent, assocQName, null, ASSOCIATION_TYPE.TARGET);
				nodeService.setAssociations(newEvent, assocQName, targets);
			}
		}

		private int daysBetween(Date d1, Date d2) {
			return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
		}

		@Override
		public void afterRollback() {

		}
	}

	private class Action {

		static final String CREATE = "CREATE";
		static final String UPDATE = "UPDATE";
		static final String REMOVE = "REMOVE";

		private NodeRef event;
		private Boolean created;
		private Boolean canceled;
		private final Map<NodeRef, String> recipients = new HashMap<>();

		public Boolean getCreateRepeated() {
			return created;
		}

		public void setCreateRepeated(Boolean createRepeated) {
			this.created = createRepeated;
		}

		public Map<NodeRef, String> getRecipients() {
			return recipients;
		}

		public NodeRef getEvent() {
			return event;
		}

		public Action(NodeRef event) {
			this.event = event;
			this.created = false;
			this.canceled = false;
		}

	}
}
