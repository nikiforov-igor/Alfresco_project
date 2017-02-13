package ru.it.lecm.ord.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.ord.api.ORDModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALoginov on 10.02.2017.
 */
public class ORDErrandsExecutorsPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {

    private NodeService nodeService;
    private PolicyComponent policyComponent;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    final public void init() {
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, ErrandsService.TYPE_ERRANDS,
                ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT,
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        NodeRef ordDoc = associationRef.getTargetRef();
        if (nodeService.getType(ordDoc).equals(ORDModel.TYPE_ORD)) {
            List<NodeRef> executors = new ArrayList<>();
            for (AssociationRef assoc : nodeService.getTargetAssocs(ordDoc, ORDModel.ASSOC_ORD_ERRANDS_EXECUTORS)) {
                executors.add(assoc.getTargetRef());
            }
            List<AssociationRef> assoc = nodeService.getTargetAssocs(associationRef.getSourceRef(), ErrandsService.ASSOC_ERRANDS_EXECUTOR);
            assoc.addAll(nodeService.getTargetAssocs(associationRef.getSourceRef(), ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS));
            for (AssociationRef associationRef1 : assoc) {
                NodeRef nodeRef = associationRef1.getTargetRef();
                if (!executors.contains(nodeRef)) {
                    nodeService.createAssociation(ordDoc, nodeRef, ORDModel.ASSOC_ORD_ERRANDS_EXECUTORS);
                    executors.add(nodeRef);
                }
            }
        }
    }
}
