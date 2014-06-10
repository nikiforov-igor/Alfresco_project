package ru.it.lecm.workflow.review.api;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.workflow.api.LecmWorkflowService;

import java.util.Date;
import java.util.List;

/**
 *
 * @author vmalygin
 */
public interface ReviewWorkflowService extends LecmWorkflowService {
	NodeRef createResultList(NodeRef bpmPackage, String documentAttachmentCategoryName, List<NodeRef> assigneesList);

	List<NodeRef> createAssigneesList(NodeRef nodeRef, DelegateExecution execution);

	void deleteAssigneesListWorkingCopy(DelegateExecution execution);

	void logWorkflowFinished(NodeRef resultList);

	void sendBareNotifications(List<NodeRef> assigneesList, Date workflowDueDate, NodeRef bpmPackage) throws WriteTransactionNeededException;

    void actualizeTask(NodeRef assignee, DelegateTask task);

}
