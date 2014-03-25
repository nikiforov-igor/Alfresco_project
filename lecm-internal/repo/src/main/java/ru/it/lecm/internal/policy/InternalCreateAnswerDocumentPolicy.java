package ru.it.lecm.internal.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.internal.api.InternalService;
import ru.it.lecm.workflow.api.WorkflowResultModel;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 11.03.14
 * Time: 16:02
 */
public class InternalCreateAnswerDocumentPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
    private static final Logger logger = LoggerFactory.getLogger(InternalCreateAnswerDocumentPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentMembersService documentMembersService;
    private SigningWorkflowService signingWorkflowService;

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                InternalService.TYPE_INTERNAL, DocumentService.ASSOC_RESPONSE_TO, new JavaBehaviour(this, "onCreateAssociation"));
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef targetRef = nodeAssocRef.getTargetRef();
        NodeRef internalRef = nodeAssocRef.getSourceRef();

        NodeRef signingContainer = signingWorkflowService.getOrCreateSigningFolderContainer(targetRef);
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(signingContainer);

        HashSet<NodeRef> members = new HashSet<NodeRef>();
        if (!childAssocs.isEmpty()) {
            for (ChildAssociationRef list : childAssocs) {
                List<ChildAssociationRef> users = nodeService.getChildAssocs(list.getChildRef());
                for (ChildAssociationRef user : users) {
                    List<AssociationRef> employees = nodeService.getTargetAssocs(user.getChildRef(), WorkflowResultModel.ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE);
                    for (AssociationRef employee : employees) {
                        members.add(employee.getTargetRef());
                    }
                }

            }
        }

        for (NodeRef member : members) {
            documentMembersService.addMemberWithoutCheckPermission(internalRef, member, new HashMap<QName, Serializable>());
        }
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setSigningWorkflowService(SigningWorkflowService signingWorkflowService) {
        this.signingWorkflowService = signingWorkflowService;
    }
}
