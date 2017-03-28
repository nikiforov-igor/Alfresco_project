package ru.it.lecm.resolutions.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:40
 */
public interface ResolutionsService {
    String CLOSERS_AUTHOR = "AUTHOR";
    String CLOSERS_CONTROLLER = "CONTROLLER";
    String CLOSERS_AUTHOR_AND_CONTROLLER = "AUTHOR_AND_CONTROLLER";

    String RESOLUTION_NAMESPACE_URI = "http://www.it.ru/logicECM/resolutions/1.0";
    String RESOLUTION_SETTINGS_NAMESPACE_URI = "http://www.it.ru/logicECM/resolutions-settings/1.0";

    String RESOLUTIONS_ROOT_NAME = "Сервис Резолюции";
    String RESOLUTIONS_ROOT_ID = "RESOLUTIONS_ROOT_ID";

    String RESOLUTION_DASHLET_SETTINGS_NODE_NAME = "Dashlet Settings";

    QName TYPE_RESOLUTION_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "document");
    QName TYPE_RESOLUTION_DASHLET_SETTINGS = QName.createQName(RESOLUTION_SETTINGS_NAMESPACE_URI, "dashlet-settings");

    QName PROP_ERRANDS_JSON = QName.createQName(RESOLUTION_NAMESPACE_URI, "errands-json");
    QName PROP_LIMITATION_DATE = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date");
    QName PROP_LIMITATION_DATE_RADIO = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-radio");
    QName PROP_LIMITATION_DATE_DAYS = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-days");
    QName PROP_LIMITATION_DATE_TYPE = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-type");
    QName PROP_LIMITATION_DATE_TEXT = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-text");
    QName PROP_CLOSERS = QName.createQName(RESOLUTION_NAMESPACE_URI, "closers");
    QName PROP_IS_EXPIRED = QName.createQName(RESOLUTION_NAMESPACE_URI, "is-expired");
    QName PROP_ANNUL_SIGNAL = QName.createQName(RESOLUTION_NAMESPACE_URI,"annul-signal");
    QName PROP_ANNUL_SIGNAL_REASON = QName.createQName(RESOLUTION_NAMESPACE_URI,"annul-signal-reason");

    QName ASSOC_BASE_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "base-document-assoc");
    QName ASSOC_BASE = QName.createQName(RESOLUTION_NAMESPACE_URI, "base-assoc");
    QName ASSOC_AUTHOR = QName.createQName(RESOLUTION_NAMESPACE_URI, "author-assoc");
    QName ASSOC_CONTROLLER = QName.createQName(RESOLUTION_NAMESPACE_URI, "controller-assoc");

    boolean checkResolutionErrandsExecutionDate(NodeRef resolution);

    List<NodeRef> getResolutionClosers(NodeRef resolution);
    /**
     * Отправить сигнал о необходимости аннулирования в резолюцию
     * @param resolution документ
     * @param reason причина сигнала
     */

    /**
     * Возвращает NodeRef настроек дашлетов для резолюций
     * @return NodeRef настроек дашлетов для резолюций
     */
    NodeRef getDashletSettingsNode();

    /**
     * Создание настроек дашлетов для резолюций
     * Create an instance of {@link NodeRef }
     */
    NodeRef createDashletSettingsNode();

    void sendAnnulSignal(NodeRef resolution, String reason);
    void resetAnnulSignal(NodeRef resolution);

    NodeRef getResolutionBase(NodeRef resolution);
}
