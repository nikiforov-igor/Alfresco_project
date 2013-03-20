package ru.it.lecm.statemachine.action.script;

import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentFrequencyAnalysisService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.WorkflowDescriptor;
import ru.it.lecm.statemachine.action.Conditions;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.action.util.DocumentWorkflowUtil;
import ru.it.lecm.statemachine.bean.StateMachineActions;
import ru.it.lecm.statemachine.expression.Expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:18
 */
public class StartWorkflowScript extends DeclarativeWebScript {

	private static ServiceRegistry serviceRegistry;
    private static DocumentFrequencyAnalysisService frequencyAnalysisService;
    private static OrgstructureBean orgstructureService;
    private static DocumentMembersService documentMembersService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        StartWorkflowScript.orgstructureService = orgstructureService;
    }

    public void setFrequencyAnalysisService(DocumentFrequencyAnalysisService frequencyAnalysisService) {
        StartWorkflowScript.frequencyAnalysisService = frequencyAnalysisService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StartWorkflowScript.serviceRegistry = serviceRegistry;
	}

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        StartWorkflowScript.documentMembersService = documentMembersService;
    }

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String taskId = req.getParameter("taskId");
		String persistedResponse = req.getParameter("formResponse");
		String actionId = req.getParameter("actionId");
		String actionType = req.getParameter("actionType");

		//Если есть actionId обрабатываем transitionAction
		if ("trans".equals(actionType)) {
			StateMachineHelper helper = new StateMachineHelper();
			List<StateMachineAction> actions = helper.getTaskActionsByName(taskId, StateMachineActions.getActionName(FinishStateWithTransitionAction.class), ExecutionListener.EVENTNAME_TAKE);
			FinishStateWithTransitionAction.NextState nextState = null;
			for (StateMachineAction action : actions) {
				FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
				List<FinishStateWithTransitionAction.NextState> states = finishStateWithTransitionAction.getStates();
				for (FinishStateWithTransitionAction.NextState state : states) {
					if (state.getActionId().equalsIgnoreCase(actionId)) {
						nextState = state;
					}
				}
			}


			if (nextState != null) {
				String executionId = helper.getCurrentExecutionId(taskId);
				NodeRef document = helper.getStatemachineDocument(executionId);
				Expression expression = new Expression(document, serviceRegistry);

				boolean access = true;
                Conditions conditions = nextState.getConditionAccess();
                for (Conditions.Condition condition : conditions.getConditions()) {
                    access = access && expression.execute(condition.getExpression());
                }


				if (access) {
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put(nextState.getOutputVariableName(), nextState.getOutputVariableValue());
					helper.setExecutionParamentersByTaskId(taskId, parameters);
					helper.nextTransition(taskId);

					if (!"null".equals(persistedResponse)) {
						int start = persistedResponse.indexOf("=") + 1;
						int end = persistedResponse.indexOf(",");

						String dependencyExecution = persistedResponse.substring(start, end);

						WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, taskId, StateMachineActions.getActionName(FinishStateWithTransitionAction.class), actionId, ExecutionListener.EVENTNAME_TAKE);
						new DocumentWorkflowUtil().addWorkflow(document, dependencyExecution, descriptor);
						helper.setInputVariables(executionId, dependencyExecution, nextState.getVariables().getInput());

                        //Добавляем участников к документу.
                        List<NodeRef> assignees = helper.getAssigneesForWorkflow(dependencyExecution);
                        for (NodeRef assignee : assignees) {
                            documentMembersService.addMember(document, assignee, null);
                        }

                    }

                    //Обновляем счетчик действий для пользователя
                    updateActionCount(document, actionId);
				}
			}
		} else if ("user".equals(actionType)){
			StateMachineHelper helper = new StateMachineHelper();
			List<StateMachineAction> actions = helper.getTaskActionsByName(taskId, StateMachineActions.getActionName(UserWorkflow.class), ExecutionListener.EVENTNAME_TAKE);
			UserWorkflow.UserWorkflowEntity workflow = null;
			for (StateMachineAction action : actions) {
				UserWorkflow userWorkflow = (UserWorkflow) action;
				List<UserWorkflow.UserWorkflowEntity> workflows = userWorkflow.getUserWorkflows();
				for (UserWorkflow.UserWorkflowEntity workflowEntity : workflows) {
					if (workflowEntity.getId().equalsIgnoreCase(actionId)) {
						workflow = workflowEntity;
					}
				}
			}
			if (workflow != null && !"null".equals(persistedResponse)) {
				String executionId = helper.getCurrentExecutionId(taskId);
				NodeRef document = helper.getStatemachineDocument(executionId);
				Expression expression = new Expression(document, serviceRegistry);

                boolean access =true;
                Conditions conditions = workflow.getConditionAccess();
                for (Conditions.Condition condition : conditions.getConditions()) {
                    access = access && expression.execute(condition.getExpression());
                }
				if (access) {
					int start = persistedResponse.indexOf("=") + 1;
					int end = persistedResponse.indexOf(",");

					String dependencyExecution = persistedResponse.substring(start, end);

					WorkflowDescriptor descriptor = new WorkflowDescriptor(executionId, taskId, StateMachineActions.getActionName(UserWorkflow.class), actionId, ExecutionListener.EVENTNAME_TAKE);
					new DocumentWorkflowUtil().addWorkflow(document, dependencyExecution, descriptor);

					helper.setInputVariables(executionId, dependencyExecution, workflow.getVariables().getInput());

                    //Обновляем счетчик действий для пользователя
                    updateActionCount(document, actionId);
                    
                    //Добавляем участников к документу.
                    List<NodeRef> assignees = helper.getAssigneesForWorkflow(dependencyExecution);
                    for (NodeRef assignee : assignees) {
                        documentMembersService.addMember(document, assignee, null);
                    }
				}
			}
		}

		return super.executeImpl(req, status, cache);    //To change body of overridden methods use File | Settings | File Templates.
	}

    private void updateActionCount(NodeRef document, String actionId) {
        NodeService nodeService = serviceRegistry.getNodeService();
        QName type = nodeService.getType(document);
        String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());

        NodeRef employee = orgstructureService.getCurrentEmployee();
        if (employee != null) {
            frequencyAnalysisService.updateFrequencyCount(employee, shortTypeName, actionId);
        }
    }

}
