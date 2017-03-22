package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 28.02.13
 * Time: 16:03
 */
public interface DocumentService {
    public static final String DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/document/1.0";
    public static final String DOCUMENT_ASPECTS_NAMESPACE_URI = "http://www.it.ru/lecm/document/aspects/1.0";
    public static final String DOCUMENT_SUBJECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/document/dictionaries/1.0";
    public static final String DOCUMENT_DELIVERY_METHOD_NAMESPACE_URI = "http://www.it.ru/logicECM/document/dictionaries/deliveryMethod/1.0";

    public static final QName TYPE_BASE_DOCUMENT = QName.createQName(DOCUMENT_NAMESPACE_URI, "base");

    public static final QName PROP_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "present-string");
    public static final QName PROP_EXT_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "ext-present-string");
    public static final QName PROP_LIST_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "list-present-string");
    public static final QName PROP_ML_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "ml-present-string");
    public static final QName PROP_ML_EXT_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "ml-ext-present-string");
    public static final QName PROP_ML_LIST_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "ml-list-present-string");
    public static final String CONSTRAINT_PRESENT_STRING = "present-string-constraint";
    public static final String CONSTRAINT_AUTHOR_PROPERTY = "author-property-constraint";
    public static final String CONSTRAINT_REG_NUMBERS_PROPERTIES = "reg-number-properties-constraint";
    public static final String CONSTRAINT_ARM_URL = "arm-url-constraint";
    public static final String CONSTRAINT_DOCUMENT_URL = "document-url-constraint";
    public static final String DEFAULT_CREATE_URL = "document-create";
    String DEFAULT_EDIT_URL = "document-edit";
    public static final String DEFAULT_VIEW_URL = "document";

    public static final QName PROP_RATING = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rating");
    public static final QName PROP_RATED_PERSONS_COUNT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-persons-count");

    public static final QName PROP_STATUS_CHANGED_DATE = QName.createQName(DOCUMENT_NAMESPACE_URI, "status-changed-date");
    public static final QName PROP_DOCUMENT_CREATOR = QName.createQName(DOCUMENT_NAMESPACE_URI, "creator");
    public static final QName PROP_DOCUMENT_CREATOR_REF = QName.createQName(DOCUMENT_NAMESPACE_URI, "creator-ref");
    public static final QName PROP_DOCUMENT_MODIFIER = QName.createQName(DOCUMENT_NAMESPACE_URI, "modifier");
    public static final QName PROP_DOCUMENT_MODIFIER_REF = QName.createQName(DOCUMENT_NAMESPACE_URI, "modifier-ref");
    public static final QName PROP_DOCUMENT_IS_TRANSMIT = QName.createQName(DOCUMENT_NAMESPACE_URI, "istransmit");
    public static final QName PROP_DOCUMENT_DEPRIVE_RIGHT = QName.createQName(DOCUMENT_NAMESPACE_URI, "deprive-right");
    public static final QName PROP_DOCUMENT_EMPLOYEE_REF = QName.createQName(DOCUMENT_NAMESPACE_URI, "employee-ref");
	public static final QName ASSOC_AUTHOR = QName.createQName(DOCUMENT_NAMESPACE_URI, "author-assoc");

    public static final String PREF_DOCUMENTS = "ru.it.lecm.documents";
    public static final String PREF_ARCHIVE_DOCUMENTS = "ru.it.lecm.documents.archive";
    public static final String PREF_DOC_LIST_AUTHOR = ".documents-list-docAuthor-filter";

    public static final QName ASPECT_PARENT_DOCUMENT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "parent-document");
    public static final QName ASSOC_PARENT_DOCUMENT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "parent-document-assoc");

    public static final QName ASPECT_SEMANTIC_ASSIST = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "semanticAssistAspect");
    public static final QName ASPECT_FINALIZE_TO_UNIT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "finalize-to-unit");
    public static final QName ASPECT_DONT_MOVE_TO_ARCHIVE_FOLDER = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "dont-move-to-archive-folder");
    public static final QName PROP_IS_SHARED_FOLDER = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "is-shared-folder");
    public static final QName ASSOC_ORGANIZATION_UNIT_ASSOC = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "organization-unit-assoc");
    public static final QName ASSOC_ADDITIONAL_ORGANIZATION_UNIT_ASSOC = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "additional-organization-unit-assoc");
	public static final QName ASPECT_LECM_ATTACHMENT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "lecm-attachment");

    public static final QName ASSOC_TEMP_ATTACHMENTS = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "attachments-temp-assoc");

    public static final QName ASSOC_RESPONSE_TO = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "response-to-assoc");

    public static final QName TYPE_DOC_SUBJECT = QName.createQName(DOCUMENT_SUBJECTS_NAMESPACE_URI, "subjects");

    public static final QName ASPECT_HAS_REG_PROJECT_DATA = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "has-reg-project-data");

    public static final QName ASPECT_HAS_REG_DOCUMENT_DATA = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "has-reg-document-data");

    public static final QName PROP_DOCUMENT_REGNUM = QName.createQName(DOCUMENT_NAMESPACE_URI, "regnum");
    public static final QName PROP_DOCUMENT_DATE = QName.createQName(DOCUMENT_NAMESPACE_URI, "doc-date");

    public static final QName PROP_REG_DATA_DOC_NUMBER = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "reg-data-number");
    public static final QName PROP_REG_DATA_DOC_DATE = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "reg-data-date");
    public static final QName PROP_REG_DATA_PROJECT_NUMBER = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "reg-project-data-number");
    public static final QName PROP_REG_DATA_PROJECT_DATE = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "reg-project-data-date");
    public static final QName PROP_REG_DATA_DOC_IS_REGISTERED = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "reg-data-is-registered");
    public static final QName ASSOC_REG_DATA_DOC_REGISTRATOR = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "registrator-assoc");

    public static final QName PROP_DOCUMENT_TYPE = QName.createQName(DOCUMENT_NAMESPACE_URI, "doc-type");

    public static final QName PROP_DELIVERY_METHOD_CODE = QName.createQName(DOCUMENT_DELIVERY_METHOD_NAMESPACE_URI, "deliveryMethod-code");

    public static final QName ASSOC_SUBJECT= QName.createQName(DOCUMENT_NAMESPACE_URI, "subject-assoc");
    public static final QName PROP_TITLE = QName.createQName(DOCUMENT_NAMESPACE_URI, "title");

    public static final String DEFAULT_REG_NUM = "Не присвоено";

    public static final String EDS_NAMESPACE_URI = "http://www.it.ru/logicECM/eds-document/1.0";
    public static final QName TYPE_EDS_DOCUMENT = QName.createQName(EDS_NAMESPACE_URI, "base");
	public static final QName PROP_EDS_EXECUTION_DATE = QName.createQName(EDS_NAMESPACE_URI, "execution-date");

	public static final QName PROP_SYS_WORKFLOWS = QName.createQName(DOCUMENT_NAMESPACE_URI, "sys_workflows");

	public static final QName ASPECT_DOC_CANCELLED = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "cancelled");
	public static final QName ASPECT_DOC_ACCEPT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "in-work");

	public static final QName ASPECT_WITHOUT_ATTACHMENTS = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "without-attachments");

    /**
     * Метод для получения рейтинга документа
     * documentNodeRef - document nodeRef
     *
     * @return document rating
     */
    public String getRating(NodeRef documentNodeRef);

    /**
     * Метод для получения количества сотрудников, оценивших документ
     * documentNodeRef - document nodeRef
     *
     * @return persons count
     */
    public Integer getRatedPersonCount(NodeRef documentNodeRef);

    /**
     * Метод для получения рейтинга документа, выставленного текущим сотрудником
     * documentNodeRef - document nodeRef
     *
     * @return my rating of the document
     */
    public Integer getMyRating(NodeRef documentNodeRef);

    /**
     * Метод для выставления рейтинга документа текущим сотрудником
     * documentNodeRef - document nodeRef
     * rating - rating
     *
     * @return setted rating
     */
    public Integer setMyRating(NodeRef documentNodeRef, Integer rating);

    /**
     * Метод получения аттрибутов документа
     *
     * @param nodeRef
     * @return attributes
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef);

    public NodeRef createDocument(String type, Map<String, String> properties, Map<String, String> association);

    public NodeRef editDocument(NodeRef nodeRef, Map<String, String> properties);

    public boolean isDocument(NodeRef ref);

    /**
     * Получение пути для корневой папки черновиков для текущего пользователя
     *
     * @return xpath до директории с черновиками
     */
    String getDraftPath();

    /**
     * Получение пути для корневой папки для данного типа документов для текущего пользователя
     *
     * @param docType
     * @return xpath до директории
     */
    String getDraftPathByType(QName docType);

	/**
	 * Создание корневой папки
	 * @param docType
	 * @return
         * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
	 */
	public NodeRef createDraftRoot(QName docType) throws WriteTransactionNeededException;

    /**
     * Получение пути для папки Documents
     *
     * @return xpath до директории
     */
    String getDocumentsFolderPath();

    /**
     * Получение корневой ноды с черновиками
     *
     * @return NodeRef
     */
    NodeRef getDraftRoot();

    /**
     * Получение ноды с черновиками для заданного типа документов
     *
     * @param docType
     * @return NodeRef
     */
    NodeRef getDraftRootByType(QName docType);

    /**
     * Получение списка участников для данного типа документов
     *
     * @return List<NodeRef> - ссылок на сотрудников
     */
    public List<NodeRef> getMembers(QName docType);

    /**
     * Поиск документов по разным параметрам
     *
     * @param docTypes       - список QName типов на документы
     * @param paths          - список путей для поиска, формат: app:company_home/cm:Черновики
     * @param statuses       - список статусов, если null то по статусам не фильтрует
     * @param sortDefinition - набор полей для сортировки
     * @return List<NodeRef> - ссылки на документы
     */
    public List<NodeRef> getDocumentsByFilter(List<QName> docTypes, List<String> paths, List<String> statuses, String filterQuery, List<SortDefinition> sortDefinition);

    /**
     * Подсчет количества документов по разным параметрам
     *
     * @param docTypes       - список QName типов на документы
     * @param paths          - список путей для поиска, формат: app:company_home/cm:Черновики
     * @param statuses       - список статусов, если null то по статусам не фильтрует
     * @param sortDefinition - набор полей для сортировки
     * @return Integer - количество документов
     */
    public Long getAmountDocumentsByFilter(List<QName> docTypes, List<String> paths, List<String> statuses, String filterQuery, List<SortDefinition> sortDefinition);

    /**
     * Метод для получения папки с черновиками для заданного типа
     *
     * @return имя папки с черновиками
     */
    public String getDraftRootLabel(QName docType);

    public String getAuthorProperty(QName docType);

    public NodeRef duplicateDocument(NodeRef document);

    /**
     * Проверяет наличие настроек копирования
     * @param document NodeRef документа
     * @return наличие настроек копирования
     */
    boolean canCopyDocument(NodeRef document);

    String getDocumentCopyURL(NodeRef document);

    /**
     * Метод для получения регистрационных номеров документа (номер проекта (если есть) и реального (если есть))
     *
     * @return список из номеров
     */
    public List<String> getRegNumbersValues(NodeRef document);

    /**
     * Получение даты регистрации проекта
     * @param document документ
     * @return дата регистрации проекта, если есть. Иначе NULL
     */
    Date getProjectRegDate(NodeRef document);

    /**
     * Получение даты регистрации документа
     * @param document документ
     * @return дата регистрации документа, если есть. Иначе NULL
     */
    Date getDocumentRegDate(NodeRef document);

    /**
     * Метод для получения регистрационного номера проекта документа
     *
     * @return рег номер проекта или (Не присвоено, если номера еще нет), либо null, если у документа нет нужного аспекта
     */
    public String getProjectRegNumber(NodeRef document);

    /**
     * Метод для получения регистрационного номера  документа
     *
     * @return рег номер документа или (Не присвоено, если номера еще нет), либо null, если у документа нет нужного аспекта
     */
    public String getDocumentRegNumber(NodeRef document);

    /**
     * Получение актуального номера документа
     * @param document документ
     * @return регистрационный номер документа, если есть. Иначе регистрационный номер проекта, если есть. Иначе NULL
     */
    String getDocumentActualNumber(NodeRef document);

    /**
     * Получение актуальной даты документа
     * @param document документ
     * @return дата регистрации документа, если есть. Иначе дату регистрации проекта, если есть. Иначе NULL
     */
    Date getDocumentActualDate(NodeRef document);

    void setDocumentActualNumber(NodeRef document, String number);

    void setDocumentActualDate(NodeRef document, Date date);

    /**
     * Метод для получения регистратора документа
     *
     * @return регистратор документа или null
     */
    public NodeRef getDocumentRegistrator(NodeRef document);

    /**
     * Возвращает заголовок документа без проверки на доступ к документу
     *
     * @param document
     * @return
     */
    public String getPresentString(NodeRef document);

    /**
     * Возвращает автора документа
     *
     * @param document - ссылка на документ
     * @return ссылку на сотрудника-автора
     */
    public NodeRef getDocumentAuthor(NodeRef document);

    public Collection<QName> getDocumentSubTypes();

    /**
     * Выполнение булева выражения для документа
     * @param document
     * @param expression
     * @return
     */
    public boolean execExpression(NodeRef document, String expression);

    /**
     * Выполнение строкового выражения для документа
     * @param document
     * @param expression
     * @return
     */
    String execStringExpression(NodeRef document, String expression);
    String execStringExpression(NodeRef document, String expression, boolean withContext);

    /**
     * Установить настройки финализации в папку подразделения
     * @param document - ссылка на документ
     * @param sharedFolder - финализировать в общую папку подразделения
     * @param primaryUnit - основное подразделение
     * @param additionalUnits - дополнительные подразделения
     */
    public void finalizeToUnit(NodeRef document, Boolean sharedFolder, NodeRef primaryUnit, List<NodeRef> additionalUnits);
    public void finalizeToUnit(NodeRef document, Boolean sharedFolder, NodeRef primaryUnit);
    public void finalizeToUnit(NodeRef document, NodeRef primaryUnit, List<NodeRef> additionalUnits);
    public void finalizeToUnit(NodeRef document, NodeRef primaryUnit);

    List<NodeRef> getDocumentsByQuery(String query, List<SortDefinition> sort, int skipCount, int loadCount);

	public String wrapAsDocumentLink(NodeRef documentRef);

    /**
     * Добавить документ в избранное
     * @param document - - ссылка на документ
     */
    void addToFavourites(NodeRef document);

    /**
     * Удалить документ из избранного
     * @param document - ссылка на документ
     */
    void removeFromFavourites(NodeRef document);

    boolean hasOrganization(NodeRef document);

    NodeRef getOrganization(NodeRef document);

    String getCreateUrl(QName type);

    String getViewUrl(QName type);

    String getEditUrl(QName type);

    String getDocumentUrl(NodeRef document);

    String getDocumentTypeLabel(String docType);
}
