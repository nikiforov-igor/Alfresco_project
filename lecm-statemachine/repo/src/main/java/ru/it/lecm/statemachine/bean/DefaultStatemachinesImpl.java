package ru.it.lecm.statemachine.bean;

import ru.it.lecm.statemachine.DefaultStatemachines;

import java.util.HashMap;

/**
 * User: pmelnikov
 * Date: 15.04.13
 * Time: 9:12
 */
public class DefaultStatemachinesImpl implements DefaultStatemachines {

    private String statemachineName;
    private String path;
    private static HashMap<String, String> paths = new HashMap<String, String>();

    public void setStatemachineName(String statemachineName) {
        this.statemachineName = statemachineName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void register() throws Exception {
        if (statemachineName != null && path != null) {
            paths.put(statemachineName.replace(":", "_"), path);
        }
    }

    public String getPath(String statemachineName) {
        return paths.get(statemachineName.replace(":", "_"));
    }

}
