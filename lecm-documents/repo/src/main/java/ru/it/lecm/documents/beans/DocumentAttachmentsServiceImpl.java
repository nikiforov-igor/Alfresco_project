package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
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
    private final static Logger logger = LoggerFactory.getLogger(DocumentAttachmentsServiceImpl.class);

    private DictionaryService dictionaryService;
	private VersionService versionService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineBean;
    private final Object lock = new Object();

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

	public NodeRef getRootFolder(final NodeRef documentRef) {
		this.lecmPermissionService.checkPermission("_lecmPerm_ContentList", documentRef);

        final String attachmentsRootName = DOCUMENT_ATTACHMENTS_ROOT_NAME;

        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        NodeRef attachmentsRef;
                        synchronized (lock) {
                            attachmentsRef = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, attachmentsRootName);
                            if (attachmentsRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, attachmentsRootName);
                                QName nodeTypeQName = ContentModel.TYPE_FOLDER;

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                properties.put(ContentModel.PROP_NAME, attachmentsRootName);
                                ChildAssociationRef associationRef = nodeService.createNode(documentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
                                attachmentsRef = associationRef.getChildRef();
                            }
                        }
                        return attachmentsRef;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

    public List<NodeRef> getCategories(final NodeRef documentRef) {
	    this.lecmPermissionService.checkPermission("_lecmPerm_ContentList", documentRef);

        QName type = nodeService.getType(documentRef);

        List<String> categories = getCategories(type);

        List<NodeRef> result = new ArrayList<NodeRef>();
        NodeRef attachmentRootRef = getRootFolder(documentRef);
        for (String category: categories) {
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
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        NodeRef categoryFolderRef;
                        synchronized (lock) {
                            categoryFolderRef = nodeService.getChildByName(attachmentRootRef, ContentModel.ASSOC_CONTAINS, category);
                            if (categoryFolderRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, category);
                                QName nodeTypeQName = ContentModel.TYPE_FOLDER;

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                properties.put(ContentModel.PROP_NAME, category);
                                ChildAssociationRef associationRef = nodeService.createNode(attachmentRootRef, assocTypeQName, assocQName, nodeTypeQName, properties);
                                categoryFolderRef = associationRef.getChildRef();
                            }
                        }
                        return categoryFolderRef;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

	public NodeRef getDocumentByAttachment(ChildAssociationRef attachRef) {
		NodeRef attachCategoryDir = attachRef.getParentRef();
		NodeRef attachRootDir = nodeService.getPrimaryParent(attachCategoryDir).getParentRef();
		if (attachRootDir != null && nodeService.getProperty(attachRootDir, ContentModel.PROP_NAME).equals(DOCUMENT_ATTACHMENTS_ROOT_NAME)) {
			NodeRef document = nodeService.getPrimaryParent(attachRootDir).getParentRef();
			if (document != null) {
				QName testType = nodeService.getType(document);
				Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
				if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
					this.lecmPermissionService.checkPermission("_lecmPerm_ContentList", document);

					return document;
				}
			}
		}
		return null;
	}

	public NodeRef getDocumentByCategory(NodeRef categoryRef) {
		NodeRef attachRootDir = nodeService.getPrimaryParent(categoryRef).getParentRef();
		if (attachRootDir != null && nodeService.getProperty(attachRootDir, ContentModel.PROP_NAME).equals(DOCUMENT_ATTACHMENTS_ROOT_NAME)) {
			NodeRef document = nodeService.getPrimaryParent(attachRootDir).getParentRef();
			if (document != null) {
				QName testType = nodeService.getType(document);
				Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
				if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
					this.lecmPermissionService.checkPermission("_lecmPerm_ContentList", document);

					return document;
				}
			}
		}
		return null;
	}

	public String getCategoryName(NodeRef categoryRef) {
		return (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
	}

	public NodeRef getDocumentByAttachment(NodeRef attachRef) {
		if (nodeService.exists(attachRef)) {
			return getDocumentByAttachment(nodeService.getPrimaryParent(attachRef));
		} else {
			return null;
		}
	}

	public NodeRef getCategoryByAttachment(NodeRef attachRef) {
		NodeRef categoryRef = nodeService.getPrimaryParent(attachRef).getParentRef();
		NodeRef attachRootDir = nodeService.getPrimaryParent(categoryRef).getParentRef();
		if (categoryRef != null && attachRootDir != null && nodeService.getProperty(attachRootDir, ContentModel.PROP_NAME).equals(DOCUMENT_ATTACHMENTS_ROOT_NAME)) {
			return categoryRef;
		}
		return null;
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

	@Override
	public void deleteAttachment(NodeRef nodeRef) {
		this.lecmPermissionService.checkPermission("_lecmPerm_ContentDelete", nodeRef);

		nodeService.deleteNode(nodeRef);
	}

	@Override
	public Collection<Version> getAttachmentVersions(NodeRef nodeRef) {
		VersionHistory history = versionService.getVersionHistory(nodeRef);
		if (history != null)
		{
			return history.getAllVersions();
		}
		return null;
	}

	@Override
	public boolean isReadonlyCategory(NodeRef nodeRef) {
		boolean result = true;
		NodeRef document = this.getDocumentByCategory(nodeRef);
		if (document != null) {
			return this.stateMachineBean.isReadOnlyCategory(document, getCategoryName(nodeRef));
		}
		return result;
	}
}
