package ru.it.lecm.workflow.review.policy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.review.ReviewServiceImpl;

/**
 *
 * @author vkuprin
 */
public class ItemChangedPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private OrgstructureBean orgstructureService;
	private AuthenticationService authenticationService;
	private ReviewServiceImpl reviewService;
	private DocumentTableService tableService;

	public DocumentTableService getTableService() {
		return tableService;
	}

	public void setTableService(DocumentTableService tableService) {
		this.tableService = tableService;
	}
	
	public ReviewServiceImpl getReviewService() {
		return reviewService;
	}

	public void setReviewService(ReviewServiceImpl reviewService) {
		this.reviewService = reviewService;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "authenticationService", authenticationService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "reviewService", reviewService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ReviewServiceImpl.TYPE_REVIEW_TS_REVIEW_TABLE_ITEM, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		if (null != nodeRef
				&& null != before.get(ReviewServiceImpl.PROP_REVIEW_TS_STATE)
				&& !before.get(ReviewServiceImpl.PROP_REVIEW_TS_STATE).equals(after.get(ReviewServiceImpl.PROP_REVIEW_TS_STATE))) {
			NodeRef document = tableService.getDocumentByTableDataRow(nodeRef);
			List<NodeRef> employees = reviewService.getActiveReviewersForDocument(document);
			StringBuilder sb = new StringBuilder();
			for (NodeRef employee : employees) {
				sb.append(employee.toString()).append(";");
			}
			if (sb.length()>0) {
				sb.deleteCharAt(sb.length()-1);
			}
			nodeService.setProperty(document, ReviewServiceImpl.PROP_REVIEW_TS_ACTIVE_REVIEWERS, sb.toString());
		}
	}

}
