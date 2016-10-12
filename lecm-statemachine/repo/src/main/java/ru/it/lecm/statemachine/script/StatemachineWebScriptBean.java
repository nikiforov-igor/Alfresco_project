package ru.it.lecm.statemachine.script;

import org.activiti.engine.task.Task;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.bean.WorkflowListBean;
import ru.it.lecm.statemachine.bean.WorkflowTaskBean;
import ru.it.lecm.statemachine.bean.WorkflowTaskListBean;

import java.util.*;
import ru.it.lecm.statemachine.DefaultStatemachines;

/**
 * User: pmelnikov Date: 15.03.13 Time: 13:56
 */
public class StatemachineWebScriptBean extends BaseWebScript {

	private OrgstructureBean orgstructureService;
	private StateMachineServiceBean stateMachineHelper;
	private DefaultStatemachines defaultStatemachines;

	public void setDefaultStatemachines(DefaultStatemachines defaultStatemachines) {
		this.defaultStatemachines = defaultStatemachines;
	}

	private final static Logger logger = LoggerFactory.getLogger(StatemachineWebScriptBean.class);

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
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

	/**
	 * Возвращает задачи для документа
	 *
	 * @param node - документ, для которого будет возвращены задачи
	 * @param stateParam статус задачи
	 * @param addSubordinatesTask поск задач подчененных сотрудников
	 * @param myTasksLimit - максимальное количество задач
	 * @return
	 */
	public WorkflowTaskListBean getTasks(ScriptNode node, String stateParam, boolean addSubordinatesTask, int myTasksLimit) {
        return getTasks(node, stateParam, addSubordinatesTask, true, myTasksLimit);
    }

    /**
     * Возвращает задачи для документа
     *
     * @param node                  документ, для которого будет возвращены задачи
     * @param stateParam            статус задачи
     * @param addSubordinatesTask   поиск задач подчененных сотрудников
     * @param addMyTasks            поиск задач текущего сотрудника
     * @param myTasksLimit          максимальное количество задач
     * @return
     */
    public WorkflowTaskListBean getTasks(ScriptNode node, String stateParam, boolean addSubordinatesTask, boolean addMyTasks, int myTasksLimit) {
		if (node == null) {
			return new WorkflowTaskListBean();
		}

		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		if (currentEmployee == null) {
			return new WorkflowTaskListBean();
		}

		final NodeRef nodeRef = node.getNodeRef();
        String presentString = getDocumentPresentString(nodeRef);
		BPMState state = BPMState.getValue(stateParam);

		List<WorkflowTask> tasks = new ArrayList<>();
		if (state == BPMState.ACTIVE || state == BPMState.ALL) {
			tasks.addAll(AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<List<WorkflowTask>>() {
				@Override
				public List<WorkflowTask> doWork() throws Exception {
					return stateMachineHelper.getDocumentTasks(nodeRef, true);
				}
			}));
		}

		if (state == BPMState.COMPLETED || state == BPMState.ALL) {
			tasks.addAll(stateMachineHelper.getDocumentTasks(nodeRef, false));
		}

		WorkflowTaskListBean result = new WorkflowTaskListBean();

		boolean isBoss = orgstructureService.isBoss(currentEmployee);
		result.setShowSubordinateTasks(isBoss);

        if (addMyTasks) {
            List<WorkflowTask> myTasks = stateMachineHelper.filterTasksByAssignees(tasks, Collections.singletonList(currentEmployee));
            result.setMyTasks(myTasks, myTasksLimit);

            for (WorkflowTaskBean task : result.getMyTasks()) {
                task.setDocumentPresentString(presentString);
            }
        }

        if (addSubordinatesTask) {
			List<NodeRef> subordinateEmployees = orgstructureService.getBossSubordinate(currentEmployee);
			List<WorkflowTask> subordinatesTasks = stateMachineHelper.filterTasksByAssignees(tasks, subordinateEmployees);
			result.setSubordinatesTasks(subordinatesTasks);

			for (WorkflowTaskBean task : result.getSubordinateTasks()) {
				task.setDocumentPresentString(presentString);
			}
		}

