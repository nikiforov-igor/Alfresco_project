package ru.it.lecm.workflow.policies;

import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.api.LecmWorkflowModel;

/**
 *
 *
 * @author dgonchar
 */
public class WorkflowAssigneesListItemPolicy implements NodeServicePolicies.OnCreateChildAssociationPolicy, NodeServicePolicies.OnCreateAssociationPolicy,
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

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
				LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE,
				new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				LecmWorkflowModel.TYPE_ASSIGNEE, new JavaBehaviour(this, "beforeDeleteNode"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				LecmWorkflowModel.TYPE_ASSIGNEE, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE,
				new JavaBehaviour(this, "onCreateAssociation"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssociationRef) {
		NodeRef assigneesList = childAssociationRef.getParentRef();
		NodeRef assigneesItem = childAssociationRef.getChildRef();

		String assigneeName = (String) nodeService.getProperty(assigneesItem, ContentModel.PROP_NAME);
		QName qName = QName.createQName(LecmWorkflowModel.WORKFLOW_NAMESPACE, assigneeName);
		nodeService.addChild(assigneesList, assigneesItem, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, qName);
	}

	/**
	 * Поместить свежесозданный элемент списка согласующих в конец списка.
	 *
	 * @param childAssocRef
	 * @param isNewNode
	 */
	@Override
	public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode) {
		int order = 0;

		NodeRef assigneesList = childAssocRef.getParentRef();
		NodeRef assigneesItem = childAssocRef.getChildRef();

		if (!nodeService.exists(assigneesItem)) {
			// наша нода была удалена. скорее всего во время актуализации списка участников
			return;
		}

		if (nodeService.getProperty(assigneesItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER) != null) {
			// порядок указан явно. предположим, что создающий знает, что делает
			return;
		}

		List<ChildAssociationRef> targetAssocs = nodeService.getChildAssocs(assigneesList, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef targetAssoc : targetAssocs) {
			NodeRef listItem = targetAssoc.getChildRef();
			if (!nodeService.exists(listItem) || assigneesItem.equals(listItem)) {
				continue;
			}
			int itemOrder = (Integer) nodeService.getProperty(listItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER);
			order = itemOrder > order ? itemOrder : order;
		}

		nodeService.setProperty(assigneesItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER, order + 1);

	}

	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		NodeRef assigneesItem = associationRef.getSourceRef();
		NodeRef employeeRef = associationRef.getTargetRef();
		if (!nodeService.exists(assigneesItem)) {
			// наша нода была удалена. скорее всего во время актуализации списка участников
			return;
		}
		String userName = orgstructureBean.getEmployeeLogin(employeeRef);
		if (StringUtils.isNotEmpty(userName)) {
			nodeService.setProperty(assigneesItem, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME, userName);
		}
	}

	/**
	 * При удалении элемента из списка согласования пересчитывает порядок оставшихся элементов (1..n с шагом 1)
	 *
	 * @param deletedAssigneesItem элемент списка, который готовится к удалению.
	 */
	@Override
	public void beforeDeleteNode(NodeRef deletedAssigneesItem) {
		NodeRef assigneesList = nodeService.getPrimaryParent(deletedAssigneesItem).getParentRef();

		int deletedItemOrder = (Integer) nodeService.getProperty(deletedAssigneesItem, LecmWorkflowModel.PROP_ASSIGNEE_ORDER);

		List<ChildAssociationRef> targetAssocs = nodeService.getChildAssocs(assigneesList, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef targetAssoc : targetAssocs) {
			NodeRef listItem = targetAssoc.getChildRef();
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
