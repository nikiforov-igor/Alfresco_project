package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
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
    public void postponedExecution(String taskId, StateMachineHelper helper) {
        if (!"".equals(script)) {
            helper.executeScript(script, helper.getCurrentExecutionId(taskId));
        }
    }
}
