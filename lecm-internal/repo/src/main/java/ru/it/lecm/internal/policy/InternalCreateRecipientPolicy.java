package ru.it.lecm.internal.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.internal.model.InternalModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 11.03.14
 * Time: 16:02
 */
public class InternalCreateRecipientPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    private PolicyComponent policyComponent;
    private EDSGlobalSettingsService edsGlobalSettingsService;
    private LecmPermissionService permissionService;
    private OrgstructureBean orgstructureService;

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                InternalModel.TYPE_INTERNAL, EDSDocumentService.ASSOC_RECIPIENTS, new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                InternalModel.TYPE_INTERNAL, EDSDocumentService.ASSOC_RECIPIENTS, new JavaBehaviour(this, "onCreateAssociation"));
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        if (edsGlobalSettingsService.isHideProperties()) {
            NodeRef employeeRef = nodeAssocRef.getTargetRef();
            NodeRef internalRef = nodeAssocRef.getSourceRef();

            LecmPermissionService.LecmPermissionGroup lpg = permissionService.findPermissionGroup("LECM_BASIC_PG_ActionPerformer_Lite");
            if (lpg != null) {
                /*if (orgstructureService.isEmployee(employeeRef)) {*/
                permissionService.grantAccess(lpg, internalRef, employeeRef);
                /*} else {
                    if (orgstructureService.isUnit(employeeRef)) {
                        List<NodeRef> employees = orgstructureService.getUnitEmployees(employeeRef);
                        for (NodeRef employee : employees) {
                            permissionService.grantAccess(lpg, internalRef, employee);
                        }
                    }
                }*/
            }
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        NodeRef employeeRef = nodeAssocRef.getTargetRef();
        NodeRef internalRef = nodeAssocRef.getSourceRef();
        LecmPermissionService.LecmPermissionGroup lpg = permissionService.findPermissionGroup("LECM_BASIC_PG_ActionPerformer_Lite");
        if (lpg != null) {
            /*if (orgstructureService.isEmployee(employeeRef)) {*/
            permissionService.revokeAccess(lpg, internalRef, employeeRef);
            /*} else {
                if (orgstructureService.isUnit(employeeRef)) {
                    List<NodeRef> employees = orgstructureService.getUnitEmployees(employeeRef);
                    for (NodeRef employee : employees) {
                        permissionService.revokeAccess(lpg, internalRef, employee);
                    }
                }
            }*/
        }
    }

    public void setPermissionService(LecmPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
        this.edsGlobalSettingsService = edsGlobalSettingsService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }
}
