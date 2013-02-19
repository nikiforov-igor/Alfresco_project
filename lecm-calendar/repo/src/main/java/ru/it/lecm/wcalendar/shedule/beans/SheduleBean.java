package ru.it.lecm.wcalendar.shedule.beans;

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
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWCalendar;
import ru.it.lecm.wcalendar.beans.AbstractWCalendarBean;
import ru.it.lecm.wcalendar.shedule.IShedule;
import ru.it.lecm.wcalendar.shedule.ISpecialSheduleRaw;

/**
 *
 * @author vlevin
 */
public class SheduleBean extends AbstractWCalendarBean implements IShedule {
	// Получить логгер, чтобы писать, что с нами происходит.

	private final static Logger logger = LoggerFactory.getLogger(SheduleBean.class);

	@Override
	public IWCalendar getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_SHEDULE;
	}

	/**
	 * Метод, который запускает Spring при старте Tomcat-а. Создает корневой
	 * объект для графиков работы.
	 */
	public final void bootstrap() {
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
		params.put("CONTAINER_TYPE", TYPE_SHEDULE_CONTAINER);

		return params;
	}

	private NodeRef recursiveSheduleSearch(NodeRef node) {
		if (!isSheduleAssociated(node)) {
			List<ChildAssociationRef> parentAssocList = nodeService.getParentAssocs(node);
			if (parentAssocList == null || parentAssocList.isEmpty()) {
				return null;
			}
			ChildAssociationRef parentAssoc = parentAssocList.get(0);
			NodeRef parentNode = parentAssoc.getParentRef();
			return recursiveSheduleSearch(parentNode);
		} else {
			return getSheduleByOrgSubject(node);
		}
	}

	/**
	 * Если node - сотрудник, то возвращает ссылку на расписание подразделения,
	 * в котором сотрудник занимает основную позицию (или вышестоящего
	 * подразделения). Если node - подразделение, то возвращает ссылку на
	 * расписание вышестоящего подразделения. Если расписание к node не
	 * привязано, то возвращает null.
	 *
	 * @param node - NodeRef на сотрудника или орг. единицу.
	 * @return NodeRef на расписание.
	 */
	@Override
	public NodeRef getParentShedule(NodeRef node) {
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
			result = recursiveSheduleSearch(primaryOU);
		} else {
			List<ChildAssociationRef> parentAssocList = nodeService.getParentAssocs(primaryOU);
			if (parentAssocList == null || parentAssocList.isEmpty()) {
				return null;
			}
			ChildAssociationRef parentAssoc = parentAssocList.get(0);
			result = recursiveSheduleSearch(parentAssoc.getParentRef());
		}
		return result;
	}

	/**
	 * Возвращает время работы и тип родительского расписания (см.
	 * getParentShedule).
	 *
	 * @param node - NodeRef на сотрудника или орг. единицу.
	 * @return Ключи map'а: "type" - тип расписания, "begin" - время начала
	 * работы, "end" - время конца работы.
	 */
	@Override
	public Map<String, String> getParentSheduleStdTime(NodeRef node) {
		HashMap<String, String> result = new HashMap<String, String>();
		String sheduleStdBegin, sheduleStdEnd, sheduleType;
		NodeRef shedule = this.getParentShedule(node);
		if (shedule == null) {
			return null;
		}
		sheduleType = (String) nodeService.getProperty(shedule, PROP_SHEDULE_TYPE);
		if (sheduleType.equals("SPECIAL")) {
			sheduleStdBegin = "00:00";
			sheduleStdEnd = "00:00";

		} else {
			sheduleStdBegin = (String) nodeService.getProperty(shedule, PROP_SHEDULE_STD_BEGIN);
			sheduleStdEnd = (String) nodeService.getProperty(shedule, PROP_SHEDULE_STD_END);
		}
		result.put("type", sheduleType);
		result.put("begin", sheduleStdBegin);
		result.put("end", sheduleStdEnd);
		return result;
	}

	/**
	 * Проверяет, привязано ли какое-нибудь расписание к node.
	 *
	 * @param node - NodeRef на сотрудника или орг. единицу.
	 * @return true - привязано, false - не привязано.
	 */
	@Override
	public boolean isSheduleAssociated(NodeRef node) {
		NodeRef shedule = findNodeByAssociationRef(node, ASSOC_SHEDULE_EMPLOYEE_LINK, TYPE_SHEDULE, ASSOCIATION_TYPE.SOURCE);
		boolean result = shedule != null;
		return result;
	}

	/**
	 * Создает новое особое расписание.
	 *
	 * @param sheduleRawData - объект с правилами повторения расписания.
	 * @param sheduleEmployeeAssoc - NodeRef на сотрудника или орг. единицу, к
	 * которому надо привязать расписание.
	 * @param sheduleContainer - NodeRef на каталог, в котором будет создано
	 * расписание.
	 * @return - NodeRef на созданное расписание.
	 */
	@Override
	public NodeRef createNewSpecialShedule(final ISpecialSheduleRaw sheduleRawData, final NodeRef sheduleEmployeeAssoc, final NodeRef sheduleContainer) {
		NodeRef createdSheduleNode;
		// Транзакция. Все хорошие мальчики ковыряются в хранилище только в транзакции.
		createdSheduleNode = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
			@Override
			public NodeRef execute() throws Throwable {
				// Создание нового расписания
				ChildAssociationRef createdSheduleChildRef;
				String sheduleEmployeeAssocCMNameStr = nodeService.getProperty(sheduleEmployeeAssoc, ContentModel.PROP_NAME).toString();
				// имя для ноды вида: "commonNameСодрудникаИлиОрганизации_shedule"
				QName sheduleEmployeeAssocCMNameQName = QName.createQName(SHEDULE_NAMESPACE, sheduleEmployeeAssocCMNameStr + "_shedule");
				DateFormat timeFormat = new SimpleDateFormat("HH:mm");

				// нам не нужно дожидаться страшной ошибки при попытке создания уже существующего расписания
				// срыгнем эксепш пораньше
				if (isSheduleAssociated(sheduleEmployeeAssoc)) {
					throw new WebScriptException(sheduleEmployeeAssoc.toString() + " already has shedule!");
				}
				// UUID.randomUUID().toString()
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
				// пропертя для ноды, которую собираемся создать
				properties.put(PROP_SHEDULE_TYPE, "SPECIAL");
				properties.put(PROP_SHEDULE_STD_BEGIN, timeFormat.format(sheduleRawData.getTimeWorkBegins()));
				properties.put(PROP_SHEDULE_STD_END, timeFormat.format(sheduleRawData.getTimeWorkEnds()));
				properties.put(PROP_SHEDULE_TIME_LIMIT_START, sheduleRawData.getTimeLimitStart());
				properties.put(PROP_SHEDULE_TIME_LIMIT_END, sheduleRawData.getTimeLimitEnd());

				try {
					// создаем новую ноду в sheduleContainer
					createdSheduleChildRef = nodeService.createNode(sheduleContainer, ContentModel.ASSOC_CONTAINS,
							sheduleEmployeeAssocCMNameQName, TYPE_SHEDULE, properties);
				} catch (Exception ex) {
					throw new WebScriptException("Unable to create node", ex);
				}
				// нод-рефа на свежесозданную ноду
				NodeRef createdSheduleNode = createdSheduleChildRef.getChildRef();
				try {
					// привязываем ноду расписания к сотруднику или подразделеню
					nodeService.createAssociation(createdSheduleNode, sheduleEmployeeAssoc, ASSOC_SHEDULE_EMPLOYEE_LINK);
				} catch (Exception ex) {
					throw new WebScriptException("Unable to link newly created " + createdSheduleNode.toString() + " with " + sheduleEmployeeAssoc, ex);
				}

				// здесь генерируем список элементов графика...
				List<SheduleElemetObject> generatedSheduleElements = generateSheduleElements(sheduleRawData);
				// ...и скармливаем его функции, которая их создаст
				createSheduleElement(createdSheduleNode, generatedSheduleElements);

				return createdSheduleNode;
			}
		});
		return createdSheduleNode;
	}

	// жуткий трэш, который генерирует интервалы рабочих дней в зависимости от настроек расписания
	private List<SheduleElemetObject> generateSheduleElements(ISpecialSheduleRaw sheduleRawData) {
		List<SheduleElemetObject> sheduleElements = new ArrayList<SheduleElemetObject>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sheduleRawData.getTimeLimitStart());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);

		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(sheduleRawData.getTimeLimitEnd());
		calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
		calendarEnd.set(Calendar.MINUTE, 0);

		if (sheduleRawData.getReiterationType() == ISpecialSheduleRaw.ReiterationType.MONTH_DAYS
				|| sheduleRawData.getReiterationType() == ISpecialSheduleRaw.ReiterationType.WEEK_DAYS) {

			while (!calendar.after(calendarEnd)) {
				if (ifDayToBeAdded(sheduleRawData, calendar)) {
					if (!sheduleElements.isEmpty()) {
						SheduleElemetObject lastSheduleElement = sheduleElements.get(sheduleElements.size() - 1);
						Date lastSheduleElementEnd = lastSheduleElement.getEnd();
						if (lastSheduleElementEnd != null) {
							Calendar lastSheduleElementEndCal = Calendar.getInstance();
							lastSheduleElementEndCal.setTime(lastSheduleElementEnd);
							lastSheduleElementEndCal.add(Calendar.DAY_OF_YEAR, 1);
							SheduleElemetObject newLastSheduleElement = new SheduleElemetObject();
							if (lastSheduleElementEndCal.equals(calendar)) {
								newLastSheduleElement.setBegin(lastSheduleElement.getBegin());
								newLastSheduleElement.setEnd(calendar.getTime());
								sheduleElements.set(sheduleElements.size() - 1, newLastSheduleElement);
							} else {
								newLastSheduleElement.setBegin(calendar.getTime());
								sheduleElements.add(newLastSheduleElement);
							}
						} else {
							Date lastSheduleElementBegin = lastSheduleElement.getBegin();
							Calendar lastSheduleElementBeginCal = Calendar.getInstance();
							lastSheduleElementBeginCal.setTime(lastSheduleElementBegin);
							lastSheduleElementBeginCal.add(Calendar.DAY_OF_YEAR, 1);
							SheduleElemetObject newLastSheduleElement = new SheduleElemetObject();
							if (lastSheduleElementBeginCal.equals(calendar)) {
								newLastSheduleElement.setBegin(lastSheduleElement.getBegin());
								newLastSheduleElement.setEnd(calendar.getTime());
								sheduleElements.set(sheduleElements.size() - 1, newLastSheduleElement);
							} else {
								newLastSheduleElement.setBegin(lastSheduleElement.getBegin());
								newLastSheduleElement.setEnd(lastSheduleElement.getBegin());
								sheduleElements.set(sheduleElements.size() - 1, newLastSheduleElement);
								newLastSheduleElement = new SheduleElemetObject();
								newLastSheduleElement.setBegin(calendar.getTime());
								newLastSheduleElement.setEnd(null);
								sheduleElements.add(newLastSheduleElement);
							}
						}
					} else {
						SheduleElemetObject newLastSheduleElement = new SheduleElemetObject();
						newLastSheduleElement.setBegin(calendar.getTime());
						newLastSheduleElement.setEnd(null);
						sheduleElements.add(newLastSheduleElement);
					}
				}
				if (calendar.equals(calendarEnd)) {
					SheduleElemetObject lastSheduleElement = sheduleElements.get(sheduleElements.size() - 1);
					if (lastSheduleElement.getEnd() == null) {
						lastSheduleElement.setEnd(lastSheduleElement.getBegin());
						sheduleElements.set(sheduleElements.size() - 1, lastSheduleElement);
					}
				}

				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}
		} else if (sheduleRawData.getReiterationType() == ISpecialSheduleRaw.ReiterationType.SHIFT) {
			while (calendar.before(calendarEnd)) {
				SheduleElemetObject sheduleElement = new SheduleElemetObject();
				sheduleElement.setBegin(calendar.getTime());
				calendar.add(Calendar.DAY_OF_YEAR, sheduleRawData.getWorkingDaysAmount() - 1);
				sheduleElement.setEnd(calendar.getTime());
				calendar.add(Calendar.DAY_OF_YEAR, sheduleRawData.getWorkingDaysInterval() + 1);
				sheduleElements.add(sheduleElement);
			}
		}
		return sheduleElements;
	}

	// проверяет, подходит ли день под правила повторяемости для расписаний по дням месяца и недели
	// используется только в generateSheduleElements
	private boolean ifDayToBeAdded(ISpecialSheduleRaw sheduleRawData, Calendar calendar) {
		boolean result;
		if (sheduleRawData.getReiterationType() == ISpecialSheduleRaw.ReiterationType.MONTH_DAYS) {
			List<Integer> monthDays = sheduleRawData.getMonthDays();
			result = monthDays.contains(calendar.get(Calendar.DAY_OF_MONTH));
		} else if (sheduleRawData.getReiterationType() == ISpecialSheduleRaw.ReiterationType.WEEK_DAYS) {
			Map<Integer, Boolean> weekDays = sheduleRawData.getWeekDays();
			result = weekDays.get(calendar.get(Calendar.DAY_OF_WEEK));
		} else {
			throw new WebScriptException("Reiteration type can not be " + sheduleRawData.getReiterationType());
		}
		return result;
	}

	// непосредственное создание элементов графика в хранилище
	private void createSheduleElement(NodeRef createdSheduleNode, List<SheduleElemetObject> generatedSheduleElements) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for (SheduleElemetObject sheduleElement : generatedSheduleElements) {
			Date begin = sheduleElement.getBegin();
			Date end = sheduleElement.getEnd();
			// имя для элемента расписания вида "датаНачала_датаОкончания"
			QName sheduleElementQName = QName.createQName(SHEDULE_NAMESPACE, dateFormat.format(begin) + "_" + dateFormat.format(end));
			// пропертя для элемента расписания
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(PROP_SHEDULE_ELEMENT_BEGIN, begin);
			properties.put(PROP_SHEDULE_ELEMENT_END, end);
			try {
				// создаем новую ноду в createdSheduleNode
				nodeService.createNode(createdSheduleNode, ContentModel.ASSOC_CONTAINS, sheduleElementQName, TYPE_SHEDULE_ELEMENT, properties);
			} catch (Exception ex) {
				// не получилось
				throw new WebScriptException("Unable to create node", ex);
			}
		}
	}

	@Override
	public NodeRef getSheduleByOrgSubject(NodeRef node) {
		return findNodeByAssociationRef(node, ASSOC_SHEDULE_EMPLOYEE_LINK, TYPE_SHEDULE, ASSOCIATION_TYPE.SOURCE);
	}

	@Override
	public void unlinkShedule(NodeRef node) {
		NodeRef employee = findNodeByAssociationRef(node, ASSOC_SHEDULE_EMPLOYEE_LINK, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		NodeRef orgUnit = findNodeByAssociationRef(node, ASSOC_SHEDULE_EMPLOYEE_LINK, OrgstructureBean.TYPE_ORGANIZATION_UNIT, ASSOCIATION_TYPE.TARGET);
		if (employee != null) {
			nodeService.removeAssociation(node, employee, ASSOC_SHEDULE_EMPLOYEE_LINK);
		} else if (orgUnit != null) {
			nodeService.removeAssociation(node, orgUnit, ASSOC_SHEDULE_EMPLOYEE_LINK);
		}
	}

	// класс для представления элементов графика: первый и последний рабочий день в серии
	private class SheduleElemetObject {

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
