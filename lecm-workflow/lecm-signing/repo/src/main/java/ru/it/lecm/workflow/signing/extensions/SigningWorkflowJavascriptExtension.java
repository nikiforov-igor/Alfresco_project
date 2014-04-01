package ru.it.lecm.workflow.signing.extensions;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.RouteAspecsModel;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.api.WorkflowResultListService;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;
import ru.it.lecm.workflow.signing.api.SigningWorkflowModel;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author vmalygin
 */
public class SigningWorkflowJavascriptExtension extends BaseWebScript {

	/**
	 * переменная регламента в которой хранится идентификатор
	 * бизнес роли, через которую будут находится исполнители регламента с учетом делегирования
	 */
	private final static String WORKFLOW_ROLE = "workflowRole";

	private NodeService nodeService;
	private SigningWorkflowService signingWorkflowService;
	private WorkflowAssigneesListService workflowAssigneesListService;
	private OrgstructureBean orgstructureBean;
	private WorkflowResultListService workflowResultListService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSigningWorkflowService(final SigningWorkflowService signingWorkflowService) {
		this.signingWorkflowService = signingWorkflowService;
	}

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public void setWorkflowResultListService(WorkflowResultListService workflowResultListService) {
		this.workflowResultListService = workflowResultListService;
	}

	public void deleteTempAssigneesList(final DelegateExecution execution) {
		workflowAssigneesListService.deleteAssigneesListWorkingCopy(execution);
	}

	public ActivitiScriptNodeList createAssigneesList(final ActivitiScriptNode assigneesListNode, final DelegateExecution execution) {
		List<NodeRef> assigneesList = workflowAssigneesListService.createAssigneesListWorkingCopy(assigneesListNode.getNodeRef(), execution);
		//у нас есть рабочая копия списка участников процесса подписания
		//нам надо пробежаться по участникам этого списка, через сервис делегирования найти актуальных исполнителей
		//актуализировать ассоциацию на сотрудника и userName
		String workflowRole = (String) execution.getVariable(WORKFLOW_ROLE);
		assigneesList = workflowAssigneesListService.actualizeAssigneesUsingDelegation(assigneesList, workflowRole);
		ActivitiScriptNodeList assigneesActivitiList = new ActivitiScriptNodeList();
		for (NodeRef assigneeNode : assigneesList) {
			assigneesActivitiList.add(new ActivitiScriptNode(assigneeNode, serviceRegistry));
		}
		return assigneesActivitiList;
	}

	public void assignTask(final ActivitiScriptNode assignee, final DelegateTask task) {
		signingWorkflowService.assignTask(assignee.getNodeRef(), task);
	}

	public WorkflowTaskDecision completeTask(final ActivitiScriptNode assignee, final DelegateTask task) {
		return signingWorkflowService.completeTask(assignee.getNodeRef(), task);
	}

	public void notifyDeadlineTasks(final String processInstanceId, final ActivitiScriptNode bpmPackage, final VariableScope variableScope) {
		//написать реализацию в SigningWorkflowService
		signingWorkflowService.notifyAssigneesDeadline(processInstanceId, bpmPackage.getNodeRef());
		signingWorkflowService.notifyInitiatorDeadline(processInstanceId, bpmPackage.getNodeRef(), variableScope);
	}

	public String getFinalDecision(final Map<String, String> decisionMap) {
		String finalDecision;
		if (decisionMap.containsValue("REJECTED")) {
			finalDecision = "REJECTED";
		} else if (decisionMap.containsValue("SIGNED")) {
			finalDecision = "SIGNED";
		} else {
			finalDecision = "NO_DECISION";
		}
		return finalDecision;
	}

	public void notifySigningFinished(final String decision, final ActivitiScriptNode bpmPackage) {
		signingWorkflowService.notifyWorkflowFinished(decision, bpmPackage.getNodeRef());
	}

	public boolean isSigned(final String finalDecision) {
		return "SIGNED".equals(finalDecision);
	}

	public void logFinalDecision(final ActivitiScriptNode resultListRef, final String finalDecision) {
		signingWorkflowService.logFinalDecision(resultListRef.getNodeRef(), finalDecision);
	}

	public void dropSigningResults(final ActivitiScriptNode resultListRef) {
		signingWorkflowService.dropSigningResults(resultListRef.getNodeRef());
	}

	public ActivitiScriptNode createResultList(final ActivitiScriptNode bpmPackage, final String documentAttachmentCategoryName, final ActivitiScriptNodeList assigneesList) {
		return new ActivitiScriptNode(signingWorkflowService.createResultList(bpmPackage.getNodeRef(), documentAttachmentCategoryName, assigneesList.getNodeReferences()), serviceRegistry);
	}

	public void logDecision(final ActivitiScriptNode resultListRef, final WorkflowTaskDecision taskDecision) {
		signingWorkflowService.logDecision(resultListRef.getNodeRef(), taskDecision);
	}

	public void saveRouteApprovalResult(final ActivitiScriptNode bpmPackage, final boolean isApproved) {
		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage.getNodeRef());
		if (nodeService.hasAspect(documentRef, RouteAspecsModel.ASPECT_ROUTABLE)) {
			nodeService.setProperty(documentRef, RouteAspecsModel.PROP_IS_SIGNED, isApproved);
		}
	}

	public ActivitiScriptNode createEmptyResultList(final ActivitiScriptNode bpmPackage, final String documentAttachmentCategoryName) {
		return new ActivitiScriptNode(signingWorkflowService.createResultList(bpmPackage.getNodeRef(), documentAttachmentCategoryName, new ArrayList<NodeRef>()), serviceRegistry);
	}

	public ActivitiScriptNode createResultItem(final ActivitiScriptNode resultListRef, final ScriptNode currentEmployee) {
		String itemTitle = String.format(WorkflowServiceAbstract.RESULT_ITEM_FORMAT, orgstructureBean.getEmployeeLogin(currentEmployee.getNodeRef()));
		return new ActivitiScriptNode(workflowResultListService.createResultItem(resultListRef.getNodeRef(), currentEmployee.getNodeRef(), itemTitle, new Date(), 1, SigningWorkflowModel.TYPE_SIGN_RESULT_ITEM), serviceRegistry);
	}

	public WorkflowTaskDecision getSignedDecision(final ScriptNode currentEmployee) {
		WorkflowTaskDecision decision = new WorkflowTaskDecision();
		decision.setDecision("SIGNED");
		decision.setStartDate(new Date());
		decision.setDueDate(new Date());
		decision.setUserName(orgstructureBean.getEmployeeLogin(currentEmployee.getNodeRef()));

		return decision;
	}

	public void addSignBusinessJournalRecord(final ActivitiScriptNode bpmPackage, final ScriptNode employee) {
		signingWorkflowService.addSignBusinessJournalRecord(bpmPackage.getNodeRef(), employee.getNodeRef());
	}
}
