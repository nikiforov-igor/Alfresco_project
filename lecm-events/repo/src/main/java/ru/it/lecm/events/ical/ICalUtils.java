package ru.it.lecm.events.ical;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Map;
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
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

/**
 *
 * @author vkuprin
 */
public class ICalUtils {

	public static final String ProdId = "GetITFromConfig";

	public String formEventRequest(CalendarEvent event) {
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
		
		Map<String, String> attendees = event.getAttendees();
		for (String personMail : attendees.keySet()) {
			String personName = attendees.get(personMail);
			personName = null == personName ? "" : personName;
			Attendee attendee = new Attendee(URI.create("mailto:" + personMail));
			//TODO проверять обязательность участника 
			attendee.getParameters().add(Role.REQ_PARTICIPANT);
			
			attendee.getParameters().add(CuType.INDIVIDUAL);
			if (personMail.equals(event.getInitiatorMail())) {
				attendee.getParameters().add(PartStat.ACCEPTED);
			} else {
				//TODO проверять статус участника 
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

	//Можно отправлять даже если удалили одного
	public String formEventCancel(CalendarEvent event) {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone(event.getStartTime().getTimeZone().getID());
		VTimeZone tz = timezone.getVTimeZone();
		VEvent vEvent = new VEvent();
		// add timezone info..
		PropertyList eventProperties = vEvent.getProperties();
		eventProperties.add(tz.getTimeZoneId());
//		 ATTENDEE        0+     MUST include all "Attendees" being removed
//                           the event. MUST include all "Attendees" if
//                           the entire event is cancelled.
		Map<String, String> attendees = event.getAttendees();
		for (String personMail : attendees.keySet()) {
			String personName = attendees.get(personMail);
			personName = null == personName ? "" : personName;
			Attendee attendee = new Attendee(URI.create("mailto:" + personMail));
			attendee.getParameters().add(CuType.INDIVIDUAL);
			attendee.getParameters().add(Rsvp.FALSE);
			attendee.getParameters().add(new Cn(personName));
			eventProperties.add(attendee);
		}
		eventProperties.add(new Organizer(URI.create("mailto:" + event.getInitiatorMail())));
//    SEQUENCE        1 must grow
		eventProperties.add(new Sequence(1));
//    UID             1       MUST be the UID of the original REQUEST
		eventProperties.add(new Uid(event.getUid()));
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

		eventProperties.add(new Location(event.getPlace()));
//    STATUS          0 or 1  MUST be set to CANCELLED. If uninviting
//                            specific "Attendees" then MUST NOT be
//                            included.
		//TODO проверять полное это удаление, или выкидываем участника
		eventProperties.add(Status.VEVENT_CANCELLED);
//    SUMMARY         0 or 1
//		eventProperties.add(new Summary(event.getSummary()));
//    TRANSP          0 or 1
//    URL             0 or 1
//    X-PROPERTY      0+
//    REQUEST-STATUS  0

		eventProperties.add(new Summary(event.getTitle()));
//
		eventProperties.add(new Description(event.getSummary()));
		eventProperties.add(new Location(event.getPlace()));
		Calendar iCal = new Calendar();
		iCal.getProperties().add(new ProdId(ProdId));
		iCal.getProperties().add(Version.VERSION_2_0);
		//TODO I hope we will use only gregorian but remember this place
		iCal.getProperties().add(CalScale.GREGORIAN);
		iCal.getProperties().add(Method.CANCEL);

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
