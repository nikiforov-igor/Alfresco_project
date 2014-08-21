package ru.it.lecm.workflow.approval.api.deprecated;

import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.api.LecmWorkflowService;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import ru.it.lecm.workflow.WorkflowTaskDecision;

/**
 *
 * @author vlevin
 */
@Deprecated
public interface ApprovalService extends LecmWorkflowService {
	void logFinalDecision(final NodeRef approvalListRef, final String finalDecision);

	WorkflowTaskDecision completeTask(NodeRef assignee, DelegateTask task, String decision, NodeRef commentRef, Date dueDate) throws WriteTransactionNeededException;

	NodeRef createApprovalList(NodeRef bpmPackage, String documentAttachmentCategoryName, String approvalType, List<NodeRef> assigneesList);

	List<NodeRef> createAssigneesList(NodeRef nodeRef, DelegateExecution execution);

	void deleteTempAssigneesList(DelegateExecution execution);

	NodeRef getEmployeeForAssignee(NodeRef assignee);

	NodeRef getOrCreateApprovalFolderContainer(NodeRef parentRef);
}
