package ru.it.lecm.workflow.approval.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.approval.api.ApprovalService;

/**
 *
 * @author vmalygin
 */
public class ApprovalJavascriptExtension extends BaseWebScript {

	private ApprovalService approvalService;

	public void setApprovalService(ApprovalService approvalService) {
		this.approvalService = approvalService;
	}

	public ScriptNode getApprovalFolder() {
		return new ScriptNode(approvalService.getApprovalFolder(), serviceRegistry, getScope());
	}

	public ScriptNode getSettings() {
		return new ScriptNode(approvalService.getSettings(), serviceRegistry, getScope());
	}
}
