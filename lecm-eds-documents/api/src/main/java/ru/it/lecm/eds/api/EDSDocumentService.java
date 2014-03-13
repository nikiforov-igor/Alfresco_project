package ru.it.lecm.eds.api;

import org.alfresco.service.namespace.QName;

/**
 * User: dbayandin
 * Date: 31.01.14
 * Time: 12:48
 */
public interface EDSDocumentService {

    public static final String EDS_NAMESPACE_URI = "http://www.it.ru/logicECM/eds-document/1.0";
	public static final String EDS_ASPECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/eds-document/aspects/1.0";

    public static final QName TYPE_EDS_DOCUMENT = QName.createQName(EDS_NAMESPACE_URI, "document");

    public static final QName PROP_NOTE = QName.createQName(EDS_NAMESPACE_URI, "note");
    public static final QName PROP_CONTENT = QName.createQName(EDS_NAMESPACE_URI, "summaryContent");

    public static final QName ASSOC_DOCUMENT_TYPE= QName.createQName(EDS_NAMESPACE_URI, "document-type-assoc");
    public static final QName ASSOC_FILE_REGISTER = QName.createQName(EDS_NAMESPACE_URI, "file-register-assoc");
	public static final QName ASSOC_RECIPIENTS = QName.createQName(EDS_NAMESPACE_URI, "recipients-assoc");

	public static final QName PROP_EXECUTION_DATE = QName.createQName(EDS_NAMESPACE_URI, "execution-date");

	public static final QName ASSOC_REGISTRARS = QName.createQName(EDS_ASPECTS_NAMESPACE_URI, "registrar-assoc");

}
