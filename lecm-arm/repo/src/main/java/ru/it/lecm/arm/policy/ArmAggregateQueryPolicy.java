package ru.it.lecm.arm.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.arm.beans.ArmService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 07.05.2014
 * Time: 17:08
 */
public class ArmAggregateQueryPolicy {
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private ArmService armService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setArmService(ArmService armService) {
		this.armService = armService;
	}

	public final void init() {
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ArmService.TYPE_ARM_BASE_NODE,
				new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				ArmService.TYPE_ARM_BASE_NODE, new JavaBehaviour(this, "onDeleteNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ArmService.TYPE_STATUSES_CHILD_RULE,
				new JavaBehaviour(this, "onUpdateStatusesProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ArmService.TYPE_ARM_BASE_NODE, ArmService.ASSOC_NODE_CHILD_RULE,
				new JavaBehaviour(this, "onCreateStatusesAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				ArmService.TYPE_ARM_BASE_NODE, ArmService.ASSOC_NODE_CHILD_RULE,
				new JavaBehaviour(this, "onDeleteStatusesAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		Boolean beforeValue = (Boolean) before.get(ArmService.PROP_IS_AGGREGATION_NODE);
		Boolean afterValue = (Boolean) after.get(ArmService.PROP_IS_AGGREGATION_NODE);
		if (afterValue != null && afterValue && !Boolean.TRUE.equals(beforeValue)) {
			armService.aggregateNode(nodeRef);
		}
		armService.aggregateNode(nodeService.getPrimaryParent(nodeRef).getParentRef());
	}

	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		if (nodeService.exists(childAssocRef.getParentRef())) {
			armService.aggregateNode(childAssocRef.getParentRef());
		}
	}

	public void onUpdateStatusesProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		List<AssociationRef> queryAssoc = nodeService.getSourceAssocs(nodeRef, ArmService.ASSOC_NODE_CHILD_RULE);
		if (queryAssoc != null && queryAssoc.size() > 0 && queryAssoc.get(0).getSourceRef() != null) {
			NodeRef sourceRef = queryAssoc.get(0).getSourceRef();
			armService.aggregateNode(nodeService.getPrimaryParent(sourceRef).getParentRef());
		}
	}

	public void onCreateStatusesAssociation(AssociationRef nodeAssocRef) {
		NodeRef nodeRef = nodeAssocRef.getSourceRef();
		if (nodeRef != null) {
			armService.aggregateNode(nodeService.getPrimaryParent(nodeRef).getParentRef());
		}
	}

	public void onDeleteStatusesAssociation(AssociationRef nodeAssocRef) {
		NodeRef nodeRef = nodeAssocRef.getSourceRef();
		if (nodeRef != null && nodeService.exists(nodeRef)) {
			armService.aggregateNode(nodeService.getPrimaryParent(nodeRef).getParentRef());
		}
	}
}
