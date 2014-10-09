package ru.it.lecm.workflow.signing.api.deprecated;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
@Deprecated
public final class SigningWorkflowModel {

	public final static String SIGN_PREFIX = "lecmSign";
	public final static String SIGN_RESULT_PREFIX = "lecmSignResult";
	public final static String SIGN_NAMESPACE = "http://www.it.ru/logicECM/model/signing/wokflow/1.0";
	public final static String SIGN_RESULT_NAMESPACE = "http://www.it.ru/logicECM/model/signing/workflow/result/1.0";

	public final static QName TYPE_SIGNING = QName.createQName(SIGN_NAMESPACE, "signing");
	public final static QName TYPE_SIGN_TASK = QName.createQName(SIGN_NAMESPACE, "signTask");

	public final static QName TYPE_SIGN_RESULT_LIST = QName.createQName(SIGN_RESULT_NAMESPACE, "signResultList");
	public final static QName TYPE_SIGN_RESULT_ITEM = QName.createQName(SIGN_RESULT_NAMESPACE, "signResultItem");
	public final static QName PROP_SIGN_RESULT_ITEM_DECISION = QName.createQName(SIGN_RESULT_NAMESPACE, "signResultItemDecision");
	public final static QName PROP_SIGN_RESULT_LIST_DECISION =  QName.createQName(SIGN_RESULT_NAMESPACE, "signResultListDecision");

	private SigningWorkflowModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of SigningWorkflowModel class.");
	}
}
