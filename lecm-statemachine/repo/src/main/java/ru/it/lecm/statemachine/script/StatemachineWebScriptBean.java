package ru.it.lecm.statemachine.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.ScriptableObject;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.TransitionResponse;
import ru.it.lecm.statemachine.bean.WorkflowListBean;
import ru.it.lecm.statemachine.bean.WorkflowTaskBean;
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
            List<WorkflowTask> activeTasks = stateMachineHelper.getActiveTasks(nodeRef);

            List<WorkflowTask> userTasks = stateMachineHelper.getAssignedAndPooledTasks(AuthenticationUtil.getFullyAuthenticatedUser());
            for (WorkflowTask activeTask : activeTasks) {
                for (WorkflowTask userTask : userTasks) {
                    if (activeTask.getId().equals(userTask.getId())) {
                        tasks.add(activeTask);
                    }
                }
            }
        }

        if (state == BPMState.COMPLETED || state == BPMState.ALL) {
            tasks.addAll(stateMachineHelper.getCompletedTasks(nodeRef));
        }

        WorkflowTaskListBean result = new WorkflowTaskListBean();

        boolean isBoss = orgstructureService.isBoss(currentEmployee);
        result.setShowSubordinateTasks(isBoss);

        List<WorkflowTask> myTasks = stateMachineHelper.filterTasksByAssignees(tasks, Collections.singletonList(currentEmployee));
        result.setMyTasks(myTasks, myTasksLimit);

        for (WorkflowTaskBean task : result.getMyTasks()) {
            String presentString = getDocumentPresentString(nodeRef);
            task.setDocumentPresentString(presentString);
        }

        if (addSubordinatesTask) {
            List<NodeRef> subordinateEmployees = orgstructureService.getBossSubordinate(currentEmployee);
            List<WorkflowTask> subordinatesTasks = stateMachineHelper.filterTasksByAssignees(tasks, subordinateEmployees);
            result.setSubordinatesTasks(subordinatesTasks);

            for (WorkflowTaskBean task : result.getSubordinateTasks()) {
                String presentString = getDocumentPresentString(nodeRef);
                task.setDocumentPresentString(presentString);
            }
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
     * Возвращает список активных задач пользователя для документа данного типа
     *
     * @param documentTypesString
     * @return
     */
    public WorkflowTaskListBean getDocumentsTasks(String documentTypesString) {
        if (StringUtils.isEmpty(documentTypesString)) {
            return new WorkflowTaskListBean();
        }

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        if (currentEmployee == null) {
            return new WorkflowTaskListBean();
        }

        List<String> documentTypes = Arrays.asList(documentTypesString.split(","));
        String fullyAuthenticatedUser = AuthenticationUtil.getFullyAuthenticatedUser();

        List<WorkflowTask> documentsTasks = stateMachineHelper.getDocumentsTasks(documentTypes, fullyAuthenticatedUser);
        List<WorkflowTask> myTasks = stateMachineHelper.filterTasksByAssignees(documentsTasks, Collections.singletonList(currentEmployee));

        WorkflowTaskListBean result = new WorkflowTaskListBean();
        result.setMyTasks(myTasks);

        for (WorkflowTaskBean task : result.getMyTasks()) {
            NodeRef taskDocument = stateMachineHelper.getTaskDocument(task.getWorkflowTask(), documentTypes);
            String documentPresentString = taskDocument != null ? getDocumentPresentString(taskDocument) : "";
            task.setDocumentPresentString(documentPresentString);
        }

        return result;
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

    public TransitionResponse executeActionByName(ScriptNode document, String actionName) {
        return stateMachineHelper.executeActionByName(document.getNodeRef(), actionName);
    }

    public String[] getStatuses(String documentType, boolean includeActive, boolean includeFinal) {
        Set<String> statuses = new HashSet<String>();
        if (documentType != null && !documentType.isEmpty()) {
            String[] types = documentType.split(",");
            for (String type : types) {
                if (!type.isEmpty()) {
                    statuses.addAll(stateMachineHelper.getStatuses(type, includeActive, includeFinal));
                }
            }
        }
        return statuses.toArray(new String[statuses.size()]);
    }

    public String[] getArchiveFolders(String documentType) {
        Set<String> folders = stateMachineHelper.getArchiveFolders(documentType);
        return folders.toArray(new String[folders.size()]);
    }

    public String getDocumentPresentString(NodeRef document) {
        return (String) serviceRegistry.getNodeService().getProperty(document, QName.createQName("http://www.it.ru/logicECM/document/1.0", "present-string"));
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

	/**
	 * Возвращает находится ли документ в финальном статусе
	 * @param nodeRef
	 * @return
	 */
	public boolean isFinal(String nodeRef) {
		return stateMachineHelper.isFinal(new NodeRef(nodeRef));
	}

    /**
     * Возвращает номер версии машины состояний
     * @param node
     * @return
     */
    public String getStatemachineVersion(ScriptNode node) {
        return stateMachineHelper.getStatemachineVersion(node.getNodeRef());
    }

    /**
     * Выдача сотруднику динамической роли и привелегии согласно текущему статусу документа
     * @param document документ
     * @param employee сотрудник
     * @param roleName имя роли
     * @return
     */
    public boolean grandDynamicRoleForEmployee(ScriptNode document, ScriptNode employee, String roleName) {
        return stateMachineHelper.grandDynamicRoleForEmployee(document.getNodeRef(), employee.getNodeRef(), roleName);
    }

    /**
     * Возвращает true, если поле возможно редактировать
     * @param document
     * @param field
     * @return
     */
    public boolean isEditableField(ScriptNode document, String field) {
        return stateMachineHelper.isEditableField(document.getNodeRef(), field);
    }


    /**
     * @param document - документ
     * @return Имя предыдущего статуса
     */
    public String getPreviousStatusName(ScriptNode document) {
        return stateMachineHelper.getPreviousStatusName(document.getNodeRef());
    }

    public void executeTransitionAction(ScriptNode document, String actionName) {
        stateMachineHelper.executeTransitionAction(document.getNodeRef(), actionName);
    }

}