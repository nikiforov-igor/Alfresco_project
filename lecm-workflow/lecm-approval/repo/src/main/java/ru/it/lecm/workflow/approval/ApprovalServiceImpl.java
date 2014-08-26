package ru.it.lecm.workflow.approval;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyMap;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.workflow.approval.api.ApprovalService;

/**
 *
 * @author vmalygin
 */
public class ApprovalServiceImpl extends BaseBean implements ApprovalService, RunAsWork<NodeRef>, RetryingTransactionCallback<NodeRef> {

	public final static String APPROVAL_FOLDER = "APPROVAL_FOLDER";
	private final static String APPROVAL_GLOBAL_SETTINGS_NAME = "Глобальные настройки согласования";

	private Integer defaultApprovalTerm;

	public void setDefaultApprovalTerm(Integer defaultApprovalTerm) {
		this.defaultApprovalTerm = defaultApprovalTerm;
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

}
