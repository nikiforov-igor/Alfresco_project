package ru.it.lecm.workflow.approval.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class ApprovalAspectsModel {

	public static final String APPROVAL_ASPECTS_URI = "http://www.it.ru/logicECM/model/approval/aspects/1.0";

	public static final QName ASPECT_APPROVAL_DETAILS = QName.createQName(APPROVAL_ASPECTS_URI, "approvalDetailsAspect");
	public static final QName PROP_APPROVAL_STATE = QName.createQName(APPROVAL_ASPECTS_URI, "approvalState");
	public static final QName PROP_APPROVAL_DECISION = QName.createQName(APPROVAL_ASPECTS_URI, "approvalDecision");
	public static final QName PROP_APPROVAL_HAS_COMMENT = QName.createQName(APPROVAL_ASPECTS_URI, "hasComment");

	private ApprovalAspectsModel() {
		throw new IllegalStateException("Class ApprovalAspectsModel can not be instantiated");
	}

}
