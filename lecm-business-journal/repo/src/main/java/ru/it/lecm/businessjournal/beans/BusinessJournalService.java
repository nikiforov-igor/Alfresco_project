package ru.it.lecm.businessjournal.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
public class BusinessJournalService {

	public static final String BJ_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";

	/**
	 * Корневой узел Business Journal
	 */
	public static final String BJ_ROOT_NAME = "Business Journal";
	public static final String BR_ASSOC_QNAME = "businessJournal";

	public static final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	public static final QName TYPE_OBJECT_TYPE = QName.createQName(BJ_NAMESPACE_URI, "objectType");
	public static final QName TYPE_EVENT_CATEGORY = QName.createQName(BJ_NAMESPACE_URI, "eventCategory");
	public static final QName TYPE_MESSAGE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate");
	public static final QName TYPE_BR_RECORD = QName.createQName(BJ_NAMESPACE_URI, "bjRecord");

	public static final QName ASSOC_MESSAGE_TEMP_OBJ_TYPE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-objType-assoc");
	public static final QName ASSOC_MESSAGE_TEMP_EVENT_CAT = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-evCategory-assoc");
	public static final QName ASSOC_BR_RECORD_INITIATOR = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-initiator-assoc");
	public static final QName ASSOC_BR_RECORD_MAIN_OBJ = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-mainObject-assoc");
	public static final QName ASSOC_BR_RECORD_EVENT_CAT = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-evCategory-assoc");
	public static final QName ASSOC_BR_RECORD_OBJ_TYPE = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-objType-assoc");
	public static final QName ASSOC_BR_RECORD_SEC_OBJ1 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj1-assoc");
	public static final QName ASSOC_BR_RECORD_SEC_OBJ2 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj2-assoc");
	public static final QName ASSOC_BR_RECORD_SEC_OBJ3 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj3-assoc");
	public static final QName ASSOC_BR_RECORD_SEC_OBJ4 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj4-assoc");
	public static final QName ASSOC_BR_RECORD_SEC_OBJ5 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj5-assoc");

