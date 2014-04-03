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

	NodeRef getDefaultAssigneesList(String workflowType); // NodeRef getDefaultListFolderRef();

	NodeRef getDefaultAssigneesList(NodeRef parentRef, String workflowType);

	void saveAssigneesList(NodeRef assigneesListRef, String assigneesListName); // String save(final JSONObject json);

	List<NodeRef> getAssingeesListsForCurrentEmployee(String workflowType); // JSONObject getAssigneesLists();

	List<NodeRef> getAssingeesListsForCurrentEmployee(NodeRef parentRef, String workflowType);

	AssigneesList getAssigneesListDetail(NodeRef assingeesListRef); // JSONObject getListContents(final JSONObject json); (getAssigneesListContents)

	void clearDueDatesInAssigneesList(NodeRef assigneeListRef); //void clearDueDates(final JSONObject json);

	void deleteAssigneesList(NodeRef assigneeListRef); // void deleteList(final JSONObject json);

	boolean changeAssigneeOrder(NodeRef assigneeNodeRef, String direction); // JSONObject changeOrder(final JSONObject json);

	int getAssigneesListItemOrder(NodeRef listItemNodeRef);

	void setAssigneesListItemOrder(NodeRef listItemNodeRef, int order);

	String getAssigneesListWorkflowType(NodeRef assigneesListRef);

	boolean isTempAssigneesList(NodeRef assigneeListRef);

	void clearAssigneesList(NodeRef assigneesListNodeRef); // void clearAssigneesList(JSONObject json);

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

	String getAssigneesListItemUserName(NodeRef assigneeListItem);

	NodeRef getEmployeeByAssignee(NodeRef assigneeListItem);

	int getAssigneesListItemDaysToComplete(final NodeRef assigneesItemRef);

	NodeRef getStaffFromAssigneesListItem(final NodeRef assigneesItemRef);

	/**
	 * расчитать сроки исполнения для участников опираясь на
	 * 1) сроки согласования в днях
	 * 2) тип списка исполнения: последовательный или параллельный
	 * этот метод предназначен для использования в маршрутах
	 * @param assigneesListRef нода списка исполнителей для которой рассчитываются
	 * @return общий срок исполнения
	 */
	Date calculateAssigneesDueDatesByCompletionDays(final NodeRef assigneesListRef);

	String getAssigneesListConcurrency(final NodeRef assigneesListRef);

	void setAssigneesListConcurrency(final NodeRef assigneesListRef, final String concurrency);

	void setAssigneesListDaysToComplete(final NodeRef assigneesListRef, final int daysToComplete);

	int getAssigneesListDaysToComplete(final NodeRef assigneesListRef);
}
