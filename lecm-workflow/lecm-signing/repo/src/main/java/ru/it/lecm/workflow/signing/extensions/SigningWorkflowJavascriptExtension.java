package ru.it.lecm.workflow.signing.extensions;

import java.util.List;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

/**
 *
 * @author vmalygin
 */
public class SigningWorkflowJavascriptExtension extends BaseWebScript {

	private SigningWorkflowService signingWorkflowService;
	private WorkflowAssigneesListService workflowAssigneesListService;

	public void setSigningWorkflowService(final SigningWorkflowService signingWorkflowService) {
		this.signingWorkflowService = signingWorkflowService;
	}

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	public void deleteTempAssigneesList(final DelegateExecution execution) {
		workflowAssigneesListService.deleteAssigneesListWorkingCopy(execution);
	}

	public ActivitiScriptNodeList createAssigneesList(final ActivitiScriptNode assigneesListNode, final DelegateExecution execution) {
		List<NodeRef> assigneesList = workflowAssigneesListService.createAssigneesListWorkingCopy(assigneesListNode.getNodeRef(), execution);
		ActivitiScriptNodeList assigneesActivitiList = new ActivitiScriptNodeList();
		for (NodeRef assigneeNode: assigneesList) {
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
}
