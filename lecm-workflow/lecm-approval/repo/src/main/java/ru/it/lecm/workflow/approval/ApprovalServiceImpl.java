package ru.it.lecm.workflow.approval;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.workflow.approval.api.ApprovalService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vmalygin
 */
public class ApprovalServiceImpl extends BaseBean implements ApprovalService, RunAsWork<NodeRef>, RetryingTransactionCallback<NodeRef> {

	public final static String APPROVAL_FOLDER = "APPROVAL_FOLDER";
	private final static String APPROVAL_GLOBAL_SETTINGS_NAME = "Глобальные настройки согласования";
	private final static String DOCUMENT_APPROVAL_FOLDER = "Согласование";
	private final static String DOCUMENT_APPROVAL_HISTORY_FOLDER = "История";
	private final static int DEFAULT_DEFAULT_APPROVAL_TERM = 1;

	private Integer defaultApprovalTerm;
	private DocumentService documentService;
	private DocumentAttachmentsService attachmentsService;
    private ContentService contentService;
	private NodeRef settingsNode;

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDefaultApprovalTerm(Integer defaultApprovalTerm) {
		this.defaultApprovalTerm = (defaultApprovalTerm != null) ? defaultApprovalTerm : DEFAULT_DEFAULT_APPROVAL_TERM;
	}

