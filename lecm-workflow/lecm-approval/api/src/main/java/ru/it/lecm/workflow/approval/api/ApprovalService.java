package ru.it.lecm.workflow.approval.api;

import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.api.LecmWorkflowService;

import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import ru.it.lecm.workflow.WorkflowTaskDecision;

/**
 *
 * @author vlevin
 */
public interface ApprovalService extends LecmWorkflowService {

	String ASSIGNEES_LISTS_PARALLEL_FOLDER_NAME = "Параллельное согласование";
	String ASSIGNEES_LISTS_SEQUENTIAL_FOLDER_NAME = "Последовательное согласование";
	String CUSTOM_APPROVAL_FOLDER_NAME = "Специальное согласование";
	String PARLLEL_APPROVAL_FOLDER_NAME = "Параллельное согласование";
	String SEQUENTIAL_APPROVAL_FOLDER_NAME = "Последовательное согласование";
	String APPROVAL_TYPE_SEQUENTIAL = "SEQUENTIAL";
	String APPROVAL_TYPE_PARALLEL = "PARALLEL";
	String APPROVAL_TYPE_CUSTOM = "CUSTOM";
	String ASSEGNEE_ITEM_FORMAT = "Согласующий %s";
	String APPROVAL_LIST_NAME = "Лист согласования версия %s";
	String BUSINESS_ROLE_CONTRACT_CURATOR_ID = "CONTRACT_CURATOR";

	void logFinalDecision(final NodeRef approvalListRef, final String finalDecision);

	WorkflowTaskDecision completeTask(NodeRef assignee, DelegateTask task, String decision, NodeRef commentRef, Date dueDate);

	NodeRef createApprovalList(NodeRef bpmPackage, String documentAttachmentCategoryName, String approvalType, List<NodeRef> assigneesList);

	List<NodeRef> createAssigneesList(NodeRef nodeRef, DelegateExecution execution);

	void deleteTempAssigneesList(DelegateExecution execution);

	public NodeRef getEmployeeForAssignee(NodeRef assignee);
}
