package ru.it.lecm.errands;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.List;

/**
 * User: AIvkin Date: 09.07.13 Time: 12:09
 */
public interface ErrandsService {

    String ERRANDS_ROOT_NAME = "Сервис Поручения";
    String ERRANDS_ROOT_ID = "ERRANDS_ROOT_ID";
    String ERRANDS_LINK_FOLDER_NAME = "Ссылки";

    String ERRANDS_SETTINGS_NODE_NAME = "Settings";
    String ERRANDS_DASHLET_SETTINGS_NODE_NAME = "Dashlet Settings";

    enum ERRANDS_TS_COEXECUTOR_REPORT_STATUS{
        PROJECT,ONCONTROL,APPROVE,DECLINE
    }

    String ERRANDS_REPORT_CONNECTION_TYPE = "docReport";

    String ERRANDS_NAMESPACE_URI = "http://www.it.ru/logicECM/errands/1.0";
    String ERRANDS_NAMESPACE_DIC_URI = "http://www.it.ru/logicECM/errands/dictionaries/1.0";
    String ERRANDS_ASPECT_NAMESPACE_URI = "http://www.it.ru/logicECM/errands-aspects/1.0";
    String ERRANDS_TS_NAMESPACE_URI = "http://www.it.ru/logicECM/errands/table-structure/1.0";

