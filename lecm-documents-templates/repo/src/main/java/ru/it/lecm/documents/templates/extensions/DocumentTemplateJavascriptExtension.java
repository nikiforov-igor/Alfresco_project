package ru.it.lecm.documents.templates.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.templates.api.DocumentTemplateService;

/**
 *
 * @author vmalygin
 */
public class DocumentTemplateJavascriptExtension extends BaseWebScript {

	private DocumentTemplateService documentTemplateService;

	public void setDocumentTemplateService(DocumentTemplateService documentTemplateService) {
		this.documentTemplateService = documentTemplateService;
	}

	public ScriptNode getDocumentTemplateFolder() {
		return new ScriptNode(documentTemplateService.getDocumentTemplateFolder(), serviceRegistry, getScope());
	}
}
