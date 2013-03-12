package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;

import java.io.Serializable;

/**
 * User: dbashmakov
 * Date: 12.03.13
 * Time: 16:54
 */
public class DocumentMembersPolicy implements NodeServicePolicies.OnCreateAssociationPolicy,
        NodeServicePolicies.OnDeleteAssociationPolicy,
        NodeServicePolicies.OnCreateNodePolicy{

    final protected Logger logger = LoggerFactory.getLogger(DocumentMembersPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentMembersService documentMembersService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
                new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
                new JavaBehaviour(this, "onDeleteAssociation"));
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        logger.debug("!!!Here are given permission to document!!!");
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef nodeRef = childAssocRef.getChildRef();
        Serializable newName = documentMembersService.generateMemberNodeName(nodeRef);
        nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, newName);
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        logger.debug("!!!Here are taken permission to the document!!!");
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }
}
