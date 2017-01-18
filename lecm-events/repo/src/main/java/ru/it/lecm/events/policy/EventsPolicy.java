package ru.it.lecm.events.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
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
	private DocumentMembersService documentMembersService;

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

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

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public final void init() {
		
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

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_INVITED_MEMBERS,
				new JavaBehaviour(this, "onCreateAddInvitedMember", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_INVITED_MEMBERS,
				new JavaBehaviour(this, "onRemoveInvitedMember", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_INITIATOR,
				new JavaBehaviour(this, "onCreateInitiator", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_INITIATOR,
				new JavaBehaviour(this, "onRemoveInitiator", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				EventsService.TYPE_EVENT,
				new JavaBehaviour(this, "onCreateEvent", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				EventsService.TYPE_EVENT,
				new JavaBehaviour(this, "onUpdateEvent", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onCreateAddInvitedMember(AssociationRef nodeAssocRef) {
		//Мероприятие
		NodeRef event = nodeAssocRef.getSourceRef();
		//Участник
		NodeRef member = nodeAssocRef.getTargetRef();

		//Отправка уведомления
		Boolean isRepeated = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_IS_REPEATED);
		Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
		sendNotifications = null == sendNotifications ? false : sendNotifications;
		if (sendNotifications && (isRepeated == null || !isRepeated)) {
			NodeRef initiator = eventService.getEventInitiator(event);
			if (initiator != null) {
				List<NodeRef> recipients = new ArrayList<>();
				recipients.add(member);
				//notificationsService.sendNotification(author, event, text, recipients, null);
				eventService.sendNotifications(event, true, recipients, false);
			}
		}
	}

	public void onRemoveInvitedMember(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef member = nodeAssocRef.getTargetRef();
		Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
		sendNotifications = null == sendNotifications ? false : sendNotifications;
		if (sendNotifications) {
			eventService.notifyAttendeeRemoved(event, member);
		}
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
				List<NodeRef> recipients = new ArrayList<>();
				recipients.add(member);
				eventService.sendNotifications(event, true, recipients, false);
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

				Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
				sendNotifications = null == sendNotifications ? false : sendNotifications;
				if (sendNotifications) {
					//Отправка уведомления
					List<NodeRef> recipients = new ArrayList<>();
					recipients.add(employee);
					notificationsService.sendNotificationByTemplate(event, recipients, "EVENTS.RESPONSIBLE_FOR_RESOURCES");
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
		eventService.createRepeated(event);
		Boolean sendNotifications = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_SEND_NOTIFICATIONS);
				sendNotifications = null == sendNotifications ? false : sendNotifications;
		if (sendNotifications) {
			eventService.sendNotifications(event, true, Arrays.asList(eventService.getEventInitiator(event)), false);
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

		if ((fromDateAfter != null && !fromDateAfter.equals(fromDateBefore)) || (toDateAfter != null && !toDateAfter.equals(toDateBefore))) {
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

	public void onCreateInitiator(AssociationRef nodeAssocRef) throws WriteTransactionNeededException {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef initiator = nodeAssocRef.getTargetRef();
		List<AssociationRef> authorAssocs = nodeService.getTargetAssocs(event, DocumentService.ASSOC_AUTHOR);

		if (authorAssocs != null && !authorAssocs.isEmpty()) {
			NodeRef author = authorAssocs.get(0).getTargetRef();

			if (!author.equals(initiator)) {
				lecmPermissionService.grantDynamicRole("EVENTS_INITIATOR_DYN", event, initiator.getId(), lecmPermissionService.findPermissionGroup("LECM_BASIC_PG_Owner"));
				documentMembersService.addMemberWithoutCheckPermission(event, initiator,  LecmPermissionService.LecmPermissionGroup.PGROLE_Reader, true);
			}
		}
	}

	public void onRemoveInitiator(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef initiator = nodeAssocRef.getTargetRef();
		List<AssociationRef> authorAssocs = nodeService.getTargetAssocs(event, DocumentService.ASSOC_AUTHOR);

		if (authorAssocs != null && !authorAssocs.isEmpty()) {
			NodeRef author = authorAssocs.get(0).getTargetRef();

			if (!author.equals(initiator)) {
				lecmPermissionService.revokeDynamicRole("EVENTS_INITIATOR_DYN", event, initiator.getId());
			}
		}
	}
}
