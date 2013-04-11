package ru.it.lecm.statemachine;

import org.activiti.engine.runtime.Execution;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: PKotelnikova
 * Date: 04.04.13
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class TimerActionHelper implements InitializingBean {
    private static final String TIMER_FOLDER_NAME = "Таймеры машины состояний";

    private static NodeService nodeService;
    private static TransactionService transactionService;
    private static RepositoryStructureHelper repositoryStructureHelper;
    private static IWorkCalendar workCalendarService;

    public void setNodeService(NodeService nodeService) {
        TimerActionHelper.nodeService = nodeService;
    }

    public void setTransactionService(TransactionService transactionService) {
        TimerActionHelper.transactionService = transactionService;
    }

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        TimerActionHelper.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setWorkCalendarService(IWorkCalendar workCalendarService) {
        TimerActionHelper.workCalendarService = workCalendarService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createTimerFolderRef();

        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                restoreTimers();
                return null;
            }
        });
    }

    public void addTimer(String stateMachineExecutionId, String stateMachineTaskId, String variable, int duration) {
        Date finishDate = workCalendarService.getNextWorkingDate(new Date(), duration);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(finishDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        long finishTimestamp = calendar.getTime().getTime();

        //TODO:stub for testing!!!
        finishTimestamp = new Date().getTime() + duration * 60000;

        startTimer(stateMachineExecutionId, stateMachineTaskId, variable, finishTimestamp);
        addTimerNode(stateMachineExecutionId, stateMachineTaskId, variable, finishTimestamp);
    }

    public void removeTimer(String stateMachineTaskId) {
        String taskId = clearPrefix(stateMachineTaskId);
        final NodeRef timerFolderRef = getTimerFolderRef();

        List<NodeRef> timers = getTimers();
        for (final NodeRef timer : timers) {
            String timerTaskId = (String) nodeService.getProperty(timer, StatemachineModel.PROP_TASK_ID);

            if (clearPrefix(timerTaskId).equals(taskId)) {
                transactionService.getRetryingTransactionHelper().doInTransaction(
                        new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                            @Override
                            public Object execute() throws Throwable {
                                nodeService.removeChild(timerFolderRef, timer);
                                return null;
                            }
                        }, false, true);
            }
        }
    }

    private void startTimer(final String stateMachineExecutionId, final String stateMachineTaskId, final String variable, long finishTimestamp) {
        if (finishTimestamp < new Date().getTime()) {
            return;
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                    @Override
                    public Object doWork() throws Exception {
                        nextTransition(stateMachineExecutionId, stateMachineTaskId, variable, true);
                        return null;
                    }
                });
            }
        };

        new Timer().schedule(timerTask, new Date(finishTimestamp));
    }

    private void addTimerNode(String stateMachineExecutionId, String stateMachineTaskId, String variable, long finishTimestamp) {
        final NodeRef timerFolderRef = getTimerFolderRef();

        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(StatemachineModel.PROP_EXECUTION_ID, stateMachineExecutionId);
        properties.put(StatemachineModel.PROP_TASK_ID, stateMachineTaskId);
        properties.put(StatemachineModel.PROP_VARIABLE, variable);
        properties.put(StatemachineModel.PROP_FINISH_TIMESTAMP, finishTimestamp);

        transactionService.getRetryingTransactionHelper().doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                    @Override
                    public Object execute() throws Throwable {
                        nodeService.createNode(timerFolderRef, ContentModel.ASSOC_CONTAINS,
                                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(GUID.generate())), StatemachineModel.TYPE_TIMER, properties);
                        return null;
                    }
                }, false, true);
    }

    private void nextTransition(String executionId, String taskId, String variable, boolean isVariableSet) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(variable, isVariableSet);

        StateMachineHelper helper = new StateMachineHelper();
        helper.setExecutionParameters(executionId, parameters);
        helper.nextTransition(providePrefix(taskId));
    }

    private void createTimerFolderRef() {
        NodeRef timerFolderRef = getTimerFolderRef();
        if (timerFolderRef != null) {
            return;
        }

        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, TIMER_FOLDER_NAME);

        transactionService.getRetryingTransactionHelper().doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                    @Override
                    public Object execute() throws Throwable {
                        nodeService.createNode(repositoryStructureHelper.getHomeRef(), ContentModel.ASSOC_CONTAINS,
                                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(TIMER_FOLDER_NAME)), ContentModel.TYPE_FOLDER, properties);
                        return null;
                    }
                }, false, true);
    }

    private void restoreTimers() {
        List<NodeRef> timers = getTimers();
        for (NodeRef timer : timers) {
            final String timerExecutionId = (String) nodeService.getProperty(timer, StatemachineModel.PROP_EXECUTION_ID);
            final String timerTaskId = (String) nodeService.getProperty(timer, StatemachineModel.PROP_TASK_ID);
            Execution execution = new StateMachineHelper().getExecution(timerExecutionId);
            if (execution == null) {
                removeTimer(timerTaskId);
                continue;
            }

            String currentTaskId = new StateMachineHelper().getCurrentTaskId(timerExecutionId);
            if (!clearPrefix(currentTaskId).equals(clearPrefix(timerTaskId))) {
                removeTimer(timerTaskId);
                continue;
            }

            final String variable = (String) nodeService.getProperty(timer, StatemachineModel.PROP_VARIABLE);
            long finishTimestamp = (Long) nodeService.getProperty(timer, StatemachineModel.PROP_FINISH_TIMESTAMP);
            if (finishTimestamp < new Date().getTime()) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                            @Override
                            public Object doWork() throws Exception {
                                nextTransition(timerExecutionId, timerTaskId, variable, true);
                                return null;
                            }
                        });
                    }
                };

                new Timer().schedule(timerTask, 300000);
                continue;
            }

            startTimer(timerExecutionId, timerTaskId, variable, finishTimestamp);
        }
    }

    private List<NodeRef> getTimers() {
        List<NodeRef> result = new ArrayList<NodeRef>();

        NodeRef timerFolderRef = getTimerFolderRef();
        List<ChildAssociationRef> timerAssocs = nodeService.getChildAssocs(timerFolderRef);
        for (ChildAssociationRef timerAssoc : timerAssocs) {
            result.add(timerAssoc.getChildRef());
        }

        return result;
    }

    //Activiti works WITHOUT prefix
    //Alfresco works WITH prefix (WorkflowService)
    private String clearPrefix(String activityId) {
        if (activityId == null) {
            return null;
        }
        return activityId.replace(StateMachineHelper.ACTIVITI_PREFIX, "");
    }

    private String providePrefix(String activityId) {
        return StateMachineHelper.ACTIVITI_PREFIX + clearPrefix(activityId);
    }

    private NodeRef getTimerFolderRef() {
        return nodeService.getChildByName(repositoryStructureHelper.getHomeRef(), ContentModel.ASSOC_CONTAINS, TIMER_FOLDER_NAME);
    }
}
