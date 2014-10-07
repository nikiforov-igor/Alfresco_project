package ru.it.lecm.subscriptions.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: mShafeev
 * Date: 31.01.13
 * Time: 11:25
 */
public interface SubscriptionsService {
	public static final String SUBSCRIPTIONS_ROOT_NAME = "Сервис Подписки";
	public static final String SUBSCRIPTIONS_ROOT_ID = "SUBSCRIPTIONS_ROOT_ID";
	public static final String DICTIONARY_ROOT_NAME = "Dictionary";
	public static final String DICTIONARY_ROOT_NAME_EVENT_CATEGORY = "Категория события";
	public static final String DICTIONARY_ROOT_NAME_TYPE_OBJECT = "Тип объекта";
	public static final String DICTIONARY_ROOT_NAME_TYPE_TEMPLATE_MESSAGE = "Шаблон сообщения";
	public static final String SUBSCRIPTIONS_NAMESPACE_URI = "http://www.it.ru/lecm/subscriptions/1.0";

	public static final QName TYPE_SUBSCRIPTION_TO_OBJECT = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "subscription-to-object");
	public static final QName TYPE_SUBSCRIPTION_TO_TYPE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "subscription-to-type");

	public static final QName ASSOC_NOTIFICATION_TYPE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "notification-type-assoc");
	public static final QName ASSOC_DESTINATION_EMPLOYEE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-employee-assoc");
	public static final QName ASSOC_SUBSCRIPTION_OBJECT = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "subscription-object-assoc");
	public static final QName ASSOC_DESTINATION_POSITION = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-position-assoc");
	public static final QName ASSOC_DESTINATION_ORGANIZATION_UNIT = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-organization-unit-assoc");
	public static final QName ASSOC_DESTINATION_WORK_GROUP = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-work-group-assoc");
	public static final QName ASSOC_DESTINATION_BUSINESS_ROLE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-business-role-assoc");
	public static final QName ASSOC_OBJECT_TYPE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "object-type-assoc");
	public static final QName ASSOC_EVENT_CATEGORY = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "event-category-assoc");
	public static final QName PROP_DESCRIPTION = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "description");

	public static final String BUSJOURNAL_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";
	public static final String TYPE_SUBSCRIPTION = "subscription";

	public static final QName ASPECT_SUBSCRIBED = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "subscribedAspect");
	public static final QName ASPECT_LAST_RECORD_ID = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "lastRecordIdAspect");
    public static final QName PROP_LAST_RECORD_ID = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "lastRecordId");
	public static final QName PROP_LAST_RECORD_TIMESTAMP = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "lastRecordTimeStamp");

	QName ASSOC_BUSJOURNAL_LINK_EMPLOYEE = QName.createQName(BUSJOURNAL_NAMESPACE_URI, "lecm-busjournal");
	QName ASSOC_BUSJOURNAL_EVENT_CATEGORY = QName.createQName(BUSJOURNAL_NAMESPACE_URI, "messageTemplate-evCategory-assoc");
	QName ASSOC_BUSJOURNAL_OBJECT_TYPE = QName.createQName(BUSJOURNAL_NAMESPACE_URI, "messageTemplate-objType-assoc");

	public static final String BUSINESS_ROLE_SUBSCRIPTIONS_ENGINEER = "BR_SUBSCRIPTIONS_ENGINEER";
	/**
	 * проверяет что объект является подпиской на объект
	 */
	public boolean isSubscriptionToObject(NodeRef ref);

	/**
	 * проверяет что объект является подпиской на тип
	 */
	public boolean isSubscriptionToType(NodeRef ref);

	/**
	 * Получение списка подписок сотрудника
	 *
	 * @param employeeRef Ссылка на сотрудника
	 * @return Список ссылок на подписки
	 */
	public List<NodeRef> getEmployeeSubscriptionsToObject(NodeRef employeeRef);

	/**
	 * Получение списка подписок на объект
	 *
	 * @param objectRef Ссылка на объек
	 * @return Список ссылок на подписки
	 */
	public List<NodeRef> getSubscriptionsToObject(NodeRef objectRef);

	/**
	 * Получения подписки сотрудника на объект
	 *
	 * @param employeeRef Ссылка на сотрудника
	 * @param objectNodeRef Ссылка на объект
	 * @return Ссылка на подписку
	 */
	public NodeRef getEmployeeSubscriptionToObject(NodeRef employeeRef, NodeRef objectNodeRef);

	/**
	 * Создание подписки на объект
	 * @param objectRef Ссылка на объект
	 * @param description описание
	 * @param notificationType тип доставки
	 * @param employee сотрудники
	 * @return Подписка
	 */
	public NodeRef createSubscriptionToObject(String name, NodeRef objectRef, String description,
	                                          List<NodeRef> notificationType,
	                                          List<NodeRef> employee);

	/**
	 * Создание подписки на тип
	 * @param description описание
	 * @param objectType тип объекта
	 * @param eventCategory категория события
	 * @param notificationType тип доставки
	 * @param employee сотрудники
	 * @param workGroup рабочие группы
	 * @param organizationUnit подразделения
	 * @param position должностная позиция
	 * @return Подписка
	 */
	public NodeRef createSubscriptionToType(String name, String description, NodeRef objectType,
	                                        NodeRef eventCategory, List<NodeRef> notificationType,
	                                        List<NodeRef> employee, List<NodeRef> workGroup,
	                                        List<NodeRef> organizationUnit, List<NodeRef> position);

	/**
	 * Удаление подписки
	 *
	 * @param nodeRef Ссылка на подписку
	 */
	public void unsubscribe(NodeRef nodeRef);

	/**
	 * Получение директории подписки.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	NodeRef getSubscriptionRootRef();
}
