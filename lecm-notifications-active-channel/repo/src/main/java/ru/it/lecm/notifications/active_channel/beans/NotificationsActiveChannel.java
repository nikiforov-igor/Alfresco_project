package ru.it.lecm.notifications.active_channel.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 17.01.13
 * Time: 15:19
 *
 * Сервис активного канала уведомлений
 */
public class NotificationsActiveChannel extends BaseBean implements NotificationChannelBeanBase {
	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_NAME = "Активный канал";
	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ASSOC_QNAME = "active_channel";

	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/channel/active/1.0";
	public static final QName TYPE_NOTIFICATION_ACTIVE_CHANNEL = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "notification");
	public final QName PROP_READ_DATE = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "read-date");

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	protected NotificationsService notificationsService;
	private OrgstructureBean orgstructureService;
	private NodeRef rootRef;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public NodeRef getRootRef() {
		return rootRef;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 */
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
		this.rootRef = AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public boolean sendNotification(NotificationUnit notification) {
		return createNotification(notification) != null;
	}

	/**
	 * Создание уведомления активного канала
	 *
	 * @param notification Атомарное уведомление
	 * @return Ссылка на уведомление активного канала
	 */
	private NodeRef createNotification(NotificationUnit notification) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, new Date());

		ChildAssociationRef associationRef = nodeService.createNode(this.rootRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, GUID.generate()),
				TYPE_NOTIFICATION_ACTIVE_CHANNEL, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}

	public boolean isActiveChannelNotification(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_NOTIFICATION_ACTIVE_CHANNEL);
		return isProperType(ref, types);
	}

	public boolean isNewNotification(NodeRef ref) {
		return ref != null && isActiveChannelNotification(ref) && !isArchive(ref) &&
				nodeService.getProperty(ref, PROP_READ_DATE) == null;
	}

	public int getNewNotificationsCount() {
		int result = 0;
		NodeRef currentEmloyeeNodeRef = orgstructureService.getCurrentEmployee();
		if (currentEmloyeeNodeRef != null) {
			List<AssociationRef> lRefs = nodeService.getSourceAssocs(currentEmloyeeNodeRef, NotificationsService.ASSOC_RECIPIENT);
			for (AssociationRef ref: lRefs) {
				if (isNewNotification(ref.getSourceRef())) {
					result++;
				}
			}
		}
		return result;
	}

	public List<NodeRef> getNotifications() {
		List<NodeRef> result = new ArrayList<NodeRef>();
		NodeRef currentEmloyeeNodeRef = orgstructureService.getCurrentEmployee();
		if (currentEmloyeeNodeRef != null) {
			List<AssociationRef> lRefs = nodeService.getSourceAssocs(currentEmloyeeNodeRef, NotificationsService.ASSOC_RECIPIENT);
			for (AssociationRef ref: lRefs) {
				if (isActiveChannelNotification(ref.getSourceRef()) && !isArchive(ref.getSourceRef())) {
					result.add(ref.getSourceRef());
					nodeService.setProperty(ref.getSourceRef(), PROP_READ_DATE, new Date());
				}
			}
		}
		return result;
	}
}
