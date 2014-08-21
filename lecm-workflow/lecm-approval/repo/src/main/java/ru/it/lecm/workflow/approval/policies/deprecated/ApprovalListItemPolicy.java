package ru.it.lecm.workflow.approval.policies.deprecated;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.workflow.approval.api.deprecated.ApprovalResultModel;
import ru.it.lecm.workflow.api.WorkflowResultModel;

/**
 * Создать кастомную чайлд-ассоциацию при создании нового элемента в листе
 * согласования.
 *
 * @author vlevin
 */
@Deprecated
public class ApprovalListItemPolicy implements NodeServicePolicies.OnCreateNodePolicy {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalListItemPolicy.class);

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				ApprovalResultModel.TYPE_APPROVAL_ITEM,
				new JavaBehaviour(this, "onCreateNode"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssociationRef) {
		NodeRef approvalList = childAssociationRef.getParentRef();
		NodeRef approvalItem = childAssociationRef.getChildRef();

		nodeService.createAssociation(approvalList, approvalItem, WorkflowResultModel.ASSOC_WORKFLOW_RESULT_LIST_RESULT_ITEM);
	}
}
