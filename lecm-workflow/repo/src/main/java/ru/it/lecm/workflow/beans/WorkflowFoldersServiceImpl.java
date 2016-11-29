package ru.it.lecm.workflow.beans;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.workflow.api.WorkflowFoldersService;

/**
 *
 * @author vmalygin
 */
public class WorkflowFoldersServiceImpl extends BaseBean implements WorkflowFoldersService {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowFoldersServiceImpl.class);

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
	
	@Override
	protected void onBootstrap(ApplicationEvent event) {
		lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
					@Override
					public Void doWork() throws Exception {
						getServiceRootFolder();
						getWorkflowFolder();
						getGlobalResultFolder();
						getAssigneesListWorkingCopyFolder();
						return null;
					}
				});
			}
		});
	}
}
