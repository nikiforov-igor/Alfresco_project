package ru.it.lecm.wcalendar.calendar.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.wcalendar.ICommonWCalendar;
import ru.it.lecm.wcalendar.beans.AbstractCommonWCalendarBean;
import ru.it.lecm.wcalendar.calendar.ICalendar;

/**
 *
 * @author vlevin
 */
public class CalendarBean extends AbstractCommonWCalendarBean implements ICalendar {

	private int yearsNumberToCreate = 0;
	private SimpleDateFormat yearParser = new SimpleDateFormat("yyyy");
	// Получить логгер, чтобы писать, что с нами происходит.
	private Logger logger = LoggerFactory.getLogger(CalendarBean.class);

	@Override
	public ICommonWCalendar getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_CALENDAR;
	}

	/**
	 * Получить количество лет, на которые нам надо сгенерировать календари.
	 *
	 * @param yearsNumberToCreate передается Spring-ом
	 */
	public final void setYearsNumberToCreate(int yearsNumberToCreate) {
		this.yearsNumberToCreate = yearsNumberToCreate;
	}

	/**
	 * Метод, который запускает Spring при старте Tomcat-а. Создает корневой
	 * объект для календарей и генерирует календари.
	 */
	public final void init() {
		PropertyCheck.mandatory(this, "repository", repository);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
		PropertyCheck.mandatory(this, "authService", authService);

		// Создание контейнера (если не существует).
		AuthenticationUtil.runAsSystem(this);

		// Обертка для эскалации прав.
		AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				// Транзакция.
				transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
					@Override
					public Object execute() throws Throwable {
						// Собственно генерация
						int yearsCreated = generateYearsList(yearsNumberToCreate);
						logger.info(String.format("Created %d calendars", yearsCreated));
						return "ok";
					}
				});
				return null;
			}
		};

		// Генерация календарей на yearsAmountToCreate вперед.
		if (yearsNumberToCreate > 0) {
			AuthenticationUtil.runAsSystem(raw);
		}
	}

	/**
	 * Генерация пустых календарей на amount лет вперед.
	 *
	 * @param amount Количество лет (начиная с текущего), на которые нужны
	 * календари.
	 * @return Количество созданных календарей. -1 если ошибка.
	 */
	private int generateYearsList(int amount) {
		String yearToAddStr, yearNodeName;
		DateFormat dateParser;
		int currentYear, yearsCreated = 0;


		NodeRef parentNodeRef = this.getWCalendarContainer();
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS; //the type of the association to create. This is used for verification against the data dictionary.
		QName nodeTypeQName = TYPE_CALENDAR; //a reference to the node type

		dateParser = new SimpleDateFormat("yyyy-mm-dd");

		currentYear = Calendar.getInstance().get(Calendar.YEAR);

		for (int i = 0; i < amount; i++) {
			int yearToAdd;
			Date yearToAddFormatted;

			yearToAdd = currentYear + i;
			if (!isCalendarExists(yearToAdd)) {
				yearToAddStr = String.valueOf(yearToAdd);
				yearNodeName = String.format("Calendar %d", yearToAdd);
				try {
					//костыль, чтобы избежать возможных проблем в другом часовом поясе
					yearToAddFormatted = dateParser.parse(yearToAddStr.concat("-01-15"));
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
					return -1;
				}
				QName assocQName = QName.createQName(WCAL_NAMESPACE, yearNodeName);
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>(); //optional map of properties to keyed by their qualified names
				properties.put(ContentModel.PROP_NAME, yearNodeName);
				properties.put(PROP_CALENDAR_YEAR, yearToAddFormatted);
				try {
					nodeService.createNode(parentNodeRef, assocTypeQName, assocQName, nodeTypeQName, properties);
				} catch (InvalidNodeRefException e) {
					logger.error("Panic! ".concat(e.getMessage()), e);
					return -1;
				} catch (InvalidTypeException e) {
					logger.error("Panic! ".concat(e.getMessage()), e);
					return -1;
				}
				yearsCreated++;
			}
		}
		return yearsCreated;
	}

	/**
	 * Проверка календаря на существование. Игнорирует lecm-dic:active. Если
	 * календарь выключен, он считается существующим. Поиск происходит в
	 * контейнере для календарей по умолчанию.
	 *
	 * @param yearToExamine год, существование календаря на который нужно
	 * проверить.
	 * @return true, если календарь существует. false в противном случае.
	 */
	@Override
	public boolean isCalendarExists(int yearToExamine) {
		return getCalendarByYear(yearToExamine) != null;
	}

	@Override
	protected Map<String, Object> containerParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CONTAINER_NAME", CONTAINER_NAME);
		params.put("CONTAINER_TYPE", TYPE_WCAL_CONTAINER);

		return params;
	}

	@Override
	public void addBusinessJournalRecord(NodeRef node, String category) {
		if (EventCategory.EDIT.equals(category)) {
			businessJournalService.log(authService.getCurrentUserName(), node, category, BUSINESS_JOURNAL_CALENDAR_MODIFIED, null);
		}
	}

	@Override
	public Boolean isWorkingDay(Date day) {
		boolean result;
		List<NodeRef> daysList;

		Date dayNoTime = resetTime(day);
		int year = Integer.valueOf(yearParser.format(day));

		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			result = false;
			daysList = getAllWorkingDaysByYear(year);
		} else {
			result = true;
			daysList = getAllNonWorkingDaysByYear(year);
		}

		if (daysList != null) {
			for (NodeRef specialDay : daysList) {
				Date specialDayDate = resetTime(getSpecialDayDate(specialDay));
				if (dayNoTime.equals(specialDayDate)) {
					result = !result;
					break;
				}
			}
		} else {
			return null;
		}
		return result;
	}

	@Override
	public NodeRef getCalendarByYear(int year) {
		NodeRef result = null;
		int yearProp;

		List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs(this.getWCalendarContainer(), ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if (childAssociationRefs != null) {
			for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
				NodeRef calendarNodeRef = childAssociationRef.getChildRef();
				yearProp = getCalendarYear(calendarNodeRef);
				if (year == yearProp) {
					result = calendarNodeRef;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public int getCalendarYear(NodeRef node) {
		Date yearDate = (Date) nodeService.getProperty(node, PROP_CALENDAR_YEAR);
		return Integer.valueOf(yearParser.format(yearDate));
	}

	@Override
	public List<NodeRef> getDaysInCalendarByType(NodeRef calendar, QName dayType) {
		List<NodeRef> daysList = null;
		if (calendar != null) {
			daysList = new ArrayList<NodeRef>();
			Set<QName> childNodesType = new HashSet<QName>();
			childNodesType.add(dayType);
			List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs(calendar, childNodesType);
			if (childAssociationRefs != null) {
				for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
					NodeRef specialDayNodeRef = childAssociationRef.getChildRef();
					daysList.add(specialDayNodeRef);
				}
			}
		}
		return daysList;
	}

	@Override
	public List<NodeRef> getAllWorkingDaysByYear(int year) {
		NodeRef calendar = getCalendarByYear(year);
		if (calendar == null) {
			return null;
		} else {
			return getDaysInCalendarByType(calendar, TYPE_WORKING_DAYS);
		}
	}

	@Override
	public List<NodeRef> getAllNonWorkingDaysByYear(int year) {
		NodeRef calendar = getCalendarByYear(year);
		if (calendar == null) {
			return null;
		} else {
			return getDaysInCalendarByType(calendar, TYPE_NON_WORKING_DAYS);
		}
	}

	@Override
	public Date getSpecialDayDate(NodeRef node) {
		return (Date) nodeService.getProperty(node, PROP_SPECIAL_DAY_DAY);
	}
}
