package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.workflow.activiti.listener.ScriptExecutionListener;

/**
 * User: PMelnikov
 * Date: 12.12.12
 * Time: 10:10
 */
public class ScriptAction extends StateMachineAction {

    private String script = "";

	@Override
	public void execute(DelegateExecution execution) {
        ScriptExecutionListener base = new ScriptExecutionListener();
        base.setScript(new FixedValue(script));
        try {
            base.notify(execution);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void init(Element actionElement, String processId) {
        script = actionElement.element("script").getText();
    }
}
