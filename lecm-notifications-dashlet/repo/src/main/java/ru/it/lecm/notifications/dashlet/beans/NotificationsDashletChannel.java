package ru.it.lecm.notifications.dashlet.beans;

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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 17.01.13
 * Time: 15:19
 */
public class NotificationsDashletChannel implements NotificationChannelBeanBase {
	public static final String NOTIFICATIONS_DASHLET_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/dashlet/1.0";
	public final QName TYPE_NOTIFICATION_DASHLET = QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, "notification");

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
		return createNotification(notification) != null;
	}

	private NodeRef createNotification(NotificationUnit notification) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, new Date());

		ChildAssociationRef associationRef = nodeService.createNode(notificationsService.getNotificationsRootRef(),	ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, GUID.generate()), TYPE_NOTIFICATION_DASHLET, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}
}
