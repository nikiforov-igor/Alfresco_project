package ru.it.lecm.workflow.approval.api.deprecated;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
@Deprecated
public final class ApprovalResultModel {

	public final static String APPROVAL_RESULT_NAMESPACE = "http://www.it.ru/logicECM/model/approval/workflow/result/1.0";
	public final static String APPROVAL_RESULT_PREFIX = "lecmApprovalResult";
	public final static QName TYPE_APPROVAL_LIST = QName.createQName(APPROVAL_RESULT_NAMESPACE, "approvalResultList");
	public final static QName PROP_APPROVAL_LIST_DECISION = QName.createQName(APPROVAL_RESULT_NAMESPACE, "approvalResultListDecision");
	public final static QName TYPE_APPROVAL_ITEM = QName.createQName(APPROVAL_RESULT_NAMESPACE, "approvalResultItem");
	public final static QName PROP_APPROVAL_ITEM_DECISION = QName.createQName(APPROVAL_RESULT_NAMESPACE, "approvalResultItemDecision");
	public final static QName PROP_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_RESULT_NAMESPACE, "approvalResultItemComment");
	public final static QName ASSOC_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_RESULT_NAMESPACE, "approvalResultItemCommentAssoc");

	private ApprovalResultModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ApprovalResultModel class.");
	}
}
