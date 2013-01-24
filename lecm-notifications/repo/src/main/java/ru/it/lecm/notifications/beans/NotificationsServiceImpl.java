package ru.it.lecm.notifications.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
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

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private OrgstructureBean orgstructureService;

	private NodeRef notificationsRootRef;
	private NodeRef notificationsGenaralizetionRootRef;
	private Map<NodeRef, NotificationChannelBeanBase> channels;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
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
		final String rootName = NOTIFICATIONS_ROOT_NAME;
		final String generalizationRootName = NOTIFICATIONS_GENERALIZATION_ROOT_NAME;
		repositoryHelper.init();
		nodeService = serviceRegistry.getNodeService();
		transactionService = serviceRegistry.getTransactionService();

		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef rootRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, rootName);
						if (rootRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, NOTIFICATIONS_ASSOC_QNAME);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, rootName);
							ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
							rootRef = associationRef.getChildRef();
						}
						return rootRef;
					}
				});
			}
		};
		notificationsRootRef = AuthenticationUtil.runAsSystem(raw);

		AuthenticationUtil.RunAsWork<NodeRef> generalizationRaw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef rootRef = nodeService.getChildByName(notificationsRootRef, ContentModel.ASSOC_CONTAINS, generalizationRootName);
						if (rootRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;
							QName assocQName = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, NOTIFICATIONS_GENERALIZATION_ASSOC_QNAME);

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, generalizationRootName);
							ChildAssociationRef associationRef = nodeService.createNode(notificationsRootRef, assocTypeQName, assocQName, nodeTypeQName, properties);

							rootRef = associationRef.getChildRef();
						}
						return rootRef;
					}
				});
			}
		};
		notificationsGenaralizetionRootRef = AuthenticationUtil.runAsSystem(generalizationRaw);

		channels = new HashMap<NodeRef, NotificationChannelBeanBase>();
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
					for (NodeRef employeeRef : generalizedNotification.getRecipientEmployeeRefs()) {
						if (orgstructureService.isEmployee(employeeRef)) {
							NotificationUnit newNotificationUnit = new NotificationUnit();
							newNotificationUnit.setAutor(generalizedNotification.getAutor());
							newNotificationUnit.setDescription(generalizedNotification.getDescription());
							newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
							newNotificationUnit.setTypeRef(typeRef);
							newNotificationUnit.setRecipientRef(employeeRef);
							result.add(newNotificationUnit);
						}
					}
				}

				if (generalizedNotification.getRecipientOrganizationUnitRefs() != null) {
					for (NodeRef organizationUnitRef : generalizedNotification.getRecipientOrganizationUnitRefs()) {
						if (orgstructureService.isUnit(organizationUnitRef)) {
							List<NodeRef> employeeRefs = orgstructureService.getOrganizationElementEmployees(organizationUnitRef);
							for (NodeRef employeeRef: employeeRefs) {
								NotificationUnit newNotificationUnit = new NotificationUnit();
								newNotificationUnit.setAutor(generalizedNotification.getAutor());
								newNotificationUnit.setDescription(generalizedNotification.getDescription());
								newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
								newNotificationUnit.setTypeRef(typeRef);
								newNotificationUnit.setRecipientRef(employeeRef);
								result.add(newNotificationUnit);
							}
						}
					}
				}

				if (generalizedNotification.getRecipientWorkGroupRefs() != null) {
					for (NodeRef workGroupRef : generalizedNotification.getRecipientWorkGroupRefs()) {
						if (orgstructureService.isWorkGroup(workGroupRef)) {
							List<NodeRef> employeeRefs = orgstructureService.getOrganizationElementEmployees(workGroupRef);
							for (NodeRef employeeRef: employeeRefs) {
								NotificationUnit newNotificationUnit = new NotificationUnit();
								newNotificationUnit.setAutor(generalizedNotification.getAutor());
								newNotificationUnit.setDescription(generalizedNotification.getDescription());
								newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
								newNotificationUnit.setTypeRef(typeRef);
								newNotificationUnit.setRecipientRef(employeeRef);
								result.add(newNotificationUnit);
							}
						}
					}
				}

				if (generalizedNotification.getRecipientPositionRefs() != null) {
					for (NodeRef positionRef : generalizedNotification.getRecipientPositionRefs()) {
						if (orgstructureService.isPosition(positionRef)) {
							//todo Добавить логику формирования атомарных уведомлений для должности
						}
					}
				}
			}
		}
		return result;
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
