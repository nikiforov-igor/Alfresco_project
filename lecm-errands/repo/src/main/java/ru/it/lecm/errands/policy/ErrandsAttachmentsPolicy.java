package ru.it.lecm.errands.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.errands.ErrandsService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 15.07.13
 * Time: 10:16
 */
public class ErrandsAttachmentsPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
	final static protected Logger logger = LoggerFactory.getLogger(ErrandsConnectionPolicy.class);

	private PolicyComponent policyComponent;
	private DocumentAttachmentsService documentAttachmentsService;
	private NodeService nodeService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	final public void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_TEMP_ATTACHMENTS, new JavaBehaviour(this, "onCreateAssociation"));
	}

	/**
	 * Добавление связи при создании поручения на основании документа
	 */
	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		NodeRef errandRef = associationRef.getSourceRef();
		NodeRef attachmentRef = associationRef.getTargetRef();

		List<NodeRef> categories = documentAttachmentsService.getCategories(errandRef);
		if (categories != null && categories.size() > 0) {
			NodeRef category = categories.get(0);

			String name = nodeService.getProperty (attachmentRef, ContentModel.PROP_NAME).toString ();
			QName assocQname = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, name);
			nodeService.moveNode(attachmentRef, category, ContentModel.ASSOC_CONTAINS, assocQname);

			nodeService.removeAssociation(errandRef, attachmentRef, associationRef.getTypeQName());
		}
	}
}
