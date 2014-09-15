package ru.it.lecm.workflow.routes.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Scriptable;
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

	public Scriptable getAllowedRoutesForCurrentUser(ScriptNode documentNode) {
		return createScriptable(routesService.getAllowedRoutesForCurrentUser(documentNode.getNodeRef()));
	}

	public ScriptNode convertRouteToIteration(ScriptNode documentNode, ScriptNode routeNode) {
		NodeRef iterationNode = routesService.convertRouteToIteration(documentNode.getNodeRef(), routeNode.getNodeRef());

		return iterationNode != null ? new ScriptNode(iterationNode, serviceRegistry, getScope()) : null;
	}

	public ScriptNode createEmptyIteration(ScriptNode documentNode) {
		NodeRef iterationNode = routesService.createEmptyIteration(documentNode.getNodeRef());

		return iterationNode != null ? new ScriptNode(iterationNode, serviceRegistry, getScope()) : null;
	}

	public void resolveStageItemMacros(ScriptNode stageItem) {
		routesService.resolveStageItemMacros(stageItem.getNodeRef());
	}

	public ScriptNode getSourceRouteForIteration(ScriptNode iterationNode) {
		NodeRef sourceRouteNode = routesService.getSourceRouteForIteration(iterationNode.getNodeRef());

		return sourceRouteNode != null ? new ScriptNode(sourceRouteNode, serviceRegistry, getScope()) : null;
	}

}
