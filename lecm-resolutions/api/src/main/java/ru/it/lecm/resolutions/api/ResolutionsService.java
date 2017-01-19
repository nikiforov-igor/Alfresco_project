package ru.it.lecm.resolutions.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.Date;
import java.util.List;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:40
 */
public interface ResolutionsService {
    String EXECUTION_DATE_RADIO_DAYS = "DAYS";
    String EXECUTION_DATE_RADIO_DATE = "DATE";
    String EXECUTION_DATE_RADIO_LIMITLESS = "LIMITLESS";

    String EXECUTION_DATE_DAYS_WORK = "WORK";
    String EXECUTION_DATE_DAYS_CALENDAR = "CALENDAR";

    String CLOSERS_AUTHOR = "AUTHOR";
    String CLOSERS_CONTROLLER = "CONTROLLER";
    String CLOSERS_AUTHOR_AND_CONTROLLER = "AUTHOR_AND_CONTROLLER";

    String RESOLUTION_NAMESPACE_URI = "http://www.it.ru/logicECM/resolutions/1.0";

    QName TYPE_RESOLUTION_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "document");

    QName PROP_ERRANDS_JSON = QName.createQName(RESOLUTION_NAMESPACE_URI, "errands-json");
    QName PROP_LIMITATION_DATE = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date");
    QName PROP_LIMITATION_DATE_RADIO = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-radio");
    QName PROP_LIMITATION_DATE_DAYS = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-days");
    QName PROP_LIMITATION_DATE_TYPE = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-type");
    QName PROP_LIMITATION_DATE_TEXT = QName.createQName(RESOLUTION_NAMESPACE_URI, "limitation-date-text");
    QName PROP_CLOSERS = QName.createQName(RESOLUTION_NAMESPACE_URI, "closers");
    QName PROP_IS_EXPIRED = QName.createQName(RESOLUTION_NAMESPACE_URI, "is-expired");

    QName ASSOC_BASE_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "base-document-assoc");
    QName ASSOC_BASE = QName.createQName(RESOLUTION_NAMESPACE_URI, "base-assoc");
    QName ASSOC_AUTHOR = QName.createQName(RESOLUTION_NAMESPACE_URI, "author-assoc");
    QName ASSOC_CONTROLLER = QName.createQName(RESOLUTION_NAMESPACE_URI, "controller-assoc");

    Date calculateResolutionExecutionDate(String radio, Integer days, String daysType, Date date);

    boolean checkResolutionErrandsExecutionDate(NodeRef resolution);

    List<NodeRef> getResolutionClosers(NodeRef resolution);
}
