package ru.it.lecm.base.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.service.ServiceRegistry;
import ru.it.lecm.base.statemachine.bean.StateMachineActions;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:29
 */
abstract public class StateMachineAction {

    private ServiceRegistry serviceRegistry;

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    abstract public void execute(DelegateExecution execution);

    abstract public void init(Element actionElement);

    public String getActionName() {
        return StateMachineActions.getActionName(getClass());
    }

}
