package ru.it.lecm.workflow.approval.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface ApprovalService {
	NodeRef getApprovalFolder();
	NodeRef getSettings();
}
