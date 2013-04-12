package ru.it.lecm.documents.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.coci.CheckOutCheckInServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 * User: dbashmakov
 * Date: 15.03.13
 * Time: 15:38
 */
public class DocumentAttachmentsPolicy extends BaseBean {

    final protected Logger logger = LoggerFactory.getLogger(DocumentAttachmentsPolicy.class);

    private PolicyComponent policyComponent;
    private DocumentMembersService documentMembersService;
    private DocumentAttachmentsService documentAttachmentsService;
    private BusinessJournalService businessJournalService;
    private OrgstructureBean orgstructureService;
    private SubstitudeBean substituteService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineBean;

	private boolean isCreatingWorkingCopy = false;

    final private QName[] AFFECTED_PROPERTIES = {ContentModel.PROP_CREATOR, ContentModel.PROP_MODIFIER};

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
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

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setStateMachineBean(StateMachineServiceBean stateMachineBean) {
		this.stateMachineBean = stateMachineBean;
	}

	public final void init() {
		policyComponent.bindClassBehaviour(CheckOutCheckInServicePolicies.BeforeCheckOut.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeCheckOut"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeCreateNodePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeCreateNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeUpdateNodePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeUpdateNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeDeleteNode"));
		policyComponent.bindClassBehaviour(VersionServicePolicies.BeforeCreateVersionPolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeCreateVersion"));
        policyComponent.bindClassBehaviour(VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
			    ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "afterCreateVersion", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

	public void beforeCreateNode(NodeRef parentRef, QName assocTypeQName, QName assocQName, QName nodeTypeQName) {
		final NodeRef document = this.documentAttachmentsService.getDocumentByCategory(parentRef);
		if (document != null) {
			if (!this.isCreatingWorkingCopy) {
				this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_ADD, document);
			}
			try {
				this.stateMachineBean.checkReadOnlyCategory(document, this.documentAttachmentsService.getCategoryName(parentRef));
			} catch (AlfrescoRuntimeException ex) {
				this.isCreatingWorkingCopy = false;
				throw ex;
			}
		}
	}

    public void onCreateNode(ChildAssociationRef childAssocRef) {
        final NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(childAssocRef);
        if (document != null) {
	        if (!this.isCreatingWorkingCopy) {
		        // добавляем пользователя добавившего вложение как участника
		        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
			        @Override
			        public NodeRef doWork() throws Exception {
				        RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					        @Override
					        public NodeRef execute() throws Throwable {
						        return documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), null);
					        }
				        });
			        }
		        });

		        List<String> objects = new ArrayList<String>(1);
		        objects.add(childAssocRef.getChildRef().toString());
		        businessJournalService.log(document, EventCategory.ADD_DOCUMENT_ATTACHMENT, "Сотрудник #initiator добавил вложение #object1 к документу #mainobject", objects);
	        } else {
	            this.isCreatingWorkingCopy = false;
	        }
        }
    }

    public void beforeDeleteNode(NodeRef nodeRef) {
        final NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
        if (document != null && !nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
	        boolean hasDeletePermission =  this.lecmPermissionService.hasPermission(LecmPermissionService.PERM_CONTENT_DELETE, document);
	        if (!hasDeletePermission) {
		        hasDeletePermission = isOwnNode(nodeRef) && this.lecmPermissionService.hasPermission(LecmPermissionService.PERM_OWN_CONTENT_DELETE, document);
	        }

	        if (hasDeletePermission) {
		        this.stateMachineBean.checkReadOnlyCategory(document, this.documentAttachmentsService.getCategoryNameByAttachment(nodeRef));
	        } else {
		        throw new AlfrescoRuntimeException("Does not have permission 'delete' for node " + nodeRef);
	        }

	        // добавляем пользователя удалившего вложения как участника
	        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
		        @Override
		        public NodeRef doWork() throws Exception {
			        RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
			        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
				        @Override
				        public NodeRef execute() throws Throwable {
					        return documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), null);
				        }
			        });
		        }
	        });

			List<String> objects = new ArrayList<String>(1);
			objects.add(nodeRef.toString());
			businessJournalService.log(document, EventCategory.DELETE_DOCUMENT_ATTACHMENT, "Сотрудник #initiator удалил вложение #object1 в документе #mainobject", objects);
        }
    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
	    NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
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

	public void beforeCreateVersion(NodeRef versionableNode) {
		NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(versionableNode);
		if (document != null) {
			this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_ADD_VER, document);
			this.stateMachineBean.checkReadOnlyCategory(document, this.documentAttachmentsService.getCategoryNameByAttachment(versionableNode));
		}
	}

	public void afterCreateVersion(NodeRef nodeRef, Version version) {
		NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
		if (document != null && !nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
			List<String> objects = new ArrayList<String>(1);
			objects.add(nodeRef.toString());
			businessJournalService.log(document, EventCategory.ADD_DOCUMENT_ATTACHMENT_NEW_VERSION, "Сотрудник #initiator обновил версию вложения #object1 в документе #mainobject", objects);
		}
	}

	public void beforeUpdateNode(NodeRef nodeRef) {
		NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
		if (document != null) {
			this.stateMachineBean.checkReadOnlyCategory(document, this.documentAttachmentsService.getCategoryNameByAttachment(nodeRef));
		}
	}

	public void beforeCheckOut(NodeRef nodeRef, NodeRef nodeRef2, QName qName, QName qName2) {
		final NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
		if (document != null) {
			this.isCreatingWorkingCopy = true;
		}
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}