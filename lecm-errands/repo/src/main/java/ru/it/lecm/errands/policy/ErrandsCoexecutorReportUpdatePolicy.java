package ru.it.lecm.errands.policy;

import org.alfresco.model.ContentModel;
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
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.*;

/**
 * Created by APanyukov on 09.12.2016.
 */
public class ErrandsCoexecutorReportUpdatePolicy implements NodeServicePolicies.OnUpdateNodePolicy {

    final static protected Logger logger = LoggerFactory.getLogger(ErrandsCoexecutorReportUpdatePolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentTableService documentTableService;
    private ErrandsService errandsService;
    private NotificationsService notificationsService;
    private BusinessJournalService businessJournalService;
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

    public BusinessJournalService getBusinessJournalService() {
        return businessJournalService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public NotificationsService getNotificationsService() {
        return notificationsService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public ErrandsService getErrandsService() {
        return errandsService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public DocumentTableService getDocumentTableService() {
        return documentTableService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "documentTableService", documentTableService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "errandsService", errandsService);
        PropertyCheck.mandatory(this, "notificationsService", notificationsService);
        PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
        PropertyCheck.mandatory(this, "authenticationService", authenticationService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdateNodePolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_COEXECUTOR_REPORT, new JavaBehaviour(this, "onUpdateNode"));

    }

    @Override
    public void onUpdateNode(NodeRef nodeRef) {

        Boolean isRouteReport = (Boolean) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_TS_COEXECUTOR_REPORT_IS_ROUTE);
        if (isRouteReport) {
            NodeRef errandNodeRef = documentTableService.getDocumentByTableDataRow(nodeRef);
            //выставляем значения атрибутов
            nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_TS_COEXECUTOR_REPORT_STATUS, ErrandsService.ERRANDS_TS_COEXECUTOR_REPORT_STATUS.ONCONTROL.toString());
            nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_TS_COEXECUTOR_REPORT_ROUTE_DATE, new Date());

            //уведомляем исполнителя
            String author = authenticationService.getCurrentUserName();
            NodeRef initiator = orgstructureService.getCurrentEmployee();
            List<NodeRef> recipients = Collections.singletonList(nodeService.getTargetAssocs(errandNodeRef, ErrandsService.ASSOC_ERRANDS_EXECUTOR).get(0).getTargetRef());
            Map<String, Object> templateConfig = new HashMap<>();
            templateConfig.put("mainObject", errandNodeRef);
            templateConfig.put("eventExecutor", initiator);
            notificationsService.sendNotification(author, initiator, recipients, "ERRANDS_COEXEC_REPORT", templateConfig, true);

            //делаем запись в журнал
            String logText = "Соисполнитель #initiator создал отчет о выполнении поручения #mainobject ";
            businessJournalService.log(errandNodeRef, "ERRAND_COEXECUTOR_REPORT", logText);

        }
    }
}
