package ru.it.lecm.base.statemachine.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import ru.it.lecm.base.statemachine.StateMachineHelper;

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
        new StateMachineHelper().startDocumentProcessing(delegateTask.getId());
    }

}
