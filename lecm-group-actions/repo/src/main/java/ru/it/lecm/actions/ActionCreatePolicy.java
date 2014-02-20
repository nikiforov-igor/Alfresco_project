package ru.it.lecm.actions;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.actions.bean.GroupActionsService;

/**
 * User: pmelnikov
 * Date: 20.02.14
 * Time: 11:16
 */
public class ActionCreatePolicy implements NodeServicePolicies.OnCreateNodePolicy {

    private static NodeService nodeService;
    private static PolicyComponent policyComponent;

    public void setNodeService(NodeService nodeService) {
        ActionCreatePolicy.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        ActionCreatePolicy.policyComponent = policyComponent;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                GroupActionsService.TYPE_GROUP_ACTION, new JavaBehaviour(this, "onCreateNode"));

    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef node = childAssocRef.getChildRef();
        nodeService.setProperty(node, GroupActionsService.PROP_FIELDS_REF, node.toString());
    }

}