	public static final QName PROP_OBJ_TYPE_CODE = QName.createQName(BJ_NAMESPACE_URI, "objectType-code");
	public static final QName PROP_OBJ_TYPE_CLASS = QName.createQName(BJ_NAMESPACE_URI, "objectType-class");
	public static final QName PROP_OBJ_TYPE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "objectType-template");
	public static final QName PROP_EVENT_CAT_CODE = QName.createQName(BJ_NAMESPACE_URI, "eventCategory-code");
	public static final QName PROP_MESSAGE_TEMP_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-template");
	public static final QName PROP_MESSAGE_TEMP_CODE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-code");

	public static final QName PROP_BR_RECORD_DATE = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-date");
	public static final QName PROP_BR_RECORD_DESC = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-description");
	public static final QName PROP_BR_RECORD_SEC_OBJ1 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj1");
	public static final QName PROP_BR_RECORD_SEC_OBJ2 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj2");
	public static final QName PROP_BR_RECORD_SEC_OBJ3 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj3");
	public static final QName PROP_BR_RECORD_SEC_OBJ4 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj4");
	public static final QName PROP_BR_RECORD_SEC_OBJ5 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj5");

	public static final String BASE_USER_HOLDER = "$baseuser";
	public static final String MAIN_OBJECT_HOLDER = "$mainobject";
	public static final String OBJECT_HOLDER = "$object";

	public static final String DEFAULT_TEMPLATE =
			"Запись журнала, не имеющая шаблонов описания. Основной объект: " + MAIN_OBJECT_HOLDER +
					", Пользователь: " + BASE_USER_HOLDER +
					", дополнительные объекты: " + OBJECT_HOLDER + "1 ," + OBJECT_HOLDER + "2 ," + OBJECT_HOLDER + "3 ," + OBJECT_HOLDER + "4 ," + OBJECT_HOLDER + "5";


	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private NodeService nodeService;
	private NodeRef bjRootRef;
	final private static Logger logger = LoggerFactory.getLogger(BusinessJournalService.class);

	final static DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

	private static enum ASSOCIATION_TYPE {
		SOURCE,
		TARGET
	}

	;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она создана.
	 * Записыывает в свойства сервиса nodeRef директории с бизнес-журналами
	 */
	public void bootstrap() {
		final String rootName = BJ_ROOT_NAME;
		repositoryHelper.init();
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

					;
				});
			}
		};
		bjRootRef = AuthenticationUtil.runAsSystem(raw);
	}

	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef objType, NodeRef eventCategory, String description, List<NodeRef> objects) {
		NodeRef record = null;
		// заполняем карту плейсхолдеров
		Map<String, String> holdersMap = fillHolders(initiator, mainObject, objects);
		//получаем шаблон сообщения
		String templateString = getTemplateString(objType, eventCategory, description);
		// заполняем шаблон данными
		String filled = formatTemplate(templateString, holdersMap);

		// создание записи
		return createRecord(date, initiator, mainObject, filled, eventCategory, objType, objects);
	}

	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef objType, NodeRef eventCategory, String description, NodeRef[] objects) {
		List<NodeRef> objs = Arrays.asList(objects);
		return fire(date, initiator, mainObject, objType, eventCategory, description, objs);
	}

	private NodeRef createRecord(final Date date, final NodeRef initiator, final NodeRef mainObject, final String filled, final NodeRef eventCategory, final NodeRef objType, final List<NodeRef> objects) {
		NodeRef recordRef = null;
		final NodeRef rootRef = getBusinessJournalDirectoryRef();
		final QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
		final QName nodeTypeQName = TYPE_BR_RECORD;

		recordRef = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
			@Override
			public NodeRef execute() throws Throwable {
				// создаем ноду
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
				properties.put(PROP_BR_RECORD_DATE, date);
				properties.put(PROP_BR_RECORD_DESC, filled);
				if (objects != null && objects.size() > 0) {
					for (int i = 0; i < objects.size(); i++) {
						NodeRef obj = objects.get(i);
						properties.put(QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i)), obj);
					}
				}
				ChildAssociationRef associationRef = nodeService.createNode(rootRef, assocTypeQName, assocQName, nodeTypeQName, properties);
				NodeRef result = associationRef.getChildRef();
				// создаем ассоциации
				nodeService.createAssociation(result, initiator, ASSOC_BR_RECORD_INITIATOR);
				nodeService.createAssociation(result, mainObject, ASSOC_BR_RECORD_MAIN_OBJ);
				nodeService.createAssociation(result, eventCategory, ASSOC_BR_RECORD_EVENT_CAT);
				nodeService.createAssociation(result, objType, ASSOC_BR_RECORD_OBJ_TYPE);
				if (objects != null && objects.size() > 0) {
					for (int j = 0; j < objects.size(); j++) {
						nodeService.createAssociation(result, objects.get(j), QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(j)));
					}
				}
				return result;
			}

			;
		});
		return recordRef;
	}

	private Map<String, String> fillHolders(NodeRef initiator, NodeRef mainObject, List<NodeRef> objects) {
		Map<String, String> holders = new HashMap<String, String>();
		holders.put(BASE_USER_HOLDER, initiator.toString());
		holders.put(MAIN_OBJECT_HOLDER, mainObject.toString());
		if (objects != null && objects.size() > 0) {
			for (int i = 0; i < objects.size(); i++) {
				NodeRef obj = objects.get(i);
				holders.put(OBJECT_HOLDER + (i + 1), obj.toString());
			}
		}
		return holders;
	}

	/**
	 * Метод формирующий описание заданного объекта по его типу
	 *
	 * @return сформированное описание или null, если для типа не задан шаблон
	 */
	public String getObjectDescriptionByTemplate(NodeRef object, NodeRef type) {
		return "object-description";
	}

	/**
	 * Получение Шаблона Сообщения из прешедших данных
	 *
	 * @return сформированный шаблон сообщения
	 */
	public String getTemplateString(NodeRef objectType, NodeRef eventCategory, String description) {
		// сначала пытаемся получить строку шаблона по типу и категории
		String template = getTemplate(objectType, eventCategory);
		if (template == null) {
			// если не удалось - получаем из параметра  description
			if (description != null && !description.isEmpty()) {
				template = description;
			} else { // если параметр description пустой - берем значение по умолчанию
				template = DEFAULT_TEMPLATE;
			}
		}
		return template;
	}

	/**
	 * Метод, получающий шаблон описания по заданным типу и категории
	 *
	 * @return шаблон описания или Null, если шаблон не задан
	 */
	public String getTemplate(NodeRef objectType, NodeRef eventCategory) {
		return "template";
	}

	/**
	 * Метод, возвращающий "заполненный" шаблон - с замещенными holders
	 *
	 * @return сформированная строка
	 */
	public String formatTemplate(String template, Map<String, String> holders) {
		return "formatedTemplate";
	}

	public NodeRef getBusinessJournalDirectoryRef() {
		return bjRootRef;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public JSONObject getRecord(NodeRef recordRef) {
		JSONObject record = new JSONObject();
		final Map<QName, Serializable> props = nodeService.getProperties(recordRef);
		try {
			record.put("bjRecord-date", DateFormatISO8601.format((Date) props.get(PROP_BR_RECORD_DATE)));
			record.put("bjRecord-description", props.get(PROP_BR_RECORD_DESC) != null ? props.get(PROP_BR_RECORD_DESC) : "");
			for (int i = 0; i < 5; i++) {
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

			for (int j = 0; j < 5; j++) {
				String name = getSeconObjAssocName(j);
				NodeRef objRef = findNodeAssociationRef(recordRef, QName.createQName(BJ_NAMESPACE_URI, name), ContentModel.TYPE_CMOBJECT, ASSOCIATION_TYPE.TARGET);
				if (objRef != null) {
					record.put(name, objRef.toString());
				}
			}
		} catch (JSONException e) {
			logger.error("", e);
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
	 * @param nodeRef исходная нода
	 * @param assocTypeName имя типа ассоциации
	 * @param typeName  имя типа данных который завязан на ассоциацию
	 * @param type направление ассоциации source или target
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
}
