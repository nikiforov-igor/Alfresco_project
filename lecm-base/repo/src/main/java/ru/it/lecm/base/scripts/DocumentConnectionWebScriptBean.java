package ru.it.lecm.base.scripts;


import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.DocumentConnectionService;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 14:02
 */
public class DocumentConnectionWebScriptBean extends BaseScopableProcessorExtension {
	private DocumentConnectionService documentConnectionService;
	private ServiceRegistry serviceRegistry;

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
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
}
