package ru.it.lecm.statemachine;

import org.activiti.engine.task.Task;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pmelnikov on 08.04.2015.
 *
 * Проксирующий класс для работы с документами с ЖЦ и без ЖЦ
 */
public class StatemachineHelperProxy implements StateMachineServiceBean {

    private SimpleStatemachineHelper simpleStatemachineHelper;
    private LifecycleStateMachineHelper lifecycleStateMachineHelper;
    private NamespaceService namespaceService;
    private NodeService nodeService;

    public void setSimpleStatemachineHelper(SimpleStatemachineHelper simpleStatemachineHelper) {
        this.simpleStatemachineHelper = simpleStatemachineHelper;
    }

    public void setLifecycleStateMachineHelper(LifecycleStateMachineHelper lifecycleStateMachineHelper) {
        this.lifecycleStateMachineHelper = lifecycleStateMachineHelper;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public boolean isReadOnlyCategory(NodeRef document, String category) {
        return getHelper(document).isReadOnlyCategory(document, category);
    }

    @Override
    public boolean hasActiveStatemachine(NodeRef document) {
        return getHelper(document).hasActiveStatemachine(document);
    }

    @Override
    public String getCurrentTaskId(String executionId) {
        return lifecycleStateMachineHelper.getCurrentTaskId(executionId);
    }

    @Override
    public boolean isDraft(NodeRef document) {
        return getHelper(document).isDraft(document);
    }

    @Override
    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef, boolean isActive) {
        return lifecycleStateMachineHelper.getDocumentWorkflows(nodeRef, isActive);
    }

    @Override
    public boolean isStarter(String type) {
        return getHelper(type).isStarter(type);
    }

	@Override
	public boolean isStarter(QName type) {
		return getHelper(type).isStarter(type);
	}

    @Override
    public NodeRef getTaskDocument(WorkflowTask task, List<String> documentTypes) {
        return lifecycleStateMachineHelper.getTaskDocument(task, documentTypes);
    }

    @Override
    public List<WorkflowTask> getDocumentTasks(NodeRef documentRef, boolean activeTasks) {
        return getHelper(documentRef).getDocumentTasks(documentRef, activeTasks);
    }

    @Override
    public boolean isNotArmCreate(String type) {
        return getHelper(type).isNotArmCreate(type);
    }

	@Override
	public boolean isNotArmCreate(QName type) {
		return getHelper(type).isNotArmCreate(type);
	}

