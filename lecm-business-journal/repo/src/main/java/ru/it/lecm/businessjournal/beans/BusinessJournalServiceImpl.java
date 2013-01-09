package ru.it.lecm.businessjournal.beans;

import java.io.Serializable;
import java.util.*;

import org.alfresco.model.ContentModel;
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
	public NodeRef fire(Date date, String initiator, NodeRef mainObject, NodeRef eventCategory, String description, List<NodeRef> objects) throws Exception{
		PersonService personService = serviceRegistry.getPersonService();
		NodeRef person = null;
		if (personService.personExists(initiator)) {
			person = personService.getPerson(initiator, false);
		}
		return fire(date, person, mainObject, eventCategory, description, objects);
	}
	/**
	 * Метод для создания записи бизнеса-журнала
	 * @param date - дата создания записи
	 * @param initiator  - инициатор события
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @param  eventCategory  - категория события
	 * @param  description  - описание события
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	@Override
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String description, List<NodeRef> objects) throws Exception{
		if (initiator == null || mainObject == null) {
			new Exception("Инициатор события и основной объект должны быть заданы!");
		}
		// заполняем карту плейсхолдеров
		Map<String, String> holdersMap = fillHolders(initiator, mainObject, objects);
		//получаем тип объекта
		NodeRef objectType = getObjectType(mainObject);
		// получаем шаблон описания
		String templateString = getTemplateString(objectType, eventCategory, description);
		// заполняем шаблон данными
		String filled = fillTemplateString(templateString, holdersMap);
		// создаем записи
		return createRecord(date, initiator, mainObject, objectType, eventCategory, objects, filled);
	}

    /**
     * Метод для создания записи бизнеса-журнала
     * @param date - дата создания записи
     * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
     * @param mainObject - основной объект
     * @param objects    - массив дополнительных объектов
     * @param  eventCategory  - категория события
     * @param  description  - описание события
     * @return ссылка на ноду записи в бизнес журнале
     */
	@Override
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String description, NodeRef[] objects) throws Exception{
		return fire(date, initiator, mainObject, eventCategory, description, Arrays.asList(objects));
	}

    /**
     * Метод для создания записи бизнеса-журнала
     * @param date - дата создания записи
     * @param initiator  - инициатор события (логин пользователя)
     * @param mainObject - имя основного объекта
     * @param objects    - список дополнительных объектов
     * @param  eventCategory  - название категории события
     * @param  description  - описание события
     * @return ссылка на ноду записи в бизнес журнале
     */
    @Override
    public NodeRef fire(Date date, String initiator, String mainObject, String eventCategory, String description, List<NodeRef> objects) throws Exception {
        return fire(date, initiator, getObjectTypeByName(mainObject), getEventCategoryByName(eventCategory), description, objects);
    }

    private NodeRef getEventCategoryByName(String eventCategory) {
        return getDicElementByName(eventCategory, DICTIONARY_EVENT_CATEGORY);
    }

    private NodeRef getObjectTypeByName(String objectName) {
        return getDicElementByName(objectName, DICTIONARY_OBJECT_TYPE);
    }

    private NodeRef getDicElementByName(String elName, String dictionaryName) {
        final NodeRef companyHome = repositoryHelper.getCompanyHome();
        NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DICTIONARIES_ROOT_NAME);
        NodeRef dictionary = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, dictionaryName);
        if (dictionary != null) {
            return nodeService.getChildByName(dictionary, ContentModel.ASSOC_CONTAINS, elName);
        }
        return null;
    }
    
    private NodeRef createRecord(final Date date, final NodeRef initiator, final NodeRef mainObject, final NodeRef objectType, final NodeRef eventCategory, final List<NodeRef> objects, final String description) {
		return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
			@Override
			public NodeRef execute() throws Throwable {
				final NodeRef saveDirectoryRef = getSaveFolder(date, objectType, eventCategory);
				// создаем ноду
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
				properties.put(PROP_BR_RECORD_DATE, date);
				properties.put(PROP_BR_RECORD_DESC, description);
				if (objects != null && objects.size() > 0) {
					for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
						NodeRef obj = objects.get(i);
						properties.put(QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i)), getObjectDescription(obj));
					}
				}
				ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
						QName.createQName(BJ_NAMESPACE_URI, GUID.generate()), TYPE_BR_RECORD, properties);

				NodeRef result = associationRef.getChildRef();
				// создаем ассоциации
				// обязательные
				nodeService.createAssociation(result, initiator, ASSOC_BR_RECORD_INITIATOR);
				nodeService.createAssociation(result, mainObject, ASSOC_BR_RECORD_MAIN_OBJ);

				// необязательные
				if (eventCategory != null) {
					nodeService.createAssociation(result, eventCategory, ASSOC_BR_RECORD_EVENT_CAT);
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
		String initiatorType = resolveInitiatorType(initiator);

		holders.put(BASE_USER_HOLDER, initiatorType.equals(SYSTEM) ? DEFAULT_SYSTEM_TEMPLATE : getObjectDescription(orgstructureService.getEmployeeByPerson(initiator)));
		holders.put(MAIN_OBJECT_HOLDER, getObjectDescription(mainObject));
		if (objects != null && objects.size() > 0) {
			for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
				holders.put(OBJECT_HOLDER + (i + 1), getObjectDescription(objects.get(i)));
			}
		}
		return holders;
	}

    /**
     * Метод возвращающий правильный тип для инициатора
     * Подразумеваем, что инициировать событие может только сотрудник или Система
     * @param initiator - инициатор события
     * @return тип инициатора (lecm-orgst:employee или System)
     */
	private String resolveInitiatorType(NodeRef initiator) {
		String initiatorType;
		if (!orgstructureService.isEmployee(initiator)) { // если не сотрудник - получаем сотрудника
			// инициатор - объект cm:person, Пытаемся получить сотрудника
			NodeRef employee = orgstructureService.getEmployeeByPerson(initiator);
			if (employee != null) {
				initiatorType = nodeService.getType(employee).toPrefixString(serviceRegistry.getNamespaceService());
			} else {
				// если не удалось получить сотрудника - считаем, что изменения сделала система
				initiatorType = SYSTEM;
			}
		} else {
			initiatorType = nodeService.getType(initiator).toPrefixString(serviceRegistry.getNamespaceService());
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
	 * @return шаблонную строку или null, если не удалось найти соответствие
	 */
	@Override
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
			// если не удалось - получаем из параметра  description
			if (defaultDescription != null && !defaultDescription.isEmpty()) {
				template = defaultDescription;
			} else { // если параметр description пустой - берем значение по умолчанию
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
			List<AssociationRef> objTypeSAssocs = nodeService.getSourceAssocs(objectType, ASSOC_MESSAGE_TEMP_OBJ_TYPE);
			List<NodeRef> types = new ArrayList<NodeRef>();
			for (AssociationRef objTypeSAssoc : objTypeSAssocs) {
				types.add(objTypeSAssoc.getSourceRef());
			}
			List<AssociationRef> evCategorySAssocs = nodeService.getSourceAssocs(eventCategory, ASSOC_MESSAGE_TEMP_EVENT_CAT);
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

	/**
	 * Метод, возвращающий корневую директорию
	 * @return ссылка
	 */
    @Override
	public NodeRef getBusinessJournalDirectory() {
		return bjRootRef;
	}

	@Override
	public JSONObject getRecordJSON(NodeRef recordRef) throws Exception{
		JSONObject record = new JSONObject();
		if (!isBJRecord(recordRef)) {
			throw new Exception("Объект [" + recordRef  +"] не является записью бизнес-журнала!");
		}
		final Map<QName, Serializable> props = nodeService.getProperties(recordRef);
		if (!props.isEmpty()) {
			try {
				record.put("bjRecord-date", DateFormatISO8601.format((Date) props.get(PROP_BR_RECORD_DATE)));
				record.put("bjRecord-description", props.get(PROP_BR_RECORD_DESC) != null ? props.get(PROP_BR_RECORD_DESC) : "");
				for (int i = 0; i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
					QName key = QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i));
					if (props.get(key) != null) {
						record.put(getSecondObjPropName(i), props.get(key));
					}
				}
				record.put("bjRecord-initiator-assoc", findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_INITIATOR, null, ASSOCIATION_TYPE.TARGET));
				record.put("bjRecord-mainObject-assoc", findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_MAIN_OBJ, null, ASSOCIATION_TYPE.TARGET));

				NodeRef eventCategory = findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_EVENT_CAT, null, ASSOCIATION_TYPE.TARGET);
				if (eventCategory != null) {
					record.put("bjRecord-evCategory-assoc", eventCategory.toString());
				}
				NodeRef objectType = findNodeByAssociationRef(recordRef, ASSOC_BR_RECORD_OBJ_TYPE, null, ASSOCIATION_TYPE.TARGET);
				if (objectType != null) {
					record.put("bjRecord-objType-assoc", objectType.toString());
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
				records.add(currentNodeRef);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return records;
	}

	/**
	 * Метод, возвращающий ссылку на директорию в директории "Бизнес Журнал" согласно заданным параметрам
	 *
	 * @param date - текущая дата
	 * @param type - тип объекта
	 * @param  category - категория события
	 * @return ссылка на директорию
	 */
    private NodeRef getSaveFolder(final Date date, final NodeRef type, final NodeRef category) {
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        // имя директории "Корень/Тип Объекта/Категория события/ГГГГ-ММ-ДД"
                        NodeRef directoryRef;
                        synchronized (lock) {
                            NodeRef[] directoryPath = new NodeRef[2];
                            directoryPath[0] = type;
                            directoryPath[1] = category;

                            NodeRef saveDir = getBusinessJournalDirectory();
                            for (NodeRef path : directoryPath) {
                                String pathString = "unknown";
                                if (path != null) {
                                    pathString = (String) nodeService.getProperty(path, ContentModel.PROP_NAME);
                                }
                                NodeRef pathDir = nodeService.getChildByName(saveDir, ContentModel.ASSOC_CONTAINS, pathString);
                                if (pathDir == null) {
                                    QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                    QName assocQName = QName.createQName(BJ_NAMESPACE_URI, pathString);
                                    QName nodeTypeQName = ContentModel.TYPE_FOLDER;
                                    Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                    properties.put(ContentModel.PROP_NAME, pathString);
                                    ChildAssociationRef result = nodeService.createNode(saveDir, assocTypeQName, assocQName, nodeTypeQName, properties);
                                    saveDir = result.getChildRef();
                                } else {
                                    saveDir = pathDir;
                                }
                            }
                            String saveFolderName = FolderNameFormat.format(date); // получаем строкое представление даты
                            directoryRef = nodeService.getChildByName(saveDir, ContentModel.ASSOC_CONTAINS, saveFolderName);
                            if (directoryRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(BJ_NAMESPACE_URI, saveFolderName);
                                QName nodeTypeQName = ContentModel.TYPE_FOLDER;
                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                properties.put(ContentModel.PROP_NAME, saveFolderName);
                                ChildAssociationRef result = nodeService.createNode(saveDir, assocTypeQName, assocQName, nodeTypeQName, properties);
                                directoryRef = result.getChildRef();
                            }
                        }
                        return directoryRef;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
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
				objType = row.getNodeRef();
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
}
