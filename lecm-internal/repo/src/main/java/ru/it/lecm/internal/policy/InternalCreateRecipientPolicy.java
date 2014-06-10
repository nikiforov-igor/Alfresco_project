package ru.it.lecm.internal.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.internal.api.InternalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * User: dbashmakov
 * Date: 11.03.14
 * Time: 16:02
 */
public class InternalCreateRecipientPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
    private static final Logger logger = LoggerFactory.getLogger(InternalCreateRecipientPolicy.class);

    private PolicyComponent policyComponent;
    private OrgstructureBean orgstructureService;
    private NodeService nodeService;

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                InternalService.TYPE_INTERNAL, InternalService.ASSOC_INTERNAL_RECIPIENTS, new JavaBehaviour(this, "onDeleteAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                InternalService.TYPE_INTERNAL, InternalService.ASSOC_INTERNAL_RECIPIENTS, new JavaBehaviour(this, "onCreateAssociation"));
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef targetRef = nodeAssocRef.getTargetRef();
        NodeRef internalRef = nodeAssocRef.getSourceRef();
        try {
            if (orgstructureService.isEmployee(targetRef)) {
                nodeService.createAssociation(internalRef, targetRef, EDSDocumentService.ASSOC_RECIPIENTS);
            } else {
                if (orgstructureService.isUnit(targetRef)) {
                    NodeRef employee = orgstructureService.getUnitBoss(targetRef);
                    if (employee != null) {
                        nodeService.createAssociation(internalRef, employee, EDSDocumentService.ASSOC_RECIPIENTS);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
//        NodeRef targetRef = nodeAssocRef.getTargetRef();
//        NodeRef internalRef = nodeAssocRef.getSourceRef();
//        try {
//            if (orgstructureService.isEmployee(targetRef)) {
//                nodeService.removeAssociation(internalRef, targetRef, EDSDocumentService.ASSOC_RECIPIENTS);
//            } else {
//                if (orgstructureService.isUnit(targetRef)) {
//                    NodeRef employee = orgstructureService.getUnitBoss(targetRef);
//                    if (employee != null) {
//                        nodeService.removeAssociation(internalRef, employee, EDSDocumentService.ASSOC_RECIPIENTS);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
