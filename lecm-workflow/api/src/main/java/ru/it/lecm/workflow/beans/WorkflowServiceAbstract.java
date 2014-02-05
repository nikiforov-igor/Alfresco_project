package ru.it.lecm.workflow.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.workflow.DocumentInfo;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.api.WorkflowFoldersService;
import ru.it.lecm.workflow.api.WorkflowModel;
import ru.it.lecm.workflow.api.WorkflowService;

/**
 *
 * @author vlevin
 */
public abstract class WorkflowServiceAbstract extends BaseBean implements WorkflowService {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowServiceAbstract.class);
	public final static String RESULT_ITEM_FORMAT = "Участник %s";
	protected final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	protected OrgstructureBean orgstructureService;
	protected LecmPermissionService lecmPermissionService;
	protected DocumentService documentService;
	protected DocumentMembersService documentMembersService;
	protected NotificationsService notificationsService;
	protected org.alfresco.service.cmr.workflow.WorkflowService workflowService;
	protected WorkflowFoldersService workflowFoldersService;

	public void setWorkflowService(org.alfresco.service.cmr.workflow.WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setWorkflowFoldersService(WorkflowFoldersService workflowFoldersService) {
		this.workflowFoldersService = workflowFoldersService;
	}

	@Override
	public void assignTask(NodeRef assignee, DelegateTask task) {
		Date dueDate = task.getDueDate();
		if (dueDate == null) {
			dueDate = (Date) nodeService.getProperty(assignee, WorkflowModel.PROP_ASSIGNEE_DUE_DATE);
			task.setDueDate(dueDate);
		}

		/*
		String currentUserName = task.getAssignee();
		String previousUserName = (String) nodeService.getProperty(assignee, WorkflowModel.PROP_ASSIGNEE_USERNAME);

		if (!currentUserName.equals(previousUserName)) {
			NodeRef resultListRef = getResultListRef(task);
			NodeRef resultItemRef = getResultItemByUserName(resultListRef, currentUserName);
			if (resultItemRef == null) {
				String newItemTitle = String.format(RESULT_ITEM_FORMAT, currentUserName);
				createResultItem(resultListRef, orgstructureService.getEmployeeByPerson(currentUserName), newItemTitle, dueDate, getResultItemType());
			}
			NodeRef oldResultListItemRef = getResultItemByUserName(resultListRef, previousUserName);
			if (oldResultListItemRef != null) {
				onTaskReassigned(oldResultListItemRef, resultItemRef);
			}
		}
		*/

		DelegateExecution execution = task.getExecution();
		NodeRef bpmPackage = ((ScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantReviewerPermissions(employeeRef, bpmPackage);
		notifyWorkflowStarted(employeeRef, dueDate, bpmPackage);
	}

	@Override
	public void grantReviewerPermissions(final NodeRef employeeRef, final NodeRef bpmPackage) {
		NodeRef documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		if (Utils.isDocument(documentRef)) {
			this.grantReviewerPermissionsInternal(employeeRef, documentRef);
		}
	}

	@Override
	public void grantReviewerPermissionsInternal(final NodeRef employeeRef, final NodeRef documentRef) {
		grantPermissions(employeeRef, documentRef, "LECM_BASIC_PG_Reviewer");
	}

	@Override
	public void grantReaderPermissions(final NodeRef employeeRef, final NodeRef bpmPackage) {
		NodeRef documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		if (Utils.isDocument(documentRef)) {
			this.grantPermissions(employeeRef, documentRef, "LECM_BASIC_PG_Reader");
		}
	}

	protected void grantPermissions(final NodeRef employeeRef, final NodeRef documentRef, final String permissionGroup) {
		if (documentRef != null) {
			NodeRef member = documentMembersService.addMemberWithoutCheckPermission(documentRef, employeeRef, permissionGroup);
			if (member == null) { // сотрудник уже добавлен как участник - значит просто раздаем доп права
				LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(permissionGroup);
				lecmPermissionService.grantAccess(pgGranting, documentRef, employeeRef);
			}
			if (logger.isTraceEnabled()) {
				String employeeName = (String) nodeService.getProperty(employeeRef, ContentModel.PROP_NAME);
				String docName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
				logger.trace("Employee {} has been invited to the document {} with {} permission. ", new Object[]{employeeName, docName, permissionGroup});
			}
		} else {
			logger.error("There is no any lecm-contract:document in bpm:package. Permissions won't be granted");
		}
	}

	@Override
	public void revokeReviewerPermissions(NodeRef employeeRef, NodeRef bpmPackage) {
		NodeRef documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		if (Utils.isDocument(documentRef)) {
			this.revokePermissions(employeeRef, documentRef, "LECM_BASIC_PG_Reviewer");
		}
	}

	protected void revokePermissions(NodeRef employeeRef, NodeRef documentRef, final String permissionGroup) {
		if (documentRef != null) {
			LecmPermissionService.LecmPermissionGroup pgRevoking = lecmPermissionService.findPermissionGroup(permissionGroup);
			lecmPermissionService.revokeAccess(pgRevoking, documentRef, employeeRef);
		} else {
			logger.error("There is no any lecm-contract:document in bpm:package. Permissions won't be revoked");
		}
	}

	@Override
	public void notifyWorkflowStarted(NodeRef employeeRef, Date dueDate, NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employeeRef);

		String description = getWorkflowStartedMessage(docInfo.getDocumentLink(), dueDate);

		sendNotification(description, docInfo.getDocumentRef(), recipients);
	}

	@Override
	public void notifyWorkflowFinished(String decision, NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(documentService.getDocumentAuthor(docInfo.getDocumentRef()));

		String description = getWorkflowFinishedMessage(docInfo.getDocumentLink(), decision);
		sendNotification(description, docInfo.getDocumentRef(), recipients);
	}

	//TODO
	@Override
	public void notifyAssigneesDeadline(String processInstanceId, NodeRef bpmPackage) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	//TODO
	@Override
	public void notifyInitiatorDeadline(String processInstanceId, NodeRef bpmPackage, VariableScope variableScope) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	//TODO
	@Override
	public NodeRef getEmployeeForAssignee(NodeRef assigneeRef) {
		return findNodeByAssociationRef(assigneeRef, WorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
	}

	protected void sendNotification(String message, NodeRef documentRef, List<NodeRef> recipients) {
		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(message);
		notification.setObjectRef(documentRef);
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notification);
	}

	protected String getIncompleteAssignees(final String processInstanceId) {
		WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
		taskQuery.setProcessId(processInstanceId);
		taskQuery.setTaskState(WorkflowTaskState.IN_PROGRESS);
		List<WorkflowTask> tasks = workflowService.queryTasks(taskQuery);
		StringBuilder builder = new StringBuilder();
		for (WorkflowTask task : tasks) {
			String owner = (String) task.getProperties().get(ContentModel.PROP_OWNER);
			NodeRef employee = orgstructureService.getEmployeeByPerson(owner);
			String name = (String) nodeService.getProperty(employee, ContentModel.PROP_NAME);
			builder.append(name).append(", ");
		}
		int length = builder.length();
		if (length > 0) {
			builder.delete(length - 2, length); //удалить последний ", "
		}
		return builder.toString();
	}

	abstract protected String getWorkflowStartedMessage(String documentLink, Date dueDate);

	abstract protected String getWorkflowFinishedMessage(String documentLink, String decision);

	abstract protected void onTaskReassigned(NodeRef oldResultListItemRef, NodeRef newResultItemRef);
}
