package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import ru.it.lecm.statemachine.StateMachineHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerAction extends StateMachineAction {
    public static final String PROP_TIMER_DURATION = "timerDuration";
    public static final String PROP_STOP_SUBWORKFLOWS = "stopSubWorkflows";
    public static final String PROP_VARIABLE_NAME = "variableName";

    private String variable = null;
    private int timerDuration = 0;
    private boolean stopSubWorkflows = false;

    @Override
    public void init(Element action, String processId) {
        List<Element> attributes = action.elements("attribute");
        for (Element attribute : attributes) {
            if (PROP_VARIABLE_NAME.equalsIgnoreCase(attribute.attribute("name"))) {
                variable = attribute.attribute("value");
            }

            if (PROP_TIMER_DURATION.equalsIgnoreCase(attribute.attribute("name"))) {
                timerDuration = Integer.parseInt(attribute.attribute("value"));
            }

            if (PROP_STOP_SUBWORKFLOWS.equalsIgnoreCase(attribute.attribute("name"))) {
                stopSubWorkflows = Boolean.parseBoolean(attribute.attribute("value"));
            }
        }
    }

    @Override
    public void execute(DelegateExecution execution) {
        final String stateMachineExecutionId = execution.getId();
        String eventName = execution.getEventName();

        if (eventName.equalsIgnoreCase("start")) {
            TimerTask waitForRealTaskId = new TimerTask() {
                @Override
                public void run() {
                    AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                        @Override
                        public Object doWork() throws Exception {
                            getTimerActionHelper().addTimer(stateMachineExecutionId, variable, timerDuration, stopSubWorkflows);
                            return null;
                        }
                    });
                }
            };

            new Timer().schedule(waitForRealTaskId, 5000);
        }

        if (eventName.equalsIgnoreCase("end")) {
            getTimerActionHelper().removeTimer(stateMachineExecutionId);
        }
    }
}
