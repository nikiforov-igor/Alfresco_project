package ru.it.lecm.documents.policy;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.coci.CheckOutCheckInServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.cmr.repository.AssociationRef;
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
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 15.03.13
 * Time: 15:38
 */
public class DocumentAttachmentsPolicy extends BaseBean {
	private static final String ATTACHMENT_CREATING_WORKING_COPY = "attachment_creating_working_copy";
	final protected Logger logger = LoggerFactory.getLogger(DocumentAttachmentsPolicy.class);

	private PolicyComponent policyComponent;
	private DocumentMembersService documentMembersService;
	private DocumentAttachmentsService documentAttachmentsService;
	private BusinessJournalService businessJournalService;
	private OrgstructureBean orgstructureService;
	private SubstitudeBean substituteService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineService;

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

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public void setSubstituteService(SubstitudeBean substitudeService) {
		this.substituteService = substitudeService;
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public final void init() {
		policyComponent.bindClassBehaviour(CheckOutCheckInServicePolicies.BeforeCheckOut.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeCheckOut"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnMoveNodePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onMoveNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeUpdateNodePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeUpdateNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(VersionServicePolicies.BeforeCreateVersionPolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeCreateVersion"));
		policyComponent.bindClassBehaviour(VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "afterCreateVersion", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeDeleteNode"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentAttachmentsService.TYPE_CATEGORY, DocumentAttachmentsService.ASSOC_CATEGORY_ATTACHMENTS,
				new JavaBehaviour(this, "onAddAttachment", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentAttachmentsService.TYPE_CATEGORY, DocumentAttachmentsService.ASSOC_CATEGORY_ATTACHMENTS,
				new JavaBehaviour(this, "onDeleteAttachmentAssoc", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onCreateNode(ChildAssociationRef childAssocRef) {
		final NodeRef attachmentRef = childAssocRef.getChildRef();
		NodeRef category = childAssocRef.getParentRef();

		final NodeRef document = this.documentAttachmentsService.getDocumentByCategory(category);
		if (document != null && nodeService.exists(attachmentRef)) {
			if (!nodeService.hasAspect(attachmentRef, ContentModel.ASPECT_WORKING_COPY) && AlfrescoTransactionSupport.getResource(ATTACHMENT_CREATING_WORKING_COPY) == null) {
				boolean assocExist = false;
				List<AssociationRef> existAssocs = nodeService.getTargetAssocs(category, DocumentAttachmentsService.ASSOC_CATEGORY_ATTACHMENTS);
				if (existAssocs != null) {
					for (AssociationRef assoc : existAssocs) {
						if (attachmentRef.equals(assoc.getTargetRef())) {
							assocExist = true;
						}
					}
				}
				if (!assocExist) {
					nodeService.createAssociation(category, attachmentRef, DocumentAttachmentsService.ASSOC_CATEGORY_ATTACHMENTS);
				}
                //Добавляем аспект с организацией для вложения
                List<AssociationRef> unitRefs = nodeService.getTargetAssocs(document, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                if (!unitRefs.isEmpty()) {
                    if (!nodeService.hasAspect(attachmentRef, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                        nodeService.addAspect(attachmentRef, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION, null);
                    }
                    List<AssociationRef> units =  nodeService.getTargetAssocs(attachmentRef, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                    for (AssociationRef unit : units) {
                        nodeService.removeAssociation(attachmentRef, unit.getTargetRef(), OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                    }
                    nodeService.createAssociation(attachmentRef, unitRefs.get(0).getTargetRef(), OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                }

                //Меняем владельца документа на системного пользователя
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                    @Override
                    public Object doWork() throws Exception {
                        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>(1, 1.0f);
                        properties.put(ContentModel.PROP_OWNER, AuthenticationUtil.SYSTEM_USER_NAME);
                        nodeService.addAspect(attachmentRef, ContentModel.ASPECT_OWNABLE, properties);
                        return null;
                    }
                });
			}
		}
	}

	public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
		onCreateNode(newChildAssocRef);
	}

	/**
	 * добавляет аспект для ссылки на документ из формы document-details
	 *
	 * @param documentRef
	 * @param attachmentRef
	 */
	public void addParentDocumentAspect(NodeRef documentRef, NodeRef attachmentRef) {
		if (!nodeService.hasAspect(attachmentRef, DocumentService.ASPECT_PARENT_DOCUMENT)) {
			nodeService.addAspect(attachmentRef, DocumentService.ASPECT_PARENT_DOCUMENT, null);
			nodeService.createAssociation(attachmentRef, documentRef, DocumentService.ASSOC_PARENT_DOCUMENT);
		}
	}

	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		// в процессе удаления ноды также сюда попадаем
		if (nodeService.exists(nodeRef)) {
			List<QName> changedProps = getAffectedProperties(before, after);
			Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
			boolean hasChanges = false;
			for (QName changedProp : changedProps) {
				QName propName = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, changedProp.getLocalName());
				QName propRef = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, changedProp.getLocalName() + "-ref");
				NodeRef employeeRef = orgstructureService.getCurrentEmployee();
				if (null != employeeRef) {
					properties.put(propName, substituteService.getObjectDescription(employeeRef));
					properties.put(propRef, employeeRef.toString());
					hasChanges = true;
				}
			}
			//TODO DONE замена нескольких setProperty на setProperties.
			if (hasChanges) {
				nodeService.setProperties(nodeRef, properties);
			}
		}

		NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
		if (document != null && !nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
			Serializable commentCountBefore = before.get(ForumModel.PROP_COMMENT_COUNT);
			Serializable commentCountAfter = after.get(ForumModel.PROP_COMMENT_COUNT);
			if (commentCountAfter != null && (commentCountBefore == null || !commentCountAfter.equals(commentCountBefore))) {
				List<String> objects = new ArrayList<String>(1);
				objects.add(nodeRef.toString());
				businessJournalService.log(document, EventCategory.COMMENT_DOCUMENT_ATTACHMENT, "#initiator прокомментировал(а) вложение #object1 в документе #mainobject", objects);
			} else if (before.size() == after.size() && hasChange(before, after)) {
				List<String> objects = new ArrayList<String>(1);
				objects.add(nodeRef.toString());
				businessJournalService.log(document, EventCategory.EDIT_DOCUMENT_ATTACHMENT_PROPERTIES, "#initiator изменил(а) свойства вложения #object1 в документе #mainobject", objects);
			}
		}
	}

	private List<QName> getAffectedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after) {
		List<QName> result = new ArrayList<QName>();
		for (QName affected : AFFECTED_PROPERTIES) {
			QName propName = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, affected.getLocalName());
			Object propValue = before.get(propName);
			Object prev = before.get(affected);
			Object cur = after.get(affected);
			if (propValue == null || (cur != null && !cur.equals(prev))) {
				result.add(affected);
			}
		}
		return result;
	}

	private boolean hasChange(Map<QName, Serializable> before, Map<QName, Serializable> after) {
		if (before.size() != after.size()) {
			return true;
		} else {
			for (QName affected : before.keySet()) {
				Object prev = before.get(affected);
				Object cur = after.get(affected);
				if (cur != null && !cur.equals(prev)) {
					return true;
				}
			}
		}
		return false;
	}

	public void beforeCreateVersion(NodeRef versionableNode) {
		NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(versionableNode);
		if (document != null) {
			Boolean notCheckPermissions = AlfrescoTransactionSupport.getResource(DocumentAttachmentsService.NOT_SECURITY_CREATE_VERSION_ATTACHMENT_POLICY);
			if (notCheckPermissions == null || !notCheckPermissions) {
				this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_ADD_VER, document);
				this.stateMachineService.checkReadOnlyCategory(document, this.documentAttachmentsService.getCategoryNameByAttachment(versionableNode));
			}
		}
	}

