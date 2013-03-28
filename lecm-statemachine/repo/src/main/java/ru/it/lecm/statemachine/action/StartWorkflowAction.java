package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.WorkflowDescriptor;
import ru.it.lecm.statemachine.action.util.DocumentWorkflowUtil;
import ru.it.lecm.statemachine.bean.StateMachineActions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: PMelnikov
 * Date: 23.10.12
 * Time: 8:47
 */
public class StartWorkflowAction extends StateMachineAction {

	private String id = "";
	private String workflowId = "";
	private String assignee = "";
	private WorkflowVariables variables = null;

	private static final String PROP_ID = "id";
	private static final String PROP_WORKFLOW_ID = "workflowId";
	private static final String PROP_ASSIGNEE = "assignee";

	@Override
	public void init(Element action, String processId) {
		List<Element> attributes = action.elements("attribute");
		for (Element attribute : attributes) {
			if (PROP_WORKFLOW_ID.equalsIgnoreCase(attribute.attribute("name"))) {
				workflowId = attribute.attribute("value");
			} else if (PROP_ASSIGNEE.equalsIgnoreCase(attribute.attribute("name"))) {
				assignee = attribute.attribute("value");
			} else if (PROP_ID.equalsIgnoreCase(attribute.attribute("name"))) {
				id = attribute.attribute("value");
			}
		}
		variables = new WorkflowVariables(action.element("workflowVariables"));
	}

	public String getId() {
		return id;
	}

	public WorkflowVariables getVariables() {
		return variables;
	}

	@Override
	public void execute(DelegateExecution execution) {
		final String stateMachineExecutionId = execution.getId();
		NodeRef nodeRef = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		final NodeRef document = getServiceRegistry().getNodeService().getChildAssocs(nodeRef).get(0).getChildRef();
		final String actionName = StateMachineActions.getActionName(StartWorkflowAction.class);
		final String eventName = execution.getEventName();


		StateMachineHelper helper = new StateMachineHelper();
		final String taskId = helper.getCurrentTaskId(stateMachineExecutionId);
		Timer timer = new Timer();
		final String user = AuthenticationUtil.getFullyAuthenticatedUser();
		final WorkflowVariables localVariables = variables;
		final String id = this.id;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

					@Override
					public Object doWork() throws Exception {
						StateMachineHelper helper = new StateMachineHelper();
						String currentTaskId = taskId;
						if (currentTaskId == null) {
							currentTaskId = helper.getCurrentTaskId(stateMachineExecutionId);
						}
						String executionId = helper.startUserWorkflowProcessing(currentTaskId.replace(StateMachineHelper.ACTIVITI_PREFIX, ""), workflowId, assignee);
						helper.setInputVariables(stateMachineExecutionId, executionId, localVariables.getInput());
						//Обозначить запуск процесса в документе
						WorkflowDescriptor descriptor = new WorkflowDescriptor(stateMachineExecutionId, taskId, actionName, id, eventName);
						new DocumentWorkflowUtil().addWorkflow(document, executionId, descriptor);

                        helper.logStartWorkflowEvent(document, StateMachineHelper.ACTIVITI_PREFIX + stateMachineExecutionId);

						return null;
					}
				}, user);
			}
		};
		timer.schedule(task, 1000);
	}

}
