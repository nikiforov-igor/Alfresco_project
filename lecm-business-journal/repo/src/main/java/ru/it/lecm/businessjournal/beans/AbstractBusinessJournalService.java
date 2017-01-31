package ru.it.lecm.businessjournal.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.*;
import org.alfresco.repo.security.authentication.AuthenticationUtil;

/**
 * User: pmelnikov
 * Date: 02.12.13
 * Time: 15:54
 */
public abstract class AbstractBusinessJournalService extends BaseBean {

    protected LecmPermissionService lecmPermissionService;
    protected StateMachineServiceBean stateMachineService;
    private DictionaryBean dictionaryService;
    protected OrgstructureBean orgstructureService;
    private SubstitudeBean substituteService;
    private DictionaryService dicService;
    private PersonService personService;
    private JmsTemplate jmsTemplate;
	private JmsTemplate jmsTemplateInternal;
	private JmsTemplate jmsTemplateExternal;
    private SimpleCache<NodeRef, Boolean> logSettingsCache;
	private boolean enableResend;
	private List<String> allowedCategories;

    private static final Logger logger = LoggerFactory.getLogger(AbstractBusinessJournalService.class);
    private ThreadLocal<IgnoredCounter> threadSettings = new ThreadLocal<IgnoredCounter>();
    private DocumentService documentService;

    public void dropCache() {
        logSettingsCache.clear();
    }


    public Boolean isEventCategoryOn(NodeRef categoryRef) {
        Boolean result;
        if (logSettingsCache.getKeys().contains(categoryRef)) {
            result = logSettingsCache.get(categoryRef);
        } else {
            result = (null == categoryRef) || !Boolean.FALSE.equals(nodeService.getProperty(categoryRef, BusinessJournalService.PROP_EVENT_CAT_ON));
            if (null != categoryRef) {
                logSettingsCache.put(categoryRef, result);
            }
        }
        return result;
    }

    public abstract void saveToStore(BusinessJournalRecord record) throws Exception;

	public void setJmsTemplateInternal(JmsTemplate jmsTemplateInternal) {
		this.jmsTemplateInternal = jmsTemplateInternal;
	}

	public void setJmsTemplateExternal(JmsTemplate jmsTemplateExternal) {
		this.jmsTemplateExternal = jmsTemplateExternal;
	}

	public void setAllowedCategories(List<String> allowedCategories) {
		this.allowedCategories = allowedCategories;
	}

	public void setEnableResend(boolean enableResend) {
		this.enableResend = enableResend;
	}

