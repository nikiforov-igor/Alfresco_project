package ru.it.lecm.resolutions.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.resolutions.api.ResolutionsService;

/**
 * User: AIvkin
 * Date: 21.02.2017
 * Time: 9:51
 */
public class ResolutionBaseDocumentPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
    private PolicyComponent policyComponent;
    private DocumentConnectionService documentConnectionService;
    private NodeService nodeService;
    private ErrandsService errandsService;
    private ResolutionsService resolutionsService;
    private DocumentMembersService documentMembersService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setResolutionsService(ResolutionsService resolutionsService) {
        this.resolutionsService = resolutionsService;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ResolutionsService.TYPE_RESOLUTION_DOCUMENT, ResolutionsService.ASSOC_BASE_DOCUMENT,
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        AuthenticationUtil.pushAuthentication();
        AuthenticationUtil.setRunAsUserSystem();
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        try {
            documentConnectionService.createConnection(associationRef.getTargetRef(), associationRef.getSourceRef(), DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, true, true);
            NodeRef parentDoc = associationRef.getSourceRef();
            while (parentDoc != null) {
                QName parentType = nodeService.getType(parentDoc);
                NodeRef initiatorRef = null;
                if (parentType.equals(ErrandsService.TYPE_ERRANDS)) {
                    initiatorRef = nodeService.getTargetAssocs(parentDoc, ErrandsService.ASSOC_ERRANDS_INITIATOR).get(0).getTargetRef();
                    parentDoc = errandsService.getBaseDocument(parentDoc);
                } else if (parentType.equals(ResolutionsService.TYPE_RESOLUTION_DOCUMENT)) {
                    initiatorRef = nodeService.getTargetAssocs(parentDoc, ResolutionsService.ASSOC_AUTHOR).get(0).getTargetRef();
                    parentDoc = resolutionsService.getResolutionBase(parentDoc);
                }
                if (initiatorRef != null) {
                    documentMembersService.addMemberWithoutCheckPermission(associationRef.getSourceRef(), initiatorRef, "LECM_BASIC_PG_Reader", true);
                }
            }
        } finally {
            AuthenticationUtil.popAuthentication();
        }
    }
}
