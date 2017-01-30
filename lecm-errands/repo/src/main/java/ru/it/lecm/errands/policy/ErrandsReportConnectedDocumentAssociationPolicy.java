package ru.it.lecm.errands.policy;

import org.alfresco.model.ContentModel;
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
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.errands.ErrandsService;

import java.util.List;

/**
 * Created by APanyukov on 09.12.2016.
 */
public class ErrandsReportConnectedDocumentAssociationPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    final static protected Logger logger = LoggerFactory.getLogger(ErrandsReportConnectedDocumentAssociationPolicy.class);

    private PolicyComponent policyComponent;
    private DocumentConnectionService documentConnectionService;
    private NodeService nodeService;
    private DocumentTableService documentTableService;
    private QName reportTypeQname;
    private QName associationQname;

    public QName getAssociationQname() {
        return associationQname;
    }

    public void setAssociationQname(QName associationQname) {
        this.associationQname = associationQname;
    }

    public QName getReportTypeQname() {
        return reportTypeQname;
    }

    public void setReportTypeQname(QName reportTypeQname) {
        this.reportTypeQname = reportTypeQname;
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

    public DocumentConnectionService getDocumentConnectionService() {
        return documentConnectionService;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
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
        PropertyCheck.mandatory(this, "documentConnectionService", documentConnectionService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                reportTypeQname, associationQname, new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                reportTypeQname, associationQname, new JavaBehaviour(this, "onDeleteAssociation"));

    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {

        NodeRef reportNodeRef = associationRef.getSourceRef();
        NodeRef connectedDoc = associationRef.getTargetRef();
        NodeRef errandNodeRef = documentTableService.getDocumentByTableDataRow(reportNodeRef);
        String connectedDocName = (String) nodeService.getProperty(connectedDoc, ContentModel.PROP_NAME);

        //создаем связь если не было.
        boolean conExist = false;
        List<NodeRef> connections = documentConnectionService.getConnectionsWithDocument(errandNodeRef, ErrandsService.ERRANDS_REPORT_CONNECTION_TYPE);
        for (NodeRef con : connections) {
            NodeRef conDoc = nodeService.getTargetAssocs(con, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT).get(0).getTargetRef();
            if (conDoc.equals(connectedDoc)) {
                conExist = true;
            }
        }
        if (!conExist) {
            documentConnectionService.createConnection(errandNodeRef, connectedDoc, ErrandsService.ERRANDS_REPORT_CONNECTION_TYPE, false);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        NodeRef reportNodeRef = associationRef.getSourceRef();
        NodeRef connectedDoc = associationRef.getTargetRef();
        NodeRef errandNodeRef = documentTableService.getDocumentByTableDataRow(reportNodeRef);

        //проверяем использование документа в других отчетах
        NodeRef tableData = nodeService.getParentAssocs(reportNodeRef).get(0).getParentRef();
        List<NodeRef> allReports = documentTableService.getTableDataRows(tableData);
        boolean docIsUsed = false;
        for (NodeRef report : allReports) {
            if (!report.equals(reportNodeRef)) {
                List<AssociationRef> reportConnectedDocumentAssoc = nodeService.getTargetAssocs(report, associationQname);
                for (AssociationRef reportConnectedDocumentAssocRef : reportConnectedDocumentAssoc) {
                    NodeRef reportConnectedDocument = reportConnectedDocumentAssocRef.getTargetRef();
                    if (reportConnectedDocument.equals(connectedDoc)) {
                        docIsUsed = true;
                    }
                }
            }

        }
        //удаляем связь если не нужна
        if (!docIsUsed) {
            //ищем нужную связь
            List<NodeRef> connections = documentConnectionService.getConnections(errandNodeRef);
            for (NodeRef con : connections) {
                NodeRef conDoc = nodeService.getTargetAssocs(con, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT).get(0).getTargetRef();
                if (conDoc.equals(connectedDoc)) {
                    documentConnectionService.deleteConnection(con);
                }
            }
        }
    }
}
