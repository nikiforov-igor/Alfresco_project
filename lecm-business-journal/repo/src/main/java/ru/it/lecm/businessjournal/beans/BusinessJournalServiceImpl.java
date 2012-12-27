package ru.it.lecm.businessjournal.beans;

import java.io.Serializable;
import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 10:18
 */
public class BusinessJournalServiceImpl implements  BusinessJournalService{
	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private NodeService nodeService;
	private SearchService searchService;
	private NodeRef bjRootRef;

	final private static Logger logger = LoggerFactory.getLogger(BusinessJournalServiceImpl.class);

	private final Object lock = new Object();

	private static enum ASSOCIATION_TYPE {
		SOURCE,
		TARGET
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
							QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, BR_ASSOC_QNAME);
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
		// инициализируем сервисы
		bjRootRef = AuthenticationUtil.runAsSystem(raw);
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
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String description, List<NodeRef> objects) {
		// заполняем карту плейсхолдеров

		Map<String, String> holdersMap = fillHolders(initiator, mainObject, objects);
		//получаем тип объекта
		NodeRef objectType = getObjectTypeByRef(mainObject);
		// получаем шаблон описания
		String templateString = getTemplateString(objectType, eventCategory, description);
		// заполняем шаблон данными
		String filled = formatTemplate(templateString, holdersMap);
		// создаем записи
		return createRecord(date, initiator, mainObject, objectType, filled, eventCategory, objects);
	}

	@Override
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String description, NodeRef[] objects) {
		return fire(date, initiator, mainObject, eventCategory, description, Arrays.asList(objects));
	}

	private NodeRef createRecord(final Date date, final NodeRef initiator, final NodeRef mainObject, final NodeRef objectType, final String filled, final NodeRef eventCategory, final List<NodeRef> objects) {
		return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
			@Override
			public NodeRef execute() throws Throwable {
				final NodeRef saveDirectoryRef = getDateFolder(date);
				// создаем ноду
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
				properties.put(PROP_BR_RECORD_DATE, date);
				properties.put(PROP_BR_RECORD_DESC, filled);
				if (objects != null && objects.size() > 0) {
					for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
						NodeRef obj = objects.get(i);
						properties.put(QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i)), obj);
					}
				}
				ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()), TYPE_BR_RECORD, properties);

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
	 * @param initiator  - инициатор события
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @return заполненная карта
	 */
	private Map<String, String> fillHolders(NodeRef initiator, NodeRef mainObject, List<NodeRef> objects) {
		Map<String, String> holders = new HashMap<String, String>();
		holders.put(BASE_USER_HOLDER, getObjectDescription(initiator));
		holders.put(MAIN_OBJECT_HOLDER, getObjectDescription(mainObject));
		if (objects != null && objects.size() > 0) {
			for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
				holders.put(OBJECT_HOLDER + (i + 1), getObjectDescription(objects.get(i)));
			}
		}
		return holders;
	}

	/**
	 * Метод формирующий описание заданного объекта на основании его типа
	 * @param object - текущий объект
	 * @return сформированное описание или null, если для типа не задан шаблон
	 */
	@Override
	public String getObjectDescription(NodeRef object) {
		NodeRef objectType = getObjectTypeByRef(object);
		String typeTemplateString = getTemplateByType(objectType);
		//TODO substinate string and return result
		return typeTemplateString;
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
			return template != null ? (String) template : null;
		} else {
			return "default"; // for type
		}

	}

	/**
	 * Получение стрковый шаблона Сообщения по типу объекта и категории события
	 * @param objectType - тип объекта
	 * @param eventCategory - категория события
	 * @param description - описание по умолчанию
	 * @return шаблон сообщения
	 */
	@Override
	public String getTemplateString(NodeRef objectType, NodeRef eventCategory, String description) {
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
			if (description != null && !description.isEmpty()) {
				template = description;
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
	@Override
	public String getMessageTemplateByTemplate(NodeRef messageTemplate) {
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
	public String formatTemplate(String template, Map<String, String> holders) {
		for (String key : holders.keySet()) {
			template = template.replaceAll(key, holders.get(key));
		}
		return template;
	}

	@Override
	/**
	 * Метод, возвращающий корневую директорию
	 * @return ссылка
	 */
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
					String key = getSecondObjPropName(i);
					if (props.get(key) != null) {
						record.put(key, props.get(key));
					}
				}
				record.put("bjRecord-initiator-assoc", findNodeAssociationRef(recordRef, ASSOC_BR_RECORD_INITIATOR, ContentModel.TYPE_PERSON, ASSOCIATION_TYPE.TARGET));
				record.put("bjRecord-mainObject-assoc", findNodeAssociationRef(recordRef, ASSOC_BR_RECORD_MAIN_OBJ, ContentModel.TYPE_CMOBJECT, ASSOCIATION_TYPE.TARGET));

				NodeRef eventCategory = findNodeAssociationRef(recordRef, ASSOC_BR_RECORD_EVENT_CAT, ContentModel.TYPE_PERSON, ASSOCIATION_TYPE.TARGET);
				if (eventCategory != null) {
					record.put("bjRecord-evCategory-assoc", eventCategory.toString());
				}
				NodeRef objectType = findNodeAssociationRef(recordRef, ASSOC_BR_RECORD_OBJ_TYPE, ContentModel.TYPE_PERSON, ASSOCIATION_TYPE.TARGET);
				if (objectType != null) {
					record.put("bjRecord-objType-assoc", objectType.toString());
				}

				for (int j = 0; j < MAX_SECONDARY_OBJECTS_COUNT; j++) {
					String name = getSeconObjAssocName(j);
					NodeRef objRef = findNodeAssociationRef(recordRef, QName.createQName(BJ_NAMESPACE_URI, name), ContentModel.TYPE_CMOBJECT, ASSOCIATION_TYPE.TARGET);
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
	 * получение связанной ноды по ассоциации. Для типа связи 1:1, 1:0, 0:1
	 *
	 * @param nodeRef       исходная нода
	 * @param assocTypeName имя типа ассоциации
	 * @param typeName      имя типа данных который завязан на ассоциацию
	 * @param type          направление ассоциации source или target
	 * @return найденный NodeRef или null
	 */
	private NodeRef findNodeAssociationRef(NodeRef nodeRef, QNamePattern assocTypeName, QNamePattern typeName, ASSOCIATION_TYPE type) {
		List<AssociationRef> associationRefs;

		switch (type) {
			case SOURCE:
				associationRefs = nodeService.getSourceAssocs(nodeRef, assocTypeName);
				break;
			case TARGET:
				associationRefs = nodeService.getTargetAssocs(nodeRef, assocTypeName);
				break;
			default:
				associationRefs = new ArrayList<AssociationRef>();
		}
		NodeRef foundNodeRef = null;
		for (AssociationRef associationRef : associationRefs) {
			NodeRef assocNodeRef;
			switch (type) {
				case SOURCE:
					assocNodeRef = associationRef.getSourceRef();
					break;
				case TARGET:
					assocNodeRef = associationRef.getTargetRef();
					break;
				default:
					assocNodeRef = null;
					break;
			}
			if (assocNodeRef != null) {
				QName foundType = nodeService.getType(assocNodeRef);
				if (typeName.isMatch(foundType)) {
					foundNodeRef = assocNodeRef;
				}
			}
		}
		return foundNodeRef;
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
		sp.setQuery("@lecm\\-busjournal\\:bjRecord\\-date:[" + MIN + " TO " + MAX + "]");
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
	 * Метод, возвращающий ссылку на директорию в директории "Бизнес Журнал" согласно заданному времени
	 *
	 * @param date - текущая дата
	 * @return ссылка на директорию
	 */
	private NodeRef getDateFolder(Date date) {
		final String saveFolderName = FolderNameFormat.format(date); // получаем строкое представление даты
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef directoryRef;
						synchronized (lock) {
							directoryRef = nodeService.getChildByName(bjRootRef, ContentModel.ASSOC_CONTAINS, saveFolderName);
							if (directoryRef == null) {
								directoryRef = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
									@Override
									public NodeRef execute() throws Throwable {
										// создаем Директорию
										QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
										QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, saveFolderName);
										QName nodeTypeQName = ContentModel.TYPE_FOLDER;
										Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
										properties.put(ContentModel.PROP_NAME, saveFolderName);
										ChildAssociationRef result = nodeService.createNode(bjRootRef, assocTypeQName, assocQName, nodeTypeQName, properties);
										return result.getChildRef();
									}
								});
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
	 * @return ссылка на объект справочника
	 */
	public NodeRef getObjectTypeByRef(NodeRef nodeRef) {
		// получаем тип объекта
		QName type = nodeService.getType(nodeRef);
		String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());
		// получаем объект "Тип объекта"
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery("TYPE:\"" + TYPE_OBJECT_TYPE.toString() + "\" AND @lecm\\-busjournal\\:objectType\\-class:\"" + shortTypeName + "\"");
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

	/**
	 * Метод, возвращающий ссылку на объект справочника "Тип объекта" для заданного объекта
	 *
	 * @param nodeRef - строкое представление ссылки на объект
	 * @return ссылка на объект справочника
	 */
	public NodeRef getObjectTypeByRef(String nodeRef) {
		return getObjectTypeByRef(new NodeRef(nodeRef));
	}

	/**
	 * проверяет что объект имеет подходящий тип
	 */
	private boolean isProperType(NodeRef ref, Set<QName> types) {
		if (ref != null) {
			QName type = nodeService.getType(ref);
			return types.contains(type);
		} else {
			return false;
		}
	}

	@Override
	public boolean isBJRecord(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_BR_RECORD);
		return isProperType(ref, types);
	}
}
