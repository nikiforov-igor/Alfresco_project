package ru.it.lecm.events.ews.deprecated;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.AvailabilityData;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.ServiceError;
import microsoft.exchange.webservices.data.AttendeeAvailability;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.WebCredentials;
import microsoft.exchange.webservices.data.AttendeeInfo;
import microsoft.exchange.webservices.data.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.TimeWindow;
import microsoft.exchange.webservices.data.CalendarEvent;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.events.beans.EWSEvent;
import ru.it.lecm.events.beans.EmployeeAvailability;
import ru.it.lecm.events.ews.AbstractEWSService;

/**
 *
 * @author vmalygin
 *
 * Основан на использовании exchange-ws-api:1.1.5.2
 * Предназначен для использования на alfresco-4.2.e
 * При окончательно переходе на alfresco5 должен быть удален
 */
@Deprecated
public class EWSServiceImpl extends AbstractEWSService {

	private final static Logger logger = LoggerFactory.getLogger(EWSServiceImpl.class);

	private ExchangeService service;
	private final DateFormat formatter;
	private final DateFormat serviceTimezoneFormatter;

	public EWSServiceImpl() {
		formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		serviceTimezoneFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		serviceTimezoneFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public void init() throws URISyntaxException {
		logger.warn("ATTENTION: you are going to use deprecated, old, obsolete, monstrous EWSService based on exchange-ws-api:1.1.5.2");
		ExchangeVersion requestedServerVersion = ExchangeVersion.valueOf(exchangeVersion);
		ExchangeCredentials credentials = new WebCredentials(username, password, domain);
		service = new ExchangeService(requestedServerVersion);
		service.setUrl(new URI(url));
		service.setCredentials(credentials);
	}

	private List<EmployeeAvailability> getEvents(List<EmployeeAvailability> events, List<AttendeeInfo> attendees, Date fromDate, Date toDate) {
		try {
			int i = 0;
			GetUserAvailabilityResults results = service.getUserAvailability(attendees, new TimeWindow(fromDate, toDate), AvailabilityData.FreeBusyAndSuggestions);
			for (AttendeeAvailability availability : results.getAttendeesAvailability()) {
				EmployeeAvailability employeeAvailability = events.get(i++);
				if (availability.getErrorCode() == ServiceError.NoError) {
					Collection<CalendarEvent> calendarEvents = availability.getCalendarEvents();
					for (CalendarEvent calendarEvent : calendarEvents) {
						String startTime = formatter.format(calendarEvent.getStartTime());
						String endTime = formatter.format(calendarEvent.getEndTime());
						Date start = serviceTimezoneFormatter.parse(startTime);
						Date end = serviceTimezoneFormatter.parse(endTime);
						employeeAvailability.getEvents().add(new EWSEvent(start, end));
					}
				} else {
					logger.error("EWS Error code: {}", availability.getErrorCode().toString());
					logger.error(availability.getErrorMessage());
					if (logger.isDebugEnabled()) {
						Map<String, String> errorDetails = availability.getErrorDetails();
						for (Entry<String, String> entry : errorDetails.entrySet()) {
							logger.debug("{} {}", entry.getKey(), entry.getValue());
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return events;
	}

	@Override
	public List<EmployeeAvailability> getEvents(NodeRef employeeRef, Date fromDate, Date toDate) {
		NodeRef personRef = orgstructureService.getPersonForEmployee(employeeRef);
		String email = (String)nodeService.getProperty(personRef, ContentModel.PROP_EMAIL);
		List<AttendeeInfo> attendees = new ArrayList<>(1);
		List<EmployeeAvailability> availabilities = new ArrayList<>(1);
		attendees.add(new AttendeeInfo(email));
		EmployeeAvailability employeeAvailability = new EmployeeAvailability(employeeRef, email);
		employeeAvailability.setEvents(new ArrayList<EWSEvent>());
		availabilities.add(employeeAvailability);
		return getEvents(availabilities, attendees, fromDate, toDate);
	}

	@Override
	public List<EmployeeAvailability> getEvents(List<NodeRef> employeeRefList, Date fromDate, Date toDate) {
		List<AttendeeInfo> attendees = new ArrayList<>(employeeRefList.size());
		List<EmployeeAvailability> availabilities = new ArrayList<>(employeeRefList.size());
		for(NodeRef employeeRef : employeeRefList) {
			NodeRef personRef = orgstructureService.getPersonForEmployee(employeeRef);
			String email = (String)nodeService.getProperty(personRef, ContentModel.PROP_EMAIL);
			attendees.add(new AttendeeInfo(email));
			EmployeeAvailability employeeAvailability = new EmployeeAvailability(employeeRef, email);
			employeeAvailability.setEvents(new ArrayList<EWSEvent>());
			availabilities.add(employeeAvailability);
		}
		return getEvents(availabilities, attendees, fromDate, toDate);
	}
}
