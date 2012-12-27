package ru.it.lecm.statemachine.assign;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: PMelnikov
 * Date: 24.12.12
 * Time: 13:58
 */
public class AssignExecution {

	private static ServiceRegistry serviceRegistry;
	private static OrgstructureBean orgstructureBean;

	private NodeRef document = null;
	private Object result = null;

	/**
	 * Enum с набором возможных команд
	 */
	private enum Command {
		CURRENTUSER,
		USER,
		BOSS,
		ATTRIBUTE,
		ROLE,
		UNIT,
		UP,
		CUSTOM
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		AssignExecution.serviceRegistry = serviceRegistry;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		AssignExecution.orgstructureBean = orgstructureBean;
	}

	/**
	 * Назначение ссылки на документ для использования команды attribute
	 * @param document
	 */
	public void setDocument(NodeRef document) {
		this.document = document;
	}

	/**
	 * Выполнение всех действий для получения ссылки на необходимого пользователя
	 * @param execution
	 * @return
	 */
	public void execute(String execution) {
		List<CommandInstance> instances = parseInstances(execution);
		result = run(instances);
	}

	public NodeRef getNodeRefResult() {
		return (NodeRef) result;
	}

	public String getStringResult() {
		return (String) result;
	}

	/**
	 * Последовательное исполнение команд
	 * @param instances
	 * @return
	 */
	private Object run(List<CommandInstance> instances) {
		Object result = null;
		for (CommandInstance instance : instances) {
			if (instance.getCommand() == Command.CURRENTUSER) {
				result = currentUser();
			} else if (instance.getCommand() == Command.USER) {
				result = user(instance.getParameter());
			} else if (instance.getCommand() == Command.BOSS) {
				result = boss((NodeRef) result);
			} else if (instance.getCommand() == Command.ATTRIBUTE) {
				result = attribute(instance.getParameter());
			} else if (instance.getCommand() == Command.ROLE) {
				result = role(instance.getParameter());
			} else if (instance.getCommand() == Command.UNIT) {
				result = unit(instance.getParameter());
			} else if (instance.getCommand() == Command.UP) {
				result = up((NodeRef) result);
			} else if (instance.getCommand() == Command.CUSTOM) {
				result = instance.getParameter();
			}
		}
		return result;
	}

	/**
	 * Подготовка списка команд готовых к выполнению
	 * @param execution
	 * @return
	 */
	private List<CommandInstance> parseInstances(String execution) {
		ArrayList<CommandInstance> instances = new ArrayList<CommandInstance>();
		Pattern pattern = Pattern.compile("(.*?\\))\\.");
		Matcher matcher = pattern.matcher(execution + ".");

		Pattern functionPattern = Pattern.compile("(.*?)\\(\"(.*?)\"\\)|(.*?)\\(\\)|(.*?)$");
		while (matcher.find()) {
			Matcher function = functionPattern.matcher(matcher.group(1));
			function.find();
			Command command = null;
			String parameter = null;
			if (function.group(1) != null) {
				command = Command.valueOf(function.group(1).toUpperCase());
				parameter = function.group(2);
				parameter = parameter.replace("\\\\", "\\");
				parameter = parameter.replace("\\\"", "\"");
			} else if (function.group(3) != null) {
				command = Command.valueOf(function.group(3).toUpperCase());
			} else if (function.group(4) != null) {
				command = Command.valueOf(function.group(4).toUpperCase());
			}
			CommandInstance instance = new CommandInstance(command, parameter);
			instances.add(instance);
		}
		return instances;
	}

	/**
	 * Выполнение команды currentUser
	 * @return
	 */
	private NodeRef currentUser() {
		String person = AuthenticationUtil.getFullyAuthenticatedUser();
		return serviceRegistry.getPersonService().getPerson(person);
	}

	/**
	 * Выполнение команды user
	 * @return
	 */
	private NodeRef user(String parameter) {
		NodeRef person = null;
		try {
			person = serviceRegistry.getPersonService().getPerson(parameter, false);
		} catch (NoSuchPersonException e) {
		}
		return person;
	}

	/**
	 * Выполнение команды boss
	 * @return
	 */
	private NodeRef boss(NodeRef employeePersonRef) {
		NodeRef employee = orgstructureBean.getEmployeeByPerson(employeePersonRef);
		if (employee != null) {
			NodeRef boss = orgstructureBean.findEmployeeBoss(employee);
			if (boss != null) {
				NodeRef person = orgstructureBean.getPersonForEmployee(boss);
				if (person != null) {
					return person;
				}
			}
		}
		return null;
	}

	/**
	 * Выполнение команды attribute
	 * @return
	 */
	private NodeRef attribute(String parameter) {
		QName qname = QName.createQName(parameter);
		List<AssociationRef> ref = serviceRegistry.getNodeService().getTargetAssocs(document, qname);
		if (ref.size() > 0) {
			NodeRef employee = ref.get(0).getTargetRef();
			return orgstructureBean.getPersonForEmployee(employee);
		} else {
			return null;
		}
	}

	/**
	 * Выполнение команды role
	 * @return
	 */
	private NodeRef role(String parameter) {
		return null;
	}

	/**
	 * Выполнение команды unit
	 * @return
	 */
	private NodeRef unit(String parameter) {
		return null;
	}

	/**
	 * Выполнение команды up
	 * @return
	 */
	private NodeRef up(NodeRef unit) {
		return null;
	}

	/**
	 * Выполнение команды custom
	 * @return
	 */
	private NodeRef custom(String parameter) {
		return null;
	}

	/**
	 * Вспомогательный класс для описания уже инициализированной команды готовой к выполнению
	 */
	private class CommandInstance {

		private Command command;
		private String parameter;

		private CommandInstance(Command command, String parameter) {
			this.command = command;
			this.parameter = parameter;
		}

		public Command getCommand() {
			return command;
		}

		public String getParameter() {
			return parameter;
		}
	}

}
