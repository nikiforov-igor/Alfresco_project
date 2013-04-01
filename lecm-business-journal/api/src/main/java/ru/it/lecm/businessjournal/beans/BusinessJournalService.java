package ru.it.lecm.businessjournal.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author dbashmakov
 *         Date: 26.12.12
 *         Time: 16:50
 */
public interface BusinessJournalService {

	String BJ_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";
	String BJ_ASPECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/aspects/1.0";

	/**
	 * Корневой узел Business Journal
	 */
	String BJ_ROOT_NAME = "Сервис Бизнес-журнал";
	String BJ_ROOT_ID = "BJ_ROOT_ID";
	String BJ_ARCHIVE_ROOT_NAME = "Архивные записи";
	String BJ_ARCHIVE_ROOT_ID = "BJ_ARCHIVE_ROOT_ID";
	String BJ_ARCHIVER_SETTINGS_NAME = "Настройки автоматического архивирования";
	String BR_ASSOC_QNAME = "businessJournal";
	String BR_ARCHIVE_ASSOC_QNAME = "archive";
	String BR_ARCHIVE_SETTINGS_ASSOC_QNAME = "archiveSettings";
	String DICTIONARY_OBJECT_TYPE = "Тип объекта";
	String DICTIONARY_EVENT_CATEGORY = "Категория события";
	String DICTIONARY_MESSAGE_TEMPLATE = "Шаблон сообщения";

	QName TYPE_OBJECT_TYPE = QName.createQName(BJ_NAMESPACE_URI, "objectType");
	QName TYPE_EVENT_CATEGORY = QName.createQName(BJ_NAMESPACE_URI, "eventCategory");
	QName TYPE_MESSAGE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate");
	QName TYPE_BR_RECORD = QName.createQName(BJ_NAMESPACE_URI, "bjRecord");
	QName TYPE_ARCHIVER_SETTINGS = QName.createQName(BJ_NAMESPACE_URI, "archiverSettings");

	QName ASSOC_MESSAGE_TEMP_OBJ_TYPE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-objType-assoc");
	QName ASSOC_MESSAGE_TEMP_EVENT_CAT = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-evCategory-assoc");
	QName ASSOC_BR_RECORD_INITIATOR = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-initiator-assoc");
	QName ASSOC_BR_RECORD_MAIN_OBJ = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-mainObject-assoc");
	QName ASSOC_BR_RECORD_EVENT_CAT = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-evCategory-assoc");
	QName ASSOC_BR_RECORD_OBJ_TYPE = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-objType-assoc");
	QName ASSOC_BR_RECORD_SEC_OBJ1 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj1-assoc");
	QName ASSOC_BR_RECORD_SEC_OBJ2 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj2-assoc");
	QName ASSOC_BR_RECORD_SEC_OBJ3 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj3-assoc");
	QName ASSOC_BR_RECORD_SEC_OBJ4 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj4-assoc");
	QName ASSOC_BR_RECORD_SEC_OBJ5 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj5-assoc");

