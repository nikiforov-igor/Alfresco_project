package ru.it.lecm.resolutions.api;

import org.alfresco.service.namespace.QName;

import java.util.Date;

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

    String RESOLUTION_NAMESPACE_URI = "http://www.it.ru/logicECM/resolutions/1.0";

    QName TYPE_RESOLUTION_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "document");

    QName ASSOC_BASE_DOCUMENT = QName.createQName(RESOLUTION_NAMESPACE_URI, "base-document-assoc");

    Date calculateResolutionExecutionDate(String radio, Integer days, String daysType, Date date);
}
