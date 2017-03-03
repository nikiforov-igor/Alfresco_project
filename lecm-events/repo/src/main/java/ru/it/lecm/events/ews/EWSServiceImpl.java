package ru.it.lecm.events.ews;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.events.beans.EWSEvent;
import ru.it.lecm.events.beans.EWSService;
import ru.it.lecm.events.beans.EmployeeAvailability;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vmalygin
 */
public class EWSServiceImpl implements EWSService {

	private final static Logger logger = LoggerFactory.getLogger(EWSServiceImpl.class);

	private String exchangeVersion;
	private String url;
	private String username;
	private String password;
	private String domain;
	private ExchangeService service;
	private OrgstructureBean orgstructureService;
	private NodeService nodeService;

	public void setExchangeVersion(String exchangeVersion) {
		this.exchangeVersion = exchangeVersion;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void init() throws URISyntaxException {
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
						employeeAvailability.getEvents().add(new EWSEvent(calendarEvent.getStartTime(), calendarEvent.getEndTime()));
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
