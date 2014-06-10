package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentWorkflowCommentsService;

/**
 * User: AIvkin
 * Date: 30.07.13
 * Time: 11:01
 */
public class DocumentWorkflowCommentsWebScriptBean extends BaseWebScript {
	private DocumentWorkflowCommentsService documentWorkflowCommentsService;

	public void setDocumentWorkflowCommentsService(DocumentWorkflowCommentsService documentWorkflowCommentsService) {
		this.documentWorkflowCommentsService = documentWorkflowCommentsService;
	}

	/**
	 * Создание комментария для процесса в документе
	 * @param document документ
	 * @param comment текс комментария
	 * @return созданный объект комментария
	 */
	public ScriptNode createWorkflowComment(ScriptNode document, String comment) {
		ParameterCheck.mandatory("document", document);
		ParameterCheck.mandatory("comment", comment);

		NodeRef commentRef = this.documentWorkflowCommentsService.createWorkflowComment(document.getNodeRef(), comment);
		return new ScriptNode(commentRef, this.serviceRegistry, getScope());
	}
}
