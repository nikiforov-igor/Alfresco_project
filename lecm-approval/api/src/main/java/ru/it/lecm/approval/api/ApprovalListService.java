package ru.it.lecm.approval.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;

/**
 *
 * @author vlevin
 */
public interface ApprovalListService {
	String APPROVAL_LIST_NAMESPACE = "http://www.it.ru/logicECM/model/approval-list/1.0";
	String APPROVAL_LIST_PREFIX = "lecm-al";
	QName TYPE_APPROVAL_ITEM = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item");
	QName TYPE_APPROVAL_LIST = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list");
	QName PROP_APPROVAL_ITEM_DECISION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-decision");
	QName PROP_APPROVAL_ITEM_APPROVE_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-approve-date");
	QName PROP_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-comment");
	QName PROP_APPROVAL_LIST_DECISION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-decision");
	QName PROP_APPROVAL_LIST_APPROVE_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-approve-date");
	QName PROP_APPROVAL_LIST_APPROVE_START = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-approve-start");
	QName PROP_APPROVAL_LIST_DOCUMENT_VERSION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-document-version");
	QName ASSOC_APPROVAL_ITEM_EMPLOYEE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-employee-assoc");
	QName ASSOC_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-comment-assoc");
	QName ASSOC_APPROVAL_LIST_CONTAINS_APPROVAL_ITEM = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-contains-approval-item");

	/**
	 * формирование нового листа согласования, для текущей версии регламента
	 * @return ссылка на новый лист согласования
	 */
	NodeRef createApprovalList(NodeRef bpmPackage);

	/**
	 * запись решения о согласовании от текущего исполнителя в лист согласования
	 * @param approvalListRef
	 * @param taskDecision
	 */
	void logDecision(final NodeRef approvalListRef, final JSONObject taskDecision);
}
