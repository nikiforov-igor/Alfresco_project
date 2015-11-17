package ru.it.lecm.errands;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.List;

/**
 * User: AIvkin Date: 09.07.13 Time: 12:09
 */
public interface ErrandsService {

    public static final String ERRANDS_ROOT_NAME = "Сервис Поручения";
    public static final String ERRANDS_ROOT_ID = "ERRANDS_ROOT_ID";
    public static final String ERRANDS_LINK_FOLDER_NAME = "Ссылки";

    public static final String ERRANDS_SETTINGS_NODE_NAME = "Settings";
    public static final String ERRANDS_DASHLET_SETTINGS_NODE_NAME = "Dashlet Settings";

    public static final String ERRANDS_NAMESPACE_URI = "http://www.it.ru/logicECM/errands/1.0";

    public static final QName TYPE_ERRANDS = QName.createQName(ERRANDS_NAMESPACE_URI, "document");
    public static final QName TYPE_ERRANDS_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings");
    public static final QName TYPE_ERRANDS_DASHLET_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "dashlet-settings");
    public static final QName TYPE_ERRANDS_USER_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings");
    public static final QName PROP_ERRANDS_INITIATOR_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "initiator-assoc-ref");
    public static final QName PROP_ERRANDS_EXECUTOR_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "executor-assoc-ref");
    public static final QName PROP_ERRANDS_CONTROLLER_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "controller-assoc-ref");
    public static final QName PROP_ERRANDS_ADDITIONAL_DOCUMENT_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "additional-document-assoc-ref");

    public static final QName PROP_ERRANDS_IS_IMPORTANT = QName.createQName(ERRANDS_NAMESPACE_URI, "is-important");
    public static final QName PROP_ERRANDS_LIMITATION_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "limitation-date");
    public static final QName PROP_ERRANDS_EXECUTION_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-date");
    public static final QName PROP_ERRANDS_IS_EXPIRED = QName.createQName(ERRANDS_NAMESPACE_URI, "is-expired");
    public static final QName PROP_ERRANDS_IS_REJECTED = QName.createQName(ERRANDS_NAMESPACE_URI, "was-rejected");
    public static final QName PROP_ERRANDS_TITLE = QName.createQName(ERRANDS_NAMESPACE_URI, "title");
    public static final QName PROP_ERRANDS_START_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "start-date");
    public static final QName PROP_ERRANDS_START_WORK_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "work-start-date");
    public static final QName PROP_ERRANDS_END_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "end-date");
    public static final QName PROP_ERRANDS_EXECUTION_REPORT = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-report");
    public static final QName PROP_ERRANDS_NUMBER = QName.createQName(ERRANDS_NAMESPACE_URI, "number");
    public static final QName PROP_ERRANDS_JUST_IN_TIME = QName.createQName(ERRANDS_NAMESPACE_URI, "just-in-time");
    public static final QName PROP_ERRANDS_CONTENT = QName.createQName(ERRANDS_NAMESPACE_URI, "content");
    public static final QName PROP_ERRANDS_WITHOUT_INITIATOR_APPROVAL = QName.createQName(ERRANDS_NAMESPACE_URI, "without-initiator-approval");

    public static final QName SETTINGS_PROP_MODE_CHOOSING_EXECUTORS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings-mode-choosing-executors");
    public static final String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION = "ORGANIZATION";
    public static final String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_UNIT = "UNIT";
    public static final QName SETTINGS_PROP_TRANSFER_RIGHT = QName.createQName(ERRANDS_NAMESPACE_URI, "settings-transfer-rights");

    public static final QName USER_SETTINGS_PROP_WITHOUT_INITIATOR_APPROVAL = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-without-initiator-approval");
    public static final QName USER_SETTINGS_ASSOC_DEFAULT_INITIATOR = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-initiator-assoc");
    public static final QName USER_SETTINGS_ASSOC_DEFAULT_SUBJECT = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-subject-assoc");

    public static final QName ASSOC_ADDITIONAL_ERRANDS_DOCUMENT = QName.createQName(ERRANDS_NAMESPACE_URI, "additional-document-assoc");
    public static final QName PROP_BASE_DOC_NUMBER = QName.createQName(ERRANDS_NAMESPACE_URI, "base-doc-number");
    public static final QName ASSOC_ERRANDS_INITIATOR = QName.createQName(ERRANDS_NAMESPACE_URI, "initiator-assoc");
    public static final QName ASSOC_ERRANDS_CONTROLLER = QName.createQName(ERRANDS_NAMESPACE_URI, "controller-assoc");
    public static final QName ASSOC_ERRANDS_EXECUTOR = QName.createQName(ERRANDS_NAMESPACE_URI, "executor-assoc");
    public static final QName ASSOC_ERRANDS_CO_EXECUTORS = QName.createQName(ERRANDS_NAMESPACE_URI, "coexecutors-assoc");
    public static final QName ASSOC_ERRANDS_LINKS = QName.createQName(ERRANDS_NAMESPACE_URI, "links-assoc");
    public static final QName ASSOC_ERRANDS_EXECUTION_LINKS = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-links-assoc");

    public static final String BUSINESS_ROLE_ERRANDS_INITIATOR_ID = "ERRANDS_INITIATOR";
    public static final String BUSINESS_ROLE_ERRANDS_CHOOSING_INITIATOR = "ERRANDS_CHOOSING_INITIATOR";

    /**
     * Получение папки для черновиков
     *
     * @return ссылку на папку с черновиками
     */
    public NodeRef getDraftRoot();

    /**
     * Получение объекта глобальных настроек для поручений
     *
     * @return ссылка на объект глобальных настроек для поручений
     */
    public NodeRef getSettingsNode();

    /**
     * Возвращает NodeRef настроек дашлетов для поручений
     * @return
     */
    public NodeRef getDashletSettingsNode();

    public NodeRef createSettingsNode() throws WriteTransactionNeededException;

    /**
     * Получение объекта настроек поручений текущего пользователя
     *
     * @return ссылка на объект пользовательских настроек поручений
     */
    public NodeRef getCurrentUserSettingsNode();

    /**
     * Создание объекта настроек поручений текущего пользователя
     *
     * @return
     * @throws WriteTransactionNeededException
     */
    public NodeRef createCurrentUserSettingsNode() throws WriteTransactionNeededException;

    /**
     * Получение списка сотрудников, доступных текущему пользователю для выбора
     * исполнителя и соисполнителей
     *
     * @return список сотрудников
     */
    public List<NodeRef> getAvailableExecutors();

    /**
     * Получение списка сотрудников, доступных данному пользователю для выбора
     * исполнителя и соисполнителей
     *
     * @return список сотрудников
     */
    public List<NodeRef> getAvailableExecutors(NodeRef employeeRef);

    /**
     * Проверяет личные настройки "Без утверждения Инициатором"
     *
     * @return true - если в личных настройках выбрано "Без утверждения
     * Инициатором"
     */
    public boolean isDefaultWithoutInitiatorApproval();

    /**
     * Получает инициатора по умолчанию из личных настроек
     *
     * @return ссылка на сотрудника
     */
    public NodeRef getDefaultInitiator();

    /**
     * Получает тематику по умолчанию из личных настроек
     *
     * @return ссылка на элеменнт справочника "Тематика"
     */
    public NodeRef getDefaultSubject();

    /**
     * Поиск подписок находит все подписки в активном статусе. Игнорируются
     * подписки в статусе черновик и в финальном статусе. Осуществлена
     * сортировка в следующем порядке Важные, Просроченные, С приближающимся
     * сроком, Остальные. Данный метод применяется в Дашлете "Мои поручения"
     *
     * @param paths пути поиска
     * @param skipCount - сколько результатов надо пропустить
     * @param maxItems - максимальное число результатов
     * @return
     */
    public List<NodeRef> getErrandsDocuments(List<String> paths, int skipCount, int maxItems);

    /**
     * Поиск подписок находит все подписки в активном статусе.
     *
     * @param paths пути поиска
     * @param skipCount - сколько результатов надо пропустить
     * @param maxItems - максимальное число результатов
     * @return
     */
    public List<NodeRef> getActiveErrands(List<String> paths, int skipCount, int maxItems);

    /**
     * Получить поручения, связанные с документом и находящиеся в статусах для
     * выбранных бизнес ролей
     *
     * @param document ссылка на документ
     * @param filter фильтр
     * @param roles роли
     * @return
     */
    List<NodeRef> getFilterDocumentErrands(NodeRef document, String filter, List<QName> roles);

    /**
     * Возвращает директорию Ссылок для конкретного документа
     *
     * @param document - ссылка на документ
     * @return ссылка на ноду
     */
    NodeRef getLinksFolderRef(final NodeRef document);

    /**
     * Создаёт директорию ссылок для конкретного документа
     *
     * @param document
     * @return
     * @throws WriteTransactionNeededException
     */
    public NodeRef createLinksFolderRef(final NodeRef document) throws WriteTransactionNeededException;

    /**
     * Возвращает ссылки на внутренние и внешние объекты системы из формы
     * поручения.
     *
     * @param document - (nodeRef) ссылка на документ
     * @return список ссылок на ссылки
     */
    List<NodeRef> getLinks(NodeRef document);

    /**
     * Возвращает ссылки на внутренние и внешние объекты системы из формы
     * поручения.
     *
     * @param document - ссылка на документ
     * @param skipCount - сколько результатов надо пропустить
     * @param maxItems - максимальное число результатов
     * @return список ссылок на ссылки
     */
    List<NodeRef> getLinks(NodeRef document, int skipCount, int maxItems);

    /**
     * Возвращает ссылки на внутренние и внешние объекты системы из формы
     * поручения.
     *
     * @param document - ссылка на документ
     * @param association - ассоциация, например: "lecm-errands:links-assoc",
     * "lecm-errands:execution-links-assoc"
     * @return список ссылок на ссылки
     */
    List<NodeRef> getLinksByAssociation(NodeRef document, String association);

    /**
     * Создание ссылки lecm-links:link
     *
     * @param document ссылка на документ
     * @param name - название ссылки
     * @param url - ссылка например: http://www.test
     * @param isExecute - true создание "lecm-errands:links-assoc" ассоциации
     * false создание "lecm-errands:execution-links-assoc" оссоциации
     * @return ссылка
     */
    NodeRef createLinks(NodeRef document, String name, String url, boolean isExecute);

    /**
     * Получить ссылку на документ-основание для поручения
     *
     * @param errand Ссылка на поручение
     * @return Ссылка на документ-основание
     */
    public NodeRef getAdditionalDocumentNode(NodeRef errand);

    /**
     * Сохранение отчёта об исполнении
     *
     * @param errandRef Ссылка на поручение
     * @param report Отчёт об исполнении
     */
    public void setExecutionReport(NodeRef errandRef, String report);

    ModeChoosingExecutors getModeChoosingExecutors();

    /**
     * Получение глобальной настройки "Передавать права на документ-основание"
     *
     * @return значение настройки
     */
    public boolean isTransferRightToBaseDocument();

    public NodeRef getExecutor(NodeRef errand);

    public NodeRef getBaseDocument(NodeRef errand);

    enum ModeChoosingExecutors {
        ORGANIZATION,
        UNIT
    }
}
