package ru.it.lecm.eds.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: dbayandin
 * Date: 31.01.14
 * Time: 12:48
 */
public interface EDSDocumentService {

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

    void sendChildChangeSignal(NodeRef baseDoc);
}
