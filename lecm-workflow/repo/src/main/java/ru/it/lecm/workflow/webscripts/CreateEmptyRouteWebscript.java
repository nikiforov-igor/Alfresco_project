package ru.it.lecm.workflow.webscripts;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.DeclarativeWebScriptHelper;
import ru.it.lecm.workflow.RouteType;
import ru.it.lecm.workflow.api.RouteService;

/**
 *
 * @author vmalygin
 */
public class CreateEmptyRouteWebscript extends DeclarativeWebScript {

	private final static String ROUTE_TYPE = "routeType";

	private RouteService routeService;

	public void setRouteService(RouteService routeService) {
		this.routeService = routeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		String routeType = DeclarativeWebScriptHelper.mandatoryParameter(ROUTE_TYPE, req.getParameter(ROUTE_TYPE));
		NodeRef routeRef = routeService.createEmptyRoute(RouteType.get(routeType));
		Map<String, Object> route = new HashMap<String, Object>();
		route.put("routeRef", routeRef);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", new JSONObject(route));
		return result;
	}
}
