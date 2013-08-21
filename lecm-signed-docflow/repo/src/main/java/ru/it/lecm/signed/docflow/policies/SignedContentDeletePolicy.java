package ru.it.lecm.signed.docflow.policies;

import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author vlevin
 */
public class SignedContentDeletePolicy implements BeforeDeleteNodePolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private SignedDocflow signedDocflowService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "signedDocflowService", signedDocflowService);

		policyComponent.bindClassBehaviour(QNAME, ContentModel.TYPE_CONTENT,
				new JavaBehaviour(this, "beforeDeleteNode", Behaviour.NotificationFrequency.FIRST_EVENT));

	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		List<NodeRef> signatureRefs = signedDocflowService.getSignaturesByContent(nodeRef);
		if (signatureRefs != null) {
			for (NodeRef signatureRef : signatureRefs) {
				nodeService.addAspect(signatureRef, ContentModel.ASPECT_TEMPORARY, null);
				nodeService.deleteNode(signatureRef);
			}
		}
	}
}
