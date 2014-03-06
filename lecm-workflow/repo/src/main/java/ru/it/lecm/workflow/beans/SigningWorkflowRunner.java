package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.utils.WorkflowVariablesHelper;

/**
 *
 * @author vmalygin
 */
public class SigningWorkflowRunner extends AbstractWorkflowRunner {

	@Override
	protected Map<QName, Serializable> runImpl(Map<String, Object> variables, Map<QName, Serializable> properties) {
		//TODO: построение bpm:workflowDueDate и lecm-workflow:workflowAssigneesListAssocs

		properties.put(LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY, "SEQUENTIAL"); //временно проверить работоспособность
		NodeRef documentRef = WorkflowVariablesHelper.getDocumentRef(variables);
		properties.put(LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY, "SEQUENTIAL"); //временно проверить работоспособность
		String extPresentString = (String)nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING);
		properties.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, "Подписание по документу: " + extPresentString);
		return properties;
	}
}
