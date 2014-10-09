package ru.it.lecm.workflow.signing.api.deprecated;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.LecmWorkflowService;

/**
 *
 * @author vmalygin
 */
@Deprecated
public interface SigningWorkflowService extends LecmWorkflowService {
	void logFinalDecision(final NodeRef resultListRef, final String finalDecision);
	void dropSigningResults(final NodeRef resultListRef);
	NodeRef createResultList(final NodeRef bpmPackage, final String documentAttachmentCategoryName, final List<NodeRef> assigneesList);
	void logDecision(final NodeRef resultListRef, final WorkflowTaskDecision taskDecision);
	NodeRef getOrCreateSigningFolderContainer(NodeRef parentRef);

	public void addSignBusinessJournalRecord(NodeRef bpmPackage, NodeRef employee);
}
