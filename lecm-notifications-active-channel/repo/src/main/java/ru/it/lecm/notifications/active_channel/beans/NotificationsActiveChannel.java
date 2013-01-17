package ru.it.lecm.notifications.active_channel.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 17.01.13
 * Time: 15:19
 */
public class NotificationsActiveChannel implements NotificationChannelBeanBase {
	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_NAME = "Активный канал";
	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ASSOC_QNAME = "active_channel";

	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/channel/active/1.0";
	public final QName TYPE_NOTIFICATION_ACTIVE_CHANNEL = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "notification");

	private ServiceRegistry serviceRegistry;
	protected NodeService nodeService;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	protected NotificationsService notificationsService;
	private NodeRef rootRef;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void init() {
		final String rootName = NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_NAME;
		repositoryHelper.init();
		nodeService = serviceRegistry.getNodeService();
		transactionService = serviceRegistry.getTransactionService();

		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef rootRef = nodeService.getChildByName(notificationsService.getNotificationsRootRef(), ContentModel.ASSOC_CONTAINS, rootName);
						if (rootRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, NOTIFICATIONS_ACTIVE_CHANNEL_ASSOC_QNAME);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, rootName);
							ChildAssociationRef associationRef = nodeService.createNode(notificationsService.getNotificationsRootRef(), assocTypeQName, assocQName, nodeTypeQName, properties);
							rootRef = associationRef.getChildRef();
						}
						return rootRef;
					}
				});
			}
		};
		rootRef = AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public boolean sendNotification(NotificationUnit notification) {
		return createNotification(notification) != null;
	}

	private NodeRef createNotification(NotificationUnit notification) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, new Date());

		ChildAssociationRef associationRef = nodeService.createNode(rootRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, GUID.generate()),
				TYPE_NOTIFICATION_ACTIVE_CHANNEL, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}
}
