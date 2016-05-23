package ru.it.lecm.workflow.approval.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
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

	public int getApprovalTerm() {
		return approvalService.getApprovalTerm();
	}

	public ScriptNode getDocumentApprovalFolder(final ScriptNode document) {
		return new ScriptNode(approvalService.getDocumentApprovalFolder(document.getNodeRef()), serviceRegistry, getScope());
	}

	public ScriptNode createDocumentApprovalFolder(final ScriptNode document) {
		return new ScriptNode(approvalService.createDocumentApprovalFolder(document.getNodeRef()), serviceRegistry, getScope());
	}

	public ScriptNode getDocumentApprovalHistoryFolder(final ScriptNode document) {
		return new ScriptNode(approvalService.getDocumentApprovalHistoryFolder(document.getNodeRef()), serviceRegistry, getScope());
	}

	public ScriptNode createDocumentApprovalHistoryFolder(final ScriptNode document) {
		return new ScriptNode(approvalService.createDocumentApprovalHistoryFolder(document.getNodeRef()), serviceRegistry, getScope());
	}

	public boolean checkExpression(NodeRef nodeRef, String expression) {
		return approvalService.checkExpression(nodeRef, expression);
	}
}
