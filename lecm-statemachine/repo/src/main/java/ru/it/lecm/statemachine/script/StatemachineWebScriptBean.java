package ru.it.lecm.statemachine.script;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.mozilla.javascript.ScriptableObject;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.WorkflowListBean;
import ru.it.lecm.statemachine.WorkflowTaskListBean;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: pmelnikov
 * Date: 15.03.13
 * Time: 13:56
 */
public class StatemachineWebScriptBean extends BaseScopableProcessorExtension {

    /**
     * Service registry
     */
    protected ServiceRegistry services;
    private OrgstructureBean orgstructureService;
    private StateMachineHelper stateMachineHelper;

    /**
     * Set the service registry
     *
     * @param services the service registry
     */
    public void setServiceRegistry(ServiceRegistry services) {
        this.services = services;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setStateMachineHelper(StateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

    public boolean hasPrivilegeByEmployee(ScriptNode employee, ScriptNode document, Object privileges) {
        Collection<String> privilegesCollection = convertToJavaCollection(privileges);
        return stateMachineHelper.hasPrivilegeByEmployee(employee.getNodeRef(), document.getNodeRef(), privilegesCollection);
    }

    public boolean hasPrivilegeByPerson(ScriptNode person, ScriptNode document, Object privileges) {
        Collection<String> privilegesCollection = convertToJavaCollection(privileges);
        return stateMachineHelper.hasPrivilegeByPerson(person.getNodeRef(), document.getNodeRef(), privilegesCollection);
    }

    public WorkflowTaskListBean getTasks(ScriptNode node, String stateParam, boolean addSubordinatesTask, int myTasksLimit) {
        return stateMachineHelper.getTasks(node.getNodeRef(), stateParam, addSubordinatesTask, myTasksLimit);
    }

    public WorkflowListBean getWorkflows(ScriptNode node, String stateParam, int activeWorkflowsLimit) {
        return stateMachineHelper.getWorkflows(node.getNodeRef(), stateParam, activeWorkflowsLimit);
    }

    private Collection<String> convertToJavaCollection(Object privileges) {
        HashSet<String> result = new HashSet<String>();
        if (privileges instanceof String) {
            result.add((String) privileges);
        } if (privileges instanceof ScriptableObject) {
            ScriptableObject object = (ScriptableObject) privileges;

            Object[] ids = object.getIds();
            for (Object id : ids) {
                String value = (String) object.get((Integer) id, object);
                result.add(value);
            }
        }
        return result;
    }

}