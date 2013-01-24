package ru.it.lecm.businessjournal.beans;

import java.io.Serializable;
import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 10:18
 */
public class BusinessJournalServiceImpl extends BaseBean implements  BusinessJournalService{

	public static final String LINK_URL = "/share/page/edit-metadata";

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private SearchService searchService;
	private OrgstructureBean orgstructureService;
	private NodeRef bjRootRef;
	private SubstitudeBean substituteService;

	final private static Logger logger = LoggerFactory.getLogger(BusinessJournalServiceImpl.class);

	private final Object lock = new Object();

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 * Записыывает в свойства сервиса nodeRef директории с бизнес-журналами
	 */
	public void init() {
		final String rootName = BJ_ROOT_NAME;
		repositoryHelper.init();
		nodeService = serviceRegistry.getNodeService();
		searchService = serviceRegistry.getSearchService();
		transactionService = serviceRegistry.getTransactionService();
		searchService = serviceRegistry.getSearchService();

		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef bjRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, rootName);
						if (bjRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(BJ_NAMESPACE_URI, BR_ASSOC_QNAME);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, rootName);
							ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
							bjRef = associationRef.getChildRef();
						}
						return bjRef;
					}
				});
			}
		};
		bjRootRef = AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public NodeRef fire(Date date, String initiator, NodeRef mainObject, NodeRef eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception {
		PersonService personService = serviceRegistry.getPersonService();
		NodeRef person = null;
		if (personService.personExists(initiator)) {
			person = personService.getPerson(initiator, false);
		}
		String evcategoryString = null;
		if (eventCategory != null) {
			evcategoryString = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
		}
		return fire(date, person, mainObject, evcategoryString, defaultDescription, objects);
	}
	/**
	 * Метод для создания записи бизнеса-журнала
	 *
	 * @param date - дата создания записи
	 * @param initiator  - инициатор события (cm:person)
	 * @param mainObject - основной объект
	 * @param eventCategory  - категория события
	 * @param defaultDescription  - описание события
	 * @param objects    - список дополнительных объектов
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	@Override
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception{
		if (initiator == null || mainObject == null) {
			new Exception("Инициатор события и основной объект должны быть заданы!");
		}
		// заполняем карту плейсхолдеров
		Map<String, String> holdersMap = fillHolders(initiator, mainObject, objects);
		// получаем шаблон описания
		String templateString = getTemplateString(getObjectType(mainObject), getEventCategoryByName(eventCategory), defaultDescription);
		// заполняем шаблон данными
		String filled = fillTemplateString(templateString, holdersMap);
		// получаем текущего пользователя по логину
		NodeRef employee = orgstructureService.getEmployeeByPerson(initiator);
		// создаем записи
		return createRecord(date, employee, mainObject, eventCategory, objects, filled);
	}

    /**
     * Метод для создания записи бизнеса-журнала
     * @param date - дата создания записи
     * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
     * @param mainObject - основной объект
     * @param objects    - массив дополнительных объектов
     * @param  eventCategory  - категория события
     * @param  defaultDescription  - описание события
     * @return ссылка на ноду записи в бизнес журнале
     */
	@Override
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String defaultDescription, NodeRef[] objects) throws Exception{
		String evCategoryString = null;
		if (eventCategory != null) {
			evCategoryString = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
		}
		return fire(date, initiator, mainObject, evCategoryString, defaultDescription, Arrays.asList(objects));
	}

    /**
     * Метод для создания записи бизнеса-журнала
     * @param date - дата создания записи
     * @param initiator  - инициатор события (логин пользователя)
     * @param mainObject - имя основного объекта
     * @param objects    - список дополнительных объектов
     * @param  eventCategory  - название категории события
     * @param  defaultDescription  - описание события
     * @return ссылка на ноду записи в бизнес журнале
     */
    @Override
    public NodeRef fire(Date date, String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception {
        return fire(date, initiator, mainObject, getEventCategoryByName(eventCategory), defaultDescription, objects);
    }

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	@Override
	public NodeRef fire(NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception {
		String evCategoryString = null;
		if (eventCategory != null) {
			evCategoryString = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
		}
		return fire(new Date(), initiator, mainObject, evCategoryString, defaultDescription, objects);
	}

	@Override
	public NodeRef fire(NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception {
		return fire(initiator, mainObject,getEventCategoryByName(eventCategory), defaultDescription, objects);
	}

	@Override
	public NodeRef fire(String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception {
		PersonService personService = serviceRegistry.getPersonService();
		NodeRef person = null;
		if (personService.personExists(initiator)) {
			person = personService.getPerson(initiator, false);
		}
		return fire(new Date(), person, mainObject, eventCategory, defaultDescription, objects);
	}

	/**
	 * Получение ссылки на Категорию События по имени категории
	 * @param  eventCategory  - название категории события
	 * @return ссылка на ноду
	 */
    private NodeRef getEventCategoryByName(String eventCategory) {
        return getDicElementByName(eventCategory, DICTIONARY_EVENT_CATEGORY);
    }

	/**
	 * Получение ссылки на Тип Объекта по имени типа
	 * @param  objectType  - название типа объекта
	 * @return ссылка на ноду
	 */
    private NodeRef getObjectTypeByName(String objectType) {
        return getDicElementByName(objectType, DICTIONARY_OBJECT_TYPE);
    }

    private NodeRef getDicElementByName(String elName, String dictionaryName) {
	    if (elName != null && !elName.isEmpty()) {
		    final NodeRef companyHome = repositoryHelper.getCompanyHome();
		    NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DICTIONARIES_ROOT_NAME);
		    NodeRef dictionary = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, dictionaryName);
		    if (dictionary != null) {
			    return nodeService.getChildByName(dictionary, ContentModel.ASSOC_CONTAINS, elName);
		    }
	    }
        return null;
    }
    
    private NodeRef createRecord(final Date date, final NodeRef initiator, final NodeRef mainObject, final String eventCategory, final List<NodeRef> objects, final String description) {
		return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
			@Override
			public NodeRef execute() throws Throwable {

				final NodeRef objectType = getObjectType(mainObject);
				String type;
				if (objectType != null) {
					type = (String) nodeService.getProperty(objectType, ContentModel.PROP_NAME);
				} else {
					type = nodeService.getType(mainObject).getPrefixString().replace(":", "_");
				}
				String category;
				if (eventCategory != null && !eventCategory.isEmpty()) {
					category = eventCategory;
				} else {
					category = "unknown";
				}
				final NodeRef saveDirectoryRef = getSaveFolder(type, category, date);

				// создаем ноду
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
				properties.put(PROP_BR_RECORD_DATE, date);
				properties.put(PROP_BR_RECORD_DESC, description);
				if (initiator != null) {
					properties.put(PROP_BR_RECORD_INITIATOR, getObjectDescription(initiator));
				} else {
					properties.put(PROP_BR_RECORD_INITIATOR, DEFAULT_SYSTEM_TEMPLATE);
				}
				properties.put(PROP_BR_RECORD_MAIN_OBJECT, getObjectDescription(mainObject));
				if (objects != null && objects.size() > 0) {
					for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
						NodeRef obj = objects.get(i);
						properties.put(QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i)), wrapAsLink(obj, false));
					}
				}

				ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
						QName.createQName(BJ_NAMESPACE_URI, GUID.generate()), TYPE_BR_RECORD, properties);
				NodeRef result = associationRef.getChildRef();

				// создаем ассоциации
				if (initiator != null) {
					nodeService.createAssociation(result, initiator, ASSOC_BR_RECORD_INITIATOR);
				}
				nodeService.createAssociation(result, mainObject, ASSOC_BR_RECORD_MAIN_OBJ);

				// необязательные
				if (eventCategory != null) {
					NodeRef evCategoryRef = getEventCategoryByName(eventCategory);
					if (evCategoryRef != null) {
						nodeService.createAssociation(result, evCategoryRef, ASSOC_BR_RECORD_EVENT_CAT);
					}
				}
				if (objectType != null) {
					nodeService.createAssociation(result, objectType, ASSOC_BR_RECORD_OBJ_TYPE);
				}

				if (objects != null && objects.size() > 0) {
					for (int j = 0; j < objects.size() && j < MAX_SECONDARY_OBJECTS_COUNT; j++) {
						nodeService.createAssociation(result, objects.get(j), QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(j)));
					}
				}
				return result;
			}
		});
	}

	/**
	 * Метод заполняет карту плейсхолдеров значениями на основании типов объектов
	 *
	 * @param initiator  - инициатор события (cm:person)
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @return заполненная карта
	 */
	private Map<String, String> fillHolders(NodeRef initiator, NodeRef mainObject, List<NodeRef> objects) {
		Map<String, String> holders = new HashMap<String, String>();
		holders.put(BASE_USER_HOLDER, wrapAsLink(initiator, true));
		holders.put(MAIN_OBJECT_HOLDER, wrapAsLink(mainObject, false));
		if (objects != null && objects.size() > 0) {
			for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
				holders.put(OBJECT_HOLDER + (i + 1), wrapAsLink(objects.get(i), false));
			}
		}
		return holders;
	}

	private String wrapAsLink(NodeRef link, boolean isInititator) {
		SysAdminParams params = serviceRegistry.getSysAdminParams();
		String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":"  +  params.getSharePort();
		return "<a href=\"" + serverUrl + LINK_URL + "?nodeRef=" + link.toString() + "\" target=\"_blank\">"
				+ (isInititator ? getInitiatorDescription(link) :  getObjectDescription(link)) + "</a>";
	}

	/**
     * Метод возвращающий правильный тип для инициатора
     * Подразумеваем, что инициировать событие может только сотрудник или Система
     * @param initiator - инициатор события (cm:person)
     * @return тип инициатора (lecm-orgst:employee или System)
     */
    private String resolveInitiatorType(NodeRef initiator) {
	    String initiatorType;
	    // инициатор - объект cm:person, Пытаемся получить сотрудника
	    NodeRef employee = orgstructureService.getEmployeeByPerson(initiator);
	    if (employee != null) {
		    initiatorType = nodeService.getType(employee).toPrefixString(serviceRegistry.getNamespaceService());
	    } else {
		    // если не удалось получить сотрудника - считаем, что изменения сделала система
		    initiatorType = SYSTEM;
	    }
	    return initiatorType;
    }

	/**
	 * Метод формирующий описание заданного объекта по шаблону, определяемому по типу объекта
	 * @param object - текущий объект
	 * @return сформированное описание
	 */
	@Override
	public String getObjectDescription(NodeRef object) {
		// получаю ссылку на справочник "Тип объекта" по типу object
		NodeRef objectType = getObjectType(object);
		// получаем шаблон описания
		String templateString = getTemplateStringByType(objectType);
		// формируем описание
		return substinateDescription(object, templateString);
	}

	private String substinateDescription(NodeRef object, String templateString) {
		return substituteService.formatNodeTitle(object, templateString);
	}

	private String getInitiatorDescription(NodeRef initiator) {
		String initiatorType = resolveInitiatorType(initiator);
		return initiatorType.equals(SYSTEM) ? DEFAULT_SYSTEM_TEMPLATE : getObjectDescription(orgstructureService.getEmployeeByPerson(initiator));
	}

	/**
	 * Метод возвращающий шаблон описание по типу объхекта
	 * @param objectType - ссылка на тип объекта
	 * @return сформированное описание или null, если для типа не задан шаблон
	 */
	private String getTemplateStringByType(NodeRef objectType) {
		return getTemplateByType(objectType);
	}

	/**
	 * Метод формирующий описание заданного объекта
	 * @param object - текущий объект
	 * @param  type - Тип по которому берется описание
	 * @return сформированное описание или null, если для типа не задан шаблон
	 */
	private String getObjectDescription(NodeRef object, String type) {
		NodeRef objectType = getObjectTypeByClass(type);
		// получаем шаблон описания
		String templateString = getTemplateStringByType(objectType);
		// формируем описание
		return substinateDescription(object, templateString);
	}
	/**
	 * Метод для получения шаблонной строки для заданного типа
	 * @param type - ссылка на объект справочника "Тип Объекта"
	 * @return шаблонную строку или DEFAULT_OBJECT_TYPE_TEMPLATE, если не удалось найти соответствие
	 */
	public String getTemplateByType(NodeRef type) {
		if (type != null) {
			Object template = nodeService.getProperty(type, PROP_OBJ_TYPE_TEMPLATE);
			return template != null ? (String) template : DEFAULT_OBJECT_TYPE_TEMPLATE;
		} else {
			return DEFAULT_OBJECT_TYPE_TEMPLATE;
		}
	}

	/**
	 * Получить стрковый шаблон Сообщения по типу объекта и категории события
	 * @param objectType - тип объекта
	 * @param eventCategory - категория события
	 * @param defaultDescription - описание по умолчанию
	 * @return шаблон сообщения
	 */
	private String getTemplateString(NodeRef objectType, NodeRef eventCategory, String defaultDescription) {
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
				template = DEFAULT_MESSAGE_TEMPLATE;
			}
		}
		return template != null ? template : DEFAULT_MESSAGE_TEMPLATE;
	}

	/**
	 * Метод для получения шаблонной строки для заданного Шаблона Сообщения
	 * @param messageTemplate - ссылка на объект Шаблон Сообщения
	 * @return шаблонную строку или null, если не удалось найти соответствие
	 */
	private String getMessageTemplateByTemplate(NodeRef messageTemplate) {
		Object message = nodeService.getProperty(messageTemplate, PROP_MESSAGE_TEMP_TEMPLATE);
		return message != null ? (String) message : null;
	}

	/**
	 * Метод для получения объекта справочника "Шаблон сообщения" по типу объекта и категории события
	 * @param objectType - тип объекта
	 * @param eventCategory - категория события
	 * @return ссылка на объект "Шаблон сообщения" или NULL - если заданным параметрам не соответствует шаблон в справочнике
	 */
	private NodeRef getMessageTemplate(NodeRef objectType, NodeRef eventCategory) {
		if (objectType != null && eventCategory != null) {
			NodeRef record = null;
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_LUCENE);
			sp.setQuery("TYPE:\"" + TYPE_MESSAGE_TEMPLATE.toString() +
					"\" AND @lecm\\-busjournal\\:messageTemplate\\-objType\\-assoc\\-ref:\"" + objectType.toString() +
					"\" AND @lecm\\-busjournal\\:messageTemplate\\-evCategory\\-assoc\\-ref:\"" + eventCategory.toString() + "\"");

			ResultSet results = null;
			try {
				results = serviceRegistry.getSearchService().query(sp);
				for (ResultSetRow row : results) {
					if (!isArchive(row.getNodeRef())) {
						record = row.getNodeRef();
					}
				}
			} finally {
				if (results != null) {
					results.close();
				}
			}
			return record;
		}
		return null;
	}

	/**
	 * Метод, возвращающий "заполненный" строку - шаблон с замещенными holders
	 * @param  template - шаблонная строка
	 * @param  holders - список заместителей
	 * @return сформированная строка
	 */
	private String fillTemplateString(String template, Map<String, String> holders) {
		for (String key : holders.keySet()) {
			template = template.replaceAll(key, holders.get(key));
		}
		return template;
	}

    @Override
	public NodeRef getBusinessJournalDirectory() {
		return bjRootRef;
	}

	@Override
	public NodeRef getBusinessJournalArchiveDirectory() {
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef bjRef = getBusinessJournalDirectory();
						NodeRef archiveRef = nodeService.getChildByName(bjRef, ContentModel.ASSOC_CONTAINS, BJ_ARCHIVE_ROOT_NAME);
						if (archiveRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(BJ_NAMESPACE_URI, BR_ASSOC_QNAME);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, BJ_ARCHIVE_ROOT_NAME);
							ChildAssociationRef associationRef = nodeService.createNode(bjRef, assocTypeQName, assocQName, nodeTypeQName, properties);
							archiveRef = associationRef.getChildRef();
						}
						return archiveRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	public JSONObject getRecordJSON(NodeRef recordRef) throws Exception{
		JSONObject record = new JSONObject();
		if (!isBJRecord(recordRef)) {
			throw new Exception("Объект [" + recordRef  +"] не является записью бизнес-журнала!");
		}
		final Map<QName, Serializable> props = nodeService.getProperties(recordRef);
		if (!props.isEmpty()) {
			try {
				record.put(PROP_BR_RECORD_DATE.getLocalName(), DateFormatISO8601.format((Date) props.get(PROP_BR_RECORD_DATE)));
				record.put(PROP_BR_RECORD_DESC.getLocalName(), props.get(PROP_BR_RECORD_DESC) != null ? props.get(PROP_BR_RECORD_DESC) : "");
				for (int i = 0; i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
					QName key = QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i));
					if (props.get(key) != null) {
						record.put(key.getLocalName(), props.get(key));
					}
				}

				record.put(PROP_BR_RECORD_INITIATOR.getLocalName(), props.get(PROP_BR_RECORD_INITIATOR));
				record.put(PROP_BR_RECORD_MAIN_OBJECT.getLocalName(), props.get(PROP_BR_RECORD_MAIN_OBJECT));

				record.put(ASSOC_BR_RECORD_INITIATOR.getLocalName(), findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_INITIATOR, null, ASSOCIATION_TYPE.TARGET));
				record.put(ASSOC_BR_RECORD_MAIN_OBJ.getLocalName(), findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_MAIN_OBJ, null, ASSOCIATION_TYPE.TARGET));

				NodeRef eventCategory = findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_EVENT_CAT, null, ASSOCIATION_TYPE.TARGET);
				if (eventCategory != null) {
					record.put(ASSOC_BR_RECORD_EVENT_CAT.getLocalName(), eventCategory.toString());
				}
				NodeRef objectType = findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_OBJ_TYPE, null, ASSOCIATION_TYPE.TARGET);
				if (objectType != null) {
					record.put(ASSOC_BR_RECORD_OBJ_TYPE.getLocalName(), objectType.toString());
				}

				for (int j = 0; j < MAX_SECONDARY_OBJECTS_COUNT; j++) {
					String name = getSeconObjAssocName(j);
					NodeRef objRef = findNodeByAssociationRef(recordRef, QName.createQName(BJ_NAMESPACE_URI, name), null, ASSOCIATION_TYPE.TARGET);
					if (objRef != null) {
						record.put(name, objRef.toString());
					}
				}
			} catch (JSONException e) {
				logger.error("", e);
			}

		}
		return record;
	}

	private String getSeconObjAssocName(int j) {
		return "bjRecord-secondaryObj" + (j + 1) + "-assoc";
	}

	private String getSecondObjPropName(int i) {
		return "bjRecord-secondaryObj" + (i + 1);
	}

	/**
	 * Метод, возвращающий список ссылок на записи бизнес-журнала, сформированные за заданный период
	 *
	 * @param begin - начальная дата
	 * @param end   - конечная дата
	 * @return список ссылок
	 */
	@Override
	public List<NodeRef> getRecordsByInterval(Date begin, Date end) {
		List<NodeRef> records = new ArrayList<NodeRef>(10);
		final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
		final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery("TYPE:\"" + TYPE_BR_RECORD.toString()  +"\" AND @lecm\\-busjournal\\:bjRecord\\-date:[" + MIN + " TO " + MAX + "]");
		ResultSet results = null;
		try {
			results = serviceRegistry.getSearchService().query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				if (!isArchive(currentNodeRef)){
					records.add(currentNodeRef);
				}
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return records;
	}

	private NodeRef getSaveFolder(final String type, final String category, final Date date) {
		return getFolder(getBusinessJournalDirectory(), type, category, date);
	}

	private NodeRef getArchiveFolder(final Date date) {
		return getFolder(getBusinessJournalArchiveDirectory(), null, null, date);
	}

	/**
	 * Метод, возвращающий ссылку на директорию в директории "Бизнес Журнал" согласно заданным параметрам
	 *
	 *
	 * @param date - текущая дата
	 * @param type - тип объекта
	 * @param  category - категория события
	 * @param root - корень, относительно которого строится путь
	 * @return ссылка на директорию
	 */
	private NodeRef getFolder(final NodeRef root, final String type, final String category, final Date date) {
		List<String> directoryPaths = new ArrayList<String>(3);
		if (type != null) {
			directoryPaths.add(type);
		}
		if (category != null) {
			directoryPaths.add(category);
		}
		directoryPaths.addAll(getDateFolderPath(date));
		return getFolder(transactionService, BJ_NAMESPACE_URI, root, directoryPaths);
	}
	/**
	 * Метод, возвращающий ссылку на объект справочника "Тип объекта" для заданного объекта
	 *
	 * @param nodeRef - ссылка на объект
	 * @return ссылка на объект справочника или NULL
	 */
	private NodeRef getObjectType(NodeRef nodeRef) {
		// получаем тип объекта
		QName type = nodeService.getType(nodeRef);
		String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());
		// получаем Тип Объекта
		return getObjectTypeByClass(shortTypeName);
	}

	/**
	 * Метод, возвращающий ссылку на объект справочника "Тип объекта"по заданному классу(типу)
	 *
	 * @param type - тип(класс) объекта
	 * @return ссылка на объект справочника или NULL
	 */
	private NodeRef getObjectTypeByClass(String type) {
		// получаем объект "Тип объекта"
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery("TYPE:\"" + TYPE_OBJECT_TYPE.toString() + "\" AND @lecm\\-busjournal\\:objectType\\-class:\"" + type + "\"");
		ResultSet results = null;
		NodeRef objType = null;
		try {
			results = serviceRegistry.getSearchService().query(sp);
			for (ResultSetRow row : results) {
				if (!isArchive(row.getNodeRef())) {
					objType = row.getNodeRef();
				}
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return objType;
	}

	@Override
	public boolean isBJRecord(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_BR_RECORD);
		return isProperType(ref, types);
	}

	public void setSubstituteService(SubstitudeBean substituteService) {
		this.substituteService = substituteService;
	}

    @Override
    public List<NodeRef> getRecordsByParams(String objectType, Date begin, Date end, String whoseKey) {
        List<NodeRef> records = new ArrayList<NodeRef>(10);
        final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
        final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";
        ResultSet results = null;
        String query = "";
        SearchParameters sp = new SearchParameters();

        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        query = "TYPE:\"" + TYPE_BR_RECORD.toString()  +"\" AND @lecm\\-busjournal\\:bjRecord\\-date:[" + MIN + " TO " + MAX + "]";

        if (objectType != null && !"".equals(objectType)) {
            query += " AND @lecm\\-busjournal\\:bjRecord\\-objType\\-assoc\\-ref:\"" + objectType + "\"";
        }

        if (whoseKey != null && !"".equals(whoseKey)) {
            switch(WhoseEnum.valueOf(whoseKey.toUpperCase())) {
                case MY : {
                    NodeRef employee = orgstructureService.getCurrentEmployee();

                    if (employee != null) {
                        query += " AND @lecm\\-busjournal\\:bjRecord\\-initiator\\-assoc\\-ref:\"" + employee.toString() + "\"";
                    }
                    break;
                }
                case DEPARTMENT: {
                    NodeRef boss = orgstructureService.getCurrentEmployee();

                    if (boss != null) {
                        String employeesList = "";
                        List<NodeRef> employees = orgstructureService.getBossSubordinate(boss);

                        employees.add(boss);
                        for (NodeRef employee : employees) {
                            if (employee != null) {
                                employeesList += ("".equals(employeesList) ? "(" : " ") + "\"" + employee.toString() + "\"";
                            }
                        }
                        employeesList += ")";
                        query += " AND @lecm\\-busjournal\\:bjRecord\\-initiator\\-assoc\\-ref:" + employeesList + "";
                    }
                    break;
                }
                case CONTROL: {
                    //todo
                    break;
                }
                default: {

                }
            }
        }
        sp.addSort("@" + PROP_BR_RECORD_DATE.toString(), false);
        sp.setQuery(query);
        try {
            results = serviceRegistry.getSearchService().query(sp);
            for (ResultSetRow row : results) {
                NodeRef currentNodeRef = row.getNodeRef();
	            if (!isArchive(currentNodeRef)){
		            records.add(currentNodeRef);
	            }
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return records;
    }

	public boolean moveRecordToArchive(final NodeRef record) {
		AuthenticationUtil.RunAsWork<Boolean> raw = new AuthenticationUtil.RunAsWork<Boolean>() {
			@Override
			public Boolean doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
					@Override
					public Boolean execute() throws Throwable {
						NodeRef archiveRef = getArchiveFolder(new Date());
						nodeService.setProperty(record, IS_ACTIVE, false); // помечаем как неактивная запись
						ChildAssociationRef newRef = nodeService.moveNode(record, archiveRef, ContentModel.ASSOC_CONTAINS, nodeService.getPrimaryParent(record).getQName());
						return newRef != null;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public List<NodeRef> getOldRecords(int numberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -numberOfDays);
		return getRecordsByInterval(null, calendar.getTime());
	}

    @Override
    public List<NodeRef> getHistory(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending) {
        List<NodeRef> result = getHistory(nodeRef);

        final QName sortFieldQName = sortColumnLocalName != null && sortColumnLocalName.length() > 0 ? QName.createQName(BJ_NAMESPACE_URI, sortColumnLocalName) : PROP_BR_RECORD_DATE;
        if (sortFieldQName == null) {
            return result;
        }

        class NodeRefComparator<T extends Serializable & Comparable<T>> implements Comparator<NodeRef> {
            @Override
            public int compare(NodeRef nodeRef1, NodeRef nodeRef2) {
                T obj1 = (T) nodeService.getProperty(nodeRef1, sortFieldQName);
                T obj2 = (T) nodeService.getProperty(nodeRef2, sortFieldQName);

                return sortAscending ? obj1.compareTo(obj2) : obj2.compareTo(obj1);
            }
        }

        if (sortFieldQName.getLocalName().equals(PROP_BR_RECORD_DATE.getLocalName())) {
            Collections.sort(result, new NodeRefComparator<Date>());
        }

        if (sortFieldQName.getLocalName().equals(PROP_BR_RECORD_DESC.getLocalName())) {
            Collections.sort(result, new NodeRefComparator<String>());
        }

        return result;
    }

    @Override
    public List<NodeRef> getHistory(NodeRef nodeRef) {
        if (nodeRef == null) {
            return new ArrayList<NodeRef>();
        }

        List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(nodeRef, ASSOC_BR_RECORD_MAIN_OBJ);

        List<NodeRef> result = new ArrayList<NodeRef>();
        for (AssociationRef sourceAssoc : sourceAssocs) {
            NodeRef bjRecordRef = sourceAssoc.getSourceRef();

            if (!isArchive(bjRecordRef)) {
                result.add(bjRecordRef);
            }
        }

        return result;
    }

    private static enum WhoseEnum {
        MY,
        DEPARTMENT,
        CONTROL,
        ALL
    }
}
