package ru.it.lecm.workflow.review.api;

import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.api.LecmWorkflowService;

/**
 *
 * @author vmalygin
 */
public interface ReviewWorkflowService extends LecmWorkflowService {
	NodeRef createResultList(NodeRef bpmPackage, String documentAttachmentCategoryName, List<NodeRef> assigneesList);

	List<NodeRef> createAssigneesList(NodeRef nodeRef, DelegateExecution execution);

	void deleteAssigneesListWorkingCopy(DelegateExecution execution);

	void logWorkflowFinished(NodeRef resultList);

	void sendBareNotifications(List<NodeRef> assigneesList, Date workflowDueDate, NodeRef bpmPackage);

}
