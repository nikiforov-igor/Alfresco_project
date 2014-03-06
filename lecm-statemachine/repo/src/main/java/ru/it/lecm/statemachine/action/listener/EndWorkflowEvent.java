package ru.it.lecm.statemachine.action.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.WorkflowDescriptor;
import ru.it.lecm.statemachine.action.*;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.util.DocumentWorkflowUtil;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;

import java.util.HashMap;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:57
 * <p/>
 * Класс-слушатель окончания процесса.
 * По завершению передает сигнал об окончании пользовательского процесса
 * для дальнейшего оповещения машины состояний.
 */
public class EndWorkflowEvent implements ExecutionListener {

    private static DocumentService documentService;

    @Override
    public void notify(final DelegateExecution delegateExecution) throws Exception {
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
                if (AuthenticationUtil.getFullyAuthenticatedUser() == null) {
                    return null;
                }
                StateMachineHelper helper = new StateMachineHelper();
                String executionId = StateMachineHelper.ACTIVITI_PREFIX + delegateExecution.getId();

                NodeRef document = helper.getStatemachineDocument(executionId);
                if (document == null) {
                    return null;
                }

                DocumentWorkflowUtil utils = new DocumentWorkflowUtil();
                WorkflowDescriptor descriptor = utils.getWorkflowDescriptor(document, executionId);

                if (descriptor != null) {
                    helper.logEndWorkflowEvent(document, executionId);

                    String actionName = descriptor.getActionName();
                    String actionId = descriptor.getActionId();
                    String statemachineId = descriptor.getStatemachineExecutionId();

                    List<StateMachineAction> actions = helper.getTaskActionsByName(descriptor.getStartTaskId(), descriptor.getActionName(), descriptor.getEventName());
                    if (actions.size() == 0) {
                        actions = helper.getHistoricalTaskActionsByName(descriptor.getStartTaskId(), descriptor.getActionName(), descriptor.getEventName());
                    }

                    List<WorkflowVariables.WorkflowVariable> variables = null;
                    if (actionName.equals(StateMachineActionsImpl.getActionNameByClass(FinishStateWithTransitionAction.class))) {
                        for (StateMachineAction action : actions) {
                            FinishStateWithTransitionAction finishStateWithTransitionAction = (FinishStateWithTransitionAction) action;
                            for (FinishStateWithTransitionAction.NextState state : finishStateWithTransitionAction.getStates()) {
                                if (state.getActionId().equalsIgnoreCase(actionId) && state.getVariables() != null) {
                                    variables = state.getVariables().getOutput();
                                }
                            }
                        }
                    } else if (actionName.equals(StateMachineActionsImpl.getActionNameByClass(UserWorkflow.class))) {
                        for (StateMachineAction action : actions) {
                            UserWorkflow userWorkflow = (UserWorkflow) action;
                            for (UserWorkflow.UserWorkflowEntity entity : userWorkflow.getUserWorkflows()) {
                                if (entity.getId().equalsIgnoreCase(actionId) && entity.getVariables() != null) {
                                    variables = entity.getVariables().getOutput();
                                }
                            }
                        }
                    } else if (actionName.equals(StateMachineActionsImpl.getActionNameByClass(StartWorkflowAction.class))) {
                        for (StateMachineAction action : actions) {
                            StartWorkflowAction startWorkflowAction = (StartWorkflowAction) action;
                            if (startWorkflowAction.getId().equalsIgnoreCase(actionId) && startWorkflowAction.getVariables() != null) {
                                variables = startWorkflowAction.getVariables().getOutput();
                            }
                        }
                    }

                    if (variables != null) {
                        helper.getOutputVariables(statemachineId, delegateExecution.getVariables(), variables);
                    }

                    String taskId = helper.getCurrentTaskId(statemachineId);
                    List<StateMachineAction> transitionActions = helper.getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(TransitionAction.class), ExecutionListener.EVENTNAME_END);
                    boolean isTrasitionValid = false;
                    boolean stopSubWorkflows = false;
                    for (StateMachineAction action : transitionActions) {
                        TransitionAction transitionAction = (TransitionAction) action;
                        boolean currentTransitionValid = documentService.execExpression(document, transitionAction.getExpression());
                        HashMap<String, Object> parameters = new HashMap<String, Object>();
                        parameters.put(transitionAction.getVariableName(), currentTransitionValid);
                        helper.setExecutionParamentersByTaskId(taskId, parameters);

                        if (currentTransitionValid) {
                            stopSubWorkflows = stopSubWorkflows || transitionAction.isStopSubWorkflows();
                        }
                        isTrasitionValid = isTrasitionValid || currentTransitionValid;
                    }

                    if (isTrasitionValid) {
                        if (stopSubWorkflows) {
                            helper.stopDocumentSubWorkflows(statemachineId, executionId);
                        }

                        helper.nextTransition(taskId);
                    }
	                utils.removeWorkflow(document, executionId);
                }
                return null;
            }
        });



    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
