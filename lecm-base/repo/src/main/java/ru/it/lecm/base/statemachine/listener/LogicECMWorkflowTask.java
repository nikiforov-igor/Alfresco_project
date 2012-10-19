package ru.it.lecm.base.statemachine.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import ru.it.lecm.base.statemachine.StateMachineHelper;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:53
 * <p/>
 * Слушатель задачи для Activiti BPM Platform, который позволяет
 * запустить пользовательский процесс
 */
public class LogicECMWorkflowTask implements TaskListener {

    private FixedValue workflowId;
    private FixedValue assignee;

    @Override
    public void notify(DelegateTask delegateTask) {
        new StateMachineHelper().startUserWorkflowProcessing(delegateTask.getId(), workflowId.getExpressionText(), assignee.getExpressionText());
    }

}
