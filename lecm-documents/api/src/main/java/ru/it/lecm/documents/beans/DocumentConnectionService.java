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

    public static final String DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE = "onBasis";

    // ALF-1583
    // При добавлении поручения через блок "Задачи" появляется сообщение "Ваши изменения не удалось сохранить"
    // В транзакцию добавляется переменная DocumentConnectionService.DO_NOT_CHECK_PERMISSION_CREATE_DOCUMENT_LINKS,
    // позволяющая отключить прооверку прав на создание связи к документу.
    // Переменная устанавливается в методе ru.it.lecm.documents.beans.DocumentConnectionServiceImpl.createConnection()
    // Проверяется в ru.it.lecm.documents.policy.DocumentConnectionPolicy.beforeCreateNode()
    public static final String DO_NOT_CHECK_PERMISSION_CREATE_DOCUMENT_LINKS = "DO_NOT_CHECK_PERMISSION_CREATE_DOCUMENT_LINKS";

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
	 * Получение типа связи по умолчанию для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentType Тип связанного объекта
	 * @return ссылка на элемент справочника "Типы связи"
	 */
	public NodeRef getDefaultConnectionType(NodeRef primaryDocumentRef, QName connectedDocumentType);

	/**
	 * Получение рекоммендуемых типов связи для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentRef Ссылка на связанный объект
	 * @return список ссылок на элементы справочника "Типы связи"
	 */
	public List<NodeRef> getRecommendedConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef);

	/**
	 * Получение рекоммендуемых типов связи для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentType Тип связанного объекта
	 * @return список ссылок на элементы справочника "Типы связи"
	 */
	public List<NodeRef> getRecommendedConnectionTypes(NodeRef primaryDocumentRef, QName connectedDocumentType);

	/**
	 * Получение доступный типов связи для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentRef Ссылка на связанный объект
	 * @return список ссылок на элементы справочника "Типы связи"
	 */
	public List<NodeRef> getAvailableConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef);

	/**
	 * Получение доступный типов связи для документов. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentRef Ссылка на исходный объект
	 * @param connectedDocumentType Тип связанного объекта
	 * @return список ссылок на элементы справочника "Типы связи"
	 */
	public List<NodeRef> getAvailableConnectionTypes(NodeRef primaryDocumentRef, QName connectedDocumentType);

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
	 * Получение связанных документов
	 * @param documentRef Ссылка на документ
	 * @return Список ссылок на связи
	 */
	public List<NodeRef> getConnections(NodeRef documentRef);

	/**
	 * Получение документов, связанных с документом
	 * @param documentRef Ссылка на документ
	 * @return Список ссылок на связи
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
	 * Создание связи
	 * @param primaryDocumentNodeRef Ссылка на исходный документ
	 * @param connectedDocumentNodeRef  Ссылка на целевой документ
	 * @param typeDictionaryElementCode Код элемента справочника для типа связи
	 * @return Ссылка на созданную связь
	 */
	public NodeRef createConnection(NodeRef primaryDocumentNodeRef, NodeRef connectedDocumentNodeRef, String typeDictionaryElementCode, boolean isSystem);

    /**
     * Создание связи
     * @param primaryDocumentNodeRef Ссылка на исходный документ
     * @param connectedDocumentNodeRef  Ссылка на целевой документ
     * @param typeNodeRef Ссылка на тип связи
     * @param isSystem создавать системную связь
     * @param doNotCheckPermission не проверять наличие прав на создание связей у текущего пользователя
     * @return Ссылка на созданную связь
     */
    public NodeRef createConnection(NodeRef primaryDocumentNodeRef, NodeRef connectedDocumentNodeRef, NodeRef typeNodeRef, boolean isSystem, boolean doNotCheckPermission);

	/**
	 * Получение связанных документов
	 * @param documentRef Ссылка на документ
	 * @param connectionTypeCode Код типа связи
	 * @param connectedDocumentType Тип связанного документа
	 * @return Связанные документы
	 */
	public List<NodeRef> getConnectedDocuments(NodeRef documentRef, String connectionTypeCode, QName connectedDocumentType);

	/**
	 * Удаление связи
	 * @param nodeRef Ссылка на связь
	 */
	public void deleteConnection(NodeRef nodeRef);

	public boolean isConnection(NodeRef ref);

	public boolean isConnectionType(NodeRef ref);

	List<NodeRef> getNotAvailableConnectionTypes(NodeRef primaryDocumentRef, NodeRef connectedDocumentRef);
	List<NodeRef> getNotAvailableConnectionTypes(NodeRef primaryDocumentRef, QName connectedDocumentType);
}
