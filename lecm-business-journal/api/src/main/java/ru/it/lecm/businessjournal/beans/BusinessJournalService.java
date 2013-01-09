package ru.it.lecm.businessjournal.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;

/**
 * @author dbashmakov
 *         Date: 26.12.12
 *         Time: 16:50
 */
public interface BusinessJournalService {

	public static final String BJ_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";

	/**
	 * Корневой узел Business Journal
	 */
	public final String BJ_ROOT_NAME = "Business Journal";
	public final String BR_ASSOC_QNAME = "businessJournal";
	public final String DICTIONARIES_ROOT_NAME = "Dictionary";
	public final String DICTIONARY_OBJECT_TYPE = "Тип объекта";
	public final String DICTIONARY_EVENT_CATEGORY = "Категория события";
	public final String DICTIONARY_MESSAGE_TEMPLATE = "Шаблон сообщения";

	public final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	public final QName TYPE_OBJECT_TYPE = QName.createQName(BJ_NAMESPACE_URI, "objectType");
	public final QName TYPE_EVENT_CATEGORY = QName.createQName(BJ_NAMESPACE_URI, "eventCategory");
	public final QName TYPE_MESSAGE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate");
	public final QName TYPE_BR_RECORD = QName.createQName(BJ_NAMESPACE_URI, "bjRecord");

	public final QName ASSOC_MESSAGE_TEMP_OBJ_TYPE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-objType-assoc");
	public final QName ASSOC_MESSAGE_TEMP_EVENT_CAT = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-evCategory-assoc");
	public final QName ASSOC_BR_RECORD_INITIATOR = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-initiator-assoc");
	public final QName ASSOC_BR_RECORD_MAIN_OBJ = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-mainObject-assoc");
	public final QName ASSOC_BR_RECORD_EVENT_CAT = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-evCategory-assoc");
	public final QName ASSOC_BR_RECORD_OBJ_TYPE = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-objType-assoc");
	public final QName ASSOC_BR_RECORD_SEC_OBJ1 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj1-assoc");
	public final QName ASSOC_BR_RECORD_SEC_OBJ2 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj2-assoc");
	public final QName ASSOC_BR_RECORD_SEC_OBJ3 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj3-assoc");
	public final QName ASSOC_BR_RECORD_SEC_OBJ4 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj4-assoc");
	public final QName ASSOC_BR_RECORD_SEC_OBJ5 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj5-assoc");

	public final QName PROP_OBJ_TYPE_CODE = QName.createQName(BJ_NAMESPACE_URI, "objectType-code");
	public final QName PROP_OBJ_TYPE_CLASS = QName.createQName(BJ_NAMESPACE_URI, "objectType-class");
	public final QName PROP_OBJ_TYPE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "objectType-template");
	public final QName PROP_EVENT_CAT_CODE = QName.createQName(BJ_NAMESPACE_URI, "eventCategory-code");
	public final QName PROP_MESSAGE_TEMP_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-template");
	public final QName PROP_MESSAGE_TEMP_CODE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-code");

	public final QName PROP_BR_RECORD_DATE = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-date");
	public QName PROP_BR_RECORD_DESC = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-description");
	public QName PROP_BR_RECORD_SEC_OBJ1 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj1");
	public final QName PROP_BR_RECORD_SEC_OBJ2 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj2");
	public final QName PROP_BR_RECORD_SEC_OBJ3 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj3");
	public final QName PROP_BR_RECORD_SEC_OBJ4 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj4");
	public final QName PROP_BR_RECORD_SEC_OBJ5 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj5");

	public final String BASE_USER_HOLDER = "#baseuser";
	public final String MAIN_OBJECT_HOLDER = "#mainobject";
	public final String OBJECT_HOLDER = "#object";

	public final String DEFAULT_MESSAGE_TEMPLATE =
			"Запись журнала, не имеющая шаблонов описания. Основной объект: " + MAIN_OBJECT_HOLDER +
					", Пользователь: " + BASE_USER_HOLDER +
					", дополнительные объекты: " + OBJECT_HOLDER + "1 ," + OBJECT_HOLDER + "2 ," + OBJECT_HOLDER + "3 ," + OBJECT_HOLDER + "4 ," + OBJECT_HOLDER + "5";
	public final int MAX_SECONDARY_OBJECTS_COUNT = 5;

	public final String DEFAULT_OBJECT_TYPE_TEMPLATE = "{cm:name}";
	public final String DEFAULT_SYSTEM_TEMPLATE = "Система";

	public final String SYSTEM = "System";

	final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
	final DateFormat FolderNameFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Метод для создания записи бизнеса-журнала
     * @param date - дата создания записи
     * @param initiator  - инициатор события (логин пользователя)
     * @param mainObject - основной объект
     * @param objects    - список дополнительных объектов
     * @param  eventCategory  - категория события
     * @param  description  - описание события
     * @return ссылка на ноду записи в бизнес журнале
     */
	public NodeRef fire(Date date, String initiator, NodeRef mainObject, NodeRef eventCategory, String description, List<NodeRef> objects) throws Exception;

    /**
     * Метод для создания записи бизнеса-журнала
     * @param date - дата создания записи
     * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
     * @param mainObject - основной объект
     * @param objects    - список дополнительных объектов
     * @param  eventCategory  - категория события
     * @param  description  - описание события
     * @return ссылка на ноду записи в бизнес журнале
     */
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String description, List<NodeRef> objects) throws Exception;

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
	public NodeRef fire(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String description, NodeRef[] objects) throws Exception;

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
    public NodeRef fire(Date date, String initiator, String mainObject, String eventCategory, String description, List<NodeRef> objects) throws Exception;
	/**
	 * Метод формирующий описание заданного объекта на основании его типа
	 * @param object - текущий объект
	 * @return сформированное описание или null, если для типа не задан шаблон
	 */
	public String getObjectDescription(NodeRef object);

	/**
	 * Метод для получения шаблонной строки для заданного типа
	 * @param type - ссылка на объект справочника "Тип Объекта"
	 * @return шаблонную строку или null, если не удалось найти соответствие
	 */
	public String getTemplateByType(NodeRef type);

	public NodeRef getBusinessJournalDirectory();

	public JSONObject getRecordJSON(NodeRef recordRef) throws Exception;

	/**
	 * Метод, возвращающий список ссылок на записи бизнес-журнала, сформированные за заданный период
	 *
	 * @param begin - начальная дата
	 * @param end   - конечная дата
	 * @return список ссылок
	 */
	public List<NodeRef> getRecordsByInterval(Date begin, Date end);

	public boolean isBJRecord(NodeRef ref);
}
