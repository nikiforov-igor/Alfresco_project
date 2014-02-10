package ru.it.lecm.workflow.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.workflow.api.WorkflowFoldersService;

/**
 *
 * @author vmalygin
 */
public class WorkflowFoldersServiceImpl extends BaseBean implements WorkflowFoldersService {

	public final static String WORKFLOW_FOLDER = "WORKFLOW_FOLDER";
	public final static String WORKFLOW_GLOBAL_RESULT_FOLDER = "WORKFLOW_GLOBAL_RESULT_FOLDER";
	public final static String ASSIGNEES_LISTS_WORKING_COPY_FOLDER = "ASSIGNEES_LISTS_WORKING_COPY_FOLDER";

	@Override
	public NodeRef getServiceRootFolder() {
		return getWorkflowFolder();
	}

	@Override
	public NodeRef getWorkflowFolder() {
		return getFolder(WORKFLOW_FOLDER);
	}

	@Override
	public NodeRef getGlobalResultFolder() {
		return getFolder(WORKFLOW_GLOBAL_RESULT_FOLDER);
	}

	@Override
	public NodeRef getAssigneesListWorkingCopyFolder() {
		return getFolder(ASSIGNEES_LISTS_WORKING_COPY_FOLDER);
	}
}
