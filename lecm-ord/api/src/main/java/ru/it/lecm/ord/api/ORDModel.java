package ru.it.lecm.ord.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author snovikov
 */
public final class ORDModel {

	public final static String ORD_PREFIX = "lecm-ord";
	public final static String ORD_NAMESPACE = "http://www.it.ru/lecm/ORD/1.0";

	public final static QName TYPE_ORD = QName.createQName(ORD_NAMESPACE, "document");

	public final static QName ASSOC_ORD_REGISTRAR = QName.createQName(ORD_NAMESPACE, "registrar-assoc");

	private ORDModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ORDModel class.");
	}
}