    public void setSubstituteService(SubstitudeBean substituteService) {
        this.substituteService = substituteService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDicService(DictionaryService dicService) {
        this.dicService = dicService;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setLogSettingsCache(SimpleCache<NodeRef, Boolean> logSettingsCache) {
        this.logSettingsCache = logSettingsCache;
    }

    /**
     * Получение ссылки на Категорию События по имени категории
     *
     * @param eventCategory - название категории события
     * @return ссылка на ноду или null
     */
    protected NodeRef getEventCategoryByCode(String eventCategory) {
        NodeRef evCategory = null;
        if (eventCategory != null) {
            evCategory = dictionaryService.getRecordByParamValue("Категория события", BusinessJournalService.PROP_EVENT_CAT_CODE, eventCategory);
        }
        return evCategory;
    }

	protected List<NodeRef> getAllEventCategories() {
		NodeRef root = dictionaryService.getDictionaryByName("Категория события");
		return dictionaryService.getChildren(root);
	}

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Метод заполняет карту плейсхолдеров значениями на основании типов объектов
     *
     * @param initiator  - инициатор события (cm:person)
     * @param mainObject - основной объект
     * @param objects    - список дополнительных объектов
     * @return заполненная карта
     */
    protected Map<String, String> fillHolders(NodeRef initiator, NodeRef mainObject, List<String> objects) {
        Map<String, String> holders = new HashMap<String, String>();
        holders.put(BusinessJournalService.BASE_USER_HOLDER, wrapAsLink(initiator, true));
        holders.put(BusinessJournalService.MAIN_OBJECT_HOLDER, wrapAsLink(mainObject, false));
        if (objects != null && objects.size() > 0) {
            for (int i = 0; i < objects.size() && i < BusinessJournalService.MAX_SECONDARY_OBJECTS_COUNT; i++) {
                if (objects.get(i) != null) {
                    String str = objects.get(i);
                    String description = NodeRef.isNodeRef(str) ? wrapAsLink(new NodeRef(str), false) : (isWorkflow(str) ? wrapAsWorkflowLink(str) : str);
                    holders.put(BusinessJournalService.OBJECT_HOLDER + (i + 1), description);
                }
            }
        }
        return holders;
    }

    protected String wrapAsLink(NodeRef link, boolean isInititator) {
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        if (link != null && !nodeService.exists(link)) {
            return "";
        }
        String description = isInititator ? getInitiatorDescription(link) : getObjectDescription(link);
        if (link != null) {
            String linkUrl = isLECMDocument(link) ? documentService.getDocumentUrl(link) : (isLECMDocumentAttachment(link) ? DOCUMENT_ATTACHMENT_LINK_URL : LINK_URL);
            return "<a href=\"" + serverUrl + linkUrl + "?nodeRef=" + link.toString() + "\">" + description + "</a>";
        } else {
            return description;
        }
    }

    /**
     * Метод формирующий описание заданного объекта по шаблону, определяемому по типу объекта
     *
     * @param object - текущий объект
     * @return сформированное описание
     */
    public String getObjectDescription(NodeRef object) {
        if (object != null) {
            return substituteService.getObjectDescription(object);
        } else {
            return "";
        }
    }

    protected String getInitiatorDescription(NodeRef initiator) {
        if (initiator != null) {
            return getObjectDescription(initiator);
        } else {
            return BusinessJournalService.DEFAULT_SYSTEM_TEMPLATE;
        }
    }

    /**
     * Получить стрковый шаблон Сообщения по типу объекта и категории события
     *
     * @param objectType         - тип объекта
     * @param eventCategory      - категория события
     * @param defaultDescription - описание по умолчанию
     * @return шаблон сообщения
     */
    protected String getTemplateString(NodeRef objectType, NodeRef eventCategory, String defaultDescription) {
        String template;
        NodeRef messageTemplate = null;
        if (objectType != null) {
            messageTemplate = getMessageTemplate(objectType, eventCategory);
        }
        if (messageTemplate != null) {
            // получаем шаблон сообщение
            template = getMessageTemplateByTemplate(messageTemplate);
        } else {
            // если не удалось - получаем из параметра  defaultDescription
            if (defaultDescription != null && !defaultDescription.isEmpty()) {
                template = defaultDescription;
            } else { // если параметр defaultDescription пустой - берем значение по умолчанию
                template = BusinessJournalService.DEFAULT_MESSAGE_TEMPLATE;
            }
        }
        return template != null ? template : BusinessJournalService.DEFAULT_MESSAGE_TEMPLATE;
    }

    /**
     * Метод для получения шаблонной строки для заданного Шаблона Сообщения
     *
     * @param messageTemplate - ссылка на объект Шаблон Сообщения
     * @return шаблонную строку или null, если не удалось найти соответствие
     */
    private String getMessageTemplateByTemplate(NodeRef messageTemplate) {
        Object message = nodeService.getProperty(messageTemplate, BusinessJournalService.PROP_MESSAGE_TEMP_TEMPLATE);
        return message != null ? (String) message : null;
    }

    /**
     * Метод для получения объекта справочника "Шаблон сообщения" по типу объекта и категории события
     *
     * @param objectType    - тип объекта
     * @param eventCategory - категория события
     * @return ссылка на объект "Шаблон сообщения" или NULL - если заданным параметрам не соответствует шаблон в справочнике
     */
    private NodeRef getMessageTemplate(NodeRef objectType, NodeRef eventCategory) {
        if (objectType != null && eventCategory != null) {
            List<AssociationRef> objTypeSAssocs = nodeService.getSourceAssocs(objectType, BusinessJournalService.ASSOC_MESSAGE_TEMP_OBJ_TYPE);
            List<NodeRef> types = new ArrayList<NodeRef>();
            for (AssociationRef objTypeSAssoc : objTypeSAssocs) {
                types.add(objTypeSAssoc.getSourceRef());
            }
            List<AssociationRef> evCategorySAssocs = nodeService.getSourceAssocs(eventCategory, BusinessJournalService.ASSOC_MESSAGE_TEMP_EVENT_CAT);
            List<NodeRef> categories = new ArrayList<NodeRef>();
            for (AssociationRef evCategorySAssoc : evCategorySAssocs) {
                categories.add(evCategorySAssoc.getSourceRef());
            }
            types.retainAll(categories);
            if (!types.isEmpty()) {
                return types.get(types.size() - 1);
            }
        }
        return null;
    }

    /**
     * Метод, возвращающий "заполненный" строку - шаблон с замещенными holders
     *
     * @param template - шаблонная строка
     * @param holders  - список заместителей
     * @return сформированная строка
     */
    protected String fillTemplateString(String template, Map<String, String> holders) {
        for (String key : holders.keySet()) {
            template = StringUtils.replace(template, key, holders.get(key));
        }
        return template;
    }

    public boolean isBJEngineer() {
        return orgstructureService.isCurrentEmployeeHasBusinessRole(BusinessJournalService.BUSINESS_ROLE_BUSINESS_JOURNAL_ENGENEER);
    }

    public boolean isBJRecord(NodeRef ref) {
        Set<QName> types = new HashSet<QName>();
        types.add(BusinessJournalService.TYPE_BR_RECORD);
        return isProperType(ref, types);
    }

    private boolean isLECMDocument(NodeRef document) {
        QName testType = nodeService.getType(document);
        Collection<QName> subDocumentTypes = dicService.getSubTypes(TYPE_BASE_DOCUMENT, true);
        return subDocumentTypes != null && subDocumentTypes.contains(testType);
    }

    private boolean isLECMDocumentAttachment(NodeRef attachment) {
        NodeRef attachCategoryDir = nodeService.getPrimaryParent(attachment).getParentRef();
        NodeRef attachRootDir = nodeService.getPrimaryParent(attachCategoryDir).getParentRef();
        if (attachRootDir != null && nodeService.getProperty(attachRootDir, ContentModel.PROP_NAME).equals("Вложения")) {
            NodeRef document = nodeService.getPrimaryParent(attachRootDir).getParentRef();
            if (document != null) {
                if (isLECMDocument(document)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getWorkflowDescription(String executionId) {
        WorkflowInstance workflow = serviceRegistry.getWorkflowService().getWorkflowById(executionId);
        return workflow.getDefinition().getTitle();
    }

    protected Boolean isWorkflow(String testString) {
        return testString.startsWith(BusinessJournalService.ACTIVITI_PREFIX);
    }

    protected String wrapAsWorkflowLink(String executionId) {
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        String description = getWorkflowDescription(executionId);
        return "<a href=\"" + serverUrl + WORKFLOW_LINK_URL + "?workflowId=" + executionId + "\">" + description + "</a>";
    }

    public void log(final Date date, final NodeRef initiator, NodeRef mainObject, final String eventCategory, final String defaultDescription, final List<String> objects) {
        try {
            if (mainObject == null) {
                logger.warn("Main Object not set! Setting default one");
                mainObject = repository.getCompanyHome();
            }
            IgnoredCounter counter = threadSettings.get();
            if (counter != null) {
                if (counter.isIgnored()) {
                    counter.execute();
                    return;
                } else {
                    counter.execute();
                }
            }
            BusinessJournalRecord record = createBusinessJournalRecord(date, initiator, mainObject, eventCategory, defaultDescription, objects);
            if (record == null) return;
            sendRecord(record);
        } catch (Exception e) {
			String msg = String.format("Error while save business journal record for '%s'", defaultDescription);
			if (logger.isDebugEnabled() || logger.isTraceEnabled()) {
				logger.debug(msg, e);
			} else {
	            logger.error(msg);
			}
        }
    }

	private String getCategoryCode(BusinessJournalRecord record) {
		NodeRef eventCategoryNodeRef = record.getEventCategory();
		if(eventCategoryNodeRef == null) {
			return null;
		}
		return (String) nodeService.getProperty(eventCategoryNodeRef, BusinessJournalService.PROP_EVENT_CAT_CODE);
	}

	private MessageCreator getMessageCreator(final BusinessJournalRecord record) {
		final ObjectMapper mapper = new ObjectMapper();
		return new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = null;
				try {
					message = session.createTextMessage(mapper.writeValueAsString(record));
				} catch (IOException ex) {
					logger.error("Failed to convert BJ records to JSON string", ex);
				}
				return message;
			}
		};
	}

    public void sendRecord(final BusinessJournalRecord record) {
		MessageCreator messageCreator = getMessageCreator(record);

		//Отправка сообщения удаленному сервису БЖ (bjQueue)
		jmsTemplate.send(messageCreator);

		//Отправка сообщения в очередь для внешних сервисов (bjQueueExternal)
		if(allowedCategories == null) {
			allowedCategories = new ArrayList<>(); //Избавляемся от дополнительной проверки на null
		}

		if(enableResend && (allowedCategories.contains(getCategoryCode(record)) || allowedCategories.isEmpty())) {
			jmsTemplateExternal.send(messageCreator);
		}

		//Отправка сообщения по внутренней очереди для подписок (bjQueueInternal)
		jmsTemplateInternal.convertAndSend(record);

    }

	public BusinessJournalRecord createBusinessJournalRecord(String initiator, NodeRef mainObject, String eventCategory, String defaultDescription) {
		NodeRef person = null;
        if (initiator != null && personService.personExists(initiator)) {
            person = personService.getPerson(initiator, false);
        }
		return createBusinessJournalRecord(new Date(), person, mainObject, eventCategory, defaultDescription, null);
	}

    public BusinessJournalRecord createBusinessJournalRecord(Date date, NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
        NodeRef employee = initiator != null ? orgstructureService.getEmployeeByPerson(initiator) : null;

        // заполняем карту плейсхолдеров
        Map<String, String> holdersMap = fillHolders(employee, mainObject, objects);
        // пытаемся получить объект Категория события по ключу
        NodeRef category = getEventCategoryByCode(eventCategory);
        if (!isEventCategoryOn(category)) {
            return null;
        }

        // получаем шаблон описания
        String templateString = getTemplateString(getObjectType(mainObject), category, defaultDescription);
        // заполняем шаблон данными
        String recorDescription = fillTemplateString(templateString, holdersMap);

        String mainObjectDescription = getObjectDescription(mainObject);
        List<RecordObject> objectsDescription = new ArrayList<RecordObject>();
        if (objects != null && objects.size() > 0) {
            for (int i = 0; i < objects.size() && i < 5; i++) {
            String str = objects.get(i);
            NodeRef nodeRef = NodeRef.isNodeRef(str) ? new NodeRef(str) : null;
            String objectDescription = NodeRef.isNodeRef(str) ? wrapAsLink(new NodeRef(str), false) : (isWorkflow(str) ? wrapAsWorkflowLink(str) : str);
            objectsDescription.add(new RecordObject(nodeRef, objectDescription));
        }
        }

        NodeRef objectType = getObjectType(mainObject);
        BusinessJournalRecord record = new BusinessJournalRecord(date, employee, mainObject, objectType, mainObjectDescription, recorDescription, category, objectsDescription, true);

        //Описание инициатора
        record.setInitiatorText(getInitiatorDescription(employee));
        //Описание категории
        if (category != null) {
            record.setEventCategoryText(nodeService.getProperty(category, ContentModel.PROP_NAME).toString());
        }
        //Описание типа
        if (objectType != null) {
            record.setObjectTypeText(nodeService.getProperty(objectType, ContentModel.PROP_NAME).toString());
        }
        return record;
    }

    public void log(Date date, String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
        NodeRef person = null;
        if (initiator != null && personService.personExists(initiator)) {
            person = personService.getPerson(initiator, false);
        }
        log(date, person, mainObject, eventCategory, defaultDescription, objects);
    }

    public void log(NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
        log(new Date(), initiator, mainObject, eventCategory, defaultDescription, objects);
    }

    public void log(String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
        NodeRef person = null;
        if (initiator != null && personService.personExists(initiator)) {
            person = personService.getPerson(initiator, false);
        }
        log(new Date(), person, mainObject, eventCategory, defaultDescription, objects);
    }

    public void log(NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
        log(new Date(), mainObject, eventCategory, defaultDescription, objects);
    }

    public void log(NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects, boolean ignoreNext) {
        IgnoredCounter counter = new IgnoredCounter(1);
        threadSettings.set(counter);
        log(mainObject, eventCategory, defaultDescription, objects);
    }

    public void log(Date date, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
        String initiator = AuthenticationUtil.getFullyAuthenticatedUser();
        if (StringUtils.isEmpty(initiator)) {
            initiator = AuthenticationUtil.getSystemUserName();
        }
        log(date, initiator, mainObject, eventCategory, defaultDescription, objects);
    }

    public void log(NodeRef mainObject, String eventCategory, String defaultDescription) {
        log(mainObject, eventCategory, defaultDescription, null);
    }

    /**
     * Метод, возвращающий ссылку на объект справочника "Тип объекта" для заданного объекта
     *
     * @param nodeRef - ссылка на объект
     * @return ссылка на объект справочника или NULL
     */
    public NodeRef getObjectType(NodeRef nodeRef) {
        // получаем тип объекта
        QName type = nodeService.getType(nodeRef);
        String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());
        // получаем Тип Объекта
        return dictionaryService.getRecordByParamValue("Тип объекта", BusinessJournalService.PROP_OBJ_TYPE_CLASS, shortTypeName);
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected static enum WhoseEnum {
        MY,
        DEPARTMENT,
		ORGANIZATION,
        CONTROL,
        ALL
    }

    class IgnoredCounter {

        private int executed = 0;
        private int ignored = 0;

        IgnoredCounter(int ignored) {
            this.ignored = ignored;
        }

        void execute() {
            executed++;
            if (executed > ignored) {
                executed = 0;
                ignored = 0;
            }
        }

        boolean isIgnored() {
            return ignored != 0 && executed >= ignored;
        }

    }
}
