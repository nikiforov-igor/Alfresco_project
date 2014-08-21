package ru.it.lecm.workflow.routes.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.routes.api.RoutesModel;
import ru.it.lecm.workflow.routes.api.RoutesService;

/**
 *
 * @author vlevin
 */
public class RoutesJavascriptExtension extends BaseWebScript {
	private RoutesService routesService;

	public void setRoutesService(RoutesService routesService) {
		this.routesService = routesService;
	}

	public ScriptNode getRoutesFolder() {
		NodeRef routesFolderNode = routesService.getRoutesFolder();
		return new ScriptNode(routesFolderNode, serviceRegistry, getScope());
	}

	public String getRouteType() {
		return RoutesModel.TYPE_ROUTE.toPrefixString(serviceRegistry.getNamespaceService());
	}

}
