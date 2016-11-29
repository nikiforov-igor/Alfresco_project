package ru.it.lecm.statemachine;

import org.activiti.engine.task.Task;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;

/**
 * Created by pmelnikov on 08.04.2015.
 *
 * Класс работы с документами без жизненного цикла
 */
public class SimpleStatemachineHelper extends LifecycleStateMachineHelper {

    final private static Logger logger = LoggerFactory.getLogger(SimpleStatemachineHelper.class);
	
	private PermissionService permissionService;

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

    @Override
    public boolean isReadOnlyCategory(NodeRef document, String category) {
        return false;
    }

    protected IllegalStateException createNotImplementedException() {
        IllegalStateException ex = new IllegalStateException("This method is not implemented yet.");
        logger.error(ex.getMessage(), ex);
        return ex;
    }
	
	private void rebuildACL(String type, NodeRef typeRoot) {
		Map<String, String> permissions = getPermissions(type);
		Map<String, LecmPermissionService.LecmPermissionGroup> permissionGroups = new HashMap<>();
		for (Map.Entry<String, String> role : permissions.entrySet()) {
			LecmPermissionService.LecmPermissionGroup permissionGroup = lecmPermissionService.findPermissionGroup(role.getValue());
			if (permissionGroup != null) {
				permissionGroups.put(role.getKey(), permissionGroup);
			}
		}
		
		Set<AccessPermission> allowedPermissions = permissionService.getAllSetPermissions(typeRoot);
		for (AccessPermission permission : allowedPermissions) {
			if (permission.isSetDirectly() && permission.getAuthority().startsWith(PermissionService.GROUP_PREFIX + Types.PFX_LECM)) {
				permissionService.deletePermission(typeRoot, permission.getAuthority(), permission.getPermission());
			}
		}

		if (!permissionGroups.isEmpty()) {
			lecmPermissionService.rebuildStaticACL(typeRoot, permissionGroups);
		}
	}

    @Override
    public boolean hasActiveStatemachine(NodeRef document) {		
        throw createNotImplementedException();
    }

    @Override
    public String getCurrentTaskId(String executionId) {
        throw createNotImplementedException();
    }

    @Override
    public boolean isDraft(NodeRef document) {
        return false;
    }

    @Override
    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef, boolean isActive) {
        throw createNotImplementedException();
    }

    @Override
    public NodeRef getTaskDocument(WorkflowTask task, List<String> documentTypes) {
        throw createNotImplementedException();
    }

    @Override
    public List<WorkflowTask> getDocumentTasks(NodeRef documentRef, boolean activeTasks) {
        throw createNotImplementedException();
    }

    @Override
    public List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal) {
        throw createNotImplementedException();
    }

    @Override
    public List<String> getAllDynamicRoles(NodeRef document) {
        return null;
    }

    @Override
    public boolean isFinal(NodeRef document) {
        return true;
    }

    @Override
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName) {
        throw createNotImplementedException();
    }

    @Override
    public String getPreviousStatusName(NodeRef document) {
        throw createNotImplementedException();
    }

    @Override
    public String getPreviousStatusNameOnTake(NodeRef document) {
        throw createNotImplementedException();
    }

    @Override
    public List<String> getPreviousStatusesNames(NodeRef document) {
        throw createNotImplementedException();
    }

    @Override
    public String getStatemachineId(NodeRef document) {
        return "Не запущен";
    }

    @Override
    public Map<String, Object> getVariables(String executionId) {
        throw createNotImplementedException();
    }

    @Override
    public void checkReadOnlyCategory(NodeRef document, String category) {
    }

    @Override
    public boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority) {
        throw createNotImplementedException();
    }

    @Override
    public boolean setTaskAssignee(NodeRef documentRef, String taskId, String beforeAuthority, String afterAuthority) {
        throw createNotImplementedException();
    }

    @Override
    public boolean setWorkflowTaskProperty(NodeRef documentRef, String workflowTaskId, QName propertyName, Serializable propertyValue) {
        throw createNotImplementedException();
    }

    @Override
    public StateFields getStateFields(NodeRef document) {
        return new StateFields(false);
    }

    @Override
    public boolean isEditableField(NodeRef document, String field) {
        throw createNotImplementedException();
    }

    @Override
    public boolean hasStatemachine(NodeRef document) {
        return true;
    }

    @Override
    public String getStatemachineVersion(NodeRef document) {
        return "N/A";
    }

    @Override
    public void executeTransitionAction(NodeRef document, String actionName) {
        throw createNotImplementedException();
    }

    @Override
    public void executeTransitionAction(NodeRef document, String actionName, String persistedResponse) {
        throw createNotImplementedException();
    }

    @Override
    public List<WorkflowTask> filterTasksByAssignees(List<WorkflowTask> tasks, List<NodeRef> assigneesEmployees) {
        throw createNotImplementedException();
    }

    @Override
    public List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser) {
        throw createNotImplementedException();
    }

    @Override
    public void terminateProcess(String processId) {
        throw createNotImplementedException();
    }

    @Override
    public void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value) {
        throw createNotImplementedException();
    }

    @Override
    public List<NodeRef> getDocumentsWithActiveTasks(String employeeLogin, Set<String> workflowIds, Integer remainingDays) {
        throw createNotImplementedException();
    }

    @Override
    public List<NodeRef> getDocumentsWithFinishedTasks(List<NodeRef> documents, String employeeLogin, Set<String> taskNames) {
        throw createNotImplementedException();
    }

    @Override
    public void sendSignal(String executionId) {
        throw createNotImplementedException();
    }

    @Override
    public boolean isServiceWorkflow(WorkflowInstance workflow) {
        throw createNotImplementedException();
    }

    @Override
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName, Task task) {
        throw createNotImplementedException();
    }

    @Override
    public Set<String> getArchiveFolders(String documentType) {
		return new HashSet<>();
	}
		
    @Override
    public void resetStateMachene() {
        throw createNotImplementedException();
    }

    @Override
    public void connectToStatemachine(NodeRef documentRef, String processInstanceID, String processDefinitionID) {
        throw createNotImplementedException();
    }

    @Override
    public void disconnectFromStatemachine(NodeRef documentRef, String processInstanceID) {
        throw createNotImplementedException();
    }

	@Override
	public void checkArchiveFolder(String type, boolean forceRebuildACL) {
		String pathStr = "Документы без МС";
		String archiveFolder = getArchiveFolder(type);
		boolean rebuildACLRequired = forceRebuildACL;

		if (archiveFolder != null && !archiveFolder.isEmpty()) {
			String[] storePath = archiveFolder.split("/");
			for (String pathItem : storePath) {
				if (!"".equals(pathItem)) {
					pathStr = pathItem;
					break;
				}
			}
		}

		NodeRef typeRoot = getFolder(repositoryHelper.getCompanyHome(), pathStr);
		if (typeRoot == null) {
			String user = AuthenticationUtil.getFullyAuthenticatedUser();
			AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
			try {
				List<String> paths = new ArrayList<>();
				paths.add(pathStr);
				typeRoot = createPath(repositoryHelper.getCompanyHome(), paths);
				
				rebuildACLRequired = true;
			} catch (WriteTransactionNeededException ex) {
				logger.error("Failed to create Simple Document folder due lack of RW transaction", ex);
			} finally {
				AuthenticationUtil.setFullyAuthenticatedUser(user);
			}
		}

		if (rebuildACLRequired) {
			rebuildACL(type, typeRoot);
		}
	}

	@Override
	public void afterPropertiesSet() {
		// DO NOTHING
	}	
}
