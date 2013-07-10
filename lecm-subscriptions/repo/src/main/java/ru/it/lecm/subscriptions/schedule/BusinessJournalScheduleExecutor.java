package ru.it.lecm.subscriptions.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

import java.util.*;

/**
 * User: PMelnikov
 * Date: 17.01.13
 * Time: 11:09
 */
public class BusinessJournalScheduleExecutor extends ActionExecuterAbstractBase {

	private final static Logger logger = LoggerFactory.getLogger(BusinessJournalScheduleExecutor.class);

	/**
	 * The node service
	 */
	private NodeService nodeService;

	private SearchService searchService;

	private NamespaceService namespaceService;

	private NotificationsService notificationsService;

	private OrgstructureBean orgstructureService;

	private SubscriptionsService subscriptionsService;

	/**
	 * Set the node service
	 *
	 * @param nodeService  the node service
	 */
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setSubscriptionsService(SubscriptionsService subscriptionsService) {
		this.subscriptionsService = subscriptionsService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef bjRecordRef) {
		String author = (String) nodeService.getProperty(bjRecordRef, BusinessJournalService.PROP_BR_RECORD_INITIATOR);
		List<AssociationRef> initiatorAssocs = nodeService.getTargetAssocs(bjRecordRef, BusinessJournalService.ASSOC_BR_RECORD_INITIATOR);
		NodeRef initiator = null;
		if (initiatorAssocs != null && !initiatorAssocs.isEmpty()) {
			initiator = initiatorAssocs.get(0).getTargetRef();
		}
		String description = (String) nodeService.getProperty(bjRecordRef, BusinessJournalService.PROP_BR_RECORD_DESC);
		Date date = (Date) nodeService.getProperty(bjRecordRef, BusinessJournalService.PROP_BR_RECORD_DATE);
		NodeRef mainObject = nodeService.getTargetAssocs(bjRecordRef, BusinessJournalService.ASSOC_BR_RECORD_MAIN_OBJ).get(0).getTargetRef();
		Set<NodeRef> subscriptions = new HashSet<NodeRef>();
		subscriptions.addAll(findSubscriptionsToType(bjRecordRef));
		subscriptions.addAll(findSubscriptionsToInitiator(initiator));

		//При подписке на сотрудника и рабочую группу в нее должны попадать не те записи Б-Ж, в которых данный сотрудник является основным объектом, а те, в которых он является инициатором
		if (!this.orgstructureService.isEmployee(mainObject) && !this.orgstructureService.isWorkGroup(mainObject)) {
			//добавляем подписки на объект
			subscriptions.addAll(subscriptionsService.getSubscriptionsToObject(mainObject));
		}
		sendNotificationsBySubscriptions(subscriptions, author, initiator, description, mainObject, date);
		nodeService.addAspect(bjRecordRef, SubscriptionsService.ASPECT_SUBSCRIBED, null);
	}

	//обработка подписок на действия сотрудника/группы/подразделения
	private Set<NodeRef> findSubscriptionsToInitiator(NodeRef initiator) {
        Set<NodeRef> subscriptions = new HashSet<NodeRef>();
		if (initiator == null || !nodeService.exists(initiator)) {
			return subscriptions;
		}
		//заполнение списка возможных объектов подписки по инициатору
		Set<NodeRef> initiators = new HashSet<NodeRef>();
		initiators.add(initiator);
		//рабочие группы, в которые входит инициатор
		List<NodeRef> workGroups = orgstructureService.getEmployeeWorkGroups(initiator);
		initiators.addAll(workGroups);
		//подразделения, в которые входит инициатор (включая родительские)
		List<NodeRef> units = orgstructureService.getEmployeeUnits(initiator, false);
		for (NodeRef unit : units) {
			initiators.add(unit);
			while ((unit = orgstructureService.getParentUnit(unit)) != null) {
				if (!initiators.add(unit)) {
					break;
				}
			}
		}

		for (NodeRef initRef : initiators) {
			subscriptions.addAll(subscriptionsService.getSubscriptionsToObject(initRef));
		}
		return subscriptions;
	}

