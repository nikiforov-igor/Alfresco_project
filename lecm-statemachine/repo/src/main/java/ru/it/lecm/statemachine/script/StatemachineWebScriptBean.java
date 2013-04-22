package ru.it.lecm.statemachine.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.mozilla.javascript.ScriptableObject;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.TransitionResponse;
import ru.it.lecm.statemachine.bean.WorkflowListBean;
import ru.it.lecm.statemachine.bean.WorkflowTaskListBean;

import java.util.*;

/**
 * User: pmelnikov
 * Date: 15.03.13
 * Time: 13:56
 */
public class StatemachineWebScriptBean extends BaseWebScript {

    private OrgstructureBean orgstructureService;
    private StateMachineHelper stateMachineHelper;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setStateMachineHelper(StateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

    enum BPMState {
        NA, ACTIVE, COMPLETED, ALL;

        public static BPMState getValue(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (Exception e) {
                return NA;
            }
        }
    }

    public WorkflowTaskListBean getTasks(ScriptNode node, String stateParam, boolean addSubordinatesTask, int myTasksLimit) {
        if (node == null) {
            return new WorkflowTaskListBean();
        }

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        if (currentEmployee == null) {
            return new WorkflowTaskListBean();
        }

        NodeRef nodeRef = node.getNodeRef();
        BPMState state = BPMState.getValue(stateParam);

        List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
        if (state == BPMState.ACTIVE || state == BPMState.ALL) {
            tasks.addAll(stateMachineHelper.getActiveTasks(nodeRef));
        }

        if (state == BPMState.COMPLETED || state == BPMState.ALL) {
            tasks.addAll(stateMachineHelper.getCompletedTasks(nodeRef));
        }

        WorkflowTaskListBean result = new WorkflowTaskListBean();

        boolean isBoss = orgstructureService.isBoss(currentEmployee);
        result.setShowSubordinateTasks(isBoss);

        List<WorkflowTask> myTasks = stateMachineHelper.filterTasksByAssignees(tasks, Collections.singletonList(currentEmployee));
        result.setMyTasks(myTasks, myTasksLimit);

        if (addSubordinatesTask) {
            List<NodeRef> subordinateEmployees = orgstructureService.getBossSubordinate(currentEmployee);
            List<WorkflowTask> subordinatesTasks = stateMachineHelper.filterTasksByAssignees(tasks, subordinateEmployees);
            result.setSubordinatesTasks(subordinatesTasks);
        }

        return result;
    }

    public WorkflowListBean getWorkflows(ScriptNode node, String stateParam, int activeWorkflowsLimit) {
        if (node == null) {
            return new WorkflowListBean();
        }

        NodeRef nodeRef = node.getNodeRef();
        BPMState state = BPMState.getValue(stateParam);
        WorkflowListBean result = new WorkflowListBean();

        List<WorkflowInstance> activeWorkflows = stateMachineHelper.getActiveWorkflows(nodeRef);
        result.setActiveWorkflows(activeWorkflows, activeWorkflowsLimit);

        if (state == BPMState.ALL) {
            List<WorkflowInstance> completedWorkflows = stateMachineHelper.getCompletedWorkflows(nodeRef);
            result.setCompletedWorkflows(completedWorkflows);
        }

        return result;
    }

    public boolean isReadOnlyCategory(ScriptNode node, String category) {
        return stateMachineHelper.isReadOnlyCategory(node.getNodeRef(), category);
    }

    /**
     * Возвращает список активных задач для документа
     * @param node
     * @return
     */
    public List<WorkflowTask> getDocumentTasks(ScriptNode node) {
        return stateMachineHelper.getDocumentTasks(node.getNodeRef());
    }

    /**
     * Возвращает список активных рабочих процессов
     * @param node
     * @return
     */
    public List<WorkflowInstance> getDocumentWorkflows(ScriptNode node) {
        return stateMachineHelper.getDocumentWorkflows(node.getNodeRef());
    }

    /**
     * Проверка наличия машины состояний у документа
     * @param node
     * @return
     */
    public boolean hasStatemachine(ScriptNode node) {
        return stateMachineHelper.hasStatemachine(node.getNodeRef());
    }

    /**
     * Возвращает может ли текущий сотрудник создавать документ определенного типа
     * @param type
     * @return
     */
    public boolean isStarter(String type) {
        return stateMachineHelper.isStarter(type);
    }

    /**
     * Проверка документа на черновой статус
     * @param node
     * @return
     */
    public boolean isDraft(ScriptNode node) {
        return stateMachineHelper.isDraft(node.getNodeRef());
    }

    public String getDocumentStatus(ScriptNode document) {
        return stateMachineHelper.getDocumentStatus(document.getNodeRef());
    }

    public TransitionResponse executeAction(ScriptNode document, String actionId) {
        return stateMachineHelper.executeUserAction(document.getNodeRef(), actionId);
    }

    public String[] getStatuses(String documentType) {
        List<String> statuses = stateMachineHelper.getStatuses(documentType);
        return statuses.toArray(new String[statuses.size()]);
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