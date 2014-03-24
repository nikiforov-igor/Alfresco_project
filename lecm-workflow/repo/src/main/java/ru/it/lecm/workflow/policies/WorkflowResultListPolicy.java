package ru.it.lecm.workflow.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.api.WorkflowResultModel;

/**
 *
 * @author vlevin
 */
public class WorkflowResultListPolicy implements NodeServicePolicies.OnCreateNodePolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private OrgstructureBean orgstructureBean;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "orgstructureBean", orgstructureBean);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				WorkflowResultModel.TYPE_WORKFLOW_RESULT_LIST, new JavaBehaviour(this, "onCreateNode"));

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef resultList = childAssocRef.getChildRef();
		NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
		if (currentEmployee != null) {
			nodeService.createAssociation(resultList, currentEmployee, WorkflowResultModel.ASSOCWORKFLOW_RESULT_LIST_INITIATOR);
		}
	}

}
