package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;

import java.util.Collection;
import java.util.List;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 12:08
 */
public class DocumentAttachmentsWebScriptBean extends BaseWebScript {

	private DocumentAttachmentsService documentAttachmentsService;

	protected NodeService nodeService;

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

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

	public Scriptable getCategories(String documentNodeRef) {
		ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

		NodeRef documentRef = new NodeRef(documentNodeRef);
		if (this.nodeService.exists(documentRef)) {
			List<NodeRef> categories = this.documentAttachmentsService.getCategories(documentRef);
			return createScriptable(categories);
		}
		return null;
	}

	public String[] getCategoriesForType(String documentType) {
		ParameterCheck.mandatory("documentType", documentType);
		QName type = QName.createQName(documentType, serviceRegistry.getNamespaceService());
		List<String> categories = this.documentAttachmentsService.getCategories(type);
		return categories.toArray(new String[categories.size()]);
	}

	public String deleteAttachment(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		NodeRef ref = new NodeRef(nodeRef);
		if (this.nodeService.exists(ref) && this.documentAttachmentsService.isDocumentAttachment(ref)) {
			this.documentAttachmentsService.deleteAttachment(ref);
			return "Success delete";
		}
		return "Failure: node not found";
	}

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

	public boolean isReadonlyCategory(String nodeRef) {
		boolean result = false;
		NodeRef ref = new NodeRef(nodeRef);
		if (this.nodeService.exists(ref)) {
			return this.documentAttachmentsService.isReadonlyCategory(ref);
		}
		return result;
	}

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

	public Scriptable getAttachmentsByCategory(ScriptNode category) {
		ParameterCheck.mandatory("category", category);

		List<NodeRef> attachments = this.documentAttachmentsService.getAttachmentsByCategory(category.getNodeRef());
		if (attachments != null) {
			return createScriptable(attachments);
		}
		return null;
	}

	public boolean isInnerAttachment(ScriptNode attachment) {
		ParameterCheck.mandatory("attachment", attachment);
		return documentAttachmentsService.isDocumentAttachment(attachment.getNodeRef()) &&
				attachment.getParent() != null &&
				documentAttachmentsService.isDocumentCategory(attachment.getParent().getNodeRef());
	}
}
