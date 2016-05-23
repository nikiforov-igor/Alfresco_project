package ru.it.lecm.workflow.approval;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.workflow.approval.api.ApprovalService;

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

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDefaultApprovalTerm(Integer defaultApprovalTerm) {
		this.defaultApprovalTerm = (defaultApprovalTerm != null) ? defaultApprovalTerm : DEFAULT_DEFAULT_APPROVAL_TERM;
	}

	public void init() {
		if (null == getSettings()) {
			AuthenticationUtil.runAsSystem(this);
		}
	}

	@Override
	public NodeRef doWork() throws Exception {
		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
		return transactionHelper.doInTransaction(this, false, true);
	}

	@Override
	public NodeRef execute() throws Throwable {
		PropertyMap props = new PropertyMap();
		if (defaultApprovalTerm != null) {
			props.put(ApprovalGlobalSettingsModel.PROP_DEFAULT_APPROVAL_TERM, defaultApprovalTerm);
		}
		return createNode(getApprovalFolder(), ApprovalGlobalSettingsModel.TYPE_SETTINGS, APPROVAL_GLOBAL_SETTINGS_NAME, props);
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
		return nodeService.getChildByName(getApprovalFolder(), ContentModel.ASSOC_CONTAINS, APPROVAL_GLOBAL_SETTINGS_NAME);
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
		return nodeService.createNode(documentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, props).getChildRef();
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
}
