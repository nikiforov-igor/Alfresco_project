package ru.it.lecm.workflow.policies;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.WorkflowResultModel;

/**
 *
 *
 * @author dgonchar
 */
public class WorkflowAssigneesListItemPolicy implements NodeServicePolicies.OnCreateAssociationPolicy,
		NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowAssigneesListItemPolicy.class);
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
				LecmWorkflowModel.TYPE_ASSIGNEE,
				new JavaBehaviour(this, "onCreateNode"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE,
				new JavaBehaviour(this, "onCreateAssociation", NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				LecmWorkflowModel.TYPE_ASSIGNEE, new JavaBehaviour(this, "beforeDeleteNode"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				LecmWorkflowModel.TYPE_ASSIGNEE, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE,
				new JavaBehaviour(this, "onCreateEmployeeAssociation", NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssociationRef) {
		NodeRef assigneesList = childAssociationRef.getParentRef();
		NodeRef assigneesItem = childAssociationRef.getChildRef();

		nodeService.createAssociation(assigneesList, assigneesItem, WorkflowResultModel.ASSOC_WORKFLOW_RESULT_LIST_RESULT_ITEM);
	}

	/**
	 * Поместить свежесозданный элемент списка согласующих в конец списка.
	 *
	 * @param associationRef созданная ассоциация на элемент списка согласующих
	 */
	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		int order = 0;

		NodeRef assigneesList = associationRef.getSourceRef();
		NodeRef assigneesItem = associationRef.getTargetRef();
		String concurrency = (String) nodeService.getProperty(assigneesList, LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY);

		if (concurrency != null && concurrency.equalsIgnoreCase("SEQUENTIAL")) {

			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(assigneesList, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE);
			for (AssociationRef targetAssoc : targetAssocs) {
				NodeRef listItem = targetAssoc.getTargetRef();
				if (assigneesItem.equals(listItem)) {
					continue;
				}
				int itemOrder = (Integer) nodeService.getProperty(listItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER);
				order = itemOrder > order ? itemOrder : order;
			}
			final int finalOrder = order + 1;
			nodeService.addAspect(assigneesItem, LecmWorkflowModel.ASPECT_ASSIGNEE_ORDER, new HashMap<QName, Serializable>(){{
				put(LecmWorkflowModel.PROP_ASSIGNEE_ORDER, finalOrder);
			}});

		}
	}

	public void onCreateEmployeeAssociation(AssociationRef associationRef) {
		NodeRef assigneeItemRef = associationRef.getSourceRef();
		NodeRef employeeRef = associationRef.getTargetRef();
		String userName = orgstructureBean.getEmployeeLogin(employeeRef);
		if (StringUtils.isNotEmpty(userName)) {
			nodeService.setProperty(assigneeItemRef, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME, userName);
		}
	}

	/**
	 * При удалении элемента из списка согласования пересчитывает порядок
	 * оставшихся элементов (1..n с шагом 1)
	 *
	 * @param deletedAssigneesItem элемент списка, который готовится к удалению.
	 */
	@Override
	public void beforeDeleteNode(NodeRef deletedAssigneesItem) {
		List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(deletedAssigneesItem);
		NodeRef assigneesList = parentAssocs.get(0).getParentRef();

		int deletedItemOrder = (Integer) nodeService.getProperty(deletedAssigneesItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER);

		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(assigneesList, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE);
		for (AssociationRef targetAssoc : targetAssocs) {
			NodeRef listItem = targetAssoc.getTargetRef();
			if (listItem.equals(deletedAssigneesItem)) {
				continue;
			}
			int itemOrder = (Integer) nodeService.getProperty(listItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER);
			if (itemOrder > deletedItemOrder) {
				nodeService.setProperty(listItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER, itemOrder - 1);
			}
		}
	}
}
