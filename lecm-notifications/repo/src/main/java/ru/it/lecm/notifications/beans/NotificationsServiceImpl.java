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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.notifications.template.FreemarkerParserImpl;
import ru.it.lecm.notifications.template.Parser;
import ru.it.lecm.notifications.template.SpelParserImpl;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretaryService;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static ru.it.lecm.orgstructure.beans.OrgstructureBean.TYPE_BUSINESS_ROLE;
import static ru.it.lecm.orgstructure.beans.OrgstructureBean.TYPE_EMPLOYEE;

/**
 * User: AIvkin Date: 10.01.13 Time: 16:53
 */
public class NotificationsServiceImpl extends BaseBean implements NotificationsService, ApplicationContextAware {

    private static final String NOTIFICATION_POST_TRANSACTION_PENDING_OBJECTS = "notification_post_transaction_pending_objects";

    final private static Logger logger = LoggerFactory.getLogger(NotificationsServiceImpl.class);
    private static final int DEFAULT_N_DAYS = 5;
    private static final String DOCUMENT_LINK = "Документ {#mainObject.wrapAsLink(#mainObject.attribute(\"lecm-document:present-string\"))}";
    private static final String DEFAULT_NOTIFICATION_TEMPLATE = "При формировании уведомления произошла ошибка. За дополнительной информацией обратитесь к администратору. %s Ошибка: %s";

    private OrgstructureBean orgstructureService;
    private DictionaryBean dictionaryService;
    private SubstitudeBean substituteService;
	private SecretaryService secretaryService;

	private Map<NodeRef, NotificationChannelBeanBase> channels;
	private Map<String, NodeRef> channelsNodeRefs;
	private LecmPermissionService lecmPermissionService;

    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionListener transactionListener;
	private ApplicationContext applicationContext;

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

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

