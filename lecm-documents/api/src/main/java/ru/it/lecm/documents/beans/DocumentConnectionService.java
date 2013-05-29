package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 13:54
 */
public interface DocumentConnectionService {
	public static final String DOCUMENT_CONNECTIONS_ROOT_NAME = "Связи";
	public static final String DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME = "Типы связи";

	public static final String DOCUMENT_CONNECTIONS_NAMESPACE_URI = "http://www.it.ru/lecm/org/connection/1.0";
	public static final String DOCUMENT_CONNECTIONS_ASPECT_NAMESPACE_URI = "http://www.it.ru/lecm/connect/aspects/1.0";
	public static final String DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI = "http://www.it.ru/lecm/org/connection/types/1.0";

	public static final QName TYPE_CONNECTION = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "connection");
	public static final QName TYPE_CONNECTION_TYPE = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "connection-type");
	public static final QName TYPE_AVAILABLE_CONNECTION_TYPES = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "available-connection-type");

	public static final QName ASSOC_PRIMARY_DOCUMENT = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "primary-document-assoc");
	public static final QName ASSOC_CONNECTED_DOCUMENT = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "connected-document-assoc");
	public static final QName ASSOC_CONNECTION_TYPE = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "connection-type-assoc");

	public static final QName PROP_IS_SYSTEM = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "is-system");
	public static final QName PROP_PRIMARY_DOCUMENT_REF = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "primary-document-assoc-ref");
	public static final QName PROP_CONNECTED_DOCUMENT_REF = QName.createQName(DOCUMENT_CONNECTIONS_NAMESPACE_URI, "connected-document-assoc-ref");

	public static final QName PROP_CONNECTION_TYPE_CODE = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "code");

	public static final QName PROP_PRIMARY_DOCUMENT_TYPE = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "primary-document-type");
	public static final QName PROP_CONNECTED_DOCUMENT_TYPE = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "connected-document-type");

	public static final QName ASSOC_DEFAULT_CONNECTION_TYPE = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "default-connection-type-assoc");
	public static final QName ASSOC_NOT_AVAILABLE_CONNECTION_TYPES = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "not-available-connection-types-assoc");
	public static final QName ASSOC_RECOMMENDED_CONNECTION_TYPES = QName.createQName(DICTIONARY_CONNECTION_TYPES_NAMESPACE_URI, "recommended-connection-types-assoc");

	public static final QName ASPECT_HAS_CONNECTED_DOCUMENTS = QName.createQName(DOCUMENT_CONNECTIONS_ASPECT_NAMESPACE_URI, "has-connected-documents");

    public static final QName PROP_CONNECTIONS_LIST = QName.createQName(DOCUMENT_CONNECTIONS_ASPECT_NAMESPACE_URI, "connections-list");
    public static final QName PROP_CONNECTIONS_WITH_LIST = QName.createQName(DOCUMENT_CONNECTIONS_ASPECT_NAMESPACE_URI, "connections-with-list");

	/**
	 * Получение папки со связями для документа
	 * @param documentRef Ссылка на документ
	 * @return Ссылка на папку
	 */
	public NodeRef getRootFolder(final NodeRef documentRef);

	/**
	 * Получение типа связи по умолчанию для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentRef Ссылка на связанный объект
	 * @return ссылка на элемент справочника "Типы связи"
	 */
	public NodeRef getDefaultConnectionType(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef);

	/**
	 * Получение рекоммендуемых типов связи для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentRef Ссылка на связанный объект
	 * @return список ссылок на элементы справочника "Типы связи"
	 */
	public List<NodeRef> getRecommendedConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef);

	/**
	 * Получение доступный типов связи для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentRef Ссылка на связанный объект
	 * @return список ссылок на элементы справочника "Типы связи"
	 */
	public List<NodeRef> getAvailableConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef);

	/**
	 * Получение полного списка типов связи
	 * @return список ссылок на элементы справочника "Типы связи"
	 */
	public List<NodeRef> getAllConnectionTypes();

    /**
     * Получение существующих типов связи между двумя документами
     * @param primaryDocumentRef Ссылка на исходный объект
     * @param connectedDocumentRef Ссылка на связанный объект
     * @return список ссылок на элементы справочника "Типы связи"
     */
    public List<NodeRef> getExistsConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef);

	/**
	 * Получение документов, связанных с документом
	 * @param documentRef Ссылка на документ
	 * @return Список сслыок на связи
	 */
	public List<NodeRef> getConnectionsWithDocument(NodeRef documentRef);

	/**
	 * Создание связи
	 * @param primaryDocumentNodeRef Ссылка на исходный документ
	 * @param connectedDocumentNodeRef  Ссылка на целевой документ
	 * @param typeNodeRef Ссылка на тип связи
	 * @return Ссылка на созданную связь
	 */
	public NodeRef createConnection(NodeRef primaryDocumentNodeRef, NodeRef connectedDocumentNodeRef, NodeRef typeNodeRef, boolean isSystem);

	/**
	 * Удаление связи
	 * @param nodeRef Ссылка на связь
	 */
	public void deleteConnection(NodeRef nodeRef);

	public boolean isConnection(NodeRef ref);

	public boolean isConnectionType(NodeRef ref);
}
