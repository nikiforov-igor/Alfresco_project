package ru.it.lecm.notifications.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.AssociationRef;
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
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static ru.it.lecm.orgstructure.beans.OrgstructureBean.TYPE_EMPLOYEE;
import static ru.it.lecm.orgstructure.beans.OrgstructureBean.TYPE_BUSINESS_ROLE;

/**
 * User: AIvkin Date: 10.01.13 Time: 16:53
 */
public class NotificationsServiceImpl extends BaseBean implements NotificationsService {

    private static final String NOTIFICATION_POST_TRANSACTION_PENDING_OBJECTS = "notification_post_transaction_pending_objects";

    final private static Logger logger = LoggerFactory.getLogger(NotificationsServiceImpl.class);
    private static final int DEFAULT_N_DAYS = 5;

    private OrgstructureBean orgstructureService;
    private DictionaryBean dictionaryService;
    private SubstitudeBean substituteService;

	private Map<NodeRef, NotificationChannelBeanBase> channels;
	private Map<String, NodeRef> channelsNodeRefs;
	private LecmPermissionService lecmPermissionService;

    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionListener transactionListener;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

	@Override
	public NodeRef getNotificationsRootRef() throws WriteTransactionNeededException {
		return getFolder(NOTIFICATIONS_ROOT_ID);
	}

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setSubstituteService(SubstitudeBean substituteService) {
        this.substituteService = substituteService;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
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
    */
    public void init() {
        //Проверить наличие и создать ноду с глобальными настройками.
        //TODO Уточнить про права. Нужно ли делать runAsSystem, при том что она и так создаётся?
        lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {

            @Override
            public Void execute() throws Throwable {
                if (null == getGlobalSettingsNode()) {
                    createGlobalSettingsNode();
                }
                return null;
            }
        });
        transactionListener = new NotificationTransactionListener();
    }

    @Override
    public boolean isNotificationType(NodeRef ref) {
        Set<QName> types = new HashSet<QName>();
        types.add(TYPE_NOTIFICATION_TYPE);
        return isProperType(ref, types);
    }

