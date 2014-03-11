package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.statemachine.StateMachineHelper;

/**
 * User: PMelnikov
 * Date: 12.12.12
 * Time: 10:10
 */
public class ScriptAction extends StateMachineAction implements PostponedAction {

    private static final transient Logger logger = LoggerFactory.getLogger(ScriptAction.class);
    private String script = "";

	@Override
	public void execute(DelegateExecution execution) {

    }

	@Override
	public void init(Element actionElement, String processId) {
        script = actionElement.element("script").getText();
    }

    @Override
    public void postponedExecution(final String taskId, final StateMachineHelper helper) {
        if (!"".equals(script)) {
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                @Override
                public Object doWork() throws Exception {
                    helper.executeScript(script, helper.getCurrentExecutionId(taskId));
                    return null;
                }
            });
        }
    }
}
