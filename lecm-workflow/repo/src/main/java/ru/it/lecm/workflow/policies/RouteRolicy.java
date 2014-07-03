package ru.it.lecm.workflow.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.api.LecmWorkflowModel;

import java.util.List;

/**
 *
 * @author vmalygin
 */
public class RouteRolicy implements OnUpdateNodePolicy, OnCreateChildAssociationPolicy, NodeServicePolicies.OnDeleteChildAssociationPolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setPolicyComponent(final PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		policyComponent.bindClassBehaviour(OnUpdateNodePolicy.QNAME, LecmWorkflowModel.TYPE_ROUTE, new JavaBehaviour(this, "onUpdateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(OnCreateChildAssociationPolicy.QNAME, LecmWorkflowModel.TYPE_ROUTE, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this, "onCreateChildAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(OnCreateChildAssociationPolicy.QNAME, LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME, LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, new JavaBehaviour(this, "onDeleteChildAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateNode(final NodeRef routeRef) {
		if (nodeService.exists(routeRef)) {
			String name = (String) nodeService.getProperty(routeRef, ContentModel.PROP_NAME);
			String title = (String) nodeService.getProperty(routeRef, ContentModel.PROP_TITLE);
			String[] parts = StringUtils.split(name, '_');
			String newName = String.format("%s_%s", parts[0], title);
			String validName = FileNameValidator.getValidFileName(newName);
			nodeService.setProperty(routeRef, ContentModel.PROP_NAME, validName);
			boolean hasTempAspect = nodeService.hasAspect(routeRef, LecmWorkflowModel.ASPECT_TEMP);
            if (hasTempAspect) {
                List<ChildAssociationRef> children = nodeService.getChildAssocs(routeRef, LecmWorkflowModel.ASSOC_ROUTE_CONTAINS_WORKFLOW_ASSIGNEES_LIST, RegexQNamePattern.MATCH_ALL);
                for (ChildAssociationRef child : children) {
                    NodeRef assigneesListRef = child.getChildRef();
                    if (!nodeService.getChildAssocs(assigneesListRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL).isEmpty()) {
                        nodeService.removeAspect(routeRef, LecmWorkflowModel.ASPECT_TEMP);
                        break;  //проверять остальные списки не требуется
                    }
                }
            }
        }
	}

	@Override
	public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode) {
		NodeRef routeRef = childAssocRef.getParentRef();
		NodeRef assigneesListRef = childAssocRef.getChildRef();
		String assigneesListName = (String) nodeService.getProperty(assigneesListRef, ContentModel.PROP_NAME);
		QName qName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, assigneesListName);
		nodeService.addChild(routeRef, assigneesListRef, LecmWorkflowModel.ASSOC_ROUTE_CONTAINS_WORKFLOW_ASSIGNEES_LIST, qName);
		if (!nodeService.hasAspect(assigneesListRef, LecmWorkflowModel.ASPECT_WORKFLOW_ROUTE)) {
			nodeService.addAspect(assigneesListRef, LecmWorkflowModel.ASPECT_WORKFLOW_ROUTE, null);
		}
	}

	public void onCreateAssociation(ChildAssociationRef childAssocRef, boolean isNewNode) {
        updateRouteDescription(childAssocRef);
	}

    @Override
    public void onDeleteChildAssociation(ChildAssociationRef childAssocRef) {
        updateRouteDescription(childAssocRef);
    }

    private void updateRouteDescription(ChildAssociationRef childAssocRef) {
        NodeRef listRef = childAssocRef.getParentRef();
        NodeRef routeRef = nodeService.getPrimaryParent(listRef).getParentRef();
        if (LecmWorkflowModel.TYPE_ROUTE.isMatch(nodeService.getType(routeRef))) {
            String approvers = "";
            String signers = "";
            int signersCount = 0;
            int approversCount = 0;
            List<ChildAssociationRef> listsRefs = nodeService.getChildAssocs(routeRef, LecmWorkflowModel.ASSOC_ROUTE_CONTAINS_WORKFLOW_ASSIGNEES_LIST, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef list : listsRefs) {
                NodeRef assigneesListRef = list.getChildRef();
                String listType = (String) nodeService.getProperty(assigneesListRef, LecmWorkflowModel.PROP_WORKFLOW_TYPE);
                List<ChildAssociationRef> assignees = nodeService.getChildAssocs(assigneesListRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL);
                for (ChildAssociationRef assignee : assignees) {
                    List<AssociationRef> refs = nodeService.getTargetAssocs(assignee.getChildRef(), LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE);
                    if (!refs.isEmpty()) {
                        NodeRef employeeRef = refs.get(0).getTargetRef();
                        String shortName = (String) nodeService.getProperty(employeeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                        if (!"".equals(shortName)) {
                            switch (listType) {
                                case "SIGNING":
                                    signers += (signersCount > 0 ? ", " : "") + shortName;
                                    signersCount++;
                                    break;
                                case "APPROVAL":
                                    approvers += (approversCount > 0 ? ", " : "") + shortName;
                                    approversCount++;
                                    break;
                            }
                        }
                    }
                }
            }
            if (approversCount > 1) {
                approvers = "Согласующие: " + approvers + "\n";
            } else if (approversCount > 0) {
                approvers = "Согласующий: " + approvers + "\n";
            }
            if (signersCount > 1) {
                signers = "Подписанты: " + signers;
            } else if (signersCount > 0) {
                signers = "Подписант: " + signers;
            }
            nodeService.setProperty(routeRef, ContentModel.PROP_DESCRIPTION, approvers + signers);
        }
    }

}
