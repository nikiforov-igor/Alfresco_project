package ru.it.lecm.businessjournal.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 10:18
 */
public class BusinessJournalServiceImpl extends BaseBean implements  BusinessJournalService{

	private static final Logger logger = LoggerFactory.getLogger(BusinessJournalServiceImpl.class);

	private ServiceRegistry serviceRegistry;
	private SearchService searchService;
	private OrgstructureBean orgstructureService;
	private NodeRef bjRootRef;
	private NodeRef bjArchiveRef;
	private SubstitudeBean substituteService;
	private DictionaryBean dictionaryService;
	private PersonService personService;
	private AuthenticationService authService;
    private DictionaryService dicService;

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

    public void setDicService(DictionaryService dicService) {
        this.dicService = dicService;
    }

    private static enum WhoseEnum {
		MY,
		DEPARTMENT,
		CONTROL,
		ALL
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 * Записыывает в свойства сервиса nodeRef директории с бизнес-журналами
	 */
	public void init() {
		bjRootRef = getFolder(BJ_ROOT_ID);
		bjArchiveRef =  getFolder(BJ_ARCHIVE_ROOT_ID);
	}

	@Override
	public NodeRef log(Date date, NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
		if (mainObject == null) {
			logger.warn("Main Object not set!");
			return null;
		}
		NodeRef record = null;
		try {
			// получаем текущего пользователя по person
			NodeRef employee = initiator != null ? orgstructureService.getEmployeeByPerson(initiator) : null;

			// заполняем карту плейсхолдеров
			Map<String, String> holdersMap = fillHolders(employee, mainObject, objects);
			// пытаемся получить объект Категория события по ключу
			NodeRef category = getEventCategoryByCode(eventCategory);
			// получаем шаблон описания
			String templateString = getTemplateString(getObjectType(mainObject), category, defaultDescription);
			// заполняем шаблон данными
			String filled = fillTemplateString(templateString, holdersMap);

			// создаем записи
			record = createRecord(date, employee, mainObject, category, objects, filled);
		} catch (Exception ex) {
			logger.warn("Could not create business-journal record", ex);
		}
		return record;
	}

    @Override
    public NodeRef log(Date date, String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects){
	    NodeRef person = null;
	    if (personService.personExists(initiator)) {
		    person = personService.getPerson(initiator, false);
	    }
	    return log(date, person, mainObject, eventCategory, defaultDescription, objects);
    }

	@Override
	public NodeRef log(NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects){
		return log(new Date(), initiator, mainObject, eventCategory, defaultDescription, objects);
	}

	@Override
	public NodeRef log(String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects){
		NodeRef person = null;
		if (personService.personExists(initiator)) {
			person = personService.getPerson(initiator, false);
		}
		return log(new Date(), person, mainObject, eventCategory, defaultDescription, objects);
	}

	@Override
	public NodeRef log(NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects){
		return log(new Date(), mainObject, eventCategory, defaultDescription, objects);
	}

	@Override
	public NodeRef log(Date date, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) {
		String initiator = authService.getCurrentUserName();
		return log(date, initiator, mainObject, eventCategory, defaultDescription, objects);
	}

	@Override
	public NodeRef log(NodeRef mainObject, String eventCategory, String defaultDescription) {
		return log (mainObject, eventCategory, defaultDescription, null);
	}

	/**
	 * Получение ссылки на Категорию События по имени категории
	 * @param  eventCategory  - название категории события
	 * @return ссылка на ноду или null
	 */
	private NodeRef getEventCategoryByCode(String eventCategory) {
		NodeRef evCategory = null;
		if (eventCategory != null) {
			evCategory = dictionaryService.getRecordByParamValue("Категория события", PROP_EVENT_CAT_CODE, eventCategory);
		}
		return evCategory;
	}

