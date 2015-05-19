package ru.it.lecm.workflow;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vlevin
 */
public class DocumentInfo extends BaseBean {

	private final NodeRef documentRef;
	private final NodeRef initiatorRef;
	private String documentLink;
	private final static Logger logger = LoggerFactory.getLogger(DocumentInfo.class);

	public DocumentInfo(final NodeRef bpmPackage, OrgstructureBean orgstructureService, DocumentService documentService, NodeService nodeService, ServiceRegistry serviceRegistry) {
		this.nodeService = nodeService;
		this.serviceRegistry = serviceRegistry;
		documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		documentLink = "<a href=\"javascript:void(0);\"></a>";
		if (documentRef != null) {
			String presentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);
			documentLink = wrapperLink(documentRef, presentString, documentService.getDocumentUrl(documentRef));
			String creator = (String) nodeService.getProperty(documentRef, ContentModel.PROP_CREATOR);
			initiatorRef = orgstructureService.getEmployeeByPerson(creator);
		} else {
			logger.warn("Can't wrap document as link, because there is no any document in bpm:package.");
			initiatorRef = null;
		}

	}

	public NodeRef getDocumentRef() {
		return documentRef;
	}

	public String getDocumentLink() {
		return documentLink;
	}

	public NodeRef getInitiatorRef() {
		return initiatorRef;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
