package ru.it.lecm.notifications.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: AIvkin
 * Date: 10.01.13
 * Time: 17:51
 *
 * Сервис уведомлений
 */
public interface NotificationsService {
	public static final String NOTIFICATIONS_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/1.0";
	public static final String NOTIFICATIONS_TYPE_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/types/1.0";
	public static final String NOTIFICATIONS_SETTINGS_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/settings/1.0";

	public static final String NOTIFICATIONS_ROOT_NAME = "Сервис Уведомления";
	public static final String NOTIFICATIONS_ROOT_ID = "NOTIFICATIONS_ROOT_ID";
	public static final String NOTIFICATIONS_GENERALIZATION_ROOT_NAME = "Обобщённые уведомления";
	public static final String NOTIFICATIONS_GENERALIZATION_ROOT_ID = "NOTIFICATIONS_GENERALIZATION_ROOT_ID";

	public final QName TYPE_GENERALIZED_NOTIFICATION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "generalized-notification");

	public final QName PROP_GENERAL_AUTOR = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-author");
	public final QName PROP_GENERAL_DESCRIPTION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-description");
	public final QName PROP_GENERAL_FORMING_DATE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-forming-date");

	public final QName ASSOC_NOTIFICATION_TYPE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "notification-type-assoc");
	public final QName ASSOC_RECIPIENT_EMPLOYEE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-employee-assoc");
	public final QName ASSOC_RECIPIENT_POSITION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-position-assoc");
	public final QName ASSOC_RECIPIENT_ORGANIZATION_UNIT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-organization-unit-assoc");
	public final QName ASSOC_RECIPIENT_WORK_GROUP = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-work-group-assoc");
	public final QName ASSOC_RECIPIENT_BUSINESS_ROLE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-business-role-assoc");
	public final QName ASSOC_NOTIFICATION_OBJECT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "object-assoc");

	public final String NOTIFICATION_TYPE_DICTIONARY_NAME = "Типы доставки уведомлений";
	public final QName TYPE_NOTIFICATION_TYPE = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "notification-type");
	public final QName PROP_SPRING_BEAN_ID = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "spring-bean-id");
	public final QName PROP_DEFAULT_SELECT = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "default-select");

	public final QName PROP_AUTOR = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "author");
	public final QName PROP_DESCRIPTION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "description");
	public final QName PROP_FORMING_DATE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "forming-date");
	public final QName ASSOC_RECIPIENT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-assoc");

	public static final QName TYPE_NOTIFICATIONS_USER_SETTINGS = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI, "user");
	public static final QName ASSOC_DEFAULT_NOTIFICATIONS_TYPES = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI, "default-types-assoc");

	public static final String NOTIFICATIONS_SETTINGS_NODE_NAME = "Settings";

	/**
	 * проверяет что объект является типом доставки уведомлений
	 */
	public boolean isNotificationType(NodeRef ref);

	/**
	 * Отправка уведомлений
	 *
	 * @param notification Обобщённое уведомление
	 * @return true - если отправка успешна, false - если при отправки возникли ошибки
	 */
	public boolean sendNotification(Notification notification);

	/**
	 * Отправка атомарного уведомления
	 *
	 * @param notification Атомарное уведомление
	 * @return true - если отправка успешна, false - если при отправки возникли ошибки
	 */
	public boolean sendNotification(NotificationUnit notification);

	/**
	 * Отправка уведомлений в каналы, заданные строками (названиями bean-ов)
	 * @param channels Список названий каналов отправки уведомолений
	 * @param notification Объект с параметрами уведомления. Поле typeRefs заполнять не нужно
	 * @return true - если отправка успешна, false - если при отправки возникли ошибки
	 */
	public boolean sendNotification(List<String> channels, Notification notification);

	/**
	 * Получение корневой директории для уведомлений
	 *
	 * @return Ссылка на корневую директорию уведомлений
	 */
	public NodeRef getNotificationsRootRef();

	/**
	 * Получение настроект пользователя
	 * @param userName Логин пользователя
	 * @param createNewIfNotExist Создавать ли настройки если их нет
	 * @return Ссылка на объект настроек
	 */
	public NodeRef geUserSettingsNode(String userName, boolean createNewIfNotExist);

	/**
	 * Получение настроект текущего пользователя
	 * @param createNewIfNotExist Создавать ли настройки если их нет
	 * @return ссылка на объект пользовательских настроек
	 */
	public NodeRef getCurrentUserSettingsNode(boolean createNewIfNotExist);

	/**
	 * Получение типов доставки уведомлений по-умолчанию из справочника
	 * @return Список ссылок на типы доставки уведомлений
	 */
	public List<NodeRef> getSystemDefaultNotificationTypes();

	/**
	 * Получение типов доставки уведомлений по-умолчанию для пользователя.
	 * Берутся личные настройки. если их нет, или в них не выбран ни один тип доставки уведомлений,
	 * то берутся системные настройки из справочника
	 *
	 * @param employee Ссылка на сотрудника
	 * @return Список ссылок на типы доставки уведомлений
	 */
	public List<NodeRef> getEmployeeDefaultNotificationTypes(NodeRef employee);

	/**
	 * Получение типов доставки уведомлений по-умолчанию для текущего пользователя.
	 * Берутся личные настройки. если их нет, или в них не выбран ни один тип доставки уведомлений,
	 * то берутся системные настройки из справочника
	 *
	 * @return Список ссылок на типы доставки уведомлений
	 */
	public List<NodeRef> getCurrentUserDefaultNotificationTypes();
}
