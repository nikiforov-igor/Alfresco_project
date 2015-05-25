package ru.it.lecm.events.ical;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.SocketException;
import java.net.URI;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.apache.tools.ant.filters.StringInputStream;

/**
 *
 * @author vkuprin
 */
public class ICalUtils {

	public static final String ProdId = "GetITFromConfig";

	public String formEventRequest(CalendarEvent event) throws SocketException {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone(event.getStartTime().getTimeZone().getID());
		VTimeZone tz = timezone.getVTimeZone();

		VEvent vEvent = new VEvent();
		PropertyList eventProperties = vEvent.getProperties();
		if (event.isFullDay()) {
			Date dtStart = new Date(event.getStartTime().getTime());
			eventProperties.add(new DtStart(dtStart));
			Date dtEnd = new Date(event.getEndTime().getTime());
			eventProperties.add(new DtEnd(dtEnd));
		} else {
			DateTime dtStart = new DateTime(event.getStartTime().getTime());
			eventProperties.add(new DtStart(dtStart));
			DateTime dtEnd = new DateTime(event.getEndTime().getTime());
			eventProperties.add(new DtEnd(dtEnd));
		}

		eventProperties.add(new Summary(event.getTitle()));
		// add timezone info..
		eventProperties.add(tz.getTimeZoneId());

		eventProperties.add(new Uid(event.getUid()));
		eventProperties.add(new Organizer(URI.create("mailto:" + event.getInitiatorMail())));
		eventProperties.add(new Description(event.getSummary()));
		eventProperties.add(new Location(event.getPlace()));
		eventProperties.add(Status.VEVENT_CONFIRMED);
		for (String personName : event.getAttendees().keySet()) {
			Attendee attendee = new Attendee(URI.create("mailto:" + event.getAttendees().get(personName)));
			attendee.getParameters().add(Role.REQ_PARTICIPANT);
			attendee.getParameters().add(CuType.INDIVIDUAL);
			if (event.getAttendees().get(personName).equals(event.getInitiatorMail())) {
				attendee.getParameters().add(PartStat.ACCEPTED);
			} else {
				attendee.getParameters().add(PartStat.NEEDS_ACTION);
			}
			attendee.getParameters().add(Rsvp.TRUE);
			attendee.getParameters().add(new Cn(personName));
			eventProperties.add(attendee);
		}

		Calendar iCal = new Calendar();
		iCal.getProperties().add(new ProdId(ProdId));
		iCal.getProperties().add(Version.VERSION_2_0);
		//TODO I hope we will use only gregorian but remember this place
		iCal.getProperties().add(CalScale.GREGORIAN);
		iCal.getProperties().add(Method.REQUEST);

		iCal.getComponents().add(vEvent);

		return iCal.toString();
	}

	public String formEventPublish(CalendarEvent event) {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone(event.getStartTime().getTimeZone().getID());
		VTimeZone tz = timezone.getVTimeZone();

		VEvent vEvent = new VEvent();
		PropertyList eventProperties = vEvent.getProperties();
		if (event.isFullDay()) {
			Date dtStart = new Date(event.getStartTime().getTime());
			eventProperties.add(new DtStart(dtStart));
			Date dtEnd = new Date(event.getEndTime().getTime());
			eventProperties.add(new DtEnd(dtEnd));
		} else {
			DateTime dtStart = new DateTime(event.getStartTime().getTime());
			eventProperties.add(new DtStart(dtStart));
			DateTime dtEnd = new DateTime(event.getEndTime().getTime());
			eventProperties.add(new DtEnd(dtEnd));
		}

		eventProperties.add(new Summary(event.getTitle()));
		// add timezone info..
		eventProperties.add(tz.getTimeZoneId());

		eventProperties.add(new Uid(event.getUid()));
		eventProperties.add(new Organizer(URI.create("mailto:" + event.getInitiatorMail())));
		eventProperties.add(new Description(event.getSummary()));
		eventProperties.add(new Location(event.getPlace()));
		eventProperties.add(Status.VEVENT_CONFIRMED);
		Calendar iCal = new Calendar();
		iCal.getProperties().add(new ProdId(ProdId));
		iCal.getProperties().add(Version.VERSION_2_0);
		//TODO I hope we will use only gregorian but remember this place
		iCal.getProperties().add(CalScale.GREGORIAN);
		iCal.getProperties().add(Method.PUBLISH);

		iCal.getComponents().add(vEvent);
		return iCal.toString();
	}
	
	//TODO Будет ли возможность добавлять события по почте?
	public CalendarReply readReply(String iCal) throws IOException, ParserException {

		CalendarBuilder cb = new CalendarBuilder();
		Calendar calendar = cb.build(new StringReader(iCal));
		if (calendar.getProperty(Method.METHOD).equals(Method.REPLY)) {
			CalendarReply reply = new CalendarReply();
			ComponentList events = calendar.getComponents(VEvent.VEVENT);
			for (Object element : events) {
				if (element instanceof VEvent) {
					VEvent event = (VEvent) element;
					reply.setUid(event.getUid().getValue());
					PropertyList attendies = event.getProperties(Attendee.ATTENDEE);
					if (attendies.size() == 1) {
						Attendee attendee = ((Attendee) attendies.get(0));
						ParameterList aParameters = attendee.getParameters();
						reply.setAnswer(aParameters.getParameter(PartStat.PARTSTAT).getValue());
						reply.setAttendeeMail(attendee.getCalAddress().toString());
					} else {
						throw new RuntimeException("Too many Attendees. Not implemented yet/");
					}
					//TODO Can read only one event yet.
					break;
				}
			}
			return reply;
		} else {
			return null;
		}

	}

}
