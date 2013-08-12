package ru.it.lecm.signed.docflow.policies;

import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.version.VersionServicePolicies.BeforeCreateVersionPolicy;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

/**
 *
 * @author VLadimir Malygin
 * @since 09.08.2013 17:26:16
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SigningContentVersionChangePolicy implements BeforeCreateVersionPolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		policyComponent.bindClassBehaviour(BeforeCreateVersionPolicy.QNAME, ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeCreateVersion", NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public void beforeCreateVersion(final NodeRef versionableNode) {
		//проверяем есть ли у ноды ассоциация на подпись
		//если ассоциация есть, то разрываем ее
		List<AssociationRef> assocs = nodeService.getSourceAssocs(versionableNode, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);
		if (assocs != null) {
			for (AssociationRef assoc : assocs) {
				nodeService.removeAssociation(assoc.getSourceRef(), assoc.getTargetRef(), SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);
			}
		}
	}
}
