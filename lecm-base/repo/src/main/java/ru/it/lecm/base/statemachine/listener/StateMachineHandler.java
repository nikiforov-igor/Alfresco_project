package ru.it.lecm.base.statemachine.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.service.ServiceRegistry;
import ru.it.lecm.base.statemachine.action.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:30
 */
public class StateMachineHandler implements ExecutionListener {

    private Map<String, ArrayList<StateMachineAction>> events = new HashMap<String, ArrayList<StateMachineAction>>();
    private static ServiceRegistry serviceRegistry;

    public StateMachineHandler() {
    }

    public StateMachineHandler(Element lecmExtention) {
        this.events.put("start", new ArrayList<StateMachineAction>());
        this.events.put("take", new ArrayList<StateMachineAction>());
        this.events.put("end", new ArrayList<StateMachineAction>());
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

    private StateMachineAction getStateMachineAction(Element action) {
        String actionName = action.attribute("type");
        List<Element> attributes = action.elements("attribute");
        StateMachineAction stateMachineAction = null;
        if ("setStatus".equalsIgnoreCase(actionName)) {
            stateMachineAction = new SetStatusAction(attributes);
        } else if ("changeState".equalsIgnoreCase(actionName)) {
            stateMachineAction = new ChangeStateAction(action);
        } else if ("StartDocumentWorkflow".equalsIgnoreCase(actionName)) {
            stateMachineAction = new StartDocumentWorkflowAction(attributes);
        } else if ("StartDocumentProcessing".equalsIgnoreCase(actionName)) {
            stateMachineAction = new StartDocumentProcessingAction(action);
        }

        if (stateMachineAction != null) {
            stateMachineAction.setServiceRegistry(serviceRegistry);
        }
        return stateMachineAction;
    }

}
