package ru.it.lecm.notifications.template;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.documents.beans.DocumentService;

/**
 *
 * @author vkuprin
 */
public class DocumentImpl extends CMObjectImpl implements Document {

	
	
	private final DocumentService documentService;

	public DocumentImpl(NodeRef ref, ApplicationContext applicationContext) {
		super(ref, applicationContext);
		this.documentService = applicationContext.getBean("documentService", DocumentService.class);
	}

	@Override
	public String getPresentString() {
		return nodeService.getProperty(nodeRef, DocumentService.PROP_PRESENT_STRING).toString();
	}

	@Override
	public String getViewUrl() {
		return documentService.getDocumentUrl(nodeRef)+"?nodeRef=" + nodeRef.toString();
	}
	
	@Override
	public String wrapAsLink() {
		return documentService.wrapAsDocumentLink(nodeRef);
	}
	
}
