package ru.it.lecm.workflow.review.extensions;

import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.review.api.ReviewWorkflowService;

/**
 *
 * @author vmalygin
 */
public class ReviewWorkflowJavascriptExtension extends BaseWebScript {

	private ReviewWorkflowService reviewWorkflowService;

	public void setReviewWorkflowService(ReviewWorkflowService reviewWorkflowService) {
		this.reviewWorkflowService = reviewWorkflowService;
	}

	public void deleteAssigneesListWorkingCopy(final DelegateExecution execution) {
		reviewWorkflowService.deleteAssigneesListWorkingCopy(execution);
	}

	public ActivitiScriptNodeList createAssigneesList(final ActivitiScriptNode assigneesListNode, final DelegateExecution execution) {
		List<NodeRef> assigneesList = reviewWorkflowService.createAssigneesList(assigneesListNode.getNodeRef(), execution);
		ActivitiScriptNodeList assigneesActivitiList = new ActivitiScriptNodeList();
		for (NodeRef assigneeNode: assigneesList) {
			assigneesActivitiList.add(new ActivitiScriptNode(assigneeNode, serviceRegistry));
		}
		return assigneesActivitiList;
	}

	public void assignTask(final ActivitiScriptNode assignee, final DelegateTask task) {
		reviewWorkflowService.assignTask(assignee.getNodeRef(), task);
	}

	public void completeTask(final ActivitiScriptNode assignee, final DelegateTask task) {
		reviewWorkflowService.completeTask(assignee.getNodeRef(), task);
	}

	public void notifyDeadlineTasks(final String processInstanceId, final ActivitiScriptNode bpmPackage, final VariableScope variableScope) {
		reviewWorkflowService.notifyAssigneesDeadline(processInstanceId, bpmPackage.getNodeRef());
		reviewWorkflowService.notifyInitiatorDeadline(processInstanceId, bpmPackage.getNodeRef(), variableScope);
	}

	public void logWorkflowFinished(final ActivitiScriptNode resultList) {
		reviewWorkflowService.logWorkflowFinished(resultList.getNodeRef());
	}

	public void notifyWorkflowFinished(final ActivitiScriptNode bpmPackage) {
		reviewWorkflowService.notifyWorkflowFinished("COMPLETED", bpmPackage.getNodeRef());
	}

	public ActivitiScriptNode createResultList(final ActivitiScriptNode bpmPackage, final String documentAttachmentCategoryName, final ActivitiScriptNodeList assigneesList) {
		return new ActivitiScriptNode(reviewWorkflowService.createResultList(bpmPackage.getNodeRef(), documentAttachmentCategoryName, assigneesList.getNodeReferences()), serviceRegistry);
	}

	public void sendBareNotifications(final ActivitiScriptNodeList assigneesList, final Date workflowDueDate, final ActivitiScriptNode bpmPackage) {
		reviewWorkflowService.sendBareNotifications(assigneesList.getNodeReferences(), workflowDueDate, bpmPackage.getNodeRef());
	}

}
