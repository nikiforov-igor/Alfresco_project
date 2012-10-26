package ru.it.lecm.base.statemachine.bean;

import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.base.statemachine.action.StateMachineAction;

import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 26.10.12
 * Time: 10:30
 */
public class StateMachineActions implements InitializingBean {

    private HashMap<String, String> actions = new HashMap<String, String>();

    private static HashMap<String, String> actionNames = new HashMap<String, String>();
    private static HashMap<String, String> actionClasses = new HashMap<String, String>();


    public void setActions(HashMap<String, String> actions) {
        this.actions = actions;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (actions != null) {
            for (String key : actions.keySet()) {
                actionClasses.put(key, actions.get(key));
                actionNames.put(actions.get(key), key);
            }
        }
    }

    public static String getActionName(Class<? extends StateMachineAction> className) {
        return actionNames.get(className.getName());
    }

    public static String getClassName(String actionName) {
        return actionClasses.get(actionName);
    }

}
