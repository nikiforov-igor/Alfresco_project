package ru.it.lecm.workflow.approval;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.workflow.approval.api.ApprovalService;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import ru.it.lecm.workflow.approval.api.ApprovalResultModel;
import ru.it.lecm.workflow.DocumentInfo;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.api.WorkflowResultModel;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;

/**
 *
 * @author vlevin
 */
public class ApprovalServiceImpl extends WorkflowServiceAbstract implements ApprovalService {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalServiceImpl.class);
	private DocumentAttachmentsService documentAttachmentsService;

	private IWorkCalendar workCalendar;
	private WorkflowAssigneesListService workflowAssigneesListService;
	private BehaviourFilter behaviourFilter;

	private final static String CUSTOM_APPROVAL_FOLDER_NAME = "Специальное согласование";
	private final static String PARALLEL_APPROVAL_FOLDER_NAME = "Параллельное согласование";
	private final static String SEQUENTIAL_APPROVAL_FOLDER_NAME = "Последовательное согласование";
	private final static String APPROVAL_TYPE_SEQUENTIAL = "SEQUENTIAL";
	private final static String APPROVAL_TYPE_PARALLEL = "PARALLEL";
	private final static String APPROVAL_TYPE_CUSTOM = "CUSTOM";
	private final static String APPROVAL_LIST_NAME = "Лист согласования версия %s";

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setWorkCalendar(IWorkCalendar workCalendar) {
		this.workCalendar = workCalendar;
	}

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService assigneesListService) {
		this.workflowAssigneesListService = assigneesListService;
	}

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	private void logDecision(final NodeRef approvalListRef, final TaskDecision taskDecision) {
		Date startDate, completionDate;
		final String comment, decision, commentFileAttachmentCategoryName, documentProjectNumber;
		final NodeRef commentRef, documentRef, approvalListItemRef;
		final Map<QName, Serializable> properties;

		startDate = DateUtils.truncate(taskDecision.getStartDate(), Calendar.DATE);
		completionDate = DateUtils.truncate(new Date(), Calendar.DATE);
		comment = taskDecision.getComment();
		decision = taskDecision.getDecision();
		commentRef = taskDecision.getCommentRef();
		documentRef = taskDecision.getDocumentRef();
		commentFileAttachmentCategoryName = taskDecision.getCommentFileAttachmentCategoryName();
		documentProjectNumber = taskDecision.getDocumentProjectNumber();

		approvalListItemRef = workflowResultListService.getResultItemByTaskId(approvalListRef, taskDecision.getId());

		properties = nodeService.getProperties(approvalListItemRef);

		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_START_DATE, startDate);
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_FINISH_DATE, completionDate);
		properties.put(ApprovalResultModel.PROP_APPROVAL_ITEM_COMMENT, comment);
		properties.put(ApprovalResultModel.PROP_APPROVAL_ITEM_DECISION, decision);

		nodeService.setProperties(approvalListItemRef, properties);

		if (commentRef != null && documentRef != null && commentFileAttachmentCategoryName != null && documentProjectNumber != null) {
			final NodeRef attachmentCategoryRef = documentAttachmentsService.getCategory(commentFileAttachmentCategoryName, documentRef);
			if (attachmentCategoryRef != null) {
				StringBuilder commentFileName = new StringBuilder();
				commentFileName.append(documentProjectNumber);
				commentFileName.append(", ");

				commentFileName.append(new SimpleDateFormat("dd.MM.yyyy HH.mm").format(new Date())).append(" + ");
				commentFileName.append("Согласование сотрудником");

				NodeRef employeeRef = findNodeByAssociationRef(approvalListItemRef, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);

				if (employeeRef != null) {
					commentFileName.append(" ").append(nodeService.getProperty(employeeRef, ContentModel.PROP_NAME));
				}

				String commentFileNameStr = FileNameValidator.getValidFileName(commentFileName.toString());

				if (nodeService.getChildByName(attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentFileNameStr) != null) {
					int i = 0;
					do {
						i++;
					} while (nodeService.getChildByName(attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentFileNameStr + " " + i) != null);
					commentFileNameStr += " " + i;
				}

				final String commentFileNameFinal = commentFileNameStr;
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef>() {
					@Override
					public ChildAssociationRef execute() throws Throwable {
						nodeService.setProperty(commentRef, ContentModel.PROP_NAME, commentFileNameFinal);
						QName commentAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, commentFileNameFinal);
						return nodeService.moveNode(commentRef, attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentAssocQName);
					}
				}, false, true);

				List<NodeRef> targetRefs = new ArrayList<NodeRef>();
				targetRefs.add(commentRef);
				behaviourFilter.disableBehaviour(documentRef);
				behaviourFilter.disableBehaviour(commentRef);
				try {
					nodeService.setAssociations(approvalListItemRef, ApprovalResultModel.ASSOC_APPROVAL_ITEM_COMMENT, targetRefs);
				} finally {
					behaviourFilter.enableBehaviour(documentRef);
					behaviourFilter.enableBehaviour(commentRef);
				}
			}
		}
	}

	@Override
	public void logFinalDecision(final NodeRef approvalListRef, final String finalDecision) {
		Map<QName, Serializable> properties = nodeService.getProperties(approvalListRef);
		properties.put(ApprovalResultModel.PROP_APPROVAL_LIST_DECISION, finalDecision);
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_LIST_COMPLETE_DATE, new Date());
		nodeService.setProperties(approvalListRef, properties);
	}

	private void notifyAssigneeDeadline(final String processInstanceId, final WorkflowTask userTask, final DocumentInfo docInfo) {
		Map<QName, Serializable> props = userTask.getProperties();
		Date dueDate = (Date) props.get(org.alfresco.repo.workflow.WorkflowModel.PROP_DUE_DATE);
		String owner = (String) props.get(ContentModel.PROP_OWNER);
		if (docInfo.getDocumentRef() != null) {
			NodeRef employee = orgstructureService.getEmployeeByPerson(owner);
			List<NodeRef> recipients = new ArrayList<NodeRef>();
			recipients.add(employee);
			Date comingSoonDate = workCalendar.getEmployeePreviousWorkingDay(employee, dueDate, -1);
			Date currentDate = new Date();
			if (comingSoonDate != null) {
				int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
				int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
				Map<QName, Serializable> fakeProps = new HashMap<QName, Serializable>();
				if (!props.containsKey(FAKE_PROP_COMINGSOON) && comingSoon >= 0) {
					fakeProps.put(FAKE_PROP_COMINGSOON, "");
					String description = String.format("Напоминание: Вам необходимо согласовать проект документа %s, срок согласования %s", docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
					sendNotification(description, docInfo.getDocumentRef(), recipients);
				}
				if (!props.containsKey(FAKE_PROP_OVERDUE) && overdue > 0) {
					fakeProps.put(FAKE_PROP_OVERDUE, "");
					String description = String.format("Внимание: Вы не согласовали документ %s, срок согласования %s", docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
					sendNotification(description, docInfo.getDocumentRef(), recipients);
				}
				if (!fakeProps.isEmpty()) {
					workflowService.updateTask(userTask.getId(), fakeProps, null, null);
				}
			}
		} else {
			logger.error("Can't notify assignee {} about deadline, because there is no document in bpmPackage. Perhaps it was deleted. Check your workflow instance {}", owner, processInstanceId);
		}
	}

	@Override
	public void notifyAssigneesDeadline(final String processInstanceId, final NodeRef bpmPackage) {
		try {
			DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);

			WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
			taskQuery.setProcessId(processInstanceId);
			taskQuery.setTaskState(WorkflowTaskState.IN_PROGRESS);
			List<WorkflowTask> tasks = workflowService.queryTasks(taskQuery);
			for (WorkflowTask task : tasks) {
				logger.trace(task.toString());
				notifyAssigneeDeadline(processInstanceId, task, docInfo);
			}
		} catch (Exception ex) {
			logger.error("Internal error while notifying Assignees", ex);
		}
	}

	@Override
	public void notifyInitiatorDeadline(final String processInstanceId, final NodeRef bpmPackage, final VariableScope variableScope) {
		try {
			boolean isDocumentApproval = Utils.isDocument(Utils.getDocumentFromBpmPackage(bpmPackage));
			DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);
			if (docInfo.getDocumentRef() != null) {
				Set<NodeRef> recipients = new HashSet<NodeRef>();
				recipients.add(docInfo.getInitiatorRef());
				WorkflowInstance workflowInstance = workflowService.getWorkflowById(processInstanceId);
				Date dueDate = workflowInstance.getDueDate();
				Date comingSoonDate = workCalendar.getEmployeePreviousWorkingDay(docInfo.getInitiatorRef(), dueDate, -1);
				Date currentDate = new Date();
				if (comingSoonDate != null) {
					int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
					int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
					if (!variableScope.hasVariable("initiatorComingSoon") && comingSoon >= 0) {
						variableScope.setVariable("initiatorComingSoon", "");
						String description = String.format("Напоминание: Вы направили на согласование проект документа %s, срок согласования %s", docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
						sendNotification(description, docInfo.getDocumentRef(), new ArrayList<NodeRef>(recipients));
					}
					if (!variableScope.hasVariable("initiatorOverdue") && overdue > 0) {
						variableScope.setVariable("initiatorOverdue", "");
						String people = getIncompleteAssignees(processInstanceId);
						String description = String.format("Внимание: проект документа %s не согласован в срок %s. Следующие сотрудники не приняли решение: %s", docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate), people);
						if (isDocumentApproval) {
							//получить список кураторов и добавить его в recipients
							recipients.addAll(Utils.getCurators());
						}
						sendNotification(description, docInfo.getDocumentRef(), new ArrayList<NodeRef>(recipients));
					}
				}
			} else {
				logger.error("Can't notify initiators about deadline, because there is no document in bpmPackage. Perhaps it was deleted. Check your workflow instance {}", processInstanceId);
			}
		} catch (Exception ex) {
			logger.error("Internal error while notifying initiator and curators", ex);
		}
	}

	private NodeRef getOrCreateCustomApprovalFolder(NodeRef parentRef) {
		NodeRef customApprovalRef = getFolder(parentRef, CUSTOM_APPROVAL_FOLDER_NAME);
		if (customApprovalRef == null) {
			customApprovalRef = createFolder(parentRef, CUSTOM_APPROVAL_FOLDER_NAME);
		}
		return customApprovalRef;
	}

	private NodeRef getOrCreateParallelApprovalFolder(NodeRef parentRef) {
		NodeRef parallelApprovalRef = getFolder(parentRef, PARALLEL_APPROVAL_FOLDER_NAME);
		if (parallelApprovalRef == null) {
			parallelApprovalRef = createFolder(parentRef, PARALLEL_APPROVAL_FOLDER_NAME);
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
	public WorkflowTaskDecision completeTask(NodeRef assignee, DelegateTask task) {
		String decision = (String) task.getVariableLocal("lecmApprove2_approveTaskResult");
		ScriptNode commentScriptNode = (ScriptNode) task.getVariableLocal("lecmApprove2_approveTaskCommentAssoc");
		NodeRef commentRef = commentScriptNode != null ? commentScriptNode.getNodeRef() : null;
		Date dueDate = (Date) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);

		return completeTask(assignee, task, decision, commentRef, dueDate);
	}

	@Override
	public WorkflowTaskDecision completeTask(NodeRef assignee, DelegateTask task, String decision, NodeRef commentRef, Date dueDate) {
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		String commentFileAttachmentCategoryName = (String) task.getVariable("commentFileAttachmentCategoryName");

		String documentProjectNumber = documentService.getDocumentActualNumber(Utils.getDocumentFromBpmPackage(bpmPackage));

		TaskDecision taskDecision = new TaskDecision();
		taskDecision.setId(String.format("activiti$%s$%s", task.getProcessInstanceId(), task.getId()));
		taskDecision.setUserName(task.getAssignee());
		taskDecision.setDecision(decision);
		taskDecision.setStartDate(task.getCreateTime());
		taskDecision.setComment((String) task.getVariableLocal("bpm_comment"));
		taskDecision.setCommentRef(commentRef);
		taskDecision.setDocumentRef(Utils.getDocumentFromBpmPackage(bpmPackage));
		taskDecision.setCommentFileAttachmentCategoryName(commentFileAttachmentCategoryName);
		taskDecision.setDocumentProjectNumber(documentProjectNumber);
		taskDecision.setDueDate(dueDate);
		taskDecision.setPreviousUserName((String) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME));

		Map<String, String> decisionsMap = (Map<String, String>) task.getVariable("decisionsMap");
		decisionsMap = addDecision(decisionsMap, taskDecision);
		task.setVariable("decisionsMap", decisionsMap);

		NodeRef approvalListRef = workflowResultListService.getResultListRef(task);
		logDecision(approvalListRef, taskDecision);

		task.setVariable("taskDecision", decision);

		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());

		completeTaskAddMembers(employeeRef, bpmPackage, task);

		return taskDecision;
	}

	private Map<String, String> addDecision(final Map<String, String> decisionMap, TaskDecision taskDecision) {
		Map<String, String> currentDecisionMap = (decisionMap == null) ? new HashMap<String, String>() : decisionMap;

		String userName = taskDecision.getUserName();
		String decision = taskDecision.getDecision();

		currentDecisionMap.put(userName, decision);
		return currentDecisionMap;
	}

	@Override
	protected String getWorkflowStartedMessage(String documentLink, Date dueDate) {
		String dueDatemessage = dueDate == null ? "(нет)" : new SimpleDateFormat(DATE_FORMAT).format(dueDate);
		return String.format("Вам необходимо согласовать документ %s, срок согласования %s", documentLink, dueDatemessage);
	}

	@Override
	protected String getWorkflowFinishedMessage(String documentLink, String decision) {
		String decisionMsg;
		if (DecisionResult.APPROVED.name().equals(decision)) {
			decisionMsg = "согласовано";
		} else if (DecisionResult.APPROVED_WITH_REMARK.name().equals(decision)) {
			decisionMsg = "согласовано с замечаниями";
		} else if (DecisionResult.REJECTED.name().equals(decision)) {
			decisionMsg = "отклонено";
		} else if (DecisionResult.APPROVED_FORCE.name().equals(decision)) {
			decisionMsg = "принудительно завершено";
		} else if (DecisionResult.REJECTED_FORCE.name().equals(decision)) {
			decisionMsg = "отозвано с согласования";
		} else if (DecisionResult.NO_DECISION.name().equals(decision)) {
			decisionMsg = "решение не принято";
		} else {
			decisionMsg = "";
		}

		return String.format("Принято решение о документе %s: \"%s\"", documentLink, decisionMsg);

	}

	@Override
	public NodeRef getOrCreateApprovalFolderContainer(NodeRef parentRef) {
		NodeRef approvalRef = getFolder(parentRef, "Согласование");
		if (approvalRef == null) {
			approvalRef = createFolder(parentRef, "Согласование");
		}
		return approvalRef;
	}

	private NodeRef getOrCreateApprovalFolder(NodeRef parentRef, String approvalType) {
		NodeRef result = null;

		NodeRef approvalRef = getOrCreateApprovalFolderContainer(parentRef);

		if (APPROVAL_TYPE_PARALLEL.equals(approvalType)) {
			result = getOrCreateParallelApprovalFolder(approvalRef);
		} else if (APPROVAL_TYPE_SEQUENTIAL.equals(approvalType)) {
			result = getOrCreateSequentialApprovalFolder(approvalRef);
		} else if (APPROVAL_TYPE_CUSTOM.equals(approvalType)) {
			result = getOrCreateCustomApprovalFolder(approvalRef);
		}
		return result;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public void assignTask(NodeRef assignee, DelegateTask task) {
		actualizeTaskAssignee(assignee, task);
		Date dueDate = task.getDueDate();
		if (dueDate == null) {
			dueDate = workflowAssigneesListService.getAssigneesListItemDueDate(assignee);
			task.setDueDate(dueDate);
		}

		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantDynamicRole(employeeRef, bpmPackage, "DA_APPROVER_DYN");
		notifyWorkflowStarted(employeeRef, dueDate, bpmPackage);
	}

	@Override
	public NodeRef createApprovalList(NodeRef bpmPackage, String documentAttachmentCategoryName, String approvalType, List<NodeRef> assigneesList) {
		NodeRef resultListContainer = workflowResultListService.getOrCreateWorkflowResultFolder(bpmPackage);
		NodeRef approvalFolder = getOrCreateApprovalFolder(resultListContainer, approvalType);
		NodeRef approvalList = workflowResultListService.createResultList(approvalFolder, bpmPackage, documentAttachmentCategoryName, ApprovalResultModel.TYPE_APPROVAL_LIST, APPROVAL_LIST_NAME);

		workflowResultListService.prepareResultList(approvalList, assigneesList, ApprovalResultModel.TYPE_APPROVAL_ITEM);

		return approvalList;
	}

	@Override
	public List<NodeRef> createAssigneesList(NodeRef nodeRef, DelegateExecution execution) {
		return workflowAssigneesListService.createAssigneesListWorkingCopy(nodeRef, execution);
	}

	@Override
	public void deleteTempAssigneesList(DelegateExecution execution) {
		workflowAssigneesListService.deleteAssigneesListWorkingCopy(execution);
	}

	@Override
	public NodeRef getEmployeeForAssignee(NodeRef assignee) {
		return workflowAssigneesListService.getEmployeeByAssignee(assignee);
	}

}
