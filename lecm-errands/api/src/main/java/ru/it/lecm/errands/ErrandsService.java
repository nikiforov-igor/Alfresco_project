package ru.it.lecm.errands;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: AIvkin
 * Date: 09.07.13
 * Time: 12:09
 */
public interface ErrandsService {
	public static final String ERRANDS_ROOT_NAME = "Сервис Поручения";
	public static final String ERRANDS_ROOT_ID = "ERRANDS_ROOT_ID";

	public static final String ERRANDS_SETTINGS_NODE_NAME = "Settings";

	public static final String ERRANDS_NAMESPACE_URI = "http://www.it.ru/logicECM/errands/1.0";

	public static final QName TYPE_ERRANDS = QName.createQName(ERRANDS_NAMESPACE_URI, "document");
	public static final QName TYPE_ERRANDS_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings");
	public static final QName TYPE_ERRANDS_USER_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings");
	public static final QName PROP_ERRANDS_INITIATOR_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "initiator-assoc-ref");
    public static final QName PROP_ERRANDS_EXECUTOR_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "executor-assoc-ref");
    public static final QName PROP_ERRANDS_CONTROLLER_REF = QName.createQName(ERRANDS_NAMESPACE_URI, "controller-assoc-ref");

    public static final QName PROP_ERRANDS_IS_IMPORTANT = QName.createQName(ERRANDS_NAMESPACE_URI, "is-important");
    public static final QName PROP_ERRANDS_LIMITATION_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "limitation-date");
    public static final QName PROP_ERRANDS_IS_EXPIRED = QName.createQName(ERRANDS_NAMESPACE_URI, "is-expired");
    public static final QName PROP_ERRANDS_TITLE = QName.createQName(ERRANDS_NAMESPACE_URI, "title");
    public static final QName PROP_ERRANDS_START_DATE = QName.createQName(ERRANDS_NAMESPACE_URI, "start-date");

	public static final QName SETTINGS_PROP_MODE_CHOOSING_EXECUTORS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings-mode-choosing-executors");
	public static final String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION = "ORGANIZATION";
	public static final String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_UNIT = "UNIT";

	public static final QName USER_SETTINGS_PROP_WITHOUT_INITIATOR_APPROVAL = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-without-initiator-approval");
	public static final QName USER_SETTINGS_ASSOC_DEFAULT_INITIATOR = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-initiator-assoc");
	public static final QName USER_SETTINGS_ASSOC_DEFAULT_SUBJECT = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-subject-assoc");

	public static final QName ASSOC_ADDITIONAL_ERRANDS_DOCUMENT = QName.createQName(ERRANDS_NAMESPACE_URI, "additional-document-assoc");
    public static final QName PROP_BASE_DOC_NUMBER = QName.createQName(ERRANDS_NAMESPACE_URI, "base-doc-number");
	public static final QName ASSOC_ERRANDS_INITIATOR = QName.createQName(ERRANDS_NAMESPACE_URI, "initiator-assoc");
	public static final QName ASSOC_ERRANDS_CONTROLLER = QName.createQName(ERRANDS_NAMESPACE_URI, "controller-assoc");
	public static final QName ASSOC_ERRANDS_EXECUTOR = QName.createQName(ERRANDS_NAMESPACE_URI, "executor-assoc");
	public static final QName ASSOC_TEMP_ATTACHMENTS = QName.createQName(ERRANDS_NAMESPACE_URI, "attachments-temp-assoc");

	public static final String BUSINESS_ROLE_ERRANDS_INITIATOR_ID = "ERRANDS_INITIATOR";
	public static final String BUSINESS_ROLE_ERRANDS_CHOOSING_INITIATOR = "ERRANDS_CHOOSING_INITIATOR";

	/**
	 * Получение папки для черновиков
	 * @return ссылку на папку с черновиками
	 */
	public NodeRef getDraftRoot();

	/**
	 * Получение объекта глобальных настроек для поручений
	 * @return ссылка на объект глобальных настроек для поручений
	 */
	public NodeRef getSettingsNode();

	/**
	 * Получение объекта настроек поручений текущего пользователя
	 * @return ссылка на объект пользовательских настроек поручений
	 */
	public NodeRef getCurrentUserSettingsNode();

	/**
	 * Получение списка сотрудников, доступных текущему пользователю для выбора инициатора
	 * @return список сотрудников
	 */
	public List<NodeRef> getAvailableInitiators();

	/**
	 * Проверяет личные настройки "Без утверждения Инициатором"
	 * @return true - если в личных настройках выбрано "Без утверждения Инициатором"
	 */
	public boolean isDefaultWithoutInitiatorApproval();

	/**
	 * Получает инициатора по умолчанию из личных настроек
	 * @return ссылка на сотрудника
	 */
	public NodeRef getDefaultInitiator();

	/**
	 * Получает тематику по умолчанию из личных настроек
	 * @return ссылка на элеменнт справочника "Тематика"
	 */
	public NodeRef getDefaultSubject();

    public List<NodeRef> getErrandsDocuments(List<String> paths, int skipCount, int maxItems);

    /**
     * Получить поручения, связанные с документом и находящиеся в статусах для выбранных бизнес ролей
     * @param document ссылка на документ
     * @param filter фильтр
     * @param roles роли
     * @return
     */
    List<NodeRef> getFilterDocumentErrands(NodeRef document, String filter, List<QName> roles);

    /**
     * Получить ссылку на документ-основание для поручения
     * @param errand Ссылка на поручение
     * @return Ссылка на документ-основание
     */
    public NodeRef getAdditionalDocumentNode(NodeRef errand);
}
