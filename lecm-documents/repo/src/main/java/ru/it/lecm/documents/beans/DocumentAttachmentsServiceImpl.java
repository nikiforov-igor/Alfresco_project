package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.node.MLPropertyInterceptor;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.UnableToReleaseLockException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmMessageService;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.*;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:59
 */
public class DocumentAttachmentsServiceImpl extends BaseBean implements DocumentAttachmentsService {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(DocumentAttachmentsServiceImpl.class);

	private DictionaryService dictionaryService;
	private VersionService versionService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineService;
	private BusinessJournalService businessJournalService;
	private NamespaceService namespaceService;
	private MessageService messageService;
	private LecmMessageService lecmMessageService;
    private LockService lockService;
	private DictionaryBean dictionaryBean;

	public void setDictionaryBean(DictionaryBean dictionaryBean) {
		this.dictionaryBean = dictionaryBean;
	}

	private List<AttachmentUnlockListener> unlockListeners = new ArrayList<>();

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setLecmMessageService(LecmMessageService lecmMessageService) {
		this.lecmMessageService = lecmMessageService;
	}

    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

	@Override
	public NodeRef getRootFolder(final NodeRef documentRef) {
		//TODO DONE Рефакторинг AL-2733
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);
		return getFolder(documentRef, DOCUMENT_ATTACHMENTS_ROOT_NAME);
	}

	@Override
	public NodeRef createRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException {
		//TODO DONE Рефакторинг AL-2733
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);

		NodeRef attachmentsRef = createFolder(documentRef, DOCUMENT_ATTACHMENTS_ROOT_NAME);
		disableNodeIndex(attachmentsRef);
		return attachmentsRef;
	}

	@Override
	public List<NodeRef> getCategories(final NodeRef documentRef) throws WriteTransactionNeededException {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);
		final QName type = nodeService.getType(documentRef);
		List<String> categories = getCategories(type);
		final List<NodeRef> result = new ArrayList<>();
		final NodeRef attachmentRootRef = getRootFolder(documentRef);
		//TODO Рефакторинг AL-2733
		//TODO Вроде как категории _должны_ создаваться машиной состояний, но сейчас всю работу по созданию выполняет этот метод, так что оптимизируем транзакцию,
		// так чтоб открывалась один раз
		final List<String> toCreate;
		if (null == attachmentRootRef) {
			toCreate = categories;
		} else {
			toCreate = new ArrayList<>();
			for (String category : categories) {
				String[] names = StringUtils.split(category, '|');
				NodeRef categoryFolderRef = getFolder(attachmentRootRef, names[0]);
				if (null == categoryFolderRef) {
					toCreate.add(category);
				} else {
					result.add(categoryFolderRef);
				}
			}
		}
		//Создаём всё, чего не хватает
		lecmTransactionHelper.doInRWTransaction( new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				NodeRef rootRef = (null == attachmentRootRef ? createRootFolder(documentRef) : attachmentRootRef);
				for (String folder : toCreate) {
					result.add(createCategoryFolder(folder, rootRef, type));
				}
				return null;
			}
		});
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

		if (categories.isEmpty()) {
			categories.add("Основные");
		}
		return categories;
	}

	//TODO: надо разделить на получение и создание, чтобы вынести транзакции из цикла getCategories... а создание категорий вызывать отдельно.
	//TODO Refactoring in progress
	//Разделить получение и создание.
	//В итоге, в получении вообще делать нечего оказалось.
