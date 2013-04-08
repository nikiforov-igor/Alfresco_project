package ru.it.lecm.notifications.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 10.01.13
 * Time: 16:53
 */
public class NotificationsServiceImpl extends BaseBean implements NotificationsService {
	final private static Logger logger = LoggerFactory.getLogger(NotificationsServiceImpl.class);

	private OrgstructureBean orgstructureService;

	private NodeRef notificationsRootRef;
	private NodeRef notificationsGenaralizetionRootRef;
	private Map<NodeRef, NotificationChannelBeanBase> channels;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	@Override
	public NodeRef getNotificationsRootRef() {
		return notificationsRootRef;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 * Записывает в свойства сервиса nodeRef директории с уведомлениями
	 */
	public void init() {
		notificationsRootRef = getFolder(NOTIFICATIONS_ROOT_ID);
		notificationsGenaralizetionRootRef = getFolder(NOTIFICATIONS_GENERALIZATION_ROOT_ID);

		channels = new HashMap<NodeRef, NotificationChannelBeanBase>();
	}

	@Override
	public boolean isNotificationType(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_NOTIFICATION_TYPE);
		return isProperType(ref, types);
	}

	@Override
	public boolean sendNotification(Notification notification) {
		if (checkNotification(notification)) {
			NodeRef generalizedNotification = createGeneralizedNotification(notification);
			if (generalizedNotification != null) {
				Set<NotificationUnit> notificationUnits = createAtomicNotifications(notification);
				if (notificationUnits != null && notificationUnits.size() > 0) {
					boolean success = true;
					for (NotificationUnit notf: notificationUnits) {
						boolean temp = sendNotification(notf);
						if (success && !temp) {
							success = false;
						}
					}
					return success;
				} else {
					logger.warn("Атомарные уведомления не были сформированы");
					return false;
				}
			} else {
				logger.warn("Обобщённое уведомление не создано");
				return false;
			}
		} else {
			logger.warn("Уведомление не прошло проверки");
			return false;
		}
	}

	@Override
	public boolean sendNotification(NotificationUnit notification) {
		if (notification != null) {
			NotificationChannelBeanBase channelBean;
			if (channels.containsKey(notification.getTypeRef())) {
				channelBean = channels.get(notification.getTypeRef());
			} else {
				WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
				String beanId = (String) nodeService.getProperty(notification.getTypeRef(), PROP_SPRING_BEAN_ID);
				try {
					channelBean = (NotificationChannelBeanBase) ctx.getBean(beanId);
					channels.put(notification.getTypeRef(), channelBean);
				} catch (NoSuchBeanDefinitionException ex) {
					logger.error("Не найден канал для отправки уведомлений", ex);
					channels.put(notification.getTypeRef(), null);
					return false;
				} catch (ClassCastException ex) {
					logger.error("Канал уведомлений не реализует базовый интерфейс", ex);
					channels.put(notification.getTypeRef(), null);
					return false;
				}
			}

			return channelBean != null && channelBean.sendNotification(notification);
		} else {
			return false;
		}
	}

	/**
	 * Создание обобщённого уведомления
	 *
	 * @param notification Обобщённое уведомление
	 * @return Ссылка на обобщённое уведомление
	 */
	private NodeRef createGeneralizedNotification(Notification notification) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
		properties.put(PROP_GENERAL_AUTOR, notification.getAutor());
		properties.put(PROP_GENERAL_DESCRIPTION, notification.getDescription());
		properties.put(PROP_GENERAL_FORMING_DATE, notification.getFormingDate());

