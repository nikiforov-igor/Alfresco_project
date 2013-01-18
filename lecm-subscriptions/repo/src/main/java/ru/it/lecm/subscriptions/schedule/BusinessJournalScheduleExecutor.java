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
import ru.it.lecm.subscriptions.beans.SubscriptionsBean;

import java.util.ArrayList;
import java.util.List;

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

	private SubscriptionsBean subscriptionsService;

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

	public void setSubscriptionsService(SubscriptionsBean subscriptionsService) {
		this.subscriptionsService = subscriptionsService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		String author = (String) nodeService.getProperty(nodeRef, BusinessJournalService.PROP_BR_RECORD_INITIATOR);
		String description = (String) nodeService.getProperty(nodeRef, BusinessJournalService.PROP_BR_RECORD_DESC);
		NodeRef mainObject = nodeService.getTargetAssocs(nodeRef, BusinessJournalService.ASSOC_BR_RECORD_MAIN_OBJ).get(0).getTargetRef();
		NodeRef byType = null;
		List<AssociationRef> types = nodeService.getTargetAssocs(nodeRef, BusinessJournalService.ASSOC_BR_RECORD_OBJ_TYPE);
		if (types.size() == 1) {
			byType = types.get(0).getTargetRef();
		}
		NodeRef byCategory = null;
		List<AssociationRef> categories = nodeService.getTargetAssocs(nodeRef, BusinessJournalService.ASSOC_BR_RECORD_EVENT_CAT);
		if (categories.size() == 1) {
			byCategory = categories.get(0).getTargetRef();
		}
		NodeRef subscriptionsRoot = subscriptionsService.getSubscriptionRootRef();
		String path = nodeService.getPath(subscriptionsRoot).toPrefixString(namespaceService);
		String type = SubscriptionsBean.TYPE_SUBSCRIPTION_TO_TYPE.toPrefixString(namespaceService);

		String subscriptionType = SubscriptionsBean.ASSOC_OBJECT_TYPE.toPrefixString(namespaceService) + "-ref";
		String subscriptionCategory = SubscriptionsBean.ASSOC_EVENT_CATEGORY.toPrefixString(namespaceService) + "-ref";

		String typeAttribute = "@" + subscriptionType.replace(":", "\\:");
		String categoryAttribute = "@" + subscriptionCategory.replace(":", "\\:");

		String query =  " +PATH:\"" + path + "//*\" AND TYPE:\"" + type +"\" ";
		if (byType != null && byCategory == null) {
			query += "AND " + typeAttribute +":\"" + byType.toString() + "\"";
		} else if (byType != null) {
			query += "AND " + typeAttribute +":\"" + byType.toString() + "\" AND (" + categoryAttribute +":\"" + byCategory.toString() + "\" OR ISNULL:\"" + subscriptionCategory + "\" OR " + categoryAttribute +":\"\")";
		}

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery(query);
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			for (ResultSetRow row : resultSet) {
				NodeRef subscription = row.getNodeRef();
				List<NodeRef> notificationTypes = assocsToCollection(subscription, SubscriptionsBean.ASSOC_NOTIFICATION_TYPE);
				List<NodeRef> employees = assocsToCollection(subscription, SubscriptionsBean.ASSOC_DESTINATION_EMPLOYEE);
				List<NodeRef> positions = assocsToCollection(subscription, SubscriptionsBean.ASSOC_DESTINATION_POSITION);
				List<NodeRef> units = assocsToCollection(subscription, SubscriptionsBean.ASSOC_DESTINATION_ORGANIZATION_UNIT);
				List<NodeRef> workgroups = assocsToCollection(subscription, SubscriptionsBean.ASSOC_DESTINATION_WORK_GROUP);
				Notification notification = new Notification();
				notification.setObjectRef(mainObject);
				notification.setAutor(author);
				notification.setDescription(description);
				notification.setTypeRefs(notificationTypes);
				notification.setRecipientEmployeeRefs(employees);
				notification.setRecipientPositionRefs(positions);
				notification.setRecipientOrganizationUnitRefs(units);
				notification.setRecipientWorkGroupRefs(workgroups);
				notificationsService.sendNotification(notification);
				nodeService.addAspect(nodeRef, SubscriptionsBean.ASPECT_SUBSCRIBED, null);
			}
		} catch (LuceneQueryParserException e) {
		} catch (Exception e1) {
			logger.error("Error while send notification for business journal's record " + nodeRef.toString(), e1);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
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
