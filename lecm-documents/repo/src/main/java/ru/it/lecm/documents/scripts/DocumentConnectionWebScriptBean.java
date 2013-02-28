package ru.it.lecm.documents.scripts;


import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.documents.beans.DocumentConnectionService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 14:02
 */
public class DocumentConnectionWebScriptBean extends BaseScopableProcessorExtension {
	private DocumentConnectionService documentConnectionService;
	private ServiceRegistry serviceRegistry;
	protected NodeService nodeService;

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Получение корневой папки связей
	 * @return
	 */
	public ScriptNode getRoot() {
		try {
			NodeRef ref = this.documentConnectionService.getConnectionsRootRef();
			return new ScriptNode(ref, this.serviceRegistry, getScope());
		} catch (Exception e) {
			throw new ScriptException("Не удалось получить директорию с уведомлениями активного канала", e);
		}
	}

	public ScriptNode getDefaultConnectionType(String primaryDocumentNodeRef, String connectedDocumentNodeRef) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);
		NodeRef connectedDocumentRef = new NodeRef(connectedDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) && this.nodeService.exists(connectedDocumentRef)) {
			NodeRef defaultConnectionType = this.documentConnectionService.getDefaultConnectionType(primaryDocumentRef, connectedDocumentRef);
			if (defaultConnectionType != null) {
				return new ScriptNode(defaultConnectionType, this.serviceRegistry, getScope());
			}
		}
		return null;
	}

	public Scriptable getAvailableConnectionTypes(String primaryDocumentNodeRef, String connectedDocumentNodeRef) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);
		NodeRef connectedDocumentRef = new NodeRef(connectedDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) && this.nodeService.exists(connectedDocumentRef)) {
			List<NodeRef> availableConnectionType = this.documentConnectionService.getAvailableConnectionTypes(primaryDocumentRef, connectedDocumentRef);
			if (availableConnectionType != null) {
				return createScriptable(availableConnectionType);
			}
		}
		return null;
	}

    public Scriptable getExistConnectionTypes(String primaryDocumentNodeRef, String connectedDocumentNodeRef) {
        ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
        ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);

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

	public Scriptable getAllConnectionTypes() {
		List<NodeRef> allConnectionType = this.documentConnectionService.getAllConnectionTypes();
		if (allConnectionType != null) {
			return createScriptable(allConnectionType);
		}
		return null;
	}

	/**
	 * Возвращает массив, пригодный для использования в веб-скриптах
	 *
	 * @return Scriptable
	 */
	private Scriptable createScriptable(List<NodeRef> refs) {
		Object[] results = new Object[refs.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = new ScriptNode(refs.get(i), this.serviceRegistry, getScope());
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}
}
