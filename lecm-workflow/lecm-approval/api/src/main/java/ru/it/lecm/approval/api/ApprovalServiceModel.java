package ru.it.lecm.approval.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class ApprovalServiceModel {

	public final static String APPROVAL_LIST_NAMESPACE = "http://www.it.ru/logicECM/model/approval-list/1.0";
	public final static String APPROVAL_LIST_PREFIX = "lecm-al";
	public final static QName TYPE_APPROVAL_ITEM = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item");
	public final static QName TYPE_APPROVAL_LIST = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list");
	public final static QName PROP_APPROVAL_ITEM_DECISION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-decision");
	public final static QName PROP_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-comment");
	public final static QName PROP_APPROVAL_LIST_DECISION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-decision");
	public final static QName ASSOC_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-comment-assoc");

	private ApprovalServiceModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ApprovalServiceModel class.");
	}
}
