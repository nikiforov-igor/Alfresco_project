package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.LecmURLService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.*;

/**
 * Created by APanyukov on 08.02.2017.
 */
public class ErrandsExecutionReportUpdatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    final static protected Logger logger = LoggerFactory.getLogger(ErrandsExecutionReportUpdatePolicy.class);

    private NodeService nodeService;
    private OrgstructureBean orgstructureService;
    private DocumentService documentService;
    private ErrandsService errandsService;
    private LecmURLService lecmURLService;
    private StateMachineServiceBean stateMachineServiceBean;
    private EDSDocumentService edsDocumentService;
    private NotificationsService notificationsService;
    private AuthenticationService authenticationService;
    private PolicyComponent policyComponent;

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

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public ErrandsService getErrandsService() {
        return errandsService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public LecmURLService getLecmURLService() {
        return lecmURLService;
    }

    public void setLecmURLService(LecmURLService lecmURLService) {
        this.lecmURLService = lecmURLService;
    }

    public StateMachineServiceBean getStateMachineServiceBean() {
        return stateMachineServiceBean;
    }

    public void setStateMachineServiceBean(StateMachineServiceBean stateMachineServiceBean) {
        this.stateMachineServiceBean = stateMachineServiceBean;
    }

    public EDSDocumentService getEdsDocumentService() {
        return edsDocumentService;
    }

    public void setEdsDocumentService(EDSDocumentService edsDocumentService) {
        this.edsDocumentService = edsDocumentService;
    }

    public NotificationsService getNotificationsService() {
        return notificationsService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "errandsService", errandsService);
        PropertyCheck.mandatory(this, "notificationsService", notificationsService);
        PropertyCheck.mandatory(this, "authenticationService", authenticationService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
        PropertyCheck.mandatory(this, "stateMachineServiceBean", stateMachineServiceBean);
        PropertyCheck.mandatory(this, "lecmURLService", lecmURLService);
        PropertyCheck.mandatory(this, "documentService", documentService);
        PropertyCheck.mandatory(this, "edsDocumentService", edsDocumentService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, new JavaBehaviour(this, "onUpdateProperties"));

    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        Boolean oldIsExecute = (Boolean) before.get(ErrandsService.PROP_ERRANDS_EXECUTION_REPORT_IS_EXECUTE);
        Boolean newIsExecute = (Boolean) after.get(ErrandsService.PROP_ERRANDS_EXECUTION_REPORT_IS_EXECUTE);
        if (oldIsExecute != null && newIsExecute != null && !oldIsExecute && newIsExecute) {
            Boolean reportRequired = (Boolean) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_REPORT_REQUIRED);
            NodeRef currentUser = orgstructureService.getCurrentEmployee();
            Boolean closeChild = (Boolean) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_EXECUTION_REPORT_CLOSE_CHILD);
            NodeRef author = nodeService.getTargetAssocs(nodeRef, ErrandsService.ASSOC_ERRANDS_INITIATOR).get(0).getTargetRef();
            NodeRef controller = null;
            List<AssociationRef> controllerAssoc = nodeService.getTargetAssocs(nodeRef, ErrandsService.ASSOC_ERRANDS_CONTROLLER);
            if (controllerAssoc != null && controllerAssoc.size() != 0) {
                controller = controllerAssoc.get(0).getTargetRef();
            }
            List<NodeRef> recipients = new ArrayList<>();

            String notificationTemplateCode;
            if (closeChild) {
                String reason = "Завершено исполнением поручения-основания ";
                reason += lecmURLService.wrapperLink(nodeRef, (String) nodeService.getProperty(nodeRef, DocumentService.PROP_PRESENT_STRING), documentService.getDocumentUrl(nodeRef));
                List<NodeRef> children = new ArrayList<>(errandsService.getChildErrands(nodeRef));
                children.addAll(errandsService.getChildResolutions(nodeRef));
                String finalReason = reason;
                children.stream()
                        .filter(child -> (!stateMachineServiceBean.isFinal(child) && !stateMachineServiceBean.isDraft(child)))
                        .forEach(child -> edsDocumentService.sendCompletionSignal(child, finalReason, currentUser));

            }
            if (!reportRequired) {
                nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_EXECUTION_REPORT_STATUS, "ACCEPT");
                notificationTemplateCode = "ERRANDS_EXECUTED_WITHOUT_REPORT";
                if (author != null) {
                    recipients.add(author);
                }
                if (controller != null) {
                    recipients.add(controller);
                }
                NodeRef additionalDocument = errandsService.getAdditionalDocumentNode(nodeRef);
                if (additionalDocument != null) {
                    String reportText = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_EXECUTION_REPORT);
                    nodeService.setProperty(additionalDocument, EDSDocumentService.PROP_COMPLETION_SIGNAL_REASON, reportText);
                }
                nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_EXECUTE_RESULT, "executed");
            } else {
                nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_EXECUTION_REPORT_STATUS, "ONCONTROL");
                String reportRecipientType = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_REPORT_RECIPIENT_TYPE);
                notificationTemplateCode = "ERRANDS_EXECUTED_WITH_REPORT";
                if (Objects.equals(reportRecipientType, "AUTHOR") && author != null) {
                    recipients.add(author);
                } else if (Objects.equals(reportRecipientType, "CONTROLLER") && controller != null) {
                    recipients.add(controller);
                } else if (Objects.equals(reportRecipientType, "AUTHOR_AND_CONTROLLER")) {
                    if (author != null) {
                        recipients.add(author);
                    }
                    if (controller != null) {
                        recipients.add(controller);
                    }
                }
                nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_EXECUTE_RESULT, "onControl");
            }
            Map<String, Object> templateConfig = new HashMap<>();
            templateConfig.put("mainObject", nodeRef);
            templateConfig.put("eventExecutor", currentUser);
            notificationsService.sendNotification(authenticationService.getCurrentUserName(), currentUser, recipients, notificationTemplateCode, templateConfig, true);
            nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_EXECUTION_REPORT_IS_EXECUTE, false);
        }
    }
}
