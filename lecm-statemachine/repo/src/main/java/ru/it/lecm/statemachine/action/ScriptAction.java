package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.workflow.activiti.listener.ScriptExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: PMelnikov
 * Date: 12.12.12
 * Time: 10:10
 */
public class ScriptAction extends StateMachineAction {

    private static final transient Logger logger = LoggerFactory.getLogger(ScriptAction.class);
    private String script = "";

	@Override
	public void execute(DelegateExecution execution) {
        ScriptExecutionListener base = new ScriptExecutionListener();
        base.setScript(new FixedValue(script));
        try {
            base.notify(execution);
        } catch (Exception e) {
            logger.error("Error while script execution", e);
        }
    }

	@Override
	public void init(Element actionElement, String processId) {
        script = actionElement.element("script").getText();
    }
}
