package ru.it.lecm.workflow.review;

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
import org.activiti.engine.delegate.DelegateExecution;
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
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.api.WorkflowResultListService;
import ru.it.lecm.workflow.api.WorkflowResultModel;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;
import ru.it.lecm.workflow.review.api.ReviewWorkflowModel;
import ru.it.lecm.workflow.review.api.ReviewWorkflowService;

/**
 *
 * @author vlevin
 */
public class ReviewWorkflowServiceImpl extends WorkflowServiceAbstract implements ReviewWorkflowService {

	private final static String RESULT_LIST_NAME_FORMAT = "Лист ознакомления версии %s";
	private final static String WORKFLOW_FINISHED_MESSAGE_FORMAT = "Закончено ознакомление с документом %s";
	private final static String WORKFLOW_STARTED_MESSAGE_FORMAT = "Вам необходимо ознакомиться с документом %s, срок ознакомления %s";
	private final static Logger logger = LoggerFactory.getLogger(ReviewWorkflowServiceImpl.class);
	private WorkflowResultListService resultListService;
	private WorkflowAssigneesListService assigneesListService;
	private IWorkCalendar workCalendarService;

	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
	}

	public void setResultListService(WorkflowResultListService resultListService) {
		this.resultListService = resultListService;
	}

	public void setAssigneesListService(WorkflowAssigneesListService assigneesListService) {
		this.assigneesListService = assigneesListService;
	}

	@Override
	protected String getWorkflowStartedMessage(String documentLink, Date dueDate) {
		String dueDatemessage = (dueDate == null) ? "(нет)" : new SimpleDateFormat(DATE_FORMAT).format(dueDate);
		return String.format(WORKFLOW_STARTED_MESSAGE_FORMAT, documentLink, dueDatemessage);
	}

	@Override
	protected String getWorkflowFinishedMessage(String documentLink, String decision) {
		return String.format(WORKFLOW_FINISHED_MESSAGE_FORMAT, documentLink);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public void assignTask(NodeRef assignee, DelegateTask task) {
		Date dueDate = task.getDueDate();
		if (dueDate == null) {
			dueDate = (Date) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);
			task.setDueDate(dueDate);
		}

		DelegateExecution execution = task.getExecution();
		NodeRef bpmPackage = ((ScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantReaderPermissions(employeeRef, bpmPackage, false);
		notifyWorkflowStarted(employeeRef, dueDate, bpmPackage);
	}

	@Override
	public WorkflowTaskDecision completeTask(NodeRef assignee, DelegateTask task) {
		String decision = (String) task.getVariableLocal("lecmReview_reviewTaskResult");
		Date dueDate = (Date) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);

		DelegateExecution execution = task.getExecution();

		WorkflowTaskDecision taskDecision = new WorkflowTaskDecision();
		taskDecision.setUserName(task.getAssignee());
		taskDecision.setDecision(decision);
		taskDecision.setStartDate(task.getCreateTime());
		taskDecision.setDueDate(dueDate);
		taskDecision.setPreviousUserName((String) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME));

		Map<String, String> decisionsMap = (Map<String, String>) execution.getVariable("decisionsMap");
		decisionsMap = addDecision(decisionsMap, taskDecision);
		execution.setVariable("decisionsMap", decisionsMap);

		execution.setVariable("taskDecision", decision);
		NodeRef resultListRef = resultListService.getResultListRef(task);
		logDecision(resultListRef, taskDecision);

		return taskDecision;
	}

	private void logDecision(final NodeRef resultListRef, final WorkflowTaskDecision taskDecision) {
		final NodeRef resultListItemRef;
		final Map<QName, Serializable> properties;

		resultListItemRef = resultListService.getResultItemByUserName(resultListRef, taskDecision.getUserName());

		properties = nodeService.getProperties(resultListItemRef);

		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_START_DATE, DateUtils.truncate(taskDecision.getStartDate(), Calendar.DATE));
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_FINISH_DATE, DateUtils.truncate(new Date(), Calendar.DATE));
		properties.put(ReviewWorkflowModel.PROP_REVIEW_RESULT_ITEM_RESULT, taskDecision.getDecision());

		nodeService.setProperties(resultListItemRef, properties);
	}

	@Override
	public void notifyAssigneesDeadline(String processInstanceId, NodeRef bpmPackage) {
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
			logger.error("Internal error while notifying Assignees in Review Workflow", ex);
		}
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
					String template = "Напоминание: Вам необходимо ознакомиться с документом %s, срок ознакомления %s";
					String description = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
					sendNotification(description, docInfo.getDocumentRef(), recipients);
				}
				if (!props.containsKey(FAKE_PROP_OVERDUE) && overdue > 0) {
					fakeProps.put(FAKE_PROP_OVERDUE, "");
					String template = "Внимание: Вы не ознакомились с документом %s, срок ознакомления %s";
					String description = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
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
						String template = "Напоминание: Вы направили на ознакомление документ %s, срок подписания %s";
						String description = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate));
						sendNotification(description, docInfo.getDocumentRef(), new ArrayList<NodeRef>(recipients));
					}
					if (!variableScope.hasVariable("initiatorOverdue") && overdue > 0) {
						variableScope.setVariable("initiatorOverdue", "");
						String people = getIncompleteAssignees(processInstanceId);
						String template = "Внимание: с документом %s не ознакомились в срок %s. Следующие сотрудники не ознакомились: %s";
						String description = String.format(template, docInfo.getDocumentLink(), new SimpleDateFormat(DATE_FORMAT).format(dueDate), people);
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

	@Override
	public NodeRef createResultList(NodeRef bpmPackage, String documentAttachmentCategoryName, List<NodeRef> assigneesList) {
		NodeRef resultListContainer = resultListService.getOrCreateWorkflowResultFolder(bpmPackage);
		NodeRef resultListRoot = getFolder(resultListContainer, "Ознакомление");
		if (resultListRoot == null) {
			resultListRoot = createFolder(resultListContainer, "Ознакомление");
		}

		NodeRef resultList = resultListService.createResultList(resultListRoot, bpmPackage, documentAttachmentCategoryName, WorkflowResultModel.TYPE_WORKFLOW_RESULT_LIST, RESULT_LIST_NAME_FORMAT);

		resultListService.prepareResultList(resultList, assigneesList, ReviewWorkflowModel.TYPE_REVIEW_RESULT_ITEM);

		return resultList;

	}

	@Override
	public List<NodeRef> createAssigneesList(NodeRef nodeRef, DelegateExecution execution) {
		return assigneesListService.createAssigneesListWorkingCopy(nodeRef, execution);
	}

	@Override
	public void deleteAssigneesListWorkingCopy(DelegateExecution execution) {
		assigneesListService.deleteAssigneesListWorkingCopy(execution);
	}

	@Override
	public void logWorkflowFinished(NodeRef resultList) {
		resultListService.setResultListCompleteDate(resultList, DateUtils.truncate(new Date(), Calendar.DATE));
	}

	@Override
	public void sendBareNotifications(List<NodeRef> assigneesList, Date workflowDueDate, NodeRef bpmPackage) {
		for (NodeRef assignee : assigneesList) {
			NodeRef employeeRef = assigneesListService.getEmployeeByAssignee(assignee);
			grantReaderPermissions(employeeRef, bpmPackage, false);
			notifyWorkflowStarted(employeeRef, workflowDueDate, bpmPackage);
		}
	}

}