	public void setSecretaryService(SecretaryService secretaryService) {
		this.secretaryService = secretaryService;
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
     * Разделение обобщённого уведомления на атомарные
     *
     * @param generalizedNotification Обобщённое уведомление
     * @return Множество атомарных уведомлений
     */
    private Set<NotificationUnit> createAtomicNotifications(Notification generalizedNotification) throws TemplateRunException, TemplateParseException, TemplateNotFoundException {
        long start = System.currentTimeMillis();
        logger.trace("createAtomicNotifications start: {}", start);
        Set<NotificationUnit> result = new HashSet<>();
        if (generalizedNotification != null) {

            if (generalizedNotification.getTemplateCode() != null) {

                NodeRef templateDicRec = dictionaryService.getRecordByParamValue(NOTIFICATION_TEMPLATE_DICTIONARY_NAME, ContentModel.PROP_NAME, generalizedNotification.getTemplateCode());
                if (templateDicRec != null) {
                    String template = (String) nodeService.getProperty(templateDicRec, PROP_NOTIFICATION_TEMPLATE);
                    String subject = (String) nodeService.getProperty(templateDicRec, PROP_NOTIFICATION_TEMPLATE_SUBJECT);
                    List<AssociationRef> templateAssocs = nodeService.getTargetAssocs(templateDicRec, ASSOC_NOTIFICATION_TEMPLATE_TEMPLATE_ASSOC);
                    NodeRef templateRef = templateAssocs.isEmpty() ? null : templateAssocs.get(0).getTargetRef();

                    generalizedNotification.setTemplate(template);
                    generalizedNotification.setSubject(subject);
                    generalizedNotification.setTemplateRef(templateRef);
                } else {
                    throw new TemplateNotFoundException("Не найден шаблон уведомления: " + generalizedNotification.getTemplateCode());
                }
            }

			String templateDescription = parseTemplate(generalizedNotification.getTemplate(), generalizedNotification.getTemplateModel());
            NodeRef templateRef = generalizedNotification.getTemplateRef();
            if (templateRef != null && generalizedNotification.getTemplateModel() != null) {
                generalizedNotification.getTemplateModel().put("notificationText", templateDescription);
            }
			String templateBody = parseTemplateRef(generalizedNotification.getTemplateRef(), generalizedNotification.getTemplateModel());
			String templateSubject  = parseTemplate(generalizedNotification.getSubject(), generalizedNotification.getTemplateModel());
            Set<NodeRef> employeeRefs = new HashSet<>();
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
                Boolean active = false;
                if (delegationOpts != null) {
                    active = Boolean.TRUE.equals(nodeService.getProperty(delegationOpts, IS_ACTIVE));
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
				//если активирован флаг "включать секретарей", а также делегирования или нет совсем, или оно не активно
				//то для каждого сотрудника из списка рассылки мы получаем секретарей
				// и формируем для них особый текст уведомления
				if (generalizedNotification.isIncludeSeretaries() && (delegationOpts == null || !active)) {
					NodeRef tasksSecretary = secretaryService.getTasksSecretary(employee);
					if (tasksSecretary != null) {
						List<NodeRef> typeRefs = generalizedNotification.getTypeRefs();
						if (typeRefs == null || typeRefs.isEmpty()) {
							typeRefs = getEmployeeDefaultNotificationTypes(tasksSecretary);
						}
						Serializable employeeShortName = nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
						String description;
						String unitTemplateSubject = null;
						if (StringUtils.isNotEmpty(templateDescription)) {
							description = String.format("Уведомление для %s: %s", employeeShortName, templateDescription);
						} else {
							description = String.format("Уведомление для %s: %s", employeeShortName, generalizedNotification.getDescription());
						}
						if (StringUtils.isNotEmpty(templateSubject)) {
							unitTemplateSubject = String.format("Уведомление для %s: %s", employeeShortName, templateSubject);
						}
						for (NodeRef typeRef : typeRefs) {
							NotificationUnit newNotificationUnit = new NotificationUnit();
							newNotificationUnit.setAutor(generalizedNotification.getAuthor());
							newNotificationUnit.setDescription(description);
							newNotificationUnit.setBody(templateBody);
							newNotificationUnit.setSubject(unitTemplateSubject);
							newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
							newNotificationUnit.setObjectRef(generalizedNotification.getObjectRef());

							newNotificationUnit.setTypeRef(typeRef);
							newNotificationUnit.setRecipientRef(tasksSecretary);
							result.add(newNotificationUnit);
						}
					}
					logger.trace("Secretaties added. Current size: {}", employeeRefs.size());
				}
            }
            logger.trace("Delegates added. Current size: {}", employeeRefs.size());
            result.addAll(addNotificationUnits(generalizedNotification, employeeRefs, templateBody, templateDescription, templateSubject));
            logger.debug("Atomic notifications. Current size: {}, time: {}", result.size(), System.currentTimeMillis() - start);
        }
        return result;
    }

    private Set<NotificationUnit> addNotificationUnits(Notification generalizedNotification, Set<NodeRef> employeeRefs, String templateBody, String templateDescription, String templateSubject) {
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
					if (StringUtils.isNotEmpty(templateDescription)) {
						newNotificationUnit.setDescription(templateDescription);
					} else {
						newNotificationUnit.setDescription(generalizedNotification.getDescription());
					}
                    newNotificationUnit.setFormingDate(generalizedNotification.getFormingDate());
                    newNotificationUnit.setObjectRef(generalizedNotification.getObjectRef());
                    newNotificationUnit.setTypeRef(typeRef);
                    newNotificationUnit.setRecipientRef(employeeRef);
					newNotificationUnit.setBody(templateBody);
					newNotificationUnit.setSubject(templateSubject);
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

    @Override
    public void sendNotificationByTemplate(NodeRef nodeRef, List<NodeRef> recipients, String templateCode) {
        sendNotificationByTemplate(nodeRef, recipients, templateCode, null);
    }

    @Override
    public void sendNotificationByTemplate(String author, NodeRef nodeRef, List<NodeRef> recipients, String templateCode) {
        sendNotificationByTemplate(AuthenticationUtil.getSystemUserName(), nodeRef, recipients, templateCode, null);
    }

    @Override
    public void sendNotificationByTemplate(NodeRef nodeRef, List<NodeRef> recipients, String templateCode, Map<String, Object> objects) {
        sendNotificationByTemplate(AuthenticationUtil.getSystemUserName(), nodeRef, recipients, templateCode, objects);
    }

    @Override
    public void sendNotificationByTemplate(String author, NodeRef nodeRef, List<NodeRef> recipients, String templateCode, Map<String, Object> objects) {
        if (objects == null) {
            objects = new HashMap<>();
        }
        objects.put("mainObject", nodeRef);
        Notification notification = new Notification(objects);
        notification.setTemplateCode(templateCode);
        notification.setRecipientEmployeeRefs(recipients);
        notification.setAuthor(author);
        notification.setObjectRef(nodeRef);
        sendNotification(notification);
    }

	private String parseTemplate(String template, Map<String, Object> objects) throws TemplateRunException, TemplateParseException {
		String parsed;
		if (StringUtils.isNotEmpty(template)) {
			Parser parser = new SpelParserImpl(applicationContext);
			parsed = parser.runTemplate(template, objects);
		} else {
			parsed = null;
		}
		return parsed;
	}

	private String parseTemplateRef(NodeRef templateRef, Map<String, Object> objects) throws TemplateRunException, TemplateParseException {
		String parsed;
		if (templateRef !=null) {
			Parser parcer = new FreemarkerParserImpl(applicationContext);
			parsed = parcer.runTemplate(templateRef.toString(), objects);
		} else {
			parsed = null;
		}
		return parsed;
	}

	/*
	@Override
	public void sendNotification(String author, Map<String, NodeRef> objects, String templateCode, List<NodeRef> recipientEmployees, NodeRef initiatorRef, boolean dontCheckAccessToObject) {
		NodeRef templateDicRec = dictionaryService.getRecordByParamValue(NOTIFICATION_TEMPLATE_DICTIONARY_NAME, PROP_NOTIFICATION_TEMPLATE_CODE, templateCode);
		String template = (String)nodeService.getProperty(templateDicRec, PROP_NOTIFICATION_TEMPLATE);
		String subject = (String)nodeService.getProperty(templateDicRec, PROP_NOTIFICATION_TEMPLATE_SUBJECT);
		List<AssociationRef> templateAssocs = nodeService.getTargetAssocs(templateDicRec, ASSOC_NOTIFICATION_TEMPLATE_TEMPLATE_ASSOC);
		NodeRef templateRef = templateAssocs.isEmpty() ? null : templateAssocs.get(0).getTargetRef();
//        String desc = parseTemplate(templateCode, objects);
//		logger.debug(desc);
		Notification notification = new Notification(objects);
		notification.setTemplate(template);
		notification.setSubject(subject);
		notification.setTemplateRef(templateRef);
		notification.setAuthor(author);
		notification.setRecipientEmployeeRefs(recipientEmployees);
		notification.setObjectRef(objects.get("mainObject"));
//		notification.setDescription(desc);
		notification.setInitiatorRef(initiatorRef);

        sendNotification(notification, dontCheckAccessToObject);
	}
	*/

	@Override
	public void sendNotification(String author, NodeRef initiatorRef, List<NodeRef> recipientRefs, String templateCode, Map<String, Object> config, boolean dontCheckAccessToObject) {
		Notification notification = new Notification(config);
        notification.setTemplateCode(templateCode);
		notification.setAuthor(author);
		notification.setRecipientEmployeeRefs(recipientRefs);
		notification.setObjectRef((NodeRef)config.get("mainObject"));
		notification.setInitiatorRef(initiatorRef);

		sendNotification(notification, dontCheckAccessToObject);
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
                                                    Set<NotificationUnit> notificationUnits;
                                                    try {
                                                        notificationUnits = createAtomicNotifications(notification);
                                                    } catch (TemplateParseException | TemplateRunException | TemplateNotFoundException e) {
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                                                        String error = dateFormat.format(new Date()) + " " + e.getMessage();
                                                        String document = "";
                                                        if (notification.getTemplateModel().get("mainObject") != null) {
                                                            document = DOCUMENT_LINK;
                                                        }
                                                        notification.setTemplateCode(null);
                                                        notification.setTemplate(String.format(DEFAULT_NOTIFICATION_TEMPLATE, document, error));
                                                        notificationUnits = createAtomicNotifications(notification);
                                                    }
                                                        if (notificationUnits != null && notificationUnits.size() > 0) {
                                                            for (NotificationUnit notf : notificationUnits) {
                                                                sendNotification(notf, notification.isDontCheckAccessToObject());
                                                            }
                                                        } else {
                                                            logger.warn("Атомарные уведомления не были сформированы");
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
