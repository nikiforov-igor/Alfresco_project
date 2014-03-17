package ru.it.lecm.workflow.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.FileNameValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.delegation.IDelegation;
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
	private WorkflowFoldersService workflowFoldersService;
	private CopyService copyService;
	private IDelegation delegationService;

	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
	}

	public void setwCalendarService(ICalendar wCalendarService) {
		this.wCalendarService = wCalendarService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setWorkflowFoldersService(WorkflowFoldersService workflowFoldersService) {
		this.workflowFoldersService = workflowFoldersService;
	}

	public void setCopyService(CopyService copyService) {
		this.copyService = copyService;
	}

	public void setDelegationService(IDelegation delegationService) {
		this.delegationService = delegationService;
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
		List<ChildAssociationRef> listItemsAssocs = nodeService.getChildAssocs(assigneesListNodeRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef listItemAssoc : listItemsAssocs) {
			result.add(listItemAssoc.getChildRef());
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
	public NodeRef getDefaultAssigneesList(String workflowType) {
		return getDefaultAssigneesList(getAssigneesListsFolder(), workflowType);
	}

	@Override
	public NodeRef getDefaultAssigneesList(final NodeRef parentRef, final String workflowType) {
		NodeRef result = null;
		List<NodeRef> assigneesLists = getAssingeesListsForCurrentEmployee(parentRef, workflowType);
		for (NodeRef assigneesList : assigneesLists) {
			if (isTempAssigneesList(assigneesList)) {
				result = assigneesList;
				break;
			}
		}

		if (result == null) {
			result = createAssigneesList(parentRef, workflowType, null);
		} else {
			if (!nodeService.hasAspect(result, LecmWorkflowModel.ASPECT_WORKFLOW_ROUTE)) {
				clearAssigneesList(result);
			}
		}

		return result;
	}

	@Override
	public List<NodeRef> getAssingeesListsForCurrentEmployee(String workflowType) {
		return getAssingeesListsForCurrentEmployee(getAssigneesListsFolder(), workflowType);
	}

	@Override
	public List<NodeRef> getAssingeesListsForCurrentEmployee(final NodeRef parentRef, final String workflowType) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		List<NodeRef> allEmployeeAssigneesList = getAllAssingeesListsForCurrentEmployee(parentRef);
		for (NodeRef assigneeListRef : allEmployeeAssigneesList) {
			if (workflowType.equalsIgnoreCase(getAssigneesListWorkflowType(assigneeListRef))) {
				result.add(assigneeListRef);
			}
		}

		return result;
	}

	private List<NodeRef> getAllAssingeesListsForCurrentEmployee(final NodeRef parentRef) {
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		List<NodeRef> allEmployeeAssigneesList = findNodesByAssociationRef(currentEmployee, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_OWNER, LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, ASSOCIATION_TYPE.SOURCE);
		List<NodeRef> result = new ArrayList<NodeRef>();
		for (NodeRef assigneeList : allEmployeeAssigneesList) {
			NodeRef primaryParentRef = nodeService.getPrimaryParent(assigneeList).getParentRef();
			if (parentRef.equals(primaryParentRef)) {
				result.add(assigneeList);
			}
		}
		return result;
	}

	@Override
	public String getAssigneesListWorkflowType(NodeRef assigneesListRef) {
		return (String) nodeService.getProperty(assigneesListRef, LecmWorkflowModel.PROP_WORKFLOW_TYPE);
	}

	@Override
	public boolean isTempAssigneesList(NodeRef assigneesListRef) {
		return nodeService.hasAspect(assigneesListRef, LecmWorkflowModel.ASPECT_TEMP);
	}

	@Override
	public void clearAssigneesList(NodeRef assigneesListNodeRef) {
		List<NodeRef> listItemsItems = getAssigneesListItems(assigneesListNodeRef);
		for (NodeRef listItemNodeRef : listItemsItems) {
			nodeService.addAspect(listItemNodeRef, ContentModel.ASPECT_TEMPORARY, null);
			nodeService.deleteNode(listItemNodeRef);
		}
	}

	@Override
	public AssigneesList getAssigneesListDetail(final NodeRef assigneesListRef) {
		AssigneesList assigneesList = new AssigneesList();
		assigneesList.setNodeRef(assigneesListRef);
		assigneesList.setListName(getNodeRefName(assigneesListRef));
		assigneesList.setConcurrency(getAssigneesListConcurrency(assigneesListRef));
		assigneesList.setDaysToComplete(getAssigneesListDaysToComplete(assigneesListRef));

		List<NodeRef> listItemsNodes = getAssigneesListItems(assigneesListRef);

		// бежим по элементам листа согласующих
		for (NodeRef listItem : listItemsNodes) {
			AssigneesListItem assigneesListItem = new AssigneesListItem();
			assigneesListItem.setNodeRef(listItem);
			assigneesListItem.setOrder(getAssigneesListItemOrder(listItem));
			assigneesListItem.setDueDate(getAssigneesListItemDueDate(listItem));
			assigneesListItem.setEmployeeRef(getEmployeeFromAssigneeListItem(listItem));
			assigneesListItem.setDaysToComplete(getAssigneesListItemDaysToComplete(listItem));
			assigneesListItem.setStaffRef(getStaffFromAssigneesListItem(listItem));//reserved for future

			assigneesList.getListItems().add(assigneesListItem);
		}
		//отсортировать их по order
		Collections.sort(assigneesList.getListItems(), new Comparator<AssigneesListItem>() {
			@Override
			public int compare(AssigneesListItem o1, AssigneesListItem o2) {
				return (o1.getOrder() < o2.getOrder()) ? -1 : ((o1.getOrder() == o2.getOrder()) ? 0 : 1);
			}
		});

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

		NodeRef assigeesListNodeRef = nodeService.getPrimaryParent(assigneeNodeRef).getParentRef();
		List<ChildAssociationRef> listItemsTargetAssocs = nodeService.getChildAssocs(assigeesListNodeRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL);

		if ("up".equalsIgnoreCase(direction)) {
			if (currentItemOrder == 1) {
				stopReordering = true;
			} else {
				newItemOrder = currentItemOrder - 1;
			}
		} else if ("down".equalsIgnoreCase(direction)) {
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
			for (ChildAssociationRef targetAssoc : listItemsTargetAssocs) {
				NodeRef item = targetAssoc.getChildRef();
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
		int period = (int) (diffDays / assigneesListItems.size());

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

	private NodeRef createEmptyAssigneesList(final NodeRef parentRef, final String name, final boolean isAnonymous) {
		Map<QName, Serializable> props = null;
		if (name != null) {
			props = new HashMap<QName, Serializable>();
			props.put(ContentModel.PROP_NAME, name);
		}
		return createAssigneesList(parentRef, "", props, isAnonymous);
	}

	private NodeRef createAssigneesList(final NodeRef parentRef, final String workflowType, final Map<QName, Serializable> properties) {
		return createAssigneesList(parentRef, workflowType, properties, false);
	}

	private NodeRef createAssigneesList(final NodeRef parentRef, final String workflowType, final Map<QName, Serializable> properties, final boolean isAnonymous) {
		Map<QName, Serializable> props = (properties == null) ? new HashMap<QName, Serializable>() : properties;

		props.put(LecmWorkflowModel.PROP_WORKFLOW_TYPE, workflowType);

		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
				props.containsKey(ContentModel.PROP_NAME) ? (String) props.get(ContentModel.PROP_NAME) : UUID.randomUUID().toString());

		NodeRef result = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName,
				LecmWorkflowModel.TYPE_WORKFLOW_ASSIGNEES_LIST, props).getChildRef();

		nodeService.addAspect(result, LecmWorkflowModel.ASPECT_TEMP, null);

		if (!isAnonymous) {
			NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
			nodeService.createAssociation(result, currentEmployee, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_OWNER);
		}

		return result;
	}

	@Override
	public void saveAssigneesList(NodeRef assigneesListRef, String assigneesListName) {
		String validAssigneesListName = FileNameValidator.getValidFileName(assigneesListName);
		NodeRef resultAssigneesList;
		if (nodeService.hasAspect(assigneesListRef, LecmWorkflowModel.ASPECT_TEMP)) {
			resultAssigneesList = assigneesListRef;
		} else {
			List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(assigneesListRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
			resultAssigneesList = copyAssigneesList(assigneesListRef, parentAssocs.get(0).getParentRef(), validAssigneesListName, false);
		}
		nodeService.removeAspect(resultAssigneesList, LecmWorkflowModel.ASPECT_TEMP);
		nodeService.setProperty(resultAssigneesList, ContentModel.PROP_NAME, validAssigneesListName);
	}

	@Override
	public String getNodeRefName(NodeRef nodeRef) {
		return (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
	}

	@Override
	public NodeRef getAssigneesListByItem(NodeRef assigneeListItem) {
		List<ChildAssociationRef> assigneesListAssocs = nodeService.getParentAssocs(assigneeListItem, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE, RegexQNamePattern.MATCH_ALL);
		NodeRef assigeesListNodeRef = assigneesListAssocs.get(0).getParentRef();
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
		NodeRef workingCopyAssigneesListNode = copyAssigneesList(assigneesListNode, workflowFoldersService.getAssigneesListWorkingCopyFolder(), true);
		String executionID = execution.getId();
		nodeService.setProperty(workingCopyAssigneesListNode, ContentModel.PROP_NAME, executionID);
		nodeService.removeAspect(workingCopyAssigneesListNode, LecmWorkflowModel.ASPECT_TEMP);

		return getAssigneesListItems(workingCopyAssigneesListNode);
	}

	private NodeRef copyAssigneesList(NodeRef source, NodeRef targetDir, boolean isAnonymous) {
		return copyAssigneesList(source, targetDir, null, isAnonymous);
	}

	private NodeRef copyAssigneesList(NodeRef source, NodeRef targetDir, String name, boolean isAnonymous) {
		//создаем новый список во временной папке
		NodeRef newAssigneesList = createEmptyAssigneesList(targetDir, name, isAnonymous);
		//копируем навешанные аспекты
		Set<QName> aspects = nodeService.getAspects(source);
		for (QName aspect : aspects) {
			if (!nodeService.hasAspect(newAssigneesList, aspect)) {
				nodeService.addAspect(newAssigneesList, aspect, null);
			}
		}
		//копируем тип бизнес-процесса
		String workflowType = (String) nodeService.getProperty(source, LecmWorkflowModel.PROP_WORKFLOW_TYPE);
		nodeService.setProperty(newAssigneesList, LecmWorkflowModel.PROP_WORKFLOW_TYPE, workflowType);
		//пробегаемся по детишкам, создаем элементы и копируем их
		List<NodeRef> assigneeRefs = getAssigneesListItems(source);
		for (NodeRef assigneeRef : assigneeRefs) {
			String assigneeName = (String) nodeService.getProperty(assigneeRef, ContentModel.PROP_NAME);
			QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, assigneeName);
			copyService.copy(assigneeRef, newAssigneesList, ContentModel.ASSOC_CONTAINS, assocQName);
		}

		return newAssigneesList;
	}

	@Override
	public String getAssigneesListItemUserName(NodeRef assigneeListItem) {
		return (String) nodeService.getProperty(assigneeListItem, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME);
	}

	@Override
	public NodeRef getEmployeeByAssignee(NodeRef assigneeListItem) {
		return findNodeByAssociationRef(assigneeListItem, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public List<NodeRef> actualizeAssigneesUsingDelegation(final List<NodeRef> assigneesList, final String workflowRole) {
		final List<NodeRef> actualizedAssigneesList = new ArrayList<NodeRef>();
		boolean delegateAll = workflowRole == null; //если роль не указана, то ориентируемся на делегирование всего
		Set<NodeRef> seenEmployees = new HashSet<NodeRef>();
		for (NodeRef assignee : assigneesList) {
			NodeRef seenEmployee;
			NodeRef employee = findNodeByAssociationRef(assignee, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			NodeRef delegationOpts = delegationService.getDelegationOpts(employee);
			boolean isDelegationActive = delegationService.isDelegationActive(delegationOpts);
			if (isDelegationActive) {
				NodeRef effectiveEmployee;
				if (delegateAll) {
					effectiveEmployee = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				} else {
					effectiveEmployee = delegationService.getEffectiveExecutor(employee, workflowRole);
					//если эффективного исполнителя не нашли по бизнес-ролям, то поискать его через параметры делегирования
					if (employee.equals(effectiveEmployee)) {
						effectiveEmployee = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
					}
				}
				if (effectiveEmployee != null) {
					// задач сотруднику не назначалось. работаем дальше
					nodeService.removeAssociation(assignee, employee, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE);
					nodeService.createAssociation(assignee, effectiveEmployee, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE);
					String userName = orgstructureService.getEmployeeLogin(effectiveEmployee);
					if (StringUtils.isNotEmpty(userName)) {
						nodeService.setProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME, userName);
					}
					// запомним, что уже видели его
					seenEmployee = effectiveEmployee;
				} else {
					// не отделегировалось. работаем, как будто ничего не произошло
					seenEmployee = employee;
				}
			} else {
				seenEmployee = employee;
			}

			// этому сотруднику уже назначалась задача?
			if (seenEmployees.contains(seenEmployee)) {
				// удалить участника совсем
				nodeService.addAspect(assignee, ContentModel.ASPECT_TEMPORARY, null);
				nodeService.deleteNode(assignee);
			} else {
			// задач сотруднику не назначалось. работаем дальше
				// добавим в новый список участников
				actualizedAssigneesList.add(assignee);
				// запомним, что уже видели его
				seenEmployees.add(seenEmployee);
			}
		}
		return actualizedAssigneesList;
	}

	@Override
	public int getAssigneesListItemDaysToComplete(final NodeRef assigneesItemRef) {
		Integer daysToComplete = (Integer) nodeService.getProperty(assigneesItemRef, LecmWorkflowModel.PROP_ASSIGNEE_DAYS_TO_COMPLETE);
		return (daysToComplete != null) ? daysToComplete : 0;
	}

	@Override
	public NodeRef getStaffFromAssigneesListItem(final NodeRef assigneesItemRef) {
		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(assigneesItemRef, LecmWorkflowModel.ASSOC_ASSIGNEE_ORG_ELEMENT_MEMBER);
		return targetAssocs.isEmpty() ? null : targetAssocs.get(0).getTargetRef();
	}

	@Override
	public Date calculateAssigneesDueDatesByCompletionDays(final NodeRef assigneesListRef) {
		AssigneesList assigneesList = getAssigneesListDetail(assigneesListRef);
		String concurrency = assigneesList.getConcurrency();
		List<AssigneesListItem> items = assigneesList.getListItems();
		Date workflowDueDate = new Date();
		if (LecmWorkflowModel.CONCURRENCY_PAR.equals(concurrency)) { //параллельный процесс
			workflowDueDate = DateUtils.addDays(workflowDueDate, assigneesList.getDaysToComplete());
			for (AssigneesListItem item : items) {
				nodeService.setProperty(item.getNodeRef(), LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE, workflowDueDate);
			}
		} else if (LecmWorkflowModel.CONCURRENCY_SEQ.equals(concurrency)) { //последовательный процесс
			Date currentDate = new Date();
			for (AssigneesListItem item : items) {
				currentDate = workCalendarService.getEmployeeNextWorkingDay(item.getEmployeeRef(), currentDate, item.getDaysToComplete());
				nodeService.setProperty(item.getNodeRef(), LecmWorkflowModel.PROP_ASSIGNEE_DUE_DATE, currentDate);
			}
			workflowDueDate = currentDate;
		} else {
			logger.error("{} is unknown concurrency for assigneesList {}. Due dates can't be calculated!", concurrency, assigneesList);
		}
		return workflowDueDate;
	}

	@Override
	public String getAssigneesListConcurrency(NodeRef assigneesListRef) {
		return (String)nodeService.getProperty(assigneesListRef, LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY);
	}

	@Override
	public void setAssigneesListConcurrency(NodeRef assigneesListRef, String concurrency) {
		nodeService.setProperty(assigneesListRef, LecmWorkflowModel.PROP_WORKFLOW_CONCURRENCY, concurrency);
	}

	@Override
	public void setAssigneesListDaysToComplete(final NodeRef assigneesListRef, int daysToComplete) {
		nodeService.setProperty(assigneesListRef, LecmWorkflowModel.PROP_ASSIGNEE_DAYS_TO_COMPLETE, daysToComplete);
	}

	@Override
	public int getAssigneesListDaysToComplete(final NodeRef assigneesListRef) {
		Integer daysToComplete = (Integer) nodeService.getProperty(assigneesListRef, LecmWorkflowModel.PROP_ASSIGNEE_DAYS_TO_COMPLETE);
		return (daysToComplete != null) ? daysToComplete : 0;
	}
}
