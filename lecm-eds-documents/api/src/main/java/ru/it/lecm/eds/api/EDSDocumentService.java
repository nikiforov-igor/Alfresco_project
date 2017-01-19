package ru.it.lecm.eds.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.Date;

/**
 * User: dbayandin
 * Date: 31.01.14
 * Time: 12:48
 */
public interface EDSDocumentService {
    String COMPLEX_DATE_RADIO_DAYS = "DAYS";
    String COMPLEX_DATE_RADIO_DATE = "DATE";
    String COMPLEX_DATE_RADIO_LIMITLESS = "LIMITLESS";

    String COMPLEX_DATE_DAYS_WORK = "WORK";
    String COMPLEX_DATE_DAYS_CALENDAR = "CALENDAR";

    String EDS_NAMESPACE_URI = "http://www.it.ru/logicECM/eds-document/1.0";
	String EDS_ASPECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/eds-document/aspects/1.0";

    QName TYPE_EDS_DOCUMENT = QName.createQName(EDS_NAMESPACE_URI, "document");

    QName PROP_NOTE = QName.createQName(EDS_NAMESPACE_URI, "note");
    QName PROP_CONTENT = QName.createQName(EDS_NAMESPACE_URI, "summaryContent");

    QName ASSOC_DOCUMENT_TYPE= QName.createQName(EDS_NAMESPACE_URI, "document-type-assoc");
    QName ASSOC_FILE_REGISTER = QName.createQName(EDS_NAMESPACE_URI, "file-register-assoc");
	QName ASSOC_RECIPIENTS = QName.createQName(EDS_NAMESPACE_URI, "recipients-assoc");

	QName PROP_EXECUTION_DATE = QName.createQName(EDS_NAMESPACE_URI, "execution-date");

	QName PROP_CHILD_CHANGE_SIGNAL_COUNT = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "child-change-signal-count");

    /**
     * Отправка сигнала об изменении дочерних документов
     * @param baseDoc Документ, которому отправляется сигнал
     */
    void sendChildChangeSignal(NodeRef baseDoc);

    /**
     * Сброс сигнала об изменении дочерних документов
     * @param baseDoc Документ, у которого сбрасывается сигнал
     */
    void resetChildChangeSignal(NodeRef baseDoc);

    /**
     * Формирование настроек срока в текстовом виде
     * @param radio значение переключателя
     * @param date дата
     * @param daysCount количество дней
     * @param daysType тип дней
     * @return настройки срока в текстовом виде
     */
    String getComplexDateText(String radio, Date date, String daysType, Integer daysCount);

    /**
     * Формирование настроек срока в текстовом виде
     * @param radio значение переключателя
     * @param date дата
     * @param daysCount количество дней
     * @param daysType тип дней
     * @return настройки срока в текстовом виде
     */
    Date convertComplexDate(String radio, Date date, String daysType, Integer daysCount);
}
