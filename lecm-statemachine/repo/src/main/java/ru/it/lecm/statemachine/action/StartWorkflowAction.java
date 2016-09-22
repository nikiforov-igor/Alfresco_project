package ru.it.lecm.statemachine.action;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.Expression;
//import org.activiti.engine.impl.util.xml.Element;
import org.activiti.bpmn.model.BaseElement;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
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
public class StartWorkflowAction extends StateMachineAction implements PostponedAction, TaskListener {

	private String id = "";
	//private String workflowId = "";
	//private String assignee = "";
	protected Expression workflowId;
	protected Expression assignee;
	private WorkflowVariables variables = null;
	private static final String STM_POST_TRANSACTION_START_WORKFLOW = "stm_post_transaction_start_worwflow";

	private static final String PROP_ID = "id";
	private static final String PROP_WORKFLOW_ID = "workflowId";
	private static final String PROP_ASSIGNEE = "assignee";
	
	public void setWorkflowId(Expression workflowId) {
        this.workflowId = workflowId;
    }

    public void setAssignee(Expression assignee) {
        this.assignee = assignee;
    }
	
	private final static Logger logger = LoggerFactory.getLogger(StartWorkflowAction.class);

	@Override
    public void notify(DelegateTask delegateTask) {		
		final NodeRef document = ((ActivitiScriptNode) delegateTask.getExecution().getVariable("stm_document")).getNodeRef();
		final NodeRef bpm_package = ((ActivitiScriptNode) delegateTask.getExecution().getVariable("bpm_package")).getNodeRef();
		final String user = AuthenticationUtil.getFullyAuthenticatedUser();
		
		Map<String, Object> startWorkflow = AlfrescoTransactionSupport.getResource(STM_POST_TRANSACTION_START_WORKFLOW);
        if (startWorkflow == null) {
        	startWorkflow = new HashMap<String, Object>();
            AlfrescoTransactionSupport.bindResource(STM_POST_TRANSACTION_START_WORKFLOW, startWorkflow);
        }
        startWorkflow.put("stateMachineExecutionId", delegateTask.getExecution().getId());
        startWorkflow.put("workflowId", this.workflowId.getExpressionText());
        startWorkflow.put("assignee", this.assignee.getExpressionText());
        // currentTaskId - Используется в EndWorkflowEvent для поиска знастроек машины состояний по задаче статуса
        startWorkflow.put("currentTaskId", delegateTask.getExecution().getId());
        //actionName -  Имя действия по классу для отработки условия в EndWorkflowEvent
        startWorkflow.put("actionName", StateMachineActionsImpl.getActionNameByClass(StartWorkflowAction.class));
        startWorkflow.put("id", this.id);
        startWorkflow.put("document", document);
        startWorkflow.put("bpm_package", bpm_package);
        startWorkflow.put("user", user);
        
        TransactionListener transactionListener = new StateMachineTransactionListener();
        AlfrescoTransactionSupport.bindListener(transactionListener);
	}
	
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
//		final String stateMachineExecutionId = execution.getId();
//		NodeRef nodeRef = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
//		final NodeRef document = getServiceRegistry().getNodeService().getChildAssocs(nodeRef).get(0).getChildRef();
//		final String actionName = StateMachineActionsImpl.getActionNameByClass(StartWorkflowAction.class);
//		final String eventName = execution.getEventName();
//
//        if (eventName.equals(ExecutionListener.EVENTNAME_END)) {
//            final String user = AuthenticationUtil.getFullyAuthenticatedUser();
//            final WorkflowVariables localVariables = variables;
//            final String id = this.id;
//                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
//
//                    @Override
//                    public Object doWork() throws Exception {
//                        String currentTaskId = getStateMachineHelper().getCurrentTaskId(stateMachineExecutionId);
//                        String executionId = getStateMachineHelper().startUserWorkflowProcessing(currentTaskId.replace(LifecycleStateMachineHelper.ACTIVITI_PREFIX, ""), workflowId, assignee);
//                        getStateMachineHelper().setInputVariables(stateMachineExecutionId, executionId, localVariables.getInput());
//                        //Обозначить запуск процесса в документе
//                        WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, stateMachineExecutionId, workflowId, currentTaskId, actionName, id, eventName);
//                        new DocumentWorkflowUtil().addWorkflow(document, executionId, descriptor);
//
//                        getStateMachineHelper().logStartWorkflowEvent(document, executionId);
//
//                        return null;
//                    }
//                }, user);
//        }
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
    