	private NodeRef createRecord(final Date date, final NodeRef initiator, final NodeRef mainObject, final NodeRef eventCategory, final List<String> objects, final String description) {
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
				if (eventCategory != null) {
					category = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
				} else {
					category = "unknown";
				}

				final NodeRef saveDirectoryRef = getSaveFolder(type, category, date);

				// создаем ноду
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
				properties.put(PROP_BR_RECORD_DATE, date);
				properties.put(PROP_BR_RECORD_DESC, description);
				properties.put(PROP_BR_RECORD_INITIATOR, getInitiatorDescription(initiator));
				properties.put(PROP_BR_RECORD_MAIN_OBJECT, getObjectDescription(mainObject));
				if (objects != null && objects.size() > 0) {
					for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
						properties.put(QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i+1)),
								(isNodeRef(objects.get(i)) ? wrapAsLink(new NodeRef(objects.get(i)),false) : objects.get(i)));
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
					nodeService.createAssociation(result, eventCategory, ASSOC_BR_RECORD_EVENT_CAT);

				}
				if (objectType != null) {
					nodeService.createAssociation(result, objectType, ASSOC_BR_RECORD_OBJ_TYPE);
				}

				if (objects != null && objects.size() > 0) {
					for (int j = 0; j < objects.size() && j < MAX_SECONDARY_OBJECTS_COUNT; j++) {
						if (isNodeRef(objects.get(j))) {
							nodeService.createAssociation(result, new NodeRef(objects.get(j)), QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(j+1)));
						}
					}
				}
				return result;
			}
		});
	}

	/**
	 * Метод заполняет карту плейсхолдеров значениями на основании типов объектов
	 *
	 *
	 * @param initiator  - инициатор события (cm:person)
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @return заполненная карта
	 */
	private Map<String, String> fillHolders(NodeRef initiator, NodeRef mainObject, List<String> objects) {
		Map<String, String> holders = new HashMap<String, String>();
		holders.put(BASE_USER_HOLDER, wrapAsLink(initiator, true));
		holders.put(MAIN_OBJECT_HOLDER, wrapAsLink(mainObject, false));
		if (objects != null && objects.size() > 0) {
			for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
				holders.put(OBJECT_HOLDER + (i + 1), isNodeRef(objects.get(i)) ? wrapAsLink(new NodeRef(objects.get(i)), false) : objects.get(i));
			}
		}
		return holders;
	}

	private String wrapAsLink(NodeRef link, boolean isInititator) {
		SysAdminParams params = serviceRegistry.getSysAdminParams();
		String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
		String description = isInititator ? getInitiatorDescription(link) : getObjectDescription(link);
		if (link != null) {
            String linkUrl = isLECMDocument(link) ? DOCUMENT_LINK_URL : LINK_URL;
            return "<a href=\"" + serverUrl + linkUrl + "?nodeRef=" + link.toString() + "\">"
                        + description + "</a>";
		} else {
			return description;
		}
	}

	/**
	 * Метод формирующий описание заданного объекта по шаблону, определяемому по типу объекта
	 * @param object - текущий объект
	 * @return сформированное описание
	 */
	@Override
	public String getObjectDescription(NodeRef object) {
        return substituteService.getObjectDescription(object);
	}

	private String getInitiatorDescription(NodeRef initiator) {
		if (initiator != null) {
			return getObjectDescription(initiator);
		} else {
			return DEFAULT_SYSTEM_TEMPLATE;
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

    @Override
	public NodeRef getBusinessJournalDirectory() {
		return bjRootRef;
	}

	@Override
	public NodeRef getBusinessJournalArchiveDirectory() {
		return bjArchiveRef;
	}

	private String getSeconObjAssocName(int j) {
		return "bjRecord-secondaryObj" + j + "-assoc";
	}

	private String getSecondObjPropName(int i) {
		return "bjRecord-secondaryObj" + i;
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
			results = searchService.query(sp);
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

	private NodeRef getArchiveFolder(final Date date, String type, String category) {
		return getFolder(getBusinessJournalArchiveDirectory(), type, category, date);
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
		return getFolder(root, directoryPaths);
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
		return dictionaryService.getRecordByParamValue("Тип объекта", PROP_OBJ_TYPE_CLASS, shortTypeName);
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
        String query;
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
            results = searchService.query(sp);
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

	@Override
	public boolean moveRecordToArchive(final NodeRef record) {
		if (!orgstructureService.isCurrentUserTheSystemUser() && !isBJEngineer()) {
			logger.warn("Current employee is not business journal engeneer");
			return false;
		}
		AuthenticationUtil.RunAsWork<Boolean> raw = new AuthenticationUtil.RunAsWork<Boolean>() {
			@Override
			public Boolean doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
					@Override
					public Boolean execute() throws Throwable {
						if (!isArchive(record)) {
							NodeRef objectType = findNodeByAssociationRef(record, ASSOC_BR_RECORD_OBJ_TYPE, null, ASSOCIATION_TYPE.TARGET);
							String type;
							if (objectType != null) {
								type = (String) nodeService.getProperty(objectType, ContentModel.PROP_NAME);
							} else {
								NodeRef mainObject = findNodeByAssociationRef(record, ASSOC_BR_RECORD_MAIN_OBJ, null, ASSOCIATION_TYPE.TARGET);
								type = nodeService.getType(mainObject).getPrefixString().replace(":", "_");
							}
							String category;
							NodeRef eventCategory = findNodeByAssociationRef(record, ASSOC_BR_RECORD_EVENT_CAT, null, ASSOCIATION_TYPE.TARGET);
							if (eventCategory != null) {
								category = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
							} else {
								category = "unknown";
							}
							NodeRef archiveRef = getArchiveFolder(new Date(), type, category);
							nodeService.setProperty(record, IS_ACTIVE, false); // помечаем как неактивная запись
							ChildAssociationRef newRef = nodeService.moveNode(record, archiveRef, ContentModel.ASSOC_CONTAINS, nodeService.getPrimaryParent(record).getQName());
							return newRef != null;
						} else {
							return true;
						}
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public boolean isBJEngineer() {
		return orgstructureService.isCurrentEmployeeHasBusinessRole(BusinessJournalService.BUSINESS_ROLE_BUSINESS_JOURNAL_ENGENEER);
	}

	@Override
    public List<NodeRef> getHistory(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending, final boolean includeSecondary) {

        List<NodeRef> result = getHistory(nodeRef, includeSecondary);

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
    public List<NodeRef> getHistory(NodeRef nodeRef, boolean includeSecondary) {
        if (nodeRef == null) {
            return new ArrayList<NodeRef>();
        }

        List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(nodeRef, ASSOC_BR_RECORD_MAIN_OBJ);

        List<NodeRef> result = new ArrayList<NodeRef>();
        int index = includeSecondary ? MAX_SECONDARY_OBJECTS_COUNT  : 0;

        for (int i = -1; i<index; i++) {
            if (i >= 0) {
                sourceAssocs = nodeService.getSourceAssocs(nodeRef, QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(i)));
            }
            for (AssociationRef sourceAssoc : sourceAssocs) {
                NodeRef bjRecordRef = sourceAssoc.getSourceRef();

                if (!isArchive(bjRecordRef)) {
                    result.add(bjRecordRef);
                }
            }
        }

        return result;
    }

    @Override
    public List<NodeRef> getStatusHistory(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending) {

        List<NodeRef> result = getHistory(nodeRef, true);
        List<NodeRef> resultStatus = new ArrayList<NodeRef>();

        final QName sortFieldQName = sortColumnLocalName != null && sortColumnLocalName.length() > 0 ? QName.createQName(BJ_NAMESPACE_URI, sortColumnLocalName) : PROP_BR_RECORD_DATE;
        if (sortFieldQName == null) {
            return result;
        }

        List<NodeRef> eventStatus = new ArrayList<NodeRef>();
        // Получаем nodeRef события - Переход документа в новый статус
        eventStatus.add(getEventCategoryByCode("CHANGE_DOCUMENT_STATUS"));
        eventStatus.add(getEventCategoryByCode("ADD"));

        for (NodeRef status : eventStatus) {
            for (int i = 0; i < result.size(); i++) {
                String strNodeRef = (String) nodeService.getProperty(result.get(i), PROP_BR_RECORD_EVENT_CAT);
                NodeRef property = new NodeRef(strNodeRef);
                if (property != null) {
                    if (status.equals(property)) {
                        resultStatus.add(result.get(i));
                    }
                }
            }
        }
        result = resultStatus;

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

    private boolean isLECMDocument(NodeRef document){
        QName testType = nodeService.getType(document);
        Collection<QName> subDocumentTypes = dicService.getSubTypes(TYPE_BASE_DOCUMENT, true);
        return subDocumentTypes != null && subDocumentTypes.contains(testType);
    }
}
