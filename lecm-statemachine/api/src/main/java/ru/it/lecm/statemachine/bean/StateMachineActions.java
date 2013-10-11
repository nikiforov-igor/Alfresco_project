package ru.it.lecm.statemachine.bean;

import java.util.List;

/**
 * User: pmelnikov
 * Date: 11.10.13
 * Time: 9:04
 */
public interface StateMachineActions {

    List<String> getActions(String execution);

    String getActionTitle(String actionName);

    public String getActionName(String className);

}
