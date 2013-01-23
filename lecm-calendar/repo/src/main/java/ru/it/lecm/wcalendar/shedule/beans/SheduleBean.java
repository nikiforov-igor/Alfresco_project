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
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWCalCommon;
import ru.it.lecm.wcalendar.beans.AbstractWCalCommonBean;

/**
 *
 * @author vlevin
 */
public class SheduleBean extends AbstractWCalCommonBean {

	public final static String CONTAINER_NAME = "SheduleContainer";
	public final static QName TYPE_SHEDULE = QName.createQName(SHEDULE_NAMESPACE, "shedule");
	public final static QName TYPE_SHEDULE_ELEMENT = QName.createQName(SHEDULE_NAMESPACE, "special-shed-element");
	public final static QName TYPE_SHEDULE_CONTAINER = QName.createQName(WCAL_NAMESPACE, "shedule-container");
	public final static QName ASSOC_SHEDULE_EMPLOYEE_LINK = QName.createQName(SHEDULE_NAMESPACE, "shed-employee-link-assoc");
	public final static QName PROP_SHEDULE_STD_BEGIN = QName.createQName(SHEDULE_NAMESPACE, "std-begin");
	public final static QName PROP_SHEDULE_STD_END = QName.createQName(SHEDULE_NAMESPACE, "std-end");
	public final static QName PROP_SHEDULE_TYPE = QName.createQName(SHEDULE_NAMESPACE, "type");
	public final static QName PROP_SHEDULE_TIME_LIMIT_START = QName.createQName(SHEDULE_NAMESPACE, "time-limit-start");
	public final static QName PROP_SHEDULE_TIME_LIMIT_END = QName.createQName(SHEDULE_NAMESPACE, "time-limit-end");
	public final static QName PROP_SHEDULE_ELEMENT_BEGIN = QName.createQName(SHEDULE_NAMESPACE, "begin");
	public final static QName PROP_SHEDULE_ELEMENT_END = QName.createQName(SHEDULE_NAMESPACE, "end");
	public final static QName PROP_SHEDULE_ELEMENT_COMMENT = QName.createQName(SHEDULE_NAMESPACE, "comment");
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(SheduleBean.class);

	@Override
	public IWCalCommon getWCalendarDescriptor() {
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
//		PropertyCheck.mandatory (this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);

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
		List<AssociationRef> sheduleAssocList = nodeService.getSourceAssocs(node, ASSOC_SHEDULE_EMPLOYEE_LINK);
		if (sheduleAssocList == null || sheduleAssocList.isEmpty()) {
			List<ChildAssociationRef> parentAssocList = nodeService.getParentAssocs(node);
			if (parentAssocList == null || parentAssocList.isEmpty()) {
				return null;
			}
			ChildAssociationRef parentAssoc = parentAssocList.get(0);
			NodeRef parentNode = parentAssoc.getParentRef();
			logger.debug(node.toString() + " has parent " + parentNode.toString());
			return recursiveSheduleSearch(parentNode);
		} else {
			AssociationRef sheduleAssoc = sheduleAssocList.get(0);
			NodeRef sheduleNode = sheduleAssoc.getSourceRef();
			return sheduleNode;
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
	public NodeRef getParentShedule(NodeRef node) {
		NodeRef primaryOU = null;
		QName nodeType = nodeService.getType(node);
		boolean searchFromCurrent = true;
		if (OrgstructureBean.TYPE_EMPLOYEE.isMatch(nodeType)) {
			List<AssociationRef> assocEmloyeeLinkList = nodeService.getSourceAssocs(node, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			if (assocEmloyeeLinkList == null || assocEmloyeeLinkList.isEmpty()) {
				return null;
			}
			for (AssociationRef assocEmloyeeLink : assocEmloyeeLinkList) {
				NodeRef nodeEmployeeLink = assocEmloyeeLink.getSourceRef();
				Serializable isPrimaryLink = nodeService.getProperty(nodeEmployeeLink, OrgstructureBean.PROP_EMP_LINK_IS_PRIMARY);
				if (!(Boolean) isPrimaryLink) {
					continue;
				}
				List<AssociationRef> assocOrgElementMemberList = nodeService.getSourceAssocs(nodeEmployeeLink, OrgstructureBean.ASSOC_ELEMENT_MEMBER_EMPLOYEE);
				if (assocOrgElementMemberList == null || assocOrgElementMemberList.isEmpty()) {
					return null;
				}
				for (AssociationRef assocOrgElementMember : assocOrgElementMemberList) {
					NodeRef nodeOrgElementMember = assocOrgElementMember.getSourceRef();
					List<ChildAssociationRef> assocOrgUnitList = nodeService.getParentAssocs(nodeOrgElementMember);
					if (assocOrgUnitList == null || assocOrgUnitList.isEmpty()) {
						return null;
					}
					NodeRef nodeOrgUnit = (assocOrgUnitList.get(0)).getParentRef();
					primaryOU = nodeOrgUnit;
					searchFromCurrent = true;
				}
			}
		} else if (OrgstructureBean.TYPE_ORGANIZATION_UNIT.isMatch(nodeType)) {
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
	public boolean isSheduleAssociated(NodeRef node) {
		List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(node, ASSOC_SHEDULE_EMPLOYEE_LINK);
		if (sourceAssocs == null || sourceAssocs.isEmpty()) {
			return false;
		} else {
			return true;
		}
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
	public NodeRef createNewSpecialShedule(final SpecialSheduleRawBean sheduleRawData, final NodeRef sheduleEmployeeAssoc, final NodeRef sheduleContainer) {
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
	private List<SheduleElemetObject> generateSheduleElements(SpecialSheduleRawBean sheduleRawData) {
		List<SheduleElemetObject> sheduleElements = new ArrayList<SheduleElemetObject>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sheduleRawData.getTimeLimitStart());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);

		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(sheduleRawData.getTimeLimitEnd());
		calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
		calendarEnd.set(Calendar.MINUTE, 0);

		if (sheduleRawData.getReiterationType() == SpecialSheduleRawBean.ReiterationType.MONTH_DAYS
				|| sheduleRawData.getReiterationType() == SpecialSheduleRawBean.ReiterationType.WEEK_DAYS) {

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
		} else if (sheduleRawData.getReiterationType() == SpecialSheduleRawBean.ReiterationType.SHIFT) {
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
	private boolean ifDayToBeAdded(SpecialSheduleRawBean sheduleRawData, Calendar calendar) {
		boolean result;
		if (sheduleRawData.getReiterationType() == SpecialSheduleRawBean.ReiterationType.MONTH_DAYS) {
			List<Integer> monthDays = sheduleRawData.getMonthDays();
			result = monthDays.contains(calendar.get(Calendar.DAY_OF_MONTH));
		} else if (sheduleRawData.getReiterationType() == SpecialSheduleRawBean.ReiterationType.WEEK_DAYS) {
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
