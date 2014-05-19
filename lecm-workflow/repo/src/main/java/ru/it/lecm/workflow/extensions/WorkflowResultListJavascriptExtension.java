package ru.it.lecm.workflow.extensions;

import java.util.Date;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.api.WorkflowResultListService;

/**
 *
 * @author vlevin
 */
public class WorkflowResultListJavascriptExtension extends BaseWebScript {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowResultListJavascriptExtension.class);

	private WorkflowResultListService workflowResultListService;
	private NamespaceService namespaceService;

	public void setWorkflowResultListService(WorkflowResultListService workflowResultListService) {
		this.workflowResultListService = workflowResultListService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public ScriptNode createResultList(ScriptNode parentRef, ScriptNode bpmPackage, String documentAttachmentCategoryName, String resultListType, String resultListName) {
		QName resultListQName = QName.createQName(resultListType, namespaceService);
		NodeRef resultList = workflowResultListService.createResultList(parentRef.getNodeRef(), bpmPackage.getNodeRef(), documentAttachmentCategoryName, resultListQName, resultListName);

		return new ScriptNode(resultList, serviceRegistry, getScope());
	}

	public ScriptNode createResultList(ScriptNode parentRef, String resultListType, String resultListName, String documentVersion) {
		QName resultListQName = QName.createQName(resultListType, namespaceService);
		NodeRef resultList = workflowResultListService.createResultList(parentRef.getNodeRef(), resultListQName, resultListName, documentVersion);

		return new ScriptNode(resultList, serviceRegistry, getScope());
	}

	public ScriptNode createResultItem(ScriptNode resultListRef, ScriptNode employeeRef, String itemTitle, Object jsDueDate, int order, String resultItemType) {
		Date dueDate = (Date) Context.jsToJava(jsDueDate, Date.class);

		return createResultItem(resultListRef, employeeRef, itemTitle, dueDate, order, resultItemType);
	}

	public ScriptNode createResultItem(ScriptNode resultListRef, ScriptNode employeeRef, String itemTitle, Date dueDate, int order, String resultItemType) {

		QName resultItemQName = QName.createQName(resultItemType, namespaceService);
		NodeRef resultItem = workflowResultListService.createResultItem(resultListRef.getNodeRef(), employeeRef.getNodeRef(), itemTitle, dueDate, order, resultItemQName);

		return new ScriptNode(resultItem, serviceRegistry, getScope());
	}

}
