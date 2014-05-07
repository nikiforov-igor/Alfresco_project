package ru.it.lecm.statemachine;

import org.activiti.engine.runtime.Execution;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.expression.TransitionExpression;
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

    private static ServiceRegistry serviceRegistry;
    private static NodeService nodeService;
    private static TransactionService transactionService;
    private static RepositoryStructureHelper repositoryStructureHelper;
    private static IWorkCalendar workCalendarService;
    private static DocumentService documentService;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        TimerActionHelper.serviceRegistry = serviceRegistry;
    }

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

    public void addTimer(String stateMachineExecutionId, String timerDuration, String variable, List<TransitionExpression> expressions) {
        if (stateMachineExecutionId == null || timerDuration == null || variable == null || expressions.size() == 0) {
            return;
        }

        long finishTimestamp = calculateFinishTimestamp(timerDuration);
        String stateMachineTaskId = new StateMachineHelper().getCurrentTaskId(stateMachineExecutionId);

        startTimer(stateMachineExecutionId, stateMachineTaskId, finishTimestamp, variable, expressions);
        addTimerNode(stateMachineExecutionId, stateMachineTaskId, finishTimestamp, variable, expressions);

        setOutputVarable(stateMachineExecutionId, variable, "");
    }

    public void removeTimerNode(String stateMachineExecutionId) {
        String stateMachineTaskId = new StateMachineHelper().getCurrentTaskId(stateMachineExecutionId);
        stateMachineTaskId = clearPrefix(stateMachineTaskId);
        final NodeRef timerFolderRef = getTimerFolderRef();

        List<NodeRef> timers = getTimers();
        for (final NodeRef timer : timers) {
            String timerTaskId = (String) nodeService.getProperty(timer, StatemachineModel.PROP_TASK_ID);

            if (clearPrefix(timerTaskId).equals(stateMachineTaskId)) {
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

    private void startTimer(final String stateMachineExecutionId, final String stateMachineTaskId, long finishTimestamp, final String variable, final List<TransitionExpression> expressions) {
        if (finishTimestamp < new Date().getTime()) {
            return;
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                    @Override
                    public Object doWork() throws Exception {
                        nextTransition(stateMachineExecutionId, stateMachineTaskId, variable, expressions);
                        return null;
                    }
                });
            }
        };

        new Timer().schedule(timerTask, new Date(finishTimestamp));
    }

    private TransitionExpression getFittingExpression(String stateMachineExecutionId, List<TransitionExpression> expressions) {
        StateMachineHelper stateMachineHelper = new StateMachineHelper();

        NodeRef document = stateMachineHelper.getStatemachineDocument(stateMachineExecutionId);
        Map<String, Object> variables = stateMachineHelper.getVariables(stateMachineExecutionId);
        for (TransitionExpression expression : expressions) {
            if (documentService.execExpression(document, expression.getExpression())) {
                return expression;
            }
        }

        return null;
    }

    private void nextTransition(final String stateMachineExecutionId, String stateMachineTaskId, String variable, List<TransitionExpression> expressions) {
        final StateMachineHelper stateMachineHelper = new StateMachineHelper();

        Execution execution = stateMachineHelper.getExecution(stateMachineExecutionId);
        if (execution == null) {
            //execution is over
            removeTimerNode(stateMachineExecutionId);
            return;
        }

        String currentTaskId = stateMachineHelper.getCurrentTaskId(stateMachineExecutionId);
        if (!clearPrefix(currentTaskId).equals(clearPrefix(stateMachineTaskId))) {
            //task is over
            removeTimerNode(stateMachineExecutionId);
            return;
        }

        final TransitionExpression expression = getFittingExpression(stateMachineExecutionId, expressions);
        if (expression == null) {
            removeTimerNode(stateMachineExecutionId);
            return;
        }

        if (expression.isStopSubWorkflows()) {
            String statemachineId = stateMachineHelper.getCurrentExecutionId(stateMachineTaskId);
            stateMachineHelper.stopDocumentSubWorkflows(statemachineId);
        }

        setOutputVarable(stateMachineExecutionId, variable, expression.getOutputValue());

        if (!"".equals(expression.getScript())) {
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                @Override
                public Object doWork() throws Exception {
                    stateMachineHelper.executeScript(expression.getScript(), stateMachineExecutionId);
                    return null;
                }
            });
        }

        stateMachineHelper.nextTransition(providePrefix(stateMachineTaskId));
    }

    private void addTimerNode(String stateMachineExecutionId, String stateMachineTaskId, long finishTimestamp, String variable, List<TransitionExpression> expressions) {
        final NodeRef timerFolderRef = getTimerFolderRef();

        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(StatemachineModel.PROP_EXECUTION_ID, stateMachineExecutionId);
        properties.put(StatemachineModel.PROP_TASK_ID, stateMachineTaskId);
        properties.put(StatemachineModel.PROP_FINISH_TIMESTAMP, finishTimestamp);

        final ChildAssociationRef[] timer = new ChildAssociationRef[1];
        transactionService.getRetryingTransactionHelper().doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                    @Override
                    public Object execute() throws Throwable {
                        timer[0] = nodeService.createNode(timerFolderRef, ContentModel.ASSOC_CONTAINS,
                                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(GUID.generate())), StatemachineModel.TYPE_TIMER, properties);
                        return null;
                    }
                }, false, true);

        for (TransitionExpression expression : expressions) {
            properties.clear();
            properties.put(StatemachineModel.PROP_STOP_SUBWORKFLOWS, expression.isStopSubWorkflows());
            properties.put(StatemachineModel.PROP_EXPRESSION, expression.getExpression());
            properties.put(StatemachineModel.PROP_OUTPUT_VALUE, expression.getOutputValue());
            properties.put(StatemachineModel.PROP_SCRIPT, expression.getScript());

            transactionService.getRetryingTransactionHelper().doInTransaction(
                    new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                        @Override
                        public Object execute() throws Throwable {
                            nodeService.createNode(timer[0].getChildRef(), ContentModel.ASSOC_CONTAINS,
                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(GUID.generate())), StatemachineModel.TYPE_TRANSITION_EXPRESSION, properties);
                            return null;
                        }
                    }, false, true);
        }
    }

    private void restoreTimers() {
        List<NodeRef> timers = getTimers();
        for (NodeRef timer : timers) {
            final String timerExecutionId = (String) nodeService.getProperty(timer, StatemachineModel.PROP_EXECUTION_ID);
            final String timerTaskId = (String) nodeService.getProperty(timer, StatemachineModel.PROP_TASK_ID);
            long finishTimestamp = (Long) nodeService.getProperty(timer, StatemachineModel.PROP_FINISH_TIMESTAMP);
            final String variable = (String) nodeService.getProperty(timer, StatemachineModel.PROP_VARIABLE);

            final List<TransitionExpression> transitionExpressions = new ArrayList<TransitionExpression>();
            List<ChildAssociationRef> expressionAssocs = nodeService.getChildAssocs(timer);
            for (ChildAssociationRef expressionAssoc : expressionAssocs) {
                NodeRef expressionRef = expressionAssoc.getChildRef();
                String expression = (String) nodeService.getProperty(expressionRef, StatemachineModel.PROP_EXPRESSION);
                String outputValue = (String) nodeService.getProperty(expressionRef, StatemachineModel.PROP_OUTPUT_VALUE);
                boolean stopSubWorkflows = (Boolean) nodeService.getProperty(expressionRef, StatemachineModel.PROP_STOP_SUBWORKFLOWS);
                String script = (String) nodeService.getProperty(expressionRef, StatemachineModel.PROP_SCRIPT);
                TransitionExpression transitionExpression = new TransitionExpression(expression, outputValue, stopSubWorkflows, script);
                transitionExpressions.add(transitionExpression);
            }

            if (finishTimestamp < new Date().getTime()) {
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                    @Override
                    public Object doWork() throws Exception {
                        nextTransition(timerExecutionId, timerTaskId, variable, transitionExpressions);
                        return null;
                    }
                });
            } else {
                startTimer(timerExecutionId, timerTaskId, finishTimestamp, variable, transitionExpressions);
            }
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

    private NodeRef getTimerFolderRef() {
        return nodeService.getChildByName(repositoryStructureHelper.getHomeRef(), ContentModel.ASSOC_CONTAINS, TIMER_FOLDER_NAME);
    }

    private void setOutputVarable(String stateMachineExecutionId, String variable, String value) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(variable, value);
        new StateMachineHelper().setExecutionParameters(stateMachineExecutionId, parameters);
    }

    private long calculateFinishTimestamp(String timerDuration) {
        Date finishDate = workCalendarService.getNextWorkingDate(new Date(), timerDuration);

//        GregorianCalendar calendar = new GregorianCalendar();
//        calendar.setTime(finishDate);
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 59);
//        calendar.set(Calendar.SECOND, 59);
//
//        long finishTimestamp = calendar.getTimeInMillis();

		long finishTimestamp = finishDate.getTime();

        //TODO:stub for testing!!!
/*
        GregorianCalendar calendarStub = new GregorianCalendar();
        calendarStub.add(Calendar.MINUTE, timerDuration);
        finishTimestamp = calendarStub.getTimeInMillis();
*/

        return finishTimestamp;
    }

    //Activiti works WITHOUT prefix
    //Alfresco (WorkflowService) works WITH prefix
    private String clearPrefix(String activityId) {
        if (activityId == null) {
            return null;
        }
        return activityId.replace(StateMachineHelper.ACTIVITI_PREFIX, "");
    }

    private String providePrefix(String activityId) {
        return StateMachineHelper.ACTIVITI_PREFIX + clearPrefix(activityId);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
