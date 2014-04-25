package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:59
 */
public class DocumentAttachmentsServiceImpl extends BaseBean implements DocumentAttachmentsService {
	private DictionaryService dictionaryService;
	private VersionService versionService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineBean;
	private BusinessJournalService businessJournalService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setStateMachineBean(StateMachineServiceBean stateMachineBean) {
		this.stateMachineBean = stateMachineBean;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	public NodeRef getRootFolder(final NodeRef documentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);

		final String attachmentsRootName = DOCUMENT_ATTACHMENTS_ROOT_NAME;

        NodeRef ref = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, attachmentsRootName);
        if (ref != null) {
            return ref;
        }
        //оставлено для подстраховки, создание папки происходит при создании документа
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef attachmentsRef = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, attachmentsRootName);
						if (attachmentsRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, attachmentsRootName);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, attachmentsRootName);
							ChildAssociationRef associationRef = nodeService.createNode(documentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
							attachmentsRef = associationRef.getChildRef();
							//не индексируем свойства папки
							disableNodeIndex(attachmentsRef);
						}
						return attachmentsRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	public List<NodeRef> getCategories(final NodeRef documentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);

		QName type = nodeService.getType(documentRef);

		List<String> categories = getCategories(type);

		List<NodeRef> result = new ArrayList<NodeRef>();
		NodeRef attachmentRootRef = getRootFolder(documentRef);
		for (String category : categories) {
			NodeRef categoryFolderRef = getCategoryFolder(category, attachmentRootRef);
			result.add(categoryFolderRef);
		}
		return result;
	}

	@Override
	public List<String> getCategories(QName documentTypeQName) {
		List<String> categories = new ArrayList<String>();

		ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(documentTypeQName.getNamespaceURI(), CONSTRAINT_ATTACHMENT_CATEGORIES));
		if (constraint != null && constraint.getConstraint() != null && (constraint.getConstraint() instanceof ListOfValuesConstraint)) {
			ListOfValuesConstraint psConstraint = (ListOfValuesConstraint) constraint.getConstraint();
			if (psConstraint.getAllowedValues() != null) {
				categories.addAll(psConstraint.getAllowedValues());
			}
		}

		if (categories.size() == 0) {
			categories.add("Основные");
		}
		return categories;
	}

	public NodeRef getCategoryFolder(final String category, final NodeRef attachmentRootRef) {
		NodeRef result = nodeService.getChildByName(attachmentRootRef, ContentModel.ASSOC_CONTAINS, category);
		if (result == null) {
			AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							NodeRef categoryRef = createNode(attachmentRootRef, TYPE_CATEGORY, category, null);
							disableNodeIndex(categoryRef);
							return categoryRef;
						}
					});
				}
			};
			result = AuthenticationUtil.runAsSystem(raw);
		}
		return result;
	}

	public NodeRef getCategory(final String category, final NodeRef documentRef) {
		NodeRef attachmentRootRef = getRootFolder(documentRef);
		NodeRef result = nodeService.getChildByName(attachmentRootRef, ContentModel.ASSOC_CONTAINS, category);
		if (isDocumentCategory(result)) {
			return result;
		}
		return null;
	}

	public NodeRef getDocumentByCategory(NodeRef categoryRef) {
		if (isDocumentCategory(categoryRef)) {
			NodeRef attachRootDir = nodeService.getPrimaryParent(categoryRef).getParentRef();
			if (attachRootDir != null && nodeService.getProperty(attachRootDir, ContentModel.PROP_NAME).equals(DOCUMENT_ATTACHMENTS_ROOT_NAME)) {
				NodeRef document = nodeService.getPrimaryParent(attachRootDir).getParentRef();
				if (document != null) {
					QName testType = nodeService.getType(document);
					Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
					if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
						return document;
					}
				}
			}
		}
		return null;
	}

	public String getCategoryName(NodeRef categoryRef) {
		if (isDocumentCategory(categoryRef)) {
			return (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
		}
		return null;
	}

	public NodeRef getDocumentByAttachment(NodeRef attachRef) {
		if (nodeService.exists(attachRef)) {
			NodeRef category = getCategoryByAttachment(attachRef);

			if (category != null) {
				return getDocumentByCategory(category);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public NodeRef getCategoryByAttachment(NodeRef attachRef) {
		if (nodeService.exists(attachRef)) {
			return findNodeByAssociationRef(attachRef, ASSOC_CATEGORY_ATTACHMENTS, TYPE_CATEGORY, ASSOCIATION_TYPE.SOURCE);
		} else {
			return null;
		}
	}

	public String getCategoryNameByAttachment(NodeRef attachRef) {
		NodeRef categoryRef = getCategoryByAttachment(attachRef);
		if (categoryRef != null) {
			return getCategoryName(categoryRef);
		}
		return null;
	}

	public boolean isDocumentAttachment(NodeRef nodeRef) {
		return getDocumentByAttachment(nodeRef) != null;
	}

	public boolean isDocumentCategory(NodeRef nodeRef) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_CATEGORY);
		return isProperType(nodeRef, types);
	}

	@Override
	public void deleteAttachment(NodeRef nodeRef) {
		nodeService.deleteNode(nodeRef);
	}

	@Override
	public Collection<Version> getAttachmentVersions(NodeRef nodeRef) {
		VersionHistory history = versionService.getVersionHistory(nodeRef);
		if (history != null) {
			return history.getAllVersions();
		}
		return null;
	}

	@Override
	public boolean isReadonlyCategory(NodeRef nodeRef) {
		NodeRef document = this.getDocumentByCategory(nodeRef);
		return document == null || this.stateMachineBean.isReadOnlyCategory(document, getCategoryName(nodeRef));
	}

	@Override
	public void copyAttachmentLog(NodeRef originalNodeRef, NodeRef copiedNodeRef) {
		NodeRef document = this.getDocumentByAttachment(originalNodeRef);
		if (document != null) {
			List<String> objects = new ArrayList<String>(2);
			objects.add(originalNodeRef.toString());
			objects.add(this.nodeService.getPrimaryParent(copiedNodeRef).getParentRef().toString());
			businessJournalService.log(document, EventCategory.COPY_DOCUMENT_ATTACHMENT, "#initiator скопировал(а) вложение #object1 в документе #mainobject в #object2", objects);
		}
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public List<NodeRef> getAttachmentsByCategory(NodeRef document, String categoryName) {
		NodeRef category = getCategory(categoryName, document);
		return getAttachmentsByCategory(category);
	}

	public List<NodeRef> getAttachmentsByCategory(NodeRef category) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		if (isDocumentCategory(category)) {
			List<AssociationRef> assocs = nodeService.getTargetAssocs(category, ASSOC_CATEGORY_ATTACHMENTS);
			for (AssociationRef assoc : assocs) {
				NodeRef attachment = assoc.getTargetRef();

				if (!isArchive(attachment)) {
					results.add(attachment);
				}
			}
		}
		return results;
	}

    /** Программное добавление вложения к документу без проверки прав
     *
     * @param attachmentRef - ссылка на вложение
     * @param attachmentCategoryRef - ссылка на категорию вложения
     */
    @Override
    public void addAttachment(NodeRef attachmentRef, NodeRef attachmentCategoryRef) {
        AlfrescoTransactionSupport.bindResource(NOT_SECURITY_MOVE_ATTACHMENT_POLICY, true);
        String assocName = nodeService.getProperty(attachmentRef, ContentModel.PROP_NAME).toString();
        QName commentAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, assocName);
        nodeService.moveNode(attachmentRef, attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentAssocQName);
    }
}