    private class StateMachineTransactionListener implements TransactionListener
    {

        @Override
        public void flush() {

        }

        @Override
        public void beforeCommit(boolean readOnly) {
        	Map<String, Object> startWorkflows = AlfrescoTransactionSupport.getResource(STM_POST_TRANSACTION_START_WORKFLOW);
        	if (startWorkflows != null) {
	          	final String stateMachineExecutionId = (String)startWorkflows.get("stateMachineExecutionId");
	          	final String workflowId =  (String)startWorkflows.get("workflowId");
	          	final String assignee =  (String)startWorkflows.get("assignee");
	          	final String currentTaskId =  (String)startWorkflows.get("currentTaskId");
	          	final String actionName =  (String)startWorkflows.get("actionName");
	          	final String id =  (String)startWorkflows.get("id");
	          	final NodeRef document =  (NodeRef)startWorkflows.get("document");
	          	final NodeRef bpm_package =  (NodeRef)startWorkflows.get("bpm_package");
	          	final String user = (String)startWorkflows.get("user");
	          	logger.info("USER: "+user);
	          	AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
              	@Override
              		public Object doWork() throws Exception {
	              		String executionId = getStateMachineHelper().startUserWorkflowProcessing(bpm_package, workflowId, assignee);
	                      //!!!!!!getStateMachineHelper().setInputVariables(stateMachineExecutionId, executionId, localVariables.getInput());
	                      //Обозначить запуск процесса в документе
	              		WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, stateMachineExecutionId, workflowId, currentTaskId, actionName, id, "");
	              		logger.info("!!!!!!!!!!! StartWorkflowAction: "+descriptor);
	              		new DocumentWorkflowUtil().addWorkflow(document, executionId, descriptor);
	
	              		getStateMachineHelper().sendSignal(executionId);
	              		
	              		getStateMachineHelper().logStartWorkflowEvent(document, executionId);
	
	              		return null;
              		}
	          	}, user);
        	}
        }

        @Override
        public void beforeCompletion() {

        }

        @Override
        public void afterCommit() {
//            Map<String, Object> startWorkflows = AlfrescoTransactionSupport.getResource(STM_POST_TRANSACTION_START_WORKFLOW);
//            if (startWorkflows != null) {
//            	final String stateMachineExecutionId = (String)startWorkflows.get("stateMachineExecutionId");
//            	final String workflowId =  (String)startWorkflows.get("workflowId");
//            	final String assignee =  (String)startWorkflows.get("assignee");
//            	final String currentTaskId =  (String)startWorkflows.get("currentTaskId");
//            	final String actionName =  (String)startWorkflows.get("actionName");
//            	final String id =  (String)startWorkflows.get("id");
//            	final NodeRef document =  (NodeRef)startWorkflows.get("document");
//            	final NodeRef bpm_package =  (NodeRef)startWorkflows.get("bpm_package");
//                final String user = (String)startWorkflows.get("user")
//                logger.info("USER: "+user);
//                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
//                	@Override
//                	public Object doWork() throws Exception {
//                		String executionId = getStateMachineHelper().startUserWorkflowProcessing(bpm_package, workflowId, assignee);
//                        //!!!!!!getStateMachineHelper().setInputVariables(stateMachineExecutionId, executionId, localVariables.getInput());
//                        //Обозначить запуск процесса в документе
//                		WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, stateMachineExecutionId, workflowId, currentTaskId, actionName, id, "");
//                		new DocumentWorkflowUtil().addWorkflow(document, executionId, descriptor);
//
//                		getStateMachineHelper().logStartWorkflowEvent(document, executionId);
//
//                		return null;
//                	}
//                }, user);
//            }
        }

        @Override
        public void afterRollback() {

        }
    }
}
