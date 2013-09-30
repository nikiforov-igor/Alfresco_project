package ru.it.lecm.statemachine.action;

import ru.it.lecm.statemachine.StateMachineHelper;

/**
 * User: pmelnikov
 * Date: 30.09.13
 * Time: 9:18
 */
public interface PostponedAction {

    public void postponedExecution(String taskId, StateMachineHelper helper);
}
