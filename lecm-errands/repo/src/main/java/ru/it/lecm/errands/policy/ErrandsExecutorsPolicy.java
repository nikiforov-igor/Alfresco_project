package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.errands.ErrandsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALoginov on 13.02.2017.
 */
public class ErrandsExecutorsPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

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
                ErrandsService.ASSOC_ERRANDS_EXECUTOR,
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME, ErrandsService.TYPE_ERRANDS,
                ErrandsService.ASSOC_ERRANDS_EXECUTOR,
                new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, ErrandsService.TYPE_ERRANDS,
                ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS,
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME, ErrandsService.TYPE_ERRANDS,
                ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS,
                new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        changeExecutorsAspect(nodeAssocRef, true);
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        changeExecutorsAspect(nodeAssocRef, false);
    }

    /** Метод в зависимости от значения change создает или удаляет ассоциацию
     *
     * @param nodeAssocRef  Ассоциация поручение\исполнитель
     * @param change        (true)создать\(false)удалить ассоциацию
     */
    private void changeExecutorsAspect(AssociationRef nodeAssocRef, boolean change) {
        List<NodeRef> executors = new ArrayList<>();
        NodeRef executor = nodeAssocRef.getTargetRef();
        List<AssociationRef> list = nodeService.getTargetAssocs(nodeAssocRef.getSourceRef(), ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
        if (list.size() > 0) {
            NodeRef document = list.get(0).getTargetRef();
            if (nodeService.hasAspect(document, ErrandsService.ASPECT_ERRANDS_EXECUTORS)) {
                for (AssociationRef associationRef : nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_EXECUTORS)) {
                    executors.add(associationRef.getTargetRef());
                }
                if (!executors.contains(executor) && change) {
                    nodeService.createAssociation(document, executor, ErrandsService.ASSOC_ERRANDS_EXECUTORS);
                } else if (executors.contains(executor) && !change) {
                    nodeService.removeAssociation(document, executor, ErrandsService.ASSOC_ERRANDS_EXECUTORS);
                }
            }
        }
    }
}