package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;

/**
 * Created by ABurlakov on 20.02.2017.
 */
public class EDSBaseDocumentTypePolicy implements NodeServicePolicies.OnCreateAssociationPolicy,
        NodeServicePolicies.OnDeleteAssociationPolicy {

    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private NamespaceService namespaceService;
    private EDSDocumentService edsDocumentService;
    private DocumentService documentService;

    private String type;
    private String assocBaseDocumentType;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setEdsDocumentService(EDSDocumentService edsDocumentService) {
        this.edsDocumentService = edsDocumentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAssocBaseDocumentType(String assocBaseDocumentType) {
        this.assocBaseDocumentType = assocBaseDocumentType;
    }

    final public void init() {
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                QName.createQName(type, namespaceService),
                QName.createQName(assocBaseDocumentType, namespaceService),
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                QName.createQName(type, namespaceService),
                QName.createQName(assocBaseDocumentType, namespaceService),
                new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        setBaseDocumentType(associationRef.getSourceRef(), associationRef.getTargetRef(), true);
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        setBaseDocumentType(associationRef.getSourceRef(), associationRef.getTargetRef(), false);
    }

    private void setBaseDocumentType(NodeRef documentRef, NodeRef baseDocumentRef, boolean isCreate) {
        String docType = new String();
        if (isCreate) {
            docType = (String) nodeService.getProperty(baseDocumentRef, DocumentService.PROP_DOCUMENT_TYPE);
        } else {
            docType = "Без документа-основания";
        }

        nodeService.setProperty(documentRef, EDSDocumentService.PROP_BASE_DOCUMENT_TYPE, docType);
    }
}
