package ru.it.lecm.meetings.beans;


import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.meetings.utils.Translit;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.workflow.routes.api.RoutesService;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author vkuprin
 */
public class MeetingsServiceImpl extends BaseBean implements MeetingsService {

	private final static Logger logger = LoggerFactory.getLogger(MeetingsServiceImpl.class);

	private static final String MEETINGS_TRANSACTION_LISTENER = "meetings_transaction_listaner";

	private final static String WEEK_DAYS = "week-days";
	private final static String MONTH_DAYS = "month-days";

	private WorkflowService workflowService;
	private PersonService personService;
	private StateMachineServiceBean stateMachineService;
	private BusinessJournalService businessJournalService;
	private DocumentTableService documentTableService;
	private DocumentService documentService;
	private DocumentConnectionService documentConnectionService;
	private final TransactionListener transactionListener = new MeetingsServiceTransactionListener();
	private EventsService eventsService;
	private RoutesService routesService;

	public RoutesService getRoutesService() {
		return routesService;
	}
	private SiteService siteService;
	private AuthorityService authorityService;
	private OrgstructureBean orgstructureService;
	private NotificationsService notificationsService;

	public void setRoutesService(RoutesService routesService) {
		this.routesService = routesService;
	}
	
	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}
	
	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}
	
	public void setAuthorityService(AuthorityService authorityService){
		this.authorityService = authorityService;
	}
	
	public void setSiteService(SiteService siteService){
		this.siteService = siteService;
	}
	
	public EventsService getEventsService() {
		return eventsService;
	}

	public void setEventsService(EventsService eventsService) {
		this.eventsService = eventsService;
	}

	public void init() {
		List<QName> propertiesToCopy = new ArrayList<>();
		propertiesToCopy.add(EventsService.PROP_EVENT_TITLE);
		propertiesToCopy.add(EventsService.PROP_EVENT_DESCRIPTION);
		propertiesToCopy.add(EventsService.PROP_EVENT_MEMBERS_MANDATORY_JSON);
		propertiesToCopy.add(EventsService.PROP_EVENT_ALL_DAY);
		propertiesToCopy.add(PROP_MEETINGS_APPROVE_AGENDA);

		List<QName> assocsToCopy = new ArrayList<>();
		assocsToCopy.add(EventsService.ASSOC_EVENT_LOCATION);
		assocsToCopy.add(EventsService.ASSOC_EVENT_INITIATOR);
		assocsToCopy.add(EventsService.ASSOC_EVENT_INVITED_MEMBERS);
		assocsToCopy.add(EventsService.ASSOC_EVENT_SUBJECT);
		assocsToCopy.add(ASSOC_MEETINGS_CHAIRMAN);
		assocsToCopy.add(ASSOC_MEETINGS_SECRETARY);

		eventsService.addUpdateType(TYPE_MEETINGS_DOCUMENT, propertiesToCopy, assocsToCopy);

	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public DocumentConnectionService getDocumentConnectionService() {
		return documentConnectionService;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public BusinessJournalService getBusinessJournalService() {
		return businessJournalService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public StateMachineServiceBean getStateMachineService() {
		return stateMachineService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(MEETINGS_ROOT_ID);
	}

	@Override
	public void createRepetedMeetings(NodeRef meeting) {

		AlfrescoTransactionSupport.bindListener(this.transactionListener);

		List<NodeRef> pendingActions = AlfrescoTransactionSupport.getResource(MEETINGS_TRANSACTION_LISTENER);
		if (pendingActions == null) {
			pendingActions = new ArrayList<>();
			AlfrescoTransactionSupport.bindResource(MEETINGS_TRANSACTION_LISTENER, pendingActions);
		}

		if (!pendingActions.contains(meeting)) {
			pendingActions.add(meeting);
		}

	}

	public Map<QName, Serializable> copyProperties(NodeRef meeting, Date newStartDate) {
		Map<QName, Serializable> oldProperties = nodeService.getProperties(meeting);
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
			propertiesToCopy.add(PROP_MEETINGS_APPROVE_AGENDA);

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
			newProperties.put(EventsService.PROP_EVENT_SEND_NOTIFICATIONS, false);
			newProperties.put(EventsService.PROP_EVENT_SHOW_IN_CALENDAR, true);

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

		assocsToCopy.add(ASSOC_MEETINGS_CHAIRMAN);
		assocsToCopy.add(ASSOC_MEETINGS_SECRETARY);

		for (QName assocQName : assocsToCopy) {
			List<NodeRef> targets = findNodesByAssociationRef(oldEvent, assocQName, null, ASSOCIATION_TYPE.TARGET);
			nodeService.setAssociations(newEvent, assocQName, targets);
		}
	}

	private int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	@Override
	public NodeRef getHoldingItemsTable(NodeRef meeting) {
		return documentTableService.getTable(meeting, TYPE_MEETINGS_TS_HOLDING_TABLE);
	}

	@Override
	public List<NodeRef> getMeetingHoldingItems(NodeRef meeting) {
		NodeRef table = getHoldingItemsTable(meeting);
		if (table != null) {
			return documentTableService.getTableDataRows(table);
		}
		return null;
	}

	public NodeRef getAgendaItemsTable(NodeRef meeting) {
		return documentTableService.getTable(meeting, TYPE_MEETINGS_TS_AGENDA_TABLE);
	}

	@Override
	public List<NodeRef> getMeetingAgendaItems(NodeRef meeting) {
		NodeRef table = getAgendaItemsTable(meeting);
		if (table != null) {
			return documentTableService.getTableDataRows(table);
		}
		return null;
	}

	@Override
	public List<NodeRef> getHoldingTechnicalMembers(NodeRef meeting) {
		List<NodeRef> result = new ArrayList<>();
		result.addAll(findNodesByAssociationRef(meeting, ASSOC_MEETINGS_HOLDING_MEMBERS, null, ASSOCIATION_TYPE.TARGET));
		result.addAll(findNodesByAssociationRef(meeting, ASSOC_MEETINGS_HOLDING_INVITED_MEMBERS, null, ASSOCIATION_TYPE.TARGET));
		return result;
	}

	@Override
	public NodeRef createNewHoldingItem(NodeRef meeting) {
		NodeRef table = getHoldingItemsTable(meeting);
		if (table != null) {
			try {
				Map<QName, Serializable> properties = new HashMap<>(1);
				properties.put(TYPE_MEETINGS_TS_HOLDING_ITEM_START_TIME, new Date());
				return createNode(table, TYPE_MEETINGS_TS_HOLDING_ITEM, null, properties);
			} catch (WriteTransactionNeededException ex) {
				logger.error("Error create new meeting item", ex);
			}

		}
		return null;
	}

	@Override
	public void updateAgendaItemMembers(NodeRef document) {
		if (null != document) {
			List<NodeRef> items = documentTableService.getTableDataRows(documentTableService.getTable(document, MeetingsService.TYPE_MEETINGS_TS_AGENDA_TABLE));
			NodeRef secretary = findNodeByAssociationRef(document, MeetingsService.ASSOC_MEETINGS_SECRETARY, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			NodeRef chairman = findNodeByAssociationRef(document, MeetingsService.ASSOC_MEETINGS_CHAIRMAN, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			List<NodeRef> members = findNodesByAssociationRef(document, EventsService.ASSOC_EVENT_TEMP_MEMBERS, null, ASSOCIATION_TYPE.TARGET);
			List<NodeRef> invitedMembers = findNodesByAssociationRef(document, EventsService.ASSOC_EVENT_INVITED_MEMBERS, null, ASSOCIATION_TYPE.TARGET);

			for (NodeRef item : items) {
				List<NodeRef> persons = findNodesByAssociationRef(item, MeetingsService.ASSOC_MEETINGS_TS_ITEM_COREPORTER, null, ASSOCIATION_TYPE.TARGET);
				persons.add(findNodeByAssociationRef(item, MeetingsService.ASSOC_MEETINGS_TS_ITEM_REPORTER, null, ASSOCIATION_TYPE.TARGET));
				for (NodeRef person : persons) {
					QName type = nodeService.getType(person);
					if (OrgstructureBean.TYPE_EMPLOYEE.isMatch(type)) {
						if (!person.equals(secretary) && !person.equals(chairman) && !members.contains(person)) {
							nodeService.createAssociation(document, person, EventsService.ASSOC_EVENT_TEMP_MEMBERS);
							members.add(person);
						}
					} else if (Contractors.TYPE_REPRESENTATIVE.isMatch(type)) {
						if (!invitedMembers.contains(person)) {
							nodeService.createAssociation(document, person, EventsService.ASSOC_EVENT_INVITED_MEMBERS);
						}
					}
				}
			}
		}
	}

	@Override
	public String getAgendaInfo(NodeRef meeting) {

		JSONObject result = new JSONObject();
		try {
			if (nodeService.getType(meeting).isMatch(TYPE_MEETINGS_DOCUMENT)) {
				NodeRef table = documentTableService.getTable(meeting, TYPE_MEETINGS_TS_AGENDA_TABLE);
				if (null != table) {
					List<NodeRef> rows = documentTableService.getTableDataRows(table);
					if (null != rows) {
						result.put("size", rows.size());
						Boolean isDeleted = Boolean.TRUE.equals(nodeService.getProperty(meeting, EventsService.PROP_EVENT_REMOVED));
						if (!isDeleted) {
							Boolean approveAgenda = (Boolean)nodeService.getProperty(meeting, PROP_MEETINGS_APPROVE_AGENDA);
							if (approveAgenda) {
								result.put("status", "approvement_not_needed");
								result.put("hideStatus", false);
							} else {
								String approvalState = routesService.getApprovalState(meeting);
								if (null == approvalState || approvalState.isEmpty() || "UNDEF".equals(approvalState) ){
									approvalState = "NEW";
								}
								result.put("status", approvalState);
								result.put("hideStatus", rows.size() <= 0);
							}
						} else {
							result.put("status", "DELETED");
							result.put("hideStatus", true);
						}
					}
				}
			}
		} catch (JSONException ex) {
			java.util.logging.Logger.getLogger(MeetingsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result.toString();
	}

	@Override
	public void deleteHoldingItem(NodeRef nodeRef) {
		nodeService.deleteNode(nodeRef);
	}

	private class MeetingsServiceTransactionListener implements TransactionListener {

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
			final List<NodeRef> pendingDocs = AlfrescoTransactionSupport.getResource(MEETINGS_TRANSACTION_LISTENER);
			if (pendingDocs != null) {
				transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
					@Override
					public Void execute() throws Throwable {
						while (!pendingDocs.isEmpty()) {
							final NodeRef meeting = pendingDocs.remove(0);

							Boolean repeatable = (Boolean) nodeService.getProperty(meeting, EventsService.PROP_EVENT_REPEATABLE);
							Boolean isRepeated = (Boolean) nodeService.getProperty(meeting, EventsService.PROP_EVENT_IS_REPEATED);
							if (repeatable != null && repeatable && (isRepeated == null || !isRepeated)) {

								final String ruleContent = (String) nodeService.getProperty(meeting, EventsService.PROP_EVENT_REPEATABLE_RULE);
								final Date startPeriod = (Date) nodeService.getProperty(meeting, EventsService.PROP_EVENT_REPEATABLE_START_PERIOD);
								final Date endPeriod = (Date) nodeService.getProperty(meeting, EventsService.PROP_EVENT_REPEATABLE_END_PERIOD);

								final Date startEventDate = (Date) nodeService.getProperty(meeting, EventsService.PROP_EVENT_FROM_DATE);
								final Date endEvenDate = (Date) nodeService.getProperty(meeting, EventsService.PROP_EVENT_TO_DATE);

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

										final Date eventFromDate = (Date) nodeService.getProperty(meeting, EventsService.PROP_EVENT_FROM_DATE);

										transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
											@Override
											public Void execute() throws Throwable {
												NodeRef lastCreatedEvent = null;

												final Calendar calStart = Calendar.getInstance();
												calStart.setTime(startPeriod);

												Calendar fromCal = Calendar.getInstance();
												fromCal.setTime(startEventDate);
												calStart.set(Calendar.HOUR_OF_DAY, fromCal.get(Calendar.HOUR_OF_DAY));
												calStart.set(Calendar.MINUTE, fromCal.get(Calendar.HOUR_OF_DAY));

												Calendar calEnd = Calendar.getInstance();
												calEnd.setTime(endPeriod);

												Calendar toCal = Calendar.getInstance();
												toCal.setTime(endEvenDate);
												calEnd.set(Calendar.HOUR_OF_DAY, toCal.get(Calendar.HOUR_OF_DAY));
												calEnd.set(Calendar.MINUTE, toCal.get(Calendar.HOUR_OF_DAY));

												boolean createdEventConnection = false;

												while (calStart.before(calEnd)) {
													int weekDay = calStart.get(Calendar.DAY_OF_WEEK);
													int monthDay = calStart.get(Calendar.DAY_OF_MONTH);
													if (weekDaysFinal.contains(weekDay) || monthDaysFinal.contains(monthDay)) {

														QName docType = nodeService.getType(meeting);
														NodeRef parentRef = documentService.getDraftRootByType(docType);
														QName assocQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
														Map<QName, Serializable> properties = copyProperties(meeting, calStart.getTime());

														if (properties != null) {
															// создаем ноду
															ChildAssociationRef createdNodeAssoc = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQname, docType, properties);
															NodeRef createdEvent = createdNodeAssoc.getChildRef();
															documentConnectionService.createRootFolder(createdEvent);
															copyAssocs(meeting, createdEvent);

															nodeService.createAssociation(meeting, createdEvent, EventsService.ASSOC_EVENT_REPEATED_EVENTS);

															Date createdFromDate = (Date) nodeService.getProperty(createdEvent, EventsService.PROP_EVENT_FROM_DATE);
															Date lastCreatedFromDate = null;
															if (lastCreatedEvent != null) {
																lastCreatedFromDate = (Date) nodeService.getProperty(lastCreatedEvent, EventsService.PROP_EVENT_FROM_DATE);
															}

															if ((lastCreatedFromDate == null || lastCreatedFromDate.before(eventFromDate)) && createdFromDate.after(eventFromDate)) {
																if (lastCreatedEvent != null) {
																	documentConnectionService.createConnection(lastCreatedEvent, meeting, "hasRepeated", true);
																	nodeService.createAssociation(lastCreatedEvent, meeting, EventsService.ASSOC_NEXT_REPEATED_EVENT);
																}
																documentConnectionService.createConnection(meeting, createdEvent, "hasRepeated", true);
																nodeService.createAssociation(meeting, createdEvent, EventsService.ASSOC_NEXT_REPEATED_EVENT);

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
													documentConnectionService.createConnection(lastCreatedEvent, meeting, "hasRepeated", true);
													nodeService.createAssociation(lastCreatedEvent, meeting, EventsService.ASSOC_NEXT_REPEATED_EVENT);
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
						return null;
					}
				}, true, true);
			}
		}

		@Override
		public void afterRollback() {

		}

	}
	
	@Override
	public String editAgendaItemWorkspace(NodeRef agendaItem, boolean newWorkspace){
		//получим совещание  
		NodeRef meeting = documentTableService.getDocumentByTableDataRow(agendaItem);
		final int maxSiteShortNameLength = 65;
		final int maxSiteNameLength = 100;
		NodeRef site = null;
		if (MeetingsService.TYPE_MEETINGS_DOCUMENT.equals(nodeService.getType(meeting))){
			if (newWorkspace){
				String meetingTitle = (String) nodeService.getProperty(meeting, EventsService.PROP_EVENT_TITLE);
				String agendaItemName = (String) nodeService.getProperty(agendaItem, MeetingsService.PROP_MEETINGS_TS_ITEM_NAME);
				String siteName = meetingTitle + ", пункт " + agendaItemName;
				siteName = siteName.length() <= maxSiteNameLength ? siteName : siteName.substring(0, maxSiteNameLength);
				Integer agendaItemNumber = (Integer) nodeService.getProperty(agendaItem, DocumentTableService.PROP_INDEX_TABLE_ROW);
				String siteShortName = Translit.toTranslit(meetingTitle);
				siteShortName = delNoDigOrLet(siteShortName);
				siteShortName = siteShortName.length() <= maxSiteShortNameLength ? siteShortName : siteShortName.substring(0, maxSiteShortNameLength);
				siteShortName += "-" + agendaItemNumber.toString();
				if (siteService.hasSite(siteShortName)){
					Integer i = 1;
					while (true){
						String newSiteShortName = siteShortName + "-" + i.toString();
						if (!siteService.hasSite(newSiteShortName)){
							siteShortName = newSiteShortName;
							break;
						}
						i++;
					}
				}
				//создадим сайт
				SiteInfo siteInfo = siteService.createSite("site-dashboard", siteShortName, siteName, "", SiteVisibility.PUBLIC);
				site = siteInfo.getNodeRef();
				//свяжем сайт с пунктом повестки
				List<NodeRef> targetList = new ArrayList<>();
				targetList.add(site);
				nodeService.setAssociations(agendaItem, MeetingsService.ASSOC_MEETINGS_TS_ITEM_SITE, targetList);
			}
			
			if (!newWorkspace){
				List<NodeRef> siteList = new ArrayList<>();
				siteList.addAll(findNodesByAssociationRef(agendaItem, ASSOC_MEETINGS_TS_ITEM_SITE, null, ASSOCIATION_TYPE.TARGET));
				if (!siteList.isEmpty()){
					site = siteList.get(0);
				}
			}

			if (null != site){
				//выдадим права секретарю и инициатору
				List<NodeRef> secretaryAndInitiatorList = new ArrayList<>();
				secretaryAndInitiatorList.addAll(findNodesByAssociationRef(meeting, ASSOC_MEETINGS_SECRETARY, null, ASSOCIATION_TYPE.TARGET));
				secretaryAndInitiatorList.addAll(findNodesByAssociationRef(meeting, EventsService.ASSOC_EVENT_INITIATOR, null, ASSOCIATION_TYPE.TARGET));

				String siteShortName = siteService.getSiteShortName(site);

				for (NodeRef si : secretaryAndInitiatorList){
					addAuthorityToSite(siteShortName, SiteModel.SITE_MANAGER, si);
				}

				//выдадим права докладчику и содокладчикам
				List<NodeRef> reporterAndCoreporterList = new ArrayList<>();
				reporterAndCoreporterList.addAll(findNodesByAssociationRef(agendaItem, ASSOC_MEETINGS_TS_ITEM_REPORTER, null, ASSOCIATION_TYPE.TARGET));
				reporterAndCoreporterList.addAll(findNodesByAssociationRef(agendaItem, ASSOC_MEETINGS_TS_ITEM_COREPORTER, null, ASSOCIATION_TYPE.TARGET));

				for (NodeRef reporter : reporterAndCoreporterList){
					if (orgstructureService.isEmployee(reporter)){
						if ( addAuthorityToSite(siteShortName, SiteModel.SITE_COLLABORATOR, reporter) ){
							List<NodeRef> recipients = new ArrayList();
							recipients.add(reporter);
							sendNotificationAboutInviteToSite(site, siteShortName, recipients);
						}
					}
				}
			}
		}
		
		if (newWorkspace){
			return null != site ? siteService.getSiteShortName(site) : "";
		} else {
			return "";
		}
	}
	
	private void sendNotificationAboutInviteToSite(NodeRef site, String siteShortName, List<NodeRef> recipients){
		SysAdminParams params = serviceRegistry.getSysAdminParams();
		Map<String, Object> objects = new HashMap<>();
		String url = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
		url = url + getUrlService().getLinkWithContext("/page/site/" + siteShortName + "/dashboard");
		objects.put("url", url);
		objects.put("eventExecutor", orgstructureService.getCurrentEmployee());

		notificationsService.sendNotificationByTemplate(site, recipients, "MEETINGS_INVITE_TO_SITE", objects);
	}
	
	private boolean addAuthorityToSite(final String siteShortName, final String permissionGroup, final NodeRef employee){
		String managerAuthority = null;
		boolean result = false;
		Map<String, String> membersList = siteService.listMembers(siteShortName, null, null, 0);
		Set<String> memberKeys = membersList.keySet();
		for (String memberKey : memberKeys){
			if (SiteModel.SITE_MANAGER.equals(membersList.get(memberKey))){
				managerAuthority = memberKey;
				break;
			}
		}
		if (null != managerAuthority){
			final String authority = orgstructureService.getEmployeeLogin(employee);
			if (!siteService.isMember(siteShortName, authority)){
				// выдавать права может только менеджер сайта
				result = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Boolean>(){

					@Override
					public Boolean doWork() throws Exception {
						if (siteService.canAddMember(siteShortName, authority, permissionGroup)){
							siteService.setMembership(siteShortName, authority, permissionGroup);
							return true;
						}
						return false;
					}

				},
				managerAuthority);
			}
		}
		return result;
	}
	
	private static String delNoDigOrLet (String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }
	
}

