package ru.it.lecm.wcalendar.schedule.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.ICommonWCalendar;
import ru.it.lecm.wcalendar.beans.AbstractCommonWCalendarBean;
import ru.it.lecm.wcalendar.schedule.ISchedule;
import ru.it.lecm.wcalendar.schedule.ISpecialScheduleRaw;

/**
 *
 * @author vlevin
 */
public class ScheduleBean extends AbstractCommonWCalendarBean implements ISchedule {
	// Получить логгер, чтобы писать, что с нами происходит.

	private final static Logger logger = LoggerFactory.getLogger(ScheduleBean.class);

	@Override
	public ICommonWCalendar getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_SCHEDULE;
	}

	/**
	 * Метод, который запускает Spring при старте Tomcat-а. Создает корневой
	 * объект для графиков работы.
	 */
	public final void init() {
		PropertyCheck.mandatory(this, "repository", repository);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

		// Создание контейнера (если не существует).
		AuthenticationUtil.runAsSystem(this);
	}

	@Override
	protected Map<String, Object> containerParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CONTAINER_NAME", CONTAINER_NAME);
		params.put("CONTAINER_TYPE", TYPE_SCHEDULE_CONTAINER);

		return params;
	}

	private NodeRef recursiveScheduleSearch(NodeRef node) {
		if (!isScheduleAssociated(node)) {
			List<ChildAssociationRef> parentAssocList = nodeService.getParentAssocs(node);
			if (parentAssocList == null || parentAssocList.isEmpty()) {
				return null;
			}
			ChildAssociationRef parentAssoc = parentAssocList.get(0);
			NodeRef parentNode = parentAssoc.getParentRef();
			return recursiveScheduleSearch(parentNode);
		} else {
			return getScheduleByOrgSubject(node);
		}
	}

	@Override
	public NodeRef getParentSchedule(NodeRef node) {
		NodeRef primaryOU = null;
		boolean searchFromCurrent = true;
		if (orgstructureService.isEmployee(node)) {
			primaryOU = orgstructureService.getEmployeePrimaryStaff(node);
			searchFromCurrent = true;
		} else if (orgstructureService.isUnit(node)) {
			primaryOU = node;
			searchFromCurrent = false;
		}

		if (primaryOU == null) {
			return null;
		}

		NodeRef result;
		if (searchFromCurrent) {
			result = recursiveScheduleSearch(primaryOU);
		} else {
			List<ChildAssociationRef> parentAssocList = nodeService.getParentAssocs(primaryOU);
			if (parentAssocList == null || parentAssocList.isEmpty()) {
				return null;
			}
			ChildAssociationRef parentAssoc = parentAssocList.get(0);
			result = recursiveScheduleSearch(parentAssoc.getParentRef());
		}
		return result;
	}

	@Override
	public boolean isScheduleAssociated(NodeRef node) {
		NodeRef schedule = findNodeByAssociationRef(node, ASSOC_SCHEDULE_EMPLOYEE_LINK, TYPE_SCHEDULE, ASSOCIATION_TYPE.SOURCE);
		boolean result = schedule != null;
		return result;
	}

	@Override
	public NodeRef createNewSpecialSchedule(final ISpecialScheduleRaw scheduleRawData, final NodeRef scheduleEmployeeAssoc, final NodeRef scheduleContainer) {
		NodeRef createdScheduleNode;
		// Транзакция. Все хорошие мальчики ковыряются в хранилище только в транзакции.
		createdScheduleNode = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
			@Override
			public NodeRef execute() throws Throwable {
				// Создание нового расписания
				ChildAssociationRef createdScheduleChildRef;
				String scheduleEmployeeAssocCMNameStr = nodeService.getProperty(scheduleEmployeeAssoc, ContentModel.PROP_NAME).toString();
				// имя для ноды вида: "commonNameСодрудникаИлиОрганизации_schedule"
				QName scheduleEmployeeAssocCMNameQName = QName.createQName(SCHEDULE_NAMESPACE, scheduleEmployeeAssocCMNameStr + "_schedule");
				DateFormat timeFormat = new SimpleDateFormat("HH:mm");

				// нам не нужно дожидаться страшной ошибки при попытке создания уже существующего расписания
				// срыгнем эксепш пораньше
				if (isScheduleAssociated(scheduleEmployeeAssoc)) {
					throw new WebScriptException(scheduleEmployeeAssoc.toString() + " already has schedule!");
				}
				// UUID.randomUUID().toString()
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
				// пропертя для ноды, которую собираемся создать
				properties.put(PROP_SCHEDULE_TYPE, "SPECIAL");
				properties.put(PROP_SCHEDULE_STD_BEGIN, timeFormat.format(scheduleRawData.getTimeWorkBegins()));
				properties.put(PROP_SCHEDULE_STD_END, timeFormat.format(scheduleRawData.getTimeWorkEnds()));
				properties.put(PROP_SCHEDULE_TIME_LIMIT_START, scheduleRawData.getTimeLimitStart());
				properties.put(PROP_SCHEDULE_TIME_LIMIT_END, scheduleRawData.getTimeLimitEnd());

				try {
					// создаем новую ноду в scheduleContainer
					createdScheduleChildRef = nodeService.createNode(scheduleContainer, ContentModel.ASSOC_CONTAINS,
							scheduleEmployeeAssocCMNameQName, TYPE_SCHEDULE, properties);
				} catch (Exception ex) {
					throw new WebScriptException("Unable to create node", ex);
				}
				// нод-рефа на свежесозданную ноду
				NodeRef createdScheduleNode = createdScheduleChildRef.getChildRef();
				try {
					// привязываем ноду расписания к сотруднику или подразделеню
					nodeService.createAssociation(createdScheduleNode, scheduleEmployeeAssoc, ASSOC_SCHEDULE_EMPLOYEE_LINK);
				} catch (Exception ex) {
					throw new WebScriptException("Unable to link newly created " + createdScheduleNode.toString() + " with " + scheduleEmployeeAssoc, ex);
				}

				// здесь генерируем список элементов графика...
				List<ScheduleElemetObject> generatedScheduleElements = generateScheduleElements(scheduleRawData);
				// ...и скармливаем его функции, которая их создаст
				createScheduleElement(createdScheduleNode, generatedScheduleElements);

				return createdScheduleNode;
			}
		});
		return createdScheduleNode;
	}

	// жуткий трэш, который генерирует интервалы рабочих дней в зависимости от настроек расписания
	private List<ScheduleElemetObject> generateScheduleElements(ISpecialScheduleRaw scheduleRawData) {
		List<ScheduleElemetObject> scheduleElements = new ArrayList<ScheduleElemetObject>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(scheduleRawData.getTimeLimitStart());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);

		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(scheduleRawData.getTimeLimitEnd());
		calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
		calendarEnd.set(Calendar.MINUTE, 0);

		if (scheduleRawData.getReiterationType() == ISpecialScheduleRaw.ReiterationType.MONTH_DAYS
				|| scheduleRawData.getReiterationType() == ISpecialScheduleRaw.ReiterationType.WEEK_DAYS) {

			while (!calendar.after(calendarEnd)) {
				if (ifDayToBeAdded(scheduleRawData, calendar)) {
					if (!scheduleElements.isEmpty()) {
						ScheduleElemetObject lastScheduleElement = scheduleElements.get(scheduleElements.size() - 1);
						Date lastScheduleElementEnd = lastScheduleElement.getEnd();
						if (lastScheduleElementEnd != null) {
							Calendar lastScheduleElementEndCal = Calendar.getInstance();
							lastScheduleElementEndCal.setTime(lastScheduleElementEnd);
							lastScheduleElementEndCal.add(Calendar.DAY_OF_YEAR, 1);
							ScheduleElemetObject newLastScheduleElement = new ScheduleElemetObject();
							if (lastScheduleElementEndCal.equals(calendar)) {
								newLastScheduleElement.setBegin(lastScheduleElement.getBegin());
								newLastScheduleElement.setEnd(calendar.getTime());
								scheduleElements.set(scheduleElements.size() - 1, newLastScheduleElement);
							} else {
								newLastScheduleElement.setBegin(calendar.getTime());
								scheduleElements.add(newLastScheduleElement);
							}
						} else {
							Date lastScheduleElementBegin = lastScheduleElement.getBegin();
							Calendar lastScheduleElementBeginCal = Calendar.getInstance();
							lastScheduleElementBeginCal.setTime(lastScheduleElementBegin);
							lastScheduleElementBeginCal.add(Calendar.DAY_OF_YEAR, 1);
							ScheduleElemetObject newLastScheduleElement = new ScheduleElemetObject();
							if (lastScheduleElementBeginCal.equals(calendar)) {
								newLastScheduleElement.setBegin(lastScheduleElement.getBegin());
								newLastScheduleElement.setEnd(calendar.getTime());
								scheduleElements.set(scheduleElements.size() - 1, newLastScheduleElement);
							} else {
								newLastScheduleElement.setBegin(lastScheduleElement.getBegin());
								newLastScheduleElement.setEnd(lastScheduleElement.getBegin());
								scheduleElements.set(scheduleElements.size() - 1, newLastScheduleElement);
								newLastScheduleElement = new ScheduleElemetObject();
								newLastScheduleElement.setBegin(calendar.getTime());
								newLastScheduleElement.setEnd(null);
								scheduleElements.add(newLastScheduleElement);
							}
						}
					} else {
						ScheduleElemetObject newLastScheduleElement = new ScheduleElemetObject();
						newLastScheduleElement.setBegin(calendar.getTime());
						newLastScheduleElement.setEnd(null);
						scheduleElements.add(newLastScheduleElement);
					}
				}
				if (calendar.equals(calendarEnd)) {
					ScheduleElemetObject lastScheduleElement = scheduleElements.get(scheduleElements.size() - 1);
					if (lastScheduleElement.getEnd() == null) {
						lastScheduleElement.setEnd(lastScheduleElement.getBegin());
						scheduleElements.set(scheduleElements.size() - 1, lastScheduleElement);
					}
				}

				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}
		} else if (scheduleRawData.getReiterationType() == ISpecialScheduleRaw.ReiterationType.SHIFT) {
			while (calendar.before(calendarEnd)) {
				ScheduleElemetObject scheduleElement = new ScheduleElemetObject();
				scheduleElement.setBegin(calendar.getTime());
				calendar.add(Calendar.DAY_OF_YEAR, scheduleRawData.getWorkingDaysAmount() - 1);
				scheduleElement.setEnd(calendar.getTime());
				calendar.add(Calendar.DAY_OF_YEAR, scheduleRawData.getWorkingDaysInterval() + 1);
				scheduleElements.add(scheduleElement);
			}
		}
		return scheduleElements;
	}

	// проверяет, подходит ли день под правила повторяемости для расписаний по дням месяца и недели
	// используется только в generateScheduleElements
	private boolean ifDayToBeAdded(ISpecialScheduleRaw scheduleRawData, Calendar calendar) {
		boolean result;
		if (scheduleRawData.getReiterationType() == ISpecialScheduleRaw.ReiterationType.MONTH_DAYS) {
			List<Integer> monthDays = scheduleRawData.getMonthDays();
			result = monthDays.contains(calendar.get(Calendar.DAY_OF_MONTH));
		} else if (scheduleRawData.getReiterationType() == ISpecialScheduleRaw.ReiterationType.WEEK_DAYS) {
			Map<Integer, Boolean> weekDays = scheduleRawData.getWeekDays();
			result = weekDays.get(calendar.get(Calendar.DAY_OF_WEEK));
		} else {
			throw new WebScriptException("Reiteration type can not be " + scheduleRawData.getReiterationType());
		}
		return result;
	}

	// непосредственное создание элементов графика в хранилище
	private void createScheduleElement(NodeRef createdScheduleNode, List<ScheduleElemetObject> generatedScheduleElements) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for (ScheduleElemetObject scheduleElement : generatedScheduleElements) {
			Date begin = scheduleElement.getBegin();
			Date end = scheduleElement.getEnd();
			// имя для элемента расписания вида "датаНачала_датаОкончания"
			QName scheduleElementQName = QName.createQName(SCHEDULE_NAMESPACE, dateFormat.format(begin) + "_" + dateFormat.format(end));
			// пропертя для элемента расписания
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(PROP_SCHEDULE_ELEMENT_BEGIN, begin);
			properties.put(PROP_SCHEDULE_ELEMENT_END, end);
			try {
				// создаем новую ноду в createdScheduleNode
				nodeService.createNode(createdScheduleNode, ContentModel.ASSOC_CONTAINS, scheduleElementQName, TYPE_SCHEDULE_ELEMENT, properties);
			} catch (Exception ex) {
				// не получилось
				throw new WebScriptException("Unable to create node", ex);
			}
		}
	}

	@Override
	public NodeRef getScheduleByOrgSubject(NodeRef node) {
		return findNodeByAssociationRef(node, ASSOC_SCHEDULE_EMPLOYEE_LINK, TYPE_SCHEDULE, ASSOCIATION_TYPE.SOURCE);
	}

	@Override
	public void unlinkSchedule(NodeRef node) {
		NodeRef orgSubj = getOrgSubjectBySchedule(node);

		if (orgSubj != null) {
			nodeService.removeAssociation(node, orgSubj, ASSOC_SCHEDULE_EMPLOYEE_LINK);
		}
	}

	@Override
	public NodeRef getOrgSubjectBySchedule(NodeRef node) {
		NodeRef result = null;
		NodeRef employee = findNodeByAssociationRef(node, ASSOC_SCHEDULE_EMPLOYEE_LINK, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		NodeRef orgUnit = findNodeByAssociationRef(node, ASSOC_SCHEDULE_EMPLOYEE_LINK, OrgstructureBean.TYPE_ORGANIZATION_UNIT, ASSOCIATION_TYPE.TARGET);
		if (employee != null) {
			result = employee;
		} else if (orgUnit != null) {
			result = orgUnit;
		}
		return result;
	}

	@Override
	public void addBusinessJournalRecord(NodeRef node, String category) {
		NodeRef orgSubj = getOrgSubjectBySchedule(node);
		String messageTemplate = null;
		String scheduleType = getScheduleType(node);
		if (orgSubj == null) {
			return;
		}

		List<String> objects = new ArrayList<String>();
		objects.add(orgSubj.toString());

		if (EventCategory.ADD.equals(category)) {
			if (orgstructureService.isEmployee(orgSubj)) {
				if (SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_COMMON_SCHEDULE_EMPLOYEE_CREATE;
				} else if (SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_SPECIAL_SCHEDULE_EMPLOYEE_CREATE;
				}
			} else if (orgstructureService.isUnit(orgSubj)) {
				if (SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_COMMON_SCHEDULE_OU_CREATE;
				} else if (SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_SPECIAL_SCHEDULE_OU_CREATE;
				}
			}
		} else if (EventCategory.DELETE.equals(category)) {
			if (orgstructureService.isEmployee(orgSubj)) {
				if (SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_COMMON_SCHEDULE_EMPLOYEE_DELETE;
				} else if (SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_SPECIAL_SCHEDULE_EMPLOYEE_DELETE;
				}
			} else if (orgstructureService.isUnit(orgSubj)) {
				if (SCHEDULE_TYPE_COMMON.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_COMMON_SCHEDULE_OU_DELETE;
				} else if (SCHEDULE_TYPE_SPECIAL.equals(scheduleType)) {
					messageTemplate = BUSINESS_JOURNAL_SPECIAL_SCHEDULE_OU_DELETE;
				}
			}
		}

		if (messageTemplate != null) {
			businessJournalService.log(authService.getCurrentUserName(), node, category, messageTemplate, objects);
		}
	}

	@Override
	public String getScheduleType(NodeRef node) {
		return (String) nodeService.getProperty(node, PROP_SCHEDULE_TYPE);
	}

	@Override
	public Boolean isWorkingDay(NodeRef node, Date day) {
		Boolean result = false;
		if (SCHEDULE_TYPE_SPECIAL.equals(getScheduleType(node))) {
			Date dayReset = DateUtils.truncate(day, Calendar.DATE);
			List<NodeRef> scheduleElements = getScheduleElements(node);
			if (scheduleElements != null && !scheduleElements.isEmpty()) {
				for (NodeRef scheduleElement : scheduleElements) {
					Date start = getScheduleElementStart(scheduleElement);
					Date end = getScheduleElementEnd(scheduleElement);
					if (!dayReset.before(start) && !dayReset.after(end)) {
						result = true;
						break;
					}
				}
			}
		} else {
			result = null;
		}

		return result;
	}

	@Override
	public List<NodeRef> getScheduleElements(NodeRef node) {
		List<NodeRef> scheduleElements = null;
		if (SCHEDULE_TYPE_SPECIAL.equals(getScheduleType(node))) {
			scheduleElements = new ArrayList<NodeRef>();
			List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs(node, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
			if (childAssociationRefs != null) {
				for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
					scheduleElements.add(childAssociationRef.getChildRef());
				}
			}
		}
		return scheduleElements;
	}

	@Override
	public Date getScheduleElementStart(NodeRef node) {
		return (Date) nodeService.getProperty(node, PROP_SCHEDULE_ELEMENT_BEGIN);
	}

	@Override
	public Date getScheduleElementEnd(NodeRef node) {
		return (Date) nodeService.getProperty(node, PROP_SCHEDULE_ELEMENT_END);
	}

	@Override
	public String getScheduleBeginTime(NodeRef node) {
		return (String) nodeService.getProperty(node, PROP_SCHEDULE_STD_BEGIN);
	}

	@Override
	public String getScheduleEndTime(NodeRef node) {
		return (String) nodeService.getProperty(node, PROP_SCHEDULE_STD_END);
	}


	// класс для представления элементов графика: первый и последний рабочий день в серии
	private class ScheduleElemetObject {

		private Date begin = null;
		private Date end = null;

		public Date getBegin() {
			return begin;
		}

		public void setBegin(Date begin) {
			this.begin = begin;
		}

		public Date getEnd() {
			return end;
		}

		public void setEnd(Date end) {
			this.end = end;
		}
	}
}
