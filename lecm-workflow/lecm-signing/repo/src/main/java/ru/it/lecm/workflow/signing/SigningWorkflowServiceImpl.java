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
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.workflow.DocumentInfo;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

/**
 *
 * @author vmalygin
 */
public class SigningWorkflowServiceImpl extends WorkflowServiceAbstract implements SigningWorkflowService {

	private final static Logger logger = LoggerFactory.getLogger(SigningWorkflowServiceImpl.class);

	private IWorkCalendar workCalendarService;

	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
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
		grantReviewerPermissions(employeeRef, bpmPackage);
		notifyWorkflowStarted(employeeRef, dueDate, bpmPackage);
	}

	@Override
	public void completeTask(NodeRef assignee, DelegateTask task) {
		String decision = (String) task.getVariableLocal("lecmSign_signTaskResult");
		Date dueDate = (Date) nodeService.getProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);

		completeTask(assignee, task, decision, dueDate);
	}

	private void completeTask(NodeRef assignee, DelegateTask task, String decision, Date dueDate) {
		DelegateExecution execution = task.getExecution();
		NodeRef bpmPackage = ((ScriptNode) execution.getVariable("bpm_package")).getNodeRef();

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

		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		revokeReviewerPermissions(employeeRef, bpmPackage);
		grantReaderPermissions(employeeRef, bpmPackage);
	}

	private void notifyAssigneeDeadline(WorkflowTask userTask, final DocumentInfo docInfo) {
		Map<QName, Serializable> props = userTask.getProperties();
		Date dueDate = (Date) props.get(org.alfresco.repo.workflow.WorkflowModel.PROP_DUE_DATE);
		String owner = (String) props.get(ContentModel.PROP_OWNER);
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
				notifyAssigneeDeadline(task, docInfo);
			}
		} catch (Exception ex) {
			logger.error("Internal error while notifying Assignees in Signing Workflow", ex);
		}
	}

	@Override
	public void notifyInitiatorDeadline(String processInstanceId, NodeRef bpmPackage, VariableScope variableScope) {
		try {
			DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);
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
		} catch (Exception ex) {
			logger.error("Internal error while notifying initiator and curators", ex);
		}
	}

	@Override
	protected String getWorkflowStartedMessage(String documentLink, Date dueDate) {
		return "";
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
}
