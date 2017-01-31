/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.subscriptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
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
import org.alfresco.service.transaction.TransactionService;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

/**
 *
 * @author ikhalikov
 */
public class RecordsListener implements MessageListener {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RecordsListener.class);

	private SearchService searchService;
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private NotificationsService notificationsService;
	private OrgstructureBean orgstructureService;
	private SubscriptionsService subscriptionsService;
	private TransactionService transactionService;

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setSubscriptionsService(SubscriptionsService subscriptionsService) {
		this.subscriptionsService = subscriptionsService;
	}

	@Override
	public void onMessage(final Message message) {
//		final ObjectMapper mapper = new ObjectMapper().disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
//
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//		transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
//
//			@Override
//			public Object execute() throws Throwable {
//				AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
//
//					@Override
//					public Object doWork() throws Exception {
//						BusinessJournalRecord rec = (BusinessJournalRecord) ((ObjectMessage) message).getObject();
//						executeSubscription(rec);
//						return null;
//					}
//				});
//				return null;
//			}
//		}, false, true);

	}

	protected void executeSubscription(BusinessJournalRecord record) {
		String author = record.getInitiatorText();
		NodeRef initiator = record.getInitiator();
		String description = record.getRecordDescription();
		Date date = record.getDate();
		NodeRef mainObject = record.getMainObject();

		Set<NodeRef> subscriptions = new HashSet<NodeRef>();
		subscriptions.addAll(findSubscriptionsToType(record.getObjectType(), record.getEventCategory()));
		subscriptions.addAll(findSubscriptionsToInitiator(initiator));

		//При подписке на сотрудника и рабочую группу в нее должны попадать не те записи Б-Ж, в которых данный сотрудник является основным объектом, а те, в которых он является инициатором
		if (mainObject != null && nodeService.exists(mainObject) && !this.orgstructureService.isEmployee(mainObject) && !this.orgstructureService.isWorkGroup(mainObject)) {
			//добавляем подписки на объект
			subscriptions.addAll(subscriptionsService.getSubscriptionsToObject(mainObject));
		}
		sendNotificationsBySubscriptions(subscriptions, author, initiator, description, mainObject, date);
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
			notification.setAuthor(author);
			notification.setInitiatorRef(initiatorRef);
			notification.setDescription(description);
			notification.setFormingDate(date);
			notification.setTypeRefs(notificationTypes);
			notification.setRecipientEmployeeRefs(employees);
			notification.setRecipientPositionRefs(positions);
			notification.setRecipientOrganizationUnitRefs(units);
			notification.setRecipientWorkGroupRefs(workgroups);
			notification.setRecipientBusinessRoleRefs(businessRoles);
			notificationsService.sendNotification(notification, true);
		}
	}

	private Set<NodeRef> findSubscriptionsToType(NodeRef byType, NodeRef byCategory) {
		NodeRef subscriptionsRoot = subscriptionsService.getSubscriptionRootRef();
		String path = nodeService.getPath(subscriptionsRoot).toPrefixString(namespaceService);
		String type = SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE.toPrefixString(namespaceService);

		String subscriptionType = SubscriptionsService.ASSOC_OBJECT_TYPE.toPrefixString(namespaceService) + "-ref";
		String subscriptionCategory = SubscriptionsService.ASSOC_EVENT_CATEGORY.toPrefixString(namespaceService) + "-ref";

		String typeAttribute = "@" + subscriptionType.replace(":", "\\:");
		String categoryAttribute = "@" + subscriptionCategory.replace(":", "\\:");

		String query = " +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\"";

		query += " AND ((ISNULL:" + subscriptionType.replace(":", "\\:") + " OR NOT EXISTS:" + subscriptionType.replace(":", "\\:") + ") OR "
				+ typeAttribute + ":\"\"";

		if (byType != null) {
			query += " OR (" + typeAttribute + ":\"" + byType.toString() + "\"";
			query += " AND ((ISNULL:" + subscriptionCategory.replace(":", "\\:") + " OR NOT EXISTS:" + subscriptionCategory.replace(":", "\\:")
					+ ") OR " + categoryAttribute + ":\"\"";
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
			logger.error("Error while send notification for business journal's record", e1);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return subscriptions;
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
