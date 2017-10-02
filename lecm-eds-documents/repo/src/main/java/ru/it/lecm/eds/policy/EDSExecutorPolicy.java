package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.notification.NotificationService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;

/**
 * Created by APanyukov on 18.01.2017.
 */
public class EDSExecutorPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    private static final String GRAND_DYNAMIC_ROLE_CODE_INITIATOR = "BR_INITIATOR";
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private LecmPermissionService lecmPermissionService;
    private NotificationsService notificationsService;
    private AuthenticationService authenticationService;
    private OrgstructureBean orgstructureService;
    private DocumentService documentService;
    private StateMachineServiceBean stateMachineService;
    private LecmBasePropertiesService propertiesService;

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public OrgstructureBean getOrgstructureService() {
        return orgstructureService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    public NotificationsService getNotificationsService() {
        return notificationsService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public LecmPermissionService getLecmPermissionService() {
        return lecmPermissionService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public StateMachineServiceBean getStateMachineService() {
        return stateMachineService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
        PropertyCheck.mandatory(this, "stateMachineService", stateMachineService);
        PropertyCheck.mandatory(this, "lecmPermissionService", lecmPermissionService);
        PropertyCheck.mandatory(this, "notificationsService", notificationsService);
        PropertyCheck.mandatory(this, "propertiesService", propertiesService);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                EDSDocumentService.TYPE_EDS_DOCUMENT, EDSDocumentService.ASSOC_EXECUTOR, new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                EDSDocumentService.TYPE_EDS_DOCUMENT, EDSDocumentService.ASSOC_EXECUTOR, new JavaBehaviour(this, "onDeleteAssociation"));

    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.bj.enabled");
        boolean enabled;
        if (editorEnabled == null) {
            enabled = true;
        } else {
            enabled = Boolean.valueOf((String) editorEnabled);
        }
        if (enabled) {
            NodeRef documentRef = associationRef.getSourceRef();
            NodeRef docExecutorRef = associationRef.getTargetRef();
            NodeRef docAuthorRef = documentService.getDocumentAuthor(documentRef);
            String author = authenticationService.getCurrentUserName();
            NodeRef initiator = orgstructureService.getCurrentEmployee();
            Map<String, Object> templateConfig = new HashMap<>();
            templateConfig.put("mainObject", documentRef);
            stateMachineService.grandDynamicRoleForEmployee(documentRef, docExecutorRef, GRAND_DYNAMIC_ROLE_CODE_INITIATOR);
            if (docAuthorRef != null && !docAuthorRef.equals(docExecutorRef) && nodeService.getProperty(documentRef, StatemachineModel.PROP_STATUS) != null) {
                notificationsService.sendNotification(author, initiator, Collections.singletonList(docExecutorRef), "EDS_EXECUTOR_NEW", templateConfig, true);
            }
        }
        else {
            throw new RuntimeException("Property ru.it.lecm.properties.bj.enabled not found");
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.bj.enabled");
        boolean enabled;
        if (editorEnabled == null) {
            enabled = true;
        } else {
            enabled = Boolean.valueOf((String) editorEnabled);
        }
        if (enabled) {
            NodeRef documentRef = associationRef.getSourceRef();
            NodeRef docExecutorRef = associationRef.getTargetRef();
            NodeRef docCompilerRef = documentService.getDocumentAuthor(documentRef);
            String author = authenticationService.getCurrentUserName();
            NodeRef initiator = orgstructureService.getCurrentEmployee();
            Map<String, Object> templateConfig = new HashMap<>();
            templateConfig.put("mainObject", documentRef);
            if (!docCompilerRef.equals(docExecutorRef)) {
                lecmPermissionService.revokeDynamicRole(GRAND_DYNAMIC_ROLE_CODE_INITIATOR, documentRef, docExecutorRef.getId());
            }
            notificationsService.sendNotification(author, initiator, Collections.singletonList(docExecutorRef), "EDS_EXECUTOR_OLD", templateConfig, true);
        }
        else {
            throw new RuntimeException("Property ru.it.lecm.properties.bj.enabled not found");
        }
    }
}
