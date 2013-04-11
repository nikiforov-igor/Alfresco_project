package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import ru.it.lecm.statemachine.StateMachineHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerAction extends StateMachineAction {
    private static final String PROP_VARIABLE_NAME = "variableName";
    private static final String PROP_DURATION = "duration";

    private String variable = null;
    private int duration = 0;

    @Override
    public void init(Element action, String processId) {
        List<Element> attributes = action.elements("attribute");
        for (Element attribute : attributes) {
            if (PROP_VARIABLE_NAME.equalsIgnoreCase(attribute.attribute("name"))) {
                variable = attribute.attribute("value");
            }

            if (PROP_DURATION.equalsIgnoreCase(attribute.attribute("name"))) {
                duration = Integer.parseInt(attribute.attribute("value"));
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
                            String currentTaskId = new StateMachineHelper().getCurrentTaskId(stateMachineExecutionId);
                            getTimerActionHelper().addTimer(stateMachineExecutionId, currentTaskId, variable, duration);
                            return null;
                        }
                    });
                }
            };

            new Timer().schedule(waitForRealTaskId, 10000);
        }

        if (eventName.equalsIgnoreCase("end")) {
            String currentTaskId = new StateMachineHelper().getCurrentTaskId(stateMachineExecutionId);
            getTimerActionHelper().removeTimer(currentTaskId);
        }
    }
}
