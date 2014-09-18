package ru.it.lecm.workflow.routes.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class RoutesMacrosModel {

	public final static String ROUTES_MACROS_URL = "http://www.it.ru/logicECM/model/workflow/routes/macros/1.0";

	public final static QName TYPE_MACROS = QName.createQName(ROUTES_MACROS_URL, "macros");
	public final static QName PROP_MACROS_STRING = QName.createQName(ROUTES_MACROS_URL, "macrosString");
	public final static QName PROP_MACROS_SERVICE_ID = QName.createQName(ROUTES_MACROS_URL, "macrosServiceId");

	private RoutesMacrosModel() {
		throw new IllegalStateException("Class RoutesMacrosModel can not be instantiated");
	}

}