//	public NodeRef getCategoryFolder(final String category, final NodeRef attachmentRootRef) {
//		NodeRef result = nodeService.getChildByName(attachmentRootRef, ContentModel.ASSOC_CONTAINS, category);
//		return result;
//	}

	private NodeRef createCategoryFolder(final String category, final NodeRef attachmentRootRef, final QName type) throws WriteTransactionNeededException {
		String[] names = StringUtils.split(category, '|');
		NodeRef categoryRef = createNode(attachmentRootRef, TYPE_CATEGORY, names[0], null);
		disableNodeIndex(categoryRef);
		if (lecmMessageService.isMlSupported() && names.length == 2) {//если у категории через разделитель задан ключ локализации
			String typename = type.toPrefixString(namespaceService).replace(':', '_');
			String categoryKey = names[1];
			String messageKey = String.format("%s.attachmentCategory.%s.title", typename, categoryKey);
			List<Locale> locales = lecmMessageService.getMlLocales();
			List<Locale> fallback = lecmMessageService.getFallbackLocales();
			MLPropertyInterceptor.setMLAware(true);
			MLText mlText = new MLText();
			for (Locale locale : fallback) {
				mlText.addValue(locale, names[0]);
			}
			for (Locale locale : locales) {
				String categoryTitle = StringEscapeUtils.unescapeJava(messageService.getMessage(messageKey, locale));
				if (categoryTitle != null) {
					mlText.addValue(locale, categoryTitle);
				}
			}
			PropertyMap props = new PropertyMap();
			props.put(ContentModel.PROP_TITLE, mlText);
			nodeService.addAspect(categoryRef, ContentModel.ASPECT_TITLED, props);
			MLPropertyInterceptor.setMLAware(false);
		}
		return categoryRef;
	}

	@Override
	public NodeRef getCategory(final String category, final NodeRef documentRef) {
		NodeRef attachmentRootRef = getRootFolder(documentRef);

		//TODO Рефакторинг AL-2733
		//TODO Вроде как категории создаются машиной состояний, и, вряд ли здесь будет существовать транзакция на запись при получении категорий.
		//Так что создание закомментировал. Заменил на возврат null. Требуется тестирование.
		if (null == attachmentRootRef){
			return null;
		}

		NodeRef result = nodeService.getChildByName(attachmentRootRef, ContentModel.ASSOC_CONTAINS, category);
		if (isDocumentCategory(result)) {
			return result;
		}
		return null;
	}

	@Override
	public String getCategoryNameFromDocTypeSettings(final QName documentTypeQName) {
		NodeRef documentTypeDictionary = dictionaryBean.getDictionaryByName(DocumentService.DOCUMENT_TYPE_SETTINGS_DICTIONARY_NAME);
		if (documentTypeDictionary != null) {
			List<NodeRef> settings = dictionaryBean.getChildren(documentTypeDictionary);
			if (settings != null && settings.size() > 0) {
				for (NodeRef item : settings) {
					String itemTypeString = (String) nodeService.getProperty(item, DocumentService.PROP_TYPE_SETTINGS_TYPE);
					QName itemType = QName.createQName(itemTypeString, serviceRegistry.getNamespaceService());
					if (Objects.equals(itemType, documentTypeQName)) {
						String itemCategoryName = (String) nodeService.getProperty(item, DocumentService.PROP_TYPE_SETTINGS_ATTACHMENT_BY_BARCODE_CATEGORY);
						if (itemCategoryName != null && !"".equals(itemCategoryName)) {
							return itemCategoryName;
						}
					}
				}
			}
		}
        String categoryName = null;
        List<String> categories = getCategories(documentTypeQName);
        if (categories != null && categories.size() > 0) {
            String categoryRawName = categories.get(0);
            if (categoryRawName.contains("|")) {
                categoryName = categoryRawName.substring(0, categoryRawName.indexOf('|'));
            } else {
                categoryName = categoryRawName;
            }
        }
		return categoryName;
	}

	@Override
	public String getCategoryNameFromDocTypeSettings(NodeRef documentRef) {
		return getCategoryNameFromDocTypeSettings(nodeService.getType(documentRef));
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
		return document == null || this.stateMachineService.isReadOnlyCategory(document, getCategoryName(nodeRef));
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
				if (lecmPermissionService.hasReadAccess(attachment)) {
					if (!isArchive(attachment)) {
						results.add(attachment);
					}
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

    @Override
    public void unlockAttachmentsAndClearLinks(final NodeRef documentRef) {
        AuthenticationUtil.RunAsWork<?> runAsWork = new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                final List<NodeRef> categories = getCategories(documentRef);
                for (NodeRef category : categories) {
                    final List<NodeRef> attachmentsByCategory = getAttachmentsByCategory(category);
                    for (NodeRef attachmentRef : attachmentsByCategory) {
                        try {
                            lockService.unlock(attachmentRef);
                            notifyUnlockListeners(attachmentRef);
                        } catch (UnableToReleaseLockException e) {
                            LOGGER.warn("Can not unlock {}. Skipped.", attachmentRef);
                        }
                    }
                }
                return null;
            }
        };

        AuthenticationUtil.runAsSystem(runAsWork);
    }

    @Override
    public void addAttachmentUnlockListener(AttachmentUnlockListener unlockListener) {
        unlockListeners.add(unlockListener);
    }

    private void notifyUnlockListeners(final NodeRef attachmentRef) {
        for (AttachmentUnlockListener listener : unlockListeners) {
            listener.onAttachmentUnlocked(attachmentRef);
        }
    }
}
