package ru.it.lecm.notifications.beans;

import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 10.01.13
 * Time: 17:51
 */
public interface NotificationsService {
	public static final String NOTIFICATIONS_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/1.0";
	public static final String NOTIFICATIONS_TYPE_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/types/1.0";

	public static final String NOTIFICATIONS_ROOT_NAME = "Уведомления";
	public static final String NOTIFICATIONS_ASSOC_QNAME = "notifications";

	public final QName TYPE_GENERALIZED_NOTIFICATION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "generalized-notification");

	public final QName PROP_GENERAL_AUTOR = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-author");
	public final QName PROP_GENERAL_DESCRIPTION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-description");
	public final QName PROP_GENERAL_FORMING_DATE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-forming-date");

	public final QName ASSOC_NOTIFICATION_TYPE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "notification-type-assoc");
	public final QName ASSOC_RECIPIENT_EMPLOYEE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-employee-assoc");
	public final QName ASSOC_RECIPIENT_POSITION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-position-assoc");
	public final QName ASSOC_RECIPIENT_ORGANIZATION_UNIT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-organization-unit-assoc");
	public final QName ASSOC_RECIPIENT_WORK_GROUP = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-work-group-assoc");
	public final QName ASSOC_NOTIFICATION_OBJECT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "object-assoc");

	public final QName TYPE_NOTIFICATION_TYPE = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "notification-type");
	public final QName PROP_SPRING_BEAN_ID = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "spring-bean-id");

	public boolean sendNotification(Notification notification);

	public boolean sendNotification(NotificationUnit notification);
}
