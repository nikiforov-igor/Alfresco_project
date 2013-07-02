package ru.it.lecm.approval;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.apache.commons.lang.time.DateUtils;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.wcalendar.IWorkCalendar;

/**
 *
 * @author vlevin
 */
public class ApprovalListServiceImpl extends BaseBean implements ApprovalListService {

	public final static String APPROVAL_FOLDER = "APPROVAL_FOLDER";
	public final static String APPROVAL_LIST_FOLDER = "APPROVAL_LIST_FOLDER";
	private final static Logger logger = LoggerFactory.getLogger(ApprovalListServiceImpl.class);
	private final static QName FAKE_PROP_COMINGSOON = QName.createQName(NamespaceService.ALFRESCO_URI, "comingSoonNotified");
	private final static QName FAKE_PROP_OVERDUE = QName.createQName(NamespaceService.ALFRESCO_URI, "overdueNotified");
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private OrgstructureBean orgstructureService;
	private DocumentAttachmentsService documentAttachmentsService;
	private DocumentMembersService documentMembersService;
	private NotificationsService notificationsService;
	private WorkflowService workflowService;
	private IWorkCalendar workCalendar;
	private LecmPermissionService lecmPermissionService;

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getApprovalFolder();
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setWorkCalendar(IWorkCalendar workCalendar) {
		this.workCalendar = workCalendar;
	}

	/**
	 * создание папки "Согласование" внутри объекта lecm-contract:document
	 *
	 * @param parentRef
	 * @return
	 */
	protected NodeRef getOrCreateRootApprovalFolder(NodeRef parentRef) {
		NodeRef approvalRef = getFolder(parentRef, "Согласование");
		if (approvalRef == null) {
			approvalRef = createFolder(parentRef, "Согласование");
		}
		return approvalRef;
	}

	/**
	 * Этот метод должен возвращать ссылку на папку, в которорую надо сложить
	 * лист согласования данного конкретного регламента согласования
	 * (последовательное, параллельное, еще какое-то)
	 *
	 * @param parentRef ссылка на папку "Согласование" в рамках вложения
	 * документа.
	 * @param approvalType тип согласования: PARALLEL, SEQUENTIAL, CUSTOM
	 * @return ссылка на папку, куда можно складывать лист согласования
	 */
	private NodeRef getOrCreateApprovalFolder(NodeRef parentRef, final String approvalType) {
		NodeRef result = null;
		if (APPROVAL_TYPE_PARALLEL.equals(approvalType)) {
			result = getOrCreateParallelApprovalFolder(parentRef);
		} else if (APPROVAL_TYPE_SEQUENTIAL.equals(approvalType)) {
			result = getOrCreateSequentialApprovalFolder(parentRef);
		} else if (APPROVAL_TYPE_CUSTOM.equals(approvalType)) {
			result = getOrCreateCustomApprovalFolder(parentRef);
		}
		return result;
	}

	private NodeRef getOrCreateApprovalFolders(NodeRef bpmPackage, String approvalType) {
		NodeRef approvalRoot;

		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		NodeRef contentRef = Utils.getContentFromBpmPackage(bpmPackage);

		if (documentRef != null) {
			approvalRoot = documentRef;
		} else if (contentRef != null) {
			String nodeUUID = (String) nodeService.getProperty(contentRef, ContentModel.PROP_NODE_UUID);
			NodeRef approvalListsFolder = getApprovalListsFolder();
			approvalRoot = getFolder(approvalListsFolder, nodeUUID);
			if (approvalRoot == null) {
				approvalRoot = createFolder(approvalListsFolder, nodeUUID);
			}
		} else {
			logger.error("There is no any lecm-contract:document nor cm:content  in bpm:package");
			return null;
		}

		//внутри этого документа получить или создать папку "Согласование/<Какое-то> согласование"
		NodeRef approvalRef = getOrCreateRootApprovalFolder(approvalRoot);
		return getOrCreateApprovalFolder(approvalRef, approvalType);
	}

