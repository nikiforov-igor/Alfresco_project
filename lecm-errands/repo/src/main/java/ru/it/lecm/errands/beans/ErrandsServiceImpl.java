package ru.it.lecm.errands.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:43
 */
public class ErrandsServiceImpl extends BaseBean implements ErrandsService {
	private DocumentService documentService;

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public NodeRef getDraftRoot() {
		return  documentService.getDraftRootByType(TYPE_ERRANDS);
	}
}
