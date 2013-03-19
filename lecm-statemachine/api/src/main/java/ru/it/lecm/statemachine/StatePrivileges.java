package ru.it.lecm.statemachine;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.I18NUtil;

import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmelnikov
 * Date: 15.03.13
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class StatePrivileges implements InitializingBean {

    private static HashSet<String> privilegesSet = new HashSet<String>();

    private List<String> privileges = null;

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (privileges != null) {
            for (String privilegy : privileges) {
                privilegesSet.add(privilegy);
            }
        }
    }

    public HashSet<String> getPrivilegesSet() {
        return privilegesSet;
    }

    public String getLabel(String privilegy) {
        String message = I18NUtil.getMessage("statemachine.state.privileges." + privilegy);
        return message == null ? privilegy : message;
    }

}
