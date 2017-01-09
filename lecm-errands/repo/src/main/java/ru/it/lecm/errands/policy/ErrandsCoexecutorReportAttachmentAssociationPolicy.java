package ru.it.lecm.errands.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by APanyukov on 09.12.2016.
 */
public class ErrandsCoexecutorReportAttachmentAssociationPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    final static protected Logger logger = LoggerFactory.getLogger(ErrandsCoexecutorReportConnectedDocumentAssociationPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentTableService documentTableService;
    private OrgstructureBean orgstructureService;
    private DocumentAttachmentsService documentAttachmentsService;

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

    public DocumentTableService getDocumentTableService() {
        return documentTableService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public DocumentAttachmentsService getDocumentAttachmentsService() {
        return documentAttachmentsService;
    }

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public OrgstructureBean getOrgstructureService() {
        return orgstructureService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "documentTableService", documentTableService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
        PropertyCheck.mandatory(this, "documentAttachmentsService", documentAttachmentsService);


        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_COEXECUTOR_REPORT, ErrandsService.ASSOC_ERRANDS_TS_ATTACHMENT, new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_COEXECUTOR_REPORT, ErrandsService.ASSOC_ERRANDS_TS_ATTACHMENT, new JavaBehaviour(this, "onDeleteAssociation"));

    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        NodeRef attachment = associationRef.getTargetRef();
        NodeRef report = associationRef.getSourceRef();
        NodeRef errandDoc = documentTableService.getDocumentByTableDataRow(report);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss.SSS");
        String dateString = dateFormat.format(new Date());
        NodeRef currentUser = orgstructureService.getCurrentEmployee();
        String attachmentName = "Отчет соисполнителя " + nodeService.getProperty(currentUser, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME) + ", от " + dateString;
        nodeService.setProperty(attachment, ContentModel.PROP_NAME, attachmentName);
        NodeRef category = documentAttachmentsService.getCategory("Отчеты соисполнителей", errandDoc);
        if (category != null) {
            documentAttachmentsService.addAttachment(attachment, category);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        NodeRef attachment = associationRef.getTargetRef();
        documentAttachmentsService.deleteAttachment(attachment);
    }
}