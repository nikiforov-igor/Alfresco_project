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
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.List;

/**
 * Created by APanyukov on 09.12.2016.
 */
public class ErrandsCoexecutorReportConnectedDocumentAssociationPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    final static protected Logger logger = LoggerFactory.getLogger(ErrandsCoexecutorReportConnectedDocumentAssociationPolicy.class);

    private PolicyComponent policyComponent;
    private DocumentConnectionService documentConnectionService;
    private NodeService nodeService;
    private DocumentTableService documentTableService;
    private ErrandsService errandsService;

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
        PropertyCheck.mandatory(this, "errandsService", errandsService);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_COEXECUTOR_REPORT, ErrandsService.ASSOC_ERRANDS_TS_CONNECTED_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_COEXECUTOR_REPORT, ErrandsService.ASSOC_ERRANDS_TS_CONNECTED_DOCUMENT, new JavaBehaviour(this, "onDeleteAssociation"));

    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {

        NodeRef reportNodeRef = associationRef.getSourceRef();
        NodeRef connectedDoc = associationRef.getTargetRef();
        NodeRef errandNodeRef = documentTableService.getDocumentByTableDataRow(reportNodeRef);
        String connectedDocName = (String) nodeService.getProperty(connectedDoc, ContentModel.PROP_NAME);


        NodeRef link = errandsService.getCoexecutorReportLink(errandNodeRef, connectedDocName, connectedDoc);
        //добавляем ассоциацию
        nodeService.createAssociation(reportNodeRef, link, ErrandsService.ASSOC_ERRANDS_TS_CONNECTED_DOCUMENT_LINK);

        //создаем связь не было.
        boolean conExist = false;
        List<NodeRef> connections = documentConnectionService.getConnectionsWithDocument(errandNodeRef, ErrandsService.ERRANDS_COEXECUTOR_REPORT_CONNECTION_TYPE);
        for (NodeRef con : connections) {
            NodeRef conDoc = nodeService.getTargetAssocs(con, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT).get(0).getTargetRef();
            if (conDoc.equals(connectedDoc)) {
                conExist = true;
            }
        }
        if (!conExist) {
            documentConnectionService.createConnection(errandNodeRef, connectedDoc, ErrandsService.ERRANDS_COEXECUTOR_REPORT_CONNECTION_TYPE, false);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        NodeRef reportNodeRef = associationRef.getSourceRef();
        NodeRef connectedDoc = associationRef.getTargetRef();
        NodeRef errandNodeRef = documentTableService.getDocumentByTableDataRow(reportNodeRef);
        String connectedDocName = (String) nodeService.getProperty(connectedDoc, ContentModel.PROP_NAME);

        //получаем ссылку на документ из ассоциации текущего отчета
        NodeRef currentLink = null;
        List<AssociationRef> reportLinksAssoc = nodeService.getTargetAssocs(reportNodeRef, ErrandsService.ASSOC_ERRANDS_TS_CONNECTED_DOCUMENT_LINK);
        for (AssociationRef rlar : reportLinksAssoc) {
            NodeRef link = rlar.getTargetRef();
            if (nodeService.getProperty(link, ContentModel.PROP_NAME).equals(connectedDocName)) {
                currentLink = link;
                break;
            }
        }
        //проверяем использование ссылки в других отчетах
        NodeRef tableData = nodeService.getParentAssocs(reportNodeRef).get(0).getParentRef();
        List<NodeRef> allReports = documentTableService.getTableDataRows(tableData);
        boolean linkIsUsed = false;
        for (NodeRef r : allReports) {
            if (!r.equals(reportNodeRef)) {
                List<AssociationRef> rLinksAssoc = nodeService.getTargetAssocs(r, ErrandsService.ASSOC_ERRANDS_TS_CONNECTED_DOCUMENT_LINK);
                for (AssociationRef rlar : rLinksAssoc) {
                    NodeRef rl = rlar.getTargetRef();
                    if (rl.equals(currentLink)) {
                        linkIsUsed = true;
                    }
                }
            }

        }
        //удаляем ассоциацию
        nodeService.removeAssociation(reportNodeRef, currentLink, ErrandsService.ASSOC_ERRANDS_TS_CONNECTED_DOCUMENT_LINK);
        //удаляем  ссылку и связь
        if (!linkIsUsed) {
            nodeService.deleteNode(currentLink);
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
