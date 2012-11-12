package ru.it.lecm.base.statemachine.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.base.statemachine.action.StateMachineAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 26.10.12
 * Time: 10:30
 */
public class StateMachineActions implements InitializingBean {

	private static HashMap<String, String> actionNames = new HashMap<String, String>();
	private static HashMap<String, String> actionClasses = new HashMap<String, String>();
	private static HashMap<String, ArrayList<String>> actionByType = new HashMap<String, ArrayList<String>>();

	private String name;
	private String action;
	private String type;

	public void setName(String name) {
		this.name = name;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (name != null && action != null && type != null) {
			actionClasses.put(name, action);
			actionNames.put(action, name);
			ArrayList<String> byType = actionByType.get(type);
			if (byType == null) {
				byType = new ArrayList<String>();
				actionByType.put(type, byType);
			}
			byType.add(name);
		}
	}

	public static String getActionName(Class<? extends StateMachineAction> className) {
		return actionNames.get(className.getName());
	}

	public static String getClassName(String actionName) {
		return actionClasses.get(actionName);
	}

	public List<String> getActionsByType(String type) {
		ArrayList<String> byType = actionByType.get(type);
		return byType == null ? new ArrayList<String>() : Collections.unmodifiableList(byType);
	}

	public String getActionTitle(String actionName) {
		String key = "statemachine.action." + actionName;
		String message = I18NUtil.getMessage(key, I18NUtil.getLocale());
		return message == null ? actionName : message;
	}

}
