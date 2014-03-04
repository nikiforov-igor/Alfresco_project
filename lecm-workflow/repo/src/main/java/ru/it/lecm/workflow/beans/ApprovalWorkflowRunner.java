package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import ru.it.lecm.workflow.AssigneesList;
import ru.it.lecm.workflow.AssigneesListItem;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.utils.WorkflowVariablesHelper;

/**
 *
 * @author vmalygin
 */
public class ApprovalWorkflowRunner extends AbstractWorkflowRunner {

	@Override
	public String run(Map<String, Object> variables) {
		checkMandatoryVariables(variables);
		//формирование bpmPackage
		Map<QName, Serializable> properties = getInitialWorkflowProperties(variables);
		//получение workflowDefinition
		WorkflowDefinition workflowDefinition = getWorkflowDefinition(variables);

		//построение bpm:workflowDueDate и lecm-workflow:workflowAssigneesListAssocs
		NodeRef routeRef = WorkflowVariablesHelper.getRouteRef(variables);
		NodeRef assigneesListRef = routeService.getAssigneesListByWorkflowType(routeRef, workflowType.name());
		AssigneesList assigneesList = workflowAssigneesListService.getAssigneesListDetail(assigneesListRef);
		List<AssigneesListItem> items = assigneesList.getListItems();
		int daysToComplete = 0;
		for (AssigneesListItem item : items) {
			daysToComplete += item.getDaysToComplete();
		}
		Date workflowDueDate = DateUtils.addDays(new Date(), daysToComplete);
		workflowAssigneesListService.calculateAssigneesListDueDates(assigneesListRef, workflowDueDate); //а вот хрен знает а надо ли так делать????
		properties.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, workflowDueDate);
		properties.put(LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST, assigneesListRef);
		// start the workflow
		WorkflowInstance workflowInstance = startWorkflow(workflowDefinition, properties);
		//инициализировать входные переменные
		setInputVariables(workflowInstance.getId(), variables);
		// log to business journal
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
		logStartWorkflowEvent(documentRef, workflowInstance);


		return workflowInstance.getId();
	}

}
