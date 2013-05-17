package ru.it.lecm.approval.policies;

import java.io.Serializable;
import java.util.List;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 *
 * @author dgonchar
 */
public class AssigneesListItemPolicy implements NodeServicePolicies.OnCreateAssociationPolicy,
		NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy {

	private final static Logger logger = LoggerFactory.getLogger(AssigneesListItemPolicy.class);
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
				ApprovalListService.TYPE_ASSIGNEES_ITEM,
				new JavaBehaviour(this, "onCreateNode"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ApprovalListService.TYPE_ASSIGNEES_LIST, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM,
				new JavaBehaviour(this, "onCreateAssociation", NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				ApprovalListService.TYPE_ASSIGNEES_ITEM, new JavaBehaviour(this, "beforeDeleteNode"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
			ApprovalListService.TYPE_ASSIGNEES_ITEM, ApprovalListService.ASSOC_ASSIGNEES_ITEM_EMPLOYEE_ASSOC,
			new JavaBehaviour(this, "onCreateEmployeeAssociation", NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssociationRef) {
		NodeRef assigneesList = childAssociationRef.getParentRef();
		NodeRef assigneesItem = childAssociationRef.getChildRef();

		nodeService.createAssociation(assigneesList, assigneesItem, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
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

		Serializable currentItemOrder = nodeService.getProperty(assigneesItem, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER);
		if (currentItemOrder == null || (Integer) currentItemOrder < 0) {
			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(assigneesList, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
			for (AssociationRef targetAssoc : targetAssocs) {
				NodeRef listItem = targetAssoc.getTargetRef();
				if (assigneesItem.equals(listItem)) {
					continue;
				}
				int itemOrder = (Integer) nodeService.getProperty(listItem, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER);
				order = itemOrder > order ? itemOrder : order;
			}
			nodeService.setProperty(assigneesItem, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER, order + 1);
		}
	}

	public void onCreateEmployeeAssociation(AssociationRef associationRef) {
		NodeRef assigneeItemRef = associationRef.getSourceRef();
		NodeRef employeeRef = associationRef.getTargetRef();
		String userName = orgstructureBean.getEmployeeLogin(employeeRef);
		if (StringUtils.isNotEmpty(userName)) {
			nodeService.setProperty(assigneeItemRef, ApprovalListService.PROP_ASSIGNEES_ITEM_USERNAME, userName);
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

		int deletedItemOrder = (Integer) nodeService.getProperty(deletedAssigneesItem, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER);

		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(assigneesList, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
		for (AssociationRef targetAssoc : targetAssocs) {
			NodeRef listItem = targetAssoc.getTargetRef();
			if (listItem.equals(deletedAssigneesItem)) {
				continue;
			}
			int itemOrder = (Integer) nodeService.getProperty(listItem, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER);
			if (itemOrder > deletedItemOrder) {
				nodeService.setProperty(listItem, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER, itemOrder - 1);
			}
		}
	}
}
