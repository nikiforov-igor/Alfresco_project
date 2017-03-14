package ru.it.lecm.resolutions.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.resolutions.api.ResolutionsService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * User: AIvkin
 * Date: 16.02.2017
 * Time: 11:50
 */
public class ResolutionErrandsPolicy {
    private PolicyComponent policyComponent;
    private ErrandsService errandsService;
    private NodeService nodeService;
    private StateMachineServiceBean stateMachineService;
    private LecmPermissionService lecmPermissionService;
    private DocumentMembersService documentMembersService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT, new JavaBehaviour(this, "onCreateErrandAdditionalDocumentAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ERRANDS_EXECUTOR, new JavaBehaviour(this, "onCreateErrandExecutorAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ERRANDS_EXECUTOR, new JavaBehaviour(this, "onDeleteErrandExecutorAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS, new JavaBehaviour(this, "onCreateErrandCoExecutorAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS, new JavaBehaviour(this, "onDeleteErrandCoExecutorAssociation"));
    }

    public void onCreateErrandAdditionalDocumentAssociation(AssociationRef associationRef) {
        NodeRef errand = associationRef.getSourceRef();
        NodeRef document = associationRef.getTargetRef();

        if (ResolutionsService.TYPE_RESOLUTION_DOCUMENT.equals(nodeService.getType(document))) {
            NodeRef executor = errandsService.getExecutor(errand);
            if (executor != null) {
                stateMachineService.grandDynamicRoleForEmployee(document, executor, "RESOLUTION_CHILD_ERRANDS_EXECUTOR");
            }
            List<AssociationRef> coexecutors = nodeService.getTargetAssocs(errand, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS);
            if (coexecutors != null) {
                for (AssociationRef assoc: coexecutors) {
                    stateMachineService.grandDynamicRoleForEmployee(document, assoc.getTargetRef(), "RESOLUTION_CHILD_ERRANDS_CO_EXECUTOR");
                }
            }

            //Добавление создателя резолюции в участники дочернего поручения
            List<AssociationRef> resolutionCreatorsAssoc = nodeService.getTargetAssocs(document, DocumentService.ASSOC_AUTHOR);
            if (resolutionCreatorsAssoc != null) {
                for (AssociationRef resolutionCreator: resolutionCreatorsAssoc) {
                    documentMembersService.addMemberWithoutCheckPermission(errand, resolutionCreator.getTargetRef(), new HashMap<QName, Serializable>(), true);
                }
            }
        }

        grandDynamicRoleForBaseDoc(associationRef, "RESOLUTION_CHILD_ERRANDS_EXECUTOR");
    }

    public void onCreateErrandExecutorAssociation(AssociationRef associationRef) {
        grandDynamicRoleForBaseDoc(associationRef, "RESOLUTION_CHILD_ERRANDS_EXECUTOR");
    }

    public void onDeleteErrandExecutorAssociation(AssociationRef associationRef) {
        revokeDynamicRoleForBaseDoc(associationRef, "RESOLUTION_CHILD_ERRANDS_EXECUTOR");
    }

    public void onCreateErrandCoExecutorAssociation(AssociationRef associationRef) {
        grandDynamicRoleForBaseDoc(associationRef, "RESOLUTION_CHILD_ERRANDS_CO_EXECUTOR");
    }

    public void onDeleteErrandCoExecutorAssociation(AssociationRef associationRef) {
        revokeDynamicRoleForBaseDoc(associationRef, "RESOLUTION_CHILD_ERRANDS_CO_EXECUTOR");
    }

    private void grandDynamicRoleForBaseDoc(AssociationRef associationRef, String roleId) {
        NodeRef errands = associationRef.getSourceRef();
        NodeRef employee = associationRef.getTargetRef();

        NodeRef baseDocument = errandsService.getBaseDocument(errands);
        if (baseDocument != null && ResolutionsService.TYPE_RESOLUTION_DOCUMENT.equals(nodeService.getType(baseDocument))) {
            stateMachineService.grandDynamicRoleForEmployee(baseDocument, employee, roleId);
        }
    }

    private void revokeDynamicRoleForBaseDoc(AssociationRef associationRef, String roleId) {
        NodeRef errands = associationRef.getSourceRef();
        NodeRef employee = associationRef.getTargetRef();

        NodeRef baseDocument = errandsService.getBaseDocument(errands);
        if (baseDocument != null && ResolutionsService.TYPE_RESOLUTION_DOCUMENT.equals(nodeService.getType(baseDocument))) {
            lecmPermissionService.revokeDynamicRole(roleId, baseDocument, employee.getId());
        }
    }
}