	public void afterCreateVersion(NodeRef nodeRef, Version version) {
		NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
		Serializable vr = nodeService.getProperty(nodeRef, ContentModel.PROP_VERSION_LABEL);
                String versionLabel = (vr != null) ? vr.toString() : "";
                if (document != null && !nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) && !versionLabel.equals("") && !versionLabel.equals("1.0")) {
			List<String> objects = new ArrayList<String>(1);
			objects.add(nodeRef.toString());
			businessJournalService.log(document, EventCategory.ADD_DOCUMENT_ATTACHMENT_NEW_VERSION, "#initiator обновил(а) версию вложения #object1 в документе #mainobject", objects);
		}
	}

	public void beforeUpdateNode(NodeRef nodeRef) {
		NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
		if (document != null) {
//			this.stateMachineBean.checkReadOnlyCategory(document, this.documentAttachmentsService.getCategoryNameByAttachment(nodeRef));
		}
	}

	public void beforeCheckOut(NodeRef nodeRef, NodeRef nodeRef2, QName qName, QName qName2) {
		final NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);
		if (document != null) {
			AlfrescoTransactionSupport.bindResource(ATTACHMENT_CREATING_WORKING_COPY, true);
		}
	}

        public void onAddAttachment(AssociationRef associationRef) {
             final NodeRef attachment = associationRef.getTargetRef();
             final NodeRef category = associationRef.getSourceRef();

             final NodeRef document = documentAttachmentsService.getDocumentByCategory(category);
             if (document != null) {
                 Boolean notCheckPermissions = AlfrescoTransactionSupport.getResource(DocumentAttachmentsService.NOT_SECURITY_MOVE_ATTACHMENT_POLICY);
                 if (notCheckPermissions == null || !notCheckPermissions) {
                     this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_ADD, document);
                 }
     //			this.stateMachineBean.checkReadOnlyCategory(document, this.documentAttachmentsService.getCategoryName(category));

                 AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
                     @Override
                     public NodeRef doWork() throws Exception {
//                        TODO: Веротянее всего, что уже выполняется в транзакции
//                        RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//                         добавим аспект lecm-attachment к файлу вложения
                         nodeService.addAspect(attachment, DocumentService.ASPECT_LECM_ATTACHMENT, null);
                         // добавляем пользователя добавившего вложение как участника
     //                    transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
     //						@Override
     //						public NodeRef execute() throws Throwable {
                         documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), new HashMap<QName, Serializable>());
     //				}
     //			});
                         addParentDocumentAspect(document, attachment);
                         return null;
                     }
                 });

                 List<String> objects = new ArrayList<String>(1);
                 objects.add(attachment.toString());
                 businessJournalService.log(document, EventCategory.ADD_DOCUMENT_ATTACHMENT, "#initiator добавил(а) вложение #object1 к документу #mainobject", objects);

             }
        }

	public void beforeDeleteNode(NodeRef nodeRef) {
		final NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(nodeRef);

		if (document != null && !nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
			onDeleteAttachment(nodeRef, document, this.documentAttachmentsService.getCategoryNameByAttachment(nodeRef));
		}
	}

	public void onDeleteAttachmentAssoc(AssociationRef nodeAssocRef) {
		NodeRef attachment = nodeAssocRef.getTargetRef();
		NodeRef category = nodeAssocRef.getSourceRef();

		if (nodeService.exists(category)) {
			final NodeRef document = documentAttachmentsService.getDocumentByCategory(category);
			if (document != null) {
				onDeleteAttachment(attachment, document, this.documentAttachmentsService.getCategoryName(category));
			}
		}
	}

	public void onDeleteAttachment(NodeRef attachment, final NodeRef document, String categoryName) {
		boolean hasDeletePermission =  this.lecmPermissionService.hasPermission(LecmPermissionService.PERM_CONTENT_DELETE, document);
		if (!hasDeletePermission) {
			hasDeletePermission = isOwnNode(attachment) && this.lecmPermissionService.hasPermission(LecmPermissionService.PERM_OWN_CONTENT_DELETE, document);
		}


		if (hasDeletePermission) {
			this.stateMachineService.checkReadOnlyCategory(document, categoryName);
		} else if (nodeService.exists(attachment)){
			throw new AlfrescoRuntimeException("Does not have permission 'delete' for node " + attachment);
		}

		// добавляем пользователя удалившего вложения как участника
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
//				TODO: Вероятнее всего, уже выполняется в транзакции
//				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//				return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//					@Override
//					public NodeRef execute() throws Throwable {
						return documentMembersService.addMember(document, orgstructureService.getCurrentEmployee(), new HashMap<QName, Serializable>());
//					}
//				});
			}
		});

		List<String> objects = new ArrayList<String>(1);
		objects.add(attachment.toString());
                if (nodeService.exists(attachment))
                    businessJournalService.log(document, EventCategory.DELETE_DOCUMENT_ATTACHMENT, "#initiator удалил(а) вложение #object1 в документе #mainobject", objects);
	}
}
