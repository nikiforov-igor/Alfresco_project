package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;

import java.util.Collection;
import java.util.List;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 12:08
 */
@SuppressWarnings("unused")
public class DocumentAttachmentsWebScriptBean extends BaseWebScript {

    private DocumentAttachmentsService documentAttachmentsService;

    protected NodeService nodeService;

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

	/**
	 * Получение папки для вложений в документе
	 * @param documentNodeRef nodeRef документа
	 * @return папка с вложениями
	 */
    public ScriptNode getRootFolder(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);

        if (this.nodeService.exists(documentRef)) {
            NodeRef attachmentsRoot = this.documentAttachmentsService.getRootFolder(documentRef);
            if (attachmentsRoot != null) {
                return new ScriptNode(attachmentsRoot, this.serviceRegistry, getScope());
            }
        }
        return null;
    }

	/**
	 * Получение документа по вложению
	 * @param nodeRef nodeRef вложения
	 * @return документ
	 */
	public ScriptNode getDocumentByAttachment(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		NodeRef ref = new NodeRef(nodeRef);

		if (this.nodeService.exists(ref)) {
			NodeRef document = this.documentAttachmentsService.getDocumentByAttachment(ref);
			if (document != null) {
				return new ScriptNode(document, this.serviceRegistry, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение категории по вложению
	 * @param nodeRef nodeRef вложения
	 * @return категория вложений
	 */
	public ScriptNode getCategoryByAttachment(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		NodeRef ref = new NodeRef(nodeRef);

		if (this.nodeService.exists(ref)) {
			NodeRef category = this.documentAttachmentsService.getCategoryByAttachment(ref);
			if (category != null) {
				return new ScriptNode(category, this.serviceRegistry, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение категорий вложений для документа
	 * @param documentNodeRef nodeRef документа
	 * @return массив категорий вложений
	 */
    public Scriptable getCategories(String documentNodeRef) throws WriteTransactionNeededException {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            List<NodeRef> categories = this.documentAttachmentsService.getCategories(documentRef);
            return createScriptable(categories);
        }
        return null;
    }

	/**
	 * Получение категорий вложений по имени
	 * @param categoryName название категории
	 * @param document nodeRef документа
	 * @return массив категорий вложений
	 */
    public ScriptNode getCategoryByName(String categoryName, ScriptNode document) {
        ParameterCheck.mandatory("categoryName", categoryName);
        ParameterCheck.mandatory("document", document);

	    NodeRef category = this.documentAttachmentsService.getCategory(categoryName, document.getNodeRef());
	    if (category != null) {
		    return new ScriptNode(category, this.serviceRegistry, getScope());
	    }
        return null;
    }

	/**
	 * Получение категорий вложений по типу документа
	 * @param documentType тип документа
	 * @return массив названий категорий вложений
	 */
    public String[] getCategoriesForType(String documentType) {
        ParameterCheck.mandatory("documentType", documentType);
        QName type = QName.createQName(documentType, serviceRegistry.getNamespaceService());
        List<String> categories = this.documentAttachmentsService.getCategories(type);
		String[] result = new String[categories.size()];
		for (String category : categories) {
			int i = categories.indexOf(category);
			if (category.contains("|")) {
				result[i] = category.substring(0, category.indexOf('|'));
			} else {
				result[i] = category;
			}
		}
		return result;
    }

	/**
	 * Удаление вложения
	 * @param nodeRef nodeRef вложения
	 * @return Сообщение о статусе удаления
	 */
    public String deleteAttachment(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		NodeRef ref = new NodeRef(nodeRef);
		if (this.nodeService.exists(ref) && this.documentAttachmentsService.isDocumentAttachment(ref)) {
			this.documentAttachmentsService.deleteAttachment(ref);
			return "Success delete";
		}
		return "Failure: node not found";
	}

	/**
	 * Получение всех версий вложения
	 * @param nodeRef nodeRef вложения
	 * @return массив с версиями вложения
	 */
	public Scriptable getAttachmentVersions(String nodeRef) {
		NodeRef ref = new NodeRef(nodeRef);
		if (this.nodeService.exists(ref) && this.documentAttachmentsService.isDocumentAttachment(ref)) {
			Collection<Version> versions = this.documentAttachmentsService.getAttachmentVersions(ref);
			if (versions != null) {
				return createVersionScriptable(versions);
			}
		}
		return null;
	}

	/**
	 * Проверка, что категория доступна только для чтения
	 * @param nodeRef nodeRef категории вложений
	 * @return true, если категория доступна только для чтения
	 */
	public boolean isReadonlyCategory(String nodeRef) {
		boolean result = false;
		NodeRef ref = new NodeRef(nodeRef);
		if (this.nodeService.exists(ref)) {
			return this.documentAttachmentsService.isReadonlyCategory(ref);
		}
		return result;
	}

	/**
	 * Логгирование копирования вложений
	 * @param originalNodeRef nodeRef оригинального вложение
	 * @param copiedNodeRef nodeRef скопированного вложение
	 * @return Сообщение о статусе логгирования
	 */
	public String copyAttachmentLog(String originalNodeRef, String copiedNodeRef) {
		ParameterCheck.mandatory("originalNodeRef", originalNodeRef);
		ParameterCheck.mandatory("copiedNodeRef", copiedNodeRef);

		NodeRef originalRef = new NodeRef(originalNodeRef);
		NodeRef copiedRef = new NodeRef(copiedNodeRef);
		if (this.nodeService.exists(originalRef) && this.documentAttachmentsService.isDocumentAttachment(originalRef) && this.nodeService.exists(copiedRef)) {
			this.documentAttachmentsService.copyAttachmentLog(originalRef, copiedRef);
			return "Success log";
		}
		return "Failure: node not found";
	}

	/**
	 * Получение вложений по категории
	 * @param documentRef nodeRef документа
	 * @param categoryName название категории вложений
	 * @return массив вложений
	 */
	public Scriptable getAttachmentsByCategory(String documentRef, String categoryName) {
		ParameterCheck.mandatory("documentRef", documentRef);
		ParameterCheck.mandatory("categoryName", categoryName);

		NodeRef ref = new NodeRef(documentRef);
		if (this.nodeService.exists(ref)) {
			List<NodeRef> attachments = this.documentAttachmentsService.getAttachmentsByCategory(ref, categoryName);
			if (attachments != null) {
				return createScriptable(attachments);
			}
		}
		return null;
	}

	/**
	 * Получение вложений по категории
	 * @param category категория вложений
	 * @return массив вложений
	 */
	public Scriptable getAttachmentsByCategory(ScriptNode category) {
		ParameterCheck.mandatory("category", category);

		List<NodeRef> attachments = this.documentAttachmentsService.getAttachmentsByCategory(category.getNodeRef());
		if (attachments != null) {
			return createScriptable(attachments);
		}
		return null;
	}

	/**
	 * Получение вложений по категории
	 * @param categoryRef nodeRef категории вложений
	 * @return массив вложений
	 */
	public Scriptable getAttachmentsByCategory(String categoryRef) {
		ParameterCheck.mandatory("categoryRef", categoryRef);

		NodeRef ref = new NodeRef(categoryRef);
		if (this.nodeService.exists(ref)) {
			List<NodeRef> attachments = this.documentAttachmentsService.getAttachmentsByCategory(ref);
			if (attachments != null) {
				return createScriptable(attachments);
			}
		}
		return null;
	}

	/**
	 * Проверка, что вложение является внутренним
	 * @param attachment вложение
	 * @return true, если вложение внутрненние, false - если вложение - ссылка
	 */
	public boolean isInnerAttachment(ScriptNode attachment) {
		ParameterCheck.mandatory("attachment", attachment);
		return documentAttachmentsService.isDocumentAttachment(attachment.getNodeRef()) &&
				attachment.getParent() != null &&
				documentAttachmentsService.isDocumentCategory(attachment.getParent().getNodeRef());
	}

    public void addAttachment(ScriptNode document, ScriptNode category) {
        documentAttachmentsService.addAttachment(document.getNodeRef(), category.getNodeRef());
    }

    /**
     * Разблокировка всех вложений документа и удаление всех ссылок (не ассоциаций) на вложения документа.
     * @param document документ
     */
    public void unlockAttachmentsAndClearLinks(ScriptNode document) {
        ParameterCheck.mandatory("document", document);
	    documentAttachmentsService.unlockAttachmentsAndClearLinks(document.getNodeRef());
    }

    /**
     * Получение категории вложений по типу документа из справочника настроек типов документов
     * @param document документ
     * @return название категории
     */
    public String getDefaultUploadCategoryName(ScriptNode document) {
		ParameterCheck.mandatory("document", document);
		return documentAttachmentsService.getDefaultUploadCategoryName(document.getNodeRef());
	}

    /**
     * Получение категории вложений по типу документа из справочника настроек типов документов
     * @param documentType Тип документа
     * @return название категории
     */
	public String getDefaultUploadCategoryName(String documentType) {
		ParameterCheck.mandatory("documentType", documentType);
		QName type = QName.createQName(documentType, serviceRegistry.getNamespaceService());
		return documentAttachmentsService.getDefaultUploadCategoryName(type);
	}
}
