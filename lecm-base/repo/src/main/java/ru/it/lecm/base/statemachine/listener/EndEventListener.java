package ru.it.lecm.base.statemachine.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import ru.it.lecm.base.statemachine.StateMachineHelper;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:57
 * <p/>
 * Класс-слушатель окончания процесса.
 * По завершению передает сигнал об окончании пользовательского процесса
 * для дальнейшего оповещения машины состояний.
 */
public class EndEventListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        new StateMachineHelper().stopUserWorkflowProcessing(delegateExecution);
    }
}
