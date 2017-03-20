package ru.it.lecm.events.ews;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.availability.AvailabilityData;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.misc.error.ServiceError;
import microsoft.exchange.webservices.data.core.response.AttendeeAvailability;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.availability.AttendeeInfo;
import microsoft.exchange.webservices.data.misc.availability.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.misc.availability.TimeWindow;
import microsoft.exchange.webservices.data.property.complex.availability.CalendarEvent;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.events.beans.EWSEvent;
import ru.it.lecm.events.beans.EmployeeAvailability;

/**
 *
 * @author vmalygin
 *
 * Основан на использовании ews-java-api:2.0
 * Предназначен для использования на alfresco-5.x
 */
public class EWSServiceImpl extends AbstractEWSService {

	private final static Logger logger = LoggerFactory.getLogger(EWSServiceImpl.class);

	private ExchangeService service;

	@Override
	public void init() throws URISyntaxException {
		logger.warn("ATTENTION: you are going to use EWSService based on ews-java-api:2.0");
		ExchangeVersion requestedServerVersion = ExchangeVersion.valueOf(exchangeVersion);
		ExchangeCredentials credentials = new WebCredentials(username, password, domain);
		service = new ExchangeService(requestedServerVersion);
		service.setUrl(new URI(url));
		service.setCredentials(credentials);
	}

	private List<EmployeeAvailability> getEvents(List<EmployeeAvailability> events, List<AttendeeInfo> attendees, Date fromDate, Date toDate) {
		try {
			int i = 0;
			Interval requiredInterval = new Interval(fromDate.getTime() - TimeZone.getDefault().getRawOffset(), toDate.getTime() - TimeZone.getDefault().getRawOffset());
			GetUserAvailabilityResults results = service.getUserAvailability(attendees, new TimeWindow(fromDate, toDate), AvailabilityData.FreeBusyAndSuggestions);
			for (AttendeeAvailability availability : results.getAttendeesAvailability()) {
				EmployeeAvailability employeeAvailability = events.get(i++);
				if (availability.getErrorCode() == ServiceError.NoError) {
					Collection<CalendarEvent> calendarEvents = availability.getCalendarEvents();
					for (CalendarEvent calendarEvent : calendarEvents) {
						if (requiredInterval.contains(calendarEvent.getStartTime().getTime())) {
							employeeAvailability.getEvents().add(new EWSEvent(calendarEvent.getStartTime(), calendarEvent.getEndTime()));
						}
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
