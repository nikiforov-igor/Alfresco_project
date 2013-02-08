package ru.it.lecm.statemachine.action.util;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import ru.it.lecm.statemachine.WorkflowDescriptor;

/**
 * User: PMelnikov
 * Date: 04.02.13
 * Time: 13:34
 */
public class DocumentWorkflowUtil {

	private static ServiceRegistry serviceRegistry;

	private static final QName PROP_WORKFLOWS = QName.createQName("http://www.it.ru/logicECM/document/1.0", "sys_workflows");

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		DocumentWorkflowUtil.serviceRegistry = serviceRegistry;
	}

	public synchronized void addWorkflow(final NodeRef document, String executionId, WorkflowDescriptor descriptor) {
		try {
			Object value = serviceRegistry.getNodeService().getProperty(document, PROP_WORKFLOWS);
			JSONObject workflows;
			if (value == null) {
				workflows = new JSONObject();
			} else {
				workflows = new JSONObject((String) value);
			}
			JSONObject descriptorJSON = new JSONObject();
			descriptorJSON.put("statemachineExecutionId", descriptor.getStatemachineExecutionId());
			descriptorJSON.put("startTaskId", descriptor.getStartTaskId());
			descriptorJSON.put("actionName", descriptor.getActionName());
			descriptorJSON.put("actionId", descriptor.getActionId());
			descriptorJSON.put("eventName", descriptor.getEventName());
			workflows.put(executionId, descriptorJSON);
			String result = workflows.toString();
			serviceRegistry.getNodeService().setProperty(document, PROP_WORKFLOWS, result);
		} catch (JSONException e) {
			throw new IllegalStateException(e);
		}
	}

	public WorkflowDescriptor getWorkflowDescriptor(NodeRef document, String executionId) {
		try {
			Object value = serviceRegistry.getNodeService().getProperty(document, PROP_WORKFLOWS);
			if (value == null) return null;

			JSONObject workflows = new JSONObject((String) value);
			JSONObject workflow = (JSONObject) workflows.get(executionId);
			WorkflowDescriptor descriptor = new WorkflowDescriptor(
					workflow.getString("statemachineExecutionId"),
					workflow.getString("startTaskId"),
					workflow.getString("actionName"),
					workflow.getString("actionId"),
					workflow.getString("eventName")
			);
			return descriptor;
		} catch (JSONException e) {
			//throw new IllegalStateException(e);
		}
		return null;
	}

	public void removeWorkflow(String executionId) {

	}

}
