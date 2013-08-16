package ru.it.lecm.br5.semantic.policies;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.br5.semantic.api.ConstantsBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;

/**
 *
 * @author snovikov
 */
public class DocumentBr5AspectPolicy implements ConstantsBean {

	private final static Logger logger = LoggerFactory.getLogger(DocumentBr5AspectPolicy.class);
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private DocumentService documentService;
	private DocumentAttachmentsService documentAttachmentsService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT,
				new JavaBehaviour(this, "onCreateDocument",NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ContentModel.TYPE_CONTENT,
				new JavaBehaviour(this, "onCreateContains"));
	}

	public void onCreateDocument(ChildAssociationRef childAssocRef){
		NodeRef document = childAssocRef.getChildRef();
		if (document!=null){
			//проверим документ на наличие аспекта, который скажет, надо ли навешивать еще аспект индексирования в БР5
			if ( nodeService.hasAspect(document, DocumentService.ASPECT_SEMANTIC_ASSIST) ){
				Map<QName,Serializable> mprops = new HashMap<QName,Serializable>();

				if (nodeService.hasAspect(document, ContentModel.ASPECT_VERSIONABLE)){
					String version = (String) nodeService.getProperty(document,ContentModel.PROP_VERSION_LABEL);
					if (version!=null && !version.isEmpty() ){
						mprops.put(ConstantsBean.PROP_BR5_INTEGRATION_VERSION, version);
					}
				}
				nodeService.addAspect(document, ConstantsBean.ASPECT_BR5_INTEGRATION, mprops);
			}
		}
	}

	public void onCreateContains (AssociationRef nodeAssocRef){
			NodeRef content = nodeAssocRef.getTargetRef();
			if (content!=null){
				NodeRef document = documentAttachmentsService.getDocumentByAttachment(content);
				if (document!=null){
					if (nodeService.hasAspect(document, ConstantsBean.ASPECT_BR5_INTEGRATION) && !nodeService.hasAspect(content, ASPECT_BR5_INTEGRATION)){
						nodeService.addAspect(content, ConstantsBean.ASPECT_BR5_INTEGRATION, null);
					}
				}
			}
	}
}
