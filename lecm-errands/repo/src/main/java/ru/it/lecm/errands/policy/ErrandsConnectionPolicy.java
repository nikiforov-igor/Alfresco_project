package ru.it.lecm.errands.policy;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.resolutions.api.ResolutionsService;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: mshafeev
 * Date: 11.07.13
 * Time: 15:51
 */
public class ErrandsConnectionPolicy extends BaseBean implements NodeServicePolicies.OnCreateAssociationPolicy {
    final static protected Logger logger = LoggerFactory.getLogger(ErrandsConnectionPolicy.class);

    private PolicyComponent policyComponent;
    private DocumentConnectionService documentConnectionService;
    private NodeService nodeService;
    private DocumentService documentService;
    private DocumentMembersService documentMembersService;
    private ErrandsService errandsService;
    private BusinessJournalService businessJournalService;
    private ResolutionsService resolutionsService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    public void setResolutionsService(ResolutionsService resolutionsService) {
        this.resolutionsService = resolutionsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateBaseDocAssociation"));
    }

    /**
     * Добавление связи при создании поручения на основании документа
     */
    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        documentConnectionService.createConnection(associationRef.getTargetRef(), associationRef.getSourceRef(), DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, true, true);

        //обновление номеров родительского и документа-основания в поручении
        NodeRef additionalDoc = associationRef.getTargetRef();
        NodeRef errandDoc = associationRef.getSourceRef();

        Date regDate = documentService.getDocumentActualDate(additionalDoc);
        String regDateString = "";
        if (regDate != null) {
            DateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy");
            regDateString = dFormat.format(regDate);
        }
        String logText = EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.errands.bjMessages.createErrandBasedOnDoc.message", "#initiator создал(а) поручение по документу %s");
        logText = String.format(logText, wrapperLink(additionalDoc, documentService.getDocumentActualNumber(additionalDoc) + " " + EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.errands.bjMessages.createErrandBasedOnDoc.docFromParamText", "от") + " " + regDateString, documentService.getDocumentUrl(additionalDoc)));
        businessJournalService.log(additionalDoc, "CREATE_ERRAND_BASED_ON_DOC", logText, null);

        QName additionalDoctype = nodeService.getType(additionalDoc);
        NodeRef parentDoc = additionalDoc;
        while (parentDoc != null) {
            QName parentType = nodeService.getType(parentDoc);
            NodeRef initiatorRef = null;
            if (parentType.equals(ErrandsService.TYPE_ERRANDS)) {
                initiatorRef = nodeService.getTargetAssocs(parentDoc, ErrandsService.ASSOC_ERRANDS_INITIATOR).get(0).getTargetRef();
                parentDoc = errandsService.getBaseDocument(parentDoc);
            } else if (parentType.equals(ResolutionsService.TYPE_RESOLUTION_DOCUMENT)) {
                initiatorRef = nodeService.getTargetAssocs(parentDoc, ResolutionsService.ASSOC_AUTHOR).get(0).getTargetRef();
                parentDoc = resolutionsService.getResolutionBase(parentDoc);
            } else {
                parentDoc = null;
            }
            if (initiatorRef != null) {
                documentMembersService.addMemberWithoutCheckPermission(errandDoc, initiatorRef, true);
            }
        }

        if (nodeService.getProperty(errandDoc, ErrandsService.PROP_ADDITIONAL_DOC_NUMBER) == null) {
            String regNum = documentService.getDocumentActualNumber(additionalDoc);

            if (regNum == null) {
                regNum = documentService.getProjectRegNumber(additionalDoc);
            }

            if (regNum != null && !regNum.isEmpty()) {
                nodeService.setProperty(errandDoc, ErrandsService.PROP_ADDITIONAL_DOC_NUMBER, regNum);
            }
        }

        //установка ассоциации документа-основания
        List<AssociationRef> baseDocAssocRefs = null;
        baseDocAssocRefs = nodeService.getTargetAssocs(errandDoc, ErrandsService.ASSOC_BASE_DOCUMENT);
        // если документ-основание не установлен в документе, то берем ассоциацию из основания
        if (baseDocAssocRefs == null || baseDocAssocRefs.size() == 0) {
            if (additionalDoctype.equals(ErrandsService.TYPE_ERRANDS)) {
                baseDocAssocRefs = nodeService.getTargetAssocs(additionalDoc, ErrandsService.ASSOC_BASE_DOCUMENT);
            } else if (additionalDoctype.equals(ResolutionsService.TYPE_RESOLUTION_DOCUMENT)) {
                baseDocAssocRefs = nodeService.getTargetAssocs(additionalDoc, ResolutionsService.ASSOC_BASE_DOCUMENT);
            }
            //если документа-основания нет, то родительский документ  является документом основанием.
            NodeRef baseDoc;
            if (baseDocAssocRefs == null || baseDocAssocRefs.size() == 0) {
                baseDoc = additionalDoc;
            } else {
                baseDoc = baseDocAssocRefs.get(0).getTargetRef();
            }
            nodeService.createAssociation(errandDoc, baseDoc, ErrandsService.ASSOC_BASE_DOCUMENT);
        }
    }

    public void onCreateBaseDocAssociation(AssociationRef associationRef) {
        NodeRef baseDoc = associationRef.getTargetRef();
        NodeRef errandDoc = associationRef.getSourceRef();

        if (nodeService.getProperty(errandDoc, ErrandsService.PROP_BASE_DOC_NUMBER) == null) {
            String baseRegNum = documentService.getDocumentActualNumber(baseDoc);
            if (baseRegNum == null) {
                baseRegNum = documentService.getProjectRegNumber(baseDoc);
            }
            if (baseRegNum != null && !baseRegNum.isEmpty()) {
                nodeService.setProperty(errandDoc, ErrandsService.PROP_BASE_DOC_NUMBER, baseRegNum);
            }
        }
        //установка ассоциации основания
        List<AssociationRef> baseAssocRefs = null;
        baseAssocRefs = nodeService.getTargetAssocs(errandDoc, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
        if (baseAssocRefs == null || baseAssocRefs.size() == 0) {
            nodeService.createAssociation(errandDoc, baseDoc, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
        }
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }
}
