package ru.it.lecm.workflow.routes.api;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public class ConvertRouteToIterationResult {

	private NodeRef iterationNode;
	private List<NodeRef> stageItems;
	private List<String> scriptErrors;

	public NodeRef getIterationNode() {
		return iterationNode;
	}

	public void setIterationNode(NodeRef iterationNode) {
		this.iterationNode = iterationNode;
	}

	public List<NodeRef> getStageItems() {
		return stageItems;
	}

	public void setStageItems(List<NodeRef> stageItems) {
		this.stageItems = stageItems;
	}

	public List<String> getScriptErrors() {
		return scriptErrors;
	}

	public void setScriptErrors(List<String> scriptErrors) {
		this.scriptErrors = scriptErrors;
	}

}
