package ru.it.lecm.workflow.signing.extensions;

import java.util.List;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

/**
 *
 * @author vmalygin
 */
public class SigningWorkflowJavascriptExtension extends BaseWebScript {

	private SigningWorkflowService signingWorkflowService;

	public void setSigningWorkflowService(final SigningWorkflowService signingWorkflowService) {
		this.signingWorkflowService = signingWorkflowService;
	}

	public void deleteTempAssigneesList(final DelegateExecution execution) {
		signingWorkflowService.deleteTempAssigneesList(execution);
	}

	public ActivitiScriptNodeList createAssigneesList(final ActivitiScriptNode assigneesListNode, final DelegateExecution execution) {
		List<NodeRef> assigneesList = signingWorkflowService.createAssigneesList(assigneesListNode.getNodeRef(), execution);
		ActivitiScriptNodeList assigneesActivitiList = new ActivitiScriptNodeList();
		for (NodeRef assigneeNode: assigneesList) {
			assigneesActivitiList.add(new ActivitiScriptNode(assigneeNode, serviceRegistry));
		}
		return assigneesActivitiList;
	}

	public void assignTask(final ActivitiScriptNode assignee, final DelegateTask task) {
		signingWorkflowService.assignTask(assignee.getNodeRef(), task);
	}

	public void completeTask(final ActivitiScriptNode assignee, final DelegateTask task) {
		//TODO: signingWorkflowService.completeTask
//		signingWorkflowService.completeTask(assignee.getNodeRef(), task);
	}

	public void notifyDeadlineTasks(final String processInstanceId, final ActivitiScriptNode bpmPackage, final VariableScope variableScope) {
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

	public void notifyFinalDecision(final String decision, final ActivitiScriptNode bpmPackage) {
		//TODO:signingWorkflowService.notifyFinalDecision
//		signingWorkflowService.notifyFinalDecision(decision, bpmPackage.getNodeRef());
	}

	public boolean isSigned(final String finalDecision) {
		return "SIGNED".equals(finalDecision);
	}
}