    public void loadChannels() {
        NodeRef channelsDictionary = dictionaryService.getDictionaryByName(NOTIFICATION_TYPE_DICTIONARY_NAME);
        if (channelsDictionary != null) {
            List<NodeRef> channelsDictionaryValue = dictionaryService.getChildren(channelsDictionary);
            if (channelsDictionaryValue != null) {
                for (NodeRef typeRef : channelsDictionaryValue) {
                    String beanId = (String) nodeService.getProperty(typeRef, PROP_SPRING_BEAN_ID);
                    if (beanId != null) {
                        if (channelsNodeRefs == null) {
                            channelsNodeRefs = new HashMap<String, NodeRef>();
                        }
                        channelsNodeRefs.put(beanId, typeRef);
                        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
                        try {
                            NotificationChannelBeanBase channelBean = (NotificationChannelBeanBase) ctx.getBean(beanId);
                            if (channels == null) {
                                channels = new HashMap<NodeRef, NotificationChannelBeanBase>();
                            }
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
    public void sendNotification(List<String> channels, Notification notification) {
        sendNotification(channels, notification, false);
    }

    @Override
    public void sendNotification(List<String> channels, Notification notification, boolean dontCheckAccessToObject) {
        List<NodeRef> typeRefs = new ArrayList<NodeRef>();
        if (channels != null) {
            for (String channel : channels) {
                if (getChannelsNodeRefs().containsKey(channel)) {
                    typeRefs.add(getChannelsNodeRefs().get(channel));
                }
            }
            notification.setTypeRefs(typeRefs);
        }
        sendNotification(notification, dontCheckAccessToObject);
    }

    @Override
    public void sendNotification(final Notification notification) {
        sendNotification(notification, false);
    }

    @Override
    public void sendNotification(final Notification notification, final boolean dontCheckAccessToObject) {
        AlfrescoTransactionSupport.bindListener(this.transactionListener);

        Map<Date,Queue<Notification>> pendingActions = AlfrescoTransactionSupport.getResource(NOTIFICATION_POST_TRANSACTION_PENDING_OBJECTS);
        if (pendingActions == null) {
//            pendingActions = new ArrayList<Notification>();
            pendingActions = new HashMap<>();
            AlfrescoTransactionSupport.bindResource(NOTIFICATION_POST_TRANSACTION_PENDING_OBJECTS, pendingActions);
        }

        notification.setDontCheckAccessToObject(dontCheckAccessToObject);
//        pendingActions.add(notification);
        Date notificatiDate = notification.getFormingDate();
        Queue<Notification> pool = pendingActions.get(notificatiDate);
        if (null == pool) {
            pool = new LinkedList<>();
            pendingActions.put(notificatiDate, pool);
        }
        pool.add(notification);
    }

	private void sendNotification(NotificationUnit notification, boolean dontCheckAccessToObject) {
		logger.trace("###### sendNotification begin ######");
		if (notification != null && notification.getRecipientRef() != null && notification.getObjectRef() != null) {
			String employeeLogin = this.orgstructureService.getEmployeeLogin(notification.getRecipientRef());
			logger.trace("###### Notification, recipient, object checked successfully. ######");
			logger.trace("###### {} ######", notification.getDescription());
			Boolean hasReadAccess = null;
			logger.trace("###### dontCheckAccessToObject = {} ######", dontCheckAccessToObject);
			if (employeeLogin != null && (dontCheckAccessToObject || (hasReadAccess = lecmPermissionService.hasReadAccess(notification.getObjectRef(), employeeLogin)))) {
				logger.trace("###### Employee login = {} ######", employeeLogin);
				logger.trace("###### hasReadAccess = {} ######", hasReadAccess);
				if (getChannels().containsKey(notification.getTypeRef())) {
					NotificationChannelBeanBase channelBean = getChannels().get(notification.getTypeRef());
					if (channelBean != null) {
						logger.trace("###### channelBean = {} ######", channelBean.getClass().getName());
						channelBean.sendNotification(notification);
					}
				}
			}
		}
		logger.trace("###### sendNotification end ######");
	}

    /**
     * Создание обобщённого уведомления
     *
     * @param notification Обобщённое уведомление
     * @return Ссылка на обобщённое уведомление
     */
    //TODO DONE Refactoring in process...
    private NodeRef createGeneralizedNotification(Notification notification) throws WriteTransactionNeededException {
        try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create GeneralizedNotification.");
        }

        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
        properties.put(PROP_GENERAL_AUTOR, notification.getAuthor());
        properties.put(PROP_GENERAL_DESCRIPTION, notification.getDescription());
        properties.put(PROP_GENERAL_FORMING_DATE, notification.getFormingDate());

        NodeRef notificationsGenaralizetionRootRef = getFolder(NOTIFICATIONS_GENERALIZATION_ROOT_ID);
        List<String> directoryPaths = getDateFolderPath(notification.getFormingDate());
        NodeRef saveDirectoryRef = getFolder(notificationsGenaralizetionRootRef, directoryPaths);
        if (null == saveDirectoryRef) {
            saveDirectoryRef = createPath(notificationsGenaralizetionRootRef, directoryPaths);
        }

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
        long start = System.currentTimeMillis();
        logger.trace("createAtomicNotifications start: {}", start);
        Set<NotificationUnit> result = new HashSet<NotificationUnit>();
        if (generalizedNotification != null) {
            Set<NodeRef> employeeRefs = new HashSet<NodeRef>();
            if (generalizedNotification.getRecipientEmployeeRefs() != null) {
                employeeRefs.addAll(generalizedNotification.getRecipientEmployeeRefs());
                logger.trace("Recipients added. Current size: {}", employeeRefs.size());
            }

            if (generalizedNotification.getRecipientOrganizationUnitRefs() != null) {
                for (NodeRef organizationUnitRef : generalizedNotification.getRecipientOrganizationUnitRefs()) {
                    employeeRefs.addAll(orgstructureService.getOrganizationElementEmployees(organizationUnitRef));
                }
                logger.trace("Units added. Current size: {}", employeeRefs.size());
            }

            if (generalizedNotification.getRecipientWorkGroupRefs() != null) {
                for (NodeRef workGroupRef : generalizedNotification.getRecipientWorkGroupRefs()) {
                    employeeRefs.addAll(orgstructureService.getOrganizationElementEmployees(workGroupRef));
                }
                logger.trace("Groups added. Current size: {}", employeeRefs.size());
            }

            if (generalizedNotification.getRecipientPositionRefs() != null) {
                for (NodeRef positionRef : generalizedNotification.getRecipientPositionRefs()) {
                    if (orgstructureService.isPosition(positionRef)) {
                        employeeRefs.addAll(orgstructureService.getEmployeesByPosition(positionRef));
                    }
                }
                logger.trace("Positions added. Current size: {}", employeeRefs.size());
            }

            if (generalizedNotification.getRecipientBusinessRoleRefs() != null) {
                for (NodeRef businessRoleRef : generalizedNotification.getRecipientBusinessRoleRefs()) {
                    employeeRefs.addAll(orgstructureService.getEmployeesByBusinessRole(businessRoleRef, true));
                }
                logger.trace("Roles added. Current size: {}", employeeRefs.size());
            }

            //пробегаемся по сотрудникам, смотрим их параметры делегирования и наличие доверенных лиц (в том числе и по доверенностям
            //если таковые имеются, то добавляем их в в общий перечень
            // временный сет нужен для того, чтобы избежать ConcurrentModificationException при модификации коллекции во время итерации по ней
			//"заместителя" по всем БР мы всегда добавляем в получатели уведомлений
			//делегатов по БР мы добавляем по факту их наличия
			final List<NodeRef> delegateBroles = generalizedNotification.getDelegateBusinessRoleRefs();
			final boolean hasDelegateBroles = delegateBroles != null && delegateBroles.size() > 0;
            Set<NodeRef> tmpEmployeeRefs = new HashSet<>(employeeRefs);
            for (NodeRef employee : tmpEmployeeRefs) {
                NodeRef delegationOpts = findNodeByAssociationRef(employee, IDelegation.ASSOC_DELEGATION_OPTS_OWNER, IDelegation.TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);
                if (delegationOpts != null) {
                    Boolean active = (Boolean) nodeService.getProperty(delegationOpts, IS_ACTIVE);
                    if (active) {
                        NodeRef trustee = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                        if (trustee != null) {
                            employeeRefs.add(trustee);
                        }

						if (hasDelegateBroles) {
							Set<QName> types = new HashSet<>();
							types.add(IDelegation.TYPE_PROCURACY);

							List<ChildAssociationRef> procuraciesList = nodeService.getChildAssocs(delegationOpts, types);

							for (ChildAssociationRef procuaryAssoc : procuraciesList) {
								NodeRef procuary = procuaryAssoc.getChildRef();
								Boolean procuaryActive = (Boolean) nodeService.getProperty(procuary, IS_ACTIVE);

								if (procuaryActive) {
									NodeRef procuaryTrustee = findNodeByAssociationRef(procuary, IDelegation.ASSOC_PROCURACY_TRUSTEE, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
									NodeRef procuracyBrole = findNodeByAssociationRef(procuary, IDelegation.ASSOC_PROCURACY_BUSINESS_ROLE, TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);

									if (procuaryTrustee != null && procuracyBrole != null && delegateBroles.contains(procuracyBrole)) {
										employeeRefs.add(procuaryTrustee);
									}
								}
							}
						}
                    }
                }
            }
            logger.trace("Delegates added. Current size: {}", employeeRefs.size());
            result.addAll(addNotificationUnits(generalizedNotification, employeeRefs));
            logger.debug("Atomic notifications. Current size: {}, time: {}", result.size(), System.currentTimeMillis() - start);
        }
        return result;
    }

    private Set<NotificationUnit> addNotificationUnits(Notification generalizedNotification, Set<NodeRef> employeeRefs) {
        Set<NotificationUnit> result = new HashSet<NotificationUnit>();

        for (NodeRef employeeRef : employeeRefs) {
            if (orgstructureService.isEmployee(employeeRef) && !employeeRef.equals(generalizedNotification.getInitiatorRef())) {
                List<NodeRef> typeRefs = generalizedNotification.getTypeRefs();
                if (typeRefs == null || typeRefs.isEmpty()) {
                    typeRefs = getEmployeeDefaultNotificationTypes(employeeRef);
                }

                for (NodeRef typeRef : typeRefs) {
                    NotificationUnit newNotificationUnit = new NotificationUnit();
                    newNotificationUnit.setAutor(generalizedNotification.getAuthor());
                    newNotificationUnit.setDescription(generalizedNotification.getDescription());
                    newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
                    newNotificationUnit.setObjectRef(generalizedNotification.getObjectRef());
                    newNotificationUnit.setTypeRef(typeRef);
                    newNotificationUnit.setRecipientRef(employeeRef);
                    result.add(newNotificationUnit);
                }
            }
        }

        return result;
    }

    /**
     * Проверка обобщённого уведомления
     *
     * @param notification Обобщённое уведомление
     * @return false - если уведомление неверно заполнено, иначе true
     */
    private boolean checkNotification(Notification notification) {
        if (notification == null) {
            logger.warn("Уведомление null");
            return false;
        }
        if (notification.getAuthor() == null) {
            logger.warn("Автор уведомление null");
            return false;
        }
        if (!nodeService.exists(notification.getObjectRef())) {
            logger.warn("Ссылка на объект уведомление не существует");
            return false;
        }
        if ((notification.getRecipientEmployeeRefs() == null || notification.getRecipientEmployeeRefs().isEmpty())
                && (notification.getRecipientOrganizationUnitRefs() == null || notification.getRecipientOrganizationUnitRefs().isEmpty())
                && (notification.getRecipientPositionRefs() == null || notification.getRecipientPositionRefs().isEmpty())
                && (notification.getRecipientBusinessRoleRefs() == null || notification.getRecipientBusinessRoleRefs().isEmpty())
                && (notification.getRecipientWorkGroupRefs() == null || notification.getRecipientWorkGroupRefs().isEmpty())) {
            logger.warn("Должен быть хотя бы один получатель уведомления");
            return false;
        }
        return true;
    }

	@Override
	public NodeRef getServiceRootFolder(){
		try {
			return getNotificationsRootRef();
		} catch(WriteTransactionNeededException e) {
			logger.debug("Can't get service root folder", e);
			throw new RuntimeException(e);
		}
	}

    @Override
    public NodeRef getCurrentUserSettingsNode() {
        return getUserSettingsNode(authService.getCurrentUserName());
    }

    @Override
    public NodeRef createCurrentUserSettingsNode() {
        return createUserSettingsNode(authService.getCurrentUserName());
    }

    @Override
    public NodeRef createUserSettingsNode(String userName) {
        final NodeRef rootFolder = this.getServiceRootFolder();
        final String settingsObjectName = userName + "_" + NOTIFICATIONS_SETTINGS_NODE_NAME;
        NodeRef settingsRef;
        QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, settingsObjectName);
        QName nodeTypeQName = TYPE_NOTIFICATIONS_USER_SETTINGS;

        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, settingsObjectName);
        ChildAssociationRef associationRef = nodeService.createNode(getServiceRootFolder(), assocTypeQName, assocQName, nodeTypeQName, properties);
        settingsRef = associationRef.getChildRef();

        List<NodeRef> systemDefaulChannels = getSystemDefaultNotificationTypes();
        if (systemDefaulChannels != null) {
            for (NodeRef typeRef : systemDefaulChannels) {
                nodeService.createAssociation(settingsRef, typeRef, ASSOC_DEFAULT_NOTIFICATIONS_TYPES);
            }
        }

        return settingsRef;
    }

    @Override
    public NodeRef getUserSettingsNode(String userName) {
//		TODO: DONE Метод разделён, создание вынесено в метод createUserSettingsNode
        final NodeRef rootFolder = this.getServiceRootFolder();
        final String settingsObjectName = userName + "_" + NOTIFICATIONS_SETTINGS_NODE_NAME;

        NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
        return settings;
    }

    @Override
    public List<NodeRef> getSystemDefaultNotificationTypes() {
        List<NodeRef> systemDefaultChannels = dictionaryService.getRecordsByParamValue(NOTIFICATION_TYPE_DICTIONARY_NAME, PROP_DEFAULT_SELECT, true);
        if (systemDefaultChannels == null || systemDefaultChannels.isEmpty()) {
            logger.warn("Do not select any default notification channel");
        }
        return systemDefaultChannels;
    }

    @Override
    public List<NodeRef> getEmployeeDefaultNotificationTypes(NodeRef employee) {
        List<NodeRef> result = new ArrayList<NodeRef>();
        String userName = orgstructureService.getEmployeeLogin(employee);
        NodeRef settings = getUserSettingsNode(userName);

        if (settings != null) {
            List<AssociationRef> defaultNotificationTypes = nodeService.getTargetAssocs(settings, ASSOC_DEFAULT_NOTIFICATIONS_TYPES);
            if (defaultNotificationTypes != null) {
                for (AssociationRef assocRef : defaultNotificationTypes) {
                    NodeRef typeRef = assocRef.getTargetRef();
                    if (!isArchive(typeRef)) {
                        result.add(typeRef);
                    }
                }
            }

        }

        if (result.isEmpty()) {
            result = getSystemDefaultNotificationTypes();
        }

        return result;
    }

    @Override
    public List<NodeRef> getCurrentUserDefaultNotificationTypes() {
        return getEmployeeDefaultNotificationTypes(orgstructureService.getCurrentEmployee());
    }

    @Override
    public NodeRef getGlobalSettingsNode() {
        final NodeRef rootFolder = this.getServiceRootFolder();
        final String settingsObjectName = NOTIFICATIONS_SETTINGS_NODE_NAME;
        return nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
    }

    private NodeRef createGlobalSettingsNode() throws WriteTransactionNeededException {
        return createNode(this.getServiceRootFolder(), TYPE_NOTIFICATIONS_GLOBAL_SETTINGS, NOTIFICATIONS_SETTINGS_NODE_NAME, null);
    }

    @Override
    public boolean isEnablePassiveNotifications() {
        NodeRef globalSettingsNode = getGlobalSettingsNode();
        if (globalSettingsNode != null) {
            return (Boolean) nodeService.getProperty(globalSettingsNode, PROP_ENABLE_PASSIVE_NOTIFICATIONS);
        } else {
            return false;
        }
    }

    @Override
    public int getSettingsNDays() {
        NodeRef globalSettingsNode = getGlobalSettingsNode();
        if (globalSettingsNode != null) {
            return (Integer) nodeService.getProperty(globalSettingsNode, PROP_N_DAYS);
        } else {
            return DEFAULT_N_DAYS;
        }
    }

    @Override
    public void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, List<String> channels, NodeRef initiatorRef) {
        sendNotification(author, object, textFormatString, recipientEmployees, channels, initiatorRef, false);
    }

    @Override
    public void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, List<String> channels, NodeRef initiatorRef, boolean dontCheckAccessToObject) {
        Notification notification = new Notification();
        notification.setAuthor(author);
        notification.setRecipientEmployeeRefs(recipientEmployees);
        notification.setObjectRef(object);
        notification.setDescription(substituteService.formatNodeTitle(object, textFormatString));
        notification.setInitiatorRef(initiatorRef);
        sendNotification(channels, notification, dontCheckAccessToObject);
    }

    @Override
    public void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, NodeRef initiatorRef) {
        sendNotification(author, object, textFormatString, recipientEmployees, initiatorRef, false);
    }

    @Override
    public void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, NodeRef initiatorRef, boolean dontCheckAccessToObject) {
        Notification notification = new Notification();
        notification.setAuthor(author);
        notification.setRecipientEmployeeRefs(recipientEmployees);
        notification.setObjectRef(object);
        notification.setDescription(substituteService.formatNodeTitle(object, textFormatString));
        notification.setInitiatorRef(initiatorRef);
        sendNotification(notification, dontCheckAccessToObject);
    }

