package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.api.WorkflowFoldersService;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.WorkflowResultListService;
import ru.it.lecm.workflow.api.WorkflowResultModel;
import static ru.it.lecm.workflow.beans.WorkflowServiceAbstract.RESULT_ITEM_FORMAT;

/**
 *
 * @author vmalygin
 */
public class WorkflowResultListServiceImpl extends BaseBean implements WorkflowResultListService {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowResultListServiceImpl.class);

	private WorkflowFoldersService workflowFoldersService;

	public void setWorkflowFoldersService(WorkflowFoldersService workflowFoldersService) {
		this.workflowFoldersService = workflowFoldersService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public NodeRef getResultListRef(DelegateTask task) {
		DelegateExecution execution = task.getExecution();
		return ((ScriptNode) execution.getVariable("resultListRef")).getNodeRef();
	}

	public NodeRef getResultItemByUserName(NodeRef resultListRef, String userName) {
		String itemTitle = String.format(RESULT_ITEM_FORMAT, userName);

		return nodeService.getChildByName(resultListRef, ContentModel.ASSOC_CONTAINS, itemTitle);
	}

	public NodeRef createResultItem(NodeRef approvalListRef, NodeRef employeeRef, String itemTitle, Date dueDate, QName resultItemType) {
		NodeRef approvalListItemRef;
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_TITLE, itemTitle);
		properties.put(ContentModel.PROP_NAME, itemTitle);
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_DUE_DATE, dueDate);

		QName assocQName = QName.createQName(WorkflowResultModel.WORKFLOW_RESULT_NAMESPACE, itemTitle);
		approvalListItemRef = nodeService.createNode(approvalListRef, ContentModel.ASSOC_CONTAINS, assocQName, resultItemType, properties).getChildRef();
		if (employeeRef != null) {
			List<NodeRef> targetRefs = new ArrayList<NodeRef>();
			targetRefs.add(employeeRef);
			nodeService.setAssociations(approvalListItemRef, WorkflowResultModel.ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE, targetRefs);
		}
		return approvalListItemRef;
	}

	public void prepareResultList(final NodeRef emptyResultList, final ActivitiScriptNodeList assigneesList, final QName resultItemType) {
		for (ActivitiScriptNode assignee : assigneesList) {
			NodeRef assigneeNode = assignee.getNodeRef();
			NodeRef employeeRef = findNodeByAssociationRef(assigneeNode, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			Date dueDate = (Date) nodeService.getProperty(assigneeNode, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);
			String userName = (String) nodeService.getProperty(assigneeNode, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME);

			String itemTitle = String.format(RESULT_ITEM_FORMAT, userName);

			createResultItem(emptyResultList, employeeRef, itemTitle, dueDate, resultItemType);
		}
	}

	public NodeRef createResultList(final NodeRef parentRef, final NodeRef bpmPackage, final String documentAttachmentCategoryName, QName resultListType, String resultListName) {
		String contractDocumentVersion = Utils.getObjectVersion(bpmPackage, documentAttachmentCategoryName);
		String resultListVersion = Utils.getResultListVersion(contractDocumentVersion, parentRef, resultListName);
		String localName = String.format(resultListName, resultListVersion);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, localName);
		properties.put(ContentModel.PROP_TITLE, localName);
		properties.put(WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_START_DATE, DateUtils.truncate(new Date(), Calendar.DATE));
		properties.put(WorkflowResultModel.PROP_WORKFLOW_LIST_DOCUMENT_VERSION, contractDocumentVersion);
		QName assocQName = QName.createQName(WorkflowResultModel.WORKFLOW_RESULT_NAMESPACE, localName);
		NodeRef resultListRef = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, resultListType, properties).getChildRef();
		//прикрепляем approval list к списку items у документа
		QName qname = QName.createQName(WorkflowResultModel.WORKFLOW_RESULT_NAMESPACE, localName);
		nodeService.addChild(bpmPackage, resultListRef, ContentModel.ASSOC_CONTAINS, qname);
		return resultListRef;
	}

	public NodeRef getOrCreateWorkflowResultFolder(NodeRef bpmPackage) {
		NodeRef workflowResultRoot;

		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		NodeRef contentRef = Utils.getContentFromBpmPackage(bpmPackage);

		if (documentRef != null) {
			workflowResultRoot = documentRef;
		} else if (contentRef != null) {
			String nodeUUID = (String) nodeService.getProperty(contentRef, ContentModel.PROP_NODE_UUID);
			NodeRef globalWorkflowResultFolder = workflowFoldersService.getGlobalResultFolder();
			workflowResultRoot = getFolder(globalWorkflowResultFolder, nodeUUID);
			if (workflowResultRoot == null) {
				workflowResultRoot = createFolder(globalWorkflowResultFolder, nodeUUID);
			}
		} else {
			logger.error("There is no any lecm-contract:document nor cm:content  in bpm:package");
			return null;
		}

		return workflowResultRoot;
	}
}
