package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.notification.NotificationService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;

/**
 * Created by APanyukov on 18.01.2017.
 */
public class EDSExecutorPolicy extends LogicECMAssociationPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private static final String GRAND_DYNAMIC_ROLE_CODE_INITIATOR = "BR_INITIATOR";

    private LecmPermissionService lecmPermissionService;
    private NotificationsService notificationsService;
    private AuthenticationService authenticationService;
    private OrgstructureBean orgstructureService;

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

    @Override
    public final void init() {
        super.init();
        PropertyCheck.mandatory(this, "authenticationService", authenticationService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
        PropertyCheck.mandatory(this, "lecmPermissionService", lecmPermissionService);
        PropertyCheck.mandatory(this, "notificationsService", notificationsService);
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                EDSDocumentService.TYPE_EDS_DOCUMENT, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                EDSDocumentService.TYPE_EDS_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation"));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                EDSDocumentService.TYPE_EDS_DOCUMENT, new JavaBehaviour(this, "onUpdateProperties"));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String oldExecutorRef = (String) before.get(EDSDocumentService.PROP_EXECUTOR_REF);
        String newExecutorRef = (String) after.get(EDSDocumentService.PROP_EXECUTOR_REF);
        NodeRef oldExecutor = null;
        NodeRef newExecutor = null;
        if (oldExecutorRef != null) {
            oldExecutor = new NodeRef(oldExecutorRef);
        }
        if (newExecutorRef != null) {
            newExecutor = new NodeRef(newExecutorRef);
        }
        if (!Objects.equals(oldExecutorRef, newExecutorRef)) {
            String author = authenticationService.getCurrentUserName();
            NodeRef initiator = orgstructureService.getCurrentEmployee();
            Map<String, Object> templateConfig = new HashMap<>();
            templateConfig.put("mainObject", nodeRef);
            if (newExecutor != null && nodeService.exists(newExecutor)) {
                lecmPermissionService.grantDynamicRole(GRAND_DYNAMIC_ROLE_CODE_INITIATOR, nodeRef, newExecutor.getId(), "LECM_BASIC_PG_Initiator");
                notificationsService.sendNotification(author, initiator, Collections.singletonList(newExecutor), "EDS_EXECUTOR_NEW", templateConfig, true);
            }
            if (oldExecutor != null && nodeService.exists(oldExecutor)) {
                lecmPermissionService.revokeDynamicRole(GRAND_DYNAMIC_ROLE_CODE_INITIATOR, nodeRef, oldExecutor.getId());
                notificationsService.sendNotification(author, initiator, Collections.singletonList(oldExecutor), "EDS_EXECUTOR_OLD", templateConfig, true);
            }
        }
    }
}
