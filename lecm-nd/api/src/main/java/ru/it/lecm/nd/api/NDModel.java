/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.nd.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author ikhalikov
 */
public final class NDModel {
	public final static String ND_PREFIX = "lecm-nd";
	public final static String ND_NAMESPACE = "http://www.it.ru/lecm/ND/1.0";

	public final static QName TYPE_ND = QName.createQName(ND_NAMESPACE, "document");

	public final static QName PROP_ND_BEGIN = QName.createQName(ND_NAMESPACE, "begin-date");
	public final static QName PROP_ND_END = QName.createQName(ND_NAMESPACE, "end-date");
	public final static QName PROP_ND_MEANLESS = QName.createQName("http://www.it.ru/lecm/document/aspects/1.0", "work-meanless-field");

	public final static QName ASSOC_ND_REGISTRAR = QName.createQName(ND_NAMESPACE, "registrar-assoc");


	private NDModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of NDModel class.");
	}
}
