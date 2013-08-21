package ru.it.lecm.signed.docflow.policies;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.CopyServicePolicies.OnCopyCompletePolicy;
import org.alfresco.repo.copy.CopyServicePolicies.OnCopyNodePolicy;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

/**
 *
 * @author vlevin
 */
public class SignedContentCopyPolicy implements OnCopyNodePolicy, OnCopyCompletePolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindClassBehaviour(OnCopyNodePolicy.QNAME, ContentModel.TYPE_CONTENT,
				new JavaBehaviour(this, "getCopyCallback"));
		policyComponent.bindClassBehaviour(OnCopyCompletePolicy.QNAME, ContentModel.TYPE_CONTENT,
				new JavaBehaviour(this, "onCopyComplete", NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public CopyBehaviourCallback getCopyCallback(QName classRef, CopyDetails copyDetails) {
		NodeRef sourceNodeRef = copyDetails.getSourceNodeRef();
		List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(sourceNodeRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);
		if (sourceAssocs.isEmpty()) {
			return new DefaultCopyBehaviourCallback();
		} else {
			return new SignedContentCopyBehaviourCallback();
		}
	}

	@Override
	public void onCopyComplete(QName classRef, NodeRef sourceNodeRef, NodeRef targetNodeRef, boolean copyToNewNode, Map<NodeRef, NodeRef> copyMap) {
		Set<QName> aspects = nodeService.getAspects(targetNodeRef);
		if (aspects.contains(SignedDocflowModel.ASPECT_SIGNABLE)) {
			nodeService.removeAspect(targetNodeRef, SignedDocflowModel.ASPECT_SIGNABLE);
		}
	}
}
