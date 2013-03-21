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
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 15.03.13
 * Time: 15:38
 */
public class DocumentAttachmentsPolicy implements NodeServicePolicies.OnDeleteNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy,
        NodeServicePolicies.OnCreateNodePolicy {

    final protected Logger logger = LoggerFactory.getLogger(DocumentAttachmentsPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentMembersService documentMembersService;
    private BusinessJournalService businessJournalService;
    private OrgstructureBean orgstructureService;
    private DictionaryService dictionaryService;
    private SubstitudeBean substituteService;

    final private QName[] AFFECTED_PROPERTIES = {ContentModel.PROP_CREATOR, ContentModel.PROP_MODIFIER};

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
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onDeleteNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef document = getDocumentByAttachment(childAssocRef);
        if (document != null) {
            // добавляем пользователя добавившего вложение как участника
            documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), null);
        }
    }

    @Override
    public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
        NodeRef document = getDocumentByAttachment(childAssocRef);
        if (document != null) {
            // добавляем пользователя удалившего вложения как участника
            documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), null);
        }
    }

    private NodeRef getDocumentByAttachment(ChildAssociationRef attachRef) {
        NodeRef attachCategoryDir = attachRef.getParentRef();
        NodeRef attachRootDir = nodeService.getPrimaryParent(attachCategoryDir).getParentRef();
        if (attachRootDir != null) {
            NodeRef document = nodeService.getPrimaryParent(attachRootDir).getParentRef();
            if (document != null) {
                QName testType = nodeService.getType(document);
                Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
                if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
                    return document;
                }
            }
        }
        return null;
    }

    private NodeRef getDocumentByAttachment(NodeRef attachRef) {
        if (nodeService.exists(attachRef)) {
            return getDocumentByAttachment(nodeService.getPrimaryParent(attachRef));
        } else {
            return null;
        }
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        List<QName> changedProps = getAffectedProperties(before, after);
        if (!changedProps.isEmpty() && getDocumentByAttachment(nodeRef) != null) { // обновляем только автора и изменившего ноду
            for (QName changedProp : changedProps) {
                QName propName = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, changedProp.getLocalName());
                QName propRef = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, changedProp.getLocalName() + "-ref");
                NodeRef employeeRef = orgstructureService.getCurrentEmployee();
                nodeService.setProperty(nodeRef, propName, substituteService.getObjectDescription(employeeRef));
                nodeService.setProperty(nodeRef, propRef, employeeRef.toString());
            }
        }
    }

    private List<QName> getAffectedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after) {
        List<QName> result = new ArrayList<QName>();
        for (QName affected : AFFECTED_PROPERTIES) {
            Object prev = before.get(affected);
            Object cur = after.get(affected);
            if (cur != null && !cur.equals(prev)) {
                result.add(affected);
            }
        }
        return result;
    }

    public void setSubstituteService(SubstitudeBean substitudeService) {
        this.substituteService = substitudeService;
    }
}
