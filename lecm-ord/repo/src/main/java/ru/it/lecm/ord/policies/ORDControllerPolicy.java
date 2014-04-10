package ru.it.lecm.ord.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 *
 * @author snovikov
 */
public class ORDControllerPolicy {
	private final static Logger logger = LoggerFactory.getLogger(ORDControllerPolicy.class);

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private StateMachineServiceBean stateMachineService;
	private LecmPermissionService lecmPermissionService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ORDModel.TYPE_ORD, ORDModel.ASSOC_ORD_CONTROLLER,
				new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				ORDModel.TYPE_ORD, ORDModel.ASSOC_ORD_CONTROLLER,
				new JavaBehaviour(this, "onDeleteAssociation"));

	}

	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef ord = nodeAssocRef.getSourceRef();
		NodeRef controller = nodeAssocRef.getTargetRef();
		stateMachineService.grandDynamicRoleForEmployee(ord, controller, "DA_CONTROLLER_DYN");
	}

	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef ord = nodeAssocRef.getSourceRef();
		NodeRef controller = nodeAssocRef.getTargetRef();
		lecmPermissionService.revokeDynamicRole("DA_CONTROLLER_DYN", ord, controller.getId());
	}
}