		final NodeRef saveDirectoryRef = getFolder(this.notificationsGenaralizetionRootRef, getDateFolderPath(notification.getFormingDate()));

		ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_NAMESPACE_URI, GUID.generate()), TYPE_GENERALIZED_NOTIFICATION, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getObjectRef(), ASSOC_NOTIFICATION_OBJECT);
		if (notification.getTypeRefs() != null) {
			for (NodeRef ref : notification.getTypeRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_NOTIFICATION_TYPE);
			}
		}
		if (notification.getRecipientEmployeeRefs() != null) {
			for (NodeRef ref : notification.getRecipientEmployeeRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_EMPLOYEE);
			}
		}
		if (notification.getRecipientOrganizationUnitRefs() != null) {
			for (NodeRef ref : notification.getRecipientOrganizationUnitRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_ORGANIZATION_UNIT);
			}
		}
		if (notification.getRecipientWorkGroupRefs() != null) {
			for (NodeRef ref : notification.getRecipientWorkGroupRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_WORK_GROUP);
			}
		}
		if (notification.getRecipientPositionRefs() != null) {
			for (NodeRef ref : notification.getRecipientPositionRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_POSITION);
			}
		}
		return result;
	}

	/**
	 * Разделение обобщённого уведомления на атомарные
	 *
	 * @param generalizedNotification Обобщённое уведомление
	 * @return Множество атомарных уведомлений
	 */
	private Set<NotificationUnit> createAtomicNotifications(Notification generalizedNotification) {
		Set<NotificationUnit> result = new HashSet<NotificationUnit>();
		if (generalizedNotification != null) {
			for (NodeRef typeRef : generalizedNotification.getTypeRefs()) {
				if (generalizedNotification.getRecipientEmployeeRefs() != null) {
					List<NodeRef> employeeRefs = generalizedNotification.getRecipientEmployeeRefs();
					addNotificationUnits(generalizedNotification, employeeRefs, typeRef, result);
				}

				if (generalizedNotification.getRecipientOrganizationUnitRefs() != null) {
					for (NodeRef organizationUnitRef : generalizedNotification.getRecipientOrganizationUnitRefs()) {
						if (orgstructureService.isUnit(organizationUnitRef)) {
							List<NodeRef> employeeRefs = orgstructureService.getOrganizationElementEmployees(organizationUnitRef);
							addNotificationUnits(generalizedNotification, employeeRefs, typeRef, result);
						}
					}
				}

				if (generalizedNotification.getRecipientWorkGroupRefs() != null) {
					for (NodeRef workGroupRef : generalizedNotification.getRecipientWorkGroupRefs()) {
						if (orgstructureService.isWorkGroup(workGroupRef)) {
							List<NodeRef> employeeRefs = orgstructureService.getOrganizationElementEmployees(workGroupRef);
							addNotificationUnits(generalizedNotification, employeeRefs, typeRef, result);
						}
					}
				}

				if (generalizedNotification.getRecipientPositionRefs() != null) {
					for (NodeRef positionRef : generalizedNotification.getRecipientPositionRefs()) {
						if (orgstructureService.isPosition(positionRef)) {
							List<NodeRef> employeeRefs = orgstructureService.getEmployeesByPosition(positionRef);
							addNotificationUnits(generalizedNotification, employeeRefs, typeRef, result);
						}
					}
				}
			}
		}
		return result;
	}

	private void addNotificationUnits(Notification generalizedNotification, List<NodeRef> employeeRefs,
	                                  NodeRef typeRef, Set<NotificationUnit> resultSet) {

		for (NodeRef employeeRef: employeeRefs) {
			if (orgstructureService.isEmployee(employeeRef) && !employeeRef.equals(generalizedNotification.getInitiatorRef())) {
				NotificationUnit newNotificationUnit = new NotificationUnit();
				newNotificationUnit.setAutor(generalizedNotification.getAutor());
				newNotificationUnit.setDescription(generalizedNotification.getDescription());
				newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
				newNotificationUnit.setTypeRef(typeRef);
				newNotificationUnit.setRecipientRef(employeeRef);
				resultSet.add(newNotificationUnit);
			}
		}
	}

	/**
	 * Проверка обобщённого уведомления
	 * @param notification Обобщённое уведомление
	 * @return false - если уведомление неверно заполнено, иначе true
	 */
	private boolean checkNotification(Notification notification) {
		if (notification == null) {
			logger.warn("Уведомление null");
			return false;
		}
		if (notification.getAutor() == null) {
			logger.warn("Автор уведомление null");
			return false;
		}
		if (!nodeService.exists(notification.getObjectRef())) {
			logger.warn("Ссылка на объект уведомление не существует");
			return false;
		}
		if ((notification.getRecipientEmployeeRefs() == null || notification.getRecipientEmployeeRefs().size() == 0) &&
				(notification.getRecipientOrganizationUnitRefs() == null || notification.getRecipientOrganizationUnitRefs().size() == 0) &&
				(notification.getRecipientPositionRefs() == null || notification.getRecipientPositionRefs().size() == 0) &&
				(notification.getRecipientWorkGroupRefs() == null || notification.getRecipientWorkGroupRefs().size() == 0)) {
			logger.warn("Должен быть хотя бы один получатель уведомления");
			return false;
		}
		return true;
	}
}
