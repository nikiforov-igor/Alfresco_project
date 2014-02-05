package ru.it.lecm.workflow.api;

import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.AssigneesList;

/**
 *
 * @author vlevin
 */
public interface WorkflowAssigneesListService {

	NodeRef getAssigneesListsFolder(); // NodeRef getListsFolderRef();

	NodeRef getDefaultAssigneesList(String workflowType, String concurrency); // NodeRef getDefaultListFolderRef();

	void saveAssigneesList(NodeRef assigneesListRef, String assigneesListName); // String save(final JSONObject json);

	List<NodeRef> getAssingeesListsForCurrentEmployee(String workflowType, String concurrency); // JSONObject getAssigneesLists();

	AssigneesList getAssigneesListDetail(NodeRef assingeesListRef); // JSONObject getListContents(final JSONObject json); (getAssigneesListContents)

	void clearDueDatesInAssigneesList(NodeRef assigneeListRef); //void clearDueDates(final JSONObject json);

	void deleteAssigneesList(NodeRef assigneeListRef); // void deleteList(final JSONObject json);

	boolean changeAssigneeOrder(NodeRef assigneeNodeRef, String direction); // JSONObject changeOrder(final JSONObject json);

	void calculateAssigneesListDueDates(NodeRef assigneeList, Date dueDate); //calculateApprovalDueDate(JSONObject json);

	int getAssigneesListItemOrder(NodeRef listItemNodeRef);

	void setAssigneesListItemOrder(NodeRef listItemNodeRef, int order);

	String getAssigneesListWorkflowType(NodeRef assigneesListRef);

	String getAssigneesListWorkflowConcurrency(NodeRef assigneesListRef);

	boolean isTempAssigneesList(NodeRef assigneeListRef);

	void clearAssigneesList(NodeRef assigneesListNodeRef); // void clearAssigneesList(JSONObject json);

	void setAssigneesListTemp(NodeRef assigneesListRef);

	Date getAssigneesListItemDueDate(NodeRef assigneeListItem);

	void setAssigneesListItemDueDate(NodeRef assigneeListItem, Date dueDate);

	void setDueDates(NodeRef assigneeListNodeRef, Date workflowDueDate); // void setDueDates(JSONObject json);

	String getNodeRefName(NodeRef nodeRef);

	NodeRef getAssigneesListByItem(NodeRef assigneeListItem);

	/**
	 * Создание рабочей копии списка исполнителей для использования ее в инстансе регламента
	 * Получение коллекции исполнителей регламента
	 * @param assigneesListNode изначальный список, на основе которого строится рабочая копия
	 * @param execution контекст регламента
	 * @return коллекция исполнителей регламента
	 */
	List<NodeRef> createAssigneesListWorkingCopy(NodeRef assigneesListNode, DelegateExecution execution);

	/**
	 * удаление рабочей копии списка исполнителей
	 * @param execution контекст регламента
	 */
	void deleteAssigneesListWorkingCopy(DelegateExecution execution);
}
