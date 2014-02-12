package ru.it.lecm.workflow.api;

import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public interface WorkflowResultListService {

	NodeRef createResultItem(NodeRef resultListRef, NodeRef employeeRef, String itemTitle, Date dueDate, QName resultItemType);

	NodeRef createResultList(final NodeRef parentRef, final NodeRef bpmPackage, final String documentAttachmentCategoryName, QName resultListType, String resultListName);

	NodeRef getOrCreateWorkflowResultFolder(NodeRef bpmPackage);

	NodeRef getResultItemByUserName(NodeRef resultListRef, String userName);

	NodeRef getResultListRef(DelegateTask task);

	NodeRef getServiceRootFolder();

	void prepareResultList(final NodeRef emptyResultList, final List<NodeRef> assigneesList, final QName resultItemType);

	void setResultListCompleteDate(final NodeRef resultList, Date finishDate);

}