	public void setAttachmentsService(DocumentAttachmentsService attachmentsService) {
		this.attachmentsService = attachmentsService;
	}

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }
    
    @Override
	public void initService() {
		super.initService();
		// TODO: Нужно либо добавлять throws везде, либо разворачивать логику
		try {
			doWork();
		} catch (Exception ex) {
		}
	}

	@Override
	public NodeRef doWork() throws Exception {
		if (null == getSettings()) {
			PropertyMap props = new PropertyMap();
			if (defaultApprovalTerm != null) {
				props.put(ApprovalGlobalSettingsModel.PROP_DEFAULT_APPROVAL_TERM, defaultApprovalTerm);
			}
			return createNode(getApprovalFolder(), ApprovalGlobalSettingsModel.TYPE_SETTINGS, APPROVAL_GLOBAL_SETTINGS_NAME, props);
		}
		return null;
	}

	@Override
	public NodeRef execute() throws Throwable {
		return AuthenticationUtil.runAsSystem(this);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(APPROVAL_FOLDER);
	}

	@Override
	public NodeRef getApprovalFolder() {
		return getServiceRootFolder();
	}

	@Override
	public NodeRef getSettings() {
		if (settingsNode == null) {
			settingsNode = nodeService.getChildByName(getApprovalFolder(), ContentModel.ASSOC_CONTAINS, APPROVAL_GLOBAL_SETTINGS_NAME);
		}
		
		return settingsNode;
	}

	@Override
	public int getApprovalTerm() {
		NodeRef settingsNode = getSettings();
		Integer approvalTerm = (Integer) nodeService.getProperty(settingsNode, ApprovalGlobalSettingsModel.PROP_DEFAULT_APPROVAL_TERM);

		return approvalTerm != null ? approvalTerm : defaultApprovalTerm;
	}

	@Override
	public NodeRef getDocumentApprovalFolder(final NodeRef documentRef) {
		return nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, DOCUMENT_APPROVAL_FOLDER);
	}

	@Override
	public NodeRef createDocumentApprovalFolder(final NodeRef documentRef) {
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, DOCUMENT_APPROVAL_FOLDER);
		PropertyMap props = new PropertyMap();
		props.put(ContentModel.PROP_NAME, DOCUMENT_APPROVAL_FOLDER);
		NodeRef approvalFolder = nodeService.createNode(documentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, props).getChildRef();
		if (!nodeService.hasAspect(documentRef, ASPECT_APPROVAL_DATA)) {
			nodeService.addAspect(documentRef, ASPECT_APPROVAL_DATA, null);
			nodeService.createAssociation(documentRef, approvalFolder, ASSOC_APPROVAL_FOLDER);
		}
		return approvalFolder;
	}

	@Override
	public NodeRef getDocumentApprovalHistoryFolder(final NodeRef documentRef) {
		NodeRef documentApprovalFolder = getDocumentApprovalFolder(documentRef);
		if (documentApprovalFolder == null) {
			throw new AlfrescoRuntimeException("can't get approval history folder, because approval folder doesn't exist");
		}
		return nodeService.getChildByName(documentApprovalFolder, ContentModel.ASSOC_CONTAINS, DOCUMENT_APPROVAL_HISTORY_FOLDER);
	}

	@Override
	public NodeRef createDocumentApprovalHistoryFolder(final NodeRef documentRef) {
		NodeRef documentApprovalFolder = getDocumentApprovalFolder(documentRef);
		if (documentApprovalFolder == null) {
			throw new AlfrescoRuntimeException("can't create approval history folder, because approval folder doesn't exist");
		}
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, DOCUMENT_APPROVAL_HISTORY_FOLDER);
		PropertyMap props = new PropertyMap();
		props.put(ContentModel.PROP_NAME, DOCUMENT_APPROVAL_HISTORY_FOLDER);
		return nodeService.createNode(documentApprovalFolder, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, props).getChildRef();
	}

	@Override
	public boolean checkExpression(NodeRef nodeRef, String expression) {
		return documentService.execExpression(nodeRef, expression);
	}

	@Override
	public void copyToDocumentAttachmentCategory(final NodeRef attachment, NodeRef document, String filename) throws WriteTransactionNeededException {
		final NodeRef category = attachmentsService.getCategory("Согласование", document);
		if (category != null) {
			NodeRef userTemp = repositoryStructureHelper.getUserTemp(true);

            String name = (String) nodeService.getProperty(attachment, ContentModel.PROP_NAME);
            if (StringUtils.isNotBlank(name)) {
                String extension = name.substring(name.lastIndexOf('.') + 1);
                if (filename.toCharArray()[filename.length() - 1] != '.') {
                    filename = filename + '.';
                }
                filename = filename + extension;
            }
            NodeRef prevFileNode = serviceRegistry.getNodeService().getChildByName(category, ContentModel.ASSOC_CONTAINS, filename);

            if (prevFileNode == null) {
                PropertyMap propertyMap = new PropertyMap();
                propertyMap.put(ContentModel.PROP_NAME, filename);
                NodeRef tmpAttach = nodeService.createNode(userTemp, ContentModel.ASSOC_CONTAINS, generateRandomQName(), ContentModel.TYPE_CONTENT, propertyMap).getChildRef();
                writeContent(attachment, tmpAttach);
                attachmentsService.addAttachment(tmpAttach, category);
            } else {
                AlfrescoTransactionSupport.bindResource(DocumentAttachmentsService.NOT_SECURITY_CREATE_VERSION_ATTACHMENT_POLICY, true);
                PropertyMap vProps = new PropertyMap();
                vProps.put(ContentModel.PROP_AUTO_VERSION, true);
                vProps.put(ContentModel.PROP_AUTO_VERSION_PROPS, false);
                serviceRegistry.getVersionService().ensureVersioningEnabled(prevFileNode, vProps);

                NodeRef tmpAttach = serviceRegistry.getCheckOutCheckInService().checkout(prevFileNode);
                writeContent(attachment, tmpAttach);
                Map<String, Serializable> ciProps = new HashMap<>();
                ciProps.put(Version.PROP_DESCRIPTION, "");
                ciProps.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);
                serviceRegistry.getCheckOutCheckInService().checkin(tmpAttach, ciProps);
            }
		}
	}

    private void writeContent(NodeRef src, NodeRef dst) {
        ContentReader cr = contentService.getReader(src, ContentModel.PROP_CONTENT);
        ContentWriter cw = contentService.getWriter(dst, ContentModel.PROP_CONTENT, true);
        cw.setMimetype(cr.getMimetype());
        cw.putContent(cr.getContentInputStream());
    }
}