	@Override
	public void sendNotification(String author, NodeRef object, String textFormatString, List<NodeRef> recipientEmployees, List<String> channels, NodeRef initiatorRef, boolean dontCheckAccessToObject, List<NodeRef> delegateBusinessRoleRefs) {
        Notification notification = new Notification();
        notification.setAuthor(author);
        notification.setRecipientEmployeeRefs(recipientEmployees);
        notification.setObjectRef(object);
        notification.setDescription(substituteService.formatNodeTitle(object, textFormatString));
        notification.setInitiatorRef(initiatorRef);
		notification.setDelegateBusinessRoleRefs(delegateBusinessRoleRefs);
        sendNotification(channels, notification, dontCheckAccessToObject);
	}

    private class NotificationTransactionListener implements TransactionListener {

        @Override
        public void flush() {

        }

        @Override
        public void beforeCommit(boolean readOnly) {

        }

        @Override
        public void beforeCompletion() {

        }

        @Override
        public void afterCommit() {
            Map<Date, Queue<Notification>> pendingNotifications = AlfrescoTransactionSupport.getResource(NOTIFICATION_POST_TRANSACTION_PENDING_OBJECTS);
            if (pendingNotifications != null) {
                for (Map.Entry<Date, Queue<Notification>> entry : pendingNotifications.entrySet()) {
                    final Date date = entry.getKey();
                    final Queue<Notification> pool = entry.getValue();
                    AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
                        @Override
                        public Void doWork() throws Exception {
                            lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {

                                @Override
                                public Void execute() throws Throwable {
                                    NodeRef notificationsGenaralizetionRootRef = getFolder(NOTIFICATIONS_GENERALIZATION_ROOT_ID);
                                    List<String> directoryPaths = getDateFolderPath(date);
                                    NodeRef saveDirectoryRef = getFolder(notificationsGenaralizetionRootRef, directoryPaths);
                                    if (null == saveDirectoryRef) {
                                        logger.debug("Notifications folder not found. Trying to create.");
                                        saveDirectoryRef = createPath(notificationsGenaralizetionRootRef, directoryPaths);
                                        logger.debug("Folder created. Ref=\""+saveDirectoryRef.toString()+"\"");
                                    }
                                    return null;
                                }
                            });
                            return null;
                        }
                    });
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
                                @Override
                                public Void doWork() throws Exception {
                                    return serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                        @Override
                                        public Void execute() throws Throwable {
                                            while (!pool.isEmpty()) {
                                                Notification notification = pool.poll();
                                                if (checkNotification(notification)) {
                                                    NodeRef generalizedNotification = createGeneralizedNotification(notification);
                                                    if (generalizedNotification != null) {
                                                        Set<NotificationUnit> notificationUnits = createAtomicNotifications(notification);
                                                        if (notificationUnits != null && notificationUnits.size() > 0) {
                                                            for (NotificationUnit notf : notificationUnits) {
                                                                sendNotification(notf, notification.isDontCheckAccessToObject());
                                                            }
                                                        } else {
                                                            logger.warn("Атомарные уведомления не были сформированы");
                                                        }
                                                    } else {
                                                        logger.warn("Обобщённое уведомление не создано");
                                                    }
                                                } else {
                                                    logger.warn("Уведомление не прошло проверки");
                                                }
                                            }
                                            return null;
                                        }
                                    }, false, true);
                                }
                            });
                        }
                    };
                    threadPoolExecutor.execute(runnable);
                }
            }
        }

        @Override
        public void afterRollback() {

        }
    }
}
