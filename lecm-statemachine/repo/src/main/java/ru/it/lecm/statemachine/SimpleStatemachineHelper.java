package ru.it.lecm.statemachine;

import org.activiti.engine.task.Task;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.bean.SimpleDocumentRegistryImpl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pmelnikov on 08.04.2015.
 *
 * Класс работы с документами без жизненного цикла
 */
public class SimpleStatemachineHelper implements StateMachineServiceBean {

    final private static Logger logger = LoggerFactory.getLogger(SimpleStatemachineHelper.class);

    private SimpleDocumentRegistryImpl simpleDocumentRegistry;
    private ServiceRegistry serviceRegistry;
    private OrgstructureBean orgstructureBean;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setSimpleDocumentRegistry(SimpleDocumentRegistryImpl simpleDocumentRegistry) {
        this.simpleDocumentRegistry = simpleDocumentRegistry;
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
    public boolean isStarter(String type) {
        Set<String> accessRoles = getStarterRoles(type);
        NodeRef employee = orgstructureBean.getCurrentEmployee();
        final String employeeLogin = orgstructureBean.getEmployeeLogin(employee);
        @SuppressWarnings("unchecked")
        Set<String> auth = (Set<String>) AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                return serviceRegistry.getAuthorityService().getAuthoritiesForUser(employeeLogin);
            }
        });
        for (String accessRole : accessRoles) {
            if (auth.contains("GROUP__LECM$BR!" + accessRole)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getStarterRoles(String documentType) {
        Set<String> result = new HashSet<>();
        QName typeQName = QName.createQName(documentType, serviceRegistry.getNamespaceService());
        SimpleDocumentRegistryItem item = simpleDocumentRegistry.getRegistryItem(typeQName);
        result.addAll(item.getStarters());
        return result;
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
    public boolean isNotArmCreate(String type) {
        QName typeQName = QName.createQName(type, serviceRegistry.getNamespaceService());
        return simpleDocumentRegistry.getRegistryItem(typeQName).isNotArmCreated();
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

}
