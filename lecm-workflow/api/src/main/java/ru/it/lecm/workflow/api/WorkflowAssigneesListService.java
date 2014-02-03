package ru.it.lecm.workflow.api;

import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.AssigneesList;

/**
 *
 * @author vlevin
 */
public interface WorkflowAssigneesListService {

	NodeRef getAssigneesListsFolder();

	NodeRef getDefaultAssigneesList(String workflowType, String concurrency);

	void saveAssigneesList(NodeRef assigneesListRef, String assigneesListName);

	List<NodeRef> getAssingeesListsForCurrentEmployee(String workflowType, String concurrency);

	AssigneesList getAssigneesListDetail(NodeRef assingeesListRef);

	void clearDueDatesInAssigneesList(NodeRef assigneeListRef);

	void deleteAssigneesList(NodeRef assigneeListRef);

	boolean changeAssigneeOrder(NodeRef assigneeNodeRef, String direction);

	void calculateAssigneesListDates(NodeRef assigneeList, Date dueDate);

	int getAssigneesListItemOrder(NodeRef listItemNodeRef);

	void setAssigneesListItemOrder(NodeRef listItemNodeRef, int order);

	String getAssigneesListWorkflowType(NodeRef assigneesListRef);

	String getAssigneesListWorkflowConcurrency(NodeRef assigneesListRef);

	boolean isTempAssigneesList(NodeRef assigneeListRef);

	void clearAssigneesList(NodeRef assigneesListNodeRef);

	void setAssigneesListTemp(NodeRef assigneesListRef);

	Date getAssigneesListItemDueDate(NodeRef assigneeListItem);

	void setAssigneesListItemDueDate(NodeRef assigneeListItem, Date dueDate);

	void setDueDates(NodeRef assigneeListNodeRef, Date workflowDueDate);

}
