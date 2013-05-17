package ru.it.lecm.statemachine.action.util;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import ru.it.lecm.statemachine.WorkflowDescriptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
			descriptorJSON.put("workflowId", descriptor.getWorkflowId());
			descriptorJSON.put("startTaskId", descriptor.getStartTaskId());
			descriptorJSON.put("actionName", descriptor.getActionName());
			descriptorJSON.put("actionId", descriptor.getActionId());
			descriptorJSON.put("eventName", descriptor.getEventName());
			workflows.put(executionId, descriptorJSON);
			final String result = workflows.toString();
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                @Override
                public Object doWork() throws Exception {
                    serviceRegistry.getNodeService().setProperty(document, PROP_WORKFLOWS, result);
                    return null;
                }
            });

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
                    executionId,
					workflow.getString("statemachineExecutionId"),
					workflow.getString("workflowId"),
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

	public List<WorkflowDescriptor> getWorkflowDescriptors(NodeRef document) {
        List<WorkflowDescriptor> result = new ArrayList<WorkflowDescriptor>();

        try {
            Object value = serviceRegistry.getNodeService().getProperty(document, PROP_WORKFLOWS);
            if (value == null) {
                return new ArrayList<WorkflowDescriptor>();
            }

            JSONObject workflows = new JSONObject((String) value);
            Iterator keys = workflows.keys();
            while (keys.hasNext()) {
                String executionId = (String)keys.next();
                WorkflowDescriptor workflowDescriptor = getWorkflowDescriptor(document, executionId);
                result.add(workflowDescriptor);
            }
        } catch (JSONException e) {
        }

        return result;
    }

	public void removeWorkflow(NodeRef document, String executionId) {
        try {
            Object value = serviceRegistry.getNodeService().getProperty(document, PROP_WORKFLOWS);
            if (value == null) {
                return;
            }

            JSONObject workflows = new JSONObject((String) value);
            workflows.remove(executionId);
            String result = workflows.toString();
            serviceRegistry.getNodeService().setProperty(document, PROP_WORKFLOWS, result);
        } catch (JSONException e) {
        }

	}

}
