package ru.it.lecm.approval;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public class ApprovalListServiceSequential extends ApprovalListServiceAbstract {

	@Override
	protected NodeRef getOrCreateApprovalFolder(NodeRef parentRef) {
		return getOrCreateSequentialApprovalFolder(parentRef);
	}
}
