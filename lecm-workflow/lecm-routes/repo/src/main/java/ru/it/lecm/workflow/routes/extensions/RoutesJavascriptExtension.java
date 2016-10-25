package ru.it.lecm.workflow.routes.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.routes.api.ConvertRouteToIterationResult;
import ru.it.lecm.workflow.routes.api.RoutesModel;
import ru.it.lecm.workflow.routes.api.RoutesService;
import ru.it.lecm.workflow.routes.entity.ConvertRouteToIterationResultForScript;

import java.io.Serializable;

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

	public ConvertRouteToIterationResultForScript convertRouteToIteration(ScriptNode documentNode, ScriptNode routeNode) {
		ConvertRouteToIterationResultForScript result = new ConvertRouteToIterationResultForScript();
		ValueConverter converter = new ValueConverter();

		ConvertRouteToIterationResult convertResult = routesService.convertRouteToIteration(documentNode.getNodeRef(), routeNode.getNodeRef());
		result.setIterationNode((ScriptNode) converter.convertValueForScript(serviceRegistry, getScope(), null, convertResult.getIterationNode()));
		result.setScriptErrors((Scriptable) converter.convertValueForScript(serviceRegistry, getScope(), null, (Serializable)convertResult.getScriptErrors()));
		result.setStageItems((Scriptable) converter.convertValueForScript(serviceRegistry, getScope(), null, (Serializable)convertResult.getStageItems()));

		return result;
	}

	public ScriptNode createEmptyIteration(ScriptNode documentNode) {
		NodeRef iterationNode = routesService.createEmptyIteration(documentNode.getNodeRef());

		return iterationNode != null ? new ScriptNode(iterationNode, serviceRegistry, getScope()) : null;
	}

	public ScriptNode createIterationFromPrevious(ScriptNode documentNode) {
		NodeRef iterationNode = routesService.createIterationFromPrevious(documentNode.getNodeRef());

		return iterationNode != null ? new ScriptNode(iterationNode, serviceRegistry, getScope()) : null;
	}

	public boolean resolveStageItemMacros(ScriptNode stageItem, ScriptNode documentNode) {
		return routesService.resolveStageItemMacros(stageItem.getNodeRef(), documentNode.getNodeRef());
	}

	public ScriptNode getSourceRouteForIteration(ScriptNode iterationNode) {
		NodeRef sourceRouteNode = routesService.getSourceRouteForIteration(iterationNode.getNodeRef());

		return sourceRouteNode != null ? new ScriptNode(sourceRouteNode, serviceRegistry, getScope()) : null;
	}

	public ScriptNode getDocumentByIteration(ScriptNode iterationNode) {
		NodeRef documentNode = routesService.getDocumentByIteration(iterationNode.getNodeRef());

		return documentNode != null ? new ScriptNode(documentNode, serviceRegistry, getScope()) : null;
	}
	public ScriptNode getDocumentByStage(ScriptNode stageNode) {
		NodeRef documentNode = routesService.getDocumentByStage(stageNode.getNodeRef());

		return documentNode != null ? new ScriptNode(documentNode, serviceRegistry, getScope()) : null;
	}
	public ScriptNode getDocumentByStageItem(ScriptNode stageItemNode) {
		NodeRef documentNode = routesService.getDocumentByStageItem(stageItemNode.getNodeRef());

		return documentNode != null ? new ScriptNode(documentNode, serviceRegistry, getScope()) : null;
	}

	public boolean hasEmployeesInRoute(final ScriptNode node) {
		return routesService.hasEmployeesInRoute(node.getNodeRef());
	}

	public boolean hasEmployeesInRoute(final ScriptNode route, final ScriptNode document) {
		return routesService.hasEmployeesInRoute(route.getNodeRef(), document != null ? document.getNodeRef() : null);
	}

	public boolean isRouteEmpty(ScriptNode node) {
		return routesService.isRouteEmpty(node.getNodeRef());
	}

	public boolean isRouteEmpty(ScriptNode route, ScriptNode document) {
		return routesService.isRouteEmpty(route.getNodeRef(), document.getNodeRef());
	}

	public boolean hasEmployeesInDocRoute(final ScriptNode route) {
		return routesService.hasEmployeesInRoute(route.getNodeRef());
	}

	public boolean hasPotentialEmployeesInRoute(final ScriptNode route, final ScriptNode document) {
		return routesService.hasPotentialEmployeesInRoute(route.getNodeRef(), document.getNodeRef());
	}

	public String getApprovalState(final ScriptNode document) {
		return routesService.getApprovalState(document.getNodeRef());
	}

	public Scriptable getEmployeesOfAllDocumentRoutes(ScriptNode document) {
		return createScriptable(routesService.getEmployeesOfAllDocumentRoutes(document.getNodeRef()));
	}
}
