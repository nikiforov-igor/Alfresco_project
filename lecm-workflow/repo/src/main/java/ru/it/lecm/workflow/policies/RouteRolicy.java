package ru.it.lecm.workflow.policies;

import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
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
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.api.LecmWorkflowModel;

/**
 *
 * @author vmalygin
 */
public class RouteRolicy implements OnUpdateNodePolicy, OnCreateChildAssociationPolicy, OnCreateAssociationPolicy {

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
		policyComponent.bindAssociationBehaviour(OnCreateAssociationPolicy.QNAME, LecmWorkflowModel.TYPE_ROUTE, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_OWNER, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateNode(final NodeRef routeRef) {
		if (nodeService.exists(routeRef)) {
			boolean hasTempAspect = nodeService.hasAspect(routeRef, LecmWorkflowModel.ASPECT_TEMP);
			List<ChildAssociationRef> children = nodeService.getChildAssocs(routeRef, LecmWorkflowModel.ASSOC_ROUTE_CONTAINS_WORKFLOW_ASSIGNEES_LIST, RegexQNamePattern.MATCH_ALL);
			int assigneesCount = 0;
			for (ChildAssociationRef child : children) {
				NodeRef assigneesListRef = child.getChildRef();
				assigneesCount += nodeService.getChildAssocs(assigneesListRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL).size();
			}
			if (hasTempAspect && assigneesCount > 0) {
				nodeService.removeAspect(routeRef, LecmWorkflowModel.ASPECT_TEMP);
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

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef routeRef = nodeAssocRef.getSourceRef();
		NodeRef ownerRef = nodeAssocRef.getTargetRef();
		QName ownerTypeQName = nodeService.getType(ownerRef);
		if (OrgstructureBean.TYPE_EMPLOYEE.isMatch(ownerTypeQName)) {
			nodeService.setProperty(routeRef, ContentModel.PROP_DESCRIPTION, "личный маршрут");
		} else if (OrgstructureBean.TYPE_ORGANIZATION_UNIT.isMatch(ownerTypeQName)) {
			String orgName = (String) nodeService.getProperty(ownerRef, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME);
			nodeService.setProperty(routeRef, ContentModel.PROP_DESCRIPTION, String.format("маршрут подразделения \"%s\"", orgName));
		}
	}
}
