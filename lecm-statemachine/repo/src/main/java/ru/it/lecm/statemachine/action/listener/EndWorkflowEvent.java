package ru.it.lecm.statemachine.action.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.WorkflowDescriptor;
import ru.it.lecm.statemachine.action.*;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;
import ru.it.lecm.statemachine.util.DocumentWorkflowUtil;

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
    private LifecycleStateMachineHelper stateMachineHelper;
    final static Logger logger = LoggerFactory.getLogger(EndWorkflowEvent.class);

    @Override
    public void notify(final DelegateExecution delegateExecution) throws Exception {
    	logger.info("!!!!!!!! notify");
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
                if (AuthenticationUtil.getFullyAuthenticatedUser() == null) {
                    return null;
                }
                String executionId = LifecycleStateMachineHelper.ACTIVITI_PREFIX + delegateExecution.getId();
                //TODO Здесь выполняется получение документа из переменных процесса по executionId
                NodeRef document = stateMachineHelper.getStatemachineDocument(executionId);
                if (document == null) {
                    return null;
                }

                DocumentWorkflowUtil utils = new DocumentWorkflowUtil();
                WorkflowDescriptor descriptor = utils.getWorkflowDescriptor(document, executionId);
                logger.info("!!!!!!!! descriptor: "+descriptor);
                if (descriptor != null) {
                    stateMachineHelper.logEndWorkflowEvent(document, executionId);

                    String actionName = descriptor.getActionName();
                    String actionId = descriptor.getActionId();
                    String statemachineId = descriptor.getStatemachineExecutionId();

                    //TODO Сразу передавать нужные параметры
                    List<StateMachineAction> actions = stateMachineHelper.getTaskActionsByName(descriptor.getStartTaskId(), descriptor.getActionName());
                    if (actions.size() == 0) {
                        actions = stateMachineHelper.getHistoricalTaskActionsByName(descriptor.getStartTaskId(), descriptor.getActionName());
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
                    logger.info("!!!!!!!! variables: "+variables);
                    if (variables != null) {
                        stateMachineHelper.getOutputVariables(statemachineId, delegateExecution.getVariables(), variables);
                    }

                    String taskId = stateMachineHelper.getCurrentTaskId(statemachineId);
                    logger.info("!!!!!!!! taskId: "+taskId);
                    //TODO Сразу передавать нужные параметры
                    List<StateMachineAction> transitionActions = stateMachineHelper.getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(TransitionAction.class));
                    boolean isTrasitionValid = false;
                    boolean stopSubWorkflows = false;
                    String messageName = "";
                    logger.info("!!!!!!!! transitionActions: "+transitionActions);
                    for (StateMachineAction action : transitionActions) {
                        TransitionAction transitionAction = (TransitionAction) action;
                        for(TransitionAction.TransitionActionEntity transition: transitionAction.getTransitions()) {
                        	logger.info("!!!!!!!! transition: "+transition);
	                        boolean currentTransitionValid = documentService.execExpression(document, transition.getExpression());
	                        logger.info("!!!!!!!! currentTransitionValid: "+currentTransitionValid);
//	                        HashMap<String, Object> parameters = new HashMap<String, Object>();
//	                        parameters.put(transition.getVariableName(), currentTransitionValid);
//	                        stateMachineHelper.setExecutionParamentersByTaskId(taskId, parameters);

	                        if (currentTransitionValid) {
	                            stopSubWorkflows = stopSubWorkflows || transition.isStopSubWorkflows();
	                            messageName = transition.getVariableName()+"_msg";
	                            logger.info("!!!!!!!! messageName: "+messageName);
	                        }
	                        isTrasitionValid = isTrasitionValid || currentTransitionValid;
                    	}
                    }

                    if (isTrasitionValid) {
                        if (stopSubWorkflows) {
                        	//TODO DONE первый параметр теперь нодреф документа а не id машины состояний
                            stateMachineHelper.stopDocumentSubWorkflows(document, executionId);
                        }
                        logger.info("!!!!!!!! sendMessage");
                        //stateMachineHelper.nextTransition(taskId);
                        stateMachineHelper.sendMessage(messageName, statemachineId.replace(LifecycleStateMachineHelper.ACTIVITI_PREFIX,""));
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

    public void setStateMachineHelper(LifecycleStateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }
}
