package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;

/**
 *
 * @author apalm
 */
public class DocumentGlobalSettingsWebScriptBean extends BaseWebScript {

	private static final Logger logger = LoggerFactory.getLogger(DocumentGlobalSettingsWebScriptBean.class);
	
	private DocumentGlobalSettingsService documentGlobalSettingsService;

	public void setDocumentGlobalSettingsService(DocumentGlobalSettingsService documentGlobalSettingsService) {
		this.documentGlobalSettingsService = documentGlobalSettingsService;
	}
	
	public ScriptNode getSettingsNode() {
		return new ScriptNode(documentGlobalSettingsService.getSettingsNode(), serviceRegistry, getScope());
	}
	
    /**
     * Проверка, скрывать ли свойства для получателей
     */
    public Boolean isHidePropsForRecipients() {
		return documentGlobalSettingsService.isHideProperties();
	}
	
	public String getLinksViewMode() {
		return documentGlobalSettingsService.getLinksViewMode();
	}
}
