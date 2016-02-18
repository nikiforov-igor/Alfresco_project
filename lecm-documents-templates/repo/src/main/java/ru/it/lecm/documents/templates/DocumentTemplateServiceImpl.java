package ru.it.lecm.documents.templates;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.templates.api.DocumentTemplateService;

/**
 *
 * @author vmalygin
 */
public class DocumentTemplateServiceImpl extends BaseBean implements DocumentTemplateService {

	public final static String DOCUMENT_TEMPLATE_FOLDER_ID = "DOCUMENT_TEMPLATE_FOLDER_ID";

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(DOCUMENT_TEMPLATE_FOLDER_ID);
	}

	@Override
	public NodeRef getDocumentTemplateFolder() {
		return getServiceRootFolder();
	}

	public void init() {

	}
}