		return result;
	}

	/**
	 * Возвращает процессы для документа
	 *
	 * @param node - nodeRef документа
	 * @param stateParam состояние заправиваемых процессов
	 * @param activeWorkflowsLimit максимальное количество возвращаемых процессов
	 * @return
	 */
	public WorkflowListBean getWorkflows(ScriptNode node, String stateParam, int activeWorkflowsLimit) {
		if (node == null) {
			return new WorkflowListBean();
		}

		NodeRef nodeRef = node.getNodeRef();
		BPMState state = BPMState.getValue(stateParam);
		WorkflowListBean result = new WorkflowListBean();

		List<WorkflowInstance> activeWorkflows = stateMachineHelper.getDocumentWorkflows(nodeRef, true);
		result.setActiveWorkflows(activeWorkflows, activeWorkflowsLimit);

		if (state == BPMState.ALL) {
			List<WorkflowInstance> completedWorkflows = stateMachineHelper.getDocumentWorkflows(nodeRef, false);
			result.setCompletedWorkflows(completedWorkflows);
		}

		return result;
	}

	/**
	 * Проверка на "Только для чтения" для категории вложения
	 *
	 * @param node - документ
	 * @param category - категория
	 * @return
	 */
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
	 *
	 * @param node
	 * @return
	 */
	public List<WorkflowTask> getDocumentTasks(ScriptNode node) {
		return stateMachineHelper.getDocumentTasks(node.getNodeRef(), true);
	}

	/**
	 * Возвращает список активных рабочих процессов
	 *
	 * @param node
	 * @return
	 */
	public List<WorkflowInstance> getDocumentWorkflows(ScriptNode node) {
		return stateMachineHelper.getDocumentWorkflows(node.getNodeRef(), true);
	}

	/**
	 * Проверка наличия машины состояний у документа
	 *
	 * @param node
	 * @return
	 */
	public boolean hasStatemachine(ScriptNode node) {
		return stateMachineHelper.hasStatemachine(node.getNodeRef());
	}

	/**
	 * Возвращает может ли текущий сотрудник создавать документ определенного типа
	 *
	 * @param type
	 * @return
	 */
	public boolean isStarter(String type) {
		return stateMachineHelper.isStarter(type);
	}

	public ru.it.lecm.statemachine.StateFields getStateFields(ScriptNode node) {
		return stateMachineHelper.getStateFields(node.getNodeRef());
	}
	
	/**
	 * Проверка документа на черновой статус
	 *
	 * @param node
	 * @return
	 */
	public boolean isDraft(ScriptNode node) {
		return stateMachineHelper.isDraft(node.getNodeRef());
	}

	/**
	 * Возвращает статус для документа
	 *
	 * @param document
	 * @return
	 */
//	public String getDocumentStatus(ScriptNode document) {
//		return stateMachineHelper.getDocumentStatus(document.getNodeRef());
//	}

	/**
	 * Выполнение действия по его идентификатору
	 *
	 * @param document
	 * @param actionId
	 * @return
	 */
//	public TransitionResponse executeAction(ScriptNode document, String actionId) {
//		return stateMachineHelper.executeUserAction(document.getNodeRef(), actionId);
//	}

	/**
	 * Выполнение действия по его имени
	 *
	 * @param document
	 * @param actionName
	 * @return
	 */
