package ru.it.lecm.workflow.review.extensions;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.workflow.review.api.ReviewWorkflowService;

import java.util.Date;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.util.ParameterCheck;
import ru.it.lecm.workflow.review.ReviewServiceImpl;

/**
 *
 * @author vmalygin
 */
public class ReviewWorkflowJavascriptExtension extends BaseWebScript {

    private ReviewWorkflowService reviewWorkflowService;

    private ReviewServiceImpl reviewServiceImpl;

    public ReviewServiceImpl getReviewServiceImpl() {
        return reviewServiceImpl;
    }

    public void setReviewServiceImpl(ReviewServiceImpl reviewServiceImpl) {
        this.reviewServiceImpl = reviewServiceImpl;
    }

    public void setReviewWorkflowService(ReviewWorkflowService reviewWorkflowService) {
        this.reviewWorkflowService = reviewWorkflowService;
    }

    public void deleteAssigneesListWorkingCopy(final DelegateExecution execution) {
        reviewWorkflowService.deleteAssigneesListWorkingCopy(execution);
    }

    public ActivitiScriptNodeList createAssigneesList(final ActivitiScriptNode assigneesListNode, final DelegateExecution execution) {
        List<NodeRef> assigneesList = reviewWorkflowService.createAssigneesList(assigneesListNode.getNodeRef(), execution);
        ActivitiScriptNodeList assigneesActivitiList = new ActivitiScriptNodeList();
        for (NodeRef assigneeNode : assigneesList) {
            assigneesActivitiList.add(new ActivitiScriptNode(assigneeNode, serviceRegistry));
        }
        return assigneesActivitiList;
    }

    public void assignTask(final ActivitiScriptNode assignee, final DelegateTask task) {
        NodeRef employeeRef = assignee.getNodeRef();
        //		TODO: Метод assignTask через несколько уровней вызывает getDelegationOpts,
//		который ранее был getOrCreate, поэтому необходимо сделать проверку на существование
//		и при необходимости создать
//              delegationOpts проверяется/создаётся при создании/изменении сотрудника, так что здесь проверять особой необходимости нет.                
//		if(delegationService.getDelegationOpts(employeeRef) == null) {
//			delegationService.createDelegationOpts(employeeRef);
//		}
        try {
            reviewWorkflowService.assignTask(employeeRef, task);
        } catch (WriteTransactionNeededException ex) {
            throw new WebScriptException("Can't assign task.", ex);
        }
    }

    public void actualizeTask(final ActivitiScriptNode assignee, final DelegateTask task) {
        NodeRef employeeRef = assignee.getNodeRef();
        reviewWorkflowService.actualizeTask(employeeRef, task);
    }

    public void reassignTask(final ActivitiScriptNode assignee, final DelegateTask task) {
        try {
            reviewWorkflowService.reassignTask(assignee.getNodeRef(), task);
        } catch (WriteTransactionNeededException ex) {
            throw new WebScriptException("Can't reassign task.", ex);
        }
    }

    public void completeTask(final ActivitiScriptNode assignee, final DelegateTask task) {
        try {
            reviewWorkflowService.completeTask(assignee.getNodeRef(), task);
        } catch (WriteTransactionNeededException ex) {
            throw new WebScriptException("Can't complete task.", ex);
        }
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
        try {
            reviewWorkflowService.sendBareNotifications(assigneesList.getNodeReferences(), workflowDueDate, bpmPackage.getNodeRef());
        } catch (WriteTransactionNeededException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }
    }

    public void markReviewedTS(ScriptNode documentRef) {
        ParameterCheck.mandatory("documentRef", documentRef);
        reviewServiceImpl.markReviewed(documentRef.getNodeRef());
    }
    
    public Boolean needReviewByCurrentUser(ScriptNode documentRef) {
        ParameterCheck.mandatory("documentRef", documentRef);
        
        return reviewServiceImpl.needReviewByCurrentUser(documentRef.getNodeRef());
    }
    
	public void processItem(ScriptNode item ) throws WriteTransactionNeededException {
		ParameterCheck.mandatory("item", item);
		reviewServiceImpl.processItem(item.getNodeRef());
	}
	
}