	protected NodeRef createApprovalList(final NodeRef parentRef, final NodeRef contractDocumentRef, final NodeRef bpmPackage, final String documentAttachmentCategoryName) {
		String contractDocumentVersion = Utils.getObjectVersion(bpmPackage, documentAttachmentCategoryName);
		String approvalListVersion = Utils.getApprovalListVersion(contractDocumentVersion, parentRef);
		String localName = String.format(Utils.APPROVAL_LIST_NAME, approvalListVersion);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, localName);
		properties.put(ContentModel.PROP_TITLE, localName);
		properties.put(PROP_APPROVAL_LIST_APPROVE_START, DateUtils.truncate(new Date(), Calendar.DATE));
		properties.put(PROP_APPROVAL_LIST_DOCUMENT_VERSION, contractDocumentVersion);
		QName assocQName = QName.createQName(APPROVAL_LIST_NAMESPACE, localName);
		NodeRef approvalListRef = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_APPROVAL_LIST, properties).getChildRef();
		//прикрепляем approval list к списку items у документа
		QName qname = QName.createQName(APPROVAL_LIST_NAMESPACE, localName);
		nodeService.addChild(bpmPackage, approvalListRef, ContentModel.ASSOC_CONTAINS, qname);
		return approvalListRef;
	}

	@Override
	public NodeRef createApprovalList(final NodeRef bpmPackage, final String documentAttachmentCategoryName, final String approvalType, ActivitiScriptNodeList assigneesList) {
		//через bpmPackage получить ссылку на документ
		NodeRef approvalListRef, approvalRoot, approvalObject;

		approvalObject = Utils.getObjectFromBpmPackage(bpmPackage);
		approvalRoot = getOrCreateApprovalFolders(bpmPackage, approvalType);

		//создаем внутри указанной папки объект "Лист согласования"
		approvalListRef = createApprovalList(approvalRoot, approvalObject, bpmPackage, documentAttachmentCategoryName);
		for (ActivitiScriptNode assignee : assigneesList) {
			NodeRef assigneeNode = assignee.getNodeRef();
			NodeRef employeeRef = findNodeByAssociationRef(assigneeNode, ASSOC_ASSIGNEES_ITEM_EMPLOYEE_ASSOC, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			Date dueDate = (Date) nodeService.getProperty(assigneeNode, PROP_ASSIGNEES_ITEM_DUE_DATE);
			String userName = (String) nodeService.getProperty(assigneeNode, PROP_ASSIGNEES_ITEM_USERNAME);

			String itemTitle = String.format(ASSEGNEE_ITEM_FORMAT, userName);

			createApprovalListItem(approvalListRef, employeeRef, itemTitle, dueDate);
		}
		return approvalListRef;
	}

	private NodeRef createApprovalListItem(NodeRef approvalListRef, NodeRef employeeRef, String itemTitle, Date dueDate) {
		NodeRef approvalListItemRef;
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_TITLE, itemTitle);
		properties.put(ContentModel.PROP_NAME, itemTitle);
		properties.put(PROP_APPROVAL_ITEM_DUE_DATE, dueDate);

		QName assocQName = QName.createQName(APPROVAL_LIST_NAMESPACE, itemTitle);
		approvalListItemRef = nodeService.createNode(approvalListRef, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_APPROVAL_ITEM, properties).getChildRef();
		if (employeeRef != null) {
			List<NodeRef> targetRefs = new ArrayList<NodeRef>();
			targetRefs.add(employeeRef);
			nodeService.setAssociations(approvalListItemRef, ASSOC_APPROVAL_ITEM_EMPLOYEE, targetRefs);
		}
		return approvalListItemRef;
	}

	private void logDecision(final NodeRef approvalListRef, final TaskDecision taskDecision) {
		Date startDate, completionDate;
		String comment, decision, userName, commentFileAttachmentCategoryName, documentProjectNumber, previousUserName;
		NodeRef commentRef, documentRef;

		startDate = DateUtils.truncate(taskDecision.getStartDate(), Calendar.DATE);
		completionDate = DateUtils.truncate(new Date(), Calendar.DATE);
		comment = taskDecision.getComment();
		decision = taskDecision.getDecision();
		userName = taskDecision.getUserName();
		commentRef = taskDecision.getCommentRef();
		documentRef = taskDecision.getDocumentRef();
		commentFileAttachmentCategoryName = taskDecision.getCommentFileAttachmentCategoryName();
		documentProjectNumber = taskDecision.getDocumentProjectNumber();
		previousUserName = taskDecision.getPreviousUserName();

		String itemTitle = String.format(ASSEGNEE_ITEM_FORMAT, userName);

		NodeRef approvalListItemRef = getApprovalListItemByUserName(approvalListRef, userName);

//		if (approvalListItemRef == null) {
//			approvalListItemRef = createApprovalListItem(approvalListRef, orgstructureService.getEmployeeByPerson(userName), itemTitle, taskDecision.getDueDate());
//		}

		Map<QName, Serializable> properties = nodeService.getProperties(approvalListItemRef);

		properties.put(PROP_APPROVAL_ITEM_START_DATE, startDate);
		properties.put(PROP_APPROVAL_ITEM_APPROVE_DATE, completionDate);
		properties.put(PROP_APPROVAL_ITEM_COMMENT, comment);
		properties.put(PROP_APPROVAL_ITEM_DECISION, decision);

		nodeService.setProperties(approvalListItemRef, properties);

		if (commentRef != null && documentRef != null && commentFileAttachmentCategoryName != null && documentProjectNumber != null) {
			NodeRef attachmentCategoryRef = documentAttachmentsService.getCategory(commentFileAttachmentCategoryName, documentRef);
			if (attachmentCategoryRef != null) {
				StringBuilder commentFileName = new StringBuilder();
				commentFileName.append(nodeService.getProperty(documentRef, QName.createQName(documentProjectNumber, serviceRegistry.getNamespaceService())));
				commentFileName.append(", ");

				commentFileName.append(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date())).append(" + ");
				commentFileName.append("Согласование сотрудником");

				NodeRef employeeRef = findNodeByAssociationRef(approvalListItemRef, ASSOC_ASSIGNEES_ITEM_EMPLOYEE_ASSOC, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);

				if (employeeRef != null) {
                                        commentFileName.append(" ");
					commentFileName.append(nodeService.getProperty(employeeRef, ContentModel.PROP_NAME));
				}

				String commentFileNameStr = FileNameValidator.getValidFileName(commentFileName.toString());

				if (nodeService.getChildByName(attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentFileNameStr) != null) {
					int i = 0;
					do {
						i++;
					} while (nodeService.getChildByName(attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentFileNameStr + " " + i) != null);
					commentFileNameStr += " " + i;
				}

				final NodeRef commentRefFinal = commentRef;
				final String commentFileNameFinal = commentFileNameStr;
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
					@Override
					public Object execute() throws Throwable {
						nodeService.setProperty(commentRefFinal, ContentModel.PROP_NAME, commentFileNameFinal);
						return null;
					}
				}, false, true);

				QName commentAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, commentFileNameStr);
				nodeService.moveNode(commentRef, attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentAssocQName);
			}

			List<NodeRef> targetRefs = new ArrayList<NodeRef>();
			targetRefs.add(commentRef);
			nodeService.setAssociations(approvalListItemRef, ASSOC_APPROVAL_ITEM_COMMENT, targetRefs);
		}

	}

	private NodeRef getApprovalListItemByUserName(NodeRef approvalListRef, String userName) {
		String itemTitle = String.format(ASSEGNEE_ITEM_FORMAT, userName);

		return nodeService.getChildByName(approvalListRef, ContentModel.ASSOC_CONTAINS, itemTitle);
	}

	@Override
	public void logFinalDecision(final NodeRef approvalListRef, final String finalDecision) {
		Map<QName, Serializable> properties = nodeService.getProperties(approvalListRef);
		properties.put(PROP_APPROVAL_LIST_DECISION, finalDecision);
		properties.put(PROP_APPROVAL_LIST_APPROVE_DATE, new Date());
		nodeService.setProperties(approvalListRef, properties);
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
		if (documentRef != null) {
			NodeRef member = documentMembersService.addMemberWithoutCheckPermission(documentRef, employeeRef, "LECM_BASIC_PG_Reviewer");
			if (member == null) { // сотрудник уже добавлен как участник - значит просто раздаем доп права
				LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup("LECM_BASIC_PG_Reviewer");
				lecmPermissionService.grantAccess(pgGranting, documentRef, employeeRef.getId());
			}
			if (logger.isTraceEnabled()) {
				String employeeName = (String) nodeService.getProperty(employeeRef, ContentModel.PROP_NAME);
				String docName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
				logger.trace("Employee {} has been invited to the document {} with LECM_BASIC_PG_Reviewer permission. ", new Object[]{employeeName, docName});
			}
		} else {
			logger.error("There is no any lecm-contract:document in bpm:package. Permissions won't be granted");
		}
	}

	@Override
	public void notifyApprovalStarted(NodeRef employeeRef, Date dueDate, NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, serviceRegistry);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employeeRef);

		String dueDatemessage = dueDate == null ? "(нет)" : DATE_FORMAT.format(dueDate);
		String description = String.format("Вам необходимо согласовать документ %s, срок согласования %s", docInfo.getDocumentLink(), dueDatemessage);

		Notification notification = new Notification();
		notification.setAutor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(docInfo.getDocumentRef());
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notificationChannels, notification);
	}

	@Override
	public void notifyFinalDecision(final String decisionCode, final NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, serviceRegistry);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(docInfo.getInitiatorRef());

		String decision;
		if (DecisionResult.APPROVED.name().equals(decisionCode)) {
			decision = "согласовано";
		} else if (DecisionResult.APPROVED_WITH_REMARK.name().equals(decisionCode)) {
			decision = "согласовано с замечаниями";
		} else if (DecisionResult.REJECTED.name().equals(decisionCode)) {
			decision = "отклонено";
		} else if (DecisionResult.APPROVED_FORCE.name().equals(decisionCode)) {
			decision = "принудительно завершено";
		} else if (DecisionResult.REJECTED_FORCE.name().equals(decisionCode)) {
			decision = "отозвано с согласования";
		} else if (DecisionResult.NO_DECISION.name().equals(decisionCode)) {
			decision = "решение не принято";
		} else {
			decision = "";
		}

		String description = String.format("Принято решение о документе %s: \"%s\"", docInfo.getDocumentLink(), decision);

		Notification notification = new Notification();
		notification.setAutor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(docInfo.getDocumentRef());
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notificationChannels, notification);
	}

	private void notifyAssigneeDeadline(WorkflowTask userTask, final DocumentInfo docInfo) {
		Map<QName, Serializable> props = userTask.getProperties();
		Date dueDate = (Date) props.get(WorkflowModel.PROP_DUE_DATE);
		String owner = (String) props.get(ContentModel.PROP_OWNER);
		NodeRef employee = orgstructureService.getEmployeeByPerson(owner);
		List<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employee);
		Date comingSoonDate = workCalendar.getEmployeePreviousWorkingDay(employee, dueDate, -1);
		Date currentDate = new Date();
		int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
		int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
		Map<QName, Serializable> fakeProps = new HashMap<QName, Serializable>();
		if (!props.containsKey(FAKE_PROP_COMINGSOON) && comingSoon >= 0) {
			fakeProps.put(FAKE_PROP_COMINGSOON, "");
			Notification notification = new Notification();
			notification.setAutor(AuthenticationUtil.getSystemUserName());
			String description = String.format("Напоминание: Вам необходимо согласовать проект документа %s, срок согласования %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate));
			notification.setDescription(description);
			notification.setObjectRef(docInfo.getDocumentRef());
			notification.setRecipientEmployeeRefs(recipients);
			notificationsService.sendNotification(notificationChannels, notification);
		}
		if (!props.containsKey(FAKE_PROP_OVERDUE) && overdue > 0) {
			fakeProps.put(FAKE_PROP_OVERDUE, "");
			Notification notification = new Notification();
			notification.setAutor(AuthenticationUtil.getSystemUserName());
			String description = String.format("Внимание: Вы не согласовали документ %s, срок согласования %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate));
			notification.setDescription(description);
			notification.setObjectRef(docInfo.getDocumentRef());
			notification.setRecipientEmployeeRefs(recipients);
			notificationsService.sendNotification(notificationChannels, notification);
		}
		if (!fakeProps.isEmpty()) {
			workflowService.updateTask(userTask.getId(), fakeProps, null, null);
		}
	}

	@Override
	public void notifyAssigneesDeadline(final String processInstanceId, final NodeRef bpmPackage) {
		try {
			DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, serviceRegistry);

			WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
			taskQuery.setProcessId(processInstanceId);
			taskQuery.setTaskState(WorkflowTaskState.IN_PROGRESS);
			List<WorkflowTask> tasks = workflowService.queryTasks(taskQuery);
			for (WorkflowTask task : tasks) {
				logger.trace(task.toString());
				notifyAssigneeDeadline(task, docInfo);
			}
		} catch (Exception ex) {
			logger.error("Internal error while notifying Assignees", ex);
		}
	}

	private String getIncompleteAssignees(final String processInstanceId) {
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

	@Override
	public void notifyInitiatorDeadline(final String processInstanceId, final NodeRef bpmPackage, final VariableScope variableScope) {
		try {
			boolean isDocumentApproval = Utils.isDocument(Utils.getDocumentFromBpmPackage(bpmPackage));
			DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, serviceRegistry);
			Set<NodeRef> recipients = new HashSet<NodeRef>();
			recipients.add(docInfo.getInitiatorRef());
			WorkflowInstance workflowInstance = workflowService.getWorkflowById(processInstanceId);
			Date dueDate = workflowInstance.getDueDate();
			Date comingSoonDate = workCalendar.getEmployeePreviousWorkingDay(docInfo.getInitiatorRef(), dueDate, -1);
			Date currentDate = new Date();
			int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
			int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
			if (!variableScope.hasVariable("initiatorComingSoon") && comingSoon >= 0) {
				variableScope.setVariable("initiatorComingSoon", "");
				Notification notification = new Notification();
				notification.setAutor(AuthenticationUtil.getSystemUserName());
				String description = String.format("Напоминание: Вы направили на согласование проект документа %s, срок согласования %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate));
				notification.setDescription(description);
				notification.setObjectRef(docInfo.getDocumentRef());
				notification.setRecipientEmployeeRefs(new ArrayList<NodeRef>(recipients));
				notificationsService.sendNotification(notificationChannels, notification);
			}
			if (!variableScope.hasVariable("initiatorOverdue") && overdue > 0) {
				variableScope.setVariable("initiatorOverdue", "");
				Notification notification = new Notification();
				notification.setAutor(AuthenticationUtil.getSystemUserName());
				String people = getIncompleteAssignees(processInstanceId);
				String description = String.format("Внимание: проект документа %s не согласован в срок %s. Следующие сотрудники не приняли решение: %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate), people);
				if (isDocumentApproval) {
					//получить список кураторов и добавить его в recipients
					recipients.addAll(Utils.getCurators());
				}
				notification.setDescription(description);
				notification.setObjectRef(docInfo.getDocumentRef());
				notification.setRecipientEmployeeRefs(new ArrayList<NodeRef>(recipients));
				notificationsService.sendNotification(notificationChannels, notification);
			}
		} catch (Exception ex) {
			logger.error("Internal error while notifying initiator and curators", ex);
		}
	}

	@Override
	public NodeRef getEmployeeForAssignee(final NodeRef assigneeRef) {
		return findNodeByAssociationRef(assigneeRef, ASSOC_ASSIGNEES_ITEM_EMPLOYEE_ASSOC, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
	}

	private NodeRef getOrCreateCustomApprovalFolder(NodeRef parentRef) {
		NodeRef customApprovalRef = getFolder(parentRef, CUSTOM_APPROVAL_FOLDER_NAME);
		if (customApprovalRef == null) {
			customApprovalRef = createFolder(parentRef, CUSTOM_APPROVAL_FOLDER_NAME);
		}
		return customApprovalRef;
	}

	private NodeRef getOrCreateParallelApprovalFolder(NodeRef parentRef) {
		NodeRef parallelApprovalRef = getFolder(parentRef, PARLLEL_APPROVAL_FOLDER_NAME);
		if (parallelApprovalRef == null) {
			parallelApprovalRef = createFolder(parentRef, PARLLEL_APPROVAL_FOLDER_NAME);
		}
		return parallelApprovalRef;
	}

	private NodeRef getOrCreateSequentialApprovalFolder(NodeRef parentRef) {
		NodeRef parallelApprovalRef = getFolder(parentRef, SEQUENTIAL_APPROVAL_FOLDER_NAME);
		if (parallelApprovalRef == null) {
			parallelApprovalRef = createFolder(parentRef, SEQUENTIAL_APPROVAL_FOLDER_NAME);
		}
		return parallelApprovalRef;
	}

	@Override
	public void assignTask(NodeRef assignee, DelegateTask task) {
		Date dueDate = task.getDueDate();
		if (dueDate == null) {
			dueDate = (Date) nodeService.getProperty(assignee, PROP_ASSIGNEES_ITEM_DUE_DATE);
			task.setDueDate(dueDate);
		}

		String currentUserName = task.getAssignee();
		String previousUserName = (String) nodeService.getProperty(assignee, PROP_ASSIGNEES_ITEM_USERNAME);

		if (!currentUserName.equals(previousUserName)) {
			NodeRef approvalListRef = getApprovalListRef(task);
			NodeRef approvalListItemRef = getApprovalListItemByUserName(approvalListRef, currentUserName);
			if (approvalListItemRef == null) {
				String newItemTitle = String.format(ASSEGNEE_ITEM_FORMAT, currentUserName);
				createApprovalListItem(approvalListRef, orgstructureService.getEmployeeByPerson(currentUserName), newItemTitle, dueDate);
			}
			NodeRef oldApprovalListItem = getApprovalListItemByUserName(approvalListRef, previousUserName);
			nodeService.setProperty(oldApprovalListItem, PROP_APPROVAL_ITEM_DECISION, DecisionResult.REASSIGNED.name());
			nodeService.setProperty(oldApprovalListItem, PROP_APPROVAL_ITEM_APPROVE_DATE, new Date());
		}

		DelegateExecution execution = task.getExecution();
		NodeRef bpmPackage = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantReviewerPermissions(employeeRef, bpmPackage);
		notifyApprovalStarted(employeeRef, dueDate, bpmPackage);
	}

	private NodeRef getApprovalListRef(DelegateTask task) {
		DelegateExecution execution = task.getExecution();
		return ((ActivitiScriptNode) execution.getVariable("approvalListRef")).getNodeRef();
	}

	@Override
	public void completeTask(NodeRef assignee, DelegateTask task) {
		TaskDecision taskDecision = new TaskDecision();
		DelegateExecution execution = task.getExecution();
		NodeRef bpmPackage = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		String commentFileAttachmentCategoryName = (String) execution.getVariable("commentFileAttachmentCategoryName");
		String documentProjectNumber = (String) execution.getVariable("documentProjectNumber");
		NodeRef approvalListRef = getApprovalListRef(task);
		String decision = (String) task.getVariableLocal("lecmApprove_approveTaskResult");
		ActivitiScriptNode commentRef = (ActivitiScriptNode) task.getVariableLocal("lecmApprove_approveTaskCommentAssoc");

		taskDecision.setUserName(task.getAssignee());
		taskDecision.setDecision(decision);
		taskDecision.setStartDate(task.getCreateTime());
		taskDecision.setComment((String) task.getVariableLocal("bpm_comment"));
		taskDecision.setCommentRef(commentRef != null ? commentRef.getNodeRef() : null);
		taskDecision.setDocumentRef(Utils.getDocumentFromBpmPackage(bpmPackage));
		taskDecision.setCommentFileAttachmentCategoryName(commentFileAttachmentCategoryName);
		taskDecision.setDocumentProjectNumber(documentProjectNumber);
		taskDecision.setDueDate((Date) nodeService.getProperty(assignee, PROP_ASSIGNEES_ITEM_DUE_DATE));
		taskDecision.setPreviousUserName((String) nodeService.getProperty(assignee, PROP_ASSIGNEES_ITEM_USERNAME));

		Map<String, String> decisionsMap = (Map<String, String>) execution.getVariable("decisionsMap");
		decisionsMap = addDecision(decisionsMap, taskDecision);
		execution.setVariable("decisionsMap", decisionsMap);

		logDecision(approvalListRef, taskDecision);
		execution.setVariable("taskDecision", decision);
	}

	private Map<String, String> addDecision(final Map<String, String> decisionMap, TaskDecision taskDecision) {
		Map<String, String> currentDecisionMap = (decisionMap == null) ? new HashMap<String, String>() : decisionMap;

		String userName = taskDecision.getUserName();
		String decision = taskDecision.getDecision();

		currentDecisionMap.put(userName, decision);
		return currentDecisionMap;
	}

	public NodeRef getApprovalListsFolder() {
		return getFolder(APPROVAL_LIST_FOLDER);
	}

	@Override
	public NodeRef getApprovalFolder() {
		return getFolder(APPROVAL_FOLDER);
	}

	@Override
	public List<NodeRef> createAssigneesList(NodeRef assigneesListNode) {
		return findNodesByAssociationRef(assigneesListNode, ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM, TYPE_ASSIGNEES_ITEM, ASSOCIATION_TYPE.TARGET);
	}
}
