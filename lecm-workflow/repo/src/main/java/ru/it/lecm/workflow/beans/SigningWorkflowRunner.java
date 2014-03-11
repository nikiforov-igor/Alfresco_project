package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.workflow.AssigneesList;
import ru.it.lecm.workflow.AssigneesListItem;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.RouteAspecsModel;
import ru.it.lecm.workflow.utils.WorkflowVariablesHelper;

/**
 *
 * @author vmalygin
 */
public class SigningWorkflowRunner extends AbstractWorkflowRunner {

	@Override
	protected Map<QName, Serializable> runImpl(Map<String, Object> variables, Map<QName, Serializable> properties) {
		//TODO: построение bpm:workflowDueDate и lecm-workflow:workflowAssigneesListAssocs
		NodeRef routeRef = WorkflowVariablesHelper.getRouteRef(variables);
		NodeRef assigneesListRef = routeService.getAssigneesListByWorkflowType(routeRef, workflowType.name());
		//TODO: переделать расчет дат по нормальному
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

		properties.put(LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY, "SEQUENTIAL"); //временно проверить работоспособность
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
		String extPresentString = (String)nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING);
		properties.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, "Подписание по документу: " + extPresentString);
		return properties;
	}

	@Override
	protected QName getWorkflowIdPropQName() {
		return RouteAspecsModel.PROP_SIGNING_WORKFLOW_ID;
	}
}
