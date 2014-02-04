package ru.it.lecm.workflow.signing;

import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

/**
 *
 * @author vmalygin
 */
public class SigningWorkflowServiceImpl extends WorkflowServiceAbstract implements SigningWorkflowService {

	@Override
	protected String getWorkflowStartedMessage(String documentLink, Date dueDate) {
	}

	@Override
	protected String getWorkflowFinishedMessage(String documentLink, String decision) {
	}

	@Override
	protected NodeRef getOrCreateWorkflowResultFolder(NodeRef parentRef, String workflowType) {
	}

	@Override
	protected void onTaskReassigned(NodeRef oldResultListItemRef, NodeRef newResultItemRef) {
	}

	@Override
	protected String getResultListName() {
	}

	@Override
	protected org.alfresco.service.namespace.QName getResultItemType() {
	}

	@Override
	protected org.alfresco.service.namespace.QName getResultListType() {
	}

}
