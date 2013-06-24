package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import ru.it.lecm.statemachine.expression.TransitionExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerAction extends StateMachineAction {
    public static final String PROP_TIMER_DURATION = "timerDuration";

    private List<TransitionExpression> transitionExpressions = new ArrayList<TransitionExpression>();
    private int timerDuration = 0;
    private String variable = null;

    @Override
    public void init(Element action, String processId) {
        Element expressions = action.element(TAG_EXPRESSIONS);
        if (expressions == null) {
            return;
        }

        variable = expressions.attribute(PROP_OUTPUT_VARIABLE);

        for (Element expressionElement : expressions.elements(TAG_EXPRESSION)) {
            String expression = expressionElement.attribute(PROP_EXPRESSION);
            String outputValue = expressionElement.attribute(PROP_OUTPUT_VALUE);
            boolean stopSubWorkflows = Boolean.parseBoolean(expressionElement.attribute(PROP_STOP_SUBWORKFLOWS));
            this.transitionExpressions.add(new TransitionExpression(expression, outputValue, stopSubWorkflows));
        }

        List<Element> attributes = action.elements("attribute");
        for (Element attribute : attributes) {
            if (PROP_TIMER_DURATION.equalsIgnoreCase(attribute.attribute("name"))) {
                timerDuration = Integer.parseInt(attribute.attribute("value"));
            }
        }
    }

    @Override
    public void execute(DelegateExecution execution) {
        String eventName = execution.getEventName();
        final String stateMachineExecutionId = execution.getId();

        if (eventName.equalsIgnoreCase("start")) {
            TimerTask waitForRealTaskId = new TimerTask() {
                @Override
                public void run() {
                    AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                        @Override
                        public Object doWork() throws Exception {
                            getTimerActionHelper().addTimer(stateMachineExecutionId, timerDuration, variable, transitionExpressions);
                            return null;
                        }
                    });
                }
            };

            new Timer().schedule(waitForRealTaskId, 5000);
        }

        if (eventName.equalsIgnoreCase("end")) {
            getTimerActionHelper().removeTimerNode(stateMachineExecutionId);
        }
    }
}
