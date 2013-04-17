package ru.it.lecm.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.repo.workflow.activiti.listener.ScriptExecutionListener;
import org.alfresco.service.ServiceRegistry;
import ru.it.lecm.documents.beans.DocumentConnectionService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 16.04.13
 * Time: 15:32
 */
public class LecmScriptExecutionListener extends ScriptExecutionListener {

	private static HashMap<String, Object> objects = new HashMap<String, Object>();
	private String name;
	private Object object;

	public void setName(String name) {
		this.name = name;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	protected Map<String, Object> getInputMap(DelegateExecution delegateExecution, String runAsUser) {
		Map<String, Object> scriptModel = super.getInputMap(delegateExecution, runAsUser);
		scriptModel.putAll(objects);
		scriptModel.put("artifact", new Artifact());
		return scriptModel;
	}

	public void register() {
		if (name != null && object != null) {
			objects.put(name, object);
		}
	}

}
