package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
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
import ru.it.lecm.workflow.api.WorkflowModel;
import ru.it.lecm.workflow.api.WorkflowResultModel;
import ru.it.lecm.workflow.api.WorkflowService;

/**
 *
 * @author vlevin
 */
public abstract class WorkflowServiceAbstract extends BaseBean implements WorkflowService {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowServiceAbstract.class);
	public final static String TEMP_WORKFLOW_ASSIGNEES_LISTS_FOLDER = "TEMP_ASSIGNEES_FOLDER";
	public final static String WORKFLOW_FOLDER = "WORKFLOW_FOLDER";
	public final static String WORKFLOW_RESULT_FOLDER = "WORKFLOW_RESULT_FOLDER";
	public final static String RESULT_ITEM_FORMAT = "Участник %s";
	protected final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	protected CopyService copyService;
	protected BehaviourFilter behaviourFilter;
	protected OrgstructureBean orgstructureService;
	protected LecmPermissionService lecmPermissionService;
	protected DocumentService documentService;
	protected DocumentMembersService documentMembersService;
	protected NotificationsService notificationsService;
	protected org.alfresco.service.cmr.workflow.WorkflowService workflowService;

	public void setWorkflowService(org.alfresco.service.cmr.workflow.WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setCopyService(CopyService copyService) {
		this.copyService = copyService;
	}

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
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

	@Override
	public void deleteTempAssigneesList(DelegateExecution execution) {
		String executionID = execution.getId();
		NodeRef tempAssigneesListsFolder = getTempWorkflowAssigneesListFolder();
		NodeRef tempAssigneesList = nodeService.getChildByName(tempAssigneesListsFolder, WorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, executionID);
		if (tempAssigneesList != null) {
			nodeService.deleteNode(tempAssigneesList);
		}
	}

	@Override
	public List<NodeRef> createAssigneesList(NodeRef assigneesListNode, DelegateExecution execution) {
		NodeRef tempAssigneesList;
		NodeRef tempAssigneesListsFolder = getTempWorkflowAssigneesListFolder();
		String executionID = execution.getId();
		QName assocQName = QName.createQName(WorkflowModel.WORKFLOW_NAMESPACE, executionID);
		behaviourFilter.disableBehaviour(WorkflowModel.TYPE_ASSIGNEE);
		try {
			tempAssigneesList = copyService.copyAndRename(assigneesListNode, tempAssigneesListsFolder, ContentModel.ASSOC_CONTAINS, assocQName, true);
		} finally {
			behaviourFilter.enableBehaviour(WorkflowModel.TYPE_ASSIGNEE);
		}
		nodeService.setProperty(tempAssigneesList, ContentModel.PROP_NAME, executionID);
		nodeService.addAspect(tempAssigneesList, ContentModel.ASPECT_TEMPORARY, null);
		return findNodesByAssociationRef(tempAssigneesList, WorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, WorkflowModel.TYPE_ASSIGNEE, ASSOCIATION_TYPE.TARGET);
	}

	protected NodeRef getTempWorkflowAssigneesListFolder() {
		return getFolder(TEMP_WORKFLOW_ASSIGNEES_LISTS_FOLDER);
	}

	@Override
	public NodeRef getWorkflowFolder() {
		return getFolder(WORKFLOW_FOLDER);
	}

	@Override
	public NodeRef getResultFolder() {
		return getFolder(WORKFLOW_RESULT_FOLDER);
	}

	protected NodeRef getResultListRef(DelegateTask task) {
		DelegateExecution execution = task.getExecution();
		return ((ActivitiScriptNode) execution.getVariable("resultListRef")).getNodeRef();
	}

	@Override
	public void assignTask(NodeRef assignee, DelegateTask task) {
		Date dueDate = task.getDueDate();
		if (dueDate == null) {
			dueDate = (Date) nodeService.getProperty(assignee, WorkflowModel.PROP_ASSIGNEE_DUE_DATE);
			task.setDueDate(dueDate);
		}

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

		DelegateExecution execution = task.getExecution();
		NodeRef bpmPackage = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantReviewerPermissions(employeeRef, bpmPackage);
		notifyWorkflowStarted(employeeRef, dueDate, bpmPackage);
	}

	protected NodeRef getResultItemByUserName(NodeRef resultListRef, String userName) {
		String itemTitle = String.format(RESULT_ITEM_FORMAT, userName);

		return nodeService.getChildByName(resultListRef, ContentModel.ASSOC_CONTAINS, itemTitle);
	}

	@Override
	public NodeRef createResultItem(NodeRef approvalListRef, NodeRef employeeRef, String itemTitle, Date dueDate, QName resultItemType) {
		NodeRef approvalListItemRef;
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_TITLE, itemTitle);
		properties.put(ContentModel.PROP_NAME, itemTitle);
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_DUE_DATE, dueDate);

		QName assocQName = QName.createQName(WorkflowResultModel.WORKFLOW_RESULT_NAMESPACE, itemTitle);
		approvalListItemRef = nodeService.createNode(approvalListRef, ContentModel.ASSOC_CONTAINS, assocQName, resultItemType, properties).getChildRef();
		if (employeeRef != null) {
			List<NodeRef> targetRefs = new ArrayList<NodeRef>();
			targetRefs.add(employeeRef);
			nodeService.setAssociations(approvalListItemRef, WorkflowResultModel.ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE, targetRefs);
		}
		return approvalListItemRef;
	}

	@Override
	public NodeRef createResultList(final NodeRef bpmPackage, final String documentAttachmentCategoryName, final String approvalType, ActivitiScriptNodeList assigneesList) {
		//через bpmPackage получить ссылку на документ
		NodeRef resultListRef, resultsRoot, approvalObject;

		approvalObject = Utils.getObjectFromBpmPackage(bpmPackage);
		resultsRoot = getOrCreateWorkflowResultFolders(bpmPackage, approvalType);

		//создаем внутри указанной папки объект "Лист согласования"
		resultListRef = createResultList(resultsRoot, approvalObject, bpmPackage, documentAttachmentCategoryName, getResultListType());
		for (ActivitiScriptNode assignee : assigneesList) {
			NodeRef assigneeNode = assignee.getNodeRef();
			NodeRef employeeRef = findNodeByAssociationRef(assigneeNode, WorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			Date dueDate = (Date) nodeService.getProperty(assigneeNode, WorkflowModel.PROP_ASSIGNEE_DUE_DATE);
			String userName = (String) nodeService.getProperty(assigneeNode, WorkflowModel.PROP_ASSIGNEE_USERNAME);

			String itemTitle = String.format(RESULT_ITEM_FORMAT, userName);

			createResultItem(resultListRef, employeeRef, itemTitle, dueDate, getResultItemType());
		}
		return resultListRef;
	}

	private NodeRef createResultList(final NodeRef parentRef, final NodeRef contractDocumentRef, final NodeRef bpmPackage, final String documentAttachmentCategoryName, QName resultListType) {
		String contractDocumentVersion = Utils.getObjectVersion(bpmPackage, documentAttachmentCategoryName);
		String approvalListVersion = Utils.getResultListVersion(contractDocumentVersion, parentRef, getResultListName());
		String localName = String.format(getResultListName(), approvalListVersion);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, localName);
		properties.put(ContentModel.PROP_TITLE, localName);
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_START_DATE, DateUtils.truncate(new Date(), Calendar.DATE));
		properties.put(WorkflowResultModel.PROP_WORKFLOW_LIST_DOCUMENT_VERSION, contractDocumentVersion);
		QName assocQName = QName.createQName(WorkflowResultModel.WORKFLOW_RESULT_NAMESPACE, localName);
		NodeRef resultListRef = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, resultListType, properties).getChildRef();
		//прикрепляем approval list к списку items у документа
		QName qname = QName.createQName(WorkflowResultModel.WORKFLOW_RESULT_NAMESPACE, localName);
		nodeService.addChild(bpmPackage, resultListRef, ContentModel.ASSOC_CONTAINS, qname);
		return resultListRef;
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
	public NodeRef getServiceRootFolder() {
		return getWorkflowFolder();
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

	protected NodeRef getOrCreateWorkflowResultFolders(NodeRef bpmPackage, String workflowType) {
		NodeRef workflowResultRoot;

		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		NodeRef contentRef = Utils.getContentFromBpmPackage(bpmPackage);

		if (documentRef != null) {
			workflowResultRoot = documentRef;
		} else if (contentRef != null) {
			String nodeUUID = (String) nodeService.getProperty(contentRef, ContentModel.PROP_NODE_UUID);
			NodeRef globalWorkflowResultFolder = getResultFolder();
			workflowResultRoot = getFolder(globalWorkflowResultFolder, nodeUUID);
			if (workflowResultRoot == null) {
				workflowResultRoot = createFolder(globalWorkflowResultFolder, nodeUUID);
			}
		} else {
			logger.error("There is no any lecm-contract:document nor cm:content  in bpm:package");
			return null;
		}

		return getOrCreateWorkflowResultFolder(workflowResultRoot, workflowType);
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

	/**
	 * Этот метод должен возвращать ссылку на папку, в которорую надо сложить
	 * лист результатов данного конкретного бизнес-процесса
	 *
	 * @param parentRef ссылка на папку, в которой надо создать необходимую структуру папок
	 * @param workflowType тип бизнес-процесса
	 * @return ссылка на папку, куда можно складывать лист результата
	 */
	abstract protected NodeRef getOrCreateWorkflowResultFolder(final NodeRef parentRef, final String workflowType) ;


	abstract protected void onTaskReassigned(NodeRef oldResultListItemRef, NodeRef newResultItemRef);

	abstract protected String getResultListName();

	abstract protected QName getResultItemType();

	abstract protected QName getResultListType();

}