//	public TransitionResponse executeActionByName(ScriptNode document, String actionName) {
//		return stateMachineHelper.executeActionByName(document.getNodeRef(), actionName);
//	}

	/**
	 * Получение списка статусов для документа
	 *
	 * @param documentType
	 * @param includeActive
	 * @param includeFinal
	 * @return
	 */
	public String[] getStatuses(String documentType, boolean includeActive, boolean includeFinal) {
		logger.debug("!!!!!!! StatemachineWebScriptBean getStatuses");
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

	/**
	 * Получение списка динамических ролей для документа
	 *
	 * @param document документ
	 * @return список динамических ролей
	 */
	public String[] getDynamicRoles(ScriptNode document) {
		Set<String> results = new HashSet<>();
		if (document != null) {
			results.addAll(stateMachineHelper.getAllDynamicRoles(document.getNodeRef()));
		}
		return results.toArray(new String[results.size()]);
	}

	/**
	 * Получение папкок с архивными документами
	 *
	 * @param documentType
	 * @return
	 */
	public String[] getArchiveFolders(String documentType) {
		Set<String> folders = stateMachineHelper.getArchiveFolders(documentType);
		return folders.toArray(new String[folders.size()]);
	}

	/**
	 * Получение строки представления для документа
	 *
	 * @param document
	 * @return
	 */
	public String getDocumentPresentString(NodeRef document) {
		return (String) serviceRegistry.getNodeService().getProperty(document, QName.createQName("http://www.it.ru/logicECM/document/1.0", "present-string"));
	}

	private Collection<String> convertToJavaCollection(Object privileges) {
		HashSet<String> result = new HashSet<String>();
		if (privileges instanceof String) {
			result.add((String) privileges);
		}
		if (privileges instanceof ScriptableObject) {
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
	 *
	 * @param nodeRef
	 * @return
	 */
	public boolean isFinal(String nodeRef) {
		return stateMachineHelper.isFinal(new NodeRef(nodeRef));
	}

	/**
	 * Возвращает номер процесса машины состояний, по которому запущен документ
	 *
	 * @param node
	 * @return
	 */
	public String getStatemachineId(ScriptNode node) {
		return stateMachineHelper.getStatemachineId(node.getNodeRef());
	}

	/**
	 * Возвращает номер версии машины состояний
	 *
	 * @param node
	 * @return
	 */
	public String getStatemachineVersion(ScriptNode node) {
		return stateMachineHelper.getStatemachineVersion(node.getNodeRef());
	}

	/**
	 * Выдача сотруднику динамической роли и привелегии согласно текущему статусу документа
	 *
	 * @param document документ
	 * @param employee сотрудник
	 * @param roleName имя роли
	 * @return
	 */
	public boolean grandDynamicRoleForEmployee(ScriptNode document, ScriptNode employee, String roleName) {
		return stateMachineHelper.grandDynamicRoleForEmployee(document.getNodeRef(), employee.getNodeRef(), roleName);
	}

	/**
	 * Выдача сотруднику динамической роли и привелегии согласно текущему статусу документа
	 *
	 * @param document документ
	 * @param employee сотрудник
	 * @param roleName имя роли
	 * @param task
	 * @return
	 */
	public boolean grandDynamicRoleForEmployee(ScriptNode document, ScriptNode employee, String roleName, Task task) {
		return stateMachineHelper.grandDynamicRoleForEmployee(document.getNodeRef(), employee.getNodeRef(), roleName, task);
	}

	/**
	 * Возвращает true, если поле возможно редактировать
	 *
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

	/**
	 * Выполнение дейсвтвия по перходу в следующий статус
	 *
	 * @param document
	 * @param actionName
	 */
	public void executeTransitionAction(ScriptNode document, String actionName) {
		stateMachineHelper.executeTransitionAction(document.getNodeRef(), actionName);
	}

	/**
	 * Выполнение дейсвтвия по перходу в следующий статус
	 *
	 * @param document
	 * @param actionName
	 * @param task
	 */
//	public void executeTransitionAction(ScriptNode document, String actionName, Task task) {
//		stateMachineHelper.executeTransitionAction(document.getNodeRef(), actionName, task);
//	}

	/**
	 * Принудительное завершение процесса
	 *
	 * @param document
	 * @param definition
	 * @param variable
	 * @param value
	 */
	public void terminateWorkflowsByDefinition(final ScriptNode document, final String definition, final String variable, final Object value) {
		ArrayList<String> definitions = new ArrayList<String>();
		definitions.add(definition);
		stateMachineHelper.terminateWorkflowsByDefinitionId(document.getNodeRef(), definitions, variable, value);
	}

	public void connectToStatemachine(final ScriptNode document, final String processInstanceID, final String processDefinitionID) {
		stateMachineHelper.connectToStatemachine(document.getNodeRef(), processInstanceID, processDefinitionID);
	}

	public void disconnectFromStatemachine(final ScriptNode document, final String processInstanceID) {
		stateMachineHelper.disconnectFromStatemachine(document.getNodeRef(), processInstanceID);
	}
	
	public String getDefaultStatemachinePath(String statemachine) {
		return defaultStatemachines.getPath(statemachine);
	}
}
