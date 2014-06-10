package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.RouteAspecsModel;
import ru.it.lecm.workflow.api.WorkflowType;
import ru.it.lecm.workflow.utils.WorkflowVariablesHelper;

/**
 *
 * @author vmalygin
 */
public class ApprovalWorkflowRunner extends AbstractWorkflowRunner {

	public static class ApprovalWorkflowType implements WorkflowType {

		@Override
		public String getWorkflowDefinitionId() {
			return "lecmApprovalWorkflow";
		}

		@Override
		public String getType() {
			return "APPROVAL";
		}
	}

	@Override
	protected Map<QName, Serializable> runImpl(final Map<String, Object> variables, final Map<QName, Serializable> properties) {
		//построение bpm:workflowDueDate и lecm-workflow:workflowAssigneesListAssocs
		NodeRef routeRef = WorkflowVariablesHelper.getRouteRef(variables);
		NodeRef assigneesListRef = routeService.getAssigneesListByWorkflowType(routeRef, workflowType.getType());
		Serializable concurrency = workflowAssigneesListService.getAssigneesListConcurrency(assigneesListRef);
		Date workflowDueDate = workflowAssigneesListService.calculateAssigneesDueDatesByCompletionDays(assigneesListRef);
		properties.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, workflowDueDate);
		properties.put(LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST, assigneesListRef);
		properties.put(LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY, concurrency);
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
		String extPresentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING);
		properties.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, "Согласование по документу: " + extPresentString);
		return properties;
	}

	@Override
	protected QName getWorkflowIdPropQName() {
		return RouteAspecsModel.PROP_APPROVAL_WORKFLOW_ID;
	}
}