	private void sendNotificationsBySubscriptions(Set<NodeRef> subscriptions, String author, NodeRef initiatorRef, String description, NodeRef mainObject, Date date) {
		for (NodeRef subscription : subscriptions) {
			List<NodeRef> notificationTypes = assocsToCollection(subscription, SubscriptionsService.ASSOC_NOTIFICATION_TYPE);
			List<NodeRef> employees = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_EMPLOYEE);
			List<NodeRef> positions = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_POSITION);
			List<NodeRef> units = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_ORGANIZATION_UNIT);
			List<NodeRef> workgroups = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_WORK_GROUP);
			List<NodeRef> businessRoles = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_BUSINESS_ROLE);
			Notification notification = new Notification();
			notification.setObjectRef(mainObject);
			notification.setAutor(author);
			notification.setInitiatorRef(initiatorRef);
			notification.setDescription(description);
			notification.setFormingDate(date);
			notification.setTypeRefs(notificationTypes);
			notification.setRecipientEmployeeRefs(employees);
			notification.setRecipientPositionRefs(positions);
			notification.setRecipientOrganizationUnitRefs(units);
			notification.setRecipientWorkGroupRefs(workgroups);
			notification.setRecipientBusinessRoleRefs(businessRoles);
			notificationsService.sendNotification(notification);
		}
	}

	private Set<NodeRef> findSubscriptionsToType(NodeRef bjRecordRef) {
		NodeRef byType = null;
		List<AssociationRef> types = nodeService.getTargetAssocs(bjRecordRef, BusinessJournalService.ASSOC_BR_RECORD_OBJ_TYPE);
		if (types.size() == 1) {
			byType = types.get(0).getTargetRef();
		}
		NodeRef byCategory = null;
		List<AssociationRef> categories = nodeService.getTargetAssocs(bjRecordRef, BusinessJournalService.ASSOC_BR_RECORD_EVENT_CAT);
		if (categories.size() == 1) {
			byCategory = categories.get(0).getTargetRef();
		}
		NodeRef subscriptionsRoot = subscriptionsService.getSubscriptionRootRef();
		String path = nodeService.getPath(subscriptionsRoot).toPrefixString(namespaceService);
		String type = SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE.toPrefixString(namespaceService);

		String subscriptionType = SubscriptionsService.ASSOC_OBJECT_TYPE.toPrefixString(namespaceService) + "-ref";
		String subscriptionCategory = SubscriptionsService.ASSOC_EVENT_CATEGORY.toPrefixString(namespaceService) + "-ref";

		String typeAttribute = "@" + subscriptionType.replace(":", "\\:");
		String categoryAttribute = "@" + subscriptionCategory.replace(":", "\\:");

		String query = " +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\"";

		query += " AND (ISNULL:" + subscriptionType.replace(":", "\\:") + " OR "
				+ typeAttribute + ":\"\"";

		if (byType != null) {
			query += " OR (" + typeAttribute + ":\"" + byType.toString() + "\"";
			query += " AND (ISNULL:" + subscriptionCategory.replace(":", "\\:")
					+ " OR " + categoryAttribute + ":\"\"";
			if (byCategory != null) {
				query += " OR " + categoryAttribute + ":\"" + byCategory.toString() + "\"";
			}
			query += "))";
		}

		query += ")";

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery(query);
		ResultSet resultSet = null;
		Set<NodeRef> subscriptions = new HashSet<NodeRef>();
		try {
			resultSet = searchService.query(parameters);
			for (ResultSetRow row : resultSet) {
				subscriptions.add(row.getNodeRef());
			}
		} catch (LuceneQueryParserException ignored) {
		} catch (Exception e1) {
			logger.error("Error while send notification for business journal's record " + bjRecordRef.toString(), e1);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return subscriptions;
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
	}

	private List<NodeRef> assocsToCollection(NodeRef parent, QName assocName) {
		ArrayList<NodeRef> result = new ArrayList<NodeRef>();
		List<AssociationRef> refs = nodeService.getTargetAssocs(parent, assocName);
		for (AssociationRef ref : refs) {
			NodeRef assocNodeRef = ref.getTargetRef();
			result.add(assocNodeRef);
		}
		return result;
	}

}
