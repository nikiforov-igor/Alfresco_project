package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 15.07.13
 * Time: 10:16
 */
public class DocumentTempAttachmentsPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
	final static protected Logger logger = LoggerFactory.getLogger(DocumentTempAttachmentsPolicy.class);

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
				DocumentService.TYPE_BASE_DOCUMENT, DocumentService.ASSOC_TEMP_ATTACHMENTS, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	/**
	 * Добавление связи при создании поручения на основании документа
	 */
	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		logger.debug("ДОКУМЕНТ. onCreateAssociation");
		NodeRef documentRef = associationRef.getSourceRef();
		NodeRef attachmentRef = associationRef.getTargetRef();

		if (!nodeService.hasAspect(attachmentRef, DocumentAttachmentsService.ASPECT_SKIP_ON_CREATE_DOCUMENT)) {
			NodeRef categoryRef = null;

			List<NodeRef> categories = null;
			try{
				categories = documentAttachmentsService.getCategories(documentRef);
			} catch (WriteTransactionNeededException e) {
				logger.error("error: ",e);
			}

			if (categories != null) {
				for (NodeRef category: categories) {
					if (!documentAttachmentsService.isReadonlyCategory(category)) {
						categoryRef = category;
						break;
					}
				}
			}

			if (categoryRef != null) {
				String name = nodeService.getProperty (attachmentRef, ContentModel.PROP_NAME).toString ();
				QName assocQname = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, name);
				nodeService.moveNode(attachmentRef, categoryRef, ContentModel.ASSOC_CONTAINS, assocQname);

				nodeService.removeAssociation(documentRef, attachmentRef, associationRef.getTypeQName());
			}
		}
	}
}
