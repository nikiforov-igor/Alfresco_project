package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.wcalendar.calendar.ICalendar;
import ru.it.lecm.workflow.AssigneesList;
import ru.it.lecm.workflow.AssigneesListItem;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.api.WorkflowFoldersService;
import ru.it.lecm.workflow.api.LecmWorkflowModel;

/**
 *
 * @author vlevin
 */
public class WorkflowAssigneesListServiceImpl extends BaseBean implements WorkflowAssigneesListService {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowAssigneesListServiceImpl.class);
	private IWorkCalendar workCalendarService;
	private ICalendar wCalendarService;
	private OrgstructureBean orgstructureService;
	private BehaviourFilter behaviourFilter;
	private WorkflowFoldersService workflowFoldersService;
	private CopyService copyService;

	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
	}

	public void setwCalendarService(ICalendar wCalendarService) {
		this.wCalendarService = wCalendarService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	public void setWorkflowFoldersService(WorkflowFoldersService workflowFoldersService) {
		this.workflowFoldersService = workflowFoldersService;
	}

	public void setCopyService(CopyService copyService) {
		this.copyService = copyService;
	}

	private NodeRef getEmployeeFromAssigneeListItem(NodeRef assigneeListItem) {
		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(assigneeListItem, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE);
		if (targetAssocs.isEmpty()) {
			return null;
		} else {
			return targetAssocs.get(0).getTargetRef();
		}
	}

	@Override
	public void setDueDates(NodeRef assigneeListNodeRef, Date workflowDueDate) {
		List<NodeRef> assigneeListItems = sortAssigneesListItems(getAssigneesListItems(assigneeListNodeRef));
		Date workflowDueDateTruncated = DateUtils.truncate(workflowDueDate, Calendar.DATE);
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);

		final int daysCount = Days.daysBetween(new DateTime(today), new DateTime(workflowDueDateTruncated)).getDays() + 1;
		final int peopleCount = assigneeListItems.size();

		double day = 0, daysPerPeople, peoplePerDay, remainingDays = daysCount, remainingPeople = peopleCount,
				daysModulo, peopleModulo, daysBuffer = 0, peopleBuffer = 0;

		if (daysCount >= peopleCount) {
			daysPerPeople = remainingDays / remainingPeople;
			daysModulo = daysPerPeople % 1;

			for (int i = 0; i < peopleCount; ++i) {
				NodeRef assigneeListItem = assigneeListItems.get(i);
				daysBuffer += daysModulo;
				if (daysPerPeople < remainingDays) {
					day += daysPerPeople;
					if (daysBuffer >= 1) {
						int curDay = (int) Math.floor(day + 0.5) - 1;
						Date dueDate = DateUtils.addDays(today, curDay);
						setEffectiveDueDate(assigneeListItem, dueDate);
						daysBuffer = Math.abs(daysBuffer - 1);
					} else {
						int curDay = (int) Math.floor(day) - 1;
						Date dueDate = DateUtils.addDays(today, curDay);
						setEffectiveDueDate(assigneeListItem, dueDate);
					}

					remainingDays -= daysPerPeople;
				} else {
					int curDay = (int) Math.round(day + remainingDays) - 1;
					Date dueDate = DateUtils.addDays(today, curDay);
					setEffectiveDueDate(assigneeListItem, dueDate);
				}
			}
		} else {
			int curPerson = 0;

			peoplePerDay = remainingPeople / remainingDays;
			int peoplePerDayFloored = (int) Math.floor(peoplePerDay);
			peopleModulo = peoplePerDay % 1;

			for (int i = 0; i < daysCount; ++i) {
				Date dueDate = DateUtils.addDays(today, i);
				peopleBuffer += peopleModulo;

				if (peoplePerDay < remainingPeople) {
					if (peopleBuffer >= 1) {
						for (int j = 0; j <= peoplePerDayFloored; j++) {
							NodeRef assigneeListItem = assigneeListItems.get(curPerson);
							setEffectiveDueDate(assigneeListItem, dueDate);
							curPerson++;
						}
						peopleBuffer = Math.abs(peopleBuffer - 1);
					} else {
						for (int j = 0; j < peoplePerDayFloored; j++) {
							NodeRef assigneeListItem = assigneeListItems.get(curPerson);
							setEffectiveDueDate(assigneeListItem, dueDate);
							curPerson++;
						}
					}

					remainingPeople -= peoplePerDay;
				} else {
					if (peopleBuffer >= 1) {
						for (int j = 0; j <= remainingPeople; j++) {
							NodeRef assigneeListItem = assigneeListItems.get(curPerson);
							setEffectiveDueDate(assigneeListItem, dueDate);
							curPerson++;
						}
						peopleBuffer = Math.abs(peopleBuffer - 1);
					} else {
						for (int j = 0; j < remainingPeople; j++) {
							NodeRef assigneeListItem = assigneeListItems.get(curPerson);
							setEffectiveDueDate(assigneeListItem, dueDate);
							curPerson++;
						}
					}
					remainingPeople -= remainingPeople;
				}
			}
		}
	}

	private List<NodeRef> getAssigneesListItems(NodeRef assigneesListNodeRef) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		List<AssociationRef> listItemsAssocs = nodeService.getTargetAssocs(assigneesListNodeRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE);
		for (AssociationRef listItemAssoc : listItemsAssocs) {
			result.add(listItemAssoc.getTargetRef());
		}
		return result;
	}

	private void setEffectiveDueDate(NodeRef assigneeListItem, Date priorDueDate) {
		Date effectiveDueDate = new Date(priorDueDate.getTime());
		NodeRef employeeNode = getEmployeeFromAssigneeListItem(assigneeListItem);
		boolean employeePresent = false;
		Calendar calendar = Calendar.getInstance();
		while (!employeePresent) {
			try {
				employeePresent = workCalendarService.getEmployeeAvailability(employeeNode, effectiveDueDate);
			} catch (IllegalArgumentException ex) {
				logger.warn(ex.getMessage());
				break;
			}
			if (!employeePresent) {
				effectiveDueDate = DateUtils.addDays(effectiveDueDate, 1);
				calendar.setTime(effectiveDueDate);
				int year = calendar.get(Calendar.YEAR);
				if (!wCalendarService.isCalendarExists(year)) {
					break;
				}
			}
		}
		setAssigneesListItemDueDate(assigneeListItem, effectiveDueDate);

	}

	private List<NodeRef> sortAssigneesListItems(List<NodeRef> assigneesListItems) {
		List<NodeRef> sortedAssigneesListItems = new ArrayList<NodeRef>(assigneesListItems);

		Comparator<NodeRef> comparator = new Comparator<NodeRef>() {
			@Override
			public int compare(NodeRef o1, NodeRef o2) {
				int order1 = getAssigneesListItemOrder(o1);
				int order2 = getAssigneesListItemOrder(o2);
				return (order1 < order2) ? -1 : ((order1 == order2) ? 0 : 1);
			}
		};
		Collections.sort(sortedAssigneesListItems, comparator);

		return sortedAssigneesListItems;
	}

	@Override
	public int getAssigneesListItemOrder(NodeRef listItemNodeRef) {
		return (Integer) nodeService.getProperty(listItemNodeRef, LecmWorkflowModel.PROP_ASSIGNEE_ORDER);
	}

	@Override
	public void setAssigneesListItemOrder(NodeRef listItemNodeRef, int order) {
		nodeService.setProperty(listItemNodeRef, LecmWorkflowModel.PROP_ASSIGNEE_ORDER, order);
	}

	@Override
	public NodeRef getAssigneesListsFolder() {
		return workflowFoldersService.getWorkflowFolder();
	}

	@Override
	public NodeRef getDefaultAssigneesList(String workflowType, String concurrency) {
		NodeRef result = null;
		List<NodeRef> assigneesLists = getAssingeesListsForCurrentEmployee(workflowType, concurrency);
		for (NodeRef assigneesList : assigneesLists) {
			if (isTempAssigneesList(assigneesList)) {
				result = assigneesList;
				break;
			}
		}

		if (result == null) {
			result = createAssigneesList(getAssigneesListsFolder(), workflowType, concurrency, null);
		} else {
			clearAssigneesList(result);
		}

		return result;
	}

	@Override
	public List<NodeRef> getAssingeesListsForCurrentEmployee(String workflowType, String concurrency) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		List<NodeRef> allEmployeeAssigneesList = getAllAssingeesListsForCurrentEmployee();
		for (NodeRef assigneeListRef : allEmployeeAssigneesList) {
			if (workflowType.equalsIgnoreCase(getAssigneesListWorkflowType(assigneeListRef))
					&& (concurrency == null || concurrency.equalsIgnoreCase(getAssigneesListWorkflowConcurrency(assigneeListRef)))) {
				result.add(assigneeListRef);
			}
		}

		return result;
	}

	private List<NodeRef> getAllAssingeesListsForCurrentEmployee() {
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		List<NodeRef> allEmployeeAssigneesList = findNodesByAssociationRef(currentEmployee, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_OWNER, LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, ASSOCIATION_TYPE.SOURCE);
		return allEmployeeAssigneesList;
	}

	@Override
	public String getAssigneesListWorkflowType(NodeRef assigneesListRef) {
		return (String) nodeService.getProperty(assigneesListRef, LecmWorkflowModel.PROP_WORKFLOW_TYPE);
	}

	@Override
	public String getAssigneesListWorkflowConcurrency(NodeRef assigneesListRef) {
		return (String) nodeService.getProperty(assigneesListRef, LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY);
	}

	@Override
	public boolean isTempAssigneesList(NodeRef assigneesListRef) {
		return nodeService.hasAspect(assigneesListRef, LecmWorkflowModel.ASPECT_TEMP);
	}

	@Override
	public void setAssigneesListTemp(NodeRef assigneesListRef) {
		nodeService.addAspect(assigneesListRef, LecmWorkflowModel.ASPECT_TEMP, null);
	}

	@Override
	public void clearAssigneesList(NodeRef assigneesListNodeRef) {
		List<NodeRef> listItemsItems = getAssigneesListItems(assigneesListNodeRef);
		for (NodeRef listItemNodeRef : listItemsItems) {
			nodeService.removeAssociation(assigneesListNodeRef, listItemNodeRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE);
			nodeService.addAspect(listItemNodeRef, ContentModel.ASPECT_TEMPORARY, null);
			nodeService.deleteNode(listItemNodeRef);
		}
	}

	@Override
	public AssigneesList getAssigneesListDetail(NodeRef assingeesListRef) {
		AssigneesList assigneesList = new AssigneesList();
		assigneesList.setListName(getNodeRefName(assingeesListRef));

		List<NodeRef> listItemsNodes = getAssigneesListItems(assingeesListRef);

		// бежим по элементам листа согласующих
		for (NodeRef listItem : listItemsNodes) {
			AssigneesListItem assigneesListItem = new AssigneesListItem();
			assigneesListItem.setOrder(getAssigneesListItemOrder(listItem));
			assigneesListItem.setDueDate(getAssigneesListItemDueDate(listItem));
			assigneesListItem.setEmployeeRef(getEmployeeFromAssigneeListItem(listItem));

			assigneesList.getListItems().add(assigneesListItem);
		}
		return assigneesList;
	}

	@Override
	public Date getAssigneesListItemDueDate(NodeRef assigneeListItem) {
		return (Date) nodeService.getProperty(assigneeListItem, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE);
	}

	@Override
	public void setAssigneesListItemDueDate(NodeRef assigneeListItem, Date dueDate) {
		nodeService.setProperty(assigneeListItem, LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE, dueDate);
	}

	@Override
	public void clearDueDatesInAssigneesList(NodeRef assigneeListRef) {
		List<NodeRef> assigneesListItems = getAssigneesListItems(assigneeListRef);

		for (NodeRef assigneesListItemRef : assigneesListItems) {
			setAssigneesListItemDueDate(assigneesListItemRef, null);
		}
	}

	@Override
	public void deleteAssigneesList(NodeRef assigneeListRef) {
		nodeService.addAspect(assigneeListRef, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(assigneeListRef);
	}

	@Override
	public boolean changeAssigneeOrder(NodeRef assigneeNodeRef, String direction) {
		int currentItemOrder, newItemOrder = 0;
		boolean stopReordering = false, success;

		currentItemOrder = getAssigneesListItemOrder(assigneeNodeRef);
		List<AssociationRef> assigneesListAssocs = nodeService.getSourceAssocs(assigneeNodeRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE);
		NodeRef assigeesListNodeRef = assigneesListAssocs.get(0).getSourceRef();
		List<AssociationRef> listItemsTargetAssocs = nodeService.getTargetAssocs(assigeesListNodeRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE);

		if ("up".equals(direction)) {
			if (currentItemOrder == 1) {
				stopReordering = true;
			} else {
				newItemOrder = currentItemOrder - 1;
			}
		} else if ("down".equals(direction)) {
			if (currentItemOrder == listItemsTargetAssocs.size()) {
				stopReordering = true;
			} else {
				newItemOrder = currentItemOrder + 1;
			}
		} else {
			stopReordering = true;
		}

		if (stopReordering) {
			success = false;
		} else {
			for (AssociationRef targetAssoc : listItemsTargetAssocs) {
				NodeRef item = targetAssoc.getTargetRef();
				if (item.equals(assigneeNodeRef)) {
					continue;
				}
				int itemOrder = getAssigneesListItemOrder(item);
				if (itemOrder == newItemOrder) {

					setAssigneesListItemOrder(item, currentItemOrder);
					break;
				}
			}
			setAssigneesListItemOrder(assigneeNodeRef, newItemOrder);

			success = true;
		}

		return success;
	}

	@Override
	public void calculateAssigneesListDueDates(NodeRef assigneesList, Date dueDate) {
		List<NodeRef> assigneesListItems = getAssigneesListItems(assigneesList);

		if (assigneesListItems.isEmpty()) {
			return;
		}

		dueDate = DateUtils.truncate(dueDate, Calendar.DATE);
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);

		long diffDays = (dueDate.getTime() - today.getTime()) / 86400000;
		int period = (int)(diffDays / assigneesListItems.size());

		Date previousDate;
		Date currentDate;

		setAssigneesListItemDueDate(assigneesListItems.get(0), DateUtils.addDays(today, period));
		for (int i = 1; i < assigneesListItems.size(); i++) {
			previousDate = getAssigneesListItemDueDate(assigneesListItems.get(i - 1));
			currentDate = DateUtils.addDays(previousDate, period);
			setAssigneesListItemDueDate(assigneesListItems.get(i), currentDate);
		}
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getAssigneesListsFolder();
	}

	private NodeRef createAssigneesList(final NodeRef parentRef, final String workflowType, final String concurrency, final Map<QName, Serializable> properties) {
		Map<QName, Serializable> props;
		NodeRef result;
		if (properties == null) {
			props = new HashMap<QName, Serializable>();
		} else {
			props = properties;
		}

		props.put(LecmWorkflowModel.PROP_WORKFLOW_TYPE, workflowType);

		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
				props.containsKey(ContentModel.PROP_NAME) ? (String) props.get(ContentModel.PROP_NAME) : UUID.randomUUID().toString());

		result = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName,
				LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, properties).getChildRef();

		if (concurrency != null) {
			nodeService.addAspect(result, LecmWorkflowModel.ASPECT_WORKFLOW_CONCURRENCY, new HashMap<QName, Serializable>(){{
				put(LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY, concurrency);
			}});
		}

		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		nodeService.createAssociation(result, currentEmployee, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_OWNER);

		return result;
	}

	@Override
	public void saveAssigneesList(NodeRef assigneesListRef, String assigneesListName) {
		nodeService.setProperty(assigneesListRef, ContentModel.PROP_NAME, assigneesListName);
		nodeService.removeAspect(assigneesListRef, LecmWorkflowModel.ASPECT_TEMP);
	}

	@Override
	public String getNodeRefName(NodeRef nodeRef) {
		return (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
	}

	@Override
	public NodeRef getAssigneesListByItem(NodeRef assigneeListItem) {
		List<AssociationRef> assigneesListAssocs = nodeService.getSourceAssocs(assigneeListItem, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE);
		NodeRef assigeesListNodeRef = assigneesListAssocs.get(0).getSourceRef();
		return assigeesListNodeRef;
	}

	@Override
	public void deleteAssigneesListWorkingCopy(DelegateExecution execution) {
		String executionID = execution.getId();
		NodeRef workingCopyFolderRef = workflowFoldersService.getAssigneesListWorkingCopyFolder();
		NodeRef workingCopyAssigneesList = nodeService.getChildByName(workingCopyFolderRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, executionID);
		if (workingCopyAssigneesList != null) {
			deleteAssigneesList(workingCopyAssigneesList);
		}
	}

	@Override
	public List<NodeRef> createAssigneesListWorkingCopy(NodeRef assigneesListNode, DelegateExecution execution) {
		NodeRef workingCopyAssigneesList;
		NodeRef workingCopyFolderRef = workflowFoldersService.getAssigneesListWorkingCopyFolder();
		String executionID = execution.getId();
		QName assocQName = QName.createQName(LecmWorkflowModel.WORKFLOW_NAMESPACE, executionID);
		behaviourFilter.disableBehaviour(LecmWorkflowModel.TYPE_ASSIGNEE);
		try {
			workingCopyAssigneesList = copyService.copyAndRename(assigneesListNode, workingCopyFolderRef, ContentModel.ASSOC_CONTAINS, assocQName, true);
		} finally {
			behaviourFilter.enableBehaviour(LecmWorkflowModel.TYPE_ASSIGNEE);
		}
		nodeService.setProperty(workingCopyAssigneesList, ContentModel.PROP_NAME, executionID);
		return findNodesByAssociationRef(workingCopyAssigneesList, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, LecmWorkflowModel.TYPE_ASSIGNEE, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public String getAssigneesListItemUserName(NodeRef assigneeListItem) {
		return (String) nodeService.getProperty(assigneeListItem, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME);
	}

	@Override
	public NodeRef getEmployeeByAssignee(NodeRef assigneeListItem) {
		return findNodeByAssociationRef(assigneeListItem, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
	}


}
