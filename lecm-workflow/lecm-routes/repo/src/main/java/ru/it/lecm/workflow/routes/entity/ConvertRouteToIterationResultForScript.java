package ru.it.lecm.workflow.routes.entity;

import org.alfresco.repo.jscript.ScriptNode;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author vlevin
 */
public class ConvertRouteToIterationResultForScript {

	private ScriptNode iterationNode;
	private Scriptable stageItems;
	private Scriptable scriptErrors;

	public ScriptNode getIterationNode() {
		return iterationNode;
	}

	public void setIterationNode(ScriptNode iterationNode) {
		this.iterationNode = iterationNode;
	}

	public Scriptable getStageItems() {
		return stageItems;
	}

	public void setStageItems(Scriptable stageItems) {
		this.stageItems = stageItems;
	}

	public Scriptable getScriptErrors() {
		return scriptErrors;
	}

	public void setScriptErrors(Scriptable scriptErrors) {
		this.scriptErrors = scriptErrors;
	}
}
