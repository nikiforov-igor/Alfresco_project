package ru.it.lecm.eds.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.Date;
import java.util.List;

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
	String DOCUMENT_DICTIONARIES_DOC_TYPE_NAMESPACE_URI = "http://www.it.ru/logicECM/document/dictionaries/docType/1.0";

    QName TYPE_EDS_DOCUMENT = QName.createQName(EDS_NAMESPACE_URI, "base");

    QName PROP_CATEGORIES_OF_ATTACHMENTS_TO_SIGN = QName.createQName(DOCUMENT_DICTIONARIES_DOC_TYPE_NAMESPACE_URI, "categories-of-attachments-to-sign");
    QName PROP_NOTE = QName.createQName(EDS_NAMESPACE_URI, "note");
    QName PROP_CONTENT = QName.createQName(EDS_NAMESPACE_URI, "summaryContent");

    QName ASSOC_DOCUMENT_TYPE= QName.createQName(EDS_NAMESPACE_URI, "document-type-assoc");
    QName ASSOC_FILE_REGISTER = QName.createQName(EDS_NAMESPACE_URI, "file-register-assoc");
	QName ASSOC_RECIPIENTS = QName.createQName(EDS_NAMESPACE_URI, "recipients-assoc");
    QName ASSOC_EXECUTOR = QName.createQName(EDS_NAMESPACE_URI, "executor-assoc");
    QName ASSOC_COMPLETION_SIGNAL_SENDER = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "completion-signal-sender-assoc");

	QName PROP_EXECUTION_DATE = QName.createQName(EDS_NAMESPACE_URI, "execution-date");
    QName PROP_IS_EXPIRED = QName.createQName(EDS_NAMESPACE_URI, "is-expired");
    QName PROP_EXECUTOR_TEXT_CONTENT = QName.createQName(EDS_NAMESPACE_URI, "executor-assoc-text-content");
    QName PROP_EXECUTOR_REF = QName.createQName(EDS_NAMESPACE_URI, "executor-assoc-ref");

    QName ASPECT_CHILD_CHANGE_SIGNAL = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "child-change-signal");
	QName PROP_CHILD_CHANGE_SIGNAL_COUNT = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "child-change-signal-count");

	QName ASPECT_CHANGE_DUE_DATE_SIGNAL = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "changeDueDateSignal");
	QName PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_SIZE = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "duedate-shift-size");
	QName PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_LIMITLESS = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "duedate-limitless");
	QName PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_NEW_DATE = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "new-limitation-date");
	QName PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_REASON = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "change-duedate-reason");
    QName PROP_COMPLETION_SIGNAL = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "completion-signal");
    QName PROP_COMPLETION_SIGNAL_REASON = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "completion-signal-reason");
    QName PROP_COMPLETION_SIGNAL_CLOSE_CHILD = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "completion-signal-close-child");
    QName ASPECT_COMPLETION_SIGNAL = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "completion-signal-aspect");

    QName ASPECT_EXECUTION_STATE = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "execution-state-aspect");
    QName PROP_EXECUTION_STATE = QName.createQName(EDS_ASPECTS_NAMESPACE_URI,"execution-state");
    QName PROP_EXECUTION_STATISTICS = QName.createQName(EDS_ASPECTS_NAMESPACE_URI,"execution-statistics");

    QName ASPECT_SIGNED_ON_PAPER = QName.createQName(EDS_ASPECTS_NAMESPACE_URI,"signed-on-paper-aspect");
    QName PROP_SIGNED_ON_PAPER = QName.createQName(EDS_ASPECTS_NAMESPACE_URI,"signed-on-paper");

    enum EXECUTION_STATE{
        IN_PROCESS,COMPLETE,NOT_REQUIRED;

        public static EXECUTION_STATE computeState(boolean allFinal, boolean anyExecuted, boolean inProcess){
            return inProcess ? IN_PROCESS : allFinal && anyExecuted ? COMPLETE : NOT_REQUIRED;
        }
    }

    QName ASPECT_BASE_DOCUMENT_TYPE = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "base-document-type-aspect");
    QName PROP_BASE_DOCUMENT_TYPE = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "base-document-type");

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
     * Отправка сигнала об изменении срока
     * @param doc Документ, которому отправляется сигнал
     * @param shiftSize Размер сдвига срока
     * @param limitless Без срока
     * @param newDate Новый срок
     * @param reason Причина изменения срока
     */
    void sendChangeDueDateSignal(NodeRef doc, Long shiftSize, Boolean limitless, Date newDate, String reason);

    /**
     * Сброс сигнала об изменении срока
     * @param doc Документ, у которого сбрасывается сигнал
     */
    void resetChangeDueDateSignal(NodeRef doc);

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
     * Конвертация относительного срока в дату
     * @param radio значение переключателя
     * @param date дата
     * @param daysCount количество дней
     * @param daysType тип дней
     * @return сконвертированный относительный срок в дату
     */
    Date convertComplexDate(String radio, Date date, String daysType, Integer daysCount);

    /**
     * Отправка сигнала о необходимости завершения
     * @param document документ, которому направляется сигнал
     * @param reason причина сигнала
     * @param signalSender отправитель сигнала
     */
    void sendCompletionSignal(NodeRef document, String reason, NodeRef signalSender);

    /**
     * Сброс сигнала завершения
     * @param document документ
     */
    void resetCompletionSignal(NodeRef document);

    /**
     * Подписан на бумажном носителе
     */
    boolean isSignedOnPaper(NodeRef document);

    /**
     * Получение категорий подписываемых вложений
     * @param documentRef NodeRef документа
     * @param documentTypeAssocName ассоциация на справочник типа документа, например, "lecm-eds-document:document-type-assoc'
     * @return список nodeRef категорий подписываемых вложений
     */
    public List<NodeRef> getCategoriesToSign(NodeRef documentRef, String documentTypeAssocName);
}
