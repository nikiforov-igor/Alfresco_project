package ru.it.lecm.notifications.email.beans;

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
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 17.01.13
 * Time: 11:19
 *
 * Сервис канала уведомлений для электронной почты
 */
public class NotificationsEmailChannel implements NotificationChannelBeanBase {
	public static final String NOTIFICATIONS_EMAIL_ROOT_NAME = "Email";
	public static final String NOTIFICATIONS_EMAIL_ASSOC_QNAME = "email";

	public static final String NOTIFICATIONS_EMAIL_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/email/1.0";
	public final QName TYPE_NOTIFICATION_EMAIL = QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, "notification");
	public final QName PROP_EMAIL = QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, "email");

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

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 */
	public void init() {
		final String rootName = NOTIFICATIONS_EMAIL_ROOT_NAME;
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
							QName assocQName = QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, NOTIFICATIONS_EMAIL_ASSOC_QNAME);
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
		String email = (String) nodeService.getProperty(notification.getRecipientRef(), OrgstructureBean.PROP_EMPLOYEE_EMAIL);
		if (email != null) {
			createNotification(notification, email);
			return sendEmail(notification, email);
		} else {
			return false;
		}
	}

	/**
	 * Создание уведомления для email
	 *
	 * @param notification Атомарное уведомление
	 * @param email Адрес электронной почты
	 * @return Ссылка на уведомление для email
	 */
	private NodeRef createNotification(NotificationUnit notification, String email) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(4);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, new Date());
		properties.put(PROP_EMAIL, email);

		ChildAssociationRef associationRef = nodeService.createNode(this.rootRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, GUID.generate()),
				TYPE_NOTIFICATION_EMAIL, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}

	/**
	 * Отправка письма с уведомлением на почту сотрудника
	 *
	 * @param notification Атомарное уведомление
	 * @param email Адрес электронной почты
	 * @return false - если при отправке письма произошла ошибка, иначе true
	 */
	private boolean sendEmail(NotificationUnit notification, String email) {
		//todo сделадь отправку электронной почты
		return false;
	}
}
