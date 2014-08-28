package ru.it.lecm.workflow.routes.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
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
	public String getStageType() {
		return RoutesModel.TYPE_STAGE.toPrefixString(serviceRegistry.getNamespaceService());
	}
	public String getStageItemType() {
		return RoutesModel.TYPE_STAGE_ITEM.toPrefixString(serviceRegistry.getNamespaceService());
	}

	public ScriptNode createNewTemporaryNode(String destination, String nodeType) {
		NodeRef tempNode, destinationNode = new NodeRef(destination);
		QName nodeTypeQName = QName.createQName(nodeType, serviceRegistry.getNamespaceService());
		tempNode = routesService.createNewTemporaryNode(destinationNode, nodeTypeQName);

		return new ScriptNode(tempNode, serviceRegistry, getScope());
	}

	public ScriptNode getDocumentCurrentIteration(ScriptNode documentNode) {
		NodeRef iterationNode = routesService.getDocumentCurrentIteration(documentNode.getNodeRef());
		return iterationNode != null ? new ScriptNode(iterationNode, serviceRegistry, getScope()) : null;
	}

}
