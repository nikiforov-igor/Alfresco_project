package ru.it.lecm.approval;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public class ApprovalListServiceSequential extends ApprovalListServiceAbstract {

	@Override
	protected NodeRef getOrCreateApprovalFolder(NodeRef parentRef) {
		NodeRef parallelApprovalRef = getFolder(parentRef, "Последовательное согласование");
		if (parallelApprovalRef == null) {
			parallelApprovalRef = createFolder(parentRef, "Последовательное согласование");
		}
		return parallelApprovalRef;
	}
}
