package ru.it.lecm.notifications.email.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
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
 */
public class NotificationsEmailChannel implements NotificationChannelBeanBase {
	public static final String NOTIFICATIONS_EMAIL_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/email/1.0";
	public final QName TYPE_NOTIFICATION_EMAIL = QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, "notification");
	public final QName PROP_EMAIL = QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, "email");

	private ServiceRegistry serviceRegistry;
	protected NodeService nodeService;
	protected NotificationsService notificationsService;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
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

	private NodeRef createNotification(NotificationUnit notification, String email) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, new Date());
		properties.put(PROP_EMAIL, email);

		ChildAssociationRef associationRef = nodeService.createNode(notificationsService.getNotificationsRootRef(),	ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, GUID.generate()), TYPE_NOTIFICATION_EMAIL, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}

	private boolean sendEmail(NotificationUnit notification, String email) {
		//todo сделадь отправку электронной почты
		return false;
	}
}
