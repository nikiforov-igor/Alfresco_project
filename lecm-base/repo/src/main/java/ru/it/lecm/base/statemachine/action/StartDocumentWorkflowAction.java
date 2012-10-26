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
public class StartDocumentWorkflowAction extends StateMachineAction {

    private String workflowId = "";
    private String assignee = "";
    private static final String PROP_WORKFLOW_ID = "workflowId";
    private static final String PROP_ASSIGNEE = "assignee";

    @Override
    public void init(Element action) {
        List<Element> attributes = action.elements("attribute");
        for (Element attribute : attributes) {
            if (PROP_WORKFLOW_ID.equalsIgnoreCase(attribute.attribute("name"))) {
                workflowId = attribute.attribute("value");
            } else if (PROP_ASSIGNEE.equalsIgnoreCase(attribute.attribute("name"))) {
                assignee = attribute.attribute("value");
            }
        }
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
                        helper.startUserWorkflowProcessing(taskId.replace(StateMachineHelper.ACTIVITI_PREFIX, ""), workflowId, assignee, true);
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);
            }
        };
        timer.schedule(task, 1000);
    }

}
