package ru.it.lecm.workflow.signing.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public final class SigningAspectsModel {
	public final static String SIGN_ASPECTS_PREFIX = "lecmSignAspects";
	public final static String SIGN_ASPECTS_NAMESPACE = "http://www.it.ru/logicECM/model/signing/aspects/1.0";

	public final static QName ASPECT_SIGNING_DETAILS = QName.createQName(SIGN_ASPECTS_NAMESPACE, "signingDetailsAspect");
	public final static QName PROP_IS_SIGNED = QName.createQName(SIGN_ASPECTS_NAMESPACE, "isSigned");
	public final static QName PROP_SIGNING_DATE = QName.createQName(SIGN_ASPECTS_NAMESPACE, "signingDate");
	public final static QName PROP_SIGNING_STATE = QName.createQName(SIGN_ASPECTS_NAMESPACE, "signingState");
	public final static QName ASSOC_SIGNER_EMPLOYEE_ASSOC = QName.createQName(SIGN_ASPECTS_NAMESPACE, "signerEmployeeAssoc");

	private SigningAspectsModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of SigningAspectsModel class.");
	}
}
