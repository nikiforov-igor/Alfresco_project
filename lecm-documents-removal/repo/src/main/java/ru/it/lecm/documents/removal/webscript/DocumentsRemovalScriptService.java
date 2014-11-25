package ru.it.lecm.documents.removal.webscript;

import org.alfresco.repo.jscript.ScriptNode;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.removal.DocumentsRemovalService;

/**
 *
 * @author azinovin
 */
public class DocumentsRemovalScriptService extends BaseWebScript {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DocumentsRemovalScriptService.class);

	private DocumentsRemovalService documentsRemovalService;

	public void setDocumentsRemovalService(DocumentsRemovalService documentsRemovalService) {
		this.documentsRemovalService = documentsRemovalService;
	}

    public void purgeDraft(ScriptNode document) {
        documentsRemovalService.purgeDraft(document.getNodeRef());
    }

}
