package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
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
public class DocumentAttachmentsPolicy implements
		NodeServicePolicies.BeforeDeleteNodePolicy,
		NodeServicePolicies.OnUpdatePropertiesPolicy,
		VersionServicePolicies.AfterCreateVersionPolicy,
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
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeDeleteNode"));
        policyComponent.bindClassBehaviour(VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
			    ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "afterCreateVersion", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef document = getDocumentByAttachment(childAssocRef);
        if (document != null) {
            // добавляем пользователя добавившего вложение как участника
            documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), null);

	        if (!childAssocRef.getQName().getLocalName().contains("(Рабочая копия)")) {

		        List<String> objects = new ArrayList<String>(1);
		        objects.add(childAssocRef.getChildRef().toString());
		        businessJournalService.log(document, EventCategory.ADD_DOCUMENT_ATTACHMENT, "Сотрудник #initiator добавил вложение #object1 к документу #mainobject", objects);
	        }
        }
    }

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        NodeRef document = getDocumentByAttachment(nodeRef);
        if (document != null) {
            // добавляем пользователя удалившего вложения как участника
            documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), null);

	        if (!nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
				List<String> objects = new ArrayList<String>(1);
				objects.add(nodeRef.toString());
				businessJournalService.log(document, EventCategory.DELETE_DOCUMENT_ATTACHMENT, "Сотрудник #initiator удалил вложение #object1 в документе #mainobject", objects);
	        }
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
	    NodeRef document = getDocumentByAttachment(nodeRef);
	    List<QName> changedProps = getAffectedProperties(before, after);
        if (!changedProps.isEmpty() && document != null) { // обновляем только автора и изменившего ноду
            for (QName changedProp : changedProps) {
                QName propName = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, changedProp.getLocalName());
                QName propRef = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, changedProp.getLocalName() + "-ref");
                NodeRef employeeRef = orgstructureService.getCurrentEmployee();
                nodeService.setProperty(nodeRef, propName, substituteService.getObjectDescription(employeeRef));
                nodeService.setProperty(nodeRef, propRef, employeeRef.toString());
            }
        }

	    if (document != null && !nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
		    Serializable commentCountBefore = before.get(ForumModel.PROP_COMMENT_COUNT);
		    Serializable commentCountAfter = after.get(ForumModel.PROP_COMMENT_COUNT);
		    if (commentCountAfter != null && (commentCountBefore == null || !commentCountAfter.equals(commentCountBefore))) {
			    List<String> objects = new ArrayList<String>(1);
			    objects.add(nodeRef.toString());
			    businessJournalService.log(document, EventCategory.COMMENT_DOCUMENT_ATTACHMENT, "Сотрудник #initiator прокомментировал вложение #object1 в документе #mainobject", objects);
		    } else if (before.size() == after.size()) {
			    List<String> objects = new ArrayList<String>(1);
			    objects.add(nodeRef.toString());
		        businessJournalService.log(document, EventCategory.EDIT_DOCUMENT_ATTACHMENT_PROPERTIES, "Сотрудник #initiator изменил свойства вложения #object1 в документе #mainobject", objects);
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

	@Override
	public void afterCreateVersion(NodeRef nodeRef, Version version) {
		NodeRef document = getDocumentByAttachment(nodeRef);
		if (document != null && !nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
			List<String> objects = new ArrayList<String>(1);
			objects.add(nodeRef.toString());
			businessJournalService.log(document, EventCategory.ADD_DOCUMENT_ATTACHMENT_NEW_VERSION, "Сотрудник #initiator обновил версию вложения #object1 в документе #mainobject", objects);
		}
	}
}