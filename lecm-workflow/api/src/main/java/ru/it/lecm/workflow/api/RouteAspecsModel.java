package ru.it.lecm.workflow.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public final class RouteAspecsModel {

	/**
	 * lecm-document-route
	 */
	public final static String ROUTE_ASPECTS_PREFIX = "lecm-document-route";

	/**
	 * http://www.it.ru/logicECM/document/route/1.0
	 */
	public final static String ROUTE_ASPECTS_NAMESPACE = "http://www.it.ru/logicECM/document/route/1.0";

	/**
	 * &lt;aspect name="lecm-document-route:routable"&gt;
	 */
	public final static QName ASPECT_ROUTABLE = QName.createQName(ROUTE_ASPECTS_NAMESPACE, "routable");

	/**
	 * &lt;property name="lecm-document-route:isRoutable"&gt;
	 */
	public final static QName PROP_IS_ROUTABLE = QName.createQName(ROUTE_ASPECTS_NAMESPACE, "isRoutable");

	/**
	 * &lt;property name="lecm-document-route:routeRef"&gt;
	 */
	public final static QName PROP_ROUTEREF = QName.createQName(ROUTE_ASPECTS_NAMESPACE, "routeRef");

	/**
	 * &lt;property name="lecm-document-route:is-register-after-signed"&gt;
	 */
	public final static QName PROP_IS_REGISTER_AFTER_SIGNED = QName.createQName(ROUTE_ASPECTS_NAMESPACE, "is-register-after-signed");

	/**
	 * &lt;property name="lecm-document-route:is-approved"&gt;
	 */
	public final static QName PROP_IS_APPROVED = QName.createQName(ROUTE_ASPECTS_NAMESPACE, "is-approved");

	/**
	 * &lt;property name="lecm-document-route:is-signed"&gt;
	 */
	public final static QName PROP_IS_SIGNED = QName.createQName(ROUTE_ASPECTS_NAMESPACE, "is-signed");

	private RouteAspecsModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of RouteAspecsModel class.");
	}
}
