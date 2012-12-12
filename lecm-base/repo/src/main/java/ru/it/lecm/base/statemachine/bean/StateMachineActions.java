package ru.it.lecm.base.statemachine.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.base.statemachine.action.StateMachineAction;

import java.util.*;

/**
 * User: PMelnikov
 * Date: 26.10.12
 * Time: 10:30
 */
public class StateMachineActions implements InitializingBean {

	private static HashMap<String, String> actionNames = new HashMap<String, String>();
	private static HashMap<String, String> actionClasses = new HashMap<String, String>();
	private static HashMap<ExecutionKey, ArrayList<String>> actionsByExecution = new HashMap<ExecutionKey, ArrayList<String>>();

	private String name;
	private String action;
	private String type;
	private String execution;

	public void setName(String name) {
		this.name = name;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setExecution(String execution) {
		this.execution = execution;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (name != null && action != null && type != null && execution != null) {
			type = type.toLowerCase();
			actionClasses.put(name, action);
			actionNames.put(action, name);
			StringTokenizer tokenizer = new StringTokenizer(execution, ",");
			while (tokenizer.hasMoreTokens()) {
				String executionKey = tokenizer.nextToken().trim().toLowerCase();
				ExecutionKey key = new ExecutionKey(type, executionKey);
				ArrayList<String> actions = actionsByExecution.get(key);
				if (actions == null) {
					actions = new ArrayList<String>();
					actionsByExecution.put(key, actions);
				}
				actions.add(name);
			}
		}
	}

	public static String getActionName(Class<? extends StateMachineAction> className) {
		return actionNames.get(className.getName());
	}

	public static String getClassName(String actionName) {
		return actionClasses.get(actionName);
	}

	public List<String> getActions(String execution) {
		ExecutionKey key = new ExecutionKey("user", execution.toLowerCase());
		List<String> actions = actionsByExecution.get(key);
		return actions == null ? new ArrayList<String>() : Collections.unmodifiableList(actions);
	}

	public String getActionTitle(String actionName) {
		String key = "statemachine.action." + actionName;
		String message = I18NUtil.getMessage(key, I18NUtil.getLocale());
		return message == null ? actionName : message;
	}

	private class ExecutionKey {

		String type;
		String execution;

		private ExecutionKey(String type, String execution) {
			this.type = type;
			this.execution = execution;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ExecutionKey that = (ExecutionKey) o;

			if (execution != null ? !execution.equals(that.execution) : that.execution != null) return false;
			if (type != null ? !type.equals(that.type) : that.type != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = type != null ? type.hashCode() : 0;
			result = 31 * result + (execution != null ? execution.hashCode() : 0);
			return result;
		}
	}

}
