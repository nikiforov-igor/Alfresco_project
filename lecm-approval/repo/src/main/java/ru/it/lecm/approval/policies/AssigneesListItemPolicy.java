package ru.it.lecm.approval.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;

/**
 *
 *
 * @author dgonchar
 */
public class AssigneesListItemPolicy implements NodeServicePolicies.OnCreateAssociationPolicy,
                                                NodeServicePolicies.OnCreateNodePolicy {

    private final static Logger logger = LoggerFactory.getLogger(AssigneesListItemPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ApprovalListService.TYPE_ASSIGNEES_ITEM,
                new JavaBehaviour(this, "onCreateNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssociationRef) {
        NodeRef assigneesList = childAssociationRef.getParentRef();
        NodeRef assigneesItem = childAssociationRef.getChildRef();

        nodeService.createAssociation(assigneesList, assigneesItem, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        int theAnswer = 42;
    }
}
