package ru.it.lecm.notifications.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

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
	private DictionaryBean dictionaryService;

	private NodeRef notificationsRootRef;
	private NodeRef notificationsGenaralizetionRootRef;
	private Map<NodeRef, NotificationChannelBeanBase> channels;
	private Map<String, NodeRef> channelsNodeRefs;
	private LecmPermissionService lecmPermissionService;

	private final Object lock = new Object();

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

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public Map<NodeRef, NotificationChannelBeanBase> getChannels() {
		if (this.channels == null) {
			loadChannels();
		}
		return this.channels;
	}

	public Map<String, NodeRef> getChannelsNodeRefs() {
		if (this.channelsNodeRefs == null) {
			loadChannels();
		}
		return this.channelsNodeRefs;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 * Записывает в свойства сервиса nodeRef директории с уведомлениями
	 */
	public void init() {
		notificationsRootRef = getFolder(NOTIFICATIONS_ROOT_ID);
		notificationsGenaralizetionRootRef = getFolder(NOTIFICATIONS_GENERALIZATION_ROOT_ID);
	}

	@Override
	public boolean isNotificationType(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_NOTIFICATION_TYPE);
		return isProperType(ref, types);
	}

	public void loadChannels() {
		channels = new HashMap<NodeRef, NotificationChannelBeanBase>();
		channelsNodeRefs = new HashMap<String, NodeRef>();

		NodeRef channelsDictionary = dictionaryService.getDictionaryByName(NOTIFICATION_TYPE_DICTIONARY_NAME);
		if (channelsDictionary != null) {
			List<NodeRef> channelsDictionaryValue = dictionaryService.getChildren(channelsDictionary);
			if (channelsDictionaryValue != null) {
				for (NodeRef typeRef: channelsDictionaryValue) {
					String beanId = (String) nodeService.getProperty(typeRef, PROP_SPRING_BEAN_ID);
					if (beanId != null) {
						channelsNodeRefs.put(beanId, typeRef);
						WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
						try {
							NotificationChannelBeanBase channelBean = (NotificationChannelBeanBase) ctx.getBean(beanId);
							channels.put(typeRef, channelBean);
						} catch (NoSuchBeanDefinitionException ex) {
							logger.error("Не найден канал для отправки уведомлений", ex);
							channels.put(typeRef, null);
						} catch (ClassCastException ex) {
							logger.error("Канал уведомлений не реализует базовый интерфейс", ex);
							channels.put(typeRef, null);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean sendNotification(List<String> channels, Notification notification) {
		List<NodeRef> typeRefs = new ArrayList<NodeRef>();
		if (channels != null) {
			for (String channel: channels) {
				if (getChannelsNodeRefs().containsKey(channel)) {
					typeRefs.add(getChannelsNodeRefs().get(channel));
				}
			}
		}
		notification.setTypeRefs(typeRefs);
		return sendNotification(notification);
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
		if (notification != null && notification.getRecipientRef() != null && notification.getObjectRef() != null) {
			String employeeLogin = this.orgstructureService.getEmployeeLogin(notification.getRecipientRef());
			if (employeeLogin != null && this.lecmPermissionService.hasReadAccess(notification.getObjectRef(), employeeLogin)) {
				NotificationChannelBeanBase channelBean = null;
				if (getChannels().containsKey(notification.getTypeRef())) {
					channelBean = getChannels().get(notification.getTypeRef());
				}
				return channelBean != null && channelBean.sendNotification(notification);
			} else {
				return false;
			}
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

        if (notification.getRecipientBusinessRoleRefs() != null) {
            for (NodeRef ref : notification.getRecipientBusinessRoleRefs()) {
                nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_BUSINESS_ROLE);
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
            Set<NodeRef> employeeRefs = new HashSet<NodeRef>();
            if (generalizedNotification.getRecipientEmployeeRefs() != null) {
                employeeRefs.addAll(generalizedNotification.getRecipientEmployeeRefs());
            }

            if (generalizedNotification.getRecipientOrganizationUnitRefs() != null) {
                for (NodeRef organizationUnitRef : generalizedNotification.getRecipientOrganizationUnitRefs()) {
                    employeeRefs.addAll(orgstructureService.getOrganizationElementEmployees(organizationUnitRef));
                }
            }

            if (generalizedNotification.getRecipientWorkGroupRefs() != null) {
                for (NodeRef workGroupRef : generalizedNotification.getRecipientWorkGroupRefs()) {
                    employeeRefs.addAll(orgstructureService.getOrganizationElementEmployees(workGroupRef));
                }
            }

            if (generalizedNotification.getRecipientPositionRefs() != null) {
                for (NodeRef positionRef : generalizedNotification.getRecipientPositionRefs()) {
                    if (orgstructureService.isPosition(positionRef)) {
                        employeeRefs.addAll(orgstructureService.getEmployeesByPosition(positionRef));
                    }
                }
            }

            if (generalizedNotification.getRecipientBusinessRoleRefs() != null) {
                for (NodeRef businessRoleRef : generalizedNotification.getRecipientBusinessRoleRefs()) {
                    employeeRefs.addAll(orgstructureService.getEmployeesByBusinessRole(businessRoleRef, true));
                }
            }

			for (NodeRef typeRef : generalizedNotification.getTypeRefs()) {
                addNotificationUnits(generalizedNotification, employeeRefs, typeRef, result);
            }
		}
		return result;
	}

	private void addNotificationUnits(Notification generalizedNotification, Set<NodeRef> employeeRefs,
	                                  NodeRef typeRef, Set<NotificationUnit> resultSet) {
		for (NodeRef employeeRef: employeeRefs) {
			if (orgstructureService.isEmployee(employeeRef) && !employeeRef.equals(generalizedNotification.getInitiatorRef())) {
				NotificationUnit newNotificationUnit = new NotificationUnit();
				newNotificationUnit.setAutor(generalizedNotification.getAutor());
				newNotificationUnit.setDescription(generalizedNotification.getDescription());
				newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
				newNotificationUnit.setObjectRef(generalizedNotification.getObjectRef());
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
				(notification.getRecipientBusinessRoleRefs() == null || notification.getRecipientBusinessRoleRefs().size() == 0) &&
				(notification.getRecipientWorkGroupRefs() == null || notification.getRecipientWorkGroupRefs().size() == 0)) {
			logger.warn("Должен быть хотя бы один получатель уведомления");
			return false;
		}
		return true;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return notificationsRootRef;
	}

	public NodeRef getCurrentUserSettingsNode() {
		final NodeRef rootFolder = this.getServiceRootFolder();
		final String settingsObjectName = authService.getCurrentUserName() + "_" + NOTIFICATIONS_SETTINGS_NODE_NAME;

		NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
		if (settings != null) {
			return settings;
		} else {
			AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							NodeRef settingsRef;
							synchronized (lock) {
								settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
								if (settingsRef == null) {
									QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
									QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, settingsObjectName);
									QName nodeTypeQName = TYPE_NOTIFICATIONS_USER_SETTINGS;

									Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
									properties.put(ContentModel.PROP_NAME, settingsObjectName);
									ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
									settingsRef = associationRef.getChildRef();
								}
							}
							return settingsRef;
						}
					});
				}
			};
			return AuthenticationUtil.runAsSystem(raw);
		}
	}
}
