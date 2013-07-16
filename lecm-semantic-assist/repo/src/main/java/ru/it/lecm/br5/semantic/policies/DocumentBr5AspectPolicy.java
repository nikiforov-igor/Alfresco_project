package ru.it.lecm.br5.semantic.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author snovikov
 */
public class DocumentBr5AspectPolicy {

	private final static Logger logger = LoggerFactory.getLogger(DocumentBr5AspectPolicy.class);
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	public static final QName ASPECT_BR5_INTEGRATION = QName.createQName("http://www.it.ru/lecm/document/aspects/1.0","br5");

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME,
				ASPECT_BR5_INTEGRATION,
				new JavaBehaviour(this, "onAddAspect"));
	}

	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName){
		logger.debug("BR5 policy has called: nodeRef= "+nodeRef.toString());
	}
}
