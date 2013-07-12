package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.errands.ErrandsService;

/**
 * User: mshafeev
 * Date: 11.07.13
 * Time: 15:51
 */
public class ErrandsConnectionPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
    final static protected Logger logger = LoggerFactory.getLogger(ErrandsConnectionPolicy.class);

    private PolicyComponent policyComponent;
    private DocumentConnectionService documentConnectionService;
    private DictionaryBean dictionaryService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation"));
    }

    /**
     * Добавление связи при создании поручения на основании документа
     */
    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        NodeRef connectionType = dictionaryService.getDictionaryValueByParam(
                DocumentConnectionService.DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME,
                DocumentConnectionService.PROP_CONNECTION_TYPE_CODE,
                DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE);

        if (connectionType != null) {
            documentConnectionService.createConnection(associationRef.getTargetRef(), associationRef.getSourceRef(), connectionType, true);
        }
    }


}
