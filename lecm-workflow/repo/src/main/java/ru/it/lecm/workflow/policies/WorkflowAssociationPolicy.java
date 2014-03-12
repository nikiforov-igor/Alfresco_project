package ru.it.lecm.workflow.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.workflow.api.WorkflowResultModel;

/**
 *
 * @author vlevin
 */
public class WorkflowAssociationPolicy extends LogicECMAssociationPolicy {

	@Override
	public void init() {
		super.init();

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				WorkflowResultModel.TYPE_WORKFLOW_RESULT_ITEM, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				WorkflowResultModel.TYPE_WORKFLOW_RESULT_ITEM, new JavaBehaviour(this, "onCreateAssociation"));
	}
}
