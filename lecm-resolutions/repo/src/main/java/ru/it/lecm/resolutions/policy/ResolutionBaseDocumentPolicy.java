package ru.it.lecm.resolutions.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.resolutions.api.ResolutionsService;

/**
 * User: AIvkin
 * Date: 21.02.2017
 * Time: 9:51
 */
public class ResolutionBaseDocumentPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
    private PolicyComponent policyComponent;
    private DocumentConnectionService documentConnectionService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ResolutionsService.TYPE_RESOLUTION_DOCUMENT, ResolutionsService.ASSOC_BASE_DOCUMENT,
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        documentConnectionService.createConnection(associationRef.getTargetRef(), associationRef.getSourceRef(), DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, true, true);
    }
}
