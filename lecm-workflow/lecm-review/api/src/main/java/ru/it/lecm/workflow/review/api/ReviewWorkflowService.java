package ru.it.lecm.workflow.review.api;

import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.api.LecmWorkflowService;

/**
 *
 * @author vmalygin
 */
public interface ReviewWorkflowService extends LecmWorkflowService {
	NodeRef createResultList(NodeRef bpmPackage, String documentAttachmentCategoryName, ActivitiScriptNodeList assigneesList);

	List<NodeRef> createAssigneesList(NodeRef nodeRef, DelegateExecution execution);

	void deleteTempAssigneesList(DelegateExecution execution);

	void logWorkflowFinished(NodeRef resultList);

}
