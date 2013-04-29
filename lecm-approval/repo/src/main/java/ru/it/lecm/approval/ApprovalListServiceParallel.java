package ru.it.lecm.approval;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public class ApprovalListServiceParallel extends ApprovalListServiceAbstract {

	@Override
	protected NodeRef getOrCreateApprovalFolder(NodeRef parentRef) {
		NodeRef parallelApprovalRef = getFolder(parentRef, "Параллельное согласование");
		if (parallelApprovalRef == null) {
			parallelApprovalRef = createFolder(parentRef, "Параллельное согласование");
		}
		return parallelApprovalRef;
	}
}
