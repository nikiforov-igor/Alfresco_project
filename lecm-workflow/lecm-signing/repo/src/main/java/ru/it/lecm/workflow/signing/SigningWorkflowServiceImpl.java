package ru.it.lecm.workflow.signing;

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
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.workflow.DocumentInfo;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.WorkflowResultListService;
import ru.it.lecm.workflow.api.WorkflowResultModel;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;
import ru.it.lecm.workflow.signing.api.SigningWorkflowModel;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

/**
 *
 * @author vmalygin
 */
public class SigningWorkflowServiceImpl extends WorkflowServiceAbstract implements SigningWorkflowService {

	private final static String RESULT_LIST_NAME_FORMAT = "Лист подписания версии %s";
	private final static Logger logger = LoggerFactory.getLogger(SigningWorkflowServiceImpl.class);

	private IWorkCalendar workCalendarService;
	private WorkflowResultListService workflowResultListService;

	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
	}

	public void setWorkflowResultListService(WorkflowResultListService workflowResultListService) {
		this.workflowResultListService = workflowResultListService;
	}

	@Override
	public void assignTask(NodeRef assignee, DelegateTask task) {
		Date dueDate = task.getDueDate();
		if (dueDate == null) {
			dueDate = (Date) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);
			task.setDueDate(dueDate);
		}
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantReaderPermissions(employeeRef, bpmPackage, true);
		notifyWorkflowStarted(employeeRef, dueDate, bpmPackage);
	}

	@Override
	public WorkflowTaskDecision completeTask(NodeRef assignee, DelegateTask task) {
		String decisionMessage;
		String decision = (String) task.getVariableLocal("lecmSign_signTaskResult");
		Date dueDate = (Date) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();

		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);
		NodeRef initiatorRef = docInfo.getInitiatorRef();
		List<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(initiatorRef);

		if (DecisionResult.SIGNED.name().equals(decision)) {
			decisionMessage = "подписал проект";
		} else if (DecisionResult.REJECTED.name().equals(decision)) {
			decisionMessage = "отклонил подписание проекта";
		} else {
			decisionMessage = "";
		}

		NodeRef employee = orgstructureService.getEmployeeByPerson(task.getAssignee());
		String name = (String) nodeService.getProperty(employee, ContentModel.PROP_NAME);
		String message = String.format("Сотрудник %s %s документа %s", name, decisionMessage, docInfo.getDocumentLink());
		sendNotification(message, docInfo.getDocumentRef(), recipients);

		WorkflowTaskDecision taskDecision = new WorkflowTaskDecision();
		taskDecision.setUserName(task.getAssignee());
		taskDecision.setDecision(decision);
		taskDecision.setStartDate(task.getCreateTime());
		taskDecision.setDueDate(dueDate);
		taskDecision.setPreviousUserName((String) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME));

		Map<String, String> decisionsMap = (Map<String, String>) task.getVariable("decisionsMap");
		decisionsMap = addDecision(decisionsMap, taskDecision);
		task.setVariable("decisionsMap", decisionsMap);//decisionsMap может быть null, поэтому если она создана, ее надо перезаписать
		task.setVariable("taskDecision", decision);

		return  taskDecision;
	}

	@Override
	public void logDecision(final NodeRef resultListRef, final WorkflowTaskDecision taskDecision) {
		final NodeRef resultListItemRef;
		final Map<QName, Serializable> properties;

		resultListItemRef = workflowResultListService.getResultItemByUserName(resultListRef, taskDecision.getUserName());

		properties = nodeService.getProperties(resultListItemRef);

		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_START_DATE, DateUtils.truncate(taskDecision.getStartDate(), Calendar.DATE));
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_FINISH_DATE, DateUtils.truncate(new Date(), Calendar.DATE));
		properties.put(SigningWorkflowModel.PROP_SIGN_RESULT_ITEM_DECISION, taskDecision.getDecision());

		nodeService.setProperties(resultListItemRef, properties);
	}

	private void notifyAssigneeDeadline(final String processInstanceId, final WorkflowTask userTask, final DocumentInfo docInfo) {
		Map<QName, Serializable> props = userTask.getProperties();
		Date dueDate = (Date) props.get(org.alfresco.repo.workflow.WorkflowModel.PROP_DUE_DATE);
		String owner = (String) props.get(ContentModel.PROP_OWNER);
		if (docInfo.getDocumentRef() != null) {
			NodeRef employee = orgstructureService.getEmployeeByPerson(owner);
			List<NodeRef> recipients = new ArrayList<NodeRef>();
			recipients.add(employee);
			Date comingSoonDate = workCalendarService.getEmployeePreviousWorkingDay(employee, dueDate, -1);
			Date currentDate = new Date();
			if (comingSoonDate != null) {
				int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
				int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
				Map<QName, Serializable> fakeProps = new HashMap<QName, Serializable>();
				if (!props.containsKey(FAKE_PROP_COMINGSOON) && comingSoon >= 0) {
					fakeProps.put(FAKE_PROP_COMINGSOON, "");
					String template = "Напоминание: Вам необходимо подписать проект документа %s, срок подписания %s";
					String message = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
					sendNotification(message, docInfo.getDocumentRef(), recipients);
				}
				if (!props.containsKey(FAKE_PROP_OVERDUE) && overdue > 0) {
					fakeProps.put(FAKE_PROP_OVERDUE, "");
					String template = "Внимание: Вы не подписали документ %s, срок подписания %s";
					String message = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
					sendNotification(message, docInfo.getDocumentRef(), recipients);
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
			logger.error("Internal error while notifying Assignees in Signing Workflow", ex);
		}
	}

	@Override
	public void notifyInitiatorDeadline(String processInstanceId, NodeRef bpmPackage, VariableScope variableScope) {
		try {
			DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);
			if (docInfo.getDocumentRef() != null) {
				Set<NodeRef> recipients = new HashSet<NodeRef>();
				recipients.add(docInfo.getInitiatorRef());
				WorkflowInstance workflowInstance = workflowService.getWorkflowById(processInstanceId);
				Date dueDate = workflowInstance.getDueDate();
				Date comingSoonDate = workCalendarService.getEmployeePreviousWorkingDay(docInfo.getInitiatorRef(), dueDate, -1);
				Date currentDate = new Date();
				if (comingSoonDate != null) {
					int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
					int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
					if (!variableScope.hasVariable("initiatorComingSoon") && comingSoon >= 0) {
						variableScope.setVariable("initiatorComingSoon", "");
						String template = "Напоминание: Вы направили на подписание проект документа %s, срок подписания %s";
						String message = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
						sendNotification(message, docInfo.getDocumentRef(), new ArrayList<NodeRef>(recipients));
					}
					if (!variableScope.hasVariable("initiatorOverdue") && overdue > 0) {
						variableScope.setVariable("initiatorOverdue", "");
						String people = getIncompleteAssignees(processInstanceId);
						String template = "Внимание: проект документа %s не подписан в срок %s. Следующие сотрудники не приняли решение: %s";
						String message = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate), people);
						sendNotification(message, docInfo.getDocumentRef(), new ArrayList<NodeRef>(recipients));
					}
				}
			} else {
				logger.error("Can't notify initiators about deadline, because there is no document in bpmPackage. Perhaps it was deleted. Check your workflow instance {}", processInstanceId);
			}
		} catch (Exception ex) {
			logger.error("Internal error while notifying initiator and curators", ex);
		}
	}

	@Override
	protected String getWorkflowStartedMessage(String documentLink, Date dueDate) {
		return String.format("Вам необходимо подписать документ %s, срок подписания %s", documentLink, new SimpleDateFormat(DATE_FORMAT).format(dueDate));
	}

	@Override
	protected String getWorkflowFinishedMessage(String documentLink, String decisionCode) {
		String decision;
		if (DecisionResult.SIGNED.name().equals(decisionCode)) {
			decision = "подписано";
		} else if (DecisionResult.REJECTED.name().equals(decisionCode)) {
			decision = "отклонено";
		} else if (DecisionResult.NO_DECISION.name().equals(decisionCode)) {
			decision = "решение не принято";
		} else {
			decision = "";
		}

		return String.format("Принято решение о документе %s: \"%s\"", documentLink, decision);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public void logFinalDecision(final NodeRef resultListRef, final String finalDecision) {
		nodeService.setProperty(resultListRef, WorkflowResultModel.PROP_WORKFLOW_RESULT_LIST_COMPLETE_DATE, new Date());
		nodeService.setProperty(resultListRef, SigningWorkflowModel.PROP_SIGN_RESULT_LIST_DECISION, finalDecision);
	}

	@Override
	public void dropSigningResults(final NodeRef resultListRef) {
		nodeService.addAspect(resultListRef, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(resultListRef);
	}

	@Override
	public NodeRef createResultList(NodeRef bpmPackage, String documentAttachmentCategoryName, List<NodeRef> assigneesList) {
		NodeRef resultListContainer = workflowResultListService.getOrCreateWorkflowResultFolder(bpmPackage);
		NodeRef resultListRoot = getOrCreateSigningFolderContainer(resultListContainer);

		NodeRef resultList = workflowResultListService.createResultList(resultListRoot, bpmPackage, documentAttachmentCategoryName, SigningWorkflowModel.TYPE_SIGN_RESULT_LIST, RESULT_LIST_NAME_FORMAT);
		workflowResultListService.prepareResultList(resultList, assigneesList, SigningWorkflowModel.TYPE_SIGN_RESULT_ITEM);
		return resultList;
	}

	@Override
	public NodeRef getOrCreateSigningFolderContainer(NodeRef parentRef) {
		NodeRef resultListRoot = getFolder(parentRef, "Подписание");
		if (resultListRoot == null) {
			resultListRoot = createFolder(parentRef, "Подписание");
		}
		return resultListRoot;
	}
}
