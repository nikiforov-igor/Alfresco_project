package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.bpmn.model.BaseElement;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.expression.TransitionExpression;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

public class TimerAction extends StateMachineAction implements PostponedAction {

    private List<TransitionExpression> transitionExpressions = new ArrayList<TransitionExpression>();
    private String timerDuration = null;
    private String variable = null;

    private final static Logger logger = LoggerFactory.getLogger(TimerAction.class);

    @Override
    public void init(BaseElement action, String processId) {
        //Element expressions = action.element(TAG_EXPRESSIONS);
        //if (expressions == null) {
        //    return;
        //}

        //variable = expressions.attribute(PROP_OUTPUT_VARIABLE);

        //for (Element expressionElement : expressions.elements(TAG_EXPRESSION)) {
        //    String expression = expressionElement.attribute(PROP_EXPRESSION);
        //    String outputValue = expressionElement.attribute(PROP_OUTPUT_VALUE);
        //    boolean stopSubWorkflows = Boolean.parseBoolean(expressionElement.attribute(StatemachineActionConstants.PROP_STOP_SUBWORKFLOWS));
        //    String script = expressionElement.getText();
        //    this.transitionExpressions.add(new TransitionExpression(expression, outputValue, stopSubWorkflows, script));
        //}

        //List<Element> attributes = action.elements("attribute");
        //for (Element attribute : attributes) {
        //    if (StatemachineActionConstants.PROP_TIMER_DURATION.equalsIgnoreCase(attribute.attribute("name"))) {
        //        timerDuration = attribute.attribute("value");
        //    }
        //}
    }

    @Override
    public void execute(DelegateExecution execution) {
        String eventName = execution.getEventName();
        final String stateMachineExecutionId = execution.getId();
        if (eventName.equalsIgnoreCase("end")) {
//			try {
//				getTimerActionHelper().removeTimerNode(stateMachineExecutionId);
//			} catch (WriteTransactionNeededException ex) {
//				throw new RuntimeException(ex);
//			}
        }
    }

    @Override
    public void postponedExecution(String taskId, StateMachineHelper helper) {
        final String stateMachineExecutionId = helper.getCurrentExecutionId(taskId);
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
//                getTimerActionHelper().addTimer(stateMachineExecutionId, timerDuration, variable, transitionExpressions);
                return null;
            }
        });
    }

}
