package ru.it.lecm.ord;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.ord.api.ORDDocumentService;

/**
 *
 * @author dbayandin
 */
public class ORDDocumentServiceImpl extends BaseBean implements ORDDocumentService{

	@Override
	public String getDocumentURL(final NodeRef documentRef) {
		String presentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);
		return wrapperLink(documentRef, presentString, BaseBean.DOCUMENT_LINK_URL);
	}
	
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
	
}
