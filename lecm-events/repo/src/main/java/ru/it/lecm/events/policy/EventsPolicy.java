package ru.it.lecm.events.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin Date: 01.04.2015 Time: 9:50
 */
public class EventsPolicy extends BaseBean {

	private final static Logger logger = LoggerFactory.getLogger(EventsPolicy.class);

	private PolicyComponent policyComponent;
	private DocumentTableService documentTableService;
	private LecmPermissionService lecmPermissionService;
	private EventsService eventService;
	private NotificationsService notificationsService;
	private DocumentConnectionService documentConnectionService;
	private TemplateService templateService;
	private JavaMailSender mailService;
	private OrgstructureBean orgstructureBean;
	private ContentService contentService;
	private String defaultFromEmail;
	private TransactionListener transactionListener;

	private static final String EVENTS_TRANSACTION_LISTENER = "events_transaction_listaner";

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private final static String WEEK_DAYS = "week-days";
	private final static String MONTH_DAYS = "month-days";

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setEventService(EventsService eventService) {
		this.eventService = eventService;
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

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setDefaultFromEmail(String defaultFromEmail) {
		this.defaultFromEmail = defaultFromEmail;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public final void init() {
		transactionListener = new EventPolicyTransactionListener();

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_MEMBERS,
				new JavaBehaviour(this, "onCreateAddMembers", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_MEMBERS,
				new JavaBehaviour(this, "onRemoveMember", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_RESOURCES,
				new JavaBehaviour(this, "onCreateAddResources", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_RESOURCES,
				new JavaBehaviour(this, "onRemoveResources", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				EventsService.TYPE_EVENT,
				new JavaBehaviour(this, "onCreateEvent", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				EventsService.TYPE_EVENT,
				new JavaBehaviour(this, "onUpdateEvent", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onCreateAddMembers(AssociationRef nodeAssocRef) {
		//Мероприятие
		NodeRef event = nodeAssocRef.getSourceRef();
		//Участник
		NodeRef member = nodeAssocRef.getTargetRef();

		lecmPermissionService.grantDynamicRole("EVENTS_MEMBER_DYN", event, member.getId(), lecmPermissionService.findPermissionGroup("LECM_BASIC_PG_ActionPerformer"));
		//Отправка уведомления
		Boolean isRepeated = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_IS_REPEATED);
		Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
		sendNotifications = null == sendNotifications ? false : sendNotifications;
		if (sendNotifications && (isRepeated == null || !isRepeated)) {
			NodeRef initiator = eventService.getEventInitiator(event);
			if (initiator != null) {
//				String author = AuthenticationUtil.getSystemUserName();
//				String employeeName = (String) nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
//				Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
//				String objectType;
//				if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) {
//					objectType = "мероприятие ";
//				} else {
//					objectType = "совещание ";
//				}
				//String text = employeeName + " приглашает на " + objectType + eventService.wrapAsEventLink(event) + ". Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
				List<NodeRef> recipients = new ArrayList<>();
				recipients.add(member);
				//notificationsService.sendNotification(author, event, text, recipients, null);
				eventService.sendNotificationsToMembers(event, true, recipients);
			}
		}

		NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
		if (tableDataRootFolder != null) {
			Set<QName> typeSet = new HashSet<>(1);
			typeSet.add(EventsService.TYPE_EVENT_MEMBERS_TABLE);
			List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
			if (childAssocs != null && childAssocs.size() == 1) {
				NodeRef table = childAssocs.get(0).getChildRef();
				if (table != null) {
					// создаем строку
					try {
						Map<QName, Serializable> props = new HashMap<>();
						props.put(EventsService.PROP_EVENT_MEMBERS_PARTICIPATION_REQUIRED, getMemberMandatory(event, member));
						NodeRef createdNode = createNode(table, EventsService.TYPE_EVENT_MEMBERS_TABLE_ROW, null, props);
						if (createdNode != null) {
							nodeService.createAssociation(createdNode, member, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE);
						}
					} catch (WriteTransactionNeededException ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		}
	}

	public void onRemoveMember(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef member = nodeAssocRef.getTargetRef();

		lecmPermissionService.revokeDynamicRole("EVENTS_MEMBER_DYN", event, member.getId());
		lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader), event, member);

		NodeRef tableRow = eventService.getMemberTableRow(event, member);
		if (tableRow != null) {
			nodeService.removeChild(nodeService.getPrimaryParent(tableRow).getParentRef(), tableRow);
		}

		Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
		sendNotifications = null == sendNotifications ? false : sendNotifications;
		if (sendNotifications) {
//			String objectType;
//			if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) {
//				objectType = "мероприятии ";
//			} else {
//				objectType = "совещании ";
//			}
//			String text = "Вам не требуется присутствовать на "+ objectType + eventService.wrapAsEventLink(event);
//			List<NodeRef> recipients = new ArrayList<>();
//			recipients.add(member);
			//notificationsService.sendNotification(AuthenticationUtil.getSystemUserName(), event, text, recipients, null, true);
			eventService.notifyAttendeeRemoved(event, member);
			
		}
	}

	public void onRemoveResources(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef resource = nodeAssocRef.getTargetRef();

		NodeRef tableRow = eventService.getResourceTableRow(event, resource);
		if (tableRow != null) {
			nodeService.removeChild(nodeService.getPrimaryParent(tableRow).getParentRef(), tableRow);
		}
	}

	private boolean getMemberMandatory(NodeRef event, NodeRef member) {
		return getMemberMandatory(member, (String) nodeService.getProperty(event, EventsService.PROP_EVENT_MEMBERS_MANDATORY_JSON));
	}

	private boolean getMemberMandatory(NodeRef member, String membersMandatory) {
		try {
			JSONArray membersMandatoryJson = new JSONArray(membersMandatory);
			for (int i = 0; i < membersMandatoryJson.length(); i++) {
				JSONObject obj = membersMandatoryJson.getJSONObject(i);
				if (obj != null && member.toString().equals(obj.get("nodeRef"))) {
					return obj.getBoolean("mandatory");
				}
			}
		} catch (JSONException e) {
			logger.error("Error parse members mandatory json", e);
		}
		return false;
	}

	public void onCreateAddResources(AssociationRef nodeAssocRef) {
		//Мероприятие
		NodeRef event = nodeAssocRef.getSourceRef();
		//Участник
		NodeRef resource = nodeAssocRef.getTargetRef();

		List<NodeRef> responsible = eventService.getResourceResponsible(resource);
		if (responsible != null) {
			for (NodeRef employee : responsible) {
				lecmPermissionService.grantDynamicRole("EVENTS_RESPONSIBLE_FOR_RESOURCES_DYN", event, employee.getId(), lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader));

				Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
				Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
				sendNotifications = null == sendNotifications ? false : sendNotifications;
				if (sendNotifications) {
					//Отправка уведомления
					String author = AuthenticationUtil.getSystemUserName();
					String text = "Запланированное " + eventService.wrapAsEventLink(event) + " требует привлечения ресурсов за которые вы ответственны. Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
					List<NodeRef> recipients = new ArrayList<>();
					recipients.add(employee);
					notificationsService.sendNotification(author, event, text, recipients, null);
				}
			}
		}

		NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
		if (tableDataRootFolder != null) {
			Set<QName> typeSet = new HashSet<>(1);
			typeSet.add(EventsService.TYPE_EVENT_RESOURCES_TABLE);
			List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
			if (childAssocs != null && childAssocs.size() == 1) {
				NodeRef table = childAssocs.get(0).getChildRef();
				if (table != null) {
					// создаем строку
					try {
						NodeRef createdNode = createNode(table, EventsService.TYPE_EVENT_RESOURCES_TABLE_ROW, null, null);
						if (createdNode != null) {
							nodeService.createAssociation(createdNode, resource, EventsService.ASSOC_EVENT_RESOURCES_TABLE_RESOURCE);
						}
					} catch (WriteTransactionNeededException ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		}
	}

	public void onCreateEvent(ChildAssociationRef childAssocRef) {
		NodeRef event = childAssocRef.getChildRef();

		AlfrescoTransactionSupport.bindListener(this.transactionListener);

		List<NodeRef> pendingActions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
		if (pendingActions == null) {
			pendingActions = new ArrayList<>();
			AlfrescoTransactionSupport.bindResource(EVENTS_TRANSACTION_LISTENER, pendingActions);
		}

		if (!pendingActions.contains(event)) {
			pendingActions.add(event);
		}
	}

	public void onUpdateEvent(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final String membersMandatoryJsonBefore = (String) before.get(EventsService.PROP_EVENT_MEMBERS_MANDATORY_JSON);
		final String membersMandatoryJsonAfter = (String) after.get(EventsService.PROP_EVENT_MEMBERS_MANDATORY_JSON);

		if (membersMandatoryJsonAfter != null && !membersMandatoryJsonAfter.equals(membersMandatoryJsonBefore)) {
			NodeRef membersTable = eventService.getMemberTable(nodeRef);

			if (membersTable != null) {
				List<NodeRef> rows = documentTableService.getTableDataRows(membersTable);
				if (rows != null) {
					for (NodeRef row : rows) {
						NodeRef rowEmployee = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
						if (rowEmployee != null) {
							nodeService.setProperty(row, EventsService.PROP_EVENT_MEMBERS_PARTICIPATION_REQUIRED, getMemberMandatory(rowEmployee, membersMandatoryJsonAfter));
						}
					}
				}
			}
		}

		//Дополнительное согласование при изменении времени
		final Date fromDateBefore = (Date) before.get(EventsService.PROP_EVENT_FROM_DATE);
		final Date fromDateAfter = (Date) after.get(EventsService.PROP_EVENT_FROM_DATE);
		final Date toDateBefore = (Date) before.get(EventsService.PROP_EVENT_TO_DATE);
		final Date toDateAfter = (Date) after.get(EventsService.PROP_EVENT_TO_DATE);

		if (!fromDateAfter.equals(fromDateBefore) || !toDateAfter.equals(toDateBefore)) {
			NodeRef table = eventService.getMemberTable(nodeRef);
			if (table != null) {
				List<NodeRef> rows = documentTableService.getTableDataRows(table);
				if (rows != null) {
					for (NodeRef row : rows) {
						Map<QName, Serializable> properties = nodeService.getProperties(row);
						properties.put(EventsService.PROP_EVENT_MEMBERS_STATUS, "EMPTY");
						properties.put(EventsService.PROP_EVENT_MEMBERS_DECLINE_REASON, null);
						properties.put(EventsService.PROP_EVENT_MEMBERS_FROM_DATE, null);
						properties.put(EventsService.PROP_EVENT_MEMBERS_TO_DATE, null);
						properties.put(EventsService.PROP_EVENT_MEMBERS_ALL_DAY, false);
						nodeService.setProperties(row, properties);
					}
				}
			}
		}
	}

	private class EventPolicyTransactionListener implements TransactionListener {

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
			List<NodeRef> pendingDocs = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
			if (pendingDocs != null) {
				while (!pendingDocs.isEmpty()) {
					final NodeRef event = pendingDocs.remove(0);

					//Запись старых участников
					final List<NodeRef> members = eventService.getEventMembers(event);
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
					Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
					sendNotifications = null == sendNotifications ? false : sendNotifications;
					if (sendNotifications) {
						//Рассылка уведомлений
						eventService.sendNotificationsToInvitedMembers(event, Boolean.TRUE);
					}
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
}
