package ru.it.lecm.approval.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;

import java.util.Date;

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
	QName PROP_APPROVAL_ITEM_START_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-start-date");
	QName PROP_APPROVAL_ITEM_DUE_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-due-date");
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

	void logFinalDecision(final NodeRef approvalListRef, final String finalDecision);

	/**
	 * раздать всем участникам процесса согласования права LECM_BASIC_PG_Reviewer
	 * @param employees
	 * @param bpmPackage
	 */
	void grantReviewerPermissions(final NodeRef employeeRef, final NodeRef bpmPackage);

	void notifyApprovalStarted(final NodeRef employeeRef, final Date dueDate, final NodeRef bpmPackage);

	/**
	 * уведомить инициатора о том что решение о согласовании принято
	 * @param decision решение
	 * @param bpmPackage
	 */
	void notifyFinalDecision(final String decision, final NodeRef bpmPackage);

    /**
     * получение ссылки на документ через переменную регламента bpm_package
     * @param bpmPackage
     * @return
     */
    NodeRef getDocumentFromBpmPackage(final NodeRef bpmPackage);

	void notifyComingSoonAssignees(final String processInstanceId);

	void notifyComingSoonInitiator(final String processInstanceId);
}