	QName PROP_OBJ_TYPE_CODE = QName.createQName(BJ_NAMESPACE_URI, "objectType-code");
	QName PROP_OBJ_TYPE_CLASS = QName.createQName(BJ_NAMESPACE_URI, "objectType-class");
	QName PROP_OBJ_TYPE_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "objectType-template");
	QName PROP_EVENT_CAT_CODE = QName.createQName(BJ_NAMESPACE_URI, "eventCategory-code");
	QName PROP_MESSAGE_TEMP_TEMPLATE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-template");
	QName PROP_MESSAGE_TEMP_CODE = QName.createQName(BJ_NAMESPACE_URI, "messageTemplate-code");

	QName PROP_BR_RECORD_DATE = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-date");
	QName PROP_BR_RECORD_DESC = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-description");
	QName PROP_BR_RECORD_INITIATOR = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-initiator");
	QName PROP_BR_RECORD_MAIN_OBJECT = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-mainObject");
	QName PROP_BR_RECORD_SEC_OBJ1 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj1");
	QName PROP_BR_RECORD_SEC_OBJ2 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj2");
	QName PROP_BR_RECORD_SEC_OBJ3 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj3");
	QName PROP_BR_RECORD_SEC_OBJ4 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj4");
	QName PROP_BR_RECORD_SEC_OBJ5 = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-secondaryObj5");

    QName PROP_BR_RECORD_EVENT_CAT = QName.createQName(BJ_NAMESPACE_URI, "bjRecord-evCategory-assoc-ref");

	QName PROP_ARCHIVER_DEEP = QName.createQName(BJ_NAMESPACE_URI, "archiver-deep");
	QName PROP_ARCHIVER_PERIOD= QName.createQName(BJ_NAMESPACE_URI, "archiver-period");

    QName TYPE_HISTORY = QName.createQName(BJ_ASPECTS_NAMESPACE_URI, "history");
    QName PROP_HISTORY_LIST = QName.createQName(BusinessJournalService.BJ_ASPECTS_NAMESPACE_URI, "history-list");

    String BASE_USER_HOLDER = "#initiator";
	String MAIN_OBJECT_HOLDER = "#mainobject";
	String OBJECT_HOLDER = "#object";

	String DEFAULT_MESSAGE_TEMPLATE =
			"Запись журнала, не имеющая шаблонов описания. Основной объект: " + MAIN_OBJECT_HOLDER +
					", Пользователь: " + BASE_USER_HOLDER +
					", дополнительные объекты: " + OBJECT_HOLDER + "1, " + OBJECT_HOLDER + "2, " + OBJECT_HOLDER + "3, " + OBJECT_HOLDER + "4, " + OBJECT_HOLDER + "5";
	int MAX_SECONDARY_OBJECTS_COUNT = 5;

	String DEFAULT_SYSTEM_TEMPLATE = "Система";

	String SYSTEM = "System";

	DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
	String BUSINESS_ROLE_BUSINESS_JOURNAL_ENGENEER = "BR_BUSINESS_JOURNAL_ENGENEER";

	String LINK_URL = "/share/page/view-metadata";
    String DOCUMENT_LINK_URL = "/share/page/document";
    String WORKFLOW_LINK_URL = "/share/page/workflow-details";
    String DOCUMENT_ATTACHMENT_LINK_URL = "/share/page/document-attachment";

    String ACTIVITI_PREFIX = "activiti$";

    /**
     * Метод для создания записи бизнеса-журнала
     *
     *
     * @param date - дата создания записи
     * @param initiator  - инициатор события (ссылка на пользователя системы или сотрудника)
     * @param mainObject - основной объект
     * @param  eventCategory  - категория события
     * @param  defaultDescription  - описание события
     * @param objects    - список дополнительных объектов
     * @return ссылка на ноду записи в бизнес журнале
     */
	public NodeRef log(Date date, NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects);

    /**
     * Метод для создания записи бизнеса-журнала
     *
     * @param date - дата создания записи
     * @param initiator  - инициатор события (логин пользователя)
     * @param mainObject - имя основного объекта
     * @param  eventCategory  - название категории события
     * @param  defaultDescription  - описание события
     * @param objects    - список дополнительных объектов
     * @return ссылка на ноду записи в бизнес журнале
     */
    public NodeRef log(Date date, String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects);

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 *
	 * @param initiator  - инициатор события (ссылка на пользователя системы)
	 * @param mainObject - основной объект
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @param objects    - список дополнительных объектов
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(NodeRef initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects);

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 *
	 * @param initiator  - инициатор события (логин пользователя)
	 * @param mainObject - основной объект
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @param objects    - список дополнительных объектов
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(String initiator, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects);

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 *
	 * @param mainObject - основной объект
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @param objects    - список дополнительных объектов
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects);

	/**
	 * Метод для создания записи бизнеса-журнала
	 *
	 *
	 * @param date - дата создания записи
	 * @param mainObject - основной объект
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @param objects    - список дополнительных объектов
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(Date date, NodeRef mainObject, String eventCategory, String defaultDescription, List<String> objects) ;

	/**
	 * Метод для создания записи бизнеса-журнала с текущей датой
	 *
	 * @param mainObject - основной объект
	 * @param  eventCategory  - категория события
	 * @param  defaultDescription  - описание события
	 * @return ссылка на ноду записи в бизнес журнале
	 */
	public NodeRef log(NodeRef mainObject, String eventCategory, String defaultDescription);
	/**
	 * Метод формирующий описание заданного объекта на основании его типа
	 * @param object - текущий объект
	 * @return сформированное описание
	 */
	String getObjectDescription(NodeRef object);

	/**
	 * Метод, возвращающий корневую директорию
	 * @return ссылка
	 */
	NodeRef getBusinessJournalDirectory();

	/**
	 * Метод, возвращающий список ссылок на записи бизнес-журнала, сформированные за заданный период
	 *
	 * @param begin - начальная дата
	 * @param end   - конечная дата
	 * @return список ссылок
	 */
	List<NodeRef> getRecordsByInterval(Date begin, Date end);

	/**
	 * Метод, проверяющий что заданная нода является записью бизнес-журнала
	 *
	 * @return true/false
	 */
	boolean isBJRecord(NodeRef ref);

	/**
	 * Метод, возвращающий список ссылок на записи заданного типа,
	 * сформированные за заданный период с учетом инициатора
	 * @param objectTypeRef    - тип объекта
	 * @param begin         - начальная дата
	 * @param end           - конечная дата
	 * @param whoseKey      - дополнительная фильтрация по инициатору  (@link BusinessJournalServiceImpl.WhoseEnum)
	 * @return список ссылок
	 */
    List<NodeRef> getRecordsByParams(String objectTypeRef, Date begin, Date end, String whoseKey);

	/**
	 * Метод, возвращающий директорию c архивными записями
	 * @return ссылка
	 */
	NodeRef getBusinessJournalArchiveDirectory();

	/**
	 * Метод, перемещающий заданную запись в архив
	 * @return boolean результат выполнения операции
	 */
	boolean moveRecordToArchive(NodeRef record);

    List<NodeRef> getHistory(NodeRef nodeRef, String sortColumnName, boolean ascending, boolean includeSecondary);

    List<NodeRef> getHistory(NodeRef nodeRef, boolean includeSecondary);

    List<NodeRef> getStatusHistory(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending);

	/**
	 * Является ли текущий пользователь технологом бизнес-журнала
	 * @return true если является
	 */
	boolean isBJEngineer();
}
