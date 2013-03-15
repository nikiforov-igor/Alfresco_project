package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.Collection;

/**
 * User: dbashmakov
 * Date: 15.03.13
 * Time: 15:38
 */
public class DocumentAttachmentsPolicy implements NodeServicePolicies.OnDeleteNodePolicy,
        NodeServicePolicies.OnCreateNodePolicy {

    final protected Logger logger = LoggerFactory.getLogger(DocumentAttachmentsPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentMembersService documentMembersService;
    private BusinessJournalService businessJournalService;
    private OrgstructureBean orgstructureService;
    private DictionaryService dictionaryService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onDeleteNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        testAndAddMember(childAssocRef);
    }

    @Override
    public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
        testAndAddMember(childAssocRef);
    }

    private void testAndAddMember(ChildAssociationRef childAssocRef) {
        NodeRef attachCategoryDir = childAssocRef.getParentRef();
        NodeRef attachRootDir = nodeService.getPrimaryParent(attachCategoryDir).getParentRef();
        if (attachRootDir != null) {
            NodeRef document = nodeService.getPrimaryParent(attachRootDir).getParentRef();
            if (document != null) {
                QName testType = nodeService.getType(document);
                Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
                if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
                    documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), null);
                }
            }
        }
    }
}
