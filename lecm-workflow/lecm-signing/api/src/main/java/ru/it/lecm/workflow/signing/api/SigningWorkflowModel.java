package ru.it.lecm.workflow.signing.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public final class SigningWorkflowModel {

	public final static String OUTGOING_PREFIX = "lecm-outgoing";
	public final static String OUTGOING_NAMESPACE = "http://www.it.ru/logicECM/outgoing/1.0";

	public final static QName TYPE_OUTGOING = QName.createQName(OUTGOING_NAMESPACE, "document");

	private SigningWorkflowModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of OutgoingModel class.");
	}
}
