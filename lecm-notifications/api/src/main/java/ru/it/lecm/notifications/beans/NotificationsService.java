package ru.it.lecm.notifications.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;
import java.util.Map;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: AIvkin
 * Date: 10.01.13
 * Time: 17:51
 *
 * Сервис уведомлений
 */
public interface NotificationsService {
	final String NOTIFICATIONS_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/1.0";
	final String NOTIFICATIONS_TYPE_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/types/1.0";
	final String NOTIFICATIONS_SETTINGS_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/settings/1.0";
	final String NOTIFICATIONS_TEMPLATE_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/template/1.0";

	final String NOTIFICATIONS_ROOT_NAME = "Сервис Уведомления";
	final String NOTIFICATIONS_ROOT_ID = "NOTIFICATIONS_ROOT_ID";
	final String NOTIFICATIONS_GENERALIZATION_ROOT_NAME = "Обобщённые уведомления";
	final String NOTIFICATIONS_GENERALIZATION_ROOT_ID = "NOTIFICATIONS_GENERALIZATION_ROOT_ID";

	QName TYPE_GENERALIZED_NOTIFICATION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "generalized-notification");

	QName PROP_GENERAL_AUTOR = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-author");
	QName PROP_GENERAL_DESCRIPTION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-description");
	QName PROP_GENERAL_FORMING_DATE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "general-forming-date");

	QName ASSOC_NOTIFICATION_TYPE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "notification-type-assoc");
	QName ASSOC_RECIPIENT_EMPLOYEE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-employee-assoc");
	QName ASSOC_RECIPIENT_POSITION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-position-assoc");
	QName ASSOC_RECIPIENT_ORGANIZATION_UNIT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-organization-unit-assoc");
	QName ASSOC_RECIPIENT_WORK_GROUP = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-work-group-assoc");
	QName ASSOC_RECIPIENT_BUSINESS_ROLE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-business-role-assoc");
	QName ASSOC_NOTIFICATION_OBJECT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "object-assoc");

	String NOTIFICATION_TEMPLATE_DICTIONARY_NAME = "Шаблоны уведомлений";
	QName TYPE_NOTIFICATION_TEMPLATE = QName.createQName(NOTIFICATIONS_TEMPLATE_NAMESPACE_URI, "template");
	QName PROP_NOTIFICATION_TEMPLATE_DESCRIPTION = QName.createQName(NOTIFICATIONS_TEMPLATE_NAMESPACE_URI, "description");
	QName PROP_NOTIFICATION_TEMPLATE = QName.createQName(NOTIFICATIONS_TEMPLATE_NAMESPACE_URI, "template");
	QName PROP_NOTIFICATION_TEMPLATE_SUBJECT = QName.createQName(NOTIFICATIONS_TEMPLATE_NAMESPACE_URI, "subject");
	QName ASSOC_NOTIFICATION_TEMPLATE_TEMPLATE_ASSOC = QName.createQName(NOTIFICATIONS_TEMPLATE_NAMESPACE_URI, "template-assoc");

	String NOTIFICATION_TYPE_DICTIONARY_NAME = "Типы доставки уведомлений";
	QName TYPE_NOTIFICATION_TYPE = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "notification-type");
	QName PROP_SPRING_BEAN_ID = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "spring-bean-id");
	QName PROP_DEFAULT_SELECT = QName.createQName(NOTIFICATIONS_TYPE_NAMESPACE_URI, "default-select");

	QName PROP_AUTOR = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "author");
	QName PROP_DESCRIPTION = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "description");
	QName PROP_FORMING_DATE = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "forming-date");
	QName ASSOC_RECIPIENT = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, "recipient-assoc");

	QName TYPE_NOTIFICATIONS_USER_SETTINGS = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI, "user");
	QName ASSOC_DEFAULT_NOTIFICATIONS_TYPES = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI, "default-types-assoc");

	@Deprecated
	QName TYPE_NOTIFICATIONS_GLOBAL_SETTINGS = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI, "global");
	@Deprecated
	QName PROP_ENABLE_PASSIVE_NOTIFICATIONS = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI, "enable-passive");
	@Deprecated
	QName PROP_N_DAYS = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI, "n-days");
	QName PROP_SHORT_N_DAYS = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI,"short-n-days");
	QName PROP_SHORT_LIMIT_DAYS = QName.createQName(NOTIFICATIONS_SETTINGS_NAMESPACE_URI,"short-limit-days");

	String NOTIFICATIONS_SETTINGS_NODE_NAME = "Settings";

	/**
	 * проверяет что объект является типом доставки уведомлений
	 */
	boolean isNotificationType(NodeRef ref);

	/**
	 * Отправка уведомлений
	 *
	 * @param notification Обобщённое уведомление
	 */
	void sendNotification(Notification notification);

    /**
     * Отправка уведомлений
     *
     * @param notification Обобщённое уведомление
     * @param dontCheckAccessToObject не проверять доступность объекта получателю
     */
    void sendNotification(Notification notification, boolean dontCheckAccessToObject);

	/**
	 * Отправка уведомлений в каналы, заданные строками (названиями bean-ов)
	 * @param channels Список названий каналов отправки уведомолений
	 * @param notification Объект с параметрами уведомления. Поле typeRefs заполнять не нужно
	 */
	void sendNotification(List<String> channels, Notification notification);

    /**
     * Отправка уведомлений в каналы, заданные строками (названиями bean-ов)
     * @param channels Список названий каналов отправки уведомолений
     * @param notification Объект с параметрами уведомления. Поле typeRefs заполнять не нужно
     * @param dontCheckAccessToObject не проверять доступность объекта получателю
     */
    void sendNotification(List<String> channels, Notification notification, boolean dontCheckAccessToObject);

    /**
	 * Получение корневой директории для уведомлений
	 *
	 * @return Ссылка на корневую директорию уведомлений
	 */
	NodeRef getNotificationsRootRef() throws WriteTransactionNeededException;

	/**
	 * Получение настроект пользователя
	 * @param userName Логин пользователя
	 * @param createNewIfNotExist Создавать ли настройки если их нет
	 * @return Ссылка на объект настроек
	 */
	NodeRef getUserSettingsNode(String userName);

	/**
	 * Создание настроек пользователя
	 * @param userName
	 * @return
	 */
	NodeRef createUserSettingsNode(String userName);

	/**
	 * Получение настроект текущего пользователя
	 * @param createNewIfNotExist Создавать ли настройки если их нет
	 * @return ссылка на объект пользовательских настроек
	 */
	NodeRef getCurrentUserSettingsNode();

	NodeRef createCurrentUserSettingsNode();

	/**
	 * Получение типов доставки уведомлений по-умолчанию из справочника
	 * @return Список ссылок на типы доставки уведомлений
	 */
	List<NodeRef> getSystemDefaultNotificationTypes();

	/**
	 * Получение типов доставки уведомлений по-умолчанию для пользователя.
	 * Берутся личные настройки. если их нет, или в них не выбран ни один тип доставки уведомлений,
	 * то берутся системные настройки из справочника
	 *
	 * @param employee Ссылка на сотрудника
	 * @return Список ссылок на типы доставки уведомлений
	 */
	List<NodeRef> getEmployeeDefaultNotificationTypes(NodeRef employee);

	/**
	 * Получение типов доставки уведомлений по-умолчанию для текущего пользователя.
	 * Берутся личные настройки. если их нет, или в них не выбран ни один тип доставки уведомлений,
	 * то берутся системные настройки из справочника
	 *
	 * @return Список ссылок на типы доставки уведомлений
	 */
	List<NodeRef> getCurrentUserDefaultNotificationTypes();

	/**
	 * Получение глобальных настроек
	 * @return ссылка на объект настроек
	 */
	NodeRef getGlobalSettingsNode();

	/**
	 * Отправка уведомления сотрудникам
	 * @param author Автор уведомления
	 * @param object Объект уведомления
	 * @param textFormatString Форматная строка для текста уведомления. Стпроится по основному объекту
	 * @param recipientEmployees Список сотрудников-получаетлей уведомления
	 * @param channels каналы уведомления
	 * @param initiatorRef Ссылка на инициатора. Если он попадает в список получателей, то ему сообщение не будет отправлено
	 */
	
	NodeRef createGlobalSettingsNode();
	
	void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, List<String> channels, NodeRef initiatorRef);

    /**
     * Отправка уведомления сотрудникам
     * @param author Автор уведомления
     * @param object Объект уведомления
     * @param textFormatString Форматная строка для текста уведомления. Стпроится по основному объекту
     * @param recipientEmployees Список сотрудников-получаетлей уведомления
     * @param channels каналы уведомления
     * @param initiatorRef Ссылка на инициатора. Если он попадает в список получателей, то ему сообщение не будет отправлено
     * @param dontCheckAccessToObject не проверять доступность объекта получателю
     */
    void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, List<String> channels, NodeRef initiatorRef, boolean dontCheckAccessToObject);

	/**
	 * Отправка уведомления сотрудникам по каналам уведомлений из личных настроек сотрудников
	 * @param author Автор уведомления
	 * @param object Объект уведомления
	 * @param textFormatString Форматная строка для текста уведомления. Стпроится по основному объекту
	 * @param recipientEmployees Список сотрудников-получаетлей уведомления
	 * @param initiatorRef Ссылка на инициатора. Если он попадает в список получателей, то ему сообщение не будет отправлено
	 */
	void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, NodeRef initiatorRef);

    /**
     * Отправка уведомления сотрудникам по каналам уведомлений из личных настроек сотрудников
     * @param author Автор уведомления
     * @param object Объект уведомления
     * @param textFormatString Форматная строка для текста уведомления. Стпроится по основному объекту
     * @param recipientEmployees Список сотрудников-получаетлей уведомления
     * @param initiatorRef Ссылка на инициатора. Если он попадает в список получателей, то ему сообщение не будет отправлено
     * @param dontCheckAccessToObject не проверять доступность объекта получателю
     */
    void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, NodeRef initiatorRef, boolean dontCheckAccessToObject);

    /**
     * @return Включены или выключены пассивные уведомления
     */
	@Deprecated
    boolean isEnablePassiveNotifications();

    /**
     * @return Количество рабочих дней за которое должно высылаться уведомление
     */
	@Deprecated
    int getSettingsNDays();

	int getSettingsShortNDays();

	int getSettingsShortLimitDays();

    /**
     * Отправка уведомления сотрудникам
     * @param author Автор уведомления
     * @param object Объект уведомления
     * @param textFormatString Форматная строка для текста уведомления. Стпроится по основному объекту
     * @param recipientEmployees Список сотрудников-получаетлей уведомления
     * @param channels каналы уведомления
     * @param initiatorRef Ссылка на инициатора. Если он попадает в список получателей, то ему сообщение не будет отправлено
     * @param dontCheckAccessToObject не проверять доступность объекта получателю
	 * @param delegateBusinessRoleRefs список бизнес ролей, по которым проверять наличие делегатов у получателей
     */
    void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, List<String> channels, NodeRef initiatorRef, boolean dontCheckAccessToObject, List<NodeRef> delegateBusinessRoleRefs);

//	void sendNotification(String author, Map<String, NodeRef> objects, String templateCode, List<NodeRef> recipientEmployees, NodeRef initiatorRef, boolean dontCheckAccessToObject) throws TemplateRunException, TemplateParseException;

	void sendNotificationByTemplate(NodeRef nodeRef, List<NodeRef> recipients, String templateCode);

	void sendNotificationByTemplate(String author, NodeRef nodeRef, List<NodeRef> recipients, String templateCode);

	void sendNotificationByTemplate(NodeRef nodeRef, List<NodeRef> recipients, String templateCode, Map<String, Object> objects);

	void sendNotificationByTemplate(String author, NodeRef nodeRef, List<NodeRef> recipients, String templateCode, Map<String, Object> objects);

	void sendNotification(String author, NodeRef initiatorRef, List<NodeRef> recipientRefs, String templateCode, Map<String, Object> config, boolean dontCheckAccessToObject);

}
