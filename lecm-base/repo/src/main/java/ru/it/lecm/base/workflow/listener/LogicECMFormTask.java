package ru.it.lecm.base.workflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import ru.it.lecm.base.workflow.WorkflowHelper;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:53
 * <p/>
 * Слушатель задачи для Activiti BPM Platform, который позволяет
 * запустить пользовательский процесс
 */
public class LogicECMFormTask implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        new WorkflowHelper().startDocumentProcessing(delegateTask.getId());
    }

}
