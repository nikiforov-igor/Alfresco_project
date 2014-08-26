package ru.it.lecm.workflow.approval;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
final class ApprovalGlobalSettingsModel {

	final static String APPROVAL_GLOBAL_SETTINGS_NAMESPACE = "http://www.it.ru/logicECM/model/approval/workflow/global-settings/1.0";
	final static String APPROVAL_GLOBAL_SETTINGS_PREFIX = "lecmApproveGlobalSettings";
	final static QName TYPE_SETTINGS = QName.createQName(APPROVAL_GLOBAL_SETTINGS_NAMESPACE, "settings");
	final static QName PROP_DEFAULT_APPROVAL_TERM = QName.createQName(APPROVAL_GLOBAL_SETTINGS_NAMESPACE, "defaultApprovalTerm");

	private ApprovalGlobalSettingsModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ApprovalGlobalSettingsModel class.");
	}
}
