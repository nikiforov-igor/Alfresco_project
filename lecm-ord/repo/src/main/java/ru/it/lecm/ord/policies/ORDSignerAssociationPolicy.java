package ru.it.lecm.ord.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.workflow.signing.api.SigningAspectsModel;

import java.util.List;

/**
 * Created by APanyukov on 01.03.2017.
 */
public class ORDSignerAssociationPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {

    private NodeService nodeService;
    private DocumentTableService documentTableService;
    private PolicyComponent policyComponent;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "documentTableService", documentTableService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ORDModel.TYPE_ORD, SigningAspectsModel.ASSOC_SIGNER_EMPLOYEE_ASSOC, new JavaBehaviour(this, "onCreateAssociation"));

    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef ordDoc = nodeAssocRef.getSourceRef();
        NodeRef signer = nodeAssocRef.getTargetRef();
        NodeRef itemsTable = documentTableService.getTable(ordDoc, ORDModel.TYPE_ORD_ITEMS_TABLE);
        if (itemsTable != null) {
            List<NodeRef> ordItems = documentTableService.getTableDataRows(itemsTable);
            if (ordItems != null && ordItems.size() > 0) {
                for (NodeRef item : ordItems) {
                    List<AssociationRef> itemSignerAssoc = nodeService.getTargetAssocs(item, ORDModel.ASSOC_ORD_TABLE_ITEM_AUTHOR);
                    if (itemSignerAssoc != null && itemSignerAssoc.size() != 0) {
                        NodeRef itemSigner = itemSignerAssoc.get(0).getTargetRef();
                        nodeService.removeAssociation(item, itemSigner, ORDModel.ASSOC_ORD_TABLE_ITEM_AUTHOR);
                        nodeService.createAssociation(item, signer, ORDModel.ASSOC_ORD_TABLE_ITEM_AUTHOR);
                    } else {
                        nodeService.createAssociation(item, signer, ORDModel.ASSOC_ORD_TABLE_ITEM_AUTHOR);
                    }
                }
            }
        }
    }
}
