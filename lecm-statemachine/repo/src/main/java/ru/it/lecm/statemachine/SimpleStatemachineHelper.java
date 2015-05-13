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

    @Override
    public boolean hasActiveStatemachine(NodeRef document) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public String getCurrentTaskId(String executionId) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public boolean isDraft(NodeRef document) {
        return false;
    }

    @Override
    public List<WorkflowInstance> getDocumentWorkflows(NodeRef nodeRef, boolean isActive) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public boolean isStarter(String type) {
        QName typeQName = QName.createQName(type, serviceRegistry.getNamespaceService());
        SimpleDocumentRegistryItem item = simpleDocumentRegistry.getRegistryItem(typeQName);
        List<String> accessRoles = item.getStarters();
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
            if (auth.contains("GROUP__LECM$BR%" + accessRole)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NodeRef getTaskDocument(WorkflowTask task, List<String> documentTypes) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public List<WorkflowTask> getDocumentTasks(NodeRef documentRef, boolean activeTasks) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public boolean isNotArmCreate(String type) {
        QName typeQName = QName.createQName(type, serviceRegistry.getNamespaceService());
        return simpleDocumentRegistry.getRegistryItem(typeQName).isNotArmCreated();
    }

    @Override
    public List<String> getStatuses(String documentType, boolean includeActive, boolean includeFinal) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public boolean isFinal(NodeRef document) {
        return true;
    }

    @Override
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public String getPreviousStatusName(NodeRef document) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public String getPreviousStatusNameOnTake(NodeRef document) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public List<String> getPreviousStatusesNames(NodeRef document) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public String getStatemachineId(NodeRef document) {
        return "Не запущен";
    }

    @Override
    public Map<String, Object> getVariables(String executionId) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public Set<String> getStarterRoles(String documentType) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public void checkReadOnlyCategory(NodeRef document, String category) {
    }

    @Override
    public boolean transferRightTask(NodeRef documentRef, String beforeAuthority, String afterAuthority) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public StateFields getStateFields(NodeRef document) {
        return new StateFields(false);
    }

    @Override
    public boolean isEditableField(NodeRef document, String field) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
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
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public List<WorkflowTask> filterTasksByAssignees(List<WorkflowTask> tasks, List<NodeRef> assigneesEmployees) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public List<WorkflowTask> getDocumentsTasks(List<String> documentTypes, String fullyAuthenticatedUser) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public void terminateProcess(String processId) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public void terminateWorkflowsByDefinitionId(NodeRef document, List<String> definitionIds, String variable, Object value) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public List<NodeRef> getDocumentsWithActiveTasks(String employeeLogin, Set<String> workflowIds, Integer remainingDays) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public void sendSignal(String executionId) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public boolean isServiceWorkflow(WorkflowInstance workflow) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public boolean grandDynamicRoleForEmployee(NodeRef document, NodeRef employee, String roleName, Task task) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public Set<String> getArchiveFolders(String documentType) {
        return new HashSet<>();
    }

    @Override
    public void resetStateMachene() {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public void connectToStatemachine(NodeRef documentRef, String processInstanceID, String processDefinitionID) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

    @Override
    public void disconnectFromStatemachine(NodeRef documentRef, String processInstanceID) {
        logger.warn("This method is not implemented yet.");
        throw new IllegalStateException("This method is not implemented yet.");
    }

}
