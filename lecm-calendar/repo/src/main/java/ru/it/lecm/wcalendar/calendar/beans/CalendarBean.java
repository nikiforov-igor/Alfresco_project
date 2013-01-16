package ru.it.lecm.wcalendar.calendar.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ru.it.lecm.wcalendar.IWCalCommon;
import ru.it.lecm.wcalendar.beans.AbstractWCalCommonBean;

/**
 *
 * @author vlevin
 */
public class CalendarBean extends AbstractWCalCommonBean {

	private final static String CONTAINER_NAME = "WCalContainer";
	private final static QName TYPE_WCAL_CONTAINER = QName.createQName(WCAL_NAMESPACE, "wcal-container");
	private final static QName TYPE_CALENDAR = QName.createQName(CALENDAR_NAMESPACE, "calendar");
	private final static QName ASSOC_CALENDAR_CONTAINER = QName.createQName(WCAL_NAMESPACE, "container-calendar-assoc");
	private final static QName PROP_YEAR = QName.createQName(CALENDAR_NAMESPACE, "year");
	private int yearsAmountToCreate = 0;
	// Получить логгер, чтобы писать, что с нами происходит.
	private Logger logger = LoggerFactory.getLogger(CalendarBean.class);

	@Override
	public IWCalCommon getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_CALENDAR;
	}

	/**
	 * Получить количество лет, на которые нам надо сгенерировать календари.
	 *
	 * @param yearsAmountToCreate передается Spring-ом
	 */
	public final void setYearsAmountToCreate(int yearsAmountToCreate) {
		this.yearsAmountToCreate = yearsAmountToCreate;
	}

	/**
	 * Метод, который запускает Spring при старте Tomcat-а. Создает корневой
	 * объект для календарей и генерирует календари.
	 */
	public final void bootstrap() {
		PropertyCheck.mandatory(this, "repository", repository);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
//		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);

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
						int yearsCreated = generateYearsList(yearsAmountToCreate);
						logger.info(String.format("Created %d calendars", yearsCreated));
						return "ok";
					}
				});
				return null;
			}
		};

		// Генерация календарей на yearsAmountToCreate вперед.
		if (yearsAmountToCreate > 0) {
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
			if (!isCalendarExists(parentNodeRef, yearToAdd)) {
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
				properties.put(PROP_YEAR, yearToAddFormatted);
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
	 * календарь выключен, он считается существующим.
	 *
	 * @param parentNodeRef nodeRef контейнера, в котором лежат календари.
	 * @param yearToExamine год, существование календаря на который нужно
	 * проверить.
	 * @return true, если календарь существует. false в противном случае.
	 */
	private boolean isCalendarExists(NodeRef parentNodeRef, int yearToExamine) {
		boolean exists = false;
		int yearProp;
		SimpleDateFormat dateParser = new SimpleDateFormat("yyyy");
		List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs(parentNodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if (childAssociationRefs != null) {
			for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
				NodeRef calendarNodeRef = childAssociationRef.getChildRef();
				Serializable year = nodeService.getProperty(calendarNodeRef, PROP_YEAR);
				yearProp = Integer.valueOf(dateParser.format(year));
				// Игнорирует lecm-dic:active. Если календарь существует, но выключен, он его добавлять не будет.
				if (yearToExamine == yearProp) {
					exists = true;
					break;
				}
			}
		}
		return exists;
	}

	@Override
	protected Map<String, Object> containerParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CONTAINER_NAME", CONTAINER_NAME);
		params.put("CONTAINER_TYPE", TYPE_WCAL_CONTAINER);

		return params;
	}
}
