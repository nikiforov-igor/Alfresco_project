package ru.it.lecm.workflow.review.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.review.api.ReviewService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vkuprin
 */
public class ItemChangedPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private OrgstructureBean orgstructureService;
	private AuthenticationService authenticationService;
	private ReviewService reviewService;
	private DocumentTableService tableService;

	public void setTableService(DocumentTableService tableService) {
		this.tableService = tableService;
	}
	
	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
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
				ReviewService.TYPE_REVIEW_TS_REVIEW_TABLE_ITEM, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (null != nodeRef
                && null != after.get(ReviewService.PROP_REVIEW_TS_STATE)
                && !after.get(ReviewService.PROP_REVIEW_TS_STATE).equals(before.get(ReviewService.PROP_REVIEW_TS_STATE))) {
            NodeRef document = tableService.getDocumentByTableDataRow(nodeRef);
            List<NodeRef> employees = reviewService.getActiveReviewersForDocument(document);
            StringBuilder sb = new StringBuilder();
            for (NodeRef employee : employees) {
                sb.append(employee.toString()).append(";");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            nodeService.setProperty(document, ReviewService.PROP_REVIEW_TS_ACTIVE_REVIEWERS, sb.toString());

            List<AssociationRef> initiatingDocuments = nodeService.getSourceAssocs(nodeRef, ReviewService.ASSOC_RELATED_REVIEW_RECORDS);
            if (initiatingDocuments != null) {
                for (AssociationRef assoc: initiatingDocuments) {
                    reviewService.addRelatedReviewChangeCount(assoc.getSourceRef());
                }
            }
        }
    }

}