    @Override
    public List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal) {
        return getHelper(documentType).getStatuses(documentType, includeActive, includeFinal);
    }

	@Override
	public List<String> getStatuses(QName documentType, boolean includeActive, boolean includeFinal) {
		return getHelper(documentType).getStatuses(documentType, includeActive, includeFinal);
	}

    @Override
    public List<String> getAllDynamicRoles(NodeRef document) {
        return lifecycleStateMachineHelper.getAllDynamicRoles(document);
    }

    @Override
    public boolean isFinal(NodeRef document) {
        return getHelper(document).isFinal(document);
    }

    @Override
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName) {
        return getHelper(document).grandDynamicRoleForEmployee(document, employee, roleName);
    }

    @Override
    public String getPreviousStatusName(NodeRef document) {
        return getHelper(document).getPreviousStatusName(document);
    }

    @Override
    public String getPreviousStatusNameOnTake(NodeRef document) {
        return getHelper(document).getPreviousStatusNameOnTake(document);
    }

    @Override
    public List<String> getPreviousStatusesNames(NodeRef document) {
        return getHelper(document).getPreviousStatusesNames(document);
    }

    @Override
    public String getStatemachineId(NodeRef document) {
        return getHelper(document).getStatemachineId(document);
    }

    @Override
    public Map<String, Object> getVariables(String executionId) {
        return lifecycleStateMachineHelper.getVariables(executionId);
    }

    @Override
    public Set<String> getStarterRoles(String documentType) {
        return getHelper(documentType).getStarterRoles(documentType);
    }

	@Override
	public Set<String> getStarterRoles(QName documentType) {
		return getHelper(documentType).getStarterRoles(documentType);
	}

    @Override
    public void checkReadOnlyCategory(NodeRef document, String category) {
        getHelper(document).checkReadOnlyCategory(document, category);
    }

    @Override
    public boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority) {
        return getHelper(documentRef).transferRightTask(documentRef, beforeAuthority, afterAuthority);
    }

    @Override
    public boolean setTaskAssignee(NodeRef documentRef, String taskId, String beforeAuthority, String afterAuthority) {
        return getHelper(documentRef).setTaskAssignee(documentRef, taskId, beforeAuthority, afterAuthority);
    }

    @Override
    public boolean setWorkflowTaskProperty(NodeRef documentRef, String workflowTaskId, QName propertyName, Serializable propertyValue) {
        return getHelper(documentRef).setWorkflowTaskProperty(documentRef, workflowTaskId, propertyName, propertyValue);
    }

    @Override
    public StateFields getStateFields(NodeRef document) {
        return getHelper(document).getStateFields(document);
    }

    @Override
    public boolean isEditableField(NodeRef document, String field) {
        return getHelper(document).isEditableField(document, field);
    }

    @Override
    public boolean hasStatemachine(NodeRef document) {
        return getHelper(document).hasStatemachine(document);
    }

    @Override
    public String getStatemachineVersion(NodeRef document) {
        return getHelper(document).getStatemachineVersion(document);
    }

    @Override
    public void executeTransitionAction(NodeRef document, String actionName) {
        getHelper(document).executeTransitionAction(document, actionName);
    }

    @Override
    public void executeTransitionAction(NodeRef document, String actionName, String persistedResponse) {
        getHelper(document).executeTransitionAction(document, actionName, persistedResponse);
    }

    @Override
    public List<WorkflowTask> filterTasksByAssignees(List<WorkflowTask> tasks, List<NodeRef> assigneesEmployees) {
        return lifecycleStateMachineHelper.filterTasksByAssignees(tasks, assigneesEmployees);
    }

    @Override
    public List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser) {
        return lifecycleStateMachineHelper.getDocumentsTasks(documentTypes, fullyAuthenticatedUser);
    }

    @Override
    public void terminateProcess(String processId) {
        lifecycleStateMachineHelper.terminateProcess(processId);
    }

    @Override
    public void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value) {
        getHelper(document).terminateWorkflowsByDefinitionId(document, definitionIds, variable, value);
    }

    @Override
    public List<NodeRef> getDocumentsWithActiveTasks(String employeeLogin, Set<String> workflowIds, Integer remainingDays) {
        return lifecycleStateMachineHelper.getDocumentsWithActiveTasks(employeeLogin, workflowIds, remainingDays);
    }

    @Override
    public List<NodeRef> getDocumentsWithFinishedTasks(List<NodeRef> documents, String employeeLogin, Set<String> taskNames) {
        return lifecycleStateMachineHelper.getDocumentsWithFinishedTasks(documents, employeeLogin, taskNames);
    }

    @Override
    public void sendSignal(String executionId) {
        lifecycleStateMachineHelper.sendSignal(executionId);
    }

    @Override
    public boolean isServiceWorkflow(WorkflowInstance workflow) {
        return lifecycleStateMachineHelper.isServiceWorkflow(workflow);
    }

    @Override
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName, Task task) {
        return getHelper(document).grandDynamicRoleForEmployee(document, employee, roleName, task);
    }

    @Override
    public Set<String> getArchiveFolders(String documentType) {
        return getHelper(documentType).getArchiveFolders(documentType);
    }

	@Override
	public Set<String> getArchiveFolders(QName documentType) {
		getHelper(documentType).getArchiveFolders(documentType);
	}

	@Override
	public String getArchiveFolder(String documentType) {
		return getHelper(documentType).getArchiveFolder(documentType);
	}

	@Override
	public String getArchiveFolder(QName documentType) {
		return getHelper(documentType).getArchiveFolder(documentType);
	}

    @Override
    public void resetStateMachene() {
        lifecycleStateMachineHelper.resetStateMachene();
    }

    @Override
    public void connectToStatemachine(NodeRef documentRef, String processInstanceID, String processDefinitionID) {
        getHelper(documentRef).connectToStatemachine(documentRef, processInstanceID, processDefinitionID);
    }

    @Override
    public void disconnectFromStatemachine(NodeRef documentRef, String processInstanceID) {
        getHelper(documentRef).disconnectFromStatemachine(documentRef, processInstanceID);
    }

	@Override
	public Map<String, String> getPermissions(String type) {
		return lifecycleStateMachineHelper.getPermissions(type);
	}

	@Override
	public Map<String, String> getPermissions(QName type) {
		return getHelper(type).getPermissions(type);
	}

	@Override
	public boolean isSimpleDocument(String type) {
		return lifecycleStateMachineHelper.isSimpleDocument(type);
	}

	@Override
	public boolean isSimpleDocument(QName type) {
		return lifecycleStateMachineHelper.isSimpleDocument(type);
	}

    private StateMachineServiceBean getHelper(String type) {
        return getHelper(QName.createQName(type, namespaceService));
    }

    private StateMachineServiceBean getHelper(NodeRef document) {
        return getHelper(nodeService.getType(document));
    }

    private StateMachineServiceBean getHelper(QName type) {
        if (lifecycleStateMachineHelper.isSimpleDocument(type.toPrefixString(namespaceService))) {
            return simpleStatemachineHelper;
        } else {
			return lifecycleStateMachineHelper;
		}
    }

	@Override
	public void checkArchiveFolder(String type, boolean forceRebuildACL) {
		getHelper(type).checkArchiveFolder(type, forceRebuildACL);
	}

	@Override
	public void checkArchiveFolder(QName type, boolean forceRebuildACL) {
		getHelper(type).checkArchiveFolder(type, forceRebuildACL);
	}
	
}
