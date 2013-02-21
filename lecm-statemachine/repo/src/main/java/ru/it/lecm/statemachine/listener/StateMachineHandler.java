package ru.it.lecm.statemachine.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.security.events.INodeACLBuilder;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.bean.StateMachineActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.security.authentication.AuthenticationUtil;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:30
 * <p/>
 * Слушатель машины состояний. Содержит в себе описание событий и действий для определенного статуса машины состояний
 */

public class StateMachineHandler implements ExecutionListener {

	private final static Logger logger = LoggerFactory.getLogger(StateMachineHandler.class);

	private Map<String, ArrayList<StateMachineAction>> events = new HashMap<String, ArrayList<StateMachineAction>>();
	private static ServiceRegistry serviceRegistry;
	private static INodeACLBuilder lecmAclBuilderBean;
	private static Repository repositoryHelper;
	private static BusinessJournalService businessJournalService;

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
    public void notify(final DelegateExecution execution) throws Exception {
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
                try {
                    String eventName = execution.getEventName();
                    List<StateMachineAction> actions = events.get(eventName);
                    for (StateMachineAction action : actions) {
                        action.execute(execution);
                    }

                    if (eventName.equals(ExecutionListener.EVENTNAME_START)) {
                        actions = events.get(ExecutionListener.EVENTNAME_TAKE);
                        for (StateMachineAction action : actions) {
                            action.execute(execution);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while action execution", e);
                    throw e;
                }
                return null;
            }
        });
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

	public void setLecmAclBuilderBean(INodeACLBuilder lecmAclBuilderBean) {
		StateMachineHandler.lecmAclBuilderBean = lecmAclBuilderBean;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		StateMachineHandler.businessJournalService = businessJournalService;
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
			action.setLecmAclBuilderBean(lecmAclBuilderBean);
			action.setBusinessJournalService(businessJournalService);
			try {
				action.init(actionElement, processId);
			} catch (Exception e) {
				logger.error("Error while init action", e);
				throw new IllegalStateException(e);
			}
		}
		return action;
	}

}
