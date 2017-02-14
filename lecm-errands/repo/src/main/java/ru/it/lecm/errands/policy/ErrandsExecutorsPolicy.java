package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.errands.ErrandsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALoginov on 13.02.2017.
 */
public class ErrandsExecutorsPolicy {

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
                new JavaBehaviour(this, "onCreateAssociationExecutors", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME, ErrandsService.TYPE_ERRANDS,
                ErrandsService.ASSOC_ERRANDS_EXECUTOR,
                new JavaBehaviour(this, "onDeleteAssociationExecutors", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, ErrandsService.TYPE_ERRANDS,
                ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS,
                new JavaBehaviour(this, "onCreateAssociationCoExecutors", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME, ErrandsService.TYPE_ERRANDS,
                ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS,
                new JavaBehaviour(this, "onDeleteAssociationCoExecutors", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    public void onCreateAssociationExecutors(AssociationRef nodeAssocRef) {
        changeExecutorsAspect(nodeAssocRef, true, ErrandsService.ASSOC_ERRANDS_EXECUTORS_FIRST_LEVEL);
    }

    public void onDeleteAssociationExecutors(AssociationRef nodeAssocRef) {
        changeExecutorsAspect(nodeAssocRef, false, ErrandsService.ASSOC_ERRANDS_EXECUTORS_FIRST_LEVEL);
    }

    public void onCreateAssociationCoExecutors(AssociationRef nodeAssocRef) {
        changeExecutorsAspect(nodeAssocRef, true, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS_FIRST_LEVEL);
    }

    public void onDeleteAssociationCoExecutors(AssociationRef nodeAssocRef) {
        changeExecutorsAspect(nodeAssocRef, false, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS_FIRST_LEVEL);
    }

    /**
     * Метод в зависимости от значения change создает или удаляет ассоциацию
     *
     * @param nodeAssocRef Ассоциация поручение\исполнитель(соисполнитель)
     * @param isAdded      (true)создать\(false)удалить ассоциацию
     * @param assoc        QName ассоциации
     */
    private void changeExecutorsAspect(AssociationRef nodeAssocRef, boolean isAdded, QName assoc) {
        List<NodeRef> executors = new ArrayList<>();
        NodeRef executor = nodeAssocRef.getTargetRef();
        List<AssociationRef> list = nodeService.getTargetAssocs(nodeAssocRef.getSourceRef(), ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
        if (list != null && list.size() > 0) {
            NodeRef document = list.get(0).getTargetRef();
            if (document != null && nodeService.hasAspect(document, ErrandsService.ASPECT_ERRANDS_EXECUTORS)) {
                for (AssociationRef associationRef : nodeService.getTargetAssocs(document, assoc)) {
                    executors.add(associationRef.getTargetRef());
                }
                if (!executors.contains(executor) && isAdded) {
                    nodeService.createAssociation(document, executor, assoc);
                } else if (!isAdded) {
                    nodeService.removeAssociation(document, executor, assoc);
                }
            }
        }
    }
}