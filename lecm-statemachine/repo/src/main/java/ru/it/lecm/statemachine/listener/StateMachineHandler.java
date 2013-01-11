package ru.it.lecm.statemachine.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.bean.StateMachineActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:30
 * <p/>
 * Слушатель машины состояний. Содержит в себе описание событий и действий для определенного статуса машины состояний
 */

public class StateMachineHandler implements ExecutionListener {

	private Map<String, ArrayList<StateMachineAction>> events = new HashMap<String, ArrayList<StateMachineAction>>();
	private static ServiceRegistry serviceRegistry;
	private static Repository repositoryHelper;
	private static Log logger = LogFactory.getLog(StateMachineHandler.class);

	private String processId = "";

	public StateMachineHandler() {
	}

	public StateMachineHandler(Element lecmExtention, String processId) {
		this.processId = processId;
		this.events.put(ExecutionListener.EVENTNAME_START, new ArrayList<StateMachineAction>());
		this.events.put(ExecutionListener.EVENTNAME_TAKE, new ArrayList<StateMachineAction>());
		this.events.put(ExecutionListener.EVENTNAME_END, new ArrayList<StateMachineAction>());
		List<Element> events = lecmExtention.elements("event");
		for (Element event : events) {
			String eventName = event.attribute("on").toLowerCase();
			ArrayList<StateMachineAction> stateMachineActions = this.events.get(eventName);
			List<Element> actions = event.elements("action");
			for (Element action : actions) {
				StateMachineAction stateMachineAction = getStateMachineAction(action);
				if (stateMachineAction != null) {
					stateMachineActions.add(stateMachineAction);
				}
			}
		}

	}

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String eventName = execution.getEventName();
		List<StateMachineAction> actions = events.get(eventName);
		for (StateMachineAction action : actions) {
			action.execute(execution);
		}
	}

	public Map<String, ArrayList<StateMachineAction>> getEvents() {
		return events;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineHandler.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		StateMachineHandler.repositoryHelper = repositoryHelper;
	}

	private StateMachineAction getStateMachineAction(Element actionElement) {
		String actionName = actionElement.attribute("type");
		StateMachineAction action = null;
		try {
			Class actionClass = Class.forName(StateMachineActions.getClassName(actionName));
			action = (StateMachineAction) actionClass.newInstance();
		} catch (Exception e) {
			logger.error("Cannot initialize action " + actionName, e);
		}

		if (action != null) {
			action.setServiceRegistry(serviceRegistry);
			action.setRepositoryHelper(repositoryHelper);
			action.init(actionElement, processId);
		}
		return action;
	}

}
