package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.List;

/**
 * Created by APanyukov on 09.12.2016.
 */
public class ErrandsReportAttachmentAssociationPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    final static protected Logger logger = LoggerFactory.getLogger(ErrandsReportConnectedDocumentAssociationPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentTableService documentTableService;
    private OrgstructureBean orgstructureService;
    private DocumentAttachmentsService documentAttachmentsService;
    private QName reportTypeQname;
    private QName associationQname;

    public QName getReportTypeQname() {
        return reportTypeQname;
    }

    public void setReportTypeQname(QName reportTypeQname) {
        this.reportTypeQname = reportTypeQname;
    }

    public QName getAssociationQname() {
        return associationQname;
    }

    public void setAssociationQname(QName associationQname) {
        this.associationQname = associationQname;
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
                reportTypeQname, associationQname, new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                reportTypeQname, associationQname, new JavaBehaviour(this, "onDeleteAssociation"));

    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        NodeRef attachment = associationRef.getTargetRef();
        NodeRef errandDoc = null;
        if (nodeService.getType(associationRef.getSourceRef()).equals(ErrandsService.TYPE_ERRANDS)){
            errandDoc = associationRef.getSourceRef();
        } else {
            errandDoc = documentTableService.getDocumentByTableDataRow(associationRef.getSourceRef());
        }
        NodeRef category = documentAttachmentsService.getCategory("Исполнение", errandDoc);
        if (category != null) {
            documentAttachmentsService.addAttachment(attachment, category);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        NodeRef attachment = associationRef.getTargetRef();
        NodeRef reportNodeRef = associationRef.getSourceRef();
        NodeRef errandDoc = null;
        if (nodeService.getType(reportNodeRef).equals(ErrandsService.TYPE_ERRANDS)) {
            errandDoc = reportNodeRef;
        } else {
            errandDoc = documentTableService.getDocumentByTableDataRow(reportNodeRef);
        }
        //проверяем использование вложения в других отчетах
        NodeRef coexecutorsTableData = nodeService.getTargetAssocs(errandDoc, ErrandsService.ASSOC_ERRANDS_TS_COEXECUTOR_REPORTS).get(0).getTargetRef();
        NodeRef executionTableData = nodeService.getTargetAssocs(errandDoc, ErrandsService.ASSOC_ERRANDS_TS_EXECUTION_REPORTS).get(0).getTargetRef();
        Boolean attachmentIsUsed = isAttachmentUsedInTable(coexecutorsTableData, ErrandsService.ASSOC_ERRANDS_TS_COEXECUTOR_ATTACHMENT, reportNodeRef, attachment) ||
                isAttachmentUsedInTable(executionTableData, ErrandsService.ASSOC_ERRANDS_TS_EXECUTOR_ATTACHMENT, reportNodeRef, attachment);

        if (!attachmentIsUsed) {
            documentAttachmentsService.deleteAttachment(attachment);
        }
    }

    private boolean isAttachmentUsedInTable(NodeRef tableData, QName associationQname, NodeRef reportNodeRef, NodeRef attachment) {
        List<NodeRef> allReports = documentTableService.getTableDataRows(tableData);
        boolean attachmentIsUsed = false;
        for (NodeRef report : allReports) {
            if (!report.equals(reportNodeRef)) {
                List<AssociationRef> reportAttachmentAssoc = nodeService.getTargetAssocs(report, associationQname);
                for (AssociationRef reportAttachmentAssocRef : reportAttachmentAssoc) {
                    NodeRef reportAttachment = reportAttachmentAssocRef.getTargetRef();
                    if (reportAttachment.equals(attachment)) {
                        attachmentIsUsed = true;
                        break;
                    }
                }
            }
            if (attachmentIsUsed) {
                break;
            }
        }
        return attachmentIsUsed;
    }
}
