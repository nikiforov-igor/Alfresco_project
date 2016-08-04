package ru.it.lecm.documents.scripts;


import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 14:02
 */
public class DocumentConnectionWebScriptBean extends BaseWebScript {
	private DocumentConnectionService documentConnectionService;
	private DocumentService documentService;
	protected NodeService nodeService;
	private NamespaceService namespaceService;
	private LecmTransactionHelper lecmTransactionHelper;

        public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
            this.lecmTransactionHelper = lecmTransactionHelper;
        }


	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	/**
	 * Получение папки для связей в документе
	 * @param documentNodeRef документ
	 * @return папка со связями
	 */
	public ScriptNode getRootFolder(String documentNodeRef) {
        org.alfresco.util.ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        final NodeRef documentRef = new NodeRef(documentNodeRef);

        if (this.nodeService.exists(documentRef)) {
            //TODO : Вынести создание rootFolder в машину состояний
            //Сейчас папка связей создаётся здесь, иначе возникают проблемы с созданием связей: нет parentRef для вызова формы создания связи

            NodeRef connectionsRoot = this.documentConnectionService.getRootFolder(documentRef);

            if (connectionsRoot != null) {
                return new ScriptNode(connectionsRoot, this.serviceRegistry, getScope());
            }
        }
        return null;
    }

	/**
	 * Получение типа связи по умолчанию. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentNodeRef nodeRef основного документа
	 * @param connectedDocumentRefOrType nodeRef связываемого документа, или его тип
	 * @return тип связи по умолчанию
	 */
	public ScriptNode getDefaultConnectionType(String primaryDocumentNodeRef, String connectedDocumentRefOrType) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentRefOrType", connectedDocumentRefOrType);
		NodeRef connectedDocumentRef = null, defaultConnectionType = null;
		QName connectedDocumentType = null;

		if (NodeRef.isNodeRef(connectedDocumentRefOrType)) {
			connectedDocumentRef = new NodeRef(connectedDocumentRefOrType);
		} else {
			connectedDocumentType = QName.createQName(connectedDocumentRefOrType, namespaceService);
		}

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) &&
				(connectedDocumentRef != null && this.nodeService.exists(connectedDocumentRef))) {
			defaultConnectionType = this.documentConnectionService.getDefaultConnectionType(primaryDocumentRef, connectedDocumentRef);
		} else if (this.nodeService.exists(primaryDocumentRef) && connectedDocumentType != null) {
			defaultConnectionType = this.documentConnectionService.getDefaultConnectionType(primaryDocumentRef, connectedDocumentType);
		}

		if (defaultConnectionType != null) {
			return new ScriptNode(defaultConnectionType, this.serviceRegistry, getScope());
		} else {
			return null;
		}
	}

	/**
	 * Получение рекомендуемых типов связи. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentNodeRef nodeRef основного документа
	 * @param connectedDocumentRefOrType nodeRef связываемого документа, или его тип
	 * @return массив рекомендуемых типов связи
	 */
	public Scriptable getRecommendedConnectionTypes(String primaryDocumentNodeRef, String connectedDocumentRefOrType) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentRefOrType", connectedDocumentRefOrType);

		NodeRef connectedDocumentRef = null;
		QName connectedDocumentType = null;
		List<NodeRef> recommendedConnectionTypes = null;

		if (NodeRef.isNodeRef(connectedDocumentRefOrType)) {
			connectedDocumentRef = new NodeRef(connectedDocumentRefOrType);
		} else {
			connectedDocumentType = QName.createQName(connectedDocumentRefOrType, namespaceService);
		}

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) &&
				(connectedDocumentRef != null && this.nodeService.exists(connectedDocumentRef))) {
			recommendedConnectionTypes = this.documentConnectionService.getRecommendedConnectionTypes(primaryDocumentRef, connectedDocumentRef);
		} else if (this.nodeService.exists(primaryDocumentRef) && connectedDocumentType != null) {
			recommendedConnectionTypes = this.documentConnectionService.getRecommendedConnectionTypes(primaryDocumentRef, connectedDocumentType);
		}

		if (recommendedConnectionTypes != null) {
			return createScriptable(recommendedConnectionTypes);
		} else {
			return null;
		}
	}

	/**
	 * Получение доступных типов связи. Берётся из справочника "Доступные типы связи"
	 * @param primaryDocumentNodeRef nodeRef основного документа
	 * @param connectedDocumentRefOrType nodeRef связываемого документа, или его тип
	 * @return массив доступных типов связи
	 */
	public Scriptable getAvailableConnectionTypes(String primaryDocumentNodeRef, String connectedDocumentRefOrType) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentRefOrType);

		NodeRef connectedDocumentRef = null;
		QName connectedDocumentType = null;
		List<NodeRef> availableConnectionType = null;

		if (NodeRef.isNodeRef(connectedDocumentRefOrType)) {
			connectedDocumentRef = new NodeRef(connectedDocumentRefOrType);
		} else {
			connectedDocumentType = QName.createQName(connectedDocumentRefOrType, namespaceService);
		}

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) &&
				(connectedDocumentRef != null && this.nodeService.exists(connectedDocumentRef))) {
			availableConnectionType = this.documentConnectionService.getAvailableConnectionTypes(primaryDocumentRef, connectedDocumentRef);
		} else if (this.nodeService.exists(primaryDocumentRef) && connectedDocumentType != null) {
			availableConnectionType = this.documentConnectionService.getAvailableConnectionTypes(primaryDocumentRef, connectedDocumentType);
		}

		if (availableConnectionType != null) {
			return createScriptable(availableConnectionType);
		} else {
			return null;
		}

	}

	/**
	 * Получение существующих типов связи между двумя документами
	 * @param primaryDocumentNodeRef nodeRef основного документа
	 * @param connectedDocumentNodeRef nodeRef связываемого документа
	 * @return массив существующих типов связи
	 */
	public Scriptable getExistConnectionTypes(String primaryDocumentNodeRef, String connectedDocumentNodeRef) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);

		if (!NodeRef.isNodeRef(connectedDocumentNodeRef)) {
			return Context.getCurrentContext().newArray(getScope(), 0);
		}

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);
		NodeRef connectedDocumentRef = new NodeRef(connectedDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) && this.nodeService.exists(connectedDocumentRef)) {
			List<NodeRef> existConnectionType = this.documentConnectionService.getExistsConnectionTypes(primaryDocumentRef, connectedDocumentRef);
			if (existConnectionType != null) {
				return createScriptable(existConnectionType);
			}
		}
		return null;
	}

	/**
	 * Получение связей документа
	 * @param documentNodeRef nodeRef документа
	 * @return массив связей
	 */
	public Scriptable getConnections(String documentNodeRef) {
		ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
		NodeRef documentRef = new NodeRef(documentNodeRef);
		if (this.nodeService.exists(documentRef)) {
			List<NodeRef> connections = this.documentConnectionService.getConnections(documentRef);
			return createScriptable(connections);
		}
		return null;
	}

	/**
	 * Получение связей на документ документа
	 * @param documentNodeRef nodeRef документа
	 * @return массив связей
	 */
	public Scriptable getConnectionsWithDocument(String documentNodeRef) {
		ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
		NodeRef documentRef = new NodeRef(documentNodeRef);
		if (this.nodeService.exists(documentRef)) {
			List<NodeRef> connections = this.documentConnectionService.getConnectionsWithDocument(documentRef);
			return createScriptable(connections);
		}
		return null;
	}

	/**
	 * Получение связей на документ документа
	 * @param document документ
	 * @param connectionTypeCode Тип связи
	 * @return массив связей
	 */
	public Scriptable getConnectionsWithDocument(ScriptNode document, String connectionTypeCode) {
		ParameterCheck.mandatory("document", document);
		ParameterCheck.mandatory("connectionTypeCode", connectionTypeCode);
		if (document != null) {
			List<NodeRef> connections = this.documentConnectionService.getConnectionsWithDocument(document.getNodeRef(), connectionTypeCode);
			return createScriptable(connections);
		}
 		return null;
	}

	/**
	 * Получение связей на документ документа
	 * @param documentNodeRef nodeRef документа
	 * @param checkPermission проверятьли доступность связанного документа
	 * @return массив связей
	 */
	public Scriptable getConnectionsWithDocument(String documentNodeRef, Boolean checkPermission) {
		ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
		NodeRef documentRef = new NodeRef(documentNodeRef);
		if (this.nodeService.exists(documentRef)) {
			List<NodeRef> connections = this.documentConnectionService.getConnectionsWithDocument(documentRef, checkPermission);
			return createScriptable(connections);
		}
		return null;
	}

	public Boolean hasConnectionsWithDocument(String documentNodeRef, Boolean checkPermission) {
		ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
		NodeRef documentRef = new NodeRef(documentNodeRef);
		if (this.nodeService.exists(documentRef)) {
			return this.documentConnectionService.hasConnectionsWithDocument(documentRef, checkPermission);
		}
		return false;
	}

	/**
	 * Создание связи
	 * @param primaryDocumentNodeRef nodeRef основного документа
	 * @param connectedDocumentNodeRef nodeRef связываемого документа
	 * @param typeNodeRef nodeRef типа связи
	 * @return созданная связь
	 */
	public ScriptNode createConnection(String primaryDocumentNodeRef, String connectedDocumentNodeRef, String typeNodeRef) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);
		ParameterCheck.mandatory("typeNodeRef", typeNodeRef);

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);
		NodeRef connectedDocumentRef = new NodeRef(connectedDocumentNodeRef);
		NodeRef typeRef = new NodeRef(typeNodeRef);
		if (this.nodeService.exists(primaryDocumentRef) && this.documentService.isDocument(primaryDocumentRef) &&
				this.nodeService.exists(connectedDocumentRef) && this.documentService.isDocument(connectedDocumentRef) &&
				this.nodeService.exists(typeRef) && this.documentConnectionService.isConnectionType(typeRef)) {

			NodeRef connectionsRef = this.documentConnectionService.createConnection(primaryDocumentRef, connectedDocumentRef, typeRef, false);
			return new ScriptNode(connectionsRef, this.serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * Создание связи
	 * @param primaryDocument основной документа
	 * @param connectedDocument связываемый документ
	 * @param typeDictionaryElementCode код типа связи
	 * @param isSystem является ли связь системной
	 * @return созданная связь
	 */
	public ScriptNode createConnection(ScriptNode primaryDocument, ScriptNode connectedDocument, String typeDictionaryElementCode, boolean isSystem) {
		return createConnection(primaryDocument, connectedDocument,typeDictionaryElementCode, isSystem, false);
	}

    public ScriptNode createConnection(ScriptNode primaryDocument, ScriptNode connectedDocument, String typeDictionaryElementCode, boolean isSystem, boolean doNotCheckPermission) {
        NodeRef connectionsRef = this.documentConnectionService.createConnection(primaryDocument.getNodeRef(), connectedDocument.getNodeRef(), typeDictionaryElementCode, isSystem, doNotCheckPermission);
        return new ScriptNode(connectionsRef, this.serviceRegistry, getScope());
    }
	/**
	 * Удаление связи
	 * @param nodeRef nodeRef связи
	 * @return сообщение о статусе удаления
	 */
	public String deleteConnection(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		NodeRef ref = new NodeRef(nodeRef);
		if (this.nodeService.exists(ref) && this.documentConnectionService.isConnection(ref)) {
			this.documentConnectionService.deleteConnection(ref);
			return "Success delete";
		}
		return "Failure: node not found";
	}

	/**
	 * Получение связанных документов
	 * @param document документ
	 * @param connectionTypeCode код типа связи
	 * @param connectedDocumentType тип связанных документов
	 * @return массив связанных документов
	 */
	public Scriptable getConnectedDocuments(ScriptNode document, String connectionTypeCode, String connectedDocumentType) {
		ParameterCheck.mandatory("document", document);
		ParameterCheck.mandatory("connectionTypeCode", connectionTypeCode);

		QName documentTypeQName = QName.createQName(connectedDocumentType, serviceRegistry.getNamespaceService());
		List<NodeRef> connectedDocuments = this.documentConnectionService.getConnectedDocuments(document.getNodeRef(), connectionTypeCode, documentTypeQName);
		if (connectedDocuments != null) {
			return createScriptable(connectedDocuments);
		}
		return null;
	}

	/**
	 * Получение связанных документов
	 * @param document документ
	 * @param connectionTypeCode код типа связи
	 * @param connectedDocumentType тип связанных документов
	 * @return массив связанных документов
	 */
	public Scriptable getConnectedDocuments(ScriptNode document, String connectionTypeCode, String connectedDocumentType, boolean onlySystem) {
		ParameterCheck.mandatory("document", document);
		ParameterCheck.mandatory("connectionTypeCode", connectionTypeCode);

		QName documentTypeQName = QName.createQName(connectedDocumentType, serviceRegistry.getNamespaceService());
		List<NodeRef> connectedDocuments = this.documentConnectionService.getConnectedDocuments(document.getNodeRef(), connectionTypeCode, documentTypeQName, onlySystem);
		if (connectedDocuments != null) {
			return createScriptable(connectedDocuments);
		}
		return null;
	}
    /**
     * Получение связанных любой связью документов любого типа
     * @param document документ
     * @return массив связанных документов
     */
    public Scriptable getConnectedWithDocument(ScriptNode document, boolean isSystem) {
        ParameterCheck.mandatory("document", document);

        List<NodeRef> connectedDocuments = documentConnectionService.getConnectedWithDocument(document.getNodeRef(), isSystem);
        if (connectedDocuments != null) {
            return createScriptable(connectedDocuments);
        }
        return null;
    }
    
    /**
     * Получение документов связанных с документом
     * @param document документ
     * @return массив связанных документов
     */
    public Scriptable getConnectedWithDocument(ScriptNode document, String connectionTypeCode, String  sourceDocumentType) {
        ParameterCheck.mandatory("document", document);
        ParameterCheck.mandatory("connectionTypeCode", connectionTypeCode);
        
        QName documentTypeQName = QName.createQName(sourceDocumentType, serviceRegistry.getNamespaceService());

        List<NodeRef> connectedDocuments = documentConnectionService.getConnectedWithDocument(document.getNodeRef(), connectionTypeCode, documentTypeQName);
        if (connectedDocuments != null) {
            return createScriptable(connectedDocuments);
        }
        return null;
    }
}
