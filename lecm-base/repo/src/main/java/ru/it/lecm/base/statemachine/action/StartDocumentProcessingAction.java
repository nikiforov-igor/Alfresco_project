package ru.it.lecm.base.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import ru.it.lecm.base.statemachine.StateMachineHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: PMelnikov
 * Date: 23.10.12
 * Time: 8:47
 */
public class StartDocumentProcessingAction extends StateMachineAction {

    private String expression = "";
    private static final String PROP_EXPRESSION = "expression";

    public StartDocumentProcessingAction(List<Element> attributes) {

        for (Element attribute : attributes) {
            if (PROP_EXPRESSION.equalsIgnoreCase(attribute.attribute("name"))) {
                expression = attribute.attribute("value");
            }
        }
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public void execute(DelegateExecution execution) {
        final String processId = execution.getProcessInstanceId();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

                    @Override
                    public Object doWork() throws Exception {
                        StateMachineHelper helper = new StateMachineHelper();
                        String taskId = helper.getCurrentTaskId(processId);
                        helper.startDocumentProcessing(taskId.replace(StateMachineHelper.ACTIVITI_PREFIX, ""));
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);
            }
        };
        timer.schedule(task, 1000);
    }

    public String getType() {
        return "StartDocumentProcessing";
    }
}
