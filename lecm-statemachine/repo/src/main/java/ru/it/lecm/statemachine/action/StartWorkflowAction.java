package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
//import org.activiti.engine.impl.util.xml.Element;
import org.activiti.bpmn.model.BaseElement;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.WorkflowDescriptor;
import ru.it.lecm.statemachine.util.DocumentWorkflowUtil;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: PMelnikov
 * Date: 23.10.12
 * Time: 8:47
 */
public class StartWorkflowAction extends StateMachineAction implements PostponedAction {

	private String id = "";
	private String workflowId = "";
	private String assignee = "";
	private WorkflowVariables variables = null;

	private static final String PROP_ID = "id";
	private static final String PROP_WORKFLOW_ID = "workflowId";
	private static final String PROP_ASSIGNEE = "assignee";
	
	private final static Logger logger = LoggerFactory.getLogger(StartWorkflowAction.class);

	@Override
	public void init(BaseElement actionElement, String processId) {
//		List<Element> attributes = action.elements("attribute");
//		for (Element attribute : attributes) {
//			if (PROP_WORKFLOW_ID.equalsIgnoreCase(attribute.attribute("name"))) {
//				workflowId = attribute.attribute("value");
//			} else if (PROP_ASSIGNEE.equalsIgnoreCase(attribute.attribute("name"))) {
//				assignee = attribute.attribute("value");
//			} else if (PROP_ID.equalsIgnoreCase(attribute.attribute("name"))) {
//				id = attribute.attribute("value");
//			}
//		}
//		variables = new WorkflowVariables(action.element("workflowVariables"));
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
		final String actionName = StateMachineActionsImpl.getActionNameByClass(StartWorkflowAction.class);
		final String eventName = execution.getEventName();

        if (eventName.equals(ExecutionListener.EVENTNAME_END)) {
            final String user = AuthenticationUtil.getFullyAuthenticatedUser();
            final WorkflowVariables localVariables = variables;
            final String id = this.id;
                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

                    @Override
                    public Object doWork() throws Exception {
                        String currentTaskId = getStateMachineHelper().getCurrentTaskId(stateMachineExecutionId);
                        String executionId = getStateMachineHelper().startUserWorkflowProcessing(currentTaskId.replace(LifecycleStateMachineHelper.ACTIVITI_PREFIX, ""), workflowId, assignee);
                        getStateMachineHelper().setInputVariables(stateMachineExecutionId, executionId, localVariables.getInput());
                        //Обозначить запуск процесса в документе
                        WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, stateMachineExecutionId, workflowId, currentTaskId, actionName, id, eventName);
                        new DocumentWorkflowUtil().addWorkflow(document, executionId, descriptor);

                        getStateMachineHelper().logStartWorkflowEvent(document, executionId);

                        return null;
                    }
                }, user);
        }
	}

    @Override
    public void postponedExecution(String taskId, final LifecycleStateMachineHelper helper) {
//        final String stateMachineExecutionId = helper.getCurrentExecutionId(taskId);
//        final NodeRef document = helper.get StatemachineDocument(stateMachineExecutionId);
//        final String actionName = StateMachineActionsImpl.getActionNameByClass(StartWorkflowAction.class);
//
//        final String user = AuthenticationUtil.getFullyAuthenticatedUser();
//        final WorkflowVariables localVariables = variables;
//        final String id = this.id;
//        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
//
//            @Override
//            public Object doWork() throws Exception {
//                String currentTaskId = helper.getCurrentTaskId(stateMachineExecutionId);
//                String executionId = helper.startUserWorkflowProcessing(currentTaskId.replace(LifecycleStateMachineHelper.ACTIVITI_PREFIX, ""), workflowId, assignee);
//                getStateMachineHelper().setInputVariables(stateMachineExecutionId, executionId, localVariables.getInput());
//                //Обозначить запуск процесса в документе
//                WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, stateMachineExecutionId, workflowId, currentTaskId, actionName, id, ExecutionListener.EVENTNAME_START);
//                new DocumentWorkflowUtil().addWorkflow(document, executionId, descriptor);
//
//                helper.logStartWorkflowEvent(document, executionId);
//
//                return null;
//            }
//        }, user);
    }

}
