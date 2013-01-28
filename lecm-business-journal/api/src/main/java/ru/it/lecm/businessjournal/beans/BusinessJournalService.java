package ru.it.lecm.businessjournal.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * @author dbashmakov
 *         Date: 26.12.12
 *         Time: 16:50
 */
public interface BusinessJournalService {

	public static final String BJ_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";
	public static final String BJ_ASPECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/aspects/1.0";

	/**
	 * Корневой узел Business Journal
	 */
	public static final String BJ_ROOT_NAME = "Business Journal";
	public static final String BJ_ARCHIVE_ROOT_NAME = "Архивные записи";
	public static final String BJ_ARCHIVER_SETTINGS_NAME = "Настройки автоматического архивирования";
	public static final String BR_ASSOC_QNAME = "businessJournal";
	public static final String BR_ARCHIVE_ASSOC_QNAME = "archive";
	public static final String BR_ARCHIVE_SETTINGS_ASSOC_QNAME = "archiveSettings";
	public static final String DICTIONARIES_ROOT_NAME = "Dictionary";
	public static final String DICTIONARY_OBJECT_TYPE = "Тип объекта";
	public static final String DICTIONARY_EVENT_CATEGORY = "Категория события";
	public static final String DICTIONARY_MESSAGE_TEMPLATE = "Шаблон сообщения";

	public static final QName TYPE_OBJECT_TYPE = QName.createQName(BJ_NAMESPACE_URI, "objectType");
	public static final QName TYPE_EVENT_CATEGORY = QName.createQName(BJ_NAMESPACE_URI, "eventCategory");
	public static final QName TYPE_MESSAGE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate");
	public static final QName TYPE_BR_RECORD = QName.createQName(BJ_NAMESPACE_URI, "bjRecord");
	public static final QName TYPE_ARCHIVER_SETTINGS = QName.createQName(BJ_NAMESPACE_URI, "archiverSettings");

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
	public static final QName PROP_BR_RECORD_INITIATOR = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-initiator");
	public static final QName PROP_BR_RECORD_MAIN_OBJECT = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-mainObject");
	public static final QName PROP_BR_RECORD_SEC_OBJ1 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj1");
	public static final QName PROP_BR_RECORD_SEC_OBJ2 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj2");
	public static final QName PROP_BR_RECORD_SEC_OBJ3 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj3");
	public static final QName PROP_BR_RECORD_SEC_OBJ4 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj4");
	public static final QName PROP_BR_RECORD_SEC_OBJ5 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj5");

	public static final QName PROP_ARCHIVER_DEEP = QName.createQName(BJ_NAMESPACE_URI, "archiver-deep");
	public static final QName PROP_ARCHIVER_PERIOD= QName.createQName(BJ_NAMESPACE_URI, "archiver-period");

    public static final QName TYPE_HISTORY = QName.createQName(BJ_ASPECTS_NAMESPACE_URI, "history");
    public static final QName PROP_HISTORY_LIST = QName.createQName(BusinessJournalService.BJ_ASPECTS_NAMESPACE_URI, "history-list");

    public static final String BASE_USER_HOLDER = "#baseuser";
	public static final String MAIN_OBJECT_HOLDER = "#mainobject";
	public static final String OBJECT_HOLDER = "#object";

	public final String DEFAULT_MESSAGE_TEMPLATE =
			"Запись журнала, не имеющая шаблонов описания. Основной объект: " + MAIN_OBJECT_HOLDER +
					", Пользователь: " + BASE_USER_HOLDER +
					", дополнительные объекты: " + OBJECT_HOLDER + "1 ," + OBJECT_HOLDER + "2 ," + OBJECT_HOLDER + "3 ," + OBJECT_HOLDER + "4 ," + OBJECT_HOLDER + "5";
	public final int MAX_SECONDARY_OBJECTS_COUNT = 5;

	public final String DEFAULT_OBJECT_TYPE_TEMPLATE = "{cm:name}";
	public final String DEFAULT_SYSTEM_TEMPLATE = "Система";

	public final String SYSTEM = "System";

	final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    /**
     * Метод для создания записи бизнеса-журнала
     * @param date - дата создания записи
     * @param initiator  - инициатор события (логин пользователя)
     * @param mainObject - основной объект
     * @param objects    - список дополнительных объектов
     * @param  eventCategory  - категория события
     * @param  defaultDescription  - описание события
     * @return ссылка на ноду записи в бизнес журнале
     */
	public NodeRef log(Date date, String initiator, NodeRef mainObject, NodeRef eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception;

    /**
     * Метод для создания записи бизнеса-журнала
     *
     * @param date - дата создания записи
     * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
     * @param mainObject - основной объект
     * @param  eventCategory  - категория события
     * @param  defaultDescription  - описание события
     * @param objects    - список дополнительных объектов
     * @return ссылка на ноду записи в бизнес журнале
     */
	public NodeRef log(Date date, NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception;

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
	public NodeRef log(Date date, NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String defaultDescription, NodeRef[] objects) throws Exception;

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
    public NodeRef log(Date date, String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception;

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(NodeRef initiator, NodeRef mainObject, NodeRef eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception;

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception;

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 * @param initiator  - инициатор события
	 * @param mainObject - основной объект
	 * @param objects    - список дополнительных объектов
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<NodeRef> objects) throws Exception;
	/**
	 * Метод формирующий описание заданного объекта на основании его типа
	 * @param object - текущий объект
	 * @return сформированное описание
	 */
	public String getObjectDescription(NodeRef object);

	/**
	 * Метод, возвращающий корневую директорию
	 * @return ссылка
	 */
	public NodeRef getBusinessJournalDirectory();

	/**
	 * Метод, возвращающий список ссылок на записи бизнес-журнала, сформированные за заданный период
	 *
	 * @param begin - начальная дата
	 * @param end   - конечная дата
	 * @return список ссылок
	 */
	public List<NodeRef> getRecordsByInterval(Date begin, Date end);

	/**
	 * Метод, проверяющий что заданная нода является записью бизнес-журнала
	 *
	 * @return true/false
	 */
	public boolean isBJRecord(NodeRef ref);

	/**
	 * Метод, возвращающий список ссылок на записи заданного типа,
	 * сформированные за заданный период с учетом инициатора
	 * @param objectTypeRef    - тип объекта
	 * @param begin         - начальная дата
	 * @param end           - конечная дата
	 * @param whoseKey      - дополнительная фильтрация по инициатору  (@link BusinessJournalServiceImpl.WhoseEnum)
	 * @return список ссылок
	 */
    public List<NodeRef> getRecordsByParams(String objectTypeRef, Date begin, Date end, String whoseKey);

	/**
	 * Метод, возвращающий директорию c архивными записями
	 * @return ссылка
	 */
	public NodeRef getBusinessJournalArchiveDirectory();

	/**
	 * Метод, перемещающий заданную запись в архив
	 * @return boolean результат выполнения операции
	 */
	public boolean moveRecordToArchive(NodeRef record);

    List<NodeRef> getHistory(NodeRef nodeRef, String sortColumnName, boolean ascending);

    List<NodeRef> getHistory(NodeRef nodeRef);

}
