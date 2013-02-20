package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 13:54
 */
public interface DocumentConnectionService {
	public static final String DOCUMENT_CONNECTIONS_ROOT_NAME = "Связи";

	public static final String DOCUMENT_CONNECTIONS_NAMESPACE_URI = "http://www.it.ru/lecm/org/connection/1.0";
	public static final String DOCUMENT_CONNECTIONS_ASPECT_NAMESPACE_URI = "http://www.it.ru/lecm/connect/aspects/1.0";

	public static final QName TYPE_CONNECTION = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "connection");

	public static final QName ASSOC_PRIMARY_DOCUMENT = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "primary-document-assoc");
	public static final QName ASSOC_CONNECTED_DOCUMENT = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "connected-document-assoc");

	public static final QName ASPECT_HAS_CONNECTED_DOCUMENTS = QName.createQName(DOCUMENT_CONNECTIONS_ASPECT_NAMESPACE_URI, "has-connected-documents");

	/**
	 * Получение директории подписки.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	NodeRef getConnectionsRootRef();
}