    QName TYPE_ERRANDS = QName.createQName(ERRANDS_NAMESPACE_URI, "document");
    QName TYPE_ERRANDS_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings");
    QName TYPE_ERRANDS_DASHLET_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "dashlet-settings");
    QName TYPE_ERRANDS_USER_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings");
    QName TYPE_ERRANDS_DIC_TITLES = QName.createQName(ERRANDS_NAMESPACE_DIC_URI,"errand-titles");
    QName TYPE_ERRANDS_DIC_TYPE = QName.createQName(ERRANDS_NAMESPACE_DIC_URI,"errand-type");
    QName TYPE_ERRANDS_TS_COEXECUTOR_REPORT = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report");
    QName TYPE_ERRANDS_TS_EXECUTION_REPORT = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"execution-report");
    QName PROP_ERRANDS_INITIATOR_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "initiator-assoc-ref");
    QName PROP_ERRANDS_EXECUTOR_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "executor-assoc-ref");
    QName PROP_ERRANDS_CONTROLLER_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "controller-assoc-ref");
    QName PROP_ERRANDS_ADDITIONAL_DOCUMENT_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "additional-document-assoc-ref");

    QName PROP_ERRANDS_IS_SHORT = QName.createQName(ERRANDS_NAMESPACE_URI, "is-short");
    QName PROP_ERRANDS_IS_IMPORTANT = QName.createQName(ERRANDS_NAMESPACE_URI, "is-important");
    QName PROP_ERRANDS_EXECUTION_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-date");
    QName PROP_ERRANDS_IS_EXPIRED = QName.createQName(ERRANDS_NAMESPACE_URI, "is-expired");
    QName PROP_ERRANDS_IS_REJECTED = QName.createQName(ERRANDS_NAMESPACE_URI, "was-rejected");
    QName PROP_ERRANDS_TITLE = QName.createQName(ERRANDS_NAMESPACE_URI, "title");
    QName PROP_ERRANDS_START_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "start-date");
    QName PROP_ERRANDS_START_WORK_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "work-start-date");
    QName PROP_ERRANDS_END_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "end-date");
    QName PROP_ERRANDS_EXECUTION_REPORT = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-report");
    QName PROP_ERRANDS_NUMBER = QName.createQName(ERRANDS_NAMESPACE_URI, "number");
    QName PROP_ERRANDS_JUST_IN_TIME = QName.createQName(ERRANDS_NAMESPACE_URI, "just-in-time");
    QName PROP_ERRANDS_CONTENT = QName.createQName(ERRANDS_NAMESPACE_URI, "content");
    QName PROP_ERRANDS_WITHOUT_INITIATOR_APPROVAL = QName.createQName(ERRANDS_NAMESPACE_URI, "without-initiator-approval");

    QName PROP_ERRANDS_REPORT_REQUIRED = QName.createQName(ERRANDS_NAMESPACE_URI,"report-required");
    QName PROP_ERRANDS_REPORT_RECIPIENT_TYPE = QName.createQName(ERRANDS_NAMESPACE_URI,"report-recipient-type");
    QName PROP_ERRANDS_LIMITATION_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "limitation-date");
    QName PROP_ERRANDS_LIMITATION_DATE_TEXT = QName.createQName(ERRANDS_NAMESPACE_URI,"limitation-date-text");
    QName PROP_ERRANDS_LIMITATION_DATE_RADIO = QName.createQName(ERRANDS_NAMESPACE_URI,"limitation-date-radio");
    QName PROP_ERRANDS_LIMITATION_DATE_DAYS = QName.createQName(ERRANDS_NAMESPACE_URI,"limitation-date-days");
    QName PROP_ERRANDS_LIMITATION_DATE_TYPE = QName.createQName(ERRANDS_NAMESPACE_URI,"limitation-date-type");
    QName PROP_ERRANDS_HALF_LIMIT_DATE = QName.createQName(ERRANDS_NAMESPACE_URI,"half-limit-date");
    QName PROP_ERRANDS_IS_LIMIT_SHORT_DATE = QName.createQName(ERRANDS_NAMESPACE_URI,"is-limit-short");

    QName PROP_ERRANDS_IS_PERIODICALLY = QName.createQName(ERRANDS_NAMESPACE_URI, "periodically");

    QName PROP_ERRANDS_CANCELLATION_SIGNAL = QName.createQName(ERRANDS_NAMESPACE_URI,"cancellation-signal");
    QName PROP_ERRANDS_CANCELLATION_SIGNAL_REASON = QName.createQName(ERRANDS_NAMESPACE_URI,"cancellation-signal-reason");

    QName PROP_ERRANDS_TS_COEXECUTOR_REPORT_ACCEPT_DATE = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report-accept-date");
    QName PROP_ERRANDS_TS_COEXECUTOR_REPORT_ROUTE_DATE = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report-route-date");
    QName PROP_ERRANDS_TS_COEXECUTOR_REPORT_STATUS = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report-status");
    QName PROP_ERRANDS_TS_COEXECUTOR_REPORT_TEXT = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report-text");
    QName PROP_ERRANDS_TS_COEXECUTOR_REPORT_IS_ROUTE = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report-is-route");

    QName ASSOC_ERRANDS_TS_COEXECUTOR = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-assoc");
    QName ASSOC_ERRANDS_TS_COEXECUTOR_ATTACHMENT = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report-attachment-assoc");
    QName ASSOC_ERRANDS_TS_COEXECUTOR_CONNECTED_DOCUMENT = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"coexecutor-report-connected-document-assoc");

    QName ASSOC_ERRANDS_TS_EXECUTOR_ATTACHMENT = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"execution-report-attachment-assoc");
    QName ASSOC_ERRANDS_TS_EXECUTOR_CONNECTED_DOCUMENT = QName.createQName(ERRANDS_TS_NAMESPACE_URI,"execution-report-connected-document-assoc");

    QName PROP_ERRANDS_DIC_TITLE_CODE = QName.createQName(ERRANDS_NAMESPACE_DIC_URI,"errand-title-code");
    QName PROP_ERRANDS_DIC_TYPE_DEFAULT_TITLE = QName.createQName(ERRANDS_NAMESPACE_DIC_URI,"errand-type-default-title");
    QName PROP_ERRANDS_DIC_TYPE_LIMITLESS = QName.createQName(ERRANDS_NAMESPACE_DIC_URI,"errand-type-limitless");
    QName PROP_ERRANDS_DIC_TYPE_REPORT_REQUIRED = QName.createQName(ERRANDS_NAMESPACE_DIC_URI,"errand-type-report-required");
    QName PROP_ERRANDS_DIC_TYPE_LAUNCH_REVIEW = QName.createQName(ERRANDS_NAMESPACE_DIC_URI,"errand-type-launch-review");
    QName PROP_ERRANDS_CHILD_INDEX = QName.createQName(ERRANDS_NAMESPACE_URI,"child-index-counter");

    QName SETTINGS_PROP_MODE_CHOOSING_EXECUTORS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings-mode-choosing-executors");
    String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION = "ORGANIZATION";
    String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_UNIT = "UNIT";
    QName SETTINGS_PROP_TRANSFER_RIGHT = QName.createQName(ERRANDS_NAMESPACE_URI, "settings-transfer-rights");
    QName SETTINGS_HIDE_ADDITIONAL_ATTRS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings-hide-additional-attrs");

    QName USER_SETTINGS_PROP_WITHOUT_INITIATOR_APPROVAL = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-without-initiator-approval");
    QName USER_SETTINGS_ASSOC_DEFAULT_INITIATOR = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-initiator-assoc");
    QName USER_SETTINGS_ASSOC_DEFAULT_SUBJECT = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-subject-assoc");

    QName ASSOC_ADDITIONAL_ERRANDS_DOCUMENT = QName.createQName(ERRANDS_NAMESPACE_URI, "additional-document-assoc");
    QName PROP_ADDITIONAL_DOC_NUMBER = QName.createQName(ERRANDS_NAMESPACE_URI, "additional-doc-number");
    QName PROP_BASE_DOC_NUMBER = QName.createQName(ERRANDS_NAMESPACE_URI, "base-doc-number");
    QName ASSOC_BASE_DOCUMENT = QName.createQName(ERRANDS_NAMESPACE_URI, "base-assoc");
    QName ASSOC_ERRANDS_INITIATOR = QName.createQName(ERRANDS_NAMESPACE_URI, "initiator-assoc");
    QName ASSOC_ERRANDS_CONTROLLER = QName.createQName(ERRANDS_NAMESPACE_URI, "controller-assoc");
    QName ASSOC_ERRANDS_EXECUTOR = QName.createQName(ERRANDS_NAMESPACE_URI, "executor-assoc");
    QName ASSOC_ERRANDS_TYPE = QName.createQName(ERRANDS_NAMESPACE_URI, "type-assoc");
    QName ASSOC_ERRANDS_CO_EXECUTORS = QName.createQName(ERRANDS_NAMESPACE_URI, "coexecutors-assoc");
    QName ASSOC_ERRANDS_LINKS = QName.createQName(ERRANDS_NAMESPACE_URI, "links-assoc");
    QName ASSOC_ERRANDS_EXECUTION_LINKS = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-links-assoc");
    QName ASSOC_ERRANDS_CANCELLATION_SIGNAL_SENDER = QName.createQName(ERRANDS_NAMESPACE_URI, "cancellation-signal-sender-assoc");
    QName ASSOC_ERRANDS_EXECUTION_CONNECTED_DOCS = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-connected-document-assoc");
    QName ASSOC_ERRANDS_EXECUTION_ATTACHMENT = QName.createQName(ERRANDS_NAMESPACE_URI, "execution-report-attachment-assoc");

    QName ASSOC_ERRANDS_TS_COEXECUTOR_REPORTS = QName.createQName(ERRANDS_TS_NAMESPACE_URI, "coexecutor-reports-assoc");
    QName ASSOC_ERRANDS_TS_EXECUTION_REPORTS = QName.createQName(ERRANDS_TS_NAMESPACE_URI, "execution-reports-assoc");
    String BUSINESS_ROLE_ERRANDS_INITIATOR_ID = "ERRANDS_INITIATOR";
    String BUSINESS_ROLE_CHOOSING_INITIATOR = "CHOOSING_INITIATOR";
    String ERRANDS_TYPE_DICTIONARY_NAME = "Типы поручений";
    String ERRAND_TYPE_ON_POINT_ORD = "Поручение по пункту ОРД";

    QName ASPECT_ERRANDS_EXECUTORS = QName.createQName(ERRANDS_ASPECT_NAMESPACE_URI, "errandsExecutorsAspect");
    QName ASSOC_ERRANDS_EXECUTORS_FIRST_LEVEL = QName.createQName(ERRANDS_ASPECT_NAMESPACE_URI, "errands-executors-assoc");
    QName ASSOC_ERRANDS_CO_EXECUTORS_FIRST_LEVEL = QName.createQName(ERRANDS_ASPECT_NAMESPACE_URI, "errands-co-executors-assoc");
    /**
     * Получение папки для черновиков
     *
     * @return ссылку на папку с черновиками
     */
    NodeRef getDraftRoot();

    /**
     * Получение объекта глобальных настроек для поручений
     *
     * @return ссылка на объект глобальных настроек для поручений
     */
    NodeRef getSettingsNode();

    /**
     * Возвращает NodeRef настроек дашлетов для поручений
     * @return
     */
    NodeRef getDashletSettingsNode();
    NodeRef createDashletSettingsNode();

    NodeRef createSettingsNode();

    boolean isHideAdditionAttributes();

    /**
     * Получение объекта настроек поручений текущего пользователя
     *
     * @return ссылка на объект пользовательских настроек поручений
     */
    NodeRef getCurrentUserSettingsNode();

    /**
     * Создание объекта настроек поручений текущего пользователя
     *
     * @return
     * @throws WriteTransactionNeededException
     */
    NodeRef createCurrentUserSettingsNode() throws WriteTransactionNeededException;

    /**
     * Получение списка сотрудников, доступных текущему пользователю для выбора
     * исполнителя и соисполнителей
     *
     * @return список сотрудников
     */
    List<NodeRef> getAvailableExecutors();

    /**
     * Получение списка сотрудников, доступных данному пользователю для выбора
     * исполнителя и соисполнителей
     *
     * @return список сотрудников
     */
    List<NodeRef> getAvailableExecutors(NodeRef employeeRef);

    /**
     * Проверяет личные настройки "Без утверждения Инициатором"
     *
     * @return true - если в личных настройках выбрано "Без утверждения
     * Инициатором"
     */
    boolean isDefaultWithoutInitiatorApproval();

    /**
     * Получает инициатора по умолчанию из личных настроек
     *
     * @return ссылка на сотрудника
     */
    NodeRef getDefaultInitiator();

    /**
     * Получает тематику по умолчанию из личных настроек
     *
     * @return ссылка на элеменнт справочника "Тематика"
     */
    NodeRef getDefaultSubject();

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
    List<NodeRef> getErrandsDocuments(List<String> paths, int skipCount, int maxItems);

    /**
     * Поиск подписок находит все подписки в активном статусе.
     *
     * @param paths пути поиска
     * @param skipCount - сколько результатов надо пропустить
     * @param maxItems - максимальное число результатов
     * @return
     */
    List<NodeRef> getActiveErrands(List<String> paths, int skipCount, int maxItems);

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
    NodeRef createLinksFolderRef(final NodeRef document) throws WriteTransactionNeededException;

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
    NodeRef getAdditionalDocumentNode(NodeRef errand);

    /**
     * Сохранение отчёта об исполнении
     *
     * @param errandRef Ссылка на поручение
     * @param report Отчёт об исполнении
     */
    void setExecutionReport(NodeRef errandRef, String report);

    ModeChoosingExecutors getModeChoosingExecutors();

    /**
     * Получение глобальной настройки "Передавать права на документ-основание"
     *
     * @return значение настройки
     */
    boolean isTransferRightToBaseDocument();

    NodeRef getExecutor(NodeRef errand);

    NodeRef getBaseDocument(NodeRef errand);

    /**
     * Проверяет наличие незавершенных дочерних поручений исполнителя
     * @param errand NodeRef поручения
     * @return наличие незавершенных дочерних поручений исполнителя
     */
    boolean hasChildNotFinalByExecutor(NodeRef errand);

    /**
     * Получение списка дочерних резолюций
     * @param errand документ
     * @return список дочерних резолюций
     */
    List<NodeRef> getChildResolutions(NodeRef errand);

    /**
     * Получение списка дочерних поручений
     * @param errand документ
     * @return список дочерних поручений
     */
    List<NodeRef> getChildErrands(NodeRef errand);

    /**
     * Отправить сигнал о необходимости завершения в поручения
     * @param errand документ
     * @param reason причина сигнала
     */
    void sendCancelSignal(NodeRef errand, String reason, NodeRef signalSender);

    enum ModeChoosingExecutors {
        ORGANIZATION,
        UNIT
    }
}
