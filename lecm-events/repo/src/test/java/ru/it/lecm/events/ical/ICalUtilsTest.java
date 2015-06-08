package ru.it.lecm.events.ical;

import ru.it.lecm.events.ical.ICalUtils;
import ru.it.lecm.events.ical.CalendarReply;
import ru.it.lecm.events.ical.CalendarEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.TimeZone;
import net.fortuna.ical4j.model.property.Uid;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author vkuprin
 */
public class ICalUtilsTest {
	
	public ICalUtilsTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of formEventNotifications method, of class ICalUtils.
	 */
	@org.junit.Test
	public void testFormEventNotifications() throws Exception {
		System.out.println("formEventNotifications");
		
		CalendarEvent event = new CalendarEvent();
		event.setTitle("test");
		event.setSummary("Let's test it");
		//event.setFullDay(Boolean.TRUE);
		event.setFullDay(Boolean.FALSE);
		event.setInitiatorMail("test@test.ru");
		event.setInitiatorName("Валентин");
		event.setPlace("In the middle of nowhere");
		Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
		startTime.set(2016, 0, 1, 15, 30, 0);
		Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
		endTime.set(2016, 0, 2, 16, 00, 0);
		event.setStartTime(startTime);
		event.setEndTime(endTime);
		Uid uid = new Uid("20150330T045040Z-uidGen@fe80:0:0:0:4e72:b9ff:fe27:d19%2");
		event.setUid(uid.getValue());
		event.addAttendee("kuprinvp@gmail.com","Valentin Kuprin");
		event.addAttendee("test@test.ru","Валентин");

		ICalUtils instance = new ICalUtils();
		String expResult = "";
		System.out.println("Form Request");
		String result = instance.formEventRequest(event);
		System.out.println(result);
		
		System.out.println("Form Publish");
		System.out.println(instance.formEventPublish(event));
		
		assertEquals(expResult, "");
	}

	@org.junit.Test
	public void testReadReply() throws Exception {
		System.out.println("readReply");
		ICalUtils instance = new ICalUtils();
		InputStream is =  this.getClass().getResourceAsStream("answer.ics");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String currentLine = reader.readLine();
		while (currentLine!=null) {
			sb.append(currentLine).append("\n");
			currentLine = reader.readLine();
		}
		CalendarReply reply = instance.readReply(sb.toString());
		System.out.println(reply.toString());
		assertEquals("", "");
	}
	
}
