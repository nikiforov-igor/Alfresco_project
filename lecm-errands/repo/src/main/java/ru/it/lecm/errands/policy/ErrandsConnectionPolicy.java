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
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
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
    private NodeService nodeService;
    private DocumentService documentService;
    private DocumentMembersService documentMembersService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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

        //обновление номера документа-основания в поручении
        NodeRef baseDoc = associationRef.getTargetRef();
        NodeRef errandDoc = associationRef.getSourceRef();

        QName type = nodeService.getType(baseDoc);
        if (type.equals(ErrandsService.TYPE_ERRANDS)){
            NodeRef initiatorRef = nodeService.getTargetAssocs(baseDoc, ErrandsService.ASSOC_ERRANDS_INITIATOR).get(0).getTargetRef();
            documentMembersService.addMemberWithoutCheckPermission(errandDoc, initiatorRef, "LECM_BASIC_PG_Reader");
        }

        QName[] regNums = documentService.getRegNumbersProperties(nodeService.getType(baseDoc));
        if (regNums != null && regNums.length > 0) {
            String regNumberValue = "";
            for (QName propName : regNums) {
                Object regNumber = nodeService.getProperty(baseDoc, propName);
                if (regNumber != null) {
                    regNumberValue += ((regNumberValue.length() > 0 ? "," : "") + regNumber);
                }
            }
            nodeService.setProperty(errandDoc, ErrandsService.PROP_BASE_DOC_NUMBER, regNumberValue);
        }
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }
}
