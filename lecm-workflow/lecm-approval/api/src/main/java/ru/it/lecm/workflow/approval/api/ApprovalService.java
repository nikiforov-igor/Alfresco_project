package ru.it.lecm.workflow.approval.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface ApprovalService {
	NodeRef getApprovalFolder();
	NodeRef getSettings();
	/**
	 * Получить срок согласования по умолчанию.
	 * @return срок согласования в днях
	 */
	int getApprovalTerm();
}
