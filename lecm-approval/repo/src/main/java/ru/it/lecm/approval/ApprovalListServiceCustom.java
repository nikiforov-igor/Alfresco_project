package ru.it.lecm.approval;

import org.alfresco.service.cmr.repository.NodeRef;

public class ApprovalListServiceCustom extends ApprovalListServiceAbstract {

    private static final String CUSTOM_APPROVAL = "Специальное согласование";

    @Override
	protected NodeRef getOrCreateApprovalFolder(NodeRef parentRef) {
		NodeRef customApprovalRef = getFolder(parentRef, CUSTOM_APPROVAL);
		if (customApprovalRef == null) {
            customApprovalRef = createFolder(parentRef, CUSTOM_APPROVAL);
		}
		return customApprovalRef;
	}
}
